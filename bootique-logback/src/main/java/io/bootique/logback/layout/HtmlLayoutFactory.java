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
import ch.qos.logback.classic.html.HTMLLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;

/**
 * @since 3.0
 */
@JsonTypeName("html")
@BQConfig
public class HtmlLayoutFactory extends LayoutFactory{
    private static final String DEFAULT_PATTERN = "%relative%thread%mdc%level%logger%msg";
    private String pattern;

    /**
     * @return configured pattern (log format)
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * Sets pattern for output logs.
     */
    @BQConfigProperty("Set pattern.")
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public Layout<ILoggingEvent> createLayout(LoggerContext context, String defaultLogFormat) {
        String currentPattern = this.pattern != null ? this.pattern : DEFAULT_PATTERN;

        HTMLLayout layout = new HTMLLayout();
        layout.setContext(context);
        layout.setPattern(currentPattern);

        layout.start();
        return layout;
    }
}
