package com.nhl.bootique.logback;

import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Rule;
import org.junit.Test;

import ch.qos.logback.classic.Logger;

public class LogbackBQConfigIT {

	@Rule
	public LogbackTestFactory LOGGER_STACK = new LogbackTestFactory();

	@Test
	public void testFileAppender() throws IOException {

		File logFile = new File("target/logfile1.log");
		logFile.delete();
		assertFalse(logFile.exists());

		Logger logger = LOGGER_STACK.newRootLogger("classpath:com/nhl/bootique/logback/test-file-appender.yml");
		logger.info("info-log-to-file");

		assertTrue(logFile.isFile());

		String logfileContents = Files.lines(logFile.toPath()).collect(joining("\n"));
		assertTrue(logfileContents.endsWith("ROOT: info-log-to-file"));
	}
}
