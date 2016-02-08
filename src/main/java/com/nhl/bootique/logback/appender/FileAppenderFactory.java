package com.nhl.bootique.logback.appender;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonTypeName;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;

/**
 * @since 0.8
 */
@JsonTypeName("file")
public class FileAppenderFactory extends AppenderFactory {

	private String file;

	public void setFile(String file) {
		this.file = file;
	}

	@Override
	public Appender<ILoggingEvent> createAppender(LoggerContext context) {
		FileAppender<ILoggingEvent> appender = new FileAppender<>();
		appender.setFile(Objects.requireNonNull(file));
		appender.setContext(context);

		LayoutWrappingEncoder<ILoggingEvent> layoutEncoder = new LayoutWrappingEncoder<>();
		layoutEncoder.setLayout(createLayout(context));
		appender.setEncoder(layoutEncoder);

		appender.start();

		return asAsync(appender);
	}
}
