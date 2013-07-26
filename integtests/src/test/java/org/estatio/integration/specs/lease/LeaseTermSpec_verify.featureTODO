@LeasesOnlyFixture
Feature: Verify Lease terms with IndexableRent and ServiceCharge items

    # corresponds to: 
    # LeaseTermTest_verify_and_InvoiceItems_calculate#t10_leaseTermRent_verify
    @integration
    @backlog
    Scenario: Verify rent term
      Given there is a lease "OXF-TOPMODEL-001"
      And   the lease's start date is "2010-7-15"
      And   the lease's items are:
             | type          | charge         | invoicingFrequency   |   
             | Rent          | RENT           | QUARTERLY_IN_ADVANCE |
      And   the lease's "Rent" item's terms collection (of rent terms) contains:
             | startDate | endDate | sequence | baseValue | baseIndexStartDate | nextIndexStartDate | effectiveDate | indexReference |   
             | 2010-7-15 | null    |        1 |  20000.00 | 2010-7-1           | 2011-1-1           | 2011-4-11     | ISTAT-FOI      |
      And   the index "ISTAT-FOI"'s nextDate with respect to the "Rent" term is "2012-1-1" with nextIndexValue "101.2000"
      When  I verify the "Rent" term
      Then  the lease's "Rent" item's terms collection (of rent terms) contains:
             | startDate | endDate | sequence | baseValue | baseIndexValue | nextIndexValue | indexationPercentage | indexedValue |   
             | 2010-7-15 | null    |        1 |  20000.00 |       137.6000 |       101.2000 |                  1.0 |   20200.0000 |
    
    
    # review: the integration tests don't really seem 
    # to assert very much for this...
    @backlog
    @integration
    Scenario: Verify service charge term
      Given I have a lease "OXF-TOPMODEL-001"
      And   the lease's start date is "2010-7-15"
      And   the lease's items are:
             | type          | charge         | invoicingFrequency   |   
             | ServiceCharge | SERVICE_CHARGE | QUARTERLY_IN_ADVANCE |
      And   the lease's "ServiceCharge" item's terms collection (of service charge terms) contains:
             | startDate | endDate | budgetedValue |   
             | 2010-7-15 | null    |       6000.00 |
      When  I verify the "ServiceCharge" term
      Then  the lease's "ServiceCharge" item's terms collection (of service charge terms) contains:
             | startDate | endDate | budgetedValue |   
             | 2010-7-15 | null    |       6000.00 |
  