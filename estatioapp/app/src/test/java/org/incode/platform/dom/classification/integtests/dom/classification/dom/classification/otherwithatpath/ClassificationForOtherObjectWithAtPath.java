package org.incode.platform.dom.classification.integtests.dom.classification.dom.classification.otherwithatpath;

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
import org.incode.platform.dom.classification.integtests.demo.dom.otherwithatpath.OtherObjectWithAtPath;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType= IdentityType.DATASTORE,
        schema="exampleDomClassification"
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@DomainObject
public class ClassificationForOtherObjectWithAtPath extends Classification {


    @Column(allowsNull = "false", name = "otherObjectId")
    @Property(editing = Editing.DISABLED)
    @Getter @Setter
    private OtherObjectWithAtPath otherObject;



    @Override
    public Object getClassified() {
        return getOtherObject();
    }

    @Override
    protected void setClassified(final Object classified) {
        setOtherObject((OtherObjectWithAtPath) classified);
    }

    @Mixin
    public static class classifications extends
            T_classifications<OtherObjectWithAtPath> {
        public classifications(final OtherObjectWithAtPath classified) {
            super(classified);
        }
    }

    @Mixin
    public static class classify extends T_classify<OtherObjectWithAtPath> {
        public classify(final OtherObjectWithAtPath classified) {
            super(classified);
        }
    }

    @Mixin
    public static class unclassify extends T_unclassify<OtherObjectWithAtPath> {
        public unclassify(final OtherObjectWithAtPath classified) {
            super(classified);
        }
    }

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SubtypeProvider
            extends ClassificationRepository.SubtypeProviderAbstract {
        public SubtypeProvider() {
            super(OtherObjectWithAtPath.class, ClassificationForOtherObjectWithAtPath.class);
        }
    }
}
