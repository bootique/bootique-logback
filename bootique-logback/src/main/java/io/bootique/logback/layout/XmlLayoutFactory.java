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
import ch.qos.logback.classic.log4j.XMLLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;

/**
 *
 * Create XMLLayout that Generates log4j.dtd compliant XML documents.
 * For more information about this layout, please refer
 * to the online manual at http://logback.qos.ch/manual/layouts.html#log4jXMLLayout
 *
 * @since 3.0
 */
@JsonTypeName("xml")
@BQConfig("XMLLayout generates output in a log4j.dtd compliant format")
public class XmlLayoutFactory extends LayoutFactory {
    private boolean locationInfo;
    private boolean properties;

    /**
     * @return location info flag.
     */
    public boolean getLocationInfo() {
        return locationInfo;
    }

    /**
     * Sets location info flag that enables the inclusion of location info (caller data) in the each event.
     * By default is false.
     */
    @BQConfigProperty("Set locationInfo that enables the inclusion of location info (caller data) in the each event. " +
            "Default is false.")
    public void setLocationInfo(boolean locationInfo) {
        this.locationInfo = locationInfo;
    }

    /**
     * @return flag properties
     */
    public boolean getProperties() {
        return properties;
    }

    /**
     * Sets properties flag that enables the inclusion of MDC information.
     * Default is false.
     */
    @BQConfigProperty("Set properties that enables the inclusion of MDC information. Default is false.")
    public void setProperties(boolean properties) {
        this.properties = properties;
    }

    @Override
    public Layout<ILoggingEvent> createLayout(LoggerContext context, String logFormat) {
        XMLLayout layout = new XMLLayout();
        layout.setContext(context);
        layout.setLocationInfo(locationInfo);
        layout.setProperties(properties);

        layout.start();
        return layout;
    }
}
