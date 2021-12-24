package io.bootique.logback.layout;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.config.PolymorphicConfiguration;

/**
 * @since 3.0
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = DefaultLayoutFactory.class)
@BQConfig("Create layout.")
public abstract class LayoutFactory implements PolymorphicConfiguration {
    private String type;

    public String getType() {
        return type;
    }

    /**
     * @param type layout type.
     */
    @BQConfigProperty("content out type, available types: \"json\", \"pattern\". By default is \"pattern\".")
    public void setType(String type) {
        this.type = type;
    }


    public abstract Layout<ILoggingEvent> createLayout(LoggerContext context, String defaultLogFormat);

}
