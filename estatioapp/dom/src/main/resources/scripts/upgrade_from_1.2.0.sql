
/**
* Stored procedure creates column with default and removes default constraint
**/
ALTER PROCEDURE addColumn
(
    @table VARCHAR(255),
    @column VARCHAR(255),
    @type VARCHAR(255),
    @default VARCHAR(255)
)
AS
BEGIN
    IF NOT EXISTS(SELECT * FROM sys.columns 
            WHERE [name] = @column AND [object_id] = OBJECT_ID(QUOTENAME(@table)))
    BEGIN
        DECLARE @addColumnCmd NVARCHAR(1000)
        SET @addColumnCmd = 'ALTER TABLE '+ QUOTENAME(@table) + ' ADD '+ QUOTENAME(@column) + ' ' + @type + ' NOT NULL DEFAULT ''' + @default + ''''
        EXEC sp_executesql @addColumnCmd

        DECLARE @dropDefaultCmd NVARCHAR(1000)
        SELECT 
            @dropDefaultCmd = 'ALTER TABLE [' + @table + '] DROP CONSTRAINT ' + d.name
        FROM
            sys.tables t 
            JOIN sys.default_constraints d ON d.parent_object_id = t.object_id  
            JOIN sys.columns c ON c.object_id = t.object_id AND c.column_id = d.parent_column_id
        WHERE 
            t.name = @table AND c.name = @column
        EXEC sp_executesql @dropDefaultCmd
    END
END
GO

/**
* Moves database to a different schema and renames it
**/
CREATE PROCEDURE moveRenameDb
(
	@FromSchema VARCHAR(255),
    @FromDb VARCHAR(255),
    @ToSchema VARCHAR(255),
    @ToDb VARCHAR(255)
)
AS
BEGIN
	DECLARE @NewName VARCHAR(255) = @ToSchema+'.'+@FromDb
	IF NOT EXISTS (
		SELECT  schema_name
		FROM    information_schema.schemata
		WHERE   schema_name = @ToSchema ) 
		BEGIN
			EXEC('CREATE SCHEMA '+@ToSchema)
		END
	EXEC('ALTER SCHEMA '+@ToSchema +' TRANSFER '+@FromSchema+'.'+@FromDb)
	EXEC sp_rename @NewName, @ToDb;
END
GO

/**
* Upgrade Application Tenancy
**/

ALTER TABLE [dbo].[IsisSecurityApplicationTenancy] DROP CONSTRAINT [IsisSecurityApplicationTenancy_PK]
ALTER TABLE [dbo].[IsisSecurityApplicationTenancy] ALTER COLUMN [path] VARCHAR(255) NOT NULL
ALTER TABLE [dbo].[IsisSecurityApplicationTenancy] ADD  CONSTRAINT [IsisSecurityApplicationTenancy_PK] PRIMARY KEY CLUSTERED 
(
	[path] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, FILLFACTOR = 12) ON [PRIMARY]
GO
ALTER TABLE IsisSecurityApplicationTenancy ALTER COLUMN parentPath VARCHAR(255)
GO
ALTER TABLE IsisSecurityApplicationUser ALTER COLUMN tenancyId VARCHAR(255)
GO
EXEC sp_rename 'IsisSecurityApplicationUser.tenancyId', 'atPath', 'COLUMN';
GO


EXEC dbo.addColumn 'Brand', 'atPath', 'VARCHAR(255)', '/ITA'
EXEC dbo.addColumn 'Index', 'atPath', 'VARCHAR(255)', '/ITA'
EXEC dbo.addColumn 'Link', 'atPath', 'VARCHAR(255)', '/ITA'
EXEC dbo.addColumn 'Tax', 'atPath', 'VARCHAR(255)', '/ITA'
EXEC dbo.addColumn 'Charge', 'atPath', 'VARCHAR(255)', '/ITA'
EXEC dbo.addColumn 'Organisation', 'atPath', 'VARCHAR(255)', '/ITA'
EXEC dbo.addColumn 'Person', 'atPath', 'VARCHAR(255)', '/ITA'
EXEC dbo.addColumn 'Property', 'atPath', 'VARCHAR(255)', '/ITA'
EXEC dbo.addColumn 'BankMandate', 'atPath', 'VARCHAR(255)', '/ITA'
EXEC dbo.addColumn 'Lease', 'atPath', 'VARCHAR(255)', '/ITA'
EXEC dbo.addColumn 'Invoice', 'atPath', 'VARCHAR(255)', '/ITA'
EXEC dbo.addColumn 'LeaseItem', 'atPath', 'VARCHAR(255)', '/ITA'
EXEC dbo.addColumn 'LeaseAssignment', 'atPath', 'VARCHAR(255)', '/ITA'
EXEC dbo.addColumn 'Tag', 'atPath', 'VARCHAR(255)', '/ITA'
GO

UPDATE lea SET atPath = '/ITA'
    FROM Lease lea
    INNER JOIN Agreement agr ON lea.id = agr.id
GO

/**
* Move databases to new schemas
*/
EXEC dbo.moveRenameDb 'dbo','IsisAuditEntry','isisaudit','AuditEntry'
EXEC dbo.moveRenameDb 'dbo','IsisCommand','isiscommand', 'Command'
EXEC dbo.moveRenameDb 'dbo','IsisPublishedEvent', 'isispublishing', 'PublishedEvent'
EXEC dbo.moveRenameDb 'dbo','IsisSecurityApplicationPermission','isissecurity','ApplicationPermission'
EXEC dbo.moveRenameDb 'dbo','IsisSecurityApplicationRole','isissecurity','ApplicationRole'
EXEC dbo.moveRenameDb 'dbo','IsisSecurityApplicationTenancy','isissecurity','ApplicationTenancy'
EXEC dbo.moveRenameDb 'dbo','IsisSecurityApplicationUser','isissecurity','ApplicationUser'
EXEC dbo.moveRenameDb 'dbo','IsisSecurityApplicationUserRoles','isissecurity','ApplicationUserRoles'
EXEC dbo.moveRenameDb 'dbo','IsisUserSetting', 'isissettings','UserSetting'

/**
* SET User's Application Tenancy
*/
UPDATE isissecurity.ApplicationUser SET atPath = '/' WHERE accountType = 'LOCAL' AND atPath IS NULL
UPDATE isissecurity.ApplicationUser SET atPath = '/ITA' WHERE atPath IS NULL


/**
* Change from DATETIME2 TO DATE
**/
ALTER TABLE dbo.Agreement ALTER COLUMN endDate DATE
ALTER TABLE dbo.Agreement ALTER COLUMN startDate DATE
ALTER TABLE dbo.AgreementRole ALTER COLUMN endDate DATE
ALTER TABLE dbo.AgreementRole ALTER COLUMN startDate DATE
ALTER TABLE dbo.AgreementRoleCommunicationChannel ALTER COLUMN endDate DATE
ALTER TABLE dbo.AgreementRoleCommunicationChannel ALTER COLUMN startDate DATE
ALTER TABLE dbo.BreakOption ALTER COLUMN breakDate DATE NOT NULL
ALTER TABLE dbo.BreakOption ALTER COLUMN exerciseDate DATE NOT NULL
ALTER TABLE dbo.BreakOption ALTER COLUMN reminderDate DATE
ALTER TABLE dbo.Event ALTER COLUMN date DATE NOT NULL
ALTER TABLE dbo.FinancialAccountTransaction ALTER COLUMN transactionDate DATE NOT NULL
ALTER TABLE dbo.FixedAssetRegistration ALTER COLUMN startDate DATE
ALTER TABLE dbo.FixedAssetRegistration ALTER COLUMN endDate DATE
ALTER TABLE dbo.FixedAssetRole ALTER COLUMN endDate DATE
ALTER TABLE dbo.FixedAssetRole ALTER COLUMN startDate DATE
ALTER TABLE dbo.Guarantee ALTER COLUMN terminationDate DATE
ALTER TABLE dbo.IndexBase ALTER COLUMN startDate DATE NOT NULL
ALTER TABLE dbo.IndexValue ALTER COLUMN startDate DATE NOT NULL
ALTER TABLE dbo.Invoice ALTER COLUMN dueDate DATE NOT NULL
ALTER TABLE dbo.Invoice ALTER COLUMN invoiceDate DATE
ALTER TABLE dbo.InvoiceItem ALTER COLUMN dueDate DATE NOT NULL
ALTER TABLE dbo.InvoiceItem ALTER COLUMN effectiveEndDate DATE
ALTER TABLE dbo.InvoiceItem ALTER COLUMN effectiveStartDate DATE
ALTER TABLE dbo.InvoiceItem ALTER COLUMN endDate DATE
ALTER TABLE dbo.InvoiceItem ALTER COLUMN startDate DATE
ALTER TABLE dbo.Lease ALTER COLUMN tenancyEndDate DATE
ALTER TABLE dbo.Lease ALTER COLUMN tenancyStartDate DATE
ALTER TABLE dbo.LeaseAssignment ALTER COLUMN assignmentDate DATE NOT NULL
ALTER TABLE dbo.LeaseItem ALTER COLUMN endDate DATE
ALTER TABLE dbo.LeaseItem ALTER COLUMN epochDate DATE
ALTER TABLE dbo.LeaseItem ALTER COLUMN nextDueDate DATE
ALTER TABLE dbo.LeaseItem ALTER COLUMN startDate DATE
ALTER TABLE dbo.LeaseTerm ALTER COLUMN endDate DATE
ALTER TABLE dbo.LeaseTerm ALTER COLUMN startDate DATE
ALTER TABLE dbo.LeaseTerm ALTER COLUMN paymentDate DATE
ALTER TABLE dbo.LeaseTerm ALTER COLUMN registrationDate DATE
ALTER TABLE dbo.LeaseTerm ALTER COLUMN baseIndexStartDate DATE
ALTER TABLE dbo.LeaseTerm ALTER COLUMN effectiveDate DATE
ALTER TABLE dbo.LeaseTerm ALTER COLUMN nextIndexStartDate DATE
ALTER TABLE dbo.Occupancy ALTER COLUMN endDate DATE
ALTER TABLE dbo.Occupancy ALTER COLUMN startDate DATE
ALTER TABLE dbo.PartyRegistration ALTER COLUMN endDate DATE
ALTER TABLE dbo.PartyRegistration ALTER COLUMN startDate DATE
ALTER TABLE dbo.PartyRelationship ALTER COLUMN endDate DATE
ALTER TABLE dbo.PartyRelationship ALTER COLUMN startDate DATE
ALTER TABLE dbo.PaymentTerm ALTER COLUMN dueDate DATE NOT NULL
ALTER TABLE dbo.Property ALTER COLUMN acquireDate DATE
ALTER TABLE dbo.Property ALTER COLUMN disposalDate DATE
ALTER TABLE dbo.Property ALTER COLUMN openingDate DATE
ALTER TABLE dbo.TaxRate ALTER COLUMN endDate DATE
ALTER TABLE dbo.TaxRate ALTER COLUMN startDate DATE
ALTER TABLE dbo.Unit ALTER COLUMN endDate DATE
ALTER TABLE dbo.Unit ALTER COLUMN startDate DATE


/**
* Clean up unused tables
**/
DROP TABLE [dbo].[UserSetting]
GO
DROP TABLE [dbo].[Tag]
GO
DROP TABLE [dbo].[IsisApplicationSetting]
GO
DROP TABLE [dbo].[PaymentTerm]
GO


/**
* Fix columns that have changed in the dom but were never upgraded
 */
ALTER TABLE LandRegister ALTER COLUMN rendita DECIMAL(19,2)
GO
ALTER TABLE FinancialAccountTransaction ALTER COLUMN financialAccountId BIGINT NOT NULL
GO
ALTER TABLE LeaseTerm DROP COLUMN taxable
GO


/**
* Drop all procedures
**/
DROP PROCEDURE dbo.addColumn
GO
DROP PROCEDURE dbo.moveRenameDb
GO




SELECT * FROM Lease


