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

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.RollingPolicy;
import ch.qos.logback.core.rolling.TriggeringPolicy;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.logback.policy.RollingPolicyFactory;

import java.util.Objects;

/**
 * A configuration object that sets up a file appender in Logback, potentially with support for rotation, etc.
 */
@JsonTypeName("file")
public class FileAppenderFactory extends AppenderFactory {

    private String file;
    private RollingPolicyFactory rollingPolicy;
    private boolean append = true;

    /**
     * @deprecated factory getters should not be publicly accessible
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public String getFile() {
        return file;
    }

    @BQConfigProperty("A filename for the current log file.")
    public void setFile(String file) {
        this.file = file;
    }


    /**
     * @deprecated factory getters should not be publicly accessible
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public RollingPolicyFactory getRollingPolicy() {
        return rollingPolicy;
    }

    /**
     * Rolling policy factory what defines rolling policy for rotation. If rolling policy factory is not defined the
     * rotation is not used
     */
    @BQConfigProperty
    public void setRollingPolicy(RollingPolicyFactory rollingPolicy) {
        this.rollingPolicy = rollingPolicy;
    }

    /**
     * @since 1.1
     * @deprecated factory getters should not be publicly accessible
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public boolean isAppend() {
        return append;
    }

    /**
     * @since 1.1
     */
    @BQConfigProperty("Whether to append to the existing file")
    public void setAppend(boolean append) {
        this.append = append;
    }

    @Override
    public Appender<ILoggingEvent> createAppender(LoggerContext context, String defaultLogFormat) {

        LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
        encoder.setLayout(createLayout(context, defaultLogFormat));

        FileAppender<ILoggingEvent> appender = (rollingPolicy == null)
                ? createSingleFileAppender(encoder, context)
                : createRollingFileAppender(encoder, context, rollingPolicy);

        appender.start();
        return asAsync(appender);
    }

    protected FileAppender<ILoggingEvent> createSingleFileAppender(
            Encoder<ILoggingEvent> encoder,
            LoggerContext context) {

        FileAppender<ILoggingEvent> appender = new FileAppender<>();

        appender.setName(name);
        appender.setFile(Objects.requireNonNull(file));
        appender.setContext(context);
        appender.setEncoder(encoder);
        appender.setAppend(append);

        createFilters(appender);

        return appender;
    }

    protected FileAppender<ILoggingEvent> createRollingFileAppender(
            Encoder<ILoggingEvent> encoder,
            LoggerContext context,
            RollingPolicyFactory rollingPolicyFactory) {

        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
        appender.setName(name);
        appender.setFile(file);
        appender.setContext(context);
        appender.setEncoder(encoder);
        appender.setAppend(append);

        RollingPolicy rollingPolicy = rollingPolicyFactory.createRollingPolicy(context);
        appender.setRollingPolicy(rollingPolicy);
        rollingPolicy.setParent(appender);
        rollingPolicy.start();

        TriggeringPolicy<ILoggingEvent> triggeringPolicy = rollingPolicyFactory.createTriggeringPolicy(context);
        if (triggeringPolicy != null) {
            appender.setTriggeringPolicy(triggeringPolicy);
            triggeringPolicy.start();
        }

        createFilters(appender);

        return appender;
    }

}
