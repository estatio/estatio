package org.estatio.module.docflow.restapi;

import javax.annotation.Nullable;
import javax.inject.Inject;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.CommandPersistence;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

import org.estatio.ecp.docflow.canonical.DocFlowZipData;
import org.estatio.module.docflow.dom.DocFlowZip;
import org.estatio.module.docflow.dom.DocFlowZipService;

@DomainService(
        nature = NatureOfService.VIEW_REST_ONLY,
        objectType = "docflow.DocFlowService"
)
public class DocFlowZipServiceRestApi {

    public static class UpsertDomainEvent extends ActionDomainEvent<Object> {}

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = UpsertDomainEvent.class,
            commandPersistence = CommandPersistence.IF_HINTED,
            publishing = Publishing.DISABLED
    )
    public DocFlowZip upsert(
            final long sdId,
            @Nullable
            final Clob xmlFileMetadati,
            final Clob xmlFatturaElettronica,
            final Blob pdfFatturaElettronica,
            @Nullable
            final Blob p7mFatturaElettronica,
            @Nullable
            final Blob pdfSupplier,
            final String atPath) {

        final String sha256 = Hashing.sha256().hashString(xmlFatturaElettronica.getChars().toString(), Charsets.UTF_8).toString();
        return docFlowZipService.handle(
                sdId,
                xmlFileMetadati, xmlFatturaElettronica, pdfFatturaElettronica, p7mFatturaElettronica, pdfSupplier,
                atPath, sha256);
    }

    // convenience for testing
    @Programmatic
    DocFlowZip upsert(
            final DocFlowZipData data,
            final String atPath) {

        return upsert(
                data.getSdId(),
                data.getXmlFileMetadati(), data.getXmlFatturaElettronica(), data.getPdfFatturaElettronica(),
                data.getP7mFatturaElettronica(), data.getPdfSupplier(),
                atPath);
    }


    @Inject
    DocFlowZipService docFlowZipService;

}


