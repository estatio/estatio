package org.estatio.dom;

import com.google.common.base.Objects;

public interface WithCodeGetter {

    public String getCode();
    
    public static class ToString {
        public static String of(WithCodeGetter p) {
            return Objects.toStringHelper(p)
                    .add("code", p.getCode())
                    .toString();
        }
    }

}
