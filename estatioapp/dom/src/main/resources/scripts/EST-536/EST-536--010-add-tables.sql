

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO

---------------------------
CREATE TABLE [dbo].[CommunicationChannelOwnerLink](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[communicationChannelId] [bigint] NOT NULL,
	[communicationChannelType] [varchar](30) NOT NULL,
	[ownerIdentifier] [varchar](255) NOT NULL,
	[ownerObjectType] [varchar](255) NOT NULL,
 CONSTRAINT [CommunicationChannelOwnerLink_PK] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [CommunicationChannelOwnerLink_commchannel_owner_UNQ] UNIQUE NONCLUSTERED
(
	[communicationChannelId] ASC,
	[ownerObjectType] ASC,
	[ownerIdentifier] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[CommunicationChannelOwnerLink]  WITH CHECK ADD  CONSTRAINT [CommunicationChannelOwnerLink_FK1] FOREIGN KEY([communicationChannelId])
REFERENCES [dbo].[CommunicationChannel] ([id])
GO

ALTER TABLE [dbo].[CommunicationChannelOwnerLink] CHECK CONSTRAINT [CommunicationChannelOwnerLink_FK1]
GO


---------------------------
CREATE TABLE [dbo].[CommunicationChannelOwnerLinkForFixedAsset](
	[id] [bigint] NOT NULL,
	[fixedAssetId] [bigint] NOT NULL,
 CONSTRAINT [CommunicationChannelOwnerLinkForFixedAsset_PK] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[CommunicationChannelOwnerLinkForFixedAsset]  WITH CHECK ADD  CONSTRAINT [CommunicationChannelOwnerLinkForFixedAsset_FK1] FOREIGN KEY([id])
REFERENCES [dbo].[CommunicationChannelOwnerLink] ([id])
GO

ALTER TABLE [dbo].[CommunicationChannelOwnerLinkForFixedAsset] CHECK CONSTRAINT [CommunicationChannelOwnerLinkForFixedAsset_FK1]
GO

ALTER TABLE [dbo].[CommunicationChannelOwnerLinkForFixedAsset]  WITH CHECK ADD  CONSTRAINT [CommunicationChannelOwnerLinkForFixedAsset_FK2] FOREIGN KEY([fixedAssetId])
REFERENCES [dbo].[FixedAsset] ([id])
GO

ALTER TABLE [dbo].[CommunicationChannelOwnerLinkForFixedAsset] CHECK CONSTRAINT [CommunicationChannelOwnerLinkForFixedAsset_FK2]
GO


---------------------------
CREATE TABLE [dbo].[CommunicationChannelOwnerLinkForParty](
	[id] [bigint] NOT NULL,
	[partyId] [bigint] NOT NULL,
 CONSTRAINT [CommunicationChannelOwnerLinkForParty_PK] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[CommunicationChannelOwnerLinkForParty]  WITH CHECK ADD  CONSTRAINT [CommunicationChannelOwnerLinkForParty_FK1] FOREIGN KEY([id])
REFERENCES [dbo].[CommunicationChannelOwnerLink] ([id])
GO

ALTER TABLE [dbo].[CommunicationChannelOwnerLinkForParty] CHECK CONSTRAINT [CommunicationChannelOwnerLinkForParty_FK1]
GO

ALTER TABLE [dbo].[CommunicationChannelOwnerLinkForParty]  WITH CHECK ADD  CONSTRAINT [CommunicationChannelOwnerLinkForParty_FK2] FOREIGN KEY([partyId])
REFERENCES [dbo].[Party] ([id])
GO

ALTER TABLE [dbo].[CommunicationChannelOwnerLinkForParty] CHECK CONSTRAINT [CommunicationChannelOwnerLinkForParty_FK2]
GO


