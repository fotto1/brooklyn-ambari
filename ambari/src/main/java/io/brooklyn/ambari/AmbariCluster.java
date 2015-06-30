/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.brooklyn.ambari;

import java.util.List;
import java.util.Map;

import com.google.common.reflect.TypeToken;

import brooklyn.catalog.Catalog;
import brooklyn.config.ConfigKey;
import brooklyn.entity.Entity;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.basic.SoftwareProcess;
import brooklyn.entity.group.Cluster;
import brooklyn.entity.group.DynamicCluster;
import brooklyn.entity.proxying.EntitySpec;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.entity.trait.Startable;
import brooklyn.event.AttributeSensor;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.event.basic.MapConfigKey;
import brooklyn.event.basic.Sensors;
import brooklyn.util.flags.SetFromFlag;
import io.brooklyn.ambari.agent.AmbariAgent;
import io.brooklyn.ambari.server.AmbariServer;

@Catalog(name = "Ambari Cluster", description = "Ambari Cluster: Made up of one or more Ambari Server and One or more Ambari Agents")
@ImplementedBy(AmbariClusterImpl.class)
public interface AmbariCluster extends Entity, Startable {

    @SetFromFlag("initialSize")
    ConfigKey<Integer> INITIAL_SIZE = ConfigKeys.newConfigKeyWithDefault(Cluster.INITIAL_SIZE, 0);

    AttributeSensor<Integer> EXPECTED_AGENTS = Sensors.newIntegerSensor("ambaricluster.expectedservers", "Number of ambari agents expected to register with cluster");

    @SetFromFlag("securityGroup")
    ConfigKey<String> SECURITY_GROUP = ConfigKeys.newStringConfigKey("securityGroup", "Security group to be shared by agents and server");
    
    @SetFromFlag("services")
    ConfigKey<List<String>> HADOOP_SERVICES = ConfigKeys.newConfigKey(new TypeToken<List<String>>() {
    }, "services", "List of services to deploy to Hadoop Cluster");

    ConfigKey<EntitySpec<? extends AmbariServer>> SERVER_SPEC = BasicConfigKey.builder(new TypeToken<EntitySpec<? extends AmbariServer>>() {
    }).name("ambaricluster.serverspec")
      .defaultValue(EntitySpec.create(AmbariServer.class))
      .build();
    
    ConfigKey<EntitySpec<? extends AmbariAgent>> AGENT_SPEC = BasicConfigKey.builder(new TypeToken<EntitySpec<? extends AmbariAgent>>() {}
    ).name("ambaricluster.agentspec")
     .defaultValue(EntitySpec.create(AmbariAgent.class))
     .build();

    @SetFromFlag("hostAddressSensor")
    ConfigKey<AttributeSensor<String>> ETC_HOST_ADDRESS = AmbariConfigAndSensors.ETC_HOST_ADDRESS;

    @SetFromFlag("version")
    ConfigKey<String> SUGGESTED_VERSION = ConfigKeys.newConfigKeyWithDefault(SoftwareProcess.SUGGESTED_VERSION, "1.7.0");

    @SetFromFlag("ambariConfigMap")
    ConfigKey<Map<String, Map>> AMBARI_CONFIGURATIONS =
            new MapConfigKey<Map>(Map.class, "ambari.configurations", "Map of maps");

    AttributeSensor<AmbariServer> AMBARI_SERVER = Sensors.newSensor(
            AmbariServer.class, "ambaricluster.configservers", "Config servers");

    AttributeSensor<DynamicCluster> AMBARI_AGENTS = Sensors.newSensor(
            DynamicCluster.class, "ambaricluster.configagents", "Config agents");

    AttributeSensor<Boolean> CLUSTER_SERVICES_INITIALISE_CALLED = Sensors.newBooleanSensor("ambari.cluster.servicesInitialiseCalled");

    void installServices();
}
