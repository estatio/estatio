Metadata
========

 * edited using: [winterwell](http://www.winterwell.com/software/markdown-editor.php) editor
 
 * as of: 10-May-2013

Modelling Stories
=================

* [JvdW] complete bank mandate 

  eg. must specify ECP's target bank account
  
* [JvdW] pro-rata calculations of Turnover Rent

* [JvdW] pro-rata calculations of Service Charge

* [JvdW] rounding (0.22 over Q4)

  * option 1: round-up (to 0.24)
  * option 2: version AuditValue & "replay" previous

* [JvdW] Add Ledger Acct (for AR)

  * global value

* Add "Instance" and most-significant primary key element throughout

  * perhaps use a custom DomainObjectContainer

* Add manual Invoice Item

  * (for fudging)

  
Integration Stories
===================

* [DH] Generate DocLines

  * Invoice/InvoiceItem giving rise to:
    
    * AR line entry, per the Invoice
    * counterbalancing Item entry + Tax entry (for each InvoiceItem)
    
    
SQL Server Reporting (2 days for these stories?)
====================

* [JvdW] Preliminary letter

* [JvdW] Printed invoice

DataWarehousing (2 days for this stories?)
===============

* [JvdW] Integrate DW with Estatio DB




Questions
=========

* how import balances into the "Service" (dummy) Coda from Manhattan?



----
Dan's desk review
=================

on: 11-May-2013

* change `LeaseType` to be an entity ?
  
  * currently hardcodes Italian names
  
* need a Clock domain service (use of `LocalData.now()`, the mock clock only used by the calc service)

  * could use Isis' applib `Clock`

* `FinancialAccountType` vs `BankAccountType`

  * don't like this, why have a discriminator for both levels of the supertype hierarchy?
  
* `IBANHelper` - should this be a service?

* throughout: make classes abstract where should be

  * eg `Agreement`, `FixedAsset`, `FinancialAccount`
  
* throughout: figure out module dependencies and use interfaces to decouple/remove cycles

  * eg refdata such as geography, index, charge
  * eg semi-refdata such as fixed assets
  
  * eg `Communicable`, `Buyer`, `Seller`
  
  * check (using Structure101) that all cycles removed
  
* naming convention to distinguish powertypes from simple enums

  * eg LeaseItemType vs UnitType
  
* throughout: check all types implement Comparable

* `LeaseUnit`'s brand/sector/activity are all free format; is this correct?

* rename `PaymentMethod` to `PaymentMethodType` ?

* should `PaymentMethod` be an attribute of `BankAccount` ?

* cycle: `Lease` - `LeaseItem` - `LeaseType` - `InvoiceItem` - `Invoice` - `Lease`

* cycle: `PaymentMethod` - `LeaseItem` - `LeaseType` - `InvoiceItem` - `Invoice` - `PaymentMethod`

* is `InvoiceItem` -> `Invoice` really optional?  code suggests it isn't

* should `LeaseTerm` be doubly linked next/previous?  could derive one of these (the DB schema will be ugly if don't)

* why does `InvoiceItem` hold `Charge` as well as `LeaseItem` ?

* the semantics of `InvoiceItem#verify()` are different (I think) from `LeaseTerm#verify()`; rename one of them

* can `Lease*#verify()` be renamed: it seems more like "bringUpToDate()"

* can `LeaseTerm#update()` be renamed: it seems more like "ensureAdjacentWithNext()"

* are `Event`s in scope for Sept; weren't mentioned in Milan?

* are `LeaseAssignment`s in scope for Sept; weren't mentioned in Milan?

* should `IndexBase` be doubly linked next/previous?  could derive one of these (the DB schema will be ugly if don't)
