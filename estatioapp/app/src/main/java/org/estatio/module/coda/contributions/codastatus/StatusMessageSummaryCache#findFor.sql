select invoiceId, smTransactionId, smTimestamp, smMessage
 from (
select i.id             as invoiceId
      ,sm.transactionId as smTransactionId
      ,sm.timestamp     as smTimestamp
      ,sm.message       as smMessage
	  ,ROW_NUMBER ( )
    OVER ( PARTITION BY i.id ORDER BY sm.timestamp DESC ) as row_number
  from dbo.Invoice original
  join dbo.Invoice i
	on i.atPath = original.atPath
    and i.status = original.status
	and i.dueDate = original.dueDate
  left join isispublishmq.PublishedEvent pe
    on 'org.estatio.dom.invoice.Invoice:' + convert(varchar,i.id) = pe.target
   and pe.memberIdentifier in (
	'org.estatio.module.lease.dom.invoicing.InvoiceForLease$_invoice#$$()',
	'org.estatio.module.lease.dom.invoicing.InvoiceForLease$_collect#$$()'
	)
  left join isispublishmq.StatusMessage sm
    on pe.transactionId = sm.transactionId
 where original.id = :invoiceId
 ) x
 where x.row_number = 1
 order by invoiceId, smTimestamp
