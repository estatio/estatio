@EstatioTransactionalObjectsFixture
Feature: Update the start/end dates of an existing AgreementRole

  The start and end dates of an AgreementRole may be updated, however they 
  cannot be moved beyond bounds of the immediate predecessor or successor.
  
  In other words, the start date of a role can move earlier, but not earlier 
  than the start date of any predecessor.

  Similarly, the end date of a role can move later, but not later 
  than the end date of any successor.


  Background:
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 


#  @integration
#  Scenario: Can update start and end dates of initial role from null
#  
#    Given the lease's roles collection contains:
#          | type     | start date | end date | party  | agreement    | indicated |
#          | Tenant   | null       | null     | PRET   | OXF-PRET-004 | *         |
#    And   I want to update the dates on the indicated agreement role
#    
#    When  I invoke the action, start date "2013-4-1", end date "2014-9-1"
#     
#    Then  the lease's roles collection should contain:
#          | type     | start date | end date | party  | agreement    |
#          | Tenant   | 2013-4-1   | 2014-9-1 | PRET   | OXF-PRET-004 |
#
#
#  @integration
#  Scenario: Cannot update start date to null if has predecessor
#
#    Given the lease's roles collection contains:
#          | type     | start date | end date | party  | agreement    | indicated |
#          | Tenant   | 2013-4-1   | null     | PRET   | OXF-PRET-004 | *         |
#          | Tenant   | null       | 2013-4-1 | POISON | OXF-PRET-004 |           |
#    And   I want to update the dates on the indicated agreement role
#          
#    When  I attempt to invoke the action, start date to "null", end date unchanged as "null"
#
#    Then  the action is invalid with message "Start date cannot be set to null if there is a predecessor"
#    And   the lease's roles collection should contain:
#          | type     | start date | end date | party  | agreement    |
#          | Tenant   | 2013-4-1   | null     | PRET   | OXF-PRET-004 |
#          | Tenant   | null       | 2013-4-1 | POISON | OXF-PRET-004 |


  @integration
  Scenario: Cannot update end date to null if has successor

    Given the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | null     | POISON | OXF-PRET-004 |           |
          | Tenant   | null       | 2013-4-1 | PRET   | OXF-PRET-004 | *         |
    And   I want to update the dates on the indicated agreement role
          
    When  I attempt to invoke the action, start date unchanged as "null", end date to "null"

    Then  the action is invalid with message "End date cannot be set to null if there is a successor"
    And   the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | null     | POISON | OXF-PRET-004 |
          | Tenant   | null       | 2013-4-1 | PRET   | OXF-PRET-004 |


  @integration
  Scenario: Can update start and end dates of initial role to null
  
    Given the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | 2914-9-1 | PRET   | OXF-PRET-004 | *         |
    And   I want to update the dates on the indicated agreement role
    
    When  I invoke the action, start date "null", end date "null"
    
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 |


  @integration
  Scenario: Can update start date when no predecessor
  
    Given the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | null     | PRET   | OXF-PRET-004 | *         |
    And   I want to update the dates on the indicated agreement role
    
    When  I invoke the action, start date "2012-3-1", end date unchanged as "null"
    
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2012-3-1   | null     | PRET   | OXF-PRET-004 |


  @integration
  Scenario: Can update end date when no successor
  
    Given the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | null       | 2013-4-1 | PRET   | OXF-PRET-004 | *         |
    And   I want to update the dates on the indicated agreement role
          
    When  I invoke the action, start date unchanged as "null", end date to "2013-5-1"
    
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | null       | 2013-5-1 | PRET   | OXF-PRET-004 |


  @integration
  Scenario: Can make start date earlier if has predecessor, when predecessor has no start date

    Given the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | null     | PRET   | OXF-PRET-004 | *         |
          | Tenant   | null       | 2013-4-1 | POISON | OXF-PRET-004 |           |
    And   I want to update the dates on the indicated agreement role
          
    When  I invoke the action, start date to "2010-9-1", end date unchanged as "null"

    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2010-9-1   | null     | PRET   | OXF-PRET-004 |
          | Tenant   | null       | 2010-9-1 | POISON | OXF-PRET-004 |


  @integration
  Scenario: Can make end date later if has successor, when successor's has no end date

    Given the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | null     | POISON | OXF-PRET-004 |           |
          | Tenant   | null       | 2013-4-1 | PRET   | OXF-PRET-004 | *         |
    And   I want to update the dates on the indicated agreement role
          
    When  I invoke the action, start date unchanged as "null", end date to "2015-9-1"

    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2015-9-1   | null     | POISON | OXF-PRET-004 |
          | Tenant   | null       | 2015-9-1 | PRET   | OXF-PRET-004 |


  @integration
  Scenario: Can make start date earlier if has predecessor, but not beyond predecessor's own start date
  
    Given the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | null     | PRET   | OXF-PRET-004 | *         |
          | Tenant   | 2012-3-1   | 2013-4-1 | POISON | OXF-PRET-004 |           |
    And   I want to update the dates on the indicated agreement role
          
    When  I invoke the action, start date to "2012-9-1", end date unchanged as "null"
    
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2012-9-1   | null     | PRET   | OXF-PRET-004 |
          | Tenant   | 2012-3-1   | 2012-9-1 | POISON | OXF-PRET-004 |

  @integration
  Scenario: Can make end date later if has successor, but not beyond successor's own end date
  
    Given the lease's roles collection contains:
          | type     | start date | end date  | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | 2015-9-1  | POISON | OXF-PRET-004 |           |
          | Tenant   | null       | 2013-4-1  | PRET   | OXF-PRET-004 | *         |
    And   I want to update the dates on the indicated agreement role
          
    When  I invoke the action, start date unchanged as "null", end date to "2015-8-31"

    Then  the lease's roles collection should contain:
          | type     | start date | end date  | party  | agreement    |
          | Tenant   | 2015-8-31  | 2015-9-1  | POISON | OXF-PRET-004 |
          | Tenant   | null       | 2015-8-31 | PRET   | OXF-PRET-004 |


  @backlog
  @integration
  Scenario: Cannot update start date earlier than predecessor's start date
  
    Given the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | null     | PRET   | OXF-PRET-004 | *         |
          | Tenant   | 2012-3-1   | 2013-4-1 | POISON | OXF-PRET-004 |           |
    And   I want to update the dates on the indicated agreement role
          
    When  I attempt to invoke the action, start date earlier than predecessor as "2012-3-1", end date unchanged as "null"

    Then  the action is invalid with message "Start date cannot be on/before start of current predecessor"
    And   the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | null     | PRET   | OXF-PRET-004 |
          | Tenant   | 2012-3-1   | 2013-4-1 | POISON | OXF-PRET-004 |


  @integration
  Scenario: Cannot update end date later than successor's end date
  
    Given the lease's roles collection contains:
          | type     | start date | end date  | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | 2015-9-1  | POISON | OXF-PRET-004 |           |
          | Tenant   | null       | 2013-4-1  | PRET   | OXF-PRET-004 | *         |
    And   I want to update the dates on the indicated agreement role
          
    When  I attempt to invoke the action, start date unchanged as "null", end date later than successor as "2015-9-1"
     
    Then  the action is invalid with message "End date cannot be on/after end of current successor"
    And   the lease's roles collection should contain:
          | type     | start date | end date  | party  | agreement    |
          | Tenant   | 2013-4-1   | 2015-9-1  | POISON | OXF-PRET-004 |
          | Tenant   | null       | 2013-4-1  | PRET   | OXF-PRET-004 |


  @integration
  Scenario: Cannot make start date later than end date
  
    Given the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | 2014-5-1 | PRET   | OXF-PRET-004 | *         |
          | Tenant   | 2012-3-1   | 2013-4-1 | POISON | OXF-PRET-004 |           |
    And   I want to update the dates on the indicated agreement role
          
    When  I attempt to invoke the action, start date "2013-5-1", end date same as start, ie "2013-5-1"
          
    Then  the action is invalid with message "Start date cannot be on/after the end date"
    And   the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | 2014-5-1 | PRET   | OXF-PRET-004 |
          | Tenant   | 2012-3-1   | 2013-4-1 | POISON | OXF-PRET-004 |



