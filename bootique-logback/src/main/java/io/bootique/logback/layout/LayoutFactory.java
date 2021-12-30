/*
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.bootique.logback.layout;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.config.PolymorphicConfiguration;

/**
 *
 * Create layout for appender. By default is PatternLayout.
 *
 * @since 3.0
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@BQConfig("Create layout. By default is PatternLayout.")
public abstract class LayoutFactory implements PolymorphicConfiguration {
    private String type;

    public String getType() {
        return type;
    }

    /**
     * @param type layout type, available types: "pattern", "json", "html", "xml". By default is "pattern".
     */
    @BQConfigProperty("Content out type (layout), available types: \"pattern\", \"json\", \"html\", \"xml\". By default is \"pattern\".")
    public void setType(String type) {
        this.type = type;
    }


    public abstract Layout<ILoggingEvent> createLayout(LoggerContext context, String defaultLogFormat);

}
