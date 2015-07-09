

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO

---------------------------

ALTER TABLE [dbo].[CommunicationChannel] DROP COLUMN [ownerPartyId]
GO


---------------------------


ALTER TABLE [dbo].[CommunicationChannel] DROP COLUMN [ownerFixedAssetId]
GO


---------------------------
--
-- other constraints will be recreated when the app is restarted.
--