/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
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

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.config.Ini;
import org.apache.shiro.realm.ldap.JndiLdapRealm;
import org.apache.shiro.realm.ldap.LdapContextFactory;
import org.apache.shiro.realm.ldap.LdapUtils;
import org.apache.shiro.subject.PrincipalCollection;

import org.apache.isis.security.shiro.permrolemapper.PermissionToRoleMapper;
import org.apache.isis.security.shiro.permrolemapper.PermissionToRoleMapperFromIni;
import org.apache.isis.security.shiro.permrolemapper.PermissionToRoleMapperFromString;

/**
 * Implementation of {@link org.apache.shiro.realm.ldap.JndiLdapRealm} that also
 * returns each user's groups.
 * 
 * <p>
 * Sample config for <tt>shiro.ini</tt>:
 * 
 * <pre>
 * ldapRealm = org.estatio.webapp.shiro.EstatioLdapRealm
 * 
 * contextFactory = org.estatio.security.shiro.EstatioLdapContextFactory
 * contextFactory.url = ldap://localhost:10389
 * contextFactory.authenticationMechanism = simple
 *  
 * contextFactory.systemUsername = ### some trusted account with read-only access ###
 * contextFactory.systemPassword = ### some trusted account with read-only access ###
 * 
 * ldapRealm.contextFactory = $contextFactory
 * 
 * # (&(objectCategory=Person)(sAMAccountName=jvanderwal))
 * ldapRealm.searchBase = DC=ECP,DC=LOC
 * 
 * # search using eg: (&(objectClass=group)(cn=ECP-Estatio-IT-Users))
 * ldapRealm.roleListByGroup = \
 *   ECP-Estatio-IT-Users : user_role,\
 *   ECP-Estatio-Administrators: user_role|admin_role
 * 
 * # the user_role and admin_role are mapped to their permissions through the ini file, eg: 
 * ldapRealm.resourcePath=\
 *     classpath:org/estatio/webapp/webinf/local_users_and_shared_role_perms.ini
 * </pre>
 * 
 * <p>
 * Can also configured an ini-based realm, sharing the same role/perms:
 * <pre>
 * # the .ini file lives in src/main/resources
 * localRealm = org.apache.shiro.realm.text.IniRealm
 * localRealm.resourcePath=\
 *   classpath:org/estatio/webapp/webinf/local_users_and_shared_role_perms.ini
 * </pre>
 * 
 * <p>
 * and finally configure Shiro to use both realms:
 * <pre>
 * # $localRealm configured
 * securityManager.realms = $localRealm,$ldapRealm
 * </pre>
 * 
 * <p>
 * where <tt>local_users_and_shared_role_perms.in</tt> is on the classpath, and maps
 * roles to permissions.
 * 
 * <p>
 * This 'ini' file can then be referenced by other realms (if multiple realm are configured
 * with the Shiro security manager). 
 */
public class EstatioLdapRealm extends JndiLdapRealm {

    private final static SearchControls SUBTREE_SCOPE = new SearchControls();
    static {
        SUBTREE_SCOPE.setSearchScope(SearchControls.SUBTREE_SCOPE);
    }
    private static final Pattern MEMBER_OF_PATTERN = Pattern.compile("CN=([^,]+),.*");

    private String searchBase;
    
    private Map<String,List<String>> roleListByGroup = Maps.newLinkedHashMap();
    private PermissionToRoleMapper permissionToRoleMapper;
    
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
        final int atSymbolIndex = userName.indexOf("@");
        if(atSymbolIndex == -1) {
            // Isis *won't* call this method for any users defined by the local realm (eg root, api) etc.
            // however, we've left in this check just in case, in the future, there *are* users in LDAP that
            // don't follow the naming convention foo@ecp.loc
            return roleNames;
        }
        String userNamePart = userName.substring(0, atSymbolIndex);
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
                    List<String> roleList = roleListByGroup.get(groupName);
                    if(roleList != null) {
                        for (String roleName : roleList) {
                            if(roleName != null) {
                                roleNames.add(roleName);
                            }
                        }
                    }
                }
            }
        }
    }


    private Set<String> permsFor(Set<String> roleNames) {
        Set<String> perms = Sets.newLinkedHashSet(); // preserve order
        for(String role: roleNames) {
            List<String> permsForRole = getPermissionsByRole().get(role);
            if(permsForRole != null) {
                perms.addAll(permsForRole);
            }
        }
        return perms;
    }

    public void setSearchBase(String searchBase) {
        this.searchBase = searchBase;
    }

    private final static Function<String,String> TRIM = new Function<String,String>(){
        public String apply(String str) {
            return str.trim();
        }
    };

    private final static Function<String, List<String>> splitOn(final char separatorChar) {
        return new Function<String, List<String>>() {
            public List<String> apply(String str) {
                Iterable<String> split = Splitter.on(separatorChar).split(str);
                return Lists.newArrayList(Iterables.transform(split, TRIM));
            }
        };
    }
    
    public void setRoleListByGroup(Map<String, String> roleListStrByGroup) {
        Map<String, List<String>> roleListByGroup = Maps.transformValues(roleListStrByGroup, splitOn('|'));
        this.roleListByGroup.putAll(roleListByGroup);
    }
    
    /**
     * Retrieves permissions by role set using either
     * {@link #setPermissionsByRole(String)} or {@link #setResourcePath(String)}.
     */
    Map<String,List<String>> getPermissionsByRole() {
        if(permissionToRoleMapper == null) {
            throw new IllegalStateException("Permissions by role not yet set.");
        } 
        return permissionToRoleMapper.getPermissionsByRole();
    }
    
    /**
     * <pre>
     * ldapRealm.resourcePath=classpath:webapp/myroles.ini
     * </pre>
     *
     * <p>
     * where <tt>myroles.ini</tt> is in <tt>src/main/resources/webapp</tt>, and takes the form:
     * 
     * <pre>
     * [roles]
     * user_role = *:ToDoItemsJdo:*:*,\
     *             *:ToDoItem:*:*
     * self-install_role = *:ToDoItemsFixturesService:install:*
     * admin_role = *
     * </pre>
     * 
     * <p>
     * This 'ini' file can then be referenced by other realms (if multiple realm are configured
     * with the Shiro security manager). 
     * 
     * @see #setResourcePath(String)
     */
    public void setResourcePath(String resourcePath) {
        if(permissionToRoleMapper != null) {
            throw new IllegalStateException("Permissions already set, " + permissionToRoleMapper.getClass().getName());
        } 
        final Ini ini = Ini.fromResourcePath(resourcePath);
        this.permissionToRoleMapper = new PermissionToRoleMapperFromIni(ini);
    }

    /**
     * Specify permissions for each role using a formatted string.
     *
     * <pre>
     * ldapRealm.permissionsByRole=\
     *    user_role = *:ToDoItemsJdo:*:*,\
     *                *:ToDoItem:*:*; \
     *    self-install_role = *:ToDoItemsFixturesService:install:* ; \
     *    admin_role = *
     * </pre>
     * 
     * @see #setResourcePath(String)
     */
    @Deprecated
    public void setPermissionsByRole(String permissionsByRoleStr) {
        if(permissionToRoleMapper != null) {
            throw new IllegalStateException("Permissions already set, " + permissionToRoleMapper.getClass().getName());
        } 
        this.permissionToRoleMapper = new PermissionToRoleMapperFromString(permissionsByRoleStr);
    }
    
}
