package io.bootique.logback.appender;

import com.fasterxml.jackson.annotation.JsonTypeName;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;

import java.util.Objects;

/**
 * @since 0.8
 */
@JsonTypeName("console")
@BQConfig("Appender that prints its output to stdout or stderr.")
public class ConsoleAppenderFactory extends AppenderFactory {

    private ConsoleTarget target;

    public ConsoleAppenderFactory() {
        this.target = ConsoleTarget.stdout;
    }

    /**
     * @return configured target (stdout or stderr).
     * @since 0.12
     */
    public ConsoleTarget getTarget() {
        return target;
    }

    /**
     * Sets whether the appender should log to stderr or stdout. "stdout" is the default.
     *
     * @param target either "stdout" or "stderr".
     * @since 0.12
     */
    @BQConfigProperty("Whether the log should be sent to stdout or stderr. The default is 'stdout'")
    public void setTarget(ConsoleTarget target) {
        this.target = Objects.requireNonNull(target);
    }

    @Override
    public Appender<ILoggingEvent> createAppender(LoggerContext context, String defaultLogFormat) {
        return asAsync(createConsoleAppender(context, defaultLogFormat));
    }

    protected ConsoleAppender<ILoggingEvent> createConsoleAppender(LoggerContext context, String defaultLogFormat) {
        ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
        appender.setName("console");
        appender.setContext(context);
        appender.setTarget(target.getLogbackTarget());

        LayoutWrappingEncoder<ILoggingEvent> layoutEncoder = new LayoutWrappingEncoder<>();
        layoutEncoder.setLayout(createLayout(context, defaultLogFormat));
        appender.setEncoder(layoutEncoder);

        appender.start();

        return appender;
    }
}
