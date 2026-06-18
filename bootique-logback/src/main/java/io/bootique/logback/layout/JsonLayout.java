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

import ch.qos.logback.classic.pattern.ThrowableHandlingConverter;
import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggerContextVO;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LayoutBase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * A Logback {@link ch.qos.logback.core.Layout} that renders each event as a JSON object. This is a Bootique port of the
 * (now unmaintained) "logback-contrib" {@code JsonLayout}/{@code JacksonJsonFormatter}, preserving identical output and
 * the {@code timestampFormat}/{@code prettyPrint} options, but without the external dependency. Serialization is done
 * with Jackson, which is already on the Bootique classpath.
 *
 * @since 4.0
 */
public class JsonLayout extends LayoutBase<ILoggingEvent> {

    public static final String TIMESTAMP_ATTR_NAME = "timestamp";
    public static final String LEVEL_ATTR_NAME = "level";
    public static final String THREAD_ATTR_NAME = "thread";
    public static final String MDC_ATTR_NAME = "mdc";
    public static final String LOGGER_ATTR_NAME = "logger";
    public static final String FORMATTED_MESSAGE_ATTR_NAME = "message";
    public static final String MESSAGE_ATTR_NAME = "raw-message";
    public static final String EXCEPTION_ATTR_NAME = "exception";
    public static final String CONTEXT_ATTR_NAME = "context";

    private final ObjectMapper objectMapper;
    private final ThrowableHandlingConverter throwableProxyConverter;

    private boolean includeTimestamp = true;
    private boolean includeLevel = true;
    private boolean includeThreadName = true;
    private boolean includeMDC = true;
    private boolean includeLoggerName = true;
    private boolean includeFormattedMessage = true;
    private boolean includeMessage = false;
    private boolean includeException = true;
    private boolean includeContextName = true;
    private boolean appendLineSeparator = false;
    private boolean prettyPrint = false;

    private String timestampFormat;
    private String timestampFormatTimezoneId;

    public JsonLayout() {
        this.objectMapper = new ObjectMapper();
        this.throwableProxyConverter = new ThrowableProxyConverter();
    }

    @Override
    public void start() {
        throwableProxyConverter.start();
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        throwableProxyConverter.stop();
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        Map<String, Object> map = toJsonMap(event);
        if (map == null || map.isEmpty()) {
            return null;
        }
        String result = toJsonString(map);
        return appendLineSeparator ? result + CoreConstants.LINE_SEPARATOR : result;
    }

    protected Map<String, Object> toJsonMap(ILoggingEvent event) {
        Map<String, Object> map = new LinkedHashMap<>();

        addTimestamp(TIMESTAMP_ATTR_NAME, includeTimestamp, event.getTimeStamp(), map);
        add(LEVEL_ATTR_NAME, includeLevel, String.valueOf(event.getLevel()), map);
        add(THREAD_ATTR_NAME, includeThreadName, event.getThreadName(), map);
        addMap(MDC_ATTR_NAME, includeMDC, event.getMDCPropertyMap(), map);
        add(LOGGER_ATTR_NAME, includeLoggerName, event.getLoggerName(), map);
        add(FORMATTED_MESSAGE_ATTR_NAME, includeFormattedMessage, event.getFormattedMessage(), map);
        add(MESSAGE_ATTR_NAME, includeMessage, event.getMessage(), map);
        add(CONTEXT_ATTR_NAME, includeContextName, contextName(event), map);
        addThrowableInfo(EXCEPTION_ATTR_NAME, includeException, event, map);

        return map;
    }

    protected String toJsonString(Map<String, Object> map) {
        ObjectWriter writer = prettyPrint ? objectMapper.writerWithDefaultPrettyPrinter() : objectMapper.writer();
        try {
            return writer.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            addError("Error formatting JSON log event. Defaulting to map.toString()", e);
            return map.toString();
        }
    }

    protected void addTimestamp(String key, boolean field, long timestamp, Map<String, Object> map) {
        if (field) {
            String formatted = formatTimestamp(timestamp);
            if (formatted != null) {
                map.put(key, formatted);
            }
        }
    }

    protected void add(String key, boolean field, String value, Map<String, Object> map) {
        if (field && value != null) {
            map.put(key, value);
        }
    }

    protected void addMap(String key, boolean field, Map<String, ?> value, Map<String, Object> map) {
        if (field && value != null && !value.isEmpty()) {
            map.put(key, value);
        }
    }

    protected void addThrowableInfo(String key, boolean field, ILoggingEvent event, Map<String, Object> map) {
        if (field && event != null) {
            IThrowableProxy throwableProxy = event.getThrowableProxy();
            if (throwableProxy != null) {
                String ex = throwableProxyConverter.convert(event);
                if (ex != null && !ex.isEmpty()) {
                    map.put(key, ex);
                }
            }
        }
    }

    protected String formatTimestamp(long timestamp) {
        if (timestampFormat == null || timestamp < 0) {
            return String.valueOf(timestamp);
        }

        DateFormat format = new SimpleDateFormat(timestampFormat);
        if (timestampFormatTimezoneId != null) {
            format.setTimeZone(TimeZone.getTimeZone(timestampFormatTimezoneId));
        }
        return format.format(new Date(timestamp));
    }

    private static String contextName(ILoggingEvent event) {
        LoggerContextVO vo = event.getLoggerContextVO();
        return vo != null ? vo.getName() : null;
    }

    public void setTimestampFormat(String timestampFormat) {
        this.timestampFormat = timestampFormat;
    }

    public void setTimestampFormatTimezoneId(String timestampFormatTimezoneId) {
        this.timestampFormatTimezoneId = timestampFormatTimezoneId;
    }

    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }

    public void setAppendLineSeparator(boolean appendLineSeparator) {
        this.appendLineSeparator = appendLineSeparator;
    }

    public void setIncludeTimestamp(boolean includeTimestamp) {
        this.includeTimestamp = includeTimestamp;
    }

    public void setIncludeLevel(boolean includeLevel) {
        this.includeLevel = includeLevel;
    }

    public void setIncludeThreadName(boolean includeThreadName) {
        this.includeThreadName = includeThreadName;
    }

    public void setIncludeMDC(boolean includeMDC) {
        this.includeMDC = includeMDC;
    }

    public void setIncludeLoggerName(boolean includeLoggerName) {
        this.includeLoggerName = includeLoggerName;
    }

    public void setIncludeFormattedMessage(boolean includeFormattedMessage) {
        this.includeFormattedMessage = includeFormattedMessage;
    }

    public void setIncludeMessage(boolean includeMessage) {
        this.includeMessage = includeMessage;
    }

    public void setIncludeException(boolean includeException) {
        this.includeException = includeException;
    }

    public void setIncludeContextName(boolean includeContextName) {
        this.includeContextName = includeContextName;
    }
}
