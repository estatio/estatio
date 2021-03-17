ALTER VIEW [ecpdw].[DimTenants] AS

SELECT DISTINCT
	pa.reference AS TenantCode,
	pa.name AS TenantName,
	adr.city AS TenantCity,
	adr.alpha2Code AS TenantCountryCode,
	adr.address1 AS TenantAddress1,
	adr.address2 AS TenantAddress2,
	NULL AS TenantAddress3
FROM
	Party pa
	INNER JOIN Organisation org ON org.id = pa.id
	INNER JOIN AgreementRole ar ON ar.partyId = pa.id
	INNER JOIN AgreementRoleType art ON art.id = ar.typeId
	LEFT OUTER JOIN (
		SELECT 
		ROW_NUMBER() OVER(PARTITION BY ownerPartyId ORDER BY cc.legal, address1) AS sequence,
		cc.ownerPartyId,
		cc.address1,
		cc.address2,
		cc.address3,
		cc.city,
		countryAlpha2Code AS alpha2Code
		FROM
		v1.CommunicationChannel cc
		WHERE legal = 1 AND communicationChannelType = 'POSTAL_ADDRESS'
	) adr ON adr.ownerPartyId = pa.id AND sequence = 1
WHERE art.title = 'Tenant'
GO

ALTER VIEW [v1].[CommunicationChannel] AS 

SELECT
cc.id AS communicationChannelId,
cc.type AS communicationChannelType,
ccolfa.fixedAssetId AS ownerFixedAssetId,
ccolpa.PartyId AS ownerPartyId,
cc.address1,
cc.address2,
cc.address3,
cc.postalCode,
cc.city,
st.name AS stateName,
st.reference AS stateReference,
cr.name AS countryName,
cr.reference AS countryReference,
cr.alpha2Code AS countryAlpha2Code,
cc.emailAddress,
cc.phoneNumber,
cc.legal
FROM 
dbo.CommunicationChannel cc
INNER JOIN dbo.CommunicationChannelOwnerLink ccol ON ccol.communicationChannelId = cc.id
LEFT OUTER JOIN dbo.CommunicationChannelOwnerLinkForFixedAsset ccolfa ON ccolfa.id = ccol.id
LEFT OUTER JOIN dbo.CommunicationChannelOwnerLinkForParty ccolpa ON ccolpa.id = ccol.id
LEFT OUTER JOIN dbo.State st ON st.id = cc.stateId
LEFT OUTER JOIN dbo.Country cr ON cr.id = cc.countryId

GO




