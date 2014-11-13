/*
Delete data
*/
DELETE FROM InvoiceItem WHERE invoiceId IS NULL

/*
Drop constraints
*/
DECLARE @table VARCHAR(max)
DECLARE @command VARCHAR(max)
DECLARE cur CURSOR FOR
	--all indexes that are non PK
	SELECT t.name, 'DROP INDEX ' + '[' + i.name + '] ON ['+ s.name + '].[' + t.name + ']' AS command
	FROM sys.indexes i 
	INNER JOIN sys.objects t ON i.object_id = t.object_id 
	INNER JOIN sys.schemas s ON s.schema_id = t.schema_id
	WHERE s.name = 'dbo' AND i.type = 2
	--all FK constraints
	UNION
	SELECT t.name, 'ALTER TABLE ' + '[' + s.name + '].[' + t.name + '] DROP CONSTRAINT ['+ c.name + ']' AS command
	FROM sys.objects c 
	INNER JOIN sys.objects t ON t.object_id = c.parent_object_id 
	INNER JOIN sys.schemas s ON s.schema_id = t.schema_id
	WHERE c.type IN ('C', 'F', 'UQ', 'D')	
	ORDER BY t.name
OPEN cur
FETCH NEXT FROM cur INTO @table, @command
WHILE (@@fetch_status = 0) BEGIN
	PRINT @table+': '+@command
	EXEC (@command)
	FETCH NEXT FROM cur INTO @table, @command
END
CLOSE cur
DEALLOCATE cur

/*
Modify tables
*/
ALTER TABLE CommunicationChannel ADD purpose VARCHAR(30)
UPDATE CommunicationChannel SET type = REPLACE(type,'ACCOUNTING_',''), purpose = 'ACCOUNTING' WHERE type LIKE 'ACCOUNTING%'
ALTER TABLE CommunicationChannel DROP COLUMN ownerOrganisationId
ALTER TABLE CommunicationChannel DROP COLUMN ownerPropertyId
ALTER TABLE IsisAuditEntry ALTER COLUMN "target" VARCHAR(2000)
ALTER TABLE IsisCommand ALTER COLUMN "result" VARCHAR(2000)
ALTER TABLE IsisPublishedEvent ALTER COLUMN "target" VARCHAR(2000)
ALTER TABLE TaxRate DROP COLUMN "description"
ALTER TABLE FinancialAccountTransaction ALTER COLUMN "financialAccountId" BIGINT NOT NULL
ALTER TABLE LandRegister ALTER COLUMN categoria varchar(50) 
ALTER TABLE LandRegister ALTER COLUMN classe varchar(50) 
ALTER TABLE LandRegister ALTER COLUMN codiceComuneCatastale varchar(50) 
ALTER TABLE LandRegister ALTER COLUMN comuneAmministrativo varchar(50) 
ALTER TABLE LandRegister ALTER COLUMN comuneCatastale varchar(50) 
ALTER TABLE LandRegister ALTER COLUMN consistenza varchar(50) 
ALTER TABLE LandRegister ALTER COLUMN foglio varchar(50) 
ALTER TABLE LandRegister ALTER COLUMN particella varchar(50) 
ALTER TABLE LandRegister ALTER COLUMN subalterno varchar(50) 
ALTER TABLE Guarantee DROP COLUMN maximumAmount
ALTER TABLE InvoiceItem ALTER COLUMN invoiceId BIGINT NOT NULL
ALTER TABLE BankMandate ALTER COLUMN "bankAccountId" BIGINT NOT NULL

/*
Drop tables
*/
DROP TABLE LeaseStatusReason



