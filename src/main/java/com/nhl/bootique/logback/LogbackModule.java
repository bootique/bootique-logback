package com.nhl.bootique.logback;

import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.nhl.bootique.FactoryModule;
import com.nhl.bootique.factory.FactoryConfigurationService;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

public class LogbackModule extends FactoryModule<LogbackFactory> {

	public LogbackModule(String configPrefix) {
		super(LogbackFactory.class, configPrefix);
	}

	public LogbackModule() {
		super(LogbackFactory.class);
	}

	@Override
	protected String defaultConfigPrefix() {
		return "log";
	}

	@Override
	public void configure(Binder binder) {
		// Binding a dummy class to trigger eager init of Logback as
		// @Provides below can not be invoked eagerly..
		binder.bind(LogInitTrigger.class).asEagerSingleton();
	}

	@Provides
	public Logger configLogbackRootLogger(FactoryConfigurationService factoryConfig) {
		LoggerContext context = createLogbackContext();
		return factoryConfig.factory(LogbackFactory.class, configPrefix).createRootLogger(context);
	}

	// copied from Dropwizard. See DW DefaultLoggingFactory and
	// http://jira.qos.ch/browse/SLF4J-167. Though presumably Bootique calls
	// this from the main thread, so we should not be affected by the issue.
	protected LoggerContext createLogbackContext() {
		long startTime = System.nanoTime();
		while (true) {
			ILoggerFactory iLoggerFactory = LoggerFactory.getILoggerFactory();

			if (iLoggerFactory instanceof LoggerContext) {
				return (LoggerContext) iLoggerFactory;
			}

			if ((System.nanoTime() - startTime) > 10_000_000) {
				throw new IllegalStateException("Unable to acquire the logger context");
			}

			try {
				Thread.sleep(100_000);
			} catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	static class LogInitTrigger {

		@Inject
		public LogInitTrigger(Logger rootLogger) {
			rootLogger.debug("Logback started");
		}
	}

}
