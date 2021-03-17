<@compress single_line=true>
    <#if (leaseType.reference == "AD") ||
(leaseType.reference == "OA") ||
(leaseType.reference == "PA") ||
(leaseType.reference == "SA")
> Contratto di affitto di ramo d'azienda </#if>
<#if (leaseType.reference == "CO") ||
(leaseType.reference == "CG")
>Contratto di commodato </#if>
<#if (leaseType.reference == "DH") ||
(leaseType.reference == "PP")
>Contratto di concessione </#if>
<#if (leaseType.reference == "LO") ||
(leaseType.reference == "OL") ||
(leaseType.reference == "PL") ||
(leaseType.reference == "SL") ||
(leaseType.reference == "AA")
>Contratto di locazione </#if>
<#if (leaseType.reference == "PR")> Contratto di locazione di spazio </#if>

<#if leaseTenancyStartDate?? >
con effetto dal ${leaseTenancyStartDate?string["dd-MM-yyyy"]}<#rt>
</#if>
<#if currentOccupancyBrandName?? > - Insegna: ${currentOccupancyBrandName}
</#if>
    <#if unitName?? >

        <#if (leaseType.reference == "AD") ||
    (leaseType.reference == "OA") ||
    (leaseType.reference == "PA") ||
    (leaseType.reference == "SA")
    >
    - Esercizio Commerciale<#rt>
    </#if>
    <#if (leaseType.reference == "CG") ||
    (leaseType.reference == "CO") ||
    (leaseType.reference == "LO") ||
    (leaseType.reference == "PR") ||
    (leaseType.reference == "OL") ||
    (leaseType.reference == "PL") ||
    (leaseType.reference == "SL")
    >
    - Unità <#rt>
    </#if>
    <#if (leaseType.reference == "DH")
    >
    - Spazio Commerciale<#rt>
    </#if>
    ${unitName}</#if>
</@compress>