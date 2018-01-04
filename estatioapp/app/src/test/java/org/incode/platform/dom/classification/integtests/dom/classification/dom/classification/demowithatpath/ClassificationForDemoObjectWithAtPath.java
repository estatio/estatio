package org.incode.platform.dom.classification.integtests.dom.classification.dom.classification.demowithatpath;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Property;

import org.incode.module.classification.dom.impl.classification.Classification;
import org.incode.module.classification.dom.impl.classification.ClassificationRepository;
import org.incode.module.classification.dom.impl.classification.T_classifications;
import org.incode.module.classification.dom.impl.classification.T_classify;
import org.incode.module.classification.dom.impl.classification.T_unclassify;
import org.incode.platform.dom.classification.integtests.demo.dom.demowithatpath.DemoObjectWithAtPath;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType= IdentityType.DATASTORE,
        schema ="exampleDomClassification"
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@DomainObject
public class ClassificationForDemoObjectWithAtPath extends Classification {


    @Column(allowsNull = "false", name = "demoObjectId")
    @Property(editing = Editing.DISABLED)
    @Getter @Setter
    private DemoObjectWithAtPath demoObject;




    @Override
    public Object getClassified() {
        return getDemoObject();
    }

    @Override
    protected void setClassified(final Object classified) {
        setDemoObject((DemoObjectWithAtPath) classified);
    }



    @Mixin
    public static class classifications
            extends T_classifications<DemoObjectWithAtPath> {
        public classifications(final DemoObjectWithAtPath classified) {
            super(classified);
        }
    }

    @Mixin
    public static class classify extends T_classify<DemoObjectWithAtPath> {
        public classify(final DemoObjectWithAtPath classified) {
            super(classified);
        }
    }

    @Mixin
    public static class unclassify extends T_unclassify<DemoObjectWithAtPath> {
        public unclassify(final DemoObjectWithAtPath classified) {
            super(classified);
        }
    }

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SubtypeProvider
            extends ClassificationRepository.SubtypeProviderAbstract {
        public SubtypeProvider() {
            super(DemoObjectWithAtPath.class, ClassificationForDemoObjectWithAtPath.class);
        }
    }


}
