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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.bootique.annotation.BQConfigProperty;

/**
 * LevelFilter filters events based on exact level matching.
 * If the event's level is equal to the configured level,
 * the filter accepts or denies the event,
 * depending on the configuration of the onMatch and onMismatch properties.
 *
 * @since 2.0
 */
@JsonTypeName("level")
public class LevelFilterFactory extends FilterFactory {

    private String onMatch;
    private String onMismatch;

    public String getOnMatch() {
        return onMatch;
    }

    /**
     * @since 2.0
     * @param onMatch
     */
    @BQConfigProperty
    public void setOnMatch(String onMatch) {
        this.onMatch = onMatch;
    }

    public String getOnMismatch() {
        return onMismatch;
    }

    /**
     * @since 2.0
     * @param onMismatch
     */
    @BQConfigProperty
    public void setOnMismatch(String onMismatch) {
        this.onMismatch = onMismatch;
    }

    @Override
    public Filter<ILoggingEvent> createFilter() {
        LevelFilter levelFilter = createLevelFilter();

        return levelFilter;
    }

    protected LevelFilter createLevelFilter() {
        LevelFilter levelFilter = new LevelFilter();
        levelFilter.setLevel(Level.valueOf(getLevel()));
        levelFilter.setOnMatch(getFilterReply(getOnMatch()));
        levelFilter.setOnMismatch(getFilterReply(getOnMismatch()));
        levelFilter.setName("level");
        setType("level");

        levelFilter.start();

        return levelFilter;
    }

    public FilterReply getFilterReply(String string) {
        if (FilterReply.DENY.toString().equals(string) ) {
            return FilterReply.DENY;
        } else if (FilterReply.ACCEPT.toString().equals(string)) {
            return FilterReply.ACCEPT;
        } else {
            return FilterReply.NEUTRAL;
        }
    }
}
