@EstatioTransactionalObjectsFixture
Feature: Manage AgreementRoles for an Agreement

   The first role in the roles collection of an agreement is added using the
   'addRole' action on the agreement.
   
   

  Background:
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 


  @integration
  Scenario: Can add a first role with no start/end date
  
    Given   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          
    When  I add a new agreement role of type "Landlord", start date "null", end date "null", for party "PRET"
     
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Landlord | null       | null     | PRET   | OXF-PRET-004 | 


  @integration
  Scenario: Can add a first role with start date only
  
    Given the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          
    When  I add a new agreement role of type "Landlord", start date "2013-4-1", end date "null", for party "PRET"
     
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Landlord | 2013-4-1   | null     | PRET   | OXF-PRET-004 | 


  @integration
  Scenario: Can add a first role with end date only
  
    Given the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          
    When  I add a new agreement role of type "Landlord", start date "null", end date "2013-4-1", for party "PRET"
     
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Landlord | null       | 2013-4-1 | PRET   | OXF-PRET-004 | 


  @integration
  Scenario: Can add a first role with a start date and an end date
  
    Given the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          
    When  I add a new agreement role of type "Landlord", start date "2012-3-1", end date "2013-4-1", for party "PRET"
     
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Landlord | 2012-3-1   | 2013-4-1 | PRET   | OXF-PRET-004 | 


  @integration
  Scenario: Can add another role of a different type
  
    Given the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Landlord | 2012-3-1   | null     | PRET   | OXF-PRET-004 |
           
    When  I attempt to add a new agreement role of type "Tenant", start date "null", end date "2013-3-1", for party "POISON"
     
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Landlord | 2012-3-1   | null     | PRET   | OXF-PRET-004 | 
          | Tenant   | null       | 2013-3-1 | POISON | OXF-PRET-004 | 


  @integration
  Scenario: Cannot add a first role of same type that starts/ends on same date
  
    Given the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          
    When  I attempt to add a new agreement role of type "Landlord", start date "2013-3-1", end date "2013-3-1", for party "PRET"
     
    Then  the action is invalid with message "End date must be after start date"
    And   the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |


  @integration
  Scenario: Cannot add a second role of same type when one already added, even if would be contiguous
  
    Given the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Landlord | 2012-3-1   | 2013-4-1 | PRET   | OXF-PRET-004 |
           
    When  I attempt to add a new agreement role of type "Landlord", start date "2013-4-1", end date "2014-5-1", for party "POISON" 
    Then  the action is invalid with message "Add a successor/predecessor to existing agreement role"
    
    And   the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Landlord | 2012-3-1   | 2013-4-1 | PRET   | OXF-PRET-004 | 


  @integration
  Scenario: Add role when agreement already has role of one type, with new role of different type
  
    Given the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 |
           
    When  I add a new agreement role of type "Landlord", start date "null", end date "null", for party "POISON"
     
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Landlord | null       | null     | POISON | OXF-PRET-004 | 
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 | 


  @integration
  Scenario: Add role when agreement already has role of one type, with new role of different type that has a start date
  
    Given the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 |
           
    When  I add a new agreement role of type "Landlord", start date "2013-4-1", end date "null", for party "POISON"
     
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Landlord | 2013-4-1   | null     | POISON | OXF-PRET-004 | 
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 | 


