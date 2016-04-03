package com.nhl.bootique.logback;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.slf4j.ILoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.nhl.bootique.logback.appender.AppenderFactory;
import com.nhl.bootique.logback.appender.ConsoleAppenderFactory;
import com.nhl.bootique.shutdown.ShutdownManager;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.jul.LevelChangePropagator;

public class LogbackContextFactory {

	private Level level;
	private Map<String, LoggerFactory> loggers;
	private Collection<AppenderFactory> appenders;

	public LogbackContextFactory() {
		this.level = Level.INFO;
		this.loggers = Collections.emptyMap();
		this.appenders = Collections.emptyList();
	}

	public Logger createRootLogger(ShutdownManager shutdownManager) {

		LoggerContext context = createLogbackContext();
		shutdownManager.addShutdownHook(() -> {
			context.stop();
		});

		rerouteJUL();

		Logger root = context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		context.reset();

		final LevelChangePropagator propagator = new LevelChangePropagator();
		propagator.setContext(context);
		propagator.setResetJUL(true);

		context.addListener(propagator);

		root.setLevel(level);

		loggers.forEach((name, lf) -> lf.configLogger(name, context));

		if (appenders.isEmpty()) {
			setAppenders(Collections.singletonList(new ConsoleAppenderFactory()));
		}

		appenders.forEach(a -> root.addAppender(a.createAppender(context)));

		return root;
	}

	// inspired by Dropwizard. See DW DefaultLoggingFactory and
	// http://jira.qos.ch/browse/SLF4J-167. Though presumably Bootique calls
	// this from the main thread, so we should not be affected by the issue.
	protected LoggerContext createLogbackContext() {
		long startTime = System.nanoTime();
		while (true) {
			ILoggerFactory iLoggerFactory = org.slf4j.LoggerFactory.getILoggerFactory();

			if (iLoggerFactory instanceof LoggerContext) {
				return (LoggerContext) iLoggerFactory;
			}

			if ((System.nanoTime() - startTime) > 10_000_000) {
				throw new IllegalStateException("Unable to acquire the logger context");
			}

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	void rerouteJUL() {
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
	}

	public void setLevel(String level) {
		this.level = Level.toLevel(level, Level.INFO);
	}

	public void setLoggers(Map<String, LoggerFactory> loggers) {
		this.loggers = loggers;
	}

	public void setAppenders(Collection<AppenderFactory> appenders) {
		this.appenders = appenders;
	}
}
