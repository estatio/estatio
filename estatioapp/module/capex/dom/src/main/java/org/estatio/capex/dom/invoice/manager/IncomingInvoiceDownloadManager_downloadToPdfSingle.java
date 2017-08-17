package org.estatio.capex.dom.invoice.manager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.assertj.core.util.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

@Mixin(method="act")
public class IncomingInvoiceDownloadManager_downloadToPdfSingle extends IncomingInvoiceDownloadManager_downloadToPdfAbstract {


    public IncomingInvoiceDownloadManager_downloadToPdfSingle(final IncomingInvoiceDownloadManager manager) {
        super(manager);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(named = "Download single PDF")
    public Blob act(
            final String fileName,
            @ParameterLayout(named = "How many first pages of each invoice's PDF?")
            final Integer numFirstPages,
            @ParameterLayout(named = "How many final pages of each invoice's PDF?")
            final Integer numLastPages) throws IOException {

        final List<DocumentPreparer> preparers = documentPreparersForInvoices(numFirstPages, numLastPages);
        final List<File> fileList = filesFrom(preparers);

        final byte[] singlePdf = pdfBoxService2.merge(fileList);

        preparers.forEach(DocumentPreparer::cleanup);

        return new Blob(fileName, "application/pdf", singlePdf);
    }

    @Override
    public String disableAct() {
        return super.disableAct();
    }

    public String default0Act() {
        return manager.defaultFileNameWithSuffix(".pdf");
    }
    public Integer default1Act() {
        return 3;
    }
    public List<Integer> choices1Act() {
        return Lists.newArrayList(1,2,3,4,5,10,20,50);
    }
    public Integer default2Act() {
        return 1;
    }
    public List<Integer> choices2Act() {
        return Lists.newArrayList(0,1,2,3,4,5,10,20,50);
    }


    @javax.inject.Inject
    PdfBoxService2 pdfBoxService2;


    private List<File> filesFrom(final List<DocumentPreparer> preparers) {
        return preparers.stream()
                .map(preparer -> preparer.stampUsing(pdfManipulator).getTempFile())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    // TODO: to add into PdfBox service in the future...
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class PdfBoxService2 {

        @Programmatic
        public byte[] merge(final List<File> fileList) throws IOException {

            final ByteArrayOutputStream baos = new ByteArrayOutputStream();

            final PDFMergerUtility ut = new PDFMergerUtility();
            for (File file : fileList) {
                ut.addSource(file);
            }

            ut.setDestinationStream(baos);
            ut.mergeDocuments(MemoryUsageSetting.setupTempFileOnly());

            return baos.toByteArray();
        }

        @Programmatic
        public byte[] merge(final File... files) throws IOException {
            return merge(Arrays.asList(files));
        }

    }
}
