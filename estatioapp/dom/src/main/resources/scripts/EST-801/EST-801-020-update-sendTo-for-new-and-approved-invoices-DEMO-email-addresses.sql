
--
-- this is just for demo purposes; none of these email addresses have been nominated as the 'official' email address.
--
begin tran

with invoice_sendTo_emailAddress_DEMO as
(select i.id as invoiceId, cc.id as communicationChannelId
  from dbo.Invoice i
  join dbo.Lease l
    on i.leaseId = l.id
  join dbo.AgreementRole ar
    on l.id = ar.agreementId
   and ar.typeId = (select art.id from dbo.AgreementRoleType art where art.title = 'Tenant')
  join dbo.Party p
    on ar.partyId = p.id
  join dbo.CommunicationChannelOwnerLinkForParty ccolfp
    on ccolfp.partyId = p.id
  join dbo.CommunicationChannelOwnerLink ccol
    on ccolfp.id = ccol.id
  join dbo.CommunicationChannel cc
    on ccol.communicationChannelId = cc.id
   and ccol.communicationChannelType = 'EMAIL_ADDRESS'
 where i.status in ('NEW', 'APPROVED')
)
update dbo.Invoice
   set sendToCommunicationChannelId = demo.communicationChannelId
  from invoice_sendTo_emailAddress_DEMO demo
 where dbo.Invoice.id = demo.invoiceId

 commit tran

