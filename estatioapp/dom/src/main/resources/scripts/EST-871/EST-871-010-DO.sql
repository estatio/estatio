begin tran
select @@trancount

declare driver cursor 
for
select 177 as cc_id_from, 171 as cc_id_to, 'RIF61000053' as ext_ref, null as city
 union
select 3014,              3014,            'Cur.codice I041',        'Curno (BG)'
 union
select 3015,              3014,            'Fir. codice I066',       null
 union
select 3016,              3014,            'Imo. codice I307',       null
 union
select 3017,              3014,            'Man. codice I077',       null
 union
select 3081,              3014,            'Car. codice I024',       null
 union
select 1237,              1237,            'codice-XA124',           'Alba (CN)'
 union
select 1238,              1237,            'Motivi XAB27  Oltre XA119', null  -- REVIEW: nowhere to move these...?
 union
select 1240,              1237,            'Motivi XA374 Oltre XA117', null   -- REVIEW: nowhere to move these...?
 union
select 1244,              1237,            'codice - XA271',         null  -- REVIEW: nowhere to move these...?
 union
select 1245,              1237,            'codice - XA302',         null  -- REVIEW: nowhere to move these...?
 union
select 1246,              1237,            'codice X4598',           null  -- REVIEW: nowhere to move these...?
 union
select 1247,              1237,            null,                     null

declare @cc_id_from int, @cc_id_to int, @ext_ref varchar(max), @city varchar(max)

open driver

fetch driver into @cc_id_from, @cc_id_to, @ext_ref, @city
while @@FETCH_STATUS = 0 begin

	select @cc_id_from, @cc_id_to, @ext_ref, @city

	-- move external reference over to AR if present
	if @ext_ref is not null begin
        update AgreementRole
		   set externalReference = @ext_ref
         where id in (select agreementRoleId from AgreementRoleCommunicationChannel where communicationChannelId = @cc_id_from)
	end
	-- fix up the city (if this row is going to remain)
	if @city is not null begin
		update CommunicationChannel
		   set City = @city
		 where id = @cc_id_from
	end

	if @cc_id_from <> @cc_id_to begin
        -- move over references
		update AgreementRoleCommunicationChannel
		   set communicationChannelId = @cc_id_to
		 where communicationChannelId = @cc_id_from
 
		update Invoice
		   set sendToCommunicationChannelId = @cc_id_to
		 where sendToCommunicationChannelId = @cc_id_from

        -- and delete
		delete from CommunicationChannelOwnerLinkForParty where id in (select id from CommunicationChannelOwnerLink where communicationChannelId = @cc_id_from)
		delete from CommunicationChannelOwnerLink where communicationChannelId = @cc_id_from
		delete from CommunicationChannel where id = @cc_id_from
	end

    fetch driver into @cc_id_from, @cc_id_to, @ext_ref, @city
end

close driver
  
deallocate driver



--
-- when done, rollback or commit as required:
--

-- commit
-- rollback

