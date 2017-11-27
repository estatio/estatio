package org.estatio.module.base.platform.integtestsupport;

import java.io.OutputStream;
import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.event.Level;

public class LogStream extends OutputStream {

    private final Logger logger;
    private final Level level;

    private final StringBuilder buf = new StringBuilder();

    public static PrintStream logPrintStream(final Logger logger, final Level level) {
        return new PrintStream(new LogStream(logger, level));
    }

    public LogStream(final Logger logger, final Level level) {
        this.logger = logger;
        this.level = level;
    }

    public void close() {}

    public void flush() {
        final String message = toString();
        switch (level) {
        case ERROR:
            logger.error(message);
            break;
        case WARN:
            logger.warn(message);
            break;
        case INFO:
            logger.info(message);
            break;
        case DEBUG:
            logger.debug(message);
            break;
        case TRACE:
            logger.trace(message);
            break;
        }

        // Clear the buffer
        buf.delete(0, buf.length());
    }

    public void write(byte[] b) {
        String str = new String(b);
        this.buf.append(str);
    }

    public void write(byte[] b, int off, int len) {
        String str = new String(b, off, len);
        this.buf.append(str);
    }

    public void write(int b) {
        String str = Integer.toString(b);
        this.buf.append(str);
    }

    public String toString() {
        return buf.toString();
    }
}
