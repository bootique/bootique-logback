package com.nhl.bootique.logback.appender;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AsyncAppenderBase;
import ch.qos.logback.core.Context;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = ConsoleAppenderFactory.class)

// TODO: how do we avoid hardcoding all subclasses in a superclass annotation?
@JsonSubTypes(value = { @JsonSubTypes.Type(value = ConsoleAppenderFactory.class),
		@JsonSubTypes.Type(value = FileAppenderFactory.class) })
public abstract class AppenderFactory {

	private String logFormat;

	public AppenderFactory() {
		this.logFormat = "%-5p [%d{ISO8601,UTC}] %thread %c{20}: %m%n%rEx";
	}

	public void setLogFormat(String logFormat) {
		this.logFormat = logFormat;
	}

	public abstract Appender<ILoggingEvent> createAppender(LoggerContext context);

	protected PatternLayout createLayout(LoggerContext context) {
		PatternLayout layout = new PatternLayout();
		layout.setPattern(logFormat);
		layout.setContext(context);

		layout.start();
		return layout;
	}

	protected Appender<ILoggingEvent> asAsync(Appender<ILoggingEvent> appender) {
		return asAsync(appender, appender.getContext());
	}

	protected Appender<ILoggingEvent> asAsync(Appender<ILoggingEvent> appender, Context context) {
		final AsyncAppender asyncAppender = new AsyncAppender();
		asyncAppender.setIncludeCallerData(false);
		asyncAppender.setQueueSize(AsyncAppenderBase.DEFAULT_QUEUE_SIZE);
		asyncAppender.setDiscardingThreshold(-1);
		asyncAppender.setContext(context);
		asyncAppender.setName(appender.getName());
		asyncAppender.addAppender(appender);
		asyncAppender.start();
		return asyncAppender;
	}
}
