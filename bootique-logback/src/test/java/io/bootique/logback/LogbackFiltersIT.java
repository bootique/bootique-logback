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

package io.bootique.logback;

import ch.qos.logback.classic.Logger;
import io.bootique.logback.unit.LogbackTestFactory;
import org.junit.Rule;
import org.junit.Test;

import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LogbackFiltersIT {

    @Rule
    public LogbackTestFactory LOGGER_STACK = new LogbackTestFactory();

    /**
     * Checks file appender with ThresholdFilter
     */
    @Test
    public void testFileAppenderThresholdFilter() {

        LOGGER_STACK.prepareLogDir("target/logs/rotate");
        Logger logger = LOGGER_STACK.newRootLogger("classpath:io/bootique/logback/test-file-appender-filter-threshold.yml");
        logger.debug("debug-log-to-file");
        logger.info("info-log-to-file");
        logger.warn("warn-log-to-file");

        // must stop to ensure logs are flushed...
        LOGGER_STACK.stop();

        Map<String, String[]> logfileContents = LOGGER_STACK.loglines("target/logs/rotate", "threshold.log");

        assertEquals(1, logfileContents.size());
        String[] lines = logfileContents.get("threshold.log");
        String oneLine = String.join("\n", asList(lines));

        assertTrue("Unexpected logs: " + oneLine, oneLine.endsWith("ROOT: warn-log-to-file"));
    }

    /**
     * Checks file appender with LevelFilter
     */
    @Test
    public void testFileAppenderLevelFilter() {

        LOGGER_STACK.prepareLogDir("target/logs/rotate");
        Logger logger = LOGGER_STACK.newRootLogger("classpath:io/bootique/logback/test-file-appender-filter-level.yml");
        logger.debug("debug-log-to-file");
        logger.info("info-log-to-file");
        logger.warn("warn-log-to-file");

        // must stop to ensure logs are flushed...
        LOGGER_STACK.stop();

        Map<String, String[]> logfileContents = LOGGER_STACK.loglines("target/logs/rotate", "level.log");

        assertEquals(1, logfileContents.size());
        String[] lines = logfileContents.get("level.log");
        String oneLine = String.join("\n", asList(lines));

        assertTrue("Unexpected logs: " + oneLine, oneLine.endsWith("ROOT: info-log-to-file"));
    }

    /**
     * Checks file appender with LevelFilter and ThresholdFilter
     * One appender, two filters
     */
    @Test
    public void testFileAppenderLevelFilterAndThresholdFilter() {

        LOGGER_STACK.prepareLogDir("target/logs/rotate");
        Logger logger = LOGGER_STACK.newRootLogger("classpath:io/bootique/logback/test-file-appender-filter-level-threshold.yml");
        logger.debug("debug-log-to-file");
        logger.info("info-log-to-file");
        logger.warn("warn-log-to-file");

        // must stop to ensure logs are flushed...
        LOGGER_STACK.stop();

        Map<String, String[]> logfileContents = LOGGER_STACK.loglines("target/logs/rotate", "filter.log");

        assertEquals(1, logfileContents.size());
        String[] lines = logfileContents.get("filter.log");
        String oneLine = String.join("\n", asList(lines));

        assertTrue("Unexpected logs: " + oneLine, oneLine.endsWith("ROOT: warn-log-to-file"));
    }

    /**
     * Checks file appenders with LevelFilter and ThresholdFilter
     * Each appender with one filter
     */
    @Test
    public void testFileAppendersLevelFilterAndThresholdFilter() {

        LOGGER_STACK.prepareLogDir("target/logs/rotate");
        Logger logger = LOGGER_STACK.newRootLogger("classpath:io/bootique/logback/test-file-appenders-filter-level-threshold.yml");
        logger.debug("debug-log-to-file");
        logger.info("info-log-to-file");
        logger.warn("warn-log-to-file");

        // must stop to ensure logs are flushed...
        LOGGER_STACK.stop();

        Map<String, String[]> logfileContents = LOGGER_STACK.loglines("target/logs/rotate", "threshold.log");

        assertEquals(1, logfileContents.size());
        String[] lines = logfileContents.get("threshold.log");
        String oneLine = String.join("\n", asList(lines));

        assertTrue("Unexpected logs: " + oneLine, oneLine.endsWith("ROOT: warn-log-to-file"));

        logfileContents = LOGGER_STACK.loglines("target/logs/rotate", "level.log");

        assertEquals(1, logfileContents.size());
        lines = logfileContents.get("level.log");
        oneLine = String.join("\n", asList(lines));

        assertTrue("Unexpected logs: " + oneLine, oneLine.endsWith("ROOT: info-log-to-file"));
    }

}
