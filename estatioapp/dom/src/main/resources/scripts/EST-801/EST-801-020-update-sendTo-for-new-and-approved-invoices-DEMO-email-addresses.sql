
--
-- this is just for demo purposes; none of these email addresses have been nominated as the 'official' email address.
--
begin tran


with invoice_sendTo_emailAddress_DEMO as
(select ROW_NUMBER() over (order by i.id) as num , i.id as invoiceId, cc.id as communicationChannelId
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
,every_other as
(
 select invoiceId, communicationChannelId from invoice_sendTo_emailAddress_DEMO where num % 2 = 0
)
update dbo.Invoice
   set sendToCommunicationChannelId = demo.communicationChannelId
  from every_other demo
 where dbo.Invoice.id = demo.invoiceId


select cc.type, count(i.id)
  from dbo.Invoice i
  left join dbo.CommunicationChannel cc
    on i.sendToCommunicationChannelId = cc.id
 where i.status in ('NEW', 'APPROVED')
 group by cc.type

 commit tran

