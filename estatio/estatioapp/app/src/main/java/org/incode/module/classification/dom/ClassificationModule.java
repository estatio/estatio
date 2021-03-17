package org.incode.module.classification.dom;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.isis.applib.ModuleAbstract;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.teardown.TeardownFixtureAbstract2;

import org.incode.module.classification.dom.impl.applicability.Applicability;
import org.incode.module.classification.dom.impl.category.Category;
import org.incode.module.classification.dom.impl.classification.Classification;

@XmlRootElement(name = "module")
public class ClassificationModule extends ModuleAbstract {

    @Override
    public FixtureScript getTeardownFixture() {
        return new TeardownFixtureAbstract2() {

            @Override
            protected void execute(final FixtureScript.ExecutionContext executionContext) {
                deleteFrom(Classification.class);
                deleteFrom(Applicability.class);
                deleteFrom(Category.class);

            }
        };
    }

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
