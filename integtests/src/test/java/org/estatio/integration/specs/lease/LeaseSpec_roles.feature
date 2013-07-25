@EstatioTransactionalObjectsFixture
Feature: Manage AgreementRoles for an Agreement


# Dan's suggestion: so far as possible, act on the role object, not the parent lease
#
# agreemement#addRole                  - only available to add the (first) role (of a given type), ie special case
# agreementRole#succeedBy              - adds the successor; adjusts any existing successor (but new successor cannot end after current successor)
# agreementRole#precedeBy              - adds the predecessor; adjusts any existing predecessor (but new predecessor cannot start before current predecessor)
# agreementRole#updateStartDate        - adjusts, along with end date of predecessor (if any)
# agreementRole#updateEndDate          - adjusts, along with end date of successor (if any)
# agreementRole#replaceWithPredecessor - removes, predecessor end date moving later to the end date of the deleted role
# agreementRole#replaceWithSuccessor   - removes, successor's start date moving earlier to start date of the deleted role
# agreement#removeRole                 - only available to remove the (first) role (of a given type), ie special case
#

# on discussing with Jeroen: 
# do need to add delegate methods so that the common case (add successor) is available from the parent (Lease) object


#############################################################################
# add first role
#############################################################################

  #@unit
  @integration
  Scenario: Can add a first role with no start/end date
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
    When  I add a new agreement role of type "Landlord", start date "null", end date "null", for this party 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Landlord | null       | null     | PRET   | OXF-PRET-004 | 

  #@unit
  @integration
  Scenario: Can add a first role with start date only
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
    When  I add a new agreement role of type "Landlord", start date "2013-4-1", end date "null", for this party 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Landlord | 2013-4-1   | null     | PRET   | OXF-PRET-004 | 


  #@unit
  @integration
  Scenario: Can add a first role with end date only
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
    When  I add a new agreement role of type "Landlord", start date "null", end date "2013-4-1", for this party 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Landlord | null       | 2013-4-1 | PRET   | OXF-PRET-004 | 


  #@unit
  @integration
  Scenario: Can add a first role with a start date and an end date
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
    When  I add a new agreement role of type "Landlord", start date "2012-3-1", end date "2013-4-1", for this party 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Landlord | 2012-3-1   | 2013-4-1 | PRET   | OXF-PRET-004 | 


  #@unit
  @integration
  Scenario: Can add another role of a different type
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Landlord | 2012-3-1   | null     | PRET   | OXF-PRET-004 | 
    And   there is a party "POISON" 
    When  I attempt to add a new agreement role of type "Tenant", start date "null", end date "2013-3-1", for this party 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Landlord | 2012-3-1   | null     | PRET   | OXF-PRET-004 | 
          | Tenant   | null       | 2013-3-1 | POISON | OXF-PRET-004 | 


  #@unit
  @backlog
  @integration
  Scenario: Cannot add a first role of same type that starts/ends on same date
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
    When  I attempt to add a new agreement role of type "Landlord", start date "2013-3-1", end date "2013-3-1", for this party 
    Then  the action is disabled with message "End date must be after start date"
    And   the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |

  #@unit
  @backlog
  @integration
  Scenario: Cannot add a second role of same type when one already added (even if would be contiguous)
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Landlord | 2012-3-1   | 2013-4-1 | PRET   | OXF-PRET-004 | 
    When  I attempt to add a new agreement role of type "Landlord", start date "2013-4-1", end date "2014-5-1", for this party 
    Then  the action is disabled with message "Use existing agreement role to add a successor or predecessor"
    And   the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Landlord | 2012-3-1   | 2013-4-1 | PRET   | OXF-PRET-004 | 

  #@unit
  @backlog
  @integration
  Scenario: Add role when agreement already has role of one type, with new role of different type
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 | 
    And   there is a party "POISON" 
    When  I add a new agreement role of type "Landlord", start date "null", end date "null", for this party 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Landlord | null       | null     | POISON | OXF-PRET-004 | 
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 | 

  #@unit
  @backlog
  @integration
  Scenario: Add role when agreement already has role of one type, with new role of different type that has a start date
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 | 
    And   there is a party "POISON" 
    When  I add a new agreement role of type "Landlord", start date "2013-4-1", end date "null", for this party 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Landlord | 2013-4-1   | null     | POISON | OXF-PRET-004 | 
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 | 



#############################################################################
# succeeded by
#############################################################################

  #@unit
  @backlog
  @integration
  Scenario: Can add a successor role for role with null end date
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 | *         |
    And   there is a party "POISON" 
    When  I add a successor to the indicated agreement role, with start date "2013-4-1", end date "null", for this party 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | null     | POISON | OXF-PRET-004 | 
          | Tenant   | null       | 2013-4-1 | PRET   | OXF-PRET-004 |


  #@unit
  @backlog
  @integration
  Scenario: Can add a successor role for role with an end date
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | null       | 2013-4-1 | PRET   | OXF-PRET-004 | *         |
    And   there is a party "POISON" 
    When  I add a successor to the indicated agreement role, with start date "2013-4-1", end date "null", for this party 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | null     | POISON | OXF-PRET-004 | 
          | Tenant   | null       | 2013-4-1 | PRET   | OXF-PRET-004 |


  #@unit
  @backlog
  @integration
  Scenario: Can add a successor role for role with an end date that already has a successor
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | null     | POISON | OXF-PRET-004 | 
          | Tenant   | null       | 2013-4-1 | PRET   | OXF-PRET-004 | *         |
    And   there is a party "TOPMODEL" 
    When  I add a successor to the indicated agreement role, with start date "2013-4-1", end date "2014-5-1", for this party 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party    | agreement    |
          | Tenant   | 2014-5-1   | null     | POISON   | OXF-PRET-004 | 
          | Tenant   | 2013-4-1   | 2014-5-1 | TOPMODEL | OXF-PRET-004 | 
          | Tenant   | null       | 2013-4-1 | PRET     | OXF-PRET-004 |


  #@unit
  @backlog
  @integration
  Scenario: Cannot add a successor role for role that already has a successor that would span that successor
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | 2014-5-1 | POISON | OXF-PRET-004 | 
          | Tenant   | null       | 2013-4-1 | PRET   | OXF-PRET-004 | *         |
    And   there is a party "TOPMODEL" 
    When  I attempt to add a successor to the indicated agreement role, with start date "2013-4-1", end date "2014-5-1", for this party 
    Then  the action is invalid with message "Proposed successor would spans an existing successor"
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | 2014-5-1 | POISON | OXF-PRET-004 | 
          | Tenant   | null       | 2013-4-1 | PRET   | OXF-PRET-004 |



  #@unit
  @backlog
  @integration
  Scenario: Cannot add a successor for existing role with same party as existing
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | null       | 2013-4-1 | PRET   | OXF-PRET-004 | *         |
    When  I attempt to add a successor to the indicated agreement role, with start date "2013-4-1", end date "2013-9-1", for the same party 
    Then  the action is invalid with message "Party of proposed successor cannot be the same as the party of this role"
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | null       | 2013-4-1 | PRET   | OXF-PRET-004 |

  #@unit
  @backlog
  @integration
  Scenario: Cannot add a successor role for role that already has a successor of same party
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | 2014-5-1 | POISON | OXF-PRET-004 |           | 
          | Tenant   | null       | 2013-4-1 | PRET   | OXF-PRET-004 | *         |
    And   there is a party "POISON" 
    When  I attempt to add a successor to the indicated agreement role, with start date "2013-4-1", end date "2013-9-1", for the successors party 
    Then  the action is invalid with message "The party of the proposed successor cannot be the same as the party of this role's current successor"
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | 2014-5-1 | POISON | OXF-PRET-004 | 
          | Tenant   | null       | 2013-4-1 | PRET   | OXF-PRET-004 |



#############################################################################
# preceded by
# (symmetric with succeeded by)
#############################################################################

  #@unit
  @backlog
  @integration
  Scenario: Can add a predecessor role for role with null start date
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 | *         |
    And   there is a party "POISON" 
    When  I add a predecessor to the indicated agreement role, with start date "null", end date "2013-4-1", for this party 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | null     | PRET   | OXF-PRET-004 |
          | Tenant   | null       | 2013-4-1 | POISON | OXF-PRET-004 | 


  #@unit
  @backlog
  @integration
  Scenario: Can add a predecessor role for role with a start date
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | null     | PRET   | OXF-PRET-004 | *         |
    And   there is a party "POISON" 
    When  I add a predecessor to the existing agreement role, with start date "null", end date "2013-4-1", for this party 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | null     | PRET   | OXF-PRET-004 |
          | Tenant   | null       | 2013-4-1 | POISON | OXF-PRET-004 | 


  #@unit
  @backlog
  @integration
  Scenario: Can add a predecessor role for role with an end date that already has a predecessor
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party    | agreement    | indicated |
          | Tenant   | 2013-4-1   | null     | PRET     | OXF-PRET-004 | *         |
          | Tenant   | null       | 2013-4-1 | POISON   | OXF-PRET-004 |           |
    And   there is a party "TOPMODEL" 
    When  I add a predecessor to the indicated agreement role, with start date "2010-5-1", end date "2013-4-1", for this party 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party    | agreement    |
          | Tenant   | 2014-5-1   | null     | PRET     | OXF-PRET-004 | 
          | Tenant   | 2010-5-1   | 2013-4-1 | TOPMODEL | OXF-PRET-004 |
          | Tenant   | null       | 2010-5-1 | POISON   | OXF-PRET-004 | 


  #@unit
  @backlog
  @integration
  Scenario: Cannot add a predecessor role for role that already has a predecessor that would span that predecessor
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | 2014-5-1   | null     | PRET   | OXF-PRET-004 | *         |
          | Tenant   | 2013-4-1   | 2014-5-1 | POISON | OXF-PRET-004 | 
    And   there is a party "TOPMODEL" 
    When  I attempt to add a predecessor to the indicated agreement role, with start date "2013-4-1", end date "2014-5-1", for this party 
    Then  the action is invalid with message "Proposed successor would spans an existing successor"
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2014-5-1   | null     | PRET   | OXF-PRET-004 |
          | Tenant   | 2013-4-1   | 2014-5-1 | POISON | OXF-PRET-004 | 



  #@unit
  @backlog
  @integration
  Scenario: Cannot add a predecessor for existing role with same party as existing
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | null     | PRET   | OXF-PRET-004 | *         |
    When  I attempt to add a predecessor to the indicated agreement role, with start date "null", end date "2013-4-1", for the same party 
    Then  the action is invalid with message "Party of proposed predecessor cannot be the same as the party of this role"
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | null     | PRET   | OXF-PRET-004 |


  #@unit
  @backlog
  @integration
  Scenario: Cannot add a predecessor role for role that already has a predecessor of same party
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | 2014-5-1 | PRET   | OXF-PRET-004 | *         |
          | Tenant   | null       | 2013-4-1 | POISON | OXF-PRET-004 |           | 
    And   there is a party "POISON" 
    When  I attempt to add a predecessor to the indicated agreement role, with start date "2013-2-1", end date "2013-4-1", for the predecessor's party 
    Then  the action is invalid with message "The party of the proposed predecessor cannot be the same as the party of this role's current predecessor"
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | 2014-5-1 | POISON | OXF-PRET-004 | 
          | Tenant   | null       | 2013-4-1 | PRET   | OXF-PRET-004 |



#############################################################################
# update start date 
#############################################################################


  #@unit
  @backlog
  @integration
  Scenario: Can update start date of initial role from null
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 | *         |
    When  I update the start date on the indicated agreement role to "2013-4-1" 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | null     | PRET   | OXF-PRET-004 |

  #@unit
  @backlog
  @integration
  Scenario: Can update start date of initial role to null
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | null     | PRET   | OXF-PRET-004 | *         |
    When  I update the start date on the indicated agreement role to "null" 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 |


  #@unit
  @backlog
  @integration
  Scenario: Can update start date when no predecessor
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | null     | PRET   | OXF-PRET-004 | *         |
    When  I update the start date on the indicated agreement role to "2012-3-1" 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-3-1   | null     | PRET   | OXF-PRET-004 |


  #@unit
  @backlog
  @integration
  Scenario: Can make start date earlier if has predecessor, when predecessor has no start date
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | null     | PRET   | OXF-PRET-004 | *         |
          | Tenant   | null       | 2013-4-1 | POISON | OXF-PRET-004 |           |
    When  I update the start date on the indicated agreement role to "2010-9-1" 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2010-9-1   | null     | PRET   | OXF-PRET-004 |
          | Tenant   | null       | 2010-9-1 | POISON | OXF-PRET-004 |

  #@unit
  @backlog
  @integration
  Scenario: Can make start date earlier if has predecessor, but not beyond predecessor's own start date
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | null     | PRET   | OXF-PRET-004 | *         |
          | Tenant   | 2012-3-1   | 2013-4-1 | POISON | OXF-PRET-004 |           |
    When  I update the start date on the indicated agreement role to "2012-9-1" 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2012-9-1   | null     | PRET   | OXF-PRET-004 |
          | Tenant   | 2012-3-1   | 2012-9-1 | POISON | OXF-PRET-004 |

  #@unit
  @backlog
  @integration
  Scenario: Cannot update start date earlier than predecessor's start date
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | null     | PRET   | OXF-PRET-004 | *         |
          | Tenant   | 2012-3-1   | 2013-4-1 | POISON | OXF-PRET-004 |           |
    When  I attempt to update the start date on the indicated agreement role to "2012-3-1" 
    Then  the action is invalid with message "Start date cannot be on/before start of current predecessor"
    And   the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | null     | PRET   | OXF-PRET-004 |
          | Tenant   | 2012-3-1   | 2013-4-1 | POISON | OXF-PRET-004 |


  #@unit
  @backlog
  @integration
  Scenario: Cannot make start date later than own end date
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | 2014-5-1 | PRET   | OXF-PRET-004 | *         |
          | Tenant   | 2012-3-1   | 2013-4-1 | POISON | OXF-PRET-004 |           |
    When  I attempt to update the start date on the indicated agreement role to "2014-5-1" 
    Then  the action is invalid with message "Start date cannot be on/after the end date"
    And   the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | 2014-5-1 | PRET   | OXF-PRET-004 |
          | Tenant   | 2012-3-1   | 2013-4-1 | POISON | OXF-PRET-004 |


#############################################################################
# update end date
# (symmetric with update start date) 
#############################################################################


  #@unit
  @backlog
  @integration
  Scenario: Can update end date of initial role from null
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 | *         |
    When  I update the end date on the indicated agreement role to "2013-4-1" 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | null       | 2013-4-1 | PRET   | OXF-PRET-004 |

  #@unit
  @backlog
  @integration
  Scenario: Can update end date of initial role to null
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | null       | 2013-4-1 | PRET   | OXF-PRET-004 | *         |
    When  I update the start date on the indicated agreement role to "null" 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 |


  #@unit
  @backlog
  @integration
  Scenario: Can update end date when no predecessor
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | null       | 2013-4-1 | PRET   | OXF-PRET-004 | *         |
    When  I update the start date on the indicated agreement role to "2013-5-1" 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | null       | 2013-5-1 | PRET   | OXF-PRET-004 |

  #@unit
  @backlog
  @integration
  Scenario: Can make end date later if has successor when successor's has no end date
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | null     | POISON | OXF-PRET-004 |           |
          | Tenant   | null       | 2013-4-1 | PRET   | OXF-PRET-004 | *         |
    When  I update the start date on the indicated agreement role to "2015-9-1" 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2015-9-1   | null     | PRET   | OXF-PRET-004 |
          | Tenant   | null       | 2015-9-1 | POISON | OXF-PRET-004 |

  #@unit
  @backlog
  @integration
  Scenario: Can make end date later if has successor but not beyond successor's own end date
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date  | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | 2015-9-1  | POISON | OXF-PRET-004 |           |
          | Tenant   | null       | 2013-4-1  | PRET   | OXF-PRET-004 | *         |
    When  I update the end date on the indicated agreement role to "2015-8-31" 
    Then  the lease's roles collection should contain:
          | type     | start date | end date  | party  | agreement    |
          | Tenant   | 2015-8-31  | 2015-9-1  | POISON | OXF-PRET-004 |
          | Tenant   | null       | 2015-8-31 | PRET   | OXF-PRET-004 |



  #@unit
  @backlog
  @integration
  Scenario: Cannot update end date later than successor's start date
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date  | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | 2015-9-1  | POISON | OXF-PRET-004 |           |
          | Tenant   | null       | 2013-4-1  | PRET   | OXF-PRET-004 | *         |
    When  I attempt to update the end date on the indicated agreement role to "2015-9-1" 
    Then  the action is invalid with message "End date cannot be on/after start date of current predecessor"
    And   the lease's roles collection should contain:
          | type     | start date | end date  | party  | agreement    |
          | Tenant   | 2013-4-1   | 2015-9-1  | POISON | OXF-PRET-004 |
          | Tenant   | null       | 2013-4-1  | PRET   | OXF-PRET-004 |



  #@unit
  @backlog
  @integration
  Scenario: Cannot make end date earlier than own start date
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | 2014-5-1 | PRET   | OXF-PRET-004 | *         |
          | Tenant   | 2012-3-1   | 2013-4-1 | POISON | OXF-PRET-004 |           |
    When  I attempt to update the end date on the indicated agreement role to "2013-4-1" 
    Then  the action is invalid with message "End date cannot be on/before the start date"
    And   the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | 2014-5-1 | PRET   | OXF-PRET-004 |
          | Tenant   | 2012-3-1   | 2013-4-1 | POISON | OXF-PRET-004 |


#############################################################################
# replace with successor 
#############################################################################

  #@unit
  @backlog
  @integration
  Scenario: Cannot replace if no successor
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 | *         |
    When  I attempt to replace the indicated agreement role with successor 
    Then  the action is disabled with message "No successor to replace"
    And   the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | null       | 2013-4-1 | PRET   | OXF-PRET-004 |

  #@unit
  @backlog
  @integration
  Scenario: Can replace if has successor
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | 2015-9-1 | POISON | OXF-PRET-004 |           |
          | Tenant   | 2010-3-1   | 2013-4-1 | PRET   | OXF-PRET-004 | *         |
    When  I replace the indicated agreement role with successor 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2010-3-1   | 2015-9-1 | POISON | OXF-PRET-004 |


#############################################################################
# replace with predecessor
# (symmetric with replace with successor) 
#############################################################################


  #@unit
  @backlog
  @integration
  Scenario: Cannot replace if no predecessor
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 | *         |
    When  I attempt to replace the indicated agreement role with predecessor 
    Then  the action is disabled with message "No predecessor to replace"
    And   the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | null       | 2013-4-1 | PRET   | OXF-PRET-004 |

  #@unit
  @backlog
  @integration
  Scenario: Can replace if has predecessor
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    | indicated |
          | Tenant   | 2013-4-1   | 2015-9-1 | PRET   | OXF-PRET-004 | *         |
          | Tenant   | 2010-3-1   | 2013-4-1 | POISON | OXF-PRET-004 |           |
    When  I replace the indicated agreement role with predecessor 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2010-3-1   | 2015-9-1 | POISON | OXF-PRET-004 |



#############################################################################
# remove (last) role
# (corollary of add role) 
#############################################################################


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
