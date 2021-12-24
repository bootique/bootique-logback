package io.bootique.logback.layout;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.contrib.json.classic.JsonLayout;
import ch.qos.logback.core.Layout;
import io.bootique.junit5.BQTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@BQTest
class JsonLayoutFactoryTest {

    @Test
    void createLayoutTest() {
        LoggerContext context = new LoggerContext();
        JsonLayoutFactory factory = new JsonLayoutFactory();
        Layout<ILoggingEvent> layout = factory.createLayout(context, null);
        assertTrue(layout instanceof JsonLayout);
        assertTrue(layout.isStarted());
    }
}