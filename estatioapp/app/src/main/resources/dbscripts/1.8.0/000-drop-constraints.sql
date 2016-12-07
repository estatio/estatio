/**
* Drop database constraints in order to perform changes on primary keys
**/
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
AND
    t.[name] != 'schema_version'      -- don't drop 'DEFAULT' constraints on flyway's schema_version table
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
GO

/**
* Drop Indexes
**/
DECLARE @qry NVARCHAR(MAX);
SELECT @qry = (
	SELECT
	'DROP INDEX ['+ i.name + '] ON '+SCHEMA_NAME(o.schema_id)+'.'+o.name+';'
	FROM sys.indexes i JOIN sys.objects o ON i.object_id = o.object_id
	WHERE o.type <> 'S' AND is_primary_key <> 1 AND index_id > 0 AND is_ms_shipped = 0
	FOR XML PATH('')
	);
EXEC sp_executesql @qry

