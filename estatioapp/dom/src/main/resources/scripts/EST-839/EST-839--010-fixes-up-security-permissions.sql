
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO

---------------------------

delete
  from [isissecurity].[ApplicationPermission]
 where featureFqn = 'org.isisaddons.module.security.dom.user.ApplicationUser#enable'

delete
  from [isissecurity].[ApplicationPermission]
 where featureFqn = 'org.isisaddons.module.security.dom.user.ApplicationUser#disable'

delete
  from [isissecurity].[ApplicationPermission]
 where featureFqn = 'org.isisaddons.module.security.fixture'

delete
  from [isissecurity].[ApplicationPermission]
 where featureFqn = 'org.estatio.webapp.services.other'

update [isissecurity].[ApplicationPermission]
   set featureFqn = 'org.estatio.app.menus.lease.LeaseTypeMenu'
 where featureFqn = 'org.estatio.dom.lease.LeaseTypes'

update [isissecurity].[ApplicationPermission]
   set featureFqn = 'org.estatio.app.menus.brand.BrandMenu'
 where featureFqn = 'org.estatio.dom.lease.tags.Brands'

update [isissecurity].[ApplicationPermission]
   set featureFqn = 'org.estatio.app.menus.brand.UnitSizeMenu'
 where featureFqn = 'org.estatio.dom.lease.tags.UnitSizes'

update [isissecurity].[ApplicationPermission]
   set featureFqn = 'org.estatio.app.menus.asset.PropertyMenu#allProperties'
 where featureFqn = 'org.estatio.dom.asset.PropertyMenu#allProperties'

update [isissecurity].[ApplicationPermission]
   set featureFqn = 'org.estatio.app.menus.asset.PropertyMenu#findProperties'
 where featureFqn = 'org.estatio.dom.asset.PropertyMenu#findProperties'

update [isissecurity].[ApplicationPermission]
   set featureFqn = 'org.estatio.app.menus.asset.UnitMenu#allUnits'
 where featureFqn = 'org.estatio.dom.asset.UnitMenu#allUnits'

update [isissecurity].[ApplicationPermission]
   set featureFqn = 'org.estatio.app.menus.asset.UnitMenu#findUnits'
 where featureFqn = 'org.estatio.dom.asset.UnitMenu#findUnits'

update [isissecurity].[ApplicationPermission]
   set featureFqn = 'org.estatio.dom.asset.financial.FixedAssetFinancialAccountContributions#newAccount'
 where featureFqn = 'org.estatio.dom.asset.financial.contributed.FixedAssetFinancialAccountContributions#newAccount'

update [isissecurity].[ApplicationPermission]
   set featureFqn = 'org.estatio.app.menus.financial.BankAccountMenu#newBankAccount'
 where featureFqn = 'org.estatio.dom.financial.bankaccount.BankAccounts#newBankAccount'

update [isissecurity].[ApplicationPermission]
   set featureFqn = 'org.estatio.dom.financial.contributed.Party_financialAccountContributions#addAccount'
 where featureFqn = 'org.estatio.dom.financial.contributed.FinancialAccountContributions#addAccount'

delete
  from [isissecurity].[ApplicationPermission]
 where featureFqn = 'org.estatio.dom.communicationchannelfixedasset'

go
