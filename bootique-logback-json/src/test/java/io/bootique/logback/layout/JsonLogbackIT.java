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

import io.bootique.junit5.BQTest;
import io.bootique.junit5.BQTestFactory;
import io.bootique.junit5.BQTestTool;
import io.bootique.logback.unit.LogTester;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@BQTest
public class JsonLogbackIT {

    @BQTestTool
    final BQTestFactory testFactory = new BQTestFactory().autoLoadModules();

    @BQTestTool
    final LogTester logTester = new LogTester(testFactory, "target/logs");

    @Test
    public void testFileAppenderJson() {
        String logfile = logTester.run(
                "classpath:io/bootique/logback/test-file-appender-json-layout.yml",
                "logfile_layout1.log",
                l -> l.info("info-log-json")
        );

        assertTrue(logfile.contains("\"message\":\"info-log-json\""), () -> "Unexpected logs: " + logfile);
    }

    @Test
    public void testFileAppenderJsonWithTimestamp() {

        String logfile = logTester.run(
                "classpath:io/bootique/logback/test-file-appender-json-layout-with-timestamp.yml",
                "logfile_layout1.log",
                l -> l.info("info-log-json")
        );

        assertTrue(logfile.contains("\"message\" : \"info-log-json\""), () -> "Unexpected logs: " + logfile);
    }
}
