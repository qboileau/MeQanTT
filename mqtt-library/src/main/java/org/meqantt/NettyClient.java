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

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.meqantt.message.ConnectMessage;
import org.meqantt.message.DisconnectMessage;
import org.meqantt.message.PingReqMessage;
import org.meqantt.message.PublishMessage;
import org.meqantt.message.QoS;
import org.meqantt.message.SubscribeMessage;
import org.meqantt.message.UnsubscribeMessage;
import org.meqantt.netty.MqttMessageDecoder;
import org.meqantt.netty.MqttMessageEncoder;
import org.meqantt.netty.NettyMessageHandler;


public class NettyClient extends AbstractMqttClient {

	private Channel channel;
	private ClientBootstrap bootstrap;

	public NettyClient(String id) {
		this.id = id;
	}

    public boolean isConnected() {
        return (channel != null) && channel.isConnected();
    }

    /* (non-Javadoc)
             * @see com.albin.mqtt.MqttClient#connect(java.lang.String, int)
             */
	public void connect(String host, int port) throws MqttException {
		bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()));

		handler = new NettyMessageHandler();
		handler.setListeners(listeners);
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {

			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(new MqttMessageEncoder(),
						new MqttMessageDecoder(), (NettyMessageHandler)handler);
			}
		});

		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("keepAlive", true);

		ChannelFuture future = bootstrap.connect(new InetSocketAddress(host,
				port));

		channel = future.awaitUninterruptibly().getChannel();
		if (!future.isSuccess()) {
			future.getCause().printStackTrace();
			bootstrap.releaseExternalResources();
			return;
		}

		channel.write(new ConnectMessage(id, true, 30));
		// TODO: Should probably wait for the ConnAck message
	}

	/* (non-Javadoc)
	 * @see com.albin.mqtt.MqttClient#disconnect()
	 */
	public void disconnect() throws MqttException {
		channel.write(new DisconnectMessage()).awaitUninterruptibly();
		channel.close().awaitUninterruptibly();
		bootstrap.releaseExternalResources();
	}

	/* (non-Javadoc)
	 * @see com.albin.mqtt.MqttClient#subscribe(java.lang.String)
	 */
	public void subscribe(String topic) throws MqttException {
		channel.write(new SubscribeMessage(topic, QoS.AT_MOST_ONCE));
	}

	/* (non-Javadoc)
	 * @see com.albin.mqtt.MqttClient#unsubscribe(java.lang.String)
	 */
	public void unsubscribe(String topic) throws MqttException {
		channel.write(new UnsubscribeMessage(topic));
	}

	/* (non-Javadoc)
	 * @see com.albin.mqtt.MqttClient#publish(java.lang.String, java.lang.String)
	 */
	public void publish(String topic, String msg) throws MqttException {
		channel.write(new PublishMessage(topic, msg));
	}

	/* (non-Javadoc)
	 * @see com.albin.mqtt.MqttClient#ping()
	 */
	public void ping() throws MqttException {
		channel.write(new PingReqMessage());
	}

}
