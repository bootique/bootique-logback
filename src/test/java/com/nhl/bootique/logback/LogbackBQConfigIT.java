package com.nhl.bootique.logback;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;

import ch.qos.logback.classic.Logger;

public class LogbackBQConfigIT {

	@Rule
	public LogbackTestFactory LOGGER_STACK = new LogbackTestFactory();

	@Test
	public void testFileAppender() throws IOException {

		File logFile = LOGGER_STACK.emptyLogFile("target/logfile1.log");
		Logger logger = LOGGER_STACK.newRootLogger("classpath:com/nhl/bootique/logback/test-file-appender.yml");

		logger.info("info-log-to-file");
		assertTrue(logFile.isFile());

		String logfileContents = Files.lines(logFile.toPath()).collect(joining("\n"));
		assertTrue(logfileContents.endsWith("ROOT: info-log-to-file"));
	}

	@Test
	public void testFileAppender_Rotate() throws InterruptedException, IOException {

		LOGGER_STACK.prepareLogDir("target/rotate");

		Logger logger = LOGGER_STACK
				.newRootLogger("classpath:com/nhl/bootique/logback/test-file-appender-10sec-rotation.yml");
		logger.info("info-log-to-file1");
		logger.info("info-log-to-file2");

		// file rotation happens every second... so wait at least that long
		Thread.sleep(1001);

		logger.info("info-log-to-file3");
		logger.info("info-log-to-file4");

		// must stop to ensure logs are flushed...
		LOGGER_STACK.stop();

		Map<String, String[]> logfileContents = LOGGER_STACK.loglines("target/rotate", "logfile-");

		assertTrue(logfileContents.size() > 1);
		logfileContents.forEach((f, lines) -> assertTrue(lines.length > 0));
		assertEquals(4, logfileContents.values().stream().flatMap(array -> asList(array).stream())
				.filter(s -> s.contains("info-log-to-file")).count());
	}
}
