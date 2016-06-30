USE estatio_xxx

alter table isisaudit.AuditEntry
  add [sequence] int
go

update isisaudit.AuditEntry
   set [sequence] = 0
 where [sequence] is null
go

alter table isisaudit.AuditEntry
  ALTER COLUMN [sequence] INT NOT NULL
go

drop index AuditEntry_ak
  on isisaudit.AuditEntry
go


create index AuditEntry_ak
  on isisaudit.AuditEntry
(
   transactionId ASC,
   [sequence]    ASC,
   target        ASC,
   propertyId    ASC
)
go

/*
Warning! The maximum key length is 900 bytes. The index 'AuditEntry_ak' has maximum length of 2090 bytes. For some combination of large values, the insert/update operation will fail.
*/

ALTER TABLE [dbo].[Agreement] DROP CONSTRAINT [Agreement_reference_UNQ]
GO

--EST-707
ALTER TABLE LeaseTerm
DROP COLUMN excludedAmount, depositValue, depositType
GO

--EST-736
ALTER TABLE BankAccount
DROP COLUMN bankAccountType
GO

--EST-667
IF NOT EXISTS (SELECT *
  FROM [INFORMATION_SCHEMA].[COLUMNS]
  WHERE TABLE_NAME = 'LeaseType'
  AND COLUMN_NAME = 'atPath')
  ALTER TABLE [dbo].[LeaseType]  ADD atPath [varchar](255)
  GO
  UPDATE [dbo].[LeaseType] SET atPath = '/ITA' WHERE atPath IS NULL
  GO
  ALTER TABLE [dbo].[LeaseType] ALTER COLUMN  atPath [varchar](255) NOT NULL
  GO


GO
IF EXISTS (SELECT *
  FROM [INFORMATION_SCHEMA].[COLUMNS]
  WHERE TABLE_NAME = 'Brand'
  AND COLUMN_NAME = 'parentBrandId')
  BEGIN
  	  DROP INDEX [Brand_N49] ON [dbo].[Brand]
	  DROP INDEX [Brand_N50] ON [dbo].[Brand]
	  ALTER TABLE [dbo].[Brand] DROP CONSTRAINT [Brand_FK2]
	  ALTER TABLE [dbo].[Brand] DROP COLUMN parentBrandId
  END
  GO


 ALTER TABLE [dbo].LeaseTerm ALTER COLUMN  indexationPercentage [decimal](19,3)

 
/*
|estatio_blank.dbo.Document                                                 |endDate                                           |date      |          |          |
|estatio_blank.dbo.Document                                                 |startDate                                         |date      |          |          |
|estatio_dev.dbo.Document                                                   |endDate                                           |datetime2 |          |          |
|estatio_dev.dbo.Document                                                   |startDate                                         |datetime2 |          |          |
|estatio_blank.dbo.BankMandate                                              |sepaMandateIdentifier                             |varchar   |(255)     |          |
|estatio_dev.dbo.BankMandate                                                |sepaMandateIdentifier                             |varchar   |(35)      |          |
*/