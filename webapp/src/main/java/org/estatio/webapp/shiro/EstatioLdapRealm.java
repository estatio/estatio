/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.webapp.shiro;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.AuthenticationException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.ldap.JndiLdapRealm;
import org.apache.shiro.realm.ldap.LdapContextFactory;
import org.apache.shiro.realm.ldap.LdapUtils;
import org.apache.shiro.subject.PrincipalCollection;

/**
 * Implementation of {@link org.apache.shiro.realm.ldap.JndiLdapRealm} that also
 * returns each user's groups.
 * 
 * <p>
 * Sample config for <tt>shiro.ini</tt>:
 * 
 * <pre>
 * contextFactory = org.estatio.security.shiro.EstatioLdapContextFactory
 * contextFactory.url = ldap://localhost:10389
 * contextFactory.authenticationMechanism = simple
 * 
 * contextFactory.systemUsername = ### some trusted account with read-only access ###
 * contextFactory.systemPassword = ### some trusted account with read-only access ###
 * 
 * ldapRealm = org.estatio.security.shiro.EstatioLdapRealm
 * ldapRealm.contextFactory = $contextFactory
 * 
 * ldapRealm.searchBase = DC=ECP,DC=LOC
 * 
 * ldapRealm.permissionsByRole=\
 *    user_role = *:ToDoItemsJdo:*:*,\
 *                *:ToDoItem:*:*; \
 *    self-install_role = *:ToDoItemsFixturesService:install:* ; \
 *    admin_role = *
 * 
 * securityManager.realms = $ldapRealm
 * 
 * </pre>
 */
public class EstatioLdapRealm extends JndiLdapRealm {

    private final static SearchControls SUBTREE_SCOPE = new SearchControls();
    static {
        SUBTREE_SCOPE.setSearchScope(SearchControls.SUBTREE_SCOPE);
    }
    private static final Pattern MEMBER_OF_PATTERN = Pattern.compile("CN=([^,]+),.*");

    private String searchBase;
    
    private Map<String,String> rolesByGroup = Maps.newLinkedHashMap();
    private final Map<String,List<String>> permissionsByRole = Maps.newLinkedHashMap();
    
    public EstatioLdapRealm() {
    }
    
    @Override
    protected AuthorizationInfo queryForAuthorizationInfo(final PrincipalCollection principals, final LdapContextFactory ldapContextFactory) throws NamingException {
        final Set<String> roleNames = getRoles(principals, ldapContextFactory);
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo(roleNames);
        Set<String> stringPermissions = permsFor(roleNames);
        simpleAuthorizationInfo.setStringPermissions(stringPermissions);
        return simpleAuthorizationInfo;
    }

    private Set<String> getRoles(final PrincipalCollection principals, final LdapContextFactory ldapContextFactory) throws NamingException {
        final String username = (String) getAvailablePrincipal(principals);

        LdapContext systemLdapCtx = null;
        try {
            systemLdapCtx = ldapContextFactory.getSystemLdapContext();
            return rolesFor(username, systemLdapCtx);
        } catch (AuthenticationException ex) {
            // principal was not authenticated on LDAP
            return Collections.emptySet();
        } finally {
            LdapUtils.closeContext(systemLdapCtx);
        }
    }

    private Set<String> rolesFor(final String userName, final LdapContext ldapCtx) throws NamingException {
        final Set<String> roleNames = Sets.newLinkedHashSet();
        String userNamePart = userName.substring(0, userName.indexOf("@"));
        final NamingEnumeration<SearchResult> searchResultEnum = ldapCtx.search(searchBase, "(&(objectCategory=Person)(sAMAccountName="+userNamePart+"))", SUBTREE_SCOPE);
        if (searchResultEnum.hasMore()) {
            final SearchResult userEntry = searchResultEnum.next();
            addRolesForEachGroupMemberOf(userEntry, roleNames);
        }
        return roleNames;
    }

    private void addRolesForEachGroupMemberOf(final SearchResult userEntry, final Set<String> roleNames) throws NamingException {
        final NamingEnumeration<? extends Attribute> attributeEnum = userEntry.getAttributes().getAll();
        while (attributeEnum.hasMore()) {
            final Attribute attr = attributeEnum.next();
            if (!"memberOf".equalsIgnoreCase(attr.getID())) {
                continue;
            }
            final NamingEnumeration<?> e = attr.getAll();
            while (e.hasMore()) {
                String attrValue = e.next().toString();
                Matcher matcher = MEMBER_OF_PATTERN.matcher(attrValue);
                if(matcher.matches()) {
                    String groupName = matcher.group(1);
                    String roleName = rolesByGroup.get(groupName);
                    if(roleName != null) {
                        roleNames.add(roleName);
                    }
                }
            }
        }
    }


    private Set<String> permsFor(Set<String> roleNames) {
        Set<String> perms = Sets.newLinkedHashSet(); // preserve order
        for(String role: roleNames) {
            List<String> permsForRole = permissionsByRole.get(role);
            if(permsForRole != null) {
                perms.addAll(permsForRole);
            }
        }
        return perms;
    }

    public void setSearchBase(String searchBase) {
        this.searchBase = searchBase;
    }

    public void setRolesByGroup(Map<String, String> rolesByGroup) {
        this.rolesByGroup.putAll(rolesByGroup);
    }
    
    public void setPermissionsByRole(String permissionsByRoleStr) {
        permissionsByRole.putAll(Util.parse(permissionsByRoleStr));
    }
    
    // bit naughty, for testing.
    Map<String, List<String>> getPermissionsByRole() {
        return permissionsByRole;
    }
    
}
