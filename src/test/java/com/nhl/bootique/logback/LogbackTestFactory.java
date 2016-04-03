package com.nhl.bootique.logback;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.nhl.bootique.BQRuntime;
import com.nhl.bootique.test.junit.BQTestFactory;

import ch.qos.logback.classic.Logger;

public class LogbackTestFactory extends BQTestFactory {

	public Logger newRootLogger(String config) {
		String arg0 = "--config=" + Objects.requireNonNull(config);
		BQRuntime runtime = newRuntime().configurator(bootique -> bootique.module(LogbackModule.class)).build(arg0);
		return runtime.getInstance(Logger.class);
	}
	
	public void stop() {
		after();
	}

	public File emptyLogFile(String name) {
		File logFile = new File(name);
		logFile.delete();
		assertFalse(logFile.exists());
		return logFile;
	}

	public void prepareLogDir(String dir) {
		File parent = new File(dir);
		if (parent.exists()) {
			assertTrue(parent.isDirectory());
			asList(parent.list()).stream().forEach(name -> {
				File file = new File(parent, name);
				file.delete();
				assertFalse(file.exists());
			});
		} else {
			parent.mkdirs();
			assertTrue(parent.isDirectory());
		}
	}

	public Map<String, String[]> loglines(String dir, String expectedLogFilePrefix) {

		File parent = new File(dir);
		assertTrue(parent.isDirectory());

		Map<String, String[]> linesByFile = new HashMap<>();

		asList(parent.list()).stream().filter(name -> name.startsWith(expectedLogFilePrefix)).forEach(name -> {

			Path p = Paths.get(parent.getAbsolutePath(), name);
			try {
				linesByFile.put(name, Files.lines(p).toArray(String[]::new));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});

		return linesByFile;
	}

}
