package org.estatio.dom;

import com.google.common.base.Objects;

public interface WithDescriptionGetter {

    public String getDescription();
    
    public static class ToString {
        private ToString() {}
        public static String of(WithDescriptionGetter p) {
            return Objects.toStringHelper(p)
                    .add("description", p.getDescription())
                    .toString();
        }
    }

}
