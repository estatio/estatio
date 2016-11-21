with invoice_sendTo as
(
    select i.id as invoiceId, cc.id as communicationChannelId
      from dbo.Invoice i
      join dbo.Lease l
        on i.leaseId = l.id
      join dbo.AgreementRole ar
        on l.id = ar.agreementId
       and ar.typeId = (select art.id from dbo.AgreementRoleType art where art.title = 'Tenant')
      left join dbo.AgreementRoleCommunicationChannel arcc
        on ar.id = arcc.agreementRoleId
       and arcc.typeId = (select arcct.id from dbo.AgreementRoleCommunicationChannelType arcct where arcct.title = 'Invoice Address')
      left join dbo.CommunicationChannel cc
        on arcc.communicationChannelId = cc.id
     where i.status in ('NEW', 'APPROVED')
 )
update dbo.Invoice
   set sendToCommunicationChannelId = invoice_sendTo.communicationChannelId
  from invoice_sendTo
 where dbo.Invoice.id = invoice_sendTo.invoiceId