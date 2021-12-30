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

package io.bootique.logback.appender;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AsyncAppenderBase;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.Layout;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.config.PolymorphicConfiguration;
import io.bootique.logback.filter.FilterFactory;
import io.bootique.logback.layout.PatternLayoutFactory;
import io.bootique.logback.layout.LayoutFactory;

import java.util.Collection;

@BQConfig("Appender of a given type.")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = ConsoleAppenderFactory.class)
public abstract class AppenderFactory implements PolymorphicConfiguration {

    private String logFormat;
    private String name;

    /**
     * @since 3.0
     */
    private LayoutFactory layout;

    private Collection<FilterFactory> filters;

    /**
     * @return configured log format
     */
    public String getLogFormat() {
        return logFormat;
    }

    @BQConfigProperty("Log format specification compatible with Logback framework. If not set, the value is propagated " +
            "from the parent configuration.")
    public void setLogFormat(String logFormat) {
        this.logFormat = logFormat;
    }

    public abstract Appender<ILoggingEvent> createAppender(LoggerContext context, String defaultLogFormat);


    /**
     * @since 3.0
     */
    public LayoutFactory getLayout() {
        return layout;
    }

    /**
     * @since 3.0
     */
    @BQConfigProperty("Layout for log message.")
    public void setLayout(LayoutFactory layout) {
        this.layout = layout;
    }

    public String getName() {
        return name;
    }

    @BQConfigProperty("Appender name.")
    public void setName(String name) {
        this.name = name;
    }

    public Collection<FilterFactory> getFilters() {
        return filters;
    }

    /**
     * @since 2.0
     * @param filters configuration of Logabck filters
     */
    @BQConfigProperty("Collection of Logback filters")
    public void setFilters(Collection<FilterFactory> filters) {
        this.filters = filters;
    }

    protected Appender<ILoggingEvent> createFilters(Appender<ILoggingEvent> appender) {
        if (filters != null) {
            filters.forEach(filter -> appender.addFilter(filter.createFilter()));
        }
        return appender;
    }

    protected Layout<ILoggingEvent> createLayout(LoggerContext context, String defaultLogFormat) {
        String logFormat = this.logFormat != null ? this.logFormat : defaultLogFormat;

        if(layout == null) {
            layout = new PatternLayoutFactory();
        }

        Layout<ILoggingEvent> currentLayout = layout.createLayout(context, logFormat);
        if (!currentLayout.isStarted()) {
            throw new RuntimeException("Layout isn't configured for starting : " + currentLayout);
        }
        return currentLayout;
    }

    protected Appender<ILoggingEvent> asAsync(Appender<ILoggingEvent> appender) {
        return asAsync(appender, appender.getContext());
    }

    protected Appender<ILoggingEvent> asAsync(Appender<ILoggingEvent> appender, Context context) {
        AsyncAppender asyncAppender = new AsyncAppender();
        asyncAppender.setIncludeCallerData(false);
        asyncAppender.setQueueSize(AsyncAppenderBase.DEFAULT_QUEUE_SIZE);
        asyncAppender.setDiscardingThreshold(-1);
        asyncAppender.setContext(context);
        asyncAppender.setName(appender.getName());
        asyncAppender.addAppender(appender);
        asyncAppender.start();
        return asyncAppender;
    }
}
