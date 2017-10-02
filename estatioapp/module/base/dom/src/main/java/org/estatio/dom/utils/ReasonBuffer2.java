package org.estatio.dom.utils;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import org.apache.isis.applib.util.ReasonBuffer;

/**
 * Extension to applib's {@link ReasonBuffer}.
 */
public class ReasonBuffer2 {

    private final String prefixIfAny;

    private final List<String> reasons = Lists.newArrayList();

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
            reasons.add(reason);
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
        if (reasons.isEmpty()) {
            return null;
        }

        final StringBuilder buf = new StringBuilder();
        if(prefixIfAny != null) {
            buf.append(prefixIfAny);
            if(reasons.size() != 1) {
                buf.append(":");
            }
            buf.append(" ");
        }


        Joiner.on("; ").appendTo(buf, reasons);

        return buf.toString();
    }

}

