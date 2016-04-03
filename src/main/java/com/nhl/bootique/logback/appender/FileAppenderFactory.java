package com.nhl.bootique.logback.appender;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonTypeName;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;

/**
 * A configuration object that sets up a file appender in Logback, potentially
 * with support for rotation, etc.
 * 
 * @since 0.8
 */
@JsonTypeName("file")
public class FileAppenderFactory extends AppenderFactory {

	private String file;
	private boolean rotate;
	private String maxFileSize;
	private int maxFiles;
	private String maxTotalFileSize;

	/**
	 * Sets a filename or a filename pattern for the log file. If "rotate" is
	 * true, the filename can be a pattern per <a href=
	 * "http://logback.qos.ch/manual/appenders.html#RollingFileAppender">Logback
	 * documentation</a> that determines time-based rotation interval.
	 * Additional settings on this factory allow to also cap individual and
	 * total files sizes and how many total log files can be kept.
	 * 
	 * @param file
	 *            a filename or a filename pattern for the log file.
	 */
	public void setFile(String file) {
		this.file = file;
	}

	/**
	 * Enables or disables file rotation. Default is "false".
	 * 
	 * @since 0.9
	 * @param rotate
	 *            if true, log file rotation is enabled.
	 */
	public void setRotate(boolean rotate) {
		this.rotate = rotate;
	}

	/**
	 * Sets a maximum size of a single log file. Exceeding this size causes
	 * rotation. This option is ignored if "rotate" is false.
	 * 
	 * @since 0.9
	 * @param maxFileSize
	 *            Max size of a single log file expressed in bytes, kilobytes,
	 *            megabytes or gigabytes by suffixing a numeric value with KB,
	 *            MB and respectively GB. For example, 5000000. Exceeding this
	 *            size causes rotation.
	 */
	public void setMaxFileSize(String maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	/**
	 * Sets a maximum number of log files to keep. Older files exceeding this
	 * number will be deleted.
	 * 
	 * @since 0.9
	 * @param maxFiles
	 *            maximum number of log files to keep.
	 */
	public void setMaxFiles(int maxFiles) {
		this.maxFiles = maxFiles;
	}

	/**
	 * @since 0.9
	 * @param maxTotalFileSize
	 *            maximum size of all log files combined.
	 */
	public void setMaxTotalFileSize(String maxTotalFileSize) {
		this.maxTotalFileSize = maxTotalFileSize;
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
