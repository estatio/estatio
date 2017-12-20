package org.incode.module.classification.dom;

public final class ClassificationModule {

    //region > constants

    public static class JdoColumnLength {


        private JdoColumnLength(){}

        public static final int CATEGORY_REFERENCE = 24;
        public static final int CATEGORY_NAME = 100;
        public static final int CATEGORY_FQNAME = 254;
        public static final int CATEGORY_FQORDINAL = 50;

        public static final int APPLICABILITY_DOMAIN_TYPE = 255;

        public static final int AT_PATH = 255;  // as per security module's ApplicationTenancy#MAX_LENGTH_PATH

        public static final int BOOKMARK = 2000;
    }


    //endregion

    //region > constructor
    private ClassificationModule(){}
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
