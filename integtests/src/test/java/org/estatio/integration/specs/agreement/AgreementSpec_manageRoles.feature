Feature: Manage AgreementRoles for an Agreement

  #@unit
  #@ignore
  @integration
  Scenario: Add role with no start/end date
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
    When  I add a new agreement role of type "Landlord", start date "null", end date "null", for this party 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Landlord | null       | null     | PRET   | OXF-PRET-004 | 

  #@unit
  #@ignore
  @integration
  Scenario: Add role with start date only
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
    When  I add a new agreement role of type "Landlord", start date "2013-4-1", end date "null", for this party 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Landlord | 2013-4-1   | null     | PRET   | OXF-PRET-004 | 

  #@unit
  #@ignore
  @integration
  Scenario: Add role with end date only
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
    When  I add a new agreement role of type "Landlord", start date "null", end date "2013-4-1", for this party 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Landlord | null       | 2013-4-1 | PRET   | OXF-PRET-004 | 

  #@unit
  #@ignore
  @integration
  Scenario: Add role with start date takes over from existing
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 | 
    And   there is a party "POISON" 
    When  I add a new agreement role of type "Tenant", start date "2013-4-1", end date "null", for this party 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | null     | POISON | OXF-PRET-004 | 
          | Tenant   | null       | 2013-4-1 | PRET   | OXF-PRET-004 | 

  #@unit
  #@ignore
  @integration
  Scenario: Add role with end date precedes existing
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 | 
    And   there is a party "POISON" 
    When  I add a new agreement role of type "Tenant", start date "null", end date "2013-4-1", for this party 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | null     | PRET   | OXF-PRET-004 | 
          | Tenant   | null       | 2013-4-1 | POISON | OXF-PRET-004 | 

  #@unit
  #@ignore
  @integration
  Scenario: Replaces role if no start/end date on either existing or new
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 | 
    And   there is a party "POISON" 
    When  I add a new agreement role of type "Tenant", start date "null", end date "null", for this party 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | null       | null     | POISON | OXF-PRET-004 | 

  #@unit
  #@ignore
  @integration
  Scenario: Bisected new role if start/end date on existing but no start/end date on new
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | 2014-5-2 | PRET   | OXF-PRET-004 | 
    And   there is a party "POISON" 
    When  I add a new agreement role of type "Tenant", start date "null", end date "null", for this party 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2014-5-2   | null     | POISON | OXF-PRET-004 | 
          | Tenant   | 2013-4-1   | 2014-5-2 | PRET   | OXF-PRET-004 | 
          | Tenant   | null       | 2013-4-1 | POISON | OXF-PRET-004 | 



  #@unit
  #@ignore
  @integration
  Scenario: Bisected new role if start/end date on existing but no start and end date after
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | 2014-5-2 | PRET   | OXF-PRET-004 | 
    And   there is a party "POISON" 
    When  I add a new agreement role of type "Tenant", start date "null", end date "2016-3-1", for this party 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2014-5-2   | 2016-3-1 | POISON | OXF-PRET-004 | 
          | Tenant   | 2013-4-1   | 2014-5-2 | PRET   | OXF-PRET-004 | 
          | Tenant   | null       | 2013-4-1 | POISON | OXF-PRET-004 | 

  #@unit
  #@ignore
  @integration
  Scenario: Bisected new role if start/end date on existing but start date before and no end date
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | 2014-5-2 | PRET   | OXF-PRET-004 | 
    And   there is a party "POISON" 
    When  I add a new agreement role of type "Tenant", start date "2010-7-1", end date "null", for this party 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2014-5-2   | null     | POISON | OXF-PRET-004 | 
          | Tenant   | 2013-4-1   | 2014-5-2 | PRET   | OXF-PRET-004 | 
          | Tenant   | 2010-7-1   | 2013-4-1 | POISON | OXF-PRET-004 | 
                    
  #@unit
  #@ignore
  @integration
  Scenario: Bisected new role if start/end date on existing but start date before and end date after
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | 2014-5-2 | PRET   | OXF-PRET-004 | 
    And   there is a party "POISON" 
    When  I add a new agreement role of type "Tenant", start date "2010-7-1", end date "2019-10-11", for this party 
    Then  the lease's roles collection should contain:
          | type     | start date | end date   | party  | agreement    |
          | Tenant   | 2014-5-2   | 2019-10-11 | POISON | OXF-PRET-004 | 
          | Tenant   | 2013-4-1   | 2014-5-2   | PRET   | OXF-PRET-004 | 
          | Tenant   | 2010-7-1   | 2013-4-1   | POISON | OXF-PRET-004 | 
                    
                    
                    
  #@unit
  #@ignore
  @integration
  Scenario: Bisected existing role if no start/end date on existing but start/end date on new
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 | 
    And   there is a party "POISON" 
    When  I add a new agreement role of type "Tenant", start date "2013-4-1" end date "2014-5-2", for this party 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2014-5-2   | null     | PRET   | OXF-PRET-004 | 
          | Tenant   | 2013-4-1   | 2014-5-2 | POISON | OXF-PRET-004 | 
          | Tenant   | null       | 2013-4-1 | PRET   | OXF-PRET-004 | 

          
  #@unit
  #@ignore
  @integration
  Scenario: Slots within existing roles
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date  | party    | agreement    |
          | Tenant   | 2013-4-1   | null      | PRET     | OXF-PRET-004 | 
          | Tenant   | null       | 2013-4-1  | POISON   | OXF-PRET-004 | 
    And   there is a party "TOPMODEL" 
    When  I add a new agreement role of type "Tenant", start date "2012-3-31", end date "2014-5-3", for this party 
    Then  the lease's roles collection should contain:
          | type     | start date | end date  | party    | agreement    |
          | Tenant   | 2014-5-3   | null      | PRET     | OXF-PRET-004 | 
          | Tenant   | 2012-3-31  | 2014-5-3  | TOPMODEL | OXF-PRET-004 | 
          | Tenant   | null       | 2012-3-31 | POISON   | OXF-PRET-004 | 


  #@unit
  #@ignore
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
  #@ignore
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


  @integration
  Scenario: Adding role that fills a gap does not automatically merge
    Given there is a lease "OXF-PRET-004"
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Landlord | 2013-4-1   | null     | POISON | OXF-PRET-004 | 
          | Landlord | 2010-2-1   | 2012-3-1 | POISON | OXF-PRET-004 | 
    And   there is a party "POISON" 
    When  I add a new agreement role of type "Landlord", start date "2012-3-1", end date "2013-4-1", for this party 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Landlord | 2013-4-1   | null     | POISON | OXF-PRET-004 | 
          | Landlord | 2012-3-1   | 2013-4-1 | POISON | OXF-PRET-004 | 
          | Landlord | 2010-2-1   | 2012-3-1 | POISON | OXF-PRET-004 | 

##############################################################################


  @integration
  Scenario: Remove last role
    Given there is a lease "OXF-PRET-004"
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 | 
    When  I remove the 1st agreement role
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |

  @integration
  Scenario: Remove role (when different role types)
    Given there is a lease "OXF-PRET-004"
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Landlord | 2013-4-1   | null     | POISON | OXF-PRET-004 | 
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 | 
    When  I remove the 2nd agreement role
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Landlord | 2013-4-1   | null     | POISON | OXF-PRET-004 | 

  @integration
  Scenario: Remove role does not change existing roles of same type
    Given there is a lease "OXF-PRET-004"
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Landlord | 2013-4-1   | null     | POISON | OXF-PRET-004 | 
          | Landlord | null       | 2013-4-1 | PRET   | OXF-PRET-004 | 
    When  I remove the 2nd agreement role
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Landlord | 2013-4-1   | null     | POISON | OXF-PRET-004 | 

  @integration
  Scenario: Remove role can leave gaps, are not automatically filled
    Given there is a lease "OXF-PRET-004"
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Landlord | 2013-4-1   | null     | POISON | OXF-PRET-004 | 
          | Landlord | 2012-3-1   | 2013-4-1 | PRET   | OXF-PRET-004 | 
          | Landlord | 2010-2-1   | 2012-3-1 | POISON | OXF-PRET-004 | 
    When  I remove the 2nd agreement role
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Landlord | 2013-4-1   | null     | POISON | OXF-PRET-004 | 
          | Landlord | 2010-2-1   | 2012-3-1 | POISON | OXF-PRET-004 | 

          