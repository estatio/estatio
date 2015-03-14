/**
* Stored procedure creates column with default and removes default constraint
**/
CREATE PROCEDURE addColumn
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
            @dropDefaultCmd = 'ALTER TABLE ' + @table + ' DROP CONSTRAINT ' + d.name
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
* Drop database constraints in order to perform changes on primary keys
**/
CREATE PROCEDURE dropConstraints
AS
BEGIN
	DECLARE @str VARCHAR(MAX)
	DECLARE cur CURSOR FOR

	SELECT 
		'ALTER TABLE ' + '[' + s.[NAME] + '].[' + t.name + '] DROP CONSTRAINT ['+ c.name + ']'
	FROM 
		sys.objects c 
		INNER JOIN sys.objects t ON  c.[parent_object_id]=t.[object_id] 
		INNER JOIN sys.schemas s ON  t.[schema_id] = s.[schema_id]
	WHERE
		c.[type] IN ('C', 'F', 'UQ', 'D') -- We don't want to drop PK 
	ORDER BY 
		c.[type]

	OPEN cur

	FETCH NEXT FROM cur INTO @str
	WHILE (@@fetch_status = 0) BEGIN
		PRINT @str
		EXEC (@str)
		FETCH NEXT FROM cur INTO @str
	END

	CLOSE cur
	DEALLOCATE cur
END
GO

/**
* Actual upgrade starts here
**/

EXECUTE [dbo].[dropConstraints] 
GO


ALTER TABLE [dbo].[IsisSecurityApplicationTenancy] DROP CONSTRAINT [IsisSecurityApplicationTenancy_PK]
ALTER TABLE [dbo].[IsisSecurityApplicationTenancy] ALTER COLUMN [path] VARCHAR(30) NOT NULL
ALTER TABLE [dbo].[IsisSecurityApplicationTenancy] ADD  CONSTRAINT [IsisSecurityApplicationTenancy_PK] PRIMARY KEY CLUSTERED 
(
	[path] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, FILLFACTOR = 12) ON [PRIMARY]
GO

ALTER TABLE IsisSecurityApplicationTenancy ALTER COLUMN parentPath VARCHAR(30)
GO

ALTER TABLE IsisSecurityApplicationUser ALTER COLUMN tenancyId VARCHAR(30) -- TODO: fix isis-module-security and re-release with as "atPath" instead
GO

ALTER TABLE [dbo].[IsisSecurityApplicationUserRoles] DROP CONSTRAINT [IsisSecurityApplicationUserRoles_FK1]
ALTER TABLE [dbo].[IsisSecurityApplicationUserRoles] DROP CONSTRAINT [IsisSecurityApplicationUserRoles_FK2]
ALTER TABLE [dbo].[IsisSecurityApplicationUserRoles] ADD  CONSTRAINT [IsisSecurityApplicationUserRoles_PK] PRIMARY KEY CLUSTERED 
(
	[roleId] ASC,
	[userId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, FILLFACTOR = 12) ON [PRIMARY]
GO

EXEC dbo.addColumn 'Brand', 'atPath', 'VARCHAR(30)', '/ITA'
EXEC dbo.addColumn 'Index', 'atPath', 'VARCHAR(30)', '/ITA'
EXEC dbo.addColumn 'Link', 'atPath', 'VARCHAR(30)', '/ITA'
EXEC dbo.addColumn 'Tax', 'atPath', 'VARCHAR(30)', '/ITA'
EXEC dbo.addColumn 'Charge', 'atPath', 'VARCHAR(30)', '/ITA'
EXEC dbo.addColumn 'Organisation', 'atPath', 'VARCHAR(30)', '/ITA'
EXEC dbo.addColumn 'Person', 'atPath', 'VARCHAR(30)', '/ITA'
EXEC dbo.addColumn 'Property', 'atPath', 'VARCHAR(30)', '/ITA'
EXEC dbo.addColumn 'BankMandate', 'atPath', 'VARCHAR(30)', '/ITA'
EXEC dbo.addColumn 'Lease', 'atPath', 'VARCHAR(30)', '/ITA'
EXEC dbo.addColumn 'Invoice', 'atPath', 'VARCHAR(30)', '/ITA'
EXEC dbo.addColumn 'LeaseItem', 'atPath', 'VARCHAR(30)', '/ITA'
EXEC dbo.addColumn 'LeaseAssignment', 'atPath', 'VARCHAR(30)', '/ITA'
EXEC dbo.addColumn 'Tag', 'atPath', 'VARCHAR(30)', '/ITA'
GO

UPDATE lea SET atPath = '/ITA/' + LEFT(agr.reference,3)
    FROM Lease lea
    INNER JOIN Agreement agr ON lea.id = agr.id
GO

DROP PROCEDURE dbo.addColumn
GO

DROP PROCEDURE dbo.dropConstraints
GO