-- all organisations that have at least one communication channel an additional reference in the city column,
-- find all their communication channels, and count for each of those comm channels whether they are used as AgreementRoleCommunicationChannels

select cc.[id] as id     ,cc.[legal] ,cc.[reference],cc.[address1],cc.[address2]
      ,cc.[address3]
      ,cc.[city]
      ,cc.[countryId]
      ,cc.[postalCode]
      ,cc.[stateId]
      ,cc.[purpose],
	  o.id as org_id,
	  p.name as org_name,
	  (select count(*) from CommunicationChannelOwnerLink where communicationChannelId = cc.id) as ccol_count,
	  (select count(*) from Invoice where sendToCommunicationChannelId = cc.id) as inv_count,
	  (select count(*) from AgreementRoleCommunicationChannel where communicationChannelId = cc.id) as arcc_count
  from CommunicationChannelOwnerLink ccol
  join Organisation o
    on ccol.ownerIdentifier = o.id
  join Party p
    on o.id = p.id
  join CommunicationChannel cc
    on ccol.communicationChannelId = cc.id
   and cc.type = 'POSTAL_ADDRESS'
where ccol.ownerIdentifier in (select ccol2.ownerIdentifier
                                    from CommunicationChannel cc2
									join CommunicationChannelOwnerLink ccol2
									  on cc2.id = ccol2.communicationChannelId
								    where 1=1
									  -- and type = 'POSTAL_ADDRESS'   and city like '%)_%' and ccol2.ownerObjectType = 'org.estatio.dom.party.Organisation'
									  and communicationChannelId in (171,177,309,3014,3015,3016,3017,3081,3588,1234,1237,1238,1240,1244,1245,1246,1247,3594)

									)
order by o.id, cc.id