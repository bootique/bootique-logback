package com.nhl.bootique.logback;

import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.nhl.bootique.factory.FactoryConfigurationService;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

public class LogbackBundle {

	private static final String CONFIG_PREFIX = "log";

	private String configPrefix;

	public static LogbackBundle create() {
		return create(CONFIG_PREFIX);
	}

	public static LogbackBundle create(String configPrefix) {
		return new LogbackBundle(configPrefix);
	}
	
	public static Module logbackModule() {
		return create().module();
	}

	private LogbackBundle(String configPrefix) {
		this.configPrefix = configPrefix;
	}

	public Module module() {
		return new LogbackModule();
	}

	class LogbackModule implements Module {

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
	}

	static class LogInitTrigger {

		@Inject
		public LogInitTrigger(Logger rootLogger) {
			rootLogger.debug("Logback started");
		}
	}

}
