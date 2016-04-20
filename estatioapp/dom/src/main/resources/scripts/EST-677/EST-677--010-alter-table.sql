

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO

---------------------------

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
