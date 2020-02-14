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

import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * The ThresholdFilter filters events below the specified threshold.
 * For events of level equal or above the threshold,
 * ThresholdFilter will respond NEUTRAL.
 * However, events with a level below the threshold will be denied.
 *
 * @since 2.0
 */
@JsonTypeName("threshold")
public class ThresholdFilterFactory extends FilterFactory {

    @Override
    public Filter<ILoggingEvent> createFilter() {
        return createThresholdFilter();
    }

    protected ThresholdFilter createThresholdFilter() {
        ThresholdFilter thresholdFilter = new ThresholdFilter();
        thresholdFilter.setLevel(getLevel());
        thresholdFilter.setName("threshold");
        setType("threshold");

        thresholdFilter.start();

        return thresholdFilter;
    }
}