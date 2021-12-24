package io.bootique.logback.layout;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.contrib.jackson.JacksonJsonFormatter;
import ch.qos.logback.contrib.json.classic.JsonLayout;
import ch.qos.logback.core.Layout;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.bootique.annotation.BQConfigProperty;

/**
 * JsonLayout configure logs with json format.
 * Can be specify timestamp format and human-readable option.
 *
 * @since 3.0
 */
@JsonTypeName("json")
public class JsonLayoutFactory extends LayoutFactory {
    private static final String DEFAULT_TIMESTAMP = "yyyy-MM-dd HH:mm:ss.SSS";
    private String timestampFormat;
    private boolean prettyPrint;

    /**
     * @return configured timestamp
     */
    public String getTimestampFormat() {
        return timestampFormat;
    }


    /**
     * Sets timestamp for output logs.
     */
    @BQConfigProperty("Timestamp format for pattern.")
    public void setTimestampFormat(String timestampFormat) {
        this.timestampFormat = timestampFormat;
    }

    /**
     * @return prettyPrint
     */
    public boolean getPrettyPrint() {
        return prettyPrint;
    }

    /**
     * Sets prettyPrint value for human-readable.
     */
    @BQConfigProperty("Print logs in a human-readable format.")
    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }


    @Override
    public Layout<ILoggingEvent> createLayout(LoggerContext context, String defaultLogFormat) {
        String timestamp = this.timestampFormat != null ? this.timestampFormat : DEFAULT_TIMESTAMP;

        JsonLayout jsonLayout = new JsonLayout();
        jsonLayout.setTimestampFormat(timestamp);

        JacksonJsonFormatter formatter = new JacksonJsonFormatter();
        formatter.setPrettyPrint(prettyPrint);
        jsonLayout.setJsonFormatter(formatter);

        jsonLayout.start();
        return jsonLayout;
    }

}
