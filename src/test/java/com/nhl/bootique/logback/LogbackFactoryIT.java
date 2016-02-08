package com.nhl.bootique.logback;

import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.function.Function;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import com.nhl.bootique.BQCoreModule;
import com.nhl.bootique.config.ConfigurationSource;
import com.nhl.bootique.log.BootLogger;

import ch.qos.logback.classic.Logger;

public class LogbackFactoryIT {

	private Module createBootiqueModule(String yaml) {

		ConfigurationSource configSource = mock(ConfigurationSource.class);
		doAnswer(new Answer<Object>() {

			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				@SuppressWarnings("unchecked")
				Function<InputStream, Object> processor = invocation.getArgumentAt(0, Function.class);

				try (InputStream in = getClass().getResourceAsStream(yaml)) {
					return processor.apply(in);
				}
			}
		}).when(configSource).readConfig(any());

		return Modules.override(BQCoreModule.builder().bootLogger(mock(BootLogger.class)).args(new String[0]).build())
				.with((b) -> b.bind(ConfigurationSource.class).toInstance(configSource));
	}

	private Logger getRootLogger(String yaml) {
		LogbackModule module = new LogbackModule();

		Injector i = Guice.createInjector(module, createBootiqueModule(yaml));
		return i.getInstance(Logger.class);
	}

	@Test
	public void testFileAppender() throws IOException {
		File outFile = new File("target/logfile1.log");
		outFile.delete();
		assertFalse(outFile.exists());

		Logger logger = getRootLogger("test-file-appender.yml");
		logger.info("info-log-to-file");

		assertTrue(outFile.isFile());

		String logfileContents = Files.lines(outFile.toPath()).collect(joining("\n"));
		assertTrue(logfileContents.endsWith("ROOT: info-log-to-file"));
	}
}
