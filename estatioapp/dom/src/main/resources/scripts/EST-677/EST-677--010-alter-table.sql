

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
BEGIN
ALTER TABLE [dbo].[LeaseType]  ADD atPath [varchar](255)
UPDATE [dbo].[LeaseType] SET atPath = '/FRA' WHERE reference IN ('BC','BD','BX','CO','D','HB','OP','P') AND atPath IS NULL
UPDATE [dbo].[LeaseType] SET atPath = '/FRA' WHERE reference IN ('FRBC','FRBD','FRBX','FRCO','FRD','FRHB','FROP','FRP') AND atPath IS NULL
UPDATE [dbo].[LeaseType] SET atPath = '/ITA' WHERE atPath IS NULL
ALTER TABLE [dbo].[LeaseType] ALTER COLUMN  atPath [varchar](255) NOT NULL
END

GO
