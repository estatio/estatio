package org.estatio.dom.party.role;

public interface PartyRoleTypeData {

    String getKey();

    String getTitle();

    PartyRoleType findUsing(PartyRoleTypeRepository repo);

    public static class Util {
        public static PartyRoleType findUsing(PartyRoleTypeData type , PartyRoleTypeRepository repo) {
            return repo.findByKey(type.getKey());
        }
    }

}
