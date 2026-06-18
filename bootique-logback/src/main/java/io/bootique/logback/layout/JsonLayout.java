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
import ch.qos.logback.core.LayoutBase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A Logback {@link ch.qos.logback.core.Layout} that renders each event as a JSON object.
 *
 * @since 4.0
 */
public class JsonLayout extends LayoutBase<ILoggingEvent> {

    private static final String TIMESTAMP_ATTR_NAME = "timestamp";
    private static final String LEVEL_ATTR_NAME = "level";
    private static final String THREAD_ATTR_NAME = "thread";
    private static final String MDC_ATTR_NAME = "mdc";
    private static final String LOGGER_ATTR_NAME = "logger";
    private static final String FORMATTED_MESSAGE_ATTR_NAME = "message";
    private static final String MESSAGE_ATTR_NAME = "raw-message";
    private static final String EXCEPTION_ATTR_NAME = "exception";
    private static final String CONTEXT_ATTR_NAME = "context";

    private final ObjectMapper objectMapper;
    private final ThrowableHandlingConverter throwableProxyConverter;

    private boolean prettyPrint = false;

    private String timestampFormat;

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
        return toJsonString(map);
    }

    protected Map<String, Object> toJsonMap(ILoggingEvent event) {
        Map<String, Object> map = new LinkedHashMap<>();

        addTimestamp(TIMESTAMP_ATTR_NAME, event.getTimeStamp(), map);
        add(LEVEL_ATTR_NAME, true, String.valueOf(event.getLevel()), map);
        add(THREAD_ATTR_NAME, true, event.getThreadName(), map);
        addMap(MDC_ATTR_NAME, event.getMDCPropertyMap(), map);
        add(LOGGER_ATTR_NAME, true, event.getLoggerName(), map);
        add(FORMATTED_MESSAGE_ATTR_NAME, true, event.getFormattedMessage(), map);
        add(MESSAGE_ATTR_NAME, true, event.getMessage(), map);
        add(CONTEXT_ATTR_NAME, true, contextName(event), map);
        addThrowableInfo(EXCEPTION_ATTR_NAME, event, map);

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

    protected void addTimestamp(String key, long timestamp, Map<String, Object> map) {
        String formatted = formatTimestamp(timestamp);
        if (formatted != null) {
            map.put(key, formatted);
        }
    }

    protected void add(String key, boolean field, String value, Map<String, Object> map) {
        if (field && value != null) {
            map.put(key, value);
        }
    }

    protected void addMap(String key, Map<String, ?> value, Map<String, Object> map) {
        if (value != null && !value.isEmpty()) {
            map.put(key, value);
        }
    }

    protected void addThrowableInfo(String key, ILoggingEvent event, Map<String, Object> map) {
        if (event != null) {
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
        return format.format(new Date(timestamp));
    }

    private static String contextName(ILoggingEvent event) {
        LoggerContextVO vo = event.getLoggerContextVO();
        return vo != null ? vo.getName() : null;
    }

    public void setTimestampFormat(String timestampFormat) {
        this.timestampFormat = timestampFormat;
    }

    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }
}
