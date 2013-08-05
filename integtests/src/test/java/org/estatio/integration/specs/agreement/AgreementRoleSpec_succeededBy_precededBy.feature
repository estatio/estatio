@EstatioTransactionalObjectsFixture
Feature: For an Agreement, specify a successor or predecessor to an existing role, starting contiguously

  An Agreement's roles run contiguously per type.
  

  Background:
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   there is a party "POISON" 
    And   there is a party "TOPMODEL" 



  @integration
  Scenario: Can add a successor role for role with null end date
  
    Given the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 | *         |
    
    When  I want to add a successor to the indicated agreement role
    Then  the default for the "start date" date parameter is "null"
    
    When  I invoke the action, with start date "2013-4-1", end date "null", for party "POISON"
    Then  the lease's roles collection should contain:
          | type     | start date | end date  | party  | agreement    |
          | Tenant   | 2013-4-1   | null      | POISON | OXF-PRET-004 | 
          | Tenant   | null       | 2013-3-31 | PRET   | OXF-PRET-004 |
 
 
  @integration
  Scenario: Can add a predecessor role for role with null start date
  
    Given the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 | *         |
    
    When  I want to add a predecessor to the indicated agreement role
    Then  the default for the "end date" date parameter is "null"
    
    When  I invoke the action, with start date "null", end date "2013-4-1", for party "POISON"
    Then  the lease's roles collection should contain:
          | type     | start date | end date  | party  | agreement    |
          | Tenant   | 2013-4-2   | null      | PRET   | OXF-PRET-004 | 
          | Tenant   | null       | 2013-4-1  | POISON | OXF-PRET-004 |



  @integration
  Scenario: Can add a successor role for role with an end date
  
    Given the lease's roles collection contains:
          | type     | start date | end date  | party  | agreement    | indicated |
          | Tenant   | null       | 2013-3-31 | PRET   | OXF-PRET-004 | *         |
     
    When  I want to add a successor to the indicated agreement role
    Then  the default for the "start date" date parameter is "2013-4-1"
    
    When  I invoke the action, with start date "2013-4-1", end date "null", for party "POISON"
    Then  the lease's roles collection should contain:
          | type     | start date | end date  | party  | agreement    |
          | Tenant   | 2013-4-1   | null      | POISON | OXF-PRET-004 | 
          | Tenant   | null       | 2013-3-31 | PRET   | OXF-PRET-004 |


  @integration
  Scenario: Can add a predecessor role for role with a start date
    Given the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | null     | PRET   | OXF-PRET-004 | *         |
          
    When  I want to add a predecessor to the indicated agreement role
    Then  the default for the "end date" date parameter is "2013-3-31"
          
    When  I invoke the action, with start date "null", end date "2013-3-31", for party "POISON" 
    Then  the lease's roles collection should contain:
          | type     | start date | end date  | party  | agreement    |
          | Tenant   | 2013-4-1   | null      | PRET   | OXF-PRET-004 |
          | Tenant   | null       | 2013-3-31 | POISON | OXF-PRET-004 | 


  @integration
  Scenario: Can change the start date for successor, updates existing's end date
  
    Given the lease's roles collection contains:
          | type     | start date | end date  | party  | agreement    | indicated |
          | Tenant   | null       | 2013-3-31 | PRET   | OXF-PRET-004 | *         |
    And   I want to add a successor to the indicated agreement role
    
    When  I invoke the action, with start date "2013-4-2", end date "null", for party "POISON"
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-2   | null     | POISON | OXF-PRET-004 | 
          | Tenant   | null       | 2013-4-1 | PRET   | OXF-PRET-004 |



  @integration
  Scenario: Can change the start date for predecessor, updates existing's start date
  
    Given the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | null     | PRET   | OXF-PRET-004 | *         |
    And   I want to add a predecessor to the indicated agreement role
    
    When  I invoke the action, with start date "null", end date "2013-4-2", for party "POISON"
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-3   | null     | PRET   | OXF-PRET-004 | 
          | Tenant   | null       | 2013-4-2 | POISON | OXF-PRET-004 |


  @integration
  Scenario: Successor must start after existing's start date
  
    Given the lease's roles collection contains:
          | type     | start date | end date  | party  | agreement    | indicated |
          | Tenant   | 2012-2-1   | 2013-3-31 | PRET   | OXF-PRET-004 | *         |
    And   I want to add a successor to the indicated agreement role
    
    When  I attempt to invoke the action, with start date "2012-2-1", end date "null", for party "POISON"
    
    Then  the action is invalid with message "Successor must start after existing"
    And   the lease's roles collection should contain:
          | type     | start date | end date  | party  | agreement    |
          | Tenant   | 2012-2-1   | 2013-3-31 | PRET   | OXF-PRET-004 |


  @integration
  Scenario: Predecessor must begin before existing's end date
  
    Given the lease's roles collection contains:
          | type     | start date | end date  | party  | agreement    | indicated |
          | Tenant   | 2012-2-1   | 2013-3-31 | PRET   | OXF-PRET-004 | *         |
    And   I want to add a predecessor to the indicated agreement role
    
    When  I attempt to invoke the action, with start date "null", end date "2013-3-31", for party "POISON"
    
    Then  the action is invalid with message "Predecessor must end before existing"
    And   the lease's roles collection should contain:
          | type     | start date | end date  | party  | agreement    |
          | Tenant   | 2012-2-1   | 2013-3-31 | PRET   | OXF-PRET-004 |


  @integration
  Scenario: Can add a successor role for role with an end date that already has a successor
  
    Given the lease's roles collection contains:
          | type     | start date | end date  | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | null      | POISON | OXF-PRET-004 |           |
          | Tenant   | null       | 2013-3-31 | PRET   | OXF-PRET-004 | *         |
    And   I want to add a successor to the indicated agreement role
    
    When  I invoke the action, with start date "2012-4-1", end date "2014-4-30", for party "TOPMODEL"

    Then  the lease's roles collection should contain:
          | type     | start date | end date  | party    | agreement    |
          | Tenant   | 2014-5-1   | null      | POISON   | OXF-PRET-004 | 
          | Tenant   | 2012-4-1   | 2014-4-30 | TOPMODEL | OXF-PRET-004 | 
          | Tenant   | null       | 2012-3-31 | PRET     | OXF-PRET-004 |


  @integration
  Scenario: Can add a predecessor role for role with an end date that already has a predecessor
  
    Given the lease's roles collection contains:
          | type     | start date | end date  | party    | agreement    | indicated |
          | Tenant   | 2013-4-1   | null      | PRET     | OXF-PRET-004 | *         |
          | Tenant   | null       | 2013-3-31 | POISON   | OXF-PRET-004 |           |
          
    And   I want to add a predecessor to the indicated agreement role
          
    When  I invoke the action, with start date "2010-5-1", end date "2013-3-31", for party "TOPMODEL"
     
    Then  the lease's roles collection should contain:
          | type     | start date | end date  | party    | agreement    |
          | Tenant   | 2013-4-1   | null      | PRET     | OXF-PRET-004 | 
          | Tenant   | 2010-5-1   | 2013-3-31 | TOPMODEL | OXF-PRET-004 |
          | Tenant   | null       | 2010-4-30 | POISON   | OXF-PRET-004 | 


  @integration
  Scenario: Cannot add a successor role for role that already has a successor without specifying an end date

    Given the lease's roles collection contains:
          | type     | start date | end date  | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | null      | POISON | OXF-PRET-004 |           |
          | Tenant   | null       | 2013-3-31 | PRET   | OXF-PRET-004 | *         |
    And   I want to add a successor to the indicated agreement role
    
    When  I attempt to invoke the action, with start date "2012-4-1", end date "null", for party "TOPMODEL"

    Then  the action is invalid with message "An end date is required because a successor already exists"
    And   the lease's roles collection should contain:
          | type     | start date | end date  | party    | agreement    |
          | Tenant   | 2013-4-1   | null      | POISON   | OXF-PRET-004 |
          | Tenant   | null       | 2013-3-31 | PRET     | OXF-PRET-004 |


  @integration
  Scenario: Cannot add a predecessor role for role that already has a predecessor without specifying a start date

    Given the lease's roles collection contains:
          | type     | start date | end date  | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | null      | PRET   | OXF-PRET-004 | *         |
          | Tenant   | null       | 2013-3-31 | POISON | OXF-PRET-004 |           |
    And   I want to add a predecessor to the indicated agreement role
    
    When  I attempt to invoke the action, with start date "null", end date "2012-3-31", for party "TOPMODEL"

    Then  the action is invalid with message "A start date is required because a predecessor already exists"
    And   the lease's roles collection should contain:
          | type     | start date | end date  | party  | agreement    |
          | Tenant   | 2013-4-1   | null      | PRET   | OXF-PRET-004 |
          | Tenant   | null       | 2013-3-31 | POISON | OXF-PRET-004 |



  @integration
  Scenario: Cannot add a successor for role that already has a successor that would span that successor
  
    Given the lease's roles collection contains:
          | type     | start date | end date  | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | 2014-4-30 | POISON | OXF-PRET-004 |           |
          | Tenant   | null       | 2013-3-31 | PRET   | OXF-PRET-004 | *         |
    And   I want to add a successor to the indicated agreement role
    
    When I attempt to invoke the action, with start date "2013-4-1", end date "2014-4-30", for party "TOPMODEL"

    Then  the action is invalid with message "Successor must end prior to existing successor"
    And   the lease's roles collection should contain:
          | type     | start date | end date  | party  | agreement    |
          | Tenant   | 2013-4-1   | 2014-4-30 | POISON | OXF-PRET-004 | 
          | Tenant   | null       | 2013-3-31 | PRET   | OXF-PRET-004 |


  @integration
  Scenario: Cannot add a predecessor role for role that already has a predecessor that would span that predecessor
    Given the lease's roles collection contains:
          | type     | start date | end date  | party  | agreement    | indicated |
          | Tenant   | 2014-5-1   | null      | PRET   | OXF-PRET-004 | *         |
          | Tenant   | 2013-4-1   | 2014-4-30 | POISON | OXF-PRET-004 |           |
    And   I want to add a predecessor to the indicated agreement role
               
    When  I attempt to invoke the action, with start date "2013-4-1", end date "2014-4-30", for party "TOPMODEL"
     
    Then  the action is invalid with message "Predecessor must start after existing predecessor"
    And   the lease's roles collection should contain:
          | type     | start date | end date  | party  | agreement    |
          | Tenant   | 2014-5-1   | null      | PRET   | OXF-PRET-004 |
          | Tenant   | 2013-4-1   | 2014-4-30 | POISON | OXF-PRET-004 | 


  @integration
  Scenario: Cannot add a successor for existing role with same party as existing
  
    Given the lease's roles collection contains:
          | type     | start date | end date  | party  | agreement    | indicated |
          | Tenant   | null       | 2013-3-31 | PRET   | OXF-PRET-004 | *         |
    And   I want to add a successor to the indicated agreement role
    
    When  I attempt to invoke the action, with start date "2013-4-1", end date "null", for the same party "PRET"
     
    Then  the action is invalid with message "Successor's party cannot be the same as this object's party"
    And   the lease's roles collection should contain:
          | type     | start date | end date  | party  | agreement    |
          | Tenant   | null       | 2013-3-31 | PRET   | OXF-PRET-004 |


  @integration
  Scenario: Cannot add a predecessor for existing role with same party as existing
  
    Given the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | null     | PRET   | OXF-PRET-004 | *         |
    And   I want to add a predecessor to the indicated agreement role
          
    When  I attempt to invoke the action, with start date "null", end date "2013-3-30", for the same party "PRET"
     
    Then  the action is invalid with message "Predecessor's party cannot be the same as this object's party"
    And   the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | null     | PRET   | OXF-PRET-004 |


  @integration @test
  Scenario: Cannot add a successor role for role that already has a successor of same party
  
    Given the lease's roles collection contains:
          | type     | start date | end date  | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | 2014-4-30 | POISON | OXF-PRET-004 |           | 
          | Tenant   | null       | 2013-3-31 | PRET   | OXF-PRET-004 | *         |

    And   I want to add a successor to the indicated agreement role
    
    When  I attempt to invoke the action, with start date "2013-4-1", end date "2013-9-30", for the existing successor's party "POISON"
     
    Then  the action is invalid with message "Successor's party cannot be the same as that of existing successor"
    And   the lease's roles collection should contain:
          | type     | start date | end date  | party  | agreement    |
          | Tenant   | 2013-4-1   | 2014-4-30 | POISON | OXF-PRET-004 | 
          | Tenant   | null       | 2013-3-31 | PRET   | OXF-PRET-004 |


  @integration
  Scenario: Cannot add a predecessor role for role that already has a predecessor of same party
    Given the lease's roles collection contains:
          | type     | start date | end date  | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | 2014-4-30 | PRET   | OXF-PRET-004 | *         |
          | Tenant   | null       | 2013-3-31 | POISON | OXF-PRET-004 |           |
    And   I want to add a predecessor to the indicated agreement role
           
    When  I attempt to invoke the action, with start date "2013-2-1", end date "2013-3-31", for the existing predecessor's party "POISON"
     
    Then  the action is invalid with message "Predecessor's party cannot be the same as that of existing predecessor"
    And   the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | 2014-4-30 | PRET   | OXF-PRET-004 | 
          | Tenant   | null       | 2013-3-31 | POISON | OXF-PRET-004 |




  @integration
  Scenario: Cannot add a successor role which ends before it starts

    Given the lease's roles collection contains:
          | type     | start date | end date  | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | null      | POISON | OXF-PRET-004 |           |
          | Tenant   | null       | 2013-3-31 | PRET   | OXF-PRET-004 | *         |
    And   I want to add a successor to the indicated agreement role
    
    When  I attempt to invoke the action, with start date "2013-4-1", end date "2013-3-31", for party "TOPMODEL"

    Then  the action is invalid with message "End date cannot be earlier than start date"
    And   the lease's roles collection should contain:
          | type     | start date | end date  | party    | agreement    |
          | Tenant   | 2013-4-1   | null      | POISON   | OXF-PRET-004 |
          | Tenant   | null       | 2013-3-31 | PRET     | OXF-PRET-004 |

  @integration
  Scenario: Cannot add a predecessor role which ends before it starts

    Given the lease's roles collection contains:
          | type     | start date | end date  | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | null      | POISON | OXF-PRET-004 | *         |
          | Tenant   | null       | 2013-3-31 | PRET   | OXF-PRET-004 |           |
    And   I want to add a predecessor to the indicated agreement role
    
    When  I attempt to invoke the action, with start date "2013-4-1", end date "2013-3-31", for party "TOPMODEL"

    Then  the action is invalid with message "End date cannot be earlier than start date"
    And   the lease's roles collection should contain:
          | type     | start date | end date  | party    | agreement    |
          | Tenant   | 2013-4-1   | null      | POISON   | OXF-PRET-004 |
          | Tenant   | null       | 2013-3-31 | PRET     | OXF-PRET-004 |







