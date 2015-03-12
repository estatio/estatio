

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO

---------------------------

INSERT INTO [dbo].[CommunicationChannelOwnerLink] (
  [communicationChannelId],
  [communicationChannelType],
  [ownerIdentifier],
  [ownerObjectType]
  )
SELECT cc.id, cc.type, convert(varchar, cc.ownerFixedAssetId), 'org.estatio.dom.asset.Property'
  FROM CommunicationChannel cc
 WHERE cc.ownerFixedAssetId IS NOT NULL
   AND cc.ownerFixedAssetId IN (SELECT id from Property)


INSERT INTO [dbo].[CommunicationChannelOwnerLink] (
  [communicationChannelId],
  [communicationChannelType],
  [ownerIdentifier],
  [ownerObjectType]
)
SELECT cc.id, cc.type, convert(varchar, cc.ownerFixedAssetId), 'org.estatio.dom.asset.Unit'
  FROM CommunicationChannel cc
 WHERE cc.ownerFixedAssetId IS NOT NULL
   AND cc.ownerFixedAssetId IN (SELECT id from Unit)



INSERT INTO [dbo].[CommunicationChannelOwnerLinkForFixedAsset] (
  [id],
  [fixedAssetId]
)
  SELECT id,convert(bigint, ownerIdentifier)
  FROM [dbo].[CommunicationChannelOwnerLink]
  WHERE ownerObjectType in ('org.estatio.dom.asset.Property', 'org.estatio.dom.asset.Unit')

---------------------------


INSERT INTO [dbo].[CommunicationChannelOwnerLink] (
  [communicationChannelId],
  [communicationChannelType],
  [ownerIdentifier],
  [ownerObjectType]
)
SELECT cc.id, cc.type, convert(varchar, cc.ownerPartyId), 'org.estatio.dom.party.Organisation'
  FROM CommunicationChannel cc
 WHERE cc.ownerPartyId IS NOT NULL
   AND cc.ownerPartyId IN (SELECT id from Organisation)

INSERT INTO [dbo].[CommunicationChannelOwnerLink] (
  [communicationChannelId],
  [communicationChannelType],
  [ownerIdentifier],
  [ownerObjectType]
)
SELECT cc.id, cc.type, convert(varchar, cc.ownerPartyId), 'org.estatio.dom.party.Person'
  FROM CommunicationChannel cc
 WHERE cc.ownerPartyId IS NOT NULL
   AND cc.ownerPartyId IN (SELECT id from Person)


INSERT INTO [dbo].[CommunicationChannelOwnerLinkForParty] (
  [id],
  [partyId]
)
  SELECT id,convert(bigint, ownerIdentifier)
  FROM [dbo].[CommunicationChannelOwnerLink]
  WHERE ownerObjectType in ('org.estatio.dom.party.Organisation', 'org.estatio.dom.party.Person')

---------------------------
