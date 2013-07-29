@EstatioTransactionalObjectsFixture
Feature: Remove the last role within an Agreement's role collection

  This is the corollary of the Agreement's addRole action.

  Background:
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 


  @backlog
  @integration
  Scenario: Remove last role
    Given there is a lease "OXF-PRET-004"
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 | *         |
    When  I remove the indicated agreement role
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |


  @backlog
  @integration
  Scenario: Remove last role (when different role types)
    Given there is a lease "OXF-PRET-004"
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Landlord | 2013-4-1   | null     | POISON | OXF-PRET-004 |           |
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 | *         |
    When  I remove the indicated agreement role
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Landlord | 2013-4-1   | null     | POISON | OXF-PRET-004 | 


  @backlog
  @integration
  Scenario: Cannot remove role if more than one role of existing type
    Given there is a lease "OXF-PRET-004"
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Landlord | 2013-4-1   | null     | POISON | OXF-PRET-004 | 
          | Landlord | null       | 2013-4-1 | PRET   | OXF-PRET-004 | *         |
    When  I attempt to remove the indicated agreement role
    Then  the action is disabled with message "Only the first role of any type can be explicitly removed" 
    And   the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Landlord | 2013-4-1   | null     | POISON | OXF-PRET-004 | 
          | Landlord | null       | 2013-4-1 | PRET   | OXF-PRET-004 | *         |
