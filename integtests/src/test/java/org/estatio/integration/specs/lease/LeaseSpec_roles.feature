@EstatioTransactionalObjectsFixture
Feature: Manage AgreementRoles for an Agreement (ORIG)



#
# this stuff is wrong, and has been marked as @ignore
#
# see LeaseSpecNEW_roles for proposed reworking..
#


  #@unit
  @ignore
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
  @ignore
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
  @ignore
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
  @ignore
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
  @ignore
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
  @ignore
  @integration
  Scenario: Add role with end date precedes existing
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 | 
    When  I update the existing agreement role's start date to "2013-4-1" 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | null     | PRET   | OXF-PRET-004 | 


  #@unit
  @ignore
  @integration
  Scenario: Add role with end date that finishes on start date of existing
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | null     | PRET   | OXF-PRET-004 | 
    And   there is a party "POISON" 
    When  I add a new agreement role's start date "2013-3-1" and end date to "2013-4-1" for this party 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | null     | PRET   | OXF-PRET-004 | 
          | Tenant   | 2013-3-1   | 2013-4-1 | POISON | OXF-PRET-004 | 


  #@unit
  @ignore
  @integration
  Scenario: Add role with end date that finishes prior to the start date of existing
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | null     | PRET   | OXF-PRET-004 | 
    And   there is a party "POISON" 
    When  I attempt to add a new agreement role's start date "2013-3-1" and end date of "2013-3-31" for this party 
    Then  the action should not be allowed
    And   the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | null     | PRET   | OXF-PRET-004 | 

  #@unit
  @ignore
  @integration
  Scenario: Replaces role if no start/end date on either existing or new (not allowed - start date is null)
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 | 
    And   there is a party "POISON" 
    When  I add attempt to a new agreement role of type "Tenant", start date "null", end date "null", for this party 
    Then  the action should not be allowed (because the start date for new roles can never be null)
    And   the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | null       | null     | PRET | OXF-PRET-004 | 

  #@unit
  @ignore
  @integration
  Scenario: Bisected new role if start/end date on existing but no start/end date on new (not allowed - start date is null)
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | 2014-5-2 | PRET   | OXF-PRET-004 | 
    And   there is a party "POISON" 
    When  I attempt to add a new agreement role of type "Tenant", start date "null", end date "null", for this party 
    Then  the action should not be allowed (because the start date for new roles can never be null)
    And   the lease's roles collection should contain:
          | Tenant   | 2013-4-1   | 2014-5-2 | PRET   | OXF-PRET-004 | 


  #@unit
  @ignore
  @integration
  Scenario: Bisected new role if start/end date on existing but no start and end date after (not allowed - start date is null)
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | 2014-5-2 | PRET   | OXF-PRET-004 | 
    And   there is a party "POISON" 
    When  I attempt to add a new agreement role of type "Tenant", start date "null", end date "2016-3-1", for this party 
    Then  the action should not be allowed (because the start date for new roles can never be null)
    And   the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | 2014-5-2 | PRET   | OXF-PRET-004 |


  #@unit
  @ignore
  @integration
  Scenario: Bisected new role if start/end date on existing but start date before and no end date (not allowed)
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | 2014-5-2 | PRET   | OXF-PRET-004 | 
    And   there is a party "POISON" 
    When  I attempt to add a new agreement role of type "Tenant", start date "2010-7-1", end date "null", for this party 
    Then  the action is disabled (because cannot span an existing)
    And   the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | 2014-5-2 | PRET   | OXF-PRET-004 | 


  #@unit
  @ignore
  @integration
  Scenario: gap afterwards not allowed
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | 2014-5-2 | PRET   | OXF-PRET-004 | 
    And   there is a party "POISON" 
    When  I attempt to add a new agreement role of type "Tenant", start date "2014-5-3", end date "null", for this party 
    Then  the action is not allowed (there would be a gap)
    And   the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | 2014-5-2 | PRET   | OXF-PRET-004 | 


  #@unit
  @ignore
  @integration
  Scenario: no gap afterwards, is ok
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | 2014-5-2 | PRET   | OXF-PRET-004 | 
    And   there is a party "POISON" 
    When  I add a new agreement role of type "Tenant", start date "2014-5-2", end date "null", for this party 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-5-2   | null     | POISON | OXF-PRET-004 | 
          | Tenant   | 2013-4-1   | 2014-5-2 | PRET   | OXF-PRET-004 | 



  #@unit
  @ignore
  @integration
  Scenario: Bisected new role if start/end date on existing but start date before and no end date (IS allowed, think have this one already earlier on)
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | 2014-5-2 | PRET   | OXF-PRET-004 | 
    And   there is a party "POISON" 
    When  I attempt to add a new agreement role of type "Tenant", start date "null", end date "2013-4-1", for this party 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | 2014-5-2 | PRET   | OXF-PRET-004 | 
          | Tenant   | null       | 2013-4-1 | POISON | OXF-PRET-004 | 


  #@unit
  @ignore
  @integration
  Scenario: Bisected new role if start/end date on existing but start date before and end date after
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | 2014-5-2 | PRET   | OXF-PRET-004 | 
    And   there is a party "POISON" 
    When  I attempt to add a new agreement role of type "Tenant", start date "2010-7-1", end date "2019-10-11", for this party
    Then  the action should not be allowed (cannot span existing) 
    Then  the lease's roles collection should contain:
          | type     | start date | end date   | party  | agreement    |
          | Tenant   | 2013-4-1   | 2014-5-2 | PRET   | OXF-PRET-004 | 
                    
                    
                    
  #@unit
  @ignore
  @integration
  Scenario: Bisected existing role if no start/end date on existing but start/end date on new
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 | 
    And   there is a party "POISON" 
    When  I attempt to add a new agreement role of type "Tenant", start date "2013-4-1" end date "2014-5-2", for this party
    Then  the action should be disallowed (cannot sit within the span of an existing) 
    And the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 | 


  #@unit
  @ignore
  @integration
  Scenario: Bisected existing role if no start/end date on existing but start/end date on new
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 | 
    And   there is a party "POISON" 
    When  I add a new agreement role of type "Tenant", start date "2013-4-1" end date "null", for this party
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | 2013-4-1   | null     | POISON | OXF-PRET-004 | 
          | Tenant   | null       | 2013-4-1 | PRET   | OXF-PRET-004 | 


          
  #@unit
  @ignore
  @integration
  Scenario: Cannot modify two existing roles
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date  | party    | agreement    |
          | Tenant   | 2013-4-1   | null      | PRET     | OXF-PRET-004 | 
          | Tenant   | null       | 2013-4-1  | POISON   | OXF-PRET-004 | 
    And   there is a party "TOPMODEL" 
    When  I attempt to add a new agreement role of type "Tenant", start date "2012-3-31", end date "2014-5-3", for this party 
    Then  the action is not allowed (would be modifying two existing)
    And   the lease's roles collection should contain:
          | Tenant   | 2013-4-1   | null      | PRET     | OXF-PRET-004 | 
          | Tenant   | null       | 2013-4-1  | POISON   | OXF-PRET-004 | 



  #@unit
  @ignore
  @integration
  Scenario: Shoves an existing role to start on a later date
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date  | party    | agreement    |
          | Tenant   | 2013-4-1   | null      | PRET     | OXF-PRET-004 | 
          | Tenant   | null       | 2013-4-1  | POISON   | OXF-PRET-004 | 
    And   there is a party "TOPMODEL" 
    When  I add a new agreement role of type "Tenant", start date "2012-4-1", end date "2014-5-3", for this party 
    Then  the lease's roles collection should contain:
          | type     | start date | end date  | party    | agreement    |
          | Tenant   | 2014-5-3   | null      | PRET     | OXF-PRET-004 | 
          | Tenant   | 2012-4-1   | 2014-5-3  | TOPMODEL | OXF-PRET-004 | 
          | Tenant   | null       | 2012-4-1  | POISON   | OXF-PRET-004 | 

  #@unit
  @ignore
  @integration
  Scenario: Shoves an existing role to finish on an earlier date
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date  | party    | agreement    |
          | Tenant   | 2013-4-1   | null      | PRET     | OXF-PRET-004 | 
          | Tenant   | null       | 2013-4-1  | POISON   | OXF-PRET-004 | 
    And   there is a party "TOPMODEL" 
    When  I add a new agreement role of type "Tenant", start date "2011-2-1", end date "2012-4-1", for this party 
    Then  the lease's roles collection should contain:
          | type     | start date | end date  | party    | agreement    |
          | Tenant   | 2013-4-1   | null      | PRET     | OXF-PRET-004 | 
          | Tenant   | 2011-2-1   | 2013-4-1  | TOPMODEL | OXF-PRET-004 | 
          | Tenant   | null       | 2011-2-1  | POISON   | OXF-PRET-004 | 


  #@unit
  @ignore
  @integration
  Scenario: Cannot modify more than one existing role
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date  | party    | agreement    |
          | Tenant   | 2013-4-1   | null      | PRET     | OXF-PRET-004 | 
          | Tenant   | 2012-4-1   | 2013-4-1  | POISON   | OXF-PRET-004 | 
          | Tenant   | 2011-4-1   | 2012-4-1  | PRET     | OXF-PRET-004 | 
    And   there is a party "TOPMODEL" 
    When  I attempt to add a new agreement role of type "Tenant", start date "2011-6-1", end date "2013-4-1", for this party 
    Then  the action is not allowed (would impact both PRET in 2011 and all of POISON)
    And   the lease's roles collection should contain:
          | type     | start date | end date  | party    | agreement    |
          | Tenant   | 2013-4-1   | null      | PRET     | OXF-PRET-004 | 
          | Tenant   | 2012-4-1   | 2013-4-1  | POISON   | OXF-PRET-004 | 
          | Tenant   | 2011-4-1   | 2012-4-1  | PRET     | OXF-PRET-004 | 




  #@unit
  @ignore
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
  @ignore
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


  #@unit
  @ignore
  @integration
  Scenario: THIS IS RUBBISH - THE GIVEN COULD NEVER EXIST (must be contiguous roles)
  Adding role that fills a gap does not automatically merge
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

  #@unit
  @ignore
  @integration
  Scenario: Can remove by changing end date
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date  | party    | agreement    |
          | Tenant   | 2013-4-1   | null      | TOPMODEL | OXF-PRET-004 | 
          | Tenant   | 2012-4-1   | 2013-4-1  | POISON   | OXF-PRET-004 | 
          | Tenant   | 2011-4-1   | 2012-4-1  | PRET     | OXF-PRET-004 | 
    When  I modify agreement role (POISON) with end date of "2012-4-1"
    Then  the lease's roles collection contains:
          | type     | start date | end date  | party    | agreement    |
          | Tenant   | 2012-4-1   | null      | TOPMODEL | OXF-PRET-004 | 
          | Tenant   | 2011-4-1   | 2012-4-1  | PRET     | OXF-PRET-004 | 


  #@unit
  @ignore
  @integration
  Scenario: Can remove by changing start date
    Given there is a lease "OXF-PRET-004"
    And   there is a party "PRET" 
    And   the lease's roles collection contains:
          | type     | start date | end date  | party    | agreement    |
          | Tenant   | 2013-4-1   | null      | TOPMODEL | OXF-PRET-004 | 
          | Tenant   | 2012-4-1   | 2013-4-1  | POISON   | OXF-PRET-004 | 
          | Tenant   | 2011-4-1   | 2012-4-1  | PRET     | OXF-PRET-004 | 
    When  I modify agreement role (POISON) with start date of "2013-4-1"
    Then  the lease's roles collection contains:
          | type     | start date | end date  | party    | agreement    |
          | Tenant   | 2013-4-1   | null      | TOPMODEL | OXF-PRET-004 | 
          | Tenant   | 2011-4-1   | 2013-4-1  | PRET     | OXF-PRET-004 | 




  @ignore
  @integration
  Scenario: GET RID OF THIS CAPABILITY ... Remove last role
    Given there is a lease "OXF-PRET-004"
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 | 
    When  I remove the 1st agreement role
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |


  @ignore
  @integration
  Scenario: GET RID OF THIS, DOES NOT MAKE SENSE.  Remove role (when different role types)
    Given there is a lease "OXF-PRET-004"
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Landlord | 2013-4-1   | null     | POISON | OXF-PRET-004 | 
          | Tenant   | null       | null     | PRET   | OXF-PRET-004 | 
    When  I remove the 2nd agreement role
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Landlord | 2013-4-1   | null     | POISON | OXF-PRET-004 | 


  @ignore
  @integration
  Scenario: GET RID OF THIS, DOES NOT MAKE SENSE.  Remove role does not change existing roles of same type
    Given there is a lease "OXF-PRET-004"
    And   the lease's roles collection contains:
          | type     | start date | end date | party  | agreement    |
          | Landlord | 2013-4-1   | null     | POISON | OXF-PRET-004 | 
          | Landlord | null       | 2013-4-1 | PRET   | OXF-PRET-004 | 
    When  I remove the 2nd agreement role
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Landlord | 2013-4-1   | null     | POISON | OXF-PRET-004 | 

  @ignore
  @integration
  Scenario: GET RID OF THIS, DOES NOT MAKE SENSE.  Remove role can leave gaps, are not automatically filled
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

          
          
          
# if there is any existing role (with or without explicit values for start and end...), 
# then any new role MUST have explicit start date (not necessarily end date) 

## TODO:
# for an existing null/null role, should be able to specify a startDate for the 
          