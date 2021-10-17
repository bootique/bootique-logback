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

import java.util.Map;

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
    public void testFileAppenderThresholdFilter() {

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
    public void testFileAppenderLevelFilter() {

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
    public void testFileAppenderLevelFilterAndThresholdFilter() {

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
    public void testFileAppendersLevelFilterAndThresholdFilter() {

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
}
