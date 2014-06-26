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

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMqttClient implements MqttClient {

    protected String id;
    protected List<MqttListener> listeners = new ArrayList<MqttListener>();
    protected MqttMessageHandler handler;

    public AbstractMqttClient() {
        this.id = String.valueOf(System.nanoTime());
    }

    public String getId() {
        return id;
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

    public List<MqttListener> getListeners() {
        return listeners;
    }
}
