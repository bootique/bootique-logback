package io.bootique.logback.layout;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggerContextVO;
import ch.qos.logback.core.Layout;
import com.fasterxml.jackson.core.JsonGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.exceptions.base.MockitoException;

import static org.junit.jupiter.api.Assertions.*;

class DefaultLayoutFactoryTest {
    private static final String LOG_FORMAT = "%c{20}: %m%n";

    @Test
    void createLayoutTest() {
        LoggerContext context = new LoggerContext();
        DefaultLayoutFactory factory = new DefaultLayoutFactory();
        Layout<ILoggingEvent> layout = factory.createLayout(context, LOG_FORMAT);
        assertTrue(layout instanceof PatternLayout);
        assertTrue(layout.isStarted());
    }

    @Test
    void createLayoutTestFormatNull() {
        LoggerContext context = new LoggerContext();
        DefaultLayoutFactory factory = new DefaultLayoutFactory();
        Layout<ILoggingEvent> layout = factory.createLayout(context, null);
        assertTrue(layout instanceof PatternLayout);
        assertFalse(layout.isStarted());
    }
}