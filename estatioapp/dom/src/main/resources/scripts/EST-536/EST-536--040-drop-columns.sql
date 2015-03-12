

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO

---------------------------


--
-- drop CommunicationChannel.ownerPartyId
--

-- todo: use Jeroen's generic "drop constraints sproc instead"

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[CommunicationChannel]') AND name = N'PostalAddress_main_idx')
DROP INDEX [PostalAddress_main_idx] ON [dbo].[CommunicationChannel] WITH ( ONLINE = OFF )
GO
IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[CommunicationChannel]') AND name = N'CommunicationChannel_N50')
DROP INDEX [CommunicationChannel_N50] ON [dbo].[CommunicationChannel] WITH ( ONLINE = OFF )
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[CommunicationChannel_FK3]') AND parent_object_id = OBJECT_ID(N'[dbo].[CommunicationChannel]'))
ALTER TABLE [dbo].[CommunicationChannel] DROP CONSTRAINT [CommunicationChannel_FK3]
GO

ALTER TABLE [dbo].[CommunicationChannel] DROP COLUMN [ownerPartyId]
GO


---------------------------

--
-- drop CommunicationChannel.ownerFixedAssetId
--

-- todo: use Jeroen's generic "drop constraints sproc instead"

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[CommunicationChannel]') AND name = N'CommunicationChannel_N51')
DROP INDEX [CommunicationChannel_N51] ON [dbo].[CommunicationChannel] WITH ( ONLINE = OFF )
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[CommunicationChannel_FK4]') AND parent_object_id = OBJECT_ID(N'[dbo].[CommunicationChannel]'))
ALTER TABLE [dbo].[CommunicationChannel] DROP CONSTRAINT [CommunicationChannel_FK4]
GO


ALTER TABLE [dbo].[CommunicationChannel] DROP COLUMN [ownerFixedAssetId]
GO


---------------------------

--
-- other constraints will be recreated when the app is restarted.
--