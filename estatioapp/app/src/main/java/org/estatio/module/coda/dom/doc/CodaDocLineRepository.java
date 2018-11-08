package org.estatio.module.coda.dom.doc;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = CodaDocLine.class
)
public class CodaDocLineRepository {

    @Programmatic
    public List<CodaDocLine> listAll() {
        return repositoryService.allInstances(CodaDocLine.class);
    }

    @Programmatic
    public CodaDocLine findByDocHeadAndLineNum(
            final CodaDocHead docHead,
            final int lineNum
    ) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        CodaDocLine.class,
                        "findByDocHeadAndLineNum",
                        "docHead", docHead,
                        "lineNum", lineNum));
    }

    @Programmatic
    public List<CodaDocLine> findByHandlingAndAccountCodeValidationStatus(
            final Handling handling,
            final ValidationStatus accountCodeValidationStatus) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocLine.class,
                        "findByHandlingAndAccountCodeValidationStatus",
                        "handling", handling,
                        "accountCodeValidationStatus", accountCodeValidationStatus
                ));
    }

    @Programmatic
    public List<CodaDocLine> findWithInvalidEl3s(final Handling handling) {
        return findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl3ValidationStatus(
                handling, ValidationStatus.VALID, ValidationStatus.INVALID);
    }

    @Programmatic
    public List<CodaDocLine> findWithInvalidEl3(
            final Handling handling,
            final ValidationStatus accountCodeValidationStatus,
            final String accountCodeEl3) {
        return findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl3ValidationStatusAndAccountCodeEl3(
                handling, accountCodeValidationStatus, ValidationStatus.INVALID, accountCodeEl3);
    }

    List<CodaDocLine> findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl3ValidationStatus(
            final Handling handling,
            final ValidationStatus accountCodeValidationStatus,
            final ValidationStatus accountCodeEl3ValidationStatus) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocLine.class,
                        "findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl3ValidationStatus",
                        "handling", handling,
                        "accountCodeValidationStatus", accountCodeValidationStatus,
                        "accountCodeEl3ValidationStatus", accountCodeEl3ValidationStatus
                ));
    }

    List<CodaDocLine> findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl3ValidationStatusAndAccountCodeEl3(
            final Handling handling,
            final ValidationStatus accountCodeValidationStatus,
            final ValidationStatus accountCodeEl3ValidationStatus,
            final String accountCodeEl3) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocLine.class,
                        "findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl3ValidationStatusAndAccountCodeEl3",
                        "handling", handling,
                        "accountCodeValidationStatus", accountCodeValidationStatus,
                        "accountCodeEl3ValidationStatus", accountCodeEl3ValidationStatus,
                        "accountCodeEl3", accountCodeEl3
                ));
    }

    @Programmatic
    public List<CodaDocLine> findWithInvalidEl5s(final Handling handling) {
        return findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl5ValidationStatus(
                handling, ValidationStatus.VALID, ValidationStatus.INVALID);
    }

    @Programmatic
    public List<CodaDocLine> findWithInvalidEl5(
            final Handling handling,
            final ValidationStatus accountCodeValidationStatus,
            final String accountCodeEl5) {
        return findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl5ValidationStatusAndAccountCodeEl5(
                handling, accountCodeValidationStatus, ValidationStatus.INVALID, accountCodeEl5);
    }

    List<CodaDocLine> findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl5ValidationStatus(
            final Handling handling,
            final ValidationStatus accountCodeValidationStatus,
            final ValidationStatus accountCodeEl5ValidationStatus) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocLine.class,
                        "findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl5ValidationStatus",
                        "handling", handling,
                        "accountCodeValidationStatus", accountCodeValidationStatus,
                        "accountCodeEl5ValidationStatus", accountCodeEl5ValidationStatus
                ));
    }

    List<CodaDocLine> findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl5ValidationStatusAndAccountCodeEl5(
            final Handling handling,
            final ValidationStatus accountCodeValidationStatus,
            final ValidationStatus accountCodeEl5ValidationStatus,
            final String accountCodeEl5) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocLine.class,
                        "findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl5ValidationStatusAndAccountCodeEl5",
                        "handling", handling,
                        "accountCodeValidationStatus", accountCodeValidationStatus,
                        "accountCodeEl5ValidationStatus", accountCodeEl5ValidationStatus,
                        "accountCodeEl5", accountCodeEl5
                ));
    }

    @Programmatic
    public List<CodaDocLine> findWithInvalidEl6s(final Handling handling) {
        return findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl6ValidationStatus(
                handling, ValidationStatus.VALID, ValidationStatus.INVALID);
    }

    @Programmatic
    public List<CodaDocLine> findWithInvalidEl6(
            final Handling handling,
            final ValidationStatus accountCodeValidationStatus,
            final String accountCodeEl6) {
        return findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl6ValidationStatusAndAccountCodeEl6(
                handling, accountCodeValidationStatus, ValidationStatus.INVALID, accountCodeEl6);
    }

    List<CodaDocLine> findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl6ValidationStatus(
            final Handling handling,
            final ValidationStatus accountCodeValidationStatus,
            final ValidationStatus accountCodeEl6ValidationStatus) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocLine.class,
                        "findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl6ValidationStatus",
                        "handling", handling,
                        "accountCodeValidationStatus", accountCodeValidationStatus,
                        "accountCodeEl6ValidationStatus", accountCodeEl6ValidationStatus
                ));
    }

    List<CodaDocLine> findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl6ValidationStatusAndAccountCodeEl6(
            final Handling handling,
            final ValidationStatus accountCodeValidationStatus,
            final ValidationStatus accountCodeEl6ValidationStatus,
            final String accountCodeEl6) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocLine.class,
                        "findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl6ValidationStatusAndAccountCodeEl6",
                        "handling", handling,
                        "accountCodeValidationStatus", accountCodeValidationStatus,
                        "accountCodeEl6ValidationStatus", accountCodeEl6ValidationStatus,
                        "accountCodeEl6", accountCodeEl6
                ));
    }

    @Programmatic
    public List<CodaDocLine> findWithInvalidSupplierBankAccounts(final Handling handling) {
        return findByHandlingAndAccountCodeValidationStatusAndSupplierBankAccountValidationStatus(
                handling, ValidationStatus.VALID, ValidationStatus.INVALID);
    }

    @Programmatic
    public List<CodaDocLine> findWithInvalidSupplierBankAccount(
            final Handling handling,
            final ValidationStatus accountCodeValidationStatus,
            final String supplierBankAccount) {
        return findByHandlingAndAccountCodeValidationStatusAndSupplierBankAccountValidationStatusAndElmBankAccount(
                handling, accountCodeValidationStatus, ValidationStatus.INVALID, supplierBankAccount);
    }

    List<CodaDocLine> findByHandlingAndAccountCodeValidationStatusAndSupplierBankAccountValidationStatus(
            final Handling handling,
            final ValidationStatus accountCodeValidationStatus,
            final ValidationStatus supplierBankAccountValidationStatus) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocLine.class,
                        "findByHandlingAndAccountCodeValidationStatusAndSupplierBankAccountValidationStatus",
                        "handling", handling,
                        "accountCodeValidationStatus", accountCodeValidationStatus,
                        "supplierBankAccountValidationStatus", supplierBankAccountValidationStatus
                ));
    }

    List<CodaDocLine> findByHandlingAndAccountCodeValidationStatusAndSupplierBankAccountValidationStatusAndElmBankAccount(
            final Handling handling,
            final ValidationStatus accountCodeValidationStatus,
            final ValidationStatus supplierBankAccountValidationStatus,
            final String elmBankAccount) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocLine.class,
                        "findByHandlingAndAccountCodeValidationStatusAndSupplierBankAccountValidationStatusAndElmBankAccount",
                        "handling", handling,
                        "accountCodeValidationStatus", accountCodeValidationStatus,
                        "supplierBankAccountValidationStatus", supplierBankAccountValidationStatus,
                        "elmBankAccount", elmBankAccount
                ));
    }

    @Programmatic
    public List<CodaDocLine> findByHandlingAndExtRefValidationStatus(
            final Handling handling,
            final ValidationStatus extRefValidationStatus) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocLine.class,
                        "findByHandlingAndExtRefValidationStatus",
                        "handling", handling,
                        "extRefValidationStatus", extRefValidationStatus
                ));
    }

    @Programmatic
    public List<CodaDocLine> findWithInvalidOrderNumber(
            final Handling handling,
            final ValidationStatus extRefValidationStatus,
            final String orderNumber) {
        return findByHandlingAndExtRefValidationStatusAndExtRefOrderValidationStatusAndOrderNumber(
                handling, extRefValidationStatus, ValidationStatus.INVALID, orderNumber);
    }

    @Programmatic
    public List<CodaDocLine> findWithInvalidOrderNumbers(final Handling handling) {
        return findByHandlingAndExtRefValidationStatusAndExtRefOrderValidationStatus(
                handling, ValidationStatus.VALID, ValidationStatus.INVALID);
    }

    List<CodaDocLine> findByHandlingAndExtRefValidationStatusAndExtRefOrderValidationStatusAndOrderNumber(
            final Handling handling,
            final ValidationStatus extRefValidationStatus,
            final ValidationStatus extRefOrderValidationStatus,
            final String orderNumber) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocLine.class,
                        "findByHandlingAndExtRefValidationStatusAndExtRefOrderValidationStatusAndOrderNumber",
                        "handling", handling,
                        "extRefValidationStatus", extRefValidationStatus,
                        "extRefOrderValidationStatus", extRefOrderValidationStatus,
                        "orderNumber", orderNumber
                ));
    }

    List<CodaDocLine> findByHandlingAndExtRefValidationStatusAndExtRefOrderValidationStatus(
            final Handling handling,
            final ValidationStatus extRefValidationStatus,
            final ValidationStatus extRefOrderValidationStatus) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocLine.class,
                        "findByHandlingAndExtRefValidationStatusAndExtRefOrderValidationStatus",
                        "handling", handling,
                        "extRefValidationStatus", extRefValidationStatus,
                        "extRefOrderValidationStatus", extRefOrderValidationStatus));
    }


    @Programmatic
    public List<CodaDocLine> findWithInvalidExtRefProject(
            final Handling handling,
            final ValidationStatus extRefValidationStatus,
            final String projectReference) {
        return findByHandlingAndExtRefValidationStatusAndExtRefProjectValidationStatusAndProjectReference(
                handling, extRefValidationStatus, ValidationStatus.INVALID, projectReference);
    }

    @Programmatic
    public List<CodaDocLine> findWithInvalidExtRefProjects(final Handling handling) {
        return findByHandlingAndExtRefValidationStatusAndExtRefProjectValidationStatus(
                handling, ValidationStatus.VALID, ValidationStatus.INVALID);
    }


    List<CodaDocLine> findByHandlingAndExtRefValidationStatusAndExtRefProjectValidationStatusAndProjectReference(
            final Handling handling,
            final ValidationStatus extRefValidationStatus,
            final ValidationStatus extRefProjectValidationStatus,
            final String projectReference) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocLine.class,
                        "findByHandlingAndExtRefValidationStatusAndExtRefProjectValidationStatusAndProjectReference",
                        "handling", handling,
                        "extRefValidationStatus", extRefValidationStatus,
                        "extRefProjectValidationStatus", extRefProjectValidationStatus,
                        "projectReference", projectReference
                    ));
    }

    List<CodaDocLine> findByHandlingAndExtRefValidationStatusAndExtRefProjectValidationStatus(
            final Handling handling,
            final ValidationStatus extRefValidationStatus,
            final ValidationStatus extRefProjectValidationStatus) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocLine.class,
                        "findByHandlingAndExtRefValidationStatusAndExtRefProjectValidationStatus",
                        "handling", handling,
                        "extRefValidationStatus", extRefValidationStatus,
                        "extRefProjectValidationStatus", extRefProjectValidationStatus
                ));
    }

    @Programmatic
    public List<CodaDocLine> findWithInvalidChargeReference(
            final Handling handling, final ValidationStatus extRefValidationStatus, final String chargeReference) {
        return findByHandlingAndExtRefValidationStatusAndExtRefWorkTypeValidationStatusAndChargeReference(
                handling, extRefValidationStatus, ValidationStatus.INVALID, chargeReference);
    }

    @Programmatic
    public List<CodaDocLine> findWithInvalidChargeReferences(final Handling handling) {
        return findByHandlingAndExtRefValidationStatusAndExtRefWorkTypeValidationStatus(
                handling, ValidationStatus.VALID, ValidationStatus.INVALID);
    }

    List<CodaDocLine> findByHandlingAndExtRefValidationStatusAndExtRefWorkTypeValidationStatusAndChargeReference(
            final Handling handling,
            final ValidationStatus extRefValidationStatus,
            final ValidationStatus extRefWorkTypeValidationStatus,
            final String chargeReference) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocLine.class,
                        "findByHandlingAndExtRefValidationStatusAndExtRefWorkTypeValidationStatusAndChargeReference",
                        "handling", handling,
                        "extRefValidationStatus", extRefValidationStatus,
                        "extRefWorkTypeValidationStatus", extRefWorkTypeValidationStatus,
                        "chargeReference", chargeReference
                ));
    }

    List<CodaDocLine> findByHandlingAndExtRefValidationStatusAndExtRefWorkTypeValidationStatus(
            final Handling handling,
            final ValidationStatus extRefValidationStatus,
            final ValidationStatus extRefWorkTypeValidationStatus) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocLine.class,
                        "findByHandlingAndExtRefValidationStatusAndExtRefWorkTypeValidationStatus",
                        "handling", handling,
                        "extRefValidationStatus", extRefValidationStatus,
                        "extRefWorkTypeValidationStatus", extRefWorkTypeValidationStatus
                ));
    }

    @Programmatic
    public List<CodaDocLine> findWithInvalidMediaCode(
            final Handling handling,
            final String mediaCode) {
        return findByHandlingAndMediaCodeValidationStatusAndMediaCode(
                handling, ValidationStatus.INVALID, mediaCode);
    }

    @Programmatic
    public List<CodaDocLine> findWithInvalidMediaCodes(final Handling handling) {
        return findByHandlingAndMediaCodeValidationStatus(
                handling, ValidationStatus.INVALID);
    }

    List<CodaDocLine> findByHandlingAndMediaCodeValidationStatus(
            final Handling handling,
            final ValidationStatus mediaCodeValidationStatus) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocLine.class,
                        "findByHandlingAndMediaCodeValidationStatus",
                        "handling", handling,
                        "mediaCodeValidationStatus", mediaCodeValidationStatus
                ));
    }

    List<CodaDocLine> findByHandlingAndMediaCodeValidationStatusAndMediaCode(
            final Handling handling,
            final ValidationStatus mediaCodeValidationStatus,
            final String mediaCode) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocLine.class,
                        "findByHandlingAndMediaCodeValidationStatusAndMediaCode",
                        "handling", handling,
                        "mediaCodeValidationStatus", mediaCodeValidationStatus,
                        "mediaCode", mediaCode
                ));
    }

    @Programmatic
    public CodaDocLine create(
            final CodaDocHead docHead,
            final int lineNum,
            final LineType lineType,
            final String accountCode,
            final String description,
            final BigDecimal docValue,
            final BigDecimal docSumTax,
            final LocalDate dueDate,
            final LocalDate vatRegistrationDate,
            final String extRef2,
            final String extRef3,
            final String extRef4,
            final String extRef5,
            final String elmBankAccount,
            final String userRef1,
            final Character userStatus,
            final String mediaCode) {
        return repositoryService.persist(
                new CodaDocLine(docHead, lineNum, lineType, accountCode, description,
                        docValue, docSumTax, dueDate, vatRegistrationDate, extRef2, extRef3,
                        extRef4, extRef5, elmBankAccount, userRef1, userStatus, mediaCode));
    }

    @Programmatic
    public CodaDocHead delete(
            final CodaDocHead docHead,
            final int lineNum) {
        final CodaDocLine docLine = findByDocHeadAndLineNum(docHead, lineNum);
        if(docLine != null) {
            repositoryService.removeAndFlush(docLine);
        }
        return docHead;
    }

    @Inject
    RepositoryService repositoryService;

}
