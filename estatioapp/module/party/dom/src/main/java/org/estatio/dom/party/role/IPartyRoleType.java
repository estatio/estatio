package org.estatio.dom.party.role;

public interface IPartyRoleType {

    String getKey();

    String getTitle();

    default PartyRoleType findUsing(final PartyRoleTypeRepository repo) {
        return repo.findByKey(getKey());
    }

    default PartyRoleType findOrCreateUsing(PartyRoleTypeRepository repository) {
        return  repository.findOrCreate(this);
    }


    class Meta {
        private Meta(){}
        public final static int MAX_LEN = 30;
    }


}
