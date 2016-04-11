package com.nhl.bootique.logback.appender;

import com.fasterxml.jackson.annotation.JsonTypeName;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;

/**
 * @since 0.8
 */
@JsonTypeName("console")
public class ConsoleAppenderFactory extends AppenderFactory {

	@Override
	public Appender<ILoggingEvent> createAppender(LoggerContext context) {
		ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
		appender.setName("console");
		appender.setContext(context);
		appender.setTarget("System.out");

		LayoutWrappingEncoder<ILoggingEvent> layoutEncoder = new LayoutWrappingEncoder<>();
		layoutEncoder.setLayout(createLayout(context));
		appender.setEncoder(layoutEncoder);

		appender.start();

		return asAsync(appender);
	}
}
