

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO

---------------------------

ALTER TABLE [dbo].[LeaseType]  ADD atPath [varchar](255)
UPDATE [dbo].[LeaseType] SET atPath = '/FRA' WHERE reference IN ('BC','BD','BX','CO','D','HB','OP','P')
UPDATE [dbo].[LeaseType] SET atPath = '/FRA' WHERE reference IN ('FRBC','FRBD','FRBX','FRCO','FRD','FRHB','FROP','FRP')
UPDATE [dbo].[LeaseType] SET atPath = '/ITA' WHERE atPath IS NULL
ALTER TABLE [dbo].[LeaseType] ALTER COLUMN  atPath [varchar](255) NOT NULL
GO
