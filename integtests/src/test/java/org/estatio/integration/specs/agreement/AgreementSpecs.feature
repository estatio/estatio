Feature: Add and remove AgreementRoles for an Agreement

  @integration
  Scenario: Add role when none, with new role having no start or end dates
    Given there is a lease "OXF-PRET-004"
    And   the lease has no existing roles
    And   there is a party "PRET" 
    When  I add a new agreement role of type "Landlord", start date "2013-4-1", end date "null", for this party 
    Then  the lease's roles collection should contain:
          | type     | start date | end date | party  | agreement    |
          | Landlord | 2013-4-1   | null     | PRET   | OXF-PRET-004 | 


#  Scenario: Add role when none, with new role have a start date
#    Given I have an agreement with start date 'null' and end date 'null'
#    And   the agreement has no existing roles
#    When I add a new 'Landlord' role, with start date '2013-4-1' and end date 'null'
#    Then the agreement's roles collection should contain the role
#    And the role should reference the agreement
#    And the role's type should be 'Landlord'
#    And the role's start date should be '2013-4-1'
#    And the role's end date should be 'null'
#
#  Scenario: Add role when none, with new role have an end date
#    Given I have an agreement with start date 'null' and end date 'null'
#    And   the agreement has no existing roles
#    When I add a new 'Landlord' role, with start date 'null' and end date '2013-4-1'
#    Then the agreement's roles collection should contain the role
#    And the role should reference the agreement
#    And the role's type should be 'Landlord'
#    And the role's start date should be 'null'
#    And the role's end date should be '2013-4-1'
#
#  Scenario: Add role when agreement already has role of same type, with start date specified for new role
#    Given I have an agreement with start date 'null' and end date 'null'
#    And   the agreement has a 'Landlord' role start date 'null' and end date 'null'
#    When I add a new 'Landlord' role, with start date '2013-4-1' and end date 'null'
#    Then the agreement's roles collection should contain the new role
#    And the new role should reference the agreement
#    And the new role's type should be 'Landlord'
#    And the new role's start date should be '2013-4-1'
#    And the existing role's end date should be '2013-4-1'
#
#  Scenario: Add role when agreement already has role of same type, but no start date specified for new role
#    Given I have an agreement with start date 'null' and end date 'null'
#    And   the agreement has a 'Landlord' role start date 'null' and end date 'null'
#    When I add attempt to add a 'Landlord' role, with start date 'null'
#    Then I should be prevented from doing so
  