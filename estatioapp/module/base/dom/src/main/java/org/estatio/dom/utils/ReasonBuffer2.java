package org.estatio.dom.utils;

import org.apache.isis.applib.util.ReasonBuffer;

/**
 * Extension to applib's {@link ReasonBuffer}.
 */
public class ReasonBuffer2 {

    private final String prefixIfAny;

    private final StringBuffer reasonBuffer = new StringBuffer();

    public static ReasonBuffer2 create() {
        return prefix(null);
    }

    public static ReasonBuffer2 prefix(final String prefix) {
        return new ReasonBuffer2(prefix);
    }

    private ReasonBuffer2(final String prefixIfAny) {
        this.prefixIfAny = prefixIfAny;
    }


    /**
     * Append a reason to the list of existing reasons.
     */
    public void append(final String reason) {
        if (reason != null) {
            if (reasonBuffer.length() > 0) {
                reasonBuffer.append("; ");
            }
            reasonBuffer.append(reason);
        }
    }

    /**
     * Append a reason to the list of existing reasons if the condition flag is
     * true.
     */
    public void appendOnCondition(final boolean condition, final String reason) {
        if (condition) {
            append(reason);
        }
    }

    /**
     * Return the combined set of reasons, or <code>null</code> if there are
     * none.
     */
    public String getReason() {
        if (reasonBuffer.length() == 0)
            return null;

        final String prefix = prefixIfAny != null ? prefixIfAny + ": " : "";
        return prefix + reasonBuffer.toString();
    }

}

