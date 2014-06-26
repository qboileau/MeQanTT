/*******************************************************************************
 * Copyright 2011 Albin Theander
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.meqantt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.meqantt.message.*;


public class SocketClient extends AbstractMqttClient {

	private MessageInputStream in;
	private Socket socket;
	private MessageOutputStream out;
	private MqttReader reader;
	private Semaphore connectionAckLock;

	public SocketClient(String id) {
		this.id = id;
        listeners.add(new SocketListener());
	}

	public void connect(String host, int port) throws MqttException {
        try {

            handler = new DefaultMessageHandler();
            handler.setListeners(listeners);

            socket = new Socket(host, port);
            InputStream is = socket.getInputStream();
            in = new MessageInputStream(is);
            OutputStream os = socket.getOutputStream();
            out = new MessageOutputStream(os);
            reader = new MqttReader();
            reader.start();
            ConnectMessage msg = new ConnectMessage(id, false, 60);
            connectionAckLock = new Semaphore(0);
            out.writeMessage(msg);
            connectionAckLock.acquire();
        } catch (InterruptedException e) {
            throw new MqttException(e.getMessage(), e);
        } catch (UnknownHostException e) {
            throw new MqttException(e.getMessage(), e);
        } catch (IOException e) {
            throw new MqttException(e.getMessage(), e);
        }
    }

    public void disconnect() throws MqttException {
        try {
            DisconnectMessage msg = new DisconnectMessage();
            out.writeMessage(msg);
            socket.close();
        } catch (IOException e) {
            throw new MqttException(e.getMessage(), e);
        }
    }

	public void subscribe(String topic) throws MqttException {
        try {
            SubscribeMessage msg = new SubscribeMessage(topic, QoS.AT_MOST_ONCE);
            out.writeMessage(msg);
        } catch (IOException e) {
            throw new MqttException(e.getMessage(), e);
        }
	}

    public void unsubscribe(String topic) throws MqttException {
        try {
            UnsubscribeMessage msg = new UnsubscribeMessage(topic);
            out.writeMessage(msg);
        } catch (IOException e) {
            throw new MqttException(e.getMessage(), e);
        }
    }

    public void publish(String topic, String message) throws MqttException {
        try {
            PublishMessage msg = new PublishMessage(topic, message);
            out.writeMessage(msg);
        } catch (IOException e) {
            throw new MqttException(e.getMessage(), e);
        }
    }

    public void ping() throws MqttException {
        try {
            PingReqMessage msg = new PingReqMessage();
            out.writeMessage(msg);
        } catch (IOException e) {
            throw new MqttException(e.getMessage(), e);
        }
    }

    public void addListener(MqttListener listener) {
        listeners.add(listener);
        if (handler != null) {
            handler.addListener(listener);
        }
    }

    public void setListeners(List<MqttListener> listeners) {
        this.listeners = listeners;
        if (handler != null) {
            handler.setListeners(listeners);
        }
    }

    private class SocketListener implements MqttListener {

        public void connectAck(ConnAckMessage.ConnectionStatus status) {
            connectionAckLock.release();
        }

        public void disconnected() {

        }

        public void publishArrived(String topic, byte[] data) {
            System.out.println("PUBLISH (" + topic + "): "
                    + new String(data));
        }
    }

    private class MqttReader extends Thread {

		@Override
		public void run() {
			Message msg;
			try {
				while (true) {
					msg = in.readMessage();
                    handler.handleMessage(msg);
				}
			} catch (IOException e) {
			}
		}
	}

}
