USE estatio_test

IF OBJECT_ID('tempdb..#result') IS NOT NULL
BEGIN
    DROP TABLE #result
END
IF OBJECT_ID('tempdb..#replace') IS NOT NULL
BEGIN
    DROP TABLE #replace
END
IF OBJECT_ID('tempdb..#fields') IS NOT NULL
BEGIN
    DROP TABLE #fields
END


CREATE TABLE #fields  ( [schema] VARCHAR(50), [table] VARCHAR(100), [column] VARCHAR(50) )
INSERT INTO #fields VALUES ('dbo','Agreement','discriminator')
INSERT INTO #fields VALUES ('dbo','ApplicationSetting','key')
INSERT INTO #fields VALUES ('dbo','BreakOption','discriminator')
INSERT INTO #fields VALUES ('dbo','CommunicationChannel','discriminator')
INSERT INTO #fields VALUES ('dbo','CommunicationChannelOwnerLink','ownerObjectType')
INSERT INTO #fields VALUES ('dbo','EventSourceLink','sourceObjectType')
INSERT INTO #fields VALUES ('dbo','FinancialAccount','discriminator')
INSERT INTO #fields VALUES ('dbo','FixedAsset','discriminator')
INSERT INTO #fields VALUES ('dbo','FixedAssetRegistration','discriminator')
INSERT INTO #fields VALUES ('dbo','FixedAssetRegistrationType','fullyQualifiedClassName')
INSERT INTO #fields VALUES ('dbo','InvoiceItem','discriminator')
INSERT INTO #fields VALUES ('dbo','Link','className')
INSERT INTO #fields VALUES ('dbo','Numerator','objectType')
INSERT INTO #fields VALUES ('dbo','Party','discriminator')
INSERT INTO #fields VALUES ('incodeClassification','Classification','classifiedStr')
INSERT INTO #fields VALUES ('isiscommand','Command','memberIdentifier')
INSERT INTO #fields VALUES ('isiscommand','Command','result')
INSERT INTO #fields VALUES ('isiscommand','Command','target')
INSERT INTO #fields VALUES ('isispublishmq','PublishedEvent','memberIdentifier')
INSERT INTO #fields VALUES ('isispublishmq','PublishedEvent','target')
INSERT INTO #fields VALUES ('isispublishmq','StatusMessage','oid')
INSERT INTO #fields VALUES ('isissecurity','ApplicationPermission','featureFqn')

/*
Ignore text files as they can't be replaced

INSERT INTO #fields VALUES ('isiscommand','Command','exception')
INSERT INTO #fields VALUES ('isiscommand','Command','arguments')
INSERT INTO #fields VALUES ('isiscommand','Command','memento')
INSERT INTO #fields VALUES ('isispublishmq','PublishedEvent','serializedForm')
INSERT INTO #fields VALUES ('isispublishmq','StatusMessage','detail')
*/

CREATE TABLE #replace ( [class] VARCHAR(50), [from] VARCHAR(100), [to] VARCHAR(100) )

INSERT INTO #replace VALUES ('AgreementRole', 'estatioagreement', 'org.estatio.dom.agreement')
INSERT INTO #replace VALUES ('AgreementRoleCommunicationChannel', 'estatioagreement', 'org.estatio.dom.agreement')
INSERT INTO #replace VALUES ('AgreementRoleCommunicationChannelType', 'estatioagreement', 'org.estatio.dom.agreement')
INSERT INTO #replace VALUES ('AgreementRoleType', 'estatioagreement', 'org.estatio.dom.agreement')
INSERT INTO #replace VALUES ('AgreementType', 'estatioagreement', 'org.estatio.dom.agreement')
INSERT INTO #replace VALUES ('FixedAssetRole', 'estatioasset', 'org.estatio.dom.asset')
INSERT INTO #replace VALUES ('FixedAssetFinancialAccount', 'estatioassetfinancial', 'org.estatio.dom.asset.financial')
INSERT INTO #replace VALUES ('FixedAssetOwnership', 'estatioassetownership', 'org.estatio.dom.asset.ownership')
INSERT INTO #replace VALUES ('FixedAssetRegistration', 'estatioassetregistration', 'org.estatio.dom.asset.registration')
INSERT INTO #replace VALUES ('FixedAssetRegistrationType', 'estatioassetregistration', 'org.estatio.dom.asset.registration')
INSERT INTO #replace VALUES ('BudgetCalculationLink', 'estatiobudgetassignment', 'org.estatio.dom.budgetassignment')
INSERT INTO #replace VALUES ('ServiceChargeItem', 'estatiobudgetassignment', 'org.estatio.dom.budgetassignment')
INSERT INTO #replace VALUES ('BudgetItemAllocation', 'estatiobudgeting', 'org.estatio.dom.budgeting.allocation')
INSERT INTO #replace VALUES ('Budget', 'estatiobudgeting', 'org.estatio.dom.budgeting')
INSERT INTO #replace VALUES ('BudgetCalculation', 'estatiobudgeting', 'org.estatio.dom.budgeting.budgetcalculation')
INSERT INTO #replace VALUES ('BudgetItem', 'estatiobudgeting', 'org.estatio.dom.budgeting.budgetitem')
INSERT INTO #replace VALUES ('KeyItem', 'estatiobudgeting', 'org.estatio.dom.budgeting.keyitem')
INSERT INTO #replace VALUES ('KeyTable', 'estatiobudgeting', 'org.estatio.dom.budgeting.keytable')

INSERT INTO #replace VALUES ('Charge', 'estatiocharge', 'org.estatio.dom.charge')

INSERT INTO #replace VALUES ('ChargeGroup', 'estatiocharge', 'org.estatio.dom.charge')
INSERT INTO #replace VALUES ('FinancialAccountTransaction', 'estatiofinancial', 'org.estatio.dom.financial')
INSERT INTO #replace VALUES ('Index', 'estatioindex', 'org.estatio.dom.index')
INSERT INTO #replace VALUES ('IndexBase', 'estatioindex', 'org.estatio.dom.index')
INSERT INTO #replace VALUES ('IndexValue', 'estatioindex', 'org.estatio.dom.index')
INSERT INTO #replace VALUES ('Invoice', 'estatioinvoice', 'org.estatio.dom.invoice')
INSERT INTO #replace VALUES ('LeaseItem', 'estatiolease', 'org.estatio.dom.lease')
INSERT INTO #replace VALUES ('LeaseItemSource', 'estatiolease', 'org.estatio.dom.lease')
INSERT INTO #replace VALUES ('LeaseType', 'estatiolease', 'org.estatio.dom.lease')
INSERT INTO #replace VALUES ('Occupancy', 'estatiolease', 'org.estatio.dom.lease')
INSERT INTO #replace VALUES ('LeaseAssignment', 'estatiolease', 'org.estatio.dom.lease.assignment')
INSERT INTO #replace VALUES ('EventSourceLinkForBreakOption', 'estatioleasebreaks', 'org.estatio.dom.lease.breaks')
INSERT INTO #replace VALUES ('Activity', 'estatioleasetags', 'org.estatio.dom.lease.tags')
INSERT INTO #replace VALUES ('Brand', 'estatioleasetags', 'org.estatio.dom.lease.tags')
INSERT INTO #replace VALUES ('Sector', 'estatioleasetags', 'org.estatio.dom.lease.tags')
INSERT INTO #replace VALUES ('UnitSize', 'estatioleasetags', 'org.estatio.dom.lease.tags')
INSERT INTO #replace VALUES ('Link', 'estatiolink', 'org.estatio.domlink')
INSERT INTO #replace VALUES ('OrganisationPreviousName', 'estatioparty', 'org.estatio.dom.party')
INSERT INTO #replace VALUES ('PartyRegistration', 'estatioparty', 'org.estatio.dom.party')
INSERT INTO #replace VALUES ('PartyRelationship', 'estatioparty', 'org.estatio.dom.party.relationship')
INSERT INTO #replace VALUES ('BusinessCase', 'estatioproject', 'org.estatio.dom.project')
INSERT INTO #replace VALUES ('Program', 'estatioproject', 'org.estatio.dom.project')
INSERT INTO #replace VALUES ('ProgramRole', 'estatioproject', 'org.estatio.dom.project')
INSERT INTO #replace VALUES ('Project', 'estatioproject', 'org.estatio.dom.project')
INSERT INTO #replace VALUES ('ProjectRole', 'estatioproject', 'org.estatio.dom.project')
INSERT INTO #replace VALUES ('ApplicationSettingForEstatio', 'estatiosettings', 'org.estatio.domsettings')
INSERT INTO #replace VALUES ('UserSettingForEstatio', 'estatiosettings', 'org.estatio.domsettings')
INSERT INTO #replace VALUES ('Tax', 'estatiotax', 'org.estatio.dom.tax')
INSERT INTO #replace VALUES ('TaxRate', 'estatiotax', 'org.estatio.dom.tax')
INSERT INTO #replace VALUES ('Currency', 'incodecurrency', 'org.estatio.dom.currency')
INSERT INTO #replace VALUES ('Event', 'incodeevent', 'org.estatio.dom.event')
INSERT INTO #replace VALUES ('Numerator', 'incodenumerator', 'org.estatio.dom.numerator')
INSERT INTO #replace VALUES ('EmailAddress', 'org.incode.module.communications.dom.impl.commchannel', 'org.estatio.dom.communicationchannel')
INSERT INTO #replace VALUES ('PostalAddress', 'org.incode.module.communications.dom.impl.commchannel', 'org.estatio.dom.communicationchannel')

CREATE TABLE #result ( [value] VARCHAR(MAX));

-----

DECLARE @schema VARCHAR(100)
DECLARE @table VARCHAR(100)
DECLARE @column VARCHAR(50)
DECLARE @Sql NVARCHAR(500)
DECLARE @Parameters NVARCHAR(500)
DECLARE @result VARCHAR(MAX)

DECLARE db_cursor CURSOR FOR
SELECT * FROM #fields
--SELECT TABLE_SCHEMA, TABLE_NAME, COLUMN_NAME FROM [INFORMATION_SCHEMA].[COLUMNS] WHERE DATA_TYPE IN ('text', 'varchar', 'nvarchar') --AND TABLE_NAME = 'Program'
OPEN db_cursor
FETCH NEXT FROM db_cursor INTO @schema, @table, @column

WHILE @@FETCH_STATUS = 0
BEGIN

	DECLARE @class VARCHAR(50)
	DECLARE @from VARCHAR(100)
	DECLARE @to VARCHAR(100)
	DECLARE @fromfull VARCHAR(150)
	DECLARE @tofull VARCHAR(150)

	DECLARE field_cursor CURSOR FOR
	SELECT *
	FROM #replace
	OPEN field_cursor
	FETCH NEXT FROM field_cursor INTO @class, @from, @to

	WHILE @@FETCH_STATUS = 0
	BEGIN
		SET @fromfull = @from + '.'+ @class
		SET @tofull = @to + '.'+ @class
		--SET @Sql = 'SELECT @ResultOUT = ['+ @column +'] FROM ['+@schema+'].['+@table+'] WHERE ' + @column + ' LIKE ''%' + @fromfull + '%'''
		--SET @Sql = 'UPDATE [' + @schema + '].[' + @table + '] SET [' + @column + '] = CAST(REPLACE(CAST([' + @column + '] AS NVarchar(MAX)),''' + @fromfull + ''', ''' + @tofull + ''') AS NText) WHERE [' + @column + '] LIKE ''%' + @fromfull + '%'''
		SET @Sql = 'UPDATE [' + @schema + '].[' + @table + '] SET [' + @column + '] = REPLACE([' + @column + '], ''' + @fromfull + ''', ''' + @tofull + ''') WHERE [' + @column + '] LIKE ''%' + @fromfull + '%'''

		EXEC sp_executesql @Sql;

		FETCH NEXT FROM field_cursor INTO @class, @from, @to
	END
	CLOSE field_cursor
	DEALLOCATE field_cursor

    FETCH NEXT FROM db_cursor INTO @schema, @table, @column
END

CLOSE db_cursor
DEALLOCATE db_cursor

SELECT * FROM #result

DROP TABLE #result
DROP TABLE #replace
DROP TABLE #fields
