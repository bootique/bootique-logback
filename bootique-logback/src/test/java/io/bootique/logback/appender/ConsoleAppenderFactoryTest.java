package io.bootique.logback.appender;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ConsoleAppenderFactoryTest {

    private LoggerContext mockContext;

    @Before
    public void before() {
        mockContext = mock(LoggerContext.class);
    }

    @Test
    public void testCreateConsoleAppenderTarget_Default() {
        ConsoleAppenderFactory factory = new ConsoleAppenderFactory();
        ConsoleAppender<ILoggingEvent> appender = factory.createConsoleAppender(mockContext);

        assertEquals(ch.qos.logback.core.joran.spi.ConsoleTarget.SystemOut.getName(), appender.getTarget());
    }

    @Test
    public void testCreateConsoleAppenderTarget_Stderr() {
        ConsoleAppenderFactory factory = new ConsoleAppenderFactory();
        factory.setTarget(ConsoleTarget.stderr);
        ConsoleAppender<ILoggingEvent> appender = factory.createConsoleAppender(mockContext);

        assertEquals(ch.qos.logback.core.joran.spi.ConsoleTarget.SystemErr.getName(), appender.getTarget());
    }

    @Test
    public void testCreateConsoleAppenderTarget_Stdout() {
        ConsoleAppenderFactory factory = new ConsoleAppenderFactory();
        factory.setTarget(ConsoleTarget.stdout);
        ConsoleAppender<ILoggingEvent> appender = factory.createConsoleAppender(mockContext);

        assertEquals(ch.qos.logback.core.joran.spi.ConsoleTarget.SystemOut.getName(), appender.getTarget());
    }

}
