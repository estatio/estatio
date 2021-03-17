select i."id" as invoiceId, c."transactionId" as smTransactionId, c."timestamp" as smTimestamp, 'dummy' as smMessage
 from "dbo"."Invoice" i
  left join "isiscommand"."Command" c
         on c."transactionId" is null
where i."id" = :invoiceId
