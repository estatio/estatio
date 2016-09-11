/*
 *  Copyright 2016 Dan Haywood
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.incode.module.documents.dom;

public final class DocumentsModule {

    //region > constants

    public static class JdoColumnLength {


        private JdoColumnLength(){}

        public static final int ID = 36; // to allow for a UUID
        public static final int REFERENCE = 24;

        public static final int NAME = 50;
        public static final int DOC_NAME = 255;

        public static final int AT_PATH = 255;  // as per security module's ApplicationTenancy#MAX_LENGTH_PATH
        public static final int USER_NAME = 30; // as per security module's ApplicationUser#USER_NAME
        public static final int MIME_TYPE = 255;
        public static final int FILE_SUFFIX = 12;
        public static final int EXTERNAL_URL = 2000;

        public final static int FQCN = 254;

        public static final int TEXT = 4000; // long varchar

        public static final int BOOKMARK = 2000;

    }

    public static class Constants {

        private Constants(){}

        public static final int CLOB_MULTILINE = 14;
    }


    //endregion

    //region > constructor
    private DocumentsModule(){}
    //endregion

    //region > ui event classes
    public abstract static class TitleUiEvent<S>
            extends org.apache.isis.applib.services.eventbus.TitleUiEvent<S> { }
    public abstract static class IconUiEvent<S>
            extends org.apache.isis.applib.services.eventbus.IconUiEvent<S> { }
    public abstract static class CssClassUiEvent<S>
            extends org.apache.isis.applib.services.eventbus.CssClassUiEvent<S> { }
    //endregion

    //region > domain event classes
    public abstract static class ActionDomainEvent<S>
            extends org.apache.isis.applib.services.eventbus.ActionDomainEvent<S> { }
    public abstract static class CollectionDomainEvent<S,T>
            extends org.apache.isis.applib.services.eventbus.CollectionDomainEvent<S,T> { }
    public abstract static class PropertyDomainEvent<S,T>
            extends org.apache.isis.applib.services.eventbus.PropertyDomainEvent<S,T> { }

    //endregion

}
