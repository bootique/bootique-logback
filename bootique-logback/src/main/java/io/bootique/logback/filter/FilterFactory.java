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

package io.bootique.logback.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.config.PolymorphicConfiguration;

/**
 * @since 2.0
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@BQConfig
public abstract class FilterFactory implements PolymorphicConfiguration {

    private String type;
    private String level;

    public String getType() {
        return type;
    }

    /**
     * @since 2.0
     * @param type
     */
    @BQConfigProperty
    public void setType(String type) {
        this.type = type;
    }

    public String getLevel() {
        return level;
    }

    /**
     * @since 2.0
     * @param level
     */
    @BQConfigProperty
    public void setLevel(String level) {
        this.level = level;
    }

    public abstract Filter<ILoggingEvent> createFilter();

}