package org.incode.platform.dom.document.integtests.dom.document.dom.paperclips.other;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.NotPersistent;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Property;

import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.mixins.T_createAndAttachDocumentAndRender;
import org.incode.module.document.dom.mixins.T_createAndAttachDocumentAndScheduleRender;
import org.incode.module.document.dom.mixins.T_documents;
import org.incode.module.document.dom.mixins.T_previewUrl;
import org.incode.platform.dom.document.integtests.demo.dom.other.OtherObject;

@javax.jdo.annotations.PersistenceCapable(
        identityType= IdentityType.DATASTORE,
        schema="exampleDomDocument"
)
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@DomainObject
public class PaperclipForOtherObject extends Paperclip {

    //region > otherObject (property)
    private OtherObject otherObject;

    @Column(
            allowsNull = "false",
            name = "otherObjectId"
    )
    @Property(
            editing = Editing.DISABLED
    )
    public OtherObject getOtherObject() {
        return otherObject;
    }

    public void setOtherObject(final OtherObject otherObject) {
        this.otherObject = otherObject;
    }
    //endregion


    //region > attachedTo (hook, derived)
    @NotPersistent
    @Override
    public Object getAttachedTo() {
        return getOtherObject();
    }

    @Override
    protected void setAttachedTo(final Object object) {
        setOtherObject((OtherObject) object);
    }
    //endregion


    //region > SubtypeProvider SPI implementation

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SubtypeProvider extends PaperclipRepository.SubtypeProviderAbstract {
        public SubtypeProvider() {
            super(OtherObject.class, PaperclipForOtherObject.class);
        }
    }
    //endregion

    //region > mixins

    @Mixin
    public static class _preview extends T_previewUrl<OtherObject> {
        public _preview(final OtherObject otherObject) {
            super(otherObject);
        }
    }

    @Mixin
    public static class _documents extends T_documents<OtherObject> {
        public _documents(final OtherObject otherObject) {
            super(otherObject);
        }
    }

    @Mixin
    public static class _createAndAttachDocumentAndRender extends T_createAndAttachDocumentAndRender<OtherObject> {
        public _createAndAttachDocumentAndRender(final OtherObject otherObject) {
            super(otherObject);
        }
    }

    @Mixin
    public static class _createAndAttachDocumentAndScheduleRender extends T_createAndAttachDocumentAndScheduleRender<OtherObject> {
        public _createAndAttachDocumentAndScheduleRender(final OtherObject otherObject) {
            super(otherObject);
        }
    }

    //endregion

}
