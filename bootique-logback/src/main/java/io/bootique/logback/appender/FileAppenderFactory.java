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
import io.bootique.logback.policy.FixedWindowPolicyFactory;
import io.bootique.logback.policy.RollingPolicyFactory;
import io.bootique.logback.policy.TimeBasedPolicyFactory;

import java.util.Objects;

/**
 * A configuration object that sets up a file appender in Logback, potentially
 * with support for rotation, etc.
 *
 * @since 0.8
 */
@JsonTypeName("file")
public class FileAppenderFactory extends AppenderFactory {

    private String file;
    private RollingPolicyFactory rollingPolicy;

    /**
     * @return name of the log file.
     * @since 0.12
     */
    public String getFile() {
        return file;
    }

    /**
     * Sets a filename for the current log file.
     *
     * @param file a filename for the current log file.
     */
    @BQConfigProperty
    public void setFile(String file) {
        this.file = file;
    }

    /**
     * @return factory for log file rotation policy
     * @since 0.12
     */
    public RollingPolicyFactory getRollingPolicy() {
        return rollingPolicy;
    }

    /**
     * Rolling policy factory what defines rolling policy for rotation.
     * If rolling policy factory is not defined the rotation is not used
     *
     * @param rollingPolicy a rolling policy factory
     * @see RollingPolicyFactory
     * @see FixedWindowPolicyFactory
     * @see TimeBasedPolicyFactory
     * @see ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy
     * @since 0.10
     */
    @BQConfigProperty
    public void setRollingPolicy(RollingPolicyFactory rollingPolicy) {
        this.rollingPolicy = rollingPolicy;
    }

    @Override
    public Appender<ILoggingEvent> createAppender(LoggerContext context, String defaultLogFormat) {

        LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
        encoder.setLayout(createLayout(context, defaultLogFormat));

        FileAppender<ILoggingEvent> appender = (rollingPolicy == null)
                ? createSingleFileAppender(encoder, context)
                : createRollingFileAppender(encoder, context, rollingPolicy);

        appender.setName(getName());
        return asAsync(appender);
    }

    protected FileAppender<ILoggingEvent> createSingleFileAppender(Encoder<ILoggingEvent> encoder,
                                                                   LoggerContext context) {
        FileAppender<ILoggingEvent> appender = new FileAppender<>();
        appender.setFile(Objects.requireNonNull(file));

        appender.setContext(context);
        appender.setEncoder(encoder);
        appender.start();

        return appender;
    }

    protected FileAppender<ILoggingEvent> createRollingFileAppender(Encoder<ILoggingEvent> encoder,
                                                                    LoggerContext context,
                                                                    RollingPolicyFactory rollingPolicy) {

        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
        appender.setFile(file);
        appender.setContext(context);
        appender.setEncoder(encoder);
        // Setup rolling policy
        RollingPolicy policy = rollingPolicy.createRollingPolicy(context);
        appender.setRollingPolicy(policy);
        policy.setParent(appender);
        // Setup triggering policy
        TriggeringPolicy<ILoggingEvent> triggeringPolicy = rollingPolicy.createTriggeringPolicy(context);
        if (triggeringPolicy != null) {
            appender.setTriggeringPolicy(triggeringPolicy);
            triggeringPolicy.start();
        }
        policy.start();
        appender.start();

        return appender;
    }

}
