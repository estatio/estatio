package org.estatio.capex.dom.payment;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.tablecol.TableColumnOrderService;

import org.estatio.dom.currency.Currency;
import org.estatio.module.bankaccount.dom.BankAccount;
import org.estatio.module.party.dom.Party;

import iso.std.iso._20022.tech.xsd.pain_001_001.ActiveOrHistoricCurrencyAndAmount;
import iso.std.iso._20022.tech.xsd.pain_001_001.AmountType3Choice;
import iso.std.iso._20022.tech.xsd.pain_001_001.CreditTransferTransactionInformation10;
import iso.std.iso._20022.tech.xsd.pain_001_001.PartyIdentification32;
import iso.std.iso._20022.tech.xsd.pain_001_001.PaymentIdentification1;
import iso.std.iso._20022.tech.xsd.pain_001_001.PostalAddress6;
import iso.std.iso._20022.tech.xsd.pain_001_001.RemittanceInformation5;
import lombok.Getter;
import lombok.Setter;

@javax.xml.bind.annotation.XmlRootElement(name = "creditTransfer")
@javax.xml.bind.annotation.XmlType(
        propOrder = {
                "batch",
                "endToEndId",
                "currency",
                "amount",
                "sellerBankAccount",
                "seller",
                "lines",
                "sellerPostalAddressCountry",
                "remittanceInformation",
        }
)
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
@DomainObject(
        editing = Editing.DISABLED
)
@Getter @Setter
public class CreditTransfer  {

    public String title() {
        return String.format("%s %s:  %s -> %s",
                getCurrency().getReference(),
                new DecimalFormat("0.00").format(getAmount()),
                getBatch().getDebtorBankAccount().getIban(),
                getSellerIban());
    }

    private PaymentBatch batch;

    private String endToEndId;

    private BigDecimal amount;

    private BankAccount sellerBankAccount;

    private Currency currency;


    String getSellerBic() {
        return getSellerBankAccount().getBic();
    }
    String getSellerIban() {
        return getSellerBankAccount().getIban();
    }

    private Party seller;

    String getSellerName() {
        return getSeller().getName();
    }


    private String sellerPostalAddressCountry;
    @Programmatic
    public String getSellerPostalAddressCountry() {
        return sellerPostalAddressCountry;
    }

    private String remittanceInformation;


    @XmlElementWrapper
    @XmlElement(name = "lines")
    @Collection()
    @Getter @Setter
    private List<PaymentLine> lines = Lists.newArrayList();


    @Programmatic
    public CreditTransferTransactionInformation10 asXml() {

        CreditTransferTransactionInformation10 cdtTrfTxInf = new CreditTransferTransactionInformation10();

        PaymentIdentification1 pmtId = new PaymentIdentification1();
        cdtTrfTxInf.setPmtId(pmtId);
        pmtId.setEndToEndId(getEndToEndId());

        AmountType3Choice amt = new AmountType3Choice();
        cdtTrfTxInf.setAmt(amt);
        ActiveOrHistoricCurrencyAndAmount instdAmt = new ActiveOrHistoricCurrencyAndAmount();
        amt.setInstdAmt(instdAmt);
        instdAmt.setCcy(getCurrency().getReference().trim());
        instdAmt.setValue(getAmount());

        BankAccount creditorBankAccount = this.getSellerBankAccount();

        cdtTrfTxInf.setCdtrAgt(PaymentBatch.agentFor(creditorBankAccount));
        cdtTrfTxInf.setCdtrAcct(PaymentBatch.cashAccountFor(creditorBankAccount));

        PartyIdentification32 cdtr = new PartyIdentification32();
        cdtTrfTxInf.setCdtr(cdtr);
        cdtr.setNm(getSeller().getName());
        PostalAddress6 pstlAdr = new PostalAddress6();
        cdtr.setPstlAdr(pstlAdr);
        pstlAdr.setCtry(PaymentBatch.ctryFor(getSeller()));

        RemittanceInformation5 rmtInf = new RemittanceInformation5();
        cdtTrfTxInf.setRmtInf(rmtInf);
        List<String> ustrdList = rmtInf.getUstrds();
        ustrdList.add(getRemittanceInformation());

        return cdtTrfTxInf;
    }

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class TableColumnOrderServiceForCreditTransfer implements TableColumnOrderService {

        @Override
        public List<String> orderParented(
                final Object parent,
                final String collectionId,
                final Class<?> collectionType,
                final List<String> propertyIds) {
            if(parent instanceof PaymentBatch && CreditTransfer.class.isAssignableFrom(collectionType)) {
                return Lists.newArrayList(
                        "endToEndId",
                        "seller",
                        "sellerBankAccount",
                        "currency",
                        "amount",
                        "remittanceInformation"
                );
            }
            return null;
        }

        @Override
        public List<String> orderStandalone(final Class<?> collectionType, final List<String> propertyIds) {
            return null;
        }
    }

}
