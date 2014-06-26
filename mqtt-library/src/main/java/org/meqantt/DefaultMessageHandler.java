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

import org.meqantt.message.*;

import java.util.ArrayList;
import java.util.List;

public class DefaultMessageHandler implements MqttMessageHandler {

    private List<MqttListener> listeners = new ArrayList<MqttListener>();

    public DefaultMessageHandler() {

    }

    public void handleMessage(Message msg) {
        if (msg == null) {
            return;
        }
        switch (msg.getType()) {
            case CONNACK:
                handleMessage((ConnAckMessage) msg);
                break;
            case PUBLISH:
                handleMessage((PublishMessage) msg);
                break;
            case PUBACK:
                //TODO
                break;
            case PUBREC:
                handleMessage((PubRecMessage) msg);
                break;
            case PUBREL:
                handleMessage((PubRelMessage) msg);
                break;
            case PUBCOMP:
                //TODO
                break;
            case SUBACK:
                handleMessage((SubAckMessage) msg);
                break;
            case UNSUBACK:
                handleMessage((UnsubAckMessage) msg);
                break;
            case PINGRESP:
                handleMessage((PingRespMessage) msg);
                break;
            default:
                break;
        }
    }

    public void handleMessage(ConnAckMessage msg) {
        for (MqttListener listener : listeners) {
            listener.connectAck(msg.getStatus());
        }
    }

    public void handleMessage(PublishMessage msg) {
        for (MqttListener listener : listeners) {
            listener.publishArrived(msg.getTopic(), msg.getData());
        }
    }

    public void handleMessage(PubRecMessage msg) {
        //TODO
    }

    public void handleMessage(PubRelMessage msg) {
        //TODO
    }

    public void handleMessage(SubAckMessage msg) {
        //TODO
    }

    public void handleMessage(UnsubAckMessage msg) {
        //TODO
    }

    public void handleMessage(PingRespMessage msg) {
        //TODO
    }

    public void addListener(MqttListener listener) {
        listeners.add(listener);
    }

    public void setListeners(List<MqttListener> listeners) {
        this.listeners = listeners;
    }
}
