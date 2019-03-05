/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.estatio.module.invoice.dom;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentAbstract;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import lombok.Getter;

/**
 * maximum length is 24 ({@link DocumentType.ReferenceType.Meta#MAX_LEN}).
 */

    // cover notes
    COVER_NOTE_PRELIM_LETTER(
            "COVER-NOTE-PRELIM-LETTER", "Email Cover Note for Preliminary Letter",
            Nature.OUTGOING, null,
            null, // supports, always null if OUTGOING
            null, // corresponding cover note
            new DocumentTemplateData[] {
                    DocumentTemplateData.COVER_NOTE_PRELIM_LETTER_GLOBAL,
                    DocumentTemplateData.COVER_NOTE_PRELIM_LETTER_ITA
            }
    ),
    COVER_NOTE_INVOICE(
            "COVER-NOTE-INVOICE", "Email Cover Note for Invoice",
            Nature.OUTGOING, null,
            null, // supports, always null if OUTGOING
            null, // corresponding cover note
            new DocumentTemplateData[] {
                    DocumentTemplateData.COVER_NOTE_INVOICE_GLOBAL,
                    DocumentTemplateData.COVER_NOTE_INVOICE_ITA
            }
    ),

    // primary docs
    PRELIM_LETTER(
            "PRELIM-LETTER", "Preliminary letter for Invoice",
            Nature.OUTGOING, "Merged Preliminary Letters.pdf",
            null, // supports, always null if OUTGOING
            COVER_NOTE_PRELIM_LETTER,
            new DocumentTemplateData[] {
                    DocumentTemplateData.PRELIM_LETTER_GLOBAL,
                    DocumentTemplateData.PRELIM_LETTER_ITA
            }
    ),
    INVOICE(
            "INVOICE", "Invoice",
            Nature.OUTGOING, "Merged Invoices.pdf",
            null, // supports, always null if OUTGOING
            COVER_NOTE_INVOICE,
            new DocumentTemplateData[] {
                    DocumentTemplateData.INVOICE_GLOBAL,
                    DocumentTemplateData.INVOICE_ITA
            }
    ),

    // supporting docs
    SUPPLIER_RECEIPT(
            "SUPPLIER-RECEIPT", "Supplier Receipt (for Invoice)",
            Nature.NOT_SPECIFIED, null,
            INVOICE, // supports
            null, // corresponding cover note, always null if not OUTGOING
            new DocumentTemplateData[]{}
    ),
    TAX_REGISTER(
            "TAX-REGISTER", "Tax Register (for Invoice)",
            // TODO: REVIEW - think this should probably be NOT_SPECIFIED (see calls to #hasIncomingType)
            Nature.INCOMING, null,
            INVOICE, // supports
            null, // corresponding cover note, always null if not OUTGOING
            new DocumentTemplateData[]{}
    ),
    CALCULATION(
            "CALCULATION", "Calculation (for Preliminary Letter)",
            Nature.NOT_SPECIFIED, null,
            PRELIM_LETTER, //supports
            null, // corresponding cover note, always null if not OUTGOING
            new DocumentTemplateData[]{}
    ),
    SPECIAL_COMMUNICATION(
            "SPECIAL-COMMUNICATION", "Special Communication (for Preliminary Letter)",
            Nature.NOT_SPECIFIED, null,
            PRELIM_LETTER, // supports
            null, // corresponding cover note, always null if not OUTGOING
            new DocumentTemplateData[]{}
    ),

    // preview only
    INVOICES(
            "INVOICES", "Invoices overview",
            Nature.NOT_SPECIFIED, null,
            null,
            null, // corresponding cover note, always null if not OUTGOING
            new DocumentTemplateData[]{
                    DocumentTemplateData.INVOICES
            }
    ),
    INVOICES_PRELIM(
            "INVOICES-PRELIM", "Preliminary letter for Invoices",
            Nature.NOT_SPECIFIED, null,
            null,
            null, // corresponding cover note, always null if not OUTGOING
            new DocumentTemplateData[] {
                    DocumentTemplateData.INVOICES_PRELIM
            }
    ),
    INVOICES_FOR_SELLER(
            "INVOICES-FOR-SELLER", "Preliminary Invoice for Seller",
            Nature.NOT_SPECIFIED, null,
            null,
            null, // corresponding cover note, always null if not OUTGOING
            new DocumentTemplateData[] {
                    DocumentTemplateData.INVOICES_FOR_SELLER
            }
    ),

    INCOMING(
            "INCOMING", "Incoming",
            Nature.INCOMING, "Merged Incoming.pdf",
            null, // supports
            null, // corresponding cover note
            new DocumentTemplateData[]{}
    ),
    INCOMING_INVOICE(
            "INCOMING_INVOICE", "Incoming Invoice",
            Nature.INCOMING, "Merged Incoming Invoices.pdf",
            null, null, // corresponding cover note, always null if not outgoing
            new DocumentTemplateData[]{}
    ),
    /*
    not in DB, so unused.
    INCOMING_LOCAL_INVOICE(
            "INCOMING_LOCAL_INVOICE", "Incoming Local Invoice",
            Nature.INCOMING, "Merged Incoming Local Invoices.pdf",
            null,
            null, // corresponding cover note, always null if not OUTGOING
            null, null, null // renderModel, attachments etc; always null if INCOMING or supports


    ),
     */
    /*
    not in DB, so unused.

    INCOMING_CORPORATE_INVOICE(
            "INCOMING_CORPORATE_INVOICE", "Incoming Corporate Invoice",
            Nature.INCOMING, "Merged Incoming Corporate Invoices.pdf",
            null,
            null, // corresponding cover note, always null if not OUTGOING
            null, null, null // renderModel, attachments etc; always null if INCOMING or supports

    ),

     */
    INCOMING_ORDER(
            "INCOMING_ORDER", "Incoming Order",
            Nature.INCOMING, "Merged Incoming Orders.pdf",
            null,
            null,
            new DocumentTemplateData[]{}
    ),

    ORDER_CONFIRM(
            "ORDER_CONFIRM", "Confirm order with Supplier",
            Nature.OUTGOING, null,
            null,
            null,
            new DocumentTemplateData[]{
                    DocumentTemplateData.ORDER_CONFIRM_ITA
            }
    ),
    IBAN_PROOF(
            "IBAN_PROOF", "Iban verification proof",
            Nature.NOT_SPECIFIED, null,
            null,
            null,
            new DocumentTemplateData[]{}
    );

    @Getter
    private final String ref;
    @Getter
    private final String name;
    @Getter
    private final String mergedFileName;
    @Getter
    private final DocumentTypeData coverNote;
    @Getter
    private final DocumentTypeData supports;
    @Getter
    private final Nature nature;

    private final Map<String,DocumentTemplateData> documentTemplateDataByPath;

    public boolean isIncoming() {
        return nature == Nature.INCOMING;
    }

    public DocumentTemplateData lookup(final String atPath) {
        return documentTemplateDataByPath.get(atPath);
    }

    public Iterable<Map.Entry<String, DocumentTemplateData>> templateIterable() {
        return documentTemplateDataByPath.entrySet();
    }

    public enum Nature {
        INCOMING,
        OUTGOING,
        NOT_SPECIFIED
    }

    DocumentTypeData(
            final String ref,
            final String name,
            final Nature nature,
            final String mergedFileName,
            final DocumentTypeData supports,
            final DocumentTypeData coverNote,
            final DocumentTemplateData[] documentTemplateData
    ) {
        this.ref = ref;
        this.name = name;
        this.mergedFileName = mergedFileName;
        this.coverNote = coverNote;
        this.supports = supports;
        this.nature = nature;
        documentTemplateDataByPath = Arrays.stream(documentTemplateData)
                                           .collect(Collectors.toMap(DocumentTemplateData::getAtPath, dtd -> dtd));
    }

    /**
     * A {@link Predicate} for {@link DocumentTemplate}, evaluating true if the provided {@link DocumentTemplate} has
     * a {@link DocumentType} corresponding to this {@link DocumentTypeData}.
     */
    public Predicate<DocumentTemplate> ofTemplate() {
        return template -> {
            if(template == null) {
                return false;
            }
            final DocumentType type = template.getType();
            return type != null && Objects.equals(type.getReference(), getRef());
        };
    }

    public DocumentType findUsing(final DocumentTypeRepository documentTypeRepository) {
        return documentTypeRepository.findByReference(getRef());
    }

    /**
     * As for {@link #findUsing(DocumentTypeRepository)}, but also caches results using supplied {@link QueryResultsCache}.
     */
    public DocumentType findUsing(
            final DocumentTypeRepository documentTypeRepository,
            final QueryResultsCache queryResultsCache) {
        return queryResultsCache.execute(
                () -> findUsing(documentTypeRepository),
                DocumentTypeData.class,
                "findUsing", this);

    }

    public boolean isDocTypeFor(final DocumentAbstract<?> document) {
        return isDocTypeFor(document.getType());
    }

    private boolean isDocTypeFor(final DocumentType documentType) {
        return ref.equals(documentType.getReference());
    }

    /**
     * For testing, primarily.
     */
    public DocumentType create() {
        return new DocumentType(getRef(), getName());
    }


    private static final Collection<DocumentTypeData> PRIMARY_DOC_TYPES =
            Collections.unmodifiableList(Lists.newArrayList(
                    PRELIM_LETTER,
                    INVOICE
            ));

    private static final Collection<DocumentTypeData> SUPPORTING_DOC_TYPES =
            Collections.unmodifiableList(Lists.newArrayList(
                    TAX_REGISTER,
                    SUPPLIER_RECEIPT,
                    SPECIAL_COMMUNICATION,
                    CALCULATION ));

    private static final Collection<String> PRIMARY_DOC_TYPE_REFS =
            FluentIterable.from(PRIMARY_DOC_TYPES).transform(DocumentTypeData::getRef).toList();

    /**
     * These doc types are the ones that primary in that they are not attached to other documents (not supporting).
     */
    public static boolean isPrimaryType(final Document candidateDocument) {
        final String docTypeRef = candidateDocument.getType().getReference();
        return PRIMARY_DOC_TYPE_REFS.contains(docTypeRef);
    }

    /**
     * These {@link DocumentType} are the ones that support PLs and invoices.
     */
    public static List<DocumentType> supportingDocTypesUsing(
            final DocumentTypeRepository documentTypeRepository,
            final QueryResultsCache queryResultsCache) {
        return Lists.newArrayList(FluentIterable
                        .from(SUPPORTING_DOC_TYPES)
                        .transform(dtd -> dtd.findUsing(documentTypeRepository, queryResultsCache))
                        .toList()
        );
    }


    /**
     * Obtain the {@link DocumentType} to use as the cover note for the supplied {@link Document} (else null).
     */
    public static DocumentType coverNoteTypeFor(
            final Document document,
            final DocumentTypeRepository documentTypeRepository,
            final QueryResultsCache queryResultsCache) {
        final DocumentTypeData documentTypeData = coverNoteFor(document);
        return documentTypeData != null
                ? documentTypeData.findUsing(documentTypeRepository, queryResultsCache)
                : null;
    }

    public static DocumentTypeData docTypeDataFor(final Document document) {
        if(document == null) {
            return null;
        }
        for (DocumentTypeData candidate : values()) {
            if (candidate.isDocTypeFor(document)) {
                return candidate;
            }
        }
        return null;
    }

    public static DocumentTypeData supportedBy(final DocumentType documentType) {
        DocumentTypeData value = reverseLookup(documentType);
        return value.getSupports();
    }

    /**
     * Returns the {@link DocumentTypeData} whose {@link DocumentTypeData#getRef()} corresponds to
     * {@link DocumentType#getReference() that} of the supplied {@link DocumentType}.
     */
    public static DocumentTypeData reverseLookup(final DocumentType documentType) {
        DocumentTypeData[] values = values();
        for (DocumentTypeData value : values) {
            if(value.isDocTypeFor(documentType)) {
                return value;
            }
        }
        throw new IllegalArgumentException(
                String.format("Could not locate any DocumentTypeData corresponding to '%s'", documentType));
    }

    static DocumentTypeData coverNoteFor(final Document document) {
        DocumentTypeData[] values = values();
        for (DocumentTypeData value : values) {
            if(value.isDocTypeFor(document)) {
                return value.getCoverNote();
            }
        }
        return null;
    }

    /**
     * The {@link DocumentTypeData} that {@link DocumentTypeData#getSupports() support} the provided {@link DocumentTypeData}.
     */
    public static List<DocumentTypeData> supports(final DocumentTypeData documentTypeData) {
        DocumentTypeData[] values = values();
        return FluentIterable
                        .of(values)
                        .filter(x -> x.getSupports() == documentTypeData || documentTypeData == null)
                        .toList();
    }

    /**
     * Performs a bulk {@link #findUsing(DocumentTypeRepository) lookup}.
     */
    public static List<DocumentType> findUsing(
            final List<DocumentTypeData> dataList,
            final DocumentTypeRepository documentTypeRepository, final QueryResultsCache queryResultsCache) {
        return Lists.newArrayList(
                FluentIterable
                        .from(dataList)
                        .transform(x -> x.findUsing(documentTypeRepository, queryResultsCache))
                        .toList()
        );
    }

    public static boolean hasIncomingType(final DocumentAbstract<?> document) {
        return hasNatureOf(document, Nature.INCOMING);
    }

    static boolean hasNatureOf(final DocumentAbstract<?> document, final Nature nature) {
        final List<DocumentTypeData> types = DocumentTypeData.natureOf(nature);
        for (DocumentTypeData documentTypeData : types) {
            if(documentTypeData.isDocTypeFor(document)) {
                return true;
            }
        }
        return false;
    }


    public static List<DocumentTypeData> natureOf(final Nature nature) {
        List<DocumentTypeData> result = Lists.newArrayList();
        for (DocumentTypeData documentTypeData : values()) {
            if(documentTypeData.getNature() == nature) {
                result.add(documentTypeData);
            }
        }
        return ImmutableList.copyOf(result);
    }


}
