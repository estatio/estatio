package org.estatio.module.base.platform.applib;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import org.apache.isis.applib.util.ReasonBuffer;

import lombok.Builder;
import lombok.Getter;

/**
 * Extension to applib's {@link ReasonBuffer}.
 */
@Builder
public class ReasonBuffer2 {

    public static interface Condition {
        public boolean evaluate();
    }

    public static interface LazyReason {
        public String evaluate();
    }

    @Getter
    private static class ConditionAndReason implements LazyReason {
        private final Condition condition;
        private final String reason;

        public static ConditionAndReason create(final Condition condition, final String reason) {
            return reason != null
                    ? new ConditionAndReason(condition, reason)
                    : null;
        }

        private ConditionAndReason(final Condition condition, final String reason) {
            this.condition = condition;
            this.reason = reason;
        }

        @Override
        public String evaluate() {
            return condition.evaluate() ? reason : null;
        }
    }

    public enum Mode {
        ALL,
        SINGLE
    }

    private final Mode mode;
    private final String prefix;

    private final List<LazyReason> lazyReasons = Lists.newArrayList();

    public static ReasonBuffer2 forAll() {
        return ReasonBuffer2.builder().build();
    }

    public static ReasonBuffer2 forSingle() {
        return ReasonBuffer2.builder().mode(Mode.SINGLE).build();
    }

    public static ReasonBuffer2 forAll(final String prefix) {
        return ReasonBuffer2.builder().prefix(prefix).build();
    }

    public static ReasonBuffer2 forSingle(final String prefix) {
        return ReasonBuffer2.builder().prefix(prefix).mode(Mode.SINGLE).build();
    }

    private ReasonBuffer2(final Mode mode, final String prefix) {
        this.prefix = prefix;
        this.mode = mode != null ? mode : Mode.ALL;
    }

    public ReasonBuffer2 append(final LazyReason lazyReason) {
        lazyReasons.add(lazyReason);
        return this;
    }

    /**
     * Append a reason to the list of existing reasons.
     */
    public ReasonBuffer2 append(final String reason) {
        append(true, reason);
        return this;
    }

    /**
     * Append a reason to the list of existing reasons if the condition flag is
     * true.
     */
    public ReasonBuffer2 append(final boolean condition, final String reason) {
        lazyReasons.add(ConditionAndReason.create(() -> condition, reason));
        return this;
    }

    /**
     * Append a reason to the list of existing reasons if the condition flag is
     * true.
     */
    public ReasonBuffer2 append(final Condition condition, final String reason) {
        lazyReasons.add(ConditionAndReason.create(condition, reason));
        return this;
    }

    /**
     * Return the combined set of reasons, or <code>null</code> if there are
     * none.
     */
    public String getReason() {

        final Optional<LazyReason> anyReasons = this.lazyReasons.stream().filter(Objects::nonNull).findAny();
        if (!anyReasons.isPresent()) {
            return null;
        }

        final StringBuilder buf = new StringBuilder();

        final String reasons = appendReason(buf);
        return reasons.isEmpty() ? null : reasons;
    }

    /**
     * Appends reasons.
     */
    public String appendReason(final StringBuilder buf) {

        final List<LazyReason> nonNullLazyReasons =
                lazyReasons.stream().filter(Objects::nonNull).collect(Collectors.toList());

        int numReasons = 0;
        for (LazyReason lazyReason : nonNullLazyReasons) {
            final String reasonIfAny = lazyReason.evaluate();
            if (reasonIfAny != null) {
                if (numReasons > 0) {
                    buf.append("; ");
                }
                buf.append(reasonIfAny);
                numReasons++;

                if (mode == Mode.SINGLE) {
                    break;
                }
            }
        }

        if (prefix != null && numReasons > 0) {
            buf.insert(0, " ");
            if (numReasons != 1) {
                buf.insert(0, ":");
            }
            buf.insert(0, prefix);
        }

        return buf.toString();
    }

    /**
     * Combines sets of reasons from another.
     */
    public ReasonBuffer2 plus(final ReasonBuffer2 other) {
        this.lazyReasons.addAll(other.lazyReasons);
        return this;
    }

}
