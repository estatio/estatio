package org.incode.module.docfragment.dom.types;

public class AtPathType {

    private AtPathType() {}

    public static class Meta {

        public static final int MAX_LEN = 255;  // as per security module's ApplicationTenancy#MAX_LENGTH_PATH

        private Meta() {}

    }

}
