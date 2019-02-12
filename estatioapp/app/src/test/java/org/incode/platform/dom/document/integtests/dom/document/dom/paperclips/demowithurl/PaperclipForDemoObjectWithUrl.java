package org.incode.platform.dom.document.integtests.dom.document.dom.paperclips.demowithurl;

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
import org.incode.platform.dom.document.integtests.demo.dom.demowithurl.DemoObjectWithUrl;

@javax.jdo.annotations.PersistenceCapable(
        identityType= IdentityType.DATASTORE,
        schema="exampleDomDocument"
)
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@DomainObject
public class PaperclipForDemoObjectWithUrl extends Paperclip {

    //region > demoObject (property)
    private DemoObjectWithUrl demoObject;

    @Column(
            allowsNull = "false",
            name = "demoObjectId"
    )
    @Property(
            editing = Editing.DISABLED
    )
    public DemoObjectWithUrl getDemoObject() {
        return demoObject;
    }

    public void setDemoObject(final DemoObjectWithUrl demoObject) {
        this.demoObject = demoObject;
    }
    //endregion


    //region > attachedTo (hook, derived)
    @NotPersistent
    @Override
    public Object getAttachedTo() {
        return getDemoObject();
    }

    @Override
    protected void setAttachedTo(final Object object) {
        setDemoObject((DemoObjectWithUrl) object);
    }
    //endregion


    //region > SubtypeProvider SPI implementation

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SubtypeProvider extends PaperclipRepository.SubtypeProviderAbstract {
        public SubtypeProvider() {
            super(DemoObjectWithUrl.class, PaperclipForDemoObjectWithUrl.class);
        }
    }
    //endregion

    //region > mixins

    @Mixin
    public static class _preview extends T_previewUrl<DemoObjectWithUrl> {
        public _preview(final DemoObjectWithUrl demoObject) {
            super(demoObject);
        }
    }

    @Mixin
    public static class _documents extends T_documents<DemoObjectWithUrl> {
        public _documents(final DemoObjectWithUrl demoObject) {
            super(demoObject);
        }
    }

    @Mixin
    public static class _createAndAttachDocumentAndRender extends T_createAndAttachDocumentAndRender<DemoObjectWithUrl> {
        public _createAndAttachDocumentAndRender(final DemoObjectWithUrl demoObject) {
            super(demoObject);
        }
    }

    @Mixin
    public static class _createAndAttachDocumentAndScheduleRender extends T_createAndAttachDocumentAndScheduleRender<DemoObjectWithUrl> {
        public _createAndAttachDocumentAndScheduleRender(final DemoObjectWithUrl demoObject) {
            super(demoObject);
        }
    }

    //endregion

}
