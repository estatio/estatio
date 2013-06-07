package org.estatio.dom;

import com.google.common.base.Objects;

public interface WithReferenceGetter {

    public String getReference();
    
    public static class ToString {
        public static String of(WithReferenceGetter p) {
            return Objects.toStringHelper(p)
                    .add("reference", p.getReference())
                    .toString();
        }
    }

}
