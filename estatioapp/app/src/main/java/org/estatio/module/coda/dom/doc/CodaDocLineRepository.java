package org.estatio.module.coda.dom.doc;

import java.math.BigDecimal;

import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = CodaDocLine.class,
        objectType = "coda.CodaDocLineRepository"
)
public class CodaDocLineRepository {

    @Programmatic
    public java.util.List<CodaDocLine> listAll() {
        return repositoryService.allInstances(CodaDocLine.class);
    }

    @Programmatic
    public CodaDocLine findByDocHeadAndLineNum(
            final CodaDocHead docHead,
            final int lineNum
    ) {
        return repositoryService.uniqueMatch(
                new org.apache.isis.applib.query.QueryDefault<>(
                        CodaDocLine.class,
                        "findByDocHeadAndLineNum",
                        "docHead", docHead,
                        "lineNum", lineNum));
    }

    @Programmatic
    public CodaDocLine create(
            final CodaDocHead docHead,
            final int lineNum,
            final String accountCode,
            final BigDecimal docValue,
            final BigDecimal docSumTax,
            final LocalDateTime valueDate,
            final String extRef3,
            final String extRef5,
            final String elmBankAccount,
            final String userRef1,
            final Character userStatus) {
        return repositoryService.persist(
                new CodaDocLine(docHead, lineNum, accountCode, docValue,
                                docSumTax, valueDate, extRef3, extRef5,
                                elmBankAccount, userRef1, userStatus));
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
            final BigDecimal docValue,
            final BigDecimal docSumTax,
            final LocalDateTime valueDate,
            final String extRef3,
            final String extRef5,
            final String elmBankAccount,
            final String userRef1,
            final Character userStatus) {

        final CodaDocLine docLine = findByDocHeadAndLineNum(docHead, lineNum);
        if(docLine == null) {
            create(docHead, lineNum, accountCode, docValue, docSumTax, valueDate, extRef3, extRef5,
                    elmBankAccount, userRef1, userStatus);
        } else {
            docLine.setAccountCode(accountCode);
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

    @javax.inject.Inject
    RepositoryService repositoryService;

}
