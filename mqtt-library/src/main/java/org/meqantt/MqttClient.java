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

import java.util.List;

/**
 * Common interface for all Mqtt clients
 */
public interface MqttClient {

    public String getId();

    public void connect(String host, int port) throws MqttException;

    public void disconnect() throws MqttException;

    public void subscribe(String topic) throws MqttException;

    public void unsubscribe(String topic) throws MqttException;

    public void publish(String topic, String msg) throws MqttException;

    public void ping() throws MqttException;

    public void addListener(MqttListener listener);

    public List<MqttListener> getListeners();
}
