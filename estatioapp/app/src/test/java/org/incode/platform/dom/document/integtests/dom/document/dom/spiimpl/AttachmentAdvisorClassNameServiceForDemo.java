package org.incode.platform.dom.document.integtests.dom.document.dom.spiimpl;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.document.dom.impl.applicability.AttachmentAdvisor;
import org.incode.module.document.dom.services.ClassNameServiceAbstract;
import org.incode.module.document.dom.services.ClassNameViewModel;
import org.incode.module.document.dom.spi.AttachmentAdvisorClassNameService;

@DomainService(
    nature = NatureOfService.DOMAIN
)
public class AttachmentAdvisorClassNameServiceForDemo extends ClassNameServiceAbstract<AttachmentAdvisor> implements
        AttachmentAdvisorClassNameService {

    public AttachmentAdvisorClassNameServiceForDemo() {
        super(AttachmentAdvisor.class, "org.incode.module.document.fixture");
    }

    @Programmatic
    @Override public List<ClassNameViewModel> attachmentAdvisorClassNames() {
        return this.classNames();
    }
}
