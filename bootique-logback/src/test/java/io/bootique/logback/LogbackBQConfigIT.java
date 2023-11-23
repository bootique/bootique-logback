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
import io.bootique.junit5.BQTest;
import io.bootique.junit5.BQTestFactory;
import io.bootique.junit5.BQTestTool;
import io.bootique.logback.unit.LogTester;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

@BQTest
public class LogbackBQConfigIT {

    private final static String LOGFILE_PREFIX = "logfile-";
    private final static String CURRENT_LOGFILE_NAME = LOGFILE_PREFIX + "current.log";
    private final static String HELLO_WORLD_VALUE = "Hello World!";
    private final static String CONTENT_VALUE_FORMAT = "%s." + HELLO_WORLD_VALUE;

    @BQTestTool
    final BQTestFactory testFactory = new BQTestFactory().autoLoadModules();

    @BQTestTool
    final LogTester logTester = new LogTester(testFactory, "target/logs");

    @Test
    public void fileAppender() {

        String logfile1 = logTester.run(
                "classpath:io/bootique/logback/test-file-appender.yml",
                "logfile1.log",
                l -> l.info("info-log-to-file")
        );

        assertTrue(logfile1.endsWith("ROOT: info-log-to-file"), () -> "Unexpected logs: " + logfile1);
    }

    @Test
    public void fileAppender_NoAppend() {

        String logfile1 = logTester.run(
                "classpath:io/bootique/logback/test-file-appender-with-flag.yml",
                "logfile1.log",
                l -> l.info("run1")
        );

        assertTrue(logfile1.endsWith("ROOT: run1"), () -> "Unexpected log: " + logfile1);


        String logfile2 = logTester.run(
                "classpath:io/bootique/logback/test-file-appender-with-flag.yml",
                "logfile1.log",
                l -> l.info("run2")
        );

        assertTrue(logfile2.endsWith("ROOT: run2"), () -> "Unexpected log: " + logfile2);
        assertFalse(logfile2.contains("run1"));
    }

    @Test
    public void fileAppenderHtml() {

        String logfile = logTester.run(
                "classpath:io/bootique/logback/test-file-appender-html-layout.yml",
                "logfile_layout12.html",
                l -> l.info("info-log-html")
        );

        assertTrue(logfile.contains("\"Message\">info-log-html<"), () -> "Unexpected logs: " + logfile);
    }

    @Test
    public void fileAppenderXml() {

        String logfile = logTester.run(
                "classpath:io/bootique/logback/test-file-appender-xml-layout.yml",
                "logfile_layout13.xml",
                l -> l.info("info-log-xml")
        );

        assertTrue(logfile.contains("message>info-log-xml<"), () -> "Unexpected logs: " + logfile);
    }

    /**
     * Checks multi appender for child Loggers.
     * Should log to different files.
     * <p>
     * It has three different cases:
     * 1 - If appender has no name it will be added to root logger and it will write logs from all sources.
     * 2 - If child appender has name, child has link to this appender and root has not, this appender will be added to this logger.
     * 3 - If appender has name and root logger has reference to this name it will be added to root logger and it will be newer added to child,
     * even if it will have reference to the same appender.
     */
    @Test
    public void fileMultiAppender_Root() {
        Map<String, String> logs = logTester.run(
                "classpath:io/bootique/logback/test-multi-file-appender.yml",
                l -> l.info("info-log-to-file")
        );

        assertEquals(3, logs.size());
        assertEquals(new HashSet<>(asList("multi-one.log", "multi-two.log", "multi-noname.log")), logs.keySet());

        assertTrue(logs.get("multi-one.log").endsWith("ROOT: info-log-to-file"), () -> "Unexpected log: " + logs.get("multi-one.log"));
        assertTrue(logs.get("multi-noname.log").endsWith("ROOT: info-log-to-file"), () -> "Unexpected log: " + logs.get("multi-noname.log"));
        assertEquals("", logs.get("multi-two.log"));
    }

    @Test
    public void fileMultiAppender_Child() {

        Map<String, String> logs = logTester.run(
                "classpath:io/bootique/logback/test-multi-file-appender.yml",
                l -> LoggerFactory.getLogger("one").info("info-log-to-file")
        );

        assertEquals(3, logs.size());
        assertEquals(new HashSet<>(asList("multi-one.log", "multi-two.log", "multi-noname.log")), logs.keySet());

        assertTrue(logs.get("multi-one.log").endsWith("one: info-log-to-file"), () -> "Unexpected log: " + logs.get("multi-one.log"));
        assertTrue(logs.get("multi-two.log").endsWith("one: info-log-to-file"), () -> "Unexpected log: " + logs.get("multi-two.log"));
        assertTrue(logs.get("multi-noname.log").endsWith("one: info-log-to-file"), () -> "Unexpected log: " + logs.get("multi-noname.log"));
    }

    /**
     * Checks file appender with rolling policy "time" (TimeBasedRollingPolicy)
     * <p>
     * This test makes 3 attempts of printing one log row each second.
     * Logback configuration defines rollover by seconds with help of file name pattern and
     * no any restrictions of total history size.
     * <p>
     * Expected following results:
     * - 2 archived files + 1 current log-file;
     * - 3 total rows in all log file
     */
    @Test
    public void fileAppender_Rotate_By_Time() {

        Map<String, String> logs = logTester.run(
                "classpath:io/bootique/logback/test-file-appender-time-rotation.yml",
                l -> printLog(l, 3, 1000, 1)
        );

        assertEquals(3, logs.size(), "Expected 2 archived files + 1 current log-file");
        assertTrue(logs.containsKey(CURRENT_LOGFILE_NAME));
        logs.keySet().forEach(key -> assertTrue(key.startsWith(LOGFILE_PREFIX), "Each file must start with 'logfile-'"));
        assertEquals(3, logs.values().stream().map(s -> countMatches(s, HELLO_WORLD_VALUE)).reduce(0, (i1, i2) -> i1 + i2));
    }

    /**
     * Checks file appender with rolling policy "time" (TimeBasedRollingPolicy)
     * <p>
     * This test makes 4 attempts of printing log row each second.
     * Logback configuration defines rollover by seconds with help of file name pattern and
     * 3 seconds of total history.
     * <p>
     * As result, 2 archived files + 1 current log-file are expected; 4 rows are expected in all log files
     */
    @Test
    public void fileAppender_Rotate_By_Time_And_History() {

        Map<String, String> logs = logTester.run(
                "classpath:io/bootique/logback/test-file-appender-time-and-history-rotation.yml",
                l -> printLog(l, 4, 1000, 1)
        );

        assertEquals(3, logs.size(), "Expected 2 archived files + 1 current log-file");
        assertTrue(logs.containsKey(CURRENT_LOGFILE_NAME));
        logs.keySet().forEach(key -> assertTrue(key.startsWith(LOGFILE_PREFIX), "each file must start with 'logfile-'"));
        assertEquals(3, logs.values().stream().map(s -> countMatches(s, HELLO_WORLD_VALUE)).reduce(0, (i1, i2) -> i1 + i2));
    }

    /**
     * Checks file appender with rolling policy "time" (TimeBasedRollingPolicy)
     * <p>
     * This test makes 5 attempts of printing log row each second.
     * Logback configuration defines rollover by seconds with help of file name pattern;
     * 5 seconds of total history and 50 bytes of total files size
     * <p>
     * As result, 3 archived files (63 bytes) + 1 current log-file are expected; 4 rows are expected in all log files
     */
    @Test
    public void fileAppender_Rotate_By_Time_And_History_And_TotalSize() {

        Map<String, String> logs = logTester.run(
                "classpath:io/bootique/logback/test-file-appender-time-and-history-and-totalsize-rotation.yml",
                l -> printLog(l, 5, 1000, 1)
        );

        assertEquals(4, logs.size(), "Expected 3 archived files + 1 current log-file");
        assertTrue(logs.containsKey(CURRENT_LOGFILE_NAME));
        logs.keySet().forEach(key -> assertTrue(key.startsWith(LOGFILE_PREFIX), "each file must start with 'logfile-'"));
        assertEquals(4, logs.values().stream().map(s -> countMatches(s, HELLO_WORLD_VALUE)).reduce(0, (i1, i2) -> i1 + i2));
    }

    /**
     * Checks file appender with rolling policy "size" (SizeAndTimeBasedRollingPolicy)
     * <p>
     * This test makes 3 attempts of printing 2 log rows each second.
     * Logback configuration defines rollover by seconds with help of file name pattern and
     * size of each file is 40 bytes.
     * <p>
     * Expected following results:
     * - 2 archived files + 1 current log-file;
     * - More than 1 row in each log-file
     * - 20 total rows in all log file
     */
    @Test
    public void fileAppender_Rotate_By_Size() {

        Map<String, String> logs = logTester.run(
                "classpath:io/bootique/logback/test-file-appender-size-rotation.yml",
                l -> printLog(l, 3, 1000, 2)
        );

        assertEquals(3, logs.size(), "Expected 2 archived files + 1 current log-file");
        assertTrue(logs.containsKey(CURRENT_LOGFILE_NAME));
        logs.keySet().forEach(key -> assertTrue(key.startsWith(LOGFILE_PREFIX), "each file must start with 'logfile-'"));
        assertEquals(6, logs.values().stream().map(s -> countMatches(s, HELLO_WORLD_VALUE)).reduce(0, (i1, i2) -> i1 + i2));
    }

    /**
     * Checks file appender with rolling policy "size" (SizeAndTimeBasedRollingPolicy)
     * <p>
     * This test makes 4 attempts of printing 2 log rows each second.
     * Logback configuration defines rollover by seconds with help of file name pattern;
     * 5 seconds of total history and size of each file is 40 bytes.
     * <p>
     * As result, 2 archived files + 1 current log-file are expected; 6 rows are expected in all log files
     */
    @Test
    public void fileAppender_Rotate_By_Size_And_History() {

        Map<String, String> logs = logTester.run(
                "classpath:io/bootique/logback/test-file-appender-size-and-history-rotation.yml",
                l -> printLog(l, 4, 1000, 2)
        );

        assertEquals(3, logs.size(), "Expected 2 archived files + 1 current log-file");
        assertTrue(logs.containsKey(CURRENT_LOGFILE_NAME));
        logs.keySet().forEach(key -> assertTrue(key.startsWith(LOGFILE_PREFIX), "each file must start with 'logfile-'"));
        assertEquals(6, logs.values().stream().map(s -> countMatches(s, HELLO_WORLD_VALUE)).reduce(0, (i1, i2) -> i1 + i2));
    }

    /**
     * Checks file appender with rolling policy "size" (SizeAndTimeBasedRollingPolicy)
     * <p>
     * This test makes 5 attempts of printing 2 log rows each second.
     * Logback configuration defines rollover by seconds with help of file name pattern;
     * 5 seconds of total history; 50 bytes in each file; 150 bytes of total files size
     * <p>
     * As result, 3 archived files (135 bytes) + 1 current log-file are expected; 8 rows are expected in all log files
     */
    @Test
    public void fileAppender_Rotate_By_Size_And_History_And_TotalSize() {

        Map<String, String> logs = logTester.run(
                "classpath:io/bootique/logback/test-file-appender-size-and-history-and-totalsize-rotation.yml",
                l -> printLog(l, 5, 1000, 2)
        );

        assertEquals(4, logs.size(), "Expected 3 archived files + 1 current log-file");
        assertTrue(logs.containsKey(CURRENT_LOGFILE_NAME));
        logs.keySet().forEach(key -> assertTrue(key.startsWith(LOGFILE_PREFIX), "each file must start with 'logfile-'"));
        assertEquals(8, logs.values().stream().map(s -> countMatches(s, HELLO_WORLD_VALUE)).reduce(0, (i1, i2) -> i1 + i2));
    }

    /**
     * Checks file appender with rolling policy "fixed" (FixedWindowRollingPolicy)
     * <p>
     * This test makes 4 attempts of printing 1 log row each second.
     * Logback configuration defines rollover by index with help of file name pattern;
     * 2 files of total history; 20 bytes in each file;
     * <p>
     * As result, 2 archived files + 1 current log-file are expected;
     */
    @Test
    public void fileAppender_Rotate_Fixed() {

        Map<String, String> logs = logTester.run(
                "classpath:io/bootique/logback/test-file-appender-fixed-rotation.yml",
                l -> printLog(l, 4, 800, 1)
        );

        assertEquals(3, logs.size(), "Expected 2 archived files + 1 current log-file");
        assertTrue(logs.containsKey(CURRENT_LOGFILE_NAME));
        logs.keySet().forEach(key -> assertTrue(key.startsWith(LOGFILE_PREFIX), "each file must start with 'logfile-'"));

        // Check file names and file content
        final String EXPECTED_VALUE_FORMAT = "ROOT: " + CONTENT_VALUE_FORMAT;
        logs.forEach((key, value) -> {
            String expectedValue;
            switch (key) {
                case "logfile-current.log": {
                    expectedValue = String.format(EXPECTED_VALUE_FORMAT, "4");
                    break;
                }
                case "logfile-1.log": {
                    expectedValue = String.format(EXPECTED_VALUE_FORMAT, "3");
                    break;
                }
                case "logfile-2.log": {
                    expectedValue = String.format(EXPECTED_VALUE_FORMAT, "2");
                    break;
                }
                default: {
                    throw new RuntimeException("Detected unexpected file name \"" + key + "\"");
                }
            }

            assertTrue(value.startsWith(expectedValue),
                    () -> "Check expected file content '" + expectedValue + "' in the current file '" + key + "'");
        });
    }

    private void printLog(Logger logger, int attempts, int period, int rowsCount) {
        int index = 1;
        for (int i = 1; i <= attempts; i++) {
            for (int j = 0; j < rowsCount; j++) {
                logger.warn(String.format(CONTENT_VALUE_FORMAT, index));
                index++;
            }

            try {
                Thread.sleep(period + 1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private int countMatches(String string, String substring) {

        int lastIndex = 0;
        int count = 0;

        while (lastIndex != -1) {

            lastIndex = string.indexOf(substring, lastIndex);

            if (lastIndex != -1) {
                count++;
                lastIndex += substring.length();
            }
        }

        return count;
    }
}
