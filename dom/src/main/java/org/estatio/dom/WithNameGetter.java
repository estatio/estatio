package org.estatio.dom;

import com.google.common.base.Objects;

public interface WithNameGetter {

    public String getName();
    
    public static class ToString {
        private ToString() {}
        public static String of(WithNameGetter p) {
            return Objects.toStringHelper(p)
                    .add("name", p.getName())
                    .toString();
        }
    }

}
