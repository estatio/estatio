package org.estatio.dom;

import com.google.common.base.Objects;

public interface WithTitleGetter {

    public String getTitle();
    
    public static class ToString {
        public static String of(WithTitleGetter p) {
            return Objects.toStringHelper(p)
                    .add("title", p.getTitle())
                    .toString();
        }
    }

}
