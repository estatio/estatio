<b>OGGETTO</b>:<#t>
 ${leasePropertyName}<#rt>
 /<#rt>
<#t>
<#t>
<#if currentOccupancyUnitReferenceNotV ><#t>
    <#t>
    <#if (leaseType.reference == "AD") ||
         (leaseType.reference == "OA") ||
         (leaseType.reference == "PA") ||
         (leaseType.reference == "SA")
    ><#t>
 Esercizio Commerciale<#rt>
    </#if><#t>
    <#if (leaseType.reference == "CG") ||
         (leaseType.reference == "CA") ||
         (leaseType.reference == "LO") ||
         (leaseType.reference == "PR") ||
         (leaseType.reference == "OL") ||
         (leaseType.reference == "PL") ||
         (leaseType.reference == "SL")
    ><#t>
 Unità<#rt>
    </#if><#t>
    <#if (leaseType.reference == "DH")
    ><#t>
Spazio Commerciale<#rt>
    </#if><#t>
 ${currentOccupancyUnitName}<#rt>
</#if><#t>
<#t>
<#t>
 / Fatturazione ${ chargeDescriptions }.<br /><br /><#rt>
Come a Voi già noto, la fatturazione relativa al ${chargeDescriptions}, verrà effettuata alla stessa data stabilita per il pagamento <#t>
 <#rt>
<#t>
<#t>
<#if (frequency == "QUARTER")
><#t>
    (1/1 - 1/4 - 1/7 - 1/10)<#t>
</#if>
<#if (frequency == "MONTH")
><#t>
    (il primo di ogni mese)<#t>
</#if>
.<br /><br /><#t>
<#t>
<#t>
<#if (paymentMethod == "DIRECT_DEBIT")
 ><#t>
    <#t>
    Pertanto, abbiamo provveduto ad inoltrare in banca la disposizione di addebito, a mezzo SEPA<#t>
 per l'importo di <b>€ ${grossAmount!"0,00"}</b> con scadenza ${dueDate?string["dd-MM-yyyy"]} così suddiviso:<#rt>
    <#t>
</#if><#t>
<#if (paymentMethod == "BANK_TRANSFER")
 ><#t>
    <#t>
    Pertanto, Vi invitiamo a voler predisporre il pagamento a mezzo bonifico bancario sul conto corrente intestato alla ${sellerName} ${sellerBankAccountBankName!""} - ${sellerBankAccountIban!""}<#t>
 per l'importo di <b>€ ${grossAmount!"0,00"}</b> con scadenza ${dueDate?string["dd-MM-yyyy"]} così suddiviso:<#rt>
    <#t>
</#if><#t>
<#if (paymentMethod == "BILLING_ACCOUNT")
 ><#t>
    <#t>
    *** NO DESCRIPTION AVAILABLE FOR PAYMENT METHOD ${paymentMethod} ***<#t>
    <#t>
</#if><#t>
<#if (paymentMethod == "CASH")
 ><#t>
    <#t>
    *** NO DESCRIPTION AVAILABLE FOR PAYMENT METHOD ${paymentMethod} ***<#t>
    <#t>
</#if><#t>
<#if (paymentMethod == "CHEQUE")
 ><#t>
    <#t>
    *** NO DESCRIPTION AVAILABLE FOR PAYMENT METHOD ${paymentMethod} ***<#t>
    <#t>
</#if><#t>
<#t>
<#t>