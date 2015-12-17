package com.nhl.bootique.logback;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.slf4j.bridge.SLF4JBridgeHandler;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.jul.LevelChangePropagator;

public class LogbackFactory {

	private Level level;
	private Map<String, LoggerFactory> loggers;
	private Collection<AppenderFactory> appenders;

	public LogbackFactory() {
		this.level = Level.INFO;
		this.loggers = Collections.emptyMap();
		this.appenders = Collections.emptyList();
	}

	public Logger createRootLogger(LoggerContext context) {

		rerouteJUL();

		final Logger root = context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		context.reset();

		final LevelChangePropagator propagator = new LevelChangePropagator();
		propagator.setContext(context);
		propagator.setResetJUL(true);

		context.addListener(propagator);

		root.setLevel(level);

		loggers.forEach((name, lf) -> lf.configLogger(name, context));

		if (appenders.isEmpty()) {
			setAppenders(Collections.singletonList(new AppenderFactory()));
		}

		appenders.forEach(a -> root.addAppender(a.createAppender(context)));

		return root;
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
