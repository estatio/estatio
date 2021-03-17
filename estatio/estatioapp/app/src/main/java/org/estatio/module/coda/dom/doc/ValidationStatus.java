package org.estatio.module.coda.dom.doc;

import java.util.Arrays;
import java.util.List;

/**
 * Whether an element of a {@link CodaDocLine}, or whether a {@link CodaDocHead} overall, is valid or not.
 * The latter is derived as an aggregate of the former.
 *
 * <p>
 * Could have used a boolean, but maybe in the future there will be other status to add.
 * </p>
 */
public enum ValidationStatus {
    /**
     * For {@link CodaDocHead}s, indicates that all elements of all {@link CodaDocLine}s are valid.
     */
    VALID,
    /**
     * For {@link CodaDocLine}s, indicates that some element of the doc line is not valid.
     *
     * For {@link CodaDocHead}s, indicates that one or more {@link CodaDocLine}s has an invalid element.
     */
    INVALID,
    /**
     * When re-validating, initially start off in a state of not checked.
     */
    NOT_CHECKED;

    public static ValidationStatus invalidIfNull(final Object obj) {
        return obj != null ? VALID : INVALID;
    }

    public static ValidationStatus deriveFrom(final List<ValidationStatus> statuses) {
        for (final ValidationStatus status : statuses) {
            if(status != VALID) {
                return INVALID;
            }
        }
        return VALID;
    }
    public static ValidationStatus deriveFrom(final ValidationStatus... statuses) {
        return deriveFrom(Arrays.asList(statuses));
    }

    public boolean isValid() {
        return this == VALID;
    }
}
