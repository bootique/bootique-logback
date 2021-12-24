package io.bootique.logback.layout;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;

/**
 * @since 3.0
 */
@JsonTypeName("pattern")
@BQConfig
public class DefaultLayoutFactory extends LayoutFactory {
    private String logFormat;

    /**
     * @return configured log format
     */
    public String getLogFormat() {
        return logFormat;
    }

    @BQConfigProperty("Log format for pattern.")
    public void setLogFormat(String logFormat) {
        this.logFormat = logFormat;
    }

    @Override
    public Layout<ILoggingEvent> createLayout(LoggerContext context, String defaultLogFormat) { //TODO if defaultLogFormat is required
        String logFormat = this.logFormat != null ? this.logFormat : defaultLogFormat;

        PatternLayout layoutPattern = new PatternLayout();
        layoutPattern.setPattern(logFormat);
        layoutPattern.setContext(context);

        layoutPattern.start();
        return layoutPattern;
    }
}
