package org.incode.module.document.dom.types;

public class UserNameType {

    private UserNameType() {}

    public static class Meta {

        public static final int MAX_LEN = 30; // as per security module's ApplicationUser#USER_NAME

        private Meta() {}

    }

}
