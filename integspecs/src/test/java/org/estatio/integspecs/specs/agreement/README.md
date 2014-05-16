
Dan's suggestion: so far as possible, act on the role object, not the parent lease

*  agreemement#addRole                  - only available to add the (first) role (of a given type), ie special case
*  agreementRole#succeedBy              - adds the successor; adjusts any existing successor (but new successor cannot end after current successor)
*  agreementRole#precedeBy              - adds the predecessor; adjusts any existing predecessor (but new predecessor cannot start before current predecessor)
*  agreementRole#updateStartDate        - adjusts, along with end date of predecessor (if any)
*  agreementRole#updateEndDate          - adjusts, along with end date of successor (if any)
*  agreementRole#replaceWithPredecessor - removes, predecessor end date moving later to the end date of the deleted role
*  agreementRole#replaceWithSuccessor   - removes, successor's start date moving earlier to start date of the deleted role
*  agreement#removeRole                 - only available to remove the (first) role (of a given type), ie special case


on discussing with Jeroen: 

*   do need to add delegate methods so that the common case (add successor) is available from the parent (Lease) object

