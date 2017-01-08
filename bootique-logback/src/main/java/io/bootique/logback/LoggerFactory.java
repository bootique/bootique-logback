package io.bootique.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;

@BQConfig
public class LoggerFactory {

	private LogbackLevel level;

	public LoggerFactory() {
		this.level = LogbackLevel.info;
	}

	@BQConfigProperty("Logging level of a given logger and its children.")
	public void setLevel(LogbackLevel level) {
		this.level = level;
	}

	public void configLogger(String loggerName, LoggerContext context) {
		Logger logger = context.getLogger(loggerName);
		logger.setLevel(Level.toLevel(level.name(), Level.INFO));
		
		// TODO: appenders
	}

}
