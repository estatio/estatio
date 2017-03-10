<#if (leaseType.reference == "AD") ||
     (leaseType.reference == "OA") ||
     (leaseType.reference == "PA") ||
     (leaseType.reference == "SA")
><#t>
    Contratto di affitto di ramo d'azienda<#t>
</#if><#t>
<#if (leaseType.reference == "CO") ||
     (leaseType.reference == "CG")
><#t>
    Contratto di commodato<#t>
</#if><#t>
<#if (leaseType.reference == "DH") ||
     (leaseType.reference == "PP")
><#t>
     Contratto di concessione<#t>
</#if><#t>
<#if (leaseType.reference == "LO") ||
     (leaseType.reference == "OL") ||
     (leaseType.reference == "PL") ||
     (leaseType.reference == "SL") ||
     (leaseType.reference == "AA")
><#t>
     Contratto di locazione<#t>
</#if><#t>
<#if (leaseType.reference == "PR")
><#t>
    Contratto di locazione di spazio<#t>
</#if><#t>
<#t>
<#t>
<#t>
<#if currentOccupancyBrandName?? > ${currentOccupancyBrandName}</#if><#t>
<#t>
<#if leaseTenancyStartDate?? ><#t>
 con effetto dal ${leaseTenancyStartDate?string["dd-MM-yyyy"]}<#rt>
</#if><#t>
<#t>
<#t>
<#if fixedAssetDefined ><#t>
    <#if (leaseType.reference == "AD") ||
         (leaseType.reference == "OA") ||
         (leaseType.reference == "PA") ||
         (leaseType.reference == "SA")
    ><#t>
 - Esercizio Commerciale<#rt>
    </#if><#t>
    <#if (leaseType.reference == "CG") ||
         (leaseType.reference == "CO") ||
         (leaseType.reference == "LO") ||
         (leaseType.reference == "PR") ||
         (leaseType.reference == "OL") ||
         (leaseType.reference == "PL") ||
         (leaseType.reference == "SL")
    ><#t>
 - Unità<#rt>
    </#if><#t>
    <#if (leaseType.reference == "DH")
    ><#t>
 - Spazio Commerciale<#rt>
    </#if><#t>
</#if><#t>


<#t>