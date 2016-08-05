package com.nhl.bootique.logback;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;

import ch.qos.logback.classic.Logger;

public class LogbackBQConfigIT {

	final String LOGFILE_PREFIX = "logfile-";
	final String CURRENT_LOGFILE_NAME = LOGFILE_PREFIX + "current.log";
	final String HELLO_WORLD_VALUE = "Hello World!";
	final String CONTENT_VALUE_FORMAT = "%s." + HELLO_WORLD_VALUE;

	@Rule
	public LogbackTestFactory LOGGER_STACK = new LogbackTestFactory();

	@Test
	public void testFileAppender() {

		LOGGER_STACK.prepareLogDir("target/logs/rotate");
		Logger logger = LOGGER_STACK.newRootLogger("classpath:com/nhl/bootique/logback/test-file-appender.yml");
		logger.info("info-log-to-file");

		// must stop to ensure logs are flushed...
		LOGGER_STACK.stop();

		Map<String, String[]> logfileContents = LOGGER_STACK.loglines("target/logs/rotate", "logfile1.log");

		assertEquals(1, logfileContents.size());
		String[] lines = logfileContents.get("logfile1.log");
		String oneLine = asList(lines).stream().collect(joining("\n"));

		assertTrue("Unexpected logs: " + oneLine, oneLine.endsWith("ROOT: info-log-to-file"));
	}

	/**
	 * Checks file appender with rolling policy "time" (TimeBasedRollingPolicy)
	 *
	 * This test makes 3 attempts of printing one log row each second.
	 * Logback configuration defines rollover by seconds with help of file name pattern and
	 * no any restrictions of total history size.
	 *
	 * Expected following results:
	 * 	- 2 archived files + 1 current log-file;
	 * 	- 3 total rows in all log file
	 *
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@Test
	public void testFileAppender_Rotate_By_Time() throws InterruptedException, IOException {

		Map<String, String[]> logfileContents = rotate("target/logs/rotate-by-time",
				"classpath:com/nhl/bootique/logback/test-file-appender-time-rotation.yml", 3, 1000, 1);

		// Checks file numbers: Expected 2 archived files + 1 current log-file
		assertEquals(3, logfileContents.size());

		// Checks current file exists
		assertTrue(logfileContents.containsKey(CURRENT_LOGFILE_NAME));

		// Checks file names: each file must start with "logfile-"
		logfileContents.keySet().forEach(key -> assertTrue(key.startsWith(LOGFILE_PREFIX)));

		// Check total rows number in all log files
		assertEquals(3, logfileContents.values().stream().flatMap(array -> asList(array).stream())
				.filter(s -> s.contains(HELLO_WORLD_VALUE)).count());
	}

	/**
	 * Checks file appender with rolling policy "time" (TimeBasedRollingPolicy)
	 *
	 * This test makes 4 attempts of printing log row each second.
	 * Logback configuration defines rollover by seconds with help of file name pattern and
	 * 3 seconds of total history.
	 *
	 * As result, 2 archived files + 1 current log-file are expected; 4 rows are expected in all log files
	 *
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@Test
	public void testFileAppender_Rotate_By_Time_And_History() throws InterruptedException, IOException {


		Map<String, String[]> logfileContents = rotate("target/logs/rotate-by-time-and-history",
				"classpath:com/nhl/bootique/logback/test-file-appender-time-and-history-rotation.yml", 4, 1000, 1);

		// Checks file numbers: Expected 2 archived files + 1 current log-file
		assertEquals(3, logfileContents.size());

		// Checks current file exists
		assertTrue(logfileContents.containsKey(CURRENT_LOGFILE_NAME));

		// Checks file names: each file must start with "logfile-"
		logfileContents.keySet().forEach(key -> assertTrue(key.startsWith(LOGFILE_PREFIX)));

		// Check total rows number in all log files
		assertEquals(3, logfileContents.values().stream().flatMap(array -> asList(array).stream())
				.filter(s -> s.contains(HELLO_WORLD_VALUE)).count());
	}

	/**
	 * Checks file appender with rolling policy "time" (TimeBasedRollingPolicy)
	 *
	 * This test makes 5 attempts of printing log row each second.
	 * Logback configuration defines rollover by seconds with help of file name pattern;
	 * 5 seconds of total history and 50 bytes of total files size
	 *
	 * As result, 3 archived files (63 bytes) + 1 current log-file are expected; 4 rows are expected in all log files
	 *
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@Test
	public void testFileAppender_Rotate_By_Time_And_History_And_TotalSize() throws InterruptedException, IOException {


		Map<String, String[]> logfileContents = rotate("target/logs/rotate-by-time-and-history-and-totalsize",
				"classpath:com/nhl/bootique/logback/test-file-appender-time-and-history-and-totalsize-rotation.yml", 5, 1000, 1);

		// Checks file numbers: Expected 3 archived files + 1 current log-file
		assertEquals(4, logfileContents.size());

		// Checks current file exists
		assertTrue(logfileContents.containsKey(CURRENT_LOGFILE_NAME));

		// Checks file names: each file must start with "logfile-"
		logfileContents.keySet().forEach(key -> assertTrue(key.startsWith(LOGFILE_PREFIX)));

		// Check total rows number in all log files
		assertEquals(4, logfileContents.values().stream().flatMap(array -> asList(array).stream())
				.filter(s -> s.contains(HELLO_WORLD_VALUE)).count());
	}

	/**
	 * Checks file appender with rolling policy "size" (SizeAndTimeBasedRollingPolicy)
	 *
	 * This test makes 3 attempts of printing 2 log rows each second.
	 * Logback configuration defines rollover by seconds with help of file name pattern and
	 * size of each file is 40 bytes.
	 *
	 * Expected following results:
	 * 	- 2 archived files + 1 current log-file;
	 * 	- More than 1 row in each log-file
	 * 	- 20 total rows in all log file
	 *
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@Test
	public void testFileAppender_Rotate_By_Size() throws InterruptedException, IOException {

		Map<String, String[]> logfileContents = rotate("target/logs/rotate-by-size",
				"classpath:com/nhl/bootique/logback/test-file-appender-size-rotation.yml", 3, 1000, 2);

		// Checks file numbers: Expected 2 archived files + 1 current log-file
		assertEquals(3, logfileContents.size());

		// Checks current file exists
		assertTrue(logfileContents.containsKey(CURRENT_LOGFILE_NAME));

		// Checks file names: each file must start with "logfile-"
		logfileContents.keySet().forEach(key -> assertTrue(key.startsWith(LOGFILE_PREFIX)));

		// Check total rows number in all log files
		assertEquals(6, logfileContents.values().stream().flatMap(array -> asList(array).stream())
				.filter(s -> s.contains(HELLO_WORLD_VALUE)).count());
	}

	/**
	 * Checks file appender with rolling policy "size" (SizeAndTimeBasedRollingPolicy)
	 *
	 * This test makes 4 attempts of printing 2 log rows each second.
	 * Logback configuration defines rollover by seconds with help of file name pattern;
	 * 5 seconds of total history and size of each file is 40 bytes.
	 *
	 * As result, 2 archived files + 1 current log-file are expected; 6 rows are expected in all log files
	 *
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@Test
	public void testFileAppender_Rotate_By_Size_And_History() throws InterruptedException, IOException {


		Map<String, String[]> logfileContents = rotate("target/logs/rotate-by-size-and-history",
				"classpath:com/nhl/bootique/logback/test-file-appender-size-and-history-rotation.yml", 4, 1000, 2);

		// Checks file numbers: Expected 2 archived files + 1 current log-file
		assertEquals(3, logfileContents.size());

		// Checks current file exists
		assertTrue(logfileContents.containsKey(CURRENT_LOGFILE_NAME));

		// Checks file names: each file must start with "logfile-"
		logfileContents.keySet().forEach(key -> assertTrue(key.startsWith(LOGFILE_PREFIX)));

		// Check total rows number in all log files
		assertEquals(6, logfileContents.values().stream().flatMap(array -> asList(array).stream())
				.filter(s -> s.contains(HELLO_WORLD_VALUE)).count());
	}

	/**
	 * Checks file appender with rolling policy "size" (SizeAndTimeBasedRollingPolicy)
	 *
	 * This test makes 5 attempts of printing 2 log rows each second.
	 * Logback configuration defines rollover by seconds with help of file name pattern;
	 * 5 seconds of total history; 50 bytes in each file; 150 bytes of total files size
	 *
	 * As result, 3 archived files (135 bytes) + 1 current log-file are expected; 8 rows are expected in all log files
	 *
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@Test
	public void testFileAppender_Rotate_By_Size_And_History_And_TotalSize() throws InterruptedException, IOException {


		Map<String, String[]> logfileContents = rotate("target/logs/rotate-by-size-and-history-and-totalsize",
				"classpath:com/nhl/bootique/logback/test-file-appender-size-and-history-and-totalsize-rotation.yml", 5, 1000, 2);

		// Checks file numbers: Expected 3 archived files + 1 current log-file
		assertEquals(4, logfileContents.size());

		// Checks current file exists
		assertTrue(logfileContents.containsKey(CURRENT_LOGFILE_NAME));

		// Checks file names: each file must start with "logfile-"
		logfileContents.keySet().forEach(key -> assertTrue(key.startsWith(LOGFILE_PREFIX)));

		// Check total rows number in all log files
		assertEquals(8, logfileContents.values().stream().flatMap(array -> asList(array).stream())
				.filter(s -> s.contains(HELLO_WORLD_VALUE)).count());
	}

	/**
	 * Checks file appender with rolling policy "fixed" (FixedWindowRollingPolicy)
	 *
	 * This test makes 4 attempts of printing 1 log row each second.
	 * Logback configuration defines rollover by index with help of file name pattern;
	 * 2 files of total history; 20 bytes in each file;
	 *
	 * As result, 2 archived files + 1 current log-file are expected;
	 *
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@Test
	public void testFileAppender_Rotate_Fixed() throws InterruptedException, IOException {


		Map<String, String[]> logfileContents = rotate("target/logs/rotate-fixed",
				"classpath:com/nhl/bootique/logback/test-file-appender-fixed-rotation.yml", 4, 800, 1);

		// Check file numbers: Expected 3 files = 1 current file + 2 archived files
		assertEquals(3, logfileContents.size());

		// Check file names and file content
		final String EXPECTED_VALUE_FORMAT = "ROOT: " + CONTENT_VALUE_FORMAT;
		logfileContents.forEach((key, value) -> {
			String expectedValue = null;
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
					fail("Detected unexpected file name \"" + key + "\"");
				}
			}
			// Check file content
			assertEquals("Check expected file content \"" + expectedValue + "\" in the current file \"" + key + "\"",
					expectedValue, value[0]);
		});
	}

	private Map<String, String[]> rotate(String logDir, String configFile, int attempts, int period, int rowsCount)
			throws InterruptedException, IOException {
		LOGGER_STACK.prepareLogDir(logDir);

		Logger logger = LOGGER_STACK.newRootLogger(configFile);

		printLog(logger, attempts, period, rowsCount);

		// must stop to ensure logs are flushed...
		LOGGER_STACK.stop();
		return LOGGER_STACK.loglines(logDir, LOGFILE_PREFIX);
	}

	private void printLog(Logger logger, int attempts, int period, int rowsCount) throws InterruptedException, IOException {
		int index = 1;
		for (int i = 1; i <= attempts; i++) {
			for (int j = 0; j < rowsCount; j++) {
				logger.info(String.format(CONTENT_VALUE_FORMAT, String.valueOf(index)));
				index++;
			}
			Thread.sleep(period+1);
		}
	}
}
