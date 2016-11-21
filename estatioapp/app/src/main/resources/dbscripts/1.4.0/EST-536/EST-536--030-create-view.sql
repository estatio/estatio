

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO

----------------------------
CREATE SCHEMA "EST-536"
go

CREATE VIEW "EST-536".CommunicationChannel
AS
SELECT cc.id, cc.description, cc.legal, cc.reference, cc.type, cc.version, cc.discriminator,
       cc.emailAddress,
       cc.address1, cc.address2, cc.address3, cc.city, cc.countryId, cc.postalCode, cc.stateId,
       cc.phoneNumber,
       cc.purpose,
    (SELECT ccolffa.fixedAssetId
       FROM dbo.CommunicationChannelOwnerLink ccol
       JOIN dbo.CommunicationChannelOwnerLinkForFixedAsset ccolffa
         ON ccol.id = ccolffa.id
      WHERE cc.id = ccol.communicationChannelId) AS ownerFixedAssetId,
    (SELECT ccolfp.partyId
       FROM dbo.CommunicationChannelOwnerLink ccol
       JOIN dbo.CommunicationChannelOwnerLinkForParty ccolfp
         ON ccol.id = ccolfp.id
      WHERE cc.id = ccol.communicationChannelId) AS ownerPartyId
  FROM dbo.CommunicationChannel cc
GO

-- SELECT * FROM dbo.CommunicationChannel
-- SELECT * FROM "EST-536".CommunicationChannel
--GO

SELECT *
  FROM dbo.CommunicationChannel t
  JOIN "EST-536".CommunicationChannel v
    ON t.id = v.id
WHERE t.ownerFixedAssetId != v.ownerFixedAssetId
   OR t.ownerPartyId != v.ownerPartyId
GO
