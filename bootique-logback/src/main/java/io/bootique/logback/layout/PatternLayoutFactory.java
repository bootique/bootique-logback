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
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;

/**
 *
 * * <p>
 * A flexible layout configurable with pattern string. The format of the string depends on the
 * <em>conversion pattern</em>.
 * <p>
 * For more information about this layout, please refer to the online manual at
 * http://logback.qos.ch/manual/layouts.html#PatternLayout
 *
 * @since 3.0
 */
@JsonTypeName("pattern")
@BQConfig("PatternLayout takes a logging event and returns a String. String can be customized by tweaking PatternLayout's conversion pattern(Parameter \'logFormat\').")
public class PatternLayoutFactory extends LayoutFactory {
    private String logFormat;

    /**
     * @return configured log format.
     */
    public String getLogFormat() {
        return logFormat;
    }

    /**
     * Set log format for output pattern. By default is set in {@link io.bootique.logback.LogbackContextFactory}
     */
    @BQConfigProperty("Log format for output pattern. By default is set in {@link io.bootique.logback.LogbackContextFactory}.")
    public void setLogFormat(String logFormat) {
        this.logFormat = logFormat;
    }

    @Override
    public Layout<ILoggingEvent> createLayout(LoggerContext context, String logFormat) {
        String currentLogFormat = this.logFormat != null ? this.logFormat : logFormat;

        PatternLayout layoutPattern = new PatternLayout();
        layoutPattern.setPattern(currentLogFormat);
        layoutPattern.setContext(context);

        layoutPattern.start();
        return layoutPattern;
    }
}
