/*
 * Copyright (C) 2024 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

/**
 * The {@link com.google.cloud.teleport.v2.templates.ProducerProperties} returns
 * client properties for destination Kafka.
 */
package com.google.cloud.teleport.v2.templates;

import com.google.cloud.teleport.v2.options.KafkaToKafkaOptions;
import com.google.common.collect.ImmutableMap;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SaslConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ProducerProperties {
  private static final Logger LOGG = LoggerFactory.getLogger(ProducerProperties.class);
  public static ImmutableMap<String, Object> get(KafkaToKafkaOptions options) {

    String[] saslCredentials = Credentials.accessSecretVersion(options).toArray(new String[0]);
    ImmutableMap.Builder<String, Object> properties = ImmutableMap.builder();
    properties.put(
        CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG,
        options.getDestinationBootstrapServer());
    properties.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
    //         Note: in other languages, set sasl.username and sasl.password instead.
    properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
    properties.put(
        SaslConfigs.SASL_JAAS_CONFIG,
        "org.apache.kafka.common.security.plain.PlainLoginModule required"
            + " username=\'"
            + saslCredentials[2]
            + "\'"
            + " password=\'"
            + saslCredentials[3]
            + "\';");

    return properties.buildOrThrow();
  }
}
