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

import io.bootique.junit5.BQTest;
import io.bootique.junit5.BQTestFactory;
import io.bootique.junit5.BQTestTool;
import io.bootique.logback.unit.LogTester;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@BQTest
public class LogbackFiltersIT {

    @BQTestTool
    final BQTestFactory testFactory = new BQTestFactory().autoLoadModules();

    @BQTestTool
    final LogTester logTester = new LogTester(testFactory, "target/logs");

    /**
     * Checks file appender with ThresholdFilter
     */
    @Test
    public void fileAppenderThresholdFilter() {

        String thresholdLog = logTester.run(
                "classpath:io/bootique/logback/test-file-appender-filter-threshold.yml",
                "threshold.log",
                l -> {
                    l.debug("debug-log-to-file");
                    l.info("info-log-to-file");
                    l.warn("warn-log-to-file");
                });

        assertTrue(thresholdLog.endsWith("ROOT: warn-log-to-file"), () -> "Unexpected log: " + thresholdLog);
    }

    /**
     * Checks file appender with LevelFilter
     */
    @Test
    public void fileAppenderLevelFilter() {

        String levelLog = logTester.run(
                "classpath:io/bootique/logback/test-file-appender-filter-level.yml",
                "level.log",
                l -> {
                    l.debug("debug-log-to-file");
                    l.info("info-log-to-file");
                    l.warn("warn-log-to-file");
                });

        assertTrue(levelLog.endsWith("ROOT: info-log-to-file"), () -> "Unexpected log: " + levelLog);
    }

    /**
     * Checks file appender with LevelFilter and ThresholdFilter
     * One appender, two filters
     */
    @Test
    public void fileAppenderLevelFilterAndThresholdFilter() {

        String filterLog = logTester.run(
                "classpath:io/bootique/logback/test-file-appender-filter-level-threshold.yml",
                "filter.log", l -> {
                    l.debug("debug-log-to-file");
                    l.info("info-log-to-file");
                    l.warn("warn-log-to-file");
                });

        assertTrue(filterLog.endsWith("ROOT: warn-log-to-file"), () -> "Unexpected log: " + filterLog);
    }

    /**
     * Checks file appenders with LevelFilter and ThresholdFilter
     * Each appender with one filter
     */
    @Test
    public void fileAppendersLevelFilterAndThresholdFilter() {

        Map<String, String> logs = logTester.run(
                "classpath:io/bootique/logback/test-file-appenders-filter-level-threshold.yml",
                l -> {
                    l.debug("debug-log-to-file");
                    l.info("info-log-to-file");
                    l.warn("warn-log-to-file");
                });

        String thresholdLog = logs.get("threshold.log");
        assertTrue(thresholdLog.endsWith("ROOT: warn-log-to-file"), () -> "Unexpected log: " + thresholdLog);

        String levelLog = logs.get("level.log");
        assertTrue(levelLog.endsWith("ROOT: info-log-to-file"), () -> "Unexpected log: " + levelLog);
    }


    @Test
    public void fileLoggerLevelOff() {

        Map<String, String> logs = logTester.run(
                "classpath:io/bootique/logback/test-logger-level-off.yml",
                l -> {
                    LoggerFactory.getLogger("one").debug("debug-log-to-file-1");
                    LoggerFactory.getLogger("one").info("info-log-to-file-1");
                    LoggerFactory.getLogger("one").warn("warn-log-to-file-1");

                    LoggerFactory.getLogger("two").debug("debug-log-to-file-2");
                    LoggerFactory.getLogger("two").info("info-log-to-file-2");
                    LoggerFactory.getLogger("two").warn("warn-log-to-file-2");
                });

        String thresholdLog = logs.get("logfile1-off.log");
        assertEquals("two: warn-log-to-file-2", thresholdLog, () -> "Unexpected log: " + thresholdLog);
    }
}
