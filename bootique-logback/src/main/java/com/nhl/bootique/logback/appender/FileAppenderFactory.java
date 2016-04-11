package com.nhl.bootique.logback.appender;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonTypeName;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;

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
	 *            MB and respectively GB. For example: 5000000, 5000KB, 5MB and
	 *            2GB.
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
	 * Sets a maximum size of all log files combined. Equivalent to Logback
	 * 'totalSizeCap' property. Requires "maxFiles" to be set.
	 * 
	 * @since 0.9
	 * @param maxTotalFileSize
	 *            maximum size of all log files combined expressed in bytes,
	 *            kilobytes, megabytes or gigabytes by suffixing a numeric value
	 *            with KB, MB and respectively GB. For example: 5000000, 5000KB,
	 *            5MB and 2GB.
	 */
	public void setMaxTotalFileSize(String maxTotalFileSize) {
		this.maxTotalFileSize = maxTotalFileSize;
	}

	@Override
	public Appender<ILoggingEvent> createAppender(LoggerContext context) {

		LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
		encoder.setLayout(createLayout(context));

		FileAppender<ILoggingEvent> appender = rotate ? createRollingFileAppender(encoder, context)
				: createSingleFileAppender(encoder, context);

		return asAsync(appender);
	}

	protected FileAppender<ILoggingEvent> createSingleFileAppender(Encoder<ILoggingEvent> encoder,
			LoggerContext context) {
		FileAppender<ILoggingEvent> appender = new FileAppender<>();
		appender.setFile(Objects.requireNonNull(file));

		appender.setContext(context);
		appender.setEncoder(encoder);
		appender.start();

		return appender;
	}

	protected FileAppender<ILoggingEvent> createRollingFileAppender(Encoder<ILoggingEvent> encoder,
			LoggerContext context) {

		TimeBasedRollingPolicy<ILoggingEvent> policy = maxFileSize != null ? createSizeAndTimeRollingPolicy(context)
				: createTimeRollingPolicy(context);

		if (shouldDeleteOlderFiles()) {
			setupToDeleteOlderFiles(policy);
		}

		RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
		appender.setRollingPolicy(policy);
		policy.setParent(appender);

		appender.setContext(context);
		appender.setEncoder(encoder);

		policy.start();
		appender.start();

		return appender;
	}

	protected boolean shouldDeleteOlderFiles() {
		return maxFiles > 0;
	}

	protected void setupToDeleteOlderFiles(TimeBasedRollingPolicy<?> policy) {

		policy.setMaxHistory(maxFiles);
		policy.setCleanHistoryOnStart(true);

		if (maxTotalFileSize != null) {
			policy.setTotalSizeCap(FileSize.valueOf(maxTotalFileSize));
		}
	}

	protected SizeAndTimeBasedRollingPolicy<ILoggingEvent> createSizeAndTimeRollingPolicy(LoggerContext context) {

		// TODO: validate filename pattern... if %i is absent, the logger would
		// quietly stop working

		SizeAndTimeBasedRollingPolicy<ILoggingEvent> policy = new SizeAndTimeBasedRollingPolicy<>();
		policy.setMaxFileSize(maxFileSize);
		policy.setFileNamePattern(file);
		policy.setContext(context);
		return policy;
	}

	protected TimeBasedRollingPolicy<ILoggingEvent> createTimeRollingPolicy(LoggerContext context) {

		// TODO: validate filename pattern... if %d{} is absent, the logger
		// would quietly stop working

		TimeBasedRollingPolicy<ILoggingEvent> policy = new TimeBasedRollingPolicy<>();
		policy.setFileNamePattern(file);
		policy.setContext(context);
		return policy;
	}
}
