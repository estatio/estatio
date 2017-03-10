<b>OGGETTO</b>:<#t>
 ${leasePropertyName}<#rt>
 /  <#rt>
<#t>
<#t>
<#if currentOccupancyUnitReferenceNotV ><#t>
    <#t>
    <#if (leaseType.reference == "AD") ||
         (leaseType.reference == "OA") ||
         (leaseType.reference == "PA") ||
         (leaseType.reference == "SA")
    ><#t>
        Esercizio Commerciale<#t>
    </#if><#t>
    <#if (leaseType.reference == "CG") ||
         (leaseType.reference == "CA") ||
         (leaseType.reference == "LO") ||
         (leaseType.reference == "PR") ||
         (leaseType.reference == "OL") ||
         (leaseType.reference == "PL") ||
         (leaseType.reference == "SL")
    ><#t>
        Unità<#t>
    </#if><#t>
    <#if (leaseType.reference == "DH")
    ><#t>
        Spazio Commerciale<#t>
    </#if><#t>
 ${currentOccupancyUnitName}<#rt>
</#if><#t>
<#t>
<#t>
 / Fatturazione ${ chargeDescriptions }.<br /><br /><#t>
Come a Voi già noto, la fatturazione relativa al ${chargeDescriptions}, verrà effettuata alla stessa data stabilita per il pagamento <#t>
 <#rt>
<#t>
<#t>
<#if (frequency == "QUARTER")
><#t>
    (1/1 - 1/4 - 1/7 - 1/10)
</#if>
<#if (frequency == "MONTH")
><#t>
    (il primo di ogni mese)
</#if>
.<br /><br /><#t>
<#t>
<#t>
<#if (paymentMethod == "DIRECT_DEBIT")
 ><#t>
    Pertanto, abbiamo provveduto ad inoltrare in banca la disposizione di addebito, a mezzo SEPA per l'importo di ${grossAmount} con scadenza ${dueDate?string["dd-MM-yyyy"]} così suddiviso:<#t>
</#if><#t>
<#if (paymentMethod == "BANK_TRANSFER")
 ><#t>
    Pertanto, Vi invitiamo a voler predisporre il pagamento a mezzo bonifico bancario sul conto corrente intestato alla ${sellerName} ${sellerBankAccountBankName!""} - ${sellerBankAccountIban!""} per l'importo di <b>€ ${grossAmount} </b> con scadenza ${dueDate?string["dd-MM-yyyy"]} così suddiviso:<#rt>
</#if><#t>
<#if (paymentMethod == "Q")
 ><#t>
    Pertanto, abbiamo provveduto ad inoltrare in banca la disposioze di addebito, a mezzo Rimessa Diretta per l'importo di ${grossAmount} con scadenza ${dueDate?string["dd-MM-yyyy"]} così suddiviso:<#t>
</#if><#t>
<#t>