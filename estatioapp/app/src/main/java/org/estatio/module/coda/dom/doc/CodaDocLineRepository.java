package org.estatio.module.coda.dom.doc;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDateTime;

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
    public List<CodaDocLine> findWithInvalidAccountCodes() {
        return findByHandlingAndAccountCodeValidationStatus(
                Handling.INCLUDE, ValidationStatus.INVALID);
    }

    List<CodaDocLine> findByHandlingAndAccountCodeValidationStatus(
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
    public List<CodaDocLine> findWithInvalidEl3s() {
        return findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl3ValidationStatus(
                Handling.INCLUDE, ValidationStatus.VALID, ValidationStatus.INVALID);
    }

    @Programmatic
    public List<CodaDocLine> findWithInvalidEl3(final String accountCodeEl3) {
        return findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl3ValidationStatusAndAccountCodeEl3(
                Handling.INCLUDE, ValidationStatus.VALID, ValidationStatus.INVALID, accountCodeEl3);
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
    public List<CodaDocLine> findWithInvalidEl5s() {
        return findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl5ValidationStatus(
                Handling.INCLUDE, ValidationStatus.VALID, ValidationStatus.INVALID);
    }

    @Programmatic
    public List<CodaDocLine> findWithInvalidEl5(final String accountCodeEl5) {
        return findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl5ValidationStatusAndAccountCodeEl5(
                Handling.INCLUDE, ValidationStatus.VALID, ValidationStatus.INVALID, accountCodeEl5);
    }

    List<CodaDocLine> findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl5ValidationStatus(
            final Handling handling,
            final ValidationStatus accountCodeValidationStatus,
            final ValidationStatus accountCodeEl5ValidationStatus) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocLine.class,
                        "findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl5ValidationStatusAndAccountCodeEl5",
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
    public List<CodaDocLine> findWithInvalidEl6s() {
        return findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl6ValidationStatus(
                Handling.INCLUDE, ValidationStatus.VALID, ValidationStatus.INVALID);
    }

    @Programmatic
    public List<CodaDocLine> findWithInvalidEl6(final String accountCodeEl6) {
        return findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl6ValidationStatusAndAccountCodeEl6(
                Handling.INCLUDE, ValidationStatus.VALID, ValidationStatus.INVALID, accountCodeEl6);
    }

    List<CodaDocLine> findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl6ValidationStatus(
            final Handling handling,
            final ValidationStatus accountCodeValidationStatus,
            final ValidationStatus accountCodeEl6ValidationStatus) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocLine.class,
                        "findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl6ValidationStatusAndAccountCodeEl6",
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
    public List<CodaDocLine> findWithInvalidSupplierBankAccounts() {
        return findByHandlingAndAccountCodeValidationStatusAndSupplierBankAccountValidationStatus(
                Handling.INCLUDE, ValidationStatus.VALID, ValidationStatus.INVALID);
    }

    @Programmatic
    public List<CodaDocLine> findWithInvalidSupplierBankAccount(final String supplierBankAccount) {
        return findByHandlingAndAccountCodeValidationStatusAndSupplierBankAccountValidationStatusAndElmBankAccount(
                Handling.INCLUDE, ValidationStatus.VALID, ValidationStatus.INVALID, supplierBankAccount);
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
    public List<CodaDocLine> findWithInvalidExtRef() {
        return findByHandlingAndExtRefValidationStatus(
                Handling.INCLUDE, ValidationStatus.INVALID);
    }

    List<CodaDocLine> findByHandlingAndExtRefValidationStatus(
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
    public List<CodaDocLine> findWithInvalidOrder(final String extRefOrder) {
        return findByHandlingAndExtRefValidationStatusAndExtRefOrderValidationStatusAndExtRefOrder(
                    Handling.INCLUDE, ValidationStatus.VALID, ValidationStatus.INVALID, extRefOrder);
    }

    @Programmatic
    public List<CodaDocLine> findWithInvalidOrders() {
        return findByHandlingAndExtRefValidationStatusAndExtRefOrderValidationStatus(
                    Handling.INCLUDE, ValidationStatus.VALID, ValidationStatus.INVALID);
    }

    List<CodaDocLine> findByHandlingAndExtRefValidationStatusAndExtRefOrderValidationStatusAndExtRefOrder(
            final Handling handling,
            final ValidationStatus extRefValidationStatus,
            final ValidationStatus extRefOrderValidationStatus,
            final String extRefOrder) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocLine.class,
                        "findByHandlingAndExtRefValidationStatusAndExtRefOrderValidationStatusAndExtRefOrder",
                        "handling", handling,
                        "extRefValidationStatus", extRefValidationStatus,
                        "extRefOrderValidationStatus", extRefOrderValidationStatus,
                        "extRefOrder", extRefOrder
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
    public List<CodaDocLine> findWithInvalidExtRefProperty(final String extRefProperty) {
        return findByHandlingAndExtRefValidationStatusAndExtRefPropertyValidationStatusAndExtRefProperty(
                Handling.INCLUDE, ValidationStatus.VALID, ValidationStatus.INVALID, extRefProperty);
    }

    @Programmatic
    public List<CodaDocLine> findWithInvalidProperties() {
        return findByHandlingAndExtRefValidationStatusAndExtRefPropertyValidationStatus(
                Handling.INCLUDE, ValidationStatus.VALID, ValidationStatus.INVALID);
    }


    List<CodaDocLine> findByHandlingAndExtRefValidationStatusAndExtRefPropertyValidationStatusAndExtRefProperty(
            final Handling handling,
            final ValidationStatus extRefValidationStatus,
            final ValidationStatus extRefPropertyValidationStatus,
            final String extRefProperty) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocLine.class,
                        "findByHandlingAndExtRefValidationStatusAndExtRefPropertyValidationStatusAndExtRefProperty",
                        "handling", handling,
                        "extRefValidationStatus", extRefValidationStatus,
                        "extRefPropertyValidationStatus", extRefPropertyValidationStatus,
                        "extRefProperty", extRefProperty
                ));
    }

    List<CodaDocLine> findByHandlingAndExtRefValidationStatusAndExtRefPropertyValidationStatus(
            final Handling handling,
            final ValidationStatus extRefValidationStatus,
            final ValidationStatus extRefPropertyValidationStatus) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocLine.class,
                        "handling", handling,
                        "findByHandlingAndExtRefValidationStatusAndExtRefPropertyValidationStatus",
                        "extRefValidationStatus", extRefValidationStatus,
                        "extRefPropertyValidationStatus", extRefPropertyValidationStatus
                ));
    }


    @Programmatic
    public List<CodaDocLine> findWithInvalidExtRefProject(final String extRefProject) {
        return findByHandlingAndExtRefValidationStatusAndExtRefProjectValidationStatusAndExtRefProject(
                    Handling.INCLUDE, ValidationStatus.VALID, ValidationStatus.INVALID, extRefProject);
    }

    @Programmatic
    public List<CodaDocLine> findWithInvalidExtRefProjects() {
        return findByHandlingAndExtRefValidationStatusAndExtRefProjectValidationStatus(
                    Handling.INCLUDE, ValidationStatus.VALID, ValidationStatus.INVALID);
    }


    List<CodaDocLine> findByHandlingAndExtRefValidationStatusAndExtRefProjectValidationStatusAndExtRefProject(
            final Handling handling,
            final ValidationStatus extRefValidationStatus,
            final ValidationStatus extRefProjectValidationStatus,
            final String extRefProject) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocLine.class,
                        "findByHandlingAndExtRefValidationStatusAndExtRefProjectValidationStatusAndExtRefProject",
                        "handling", handling,
                        "extRefValidationStatus", extRefValidationStatus,
                        "extRefProjectValidationStatus", extRefProjectValidationStatus,
                        "extRefProject", extRefProject
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
    public List<CodaDocLine> findWithInvalidExtRefWorkType(final String extRefWorkType) {
        return findByHandlingAndExtRefValidationStatusAndExtRefWorkTypeValidationStatusAndExtRefWorkType(
                Handling.INCLUDE, ValidationStatus.VALID, ValidationStatus.INVALID, extRefWorkType);
    }

    @Programmatic
    public List<CodaDocLine> findWithInvalidExtRefWorkTypes() {
        return findByHandlingAndExtRefValidationStatusAndExtRefWorkTypeValidationStatus(
                Handling.INCLUDE, ValidationStatus.VALID, ValidationStatus.INVALID);
    }

    List<CodaDocLine> findByHandlingAndExtRefValidationStatusAndExtRefWorkTypeValidationStatusAndExtRefWorkType(
            final Handling handling,
            final ValidationStatus extRefValidationStatus,
            final ValidationStatus extRefWorkTypeValidationStatus,
            final String extRefWorkType) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocLine.class,
                        "findByHandlingAndExtRefValidationStatusAndExtRefWorkTypeValidationStatusAndExtRefWorkType",
                        "handling", handling,
                        "extRefValidationStatus", extRefValidationStatus,
                        "extRefWorkTypeValidationStatus", extRefWorkTypeValidationStatus,
                        "extRefWorkType", extRefWorkType
                ));
    }

    List<CodaDocLine> findByHandlingAndExtRefValidationStatusAndExtRefWorkTypeValidationStatus(
            final Handling handling,
            final ValidationStatus extRefValidationStatus,
            final ValidationStatus extRefWorkTypeValidationStatus) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocLine.class,
                        "handling", handling,
                        "findByHandlingAndExtRefValidationStatusAndExtRefWorkTypeValidationStatus",
                        "extRefValidationStatus", extRefValidationStatus,
                        "extRefWorkTypeValidationStatus", extRefWorkTypeValidationStatus
                ));
    }

    @Programmatic
    public List<CodaDocLine> findWithInvalidMediaCode(final String mediaCode) {
        return findByHandlingAndMediaCodeValidationStatusAndMediaCode(
                Handling.INCLUDE, ValidationStatus.INVALID, mediaCode);
    }

    @Programmatic
    public List<CodaDocLine> findWithInvalidMediaCodes() {
        return findByHandlingAndMediaCodeValidationStatus(
                Handling.INCLUDE, ValidationStatus.INVALID);
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
            final String accountCode,
            final String description,
            final BigDecimal docValue,
            final BigDecimal docSumTax,
            final LocalDateTime valueDate,
            final String extRef3,
            final String extRef5,
            final String elmBankAccount,
            final String userRef1,
            final Character userStatus,
            final String mediaCode) {
        return repositoryService.persist(
                new CodaDocLine(docHead, lineNum, accountCode, description,
                        docValue, docSumTax, valueDate, extRef3,
                        extRef5, elmBankAccount, userRef1, userStatus, mediaCode));
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

    @Programmatic
    public CodaDocLine upsert(
            final CodaDocHead docHead,
            final int lineNum,
            final String accountCode,
            final String description,
            final BigDecimal docValue,
            final BigDecimal docSumTax,
            final LocalDateTime valueDate,
            final String extRef3,
            final String extRef5,
            final String elmBankAccount,
            final String userRef1,
            final Character userStatus,
            final String mediaCode) {

        CodaDocLine docLine = findByDocHeadAndLineNum(docHead, lineNum);
        if(docLine == null) {
            return create(docHead, lineNum, accountCode, description, docValue, docSumTax, valueDate, extRef3, extRef5,
                    elmBankAccount, userRef1, userStatus, mediaCode);
        } else {
            docLine.setAccountCode(accountCode);
            docLine.setDescription(description);
            docLine.setDocValue(docValue);
            docLine.setDocSumTax(docSumTax);
            docLine.setValueDate(valueDate);
            docLine.setExtRef3(extRef3);
            docLine.setExtRef5(extRef5);
            docLine.setElmBankAccount(elmBankAccount);
            docLine.setUserRef1(userRef1);
            docLine.setUserStatus(userStatus);
        }
        return docLine;
    }

    @Inject
    RepositoryService repositoryService;

}
