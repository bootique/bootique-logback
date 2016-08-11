package io.bootique.logback.appender;

import com.fasterxml.jackson.annotation.JsonTypeName;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;

import java.util.Objects;

/**
 * @since 0.8
 */
@JsonTypeName("console")
public class ConsoleAppenderFactory extends AppenderFactory {

    private ConsoleTarget target;

    public ConsoleAppenderFactory() {
        this.target = ConsoleTarget.stdout;
    }

    /**
     * Sets whether the appender should log to stderr or stdout. "stdout" is the default.
     *
     * @param target either "stdout" or "stderr".
     * @since 0.12
     */
    public void setTarget(ConsoleTarget target) {
        this.target = Objects.requireNonNull(target);
    }

    /**
     * @since 0.12
     * @return configured target (stdout or stderr).
     */
    public ConsoleTarget getTarget() {
        return target;
    }

    @Override
    public Appender<ILoggingEvent> createAppender(LoggerContext context) {
        return asAsync(createConsoleAppender(context));
    }

    protected ConsoleAppender<ILoggingEvent> createConsoleAppender(LoggerContext context) {
        ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
        appender.setName("console");
        appender.setContext(context);
        appender.setTarget(target.getLogbackTarget());

        LayoutWrappingEncoder<ILoggingEvent> layoutEncoder = new LayoutWrappingEncoder<>();
        layoutEncoder.setLayout(createLayout(context));
        appender.setEncoder(layoutEncoder);

        appender.start();

        return appender;
    }
}
