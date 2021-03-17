/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
package org.estatio.module.base.dom.apptenancy;

import java.util.List;
import java.util.Objects;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

/**
 * Value type.
 */
public final class ApplicationTenancyLevel implements Comparable<ApplicationTenancyLevel> {

    private static final String ROOT_PATH = "/";
    private static final String PATH_SEPARATOR = "/";
    private static final String OTHER_PART = "_";

    public static ApplicationTenancyLevel ROOT = ApplicationTenancyLevel.of(ROOT_PATH);
    public static final ApplicationTenancyLevel ITALY = ROOT.child("ITA");
    public static final ApplicationTenancyLevel FRANCE = ROOT.child("FRA");

    public static ApplicationTenancyLevel of(final String path) {
        if (path == null) {
            return null;
        }
        return new ApplicationTenancyLevel(path);
    }

    public static ApplicationTenancyLevel of(final ApplicationTenancy applicationTenancy) {
        if (applicationTenancy == null) {
            return null;
        }
        return of(applicationTenancy.getPath());
    }

    public static ApplicationTenancyLevel of(final WithApplicationTenancy withApplicationTenancy) {
        if (withApplicationTenancy == null) {
            return null;
        }
        final ApplicationTenancy applicationTenancy = withApplicationTenancy.getApplicationTenancy();
        return of(applicationTenancy);
    }

    public ApplicationTenancyLevel(final String path) {
        this.path = path;
        this.name = "";
    }

    public ApplicationTenancyLevel(final String path, final String name) {
        this.path = path;
        this.name = name;
    }

    // //////////////////////////////////////

    private final String path;

    public String getPath() {
        return path;
    }

    private final String name;

    public String getName() {
        return name;
    }


    //region > parentOf, childOf, peerOf

    public boolean parentOf(final ApplicationTenancyLevel other) {
        return other != null && contains(this, other);
    }

    public boolean childOf(final ApplicationTenancyLevel other) {
        return other != null && contains(other, this);
    }

    public boolean peerOf(final ApplicationTenancyLevel other) {
        return !this.equals(other) && !this.parentOf(other) && !other.parentOf(this);
    }

    private static boolean contains(final ApplicationTenancyLevel container, final ApplicationTenancyLevel contained) {
        return contained.path.startsWith(container.path) && contained.path.length() > container.path.length();
    }
    //endregion


    //region > parent
    public ApplicationTenancyLevel parent() {
        if(path.equals(ROOT_PATH)) {
            return null;
        }
        final List<String> parts = getParts();
        final List<String> strings = parts.subList(0, parts.size() - 1);
        final String join = Joiner.on(PATH_SEPARATOR).join(strings);
        return new ApplicationTenancyLevel(ROOT_PATH + join);
    }

    public ApplicationTenancyLevel child(final String child) {
        if(child.contains(PATH_SEPARATOR)) {
            throw new IllegalArgumentException(String.format("Argument '%s' must not contain path separator '%s'", child, PATH_SEPARATOR));
        }

        return ApplicationTenancyLevel.of(getPath()+(isRoot()?"":ROOT_PATH)+child);
    }

    /**
     * Returns a mutable copy of the parts.
     *
     * <p>
     *     For example:
     * </p>
     * <ul>
     *     <li>"/" -> []</li>
     *     <li>"/a" -> ["a"]</li>
     *     <li>"/a/bb" -> ["a", "bb"]</li>
     * </ul>
     */
    List<String> getParts() {
        return Lists.newArrayList(Iterables.filter(Splitter.on('/').split(path), input -> !Strings.isNullOrEmpty(input)
        ));
    }
    //endregion

    //region > is{Root|Country|Property|Local}{|Other}

    public boolean isRoot() {
        return ROOT_PATH.equals(getPath());
    }

    public boolean isRootOther() {
        return getParts().size() == 1 && OTHER_PART.equals(getParts().get(0));
    }

    public boolean isCountry() {
        return getParts().size() == 1 && !isRootOther();
    }


    public boolean isCountryOther() {
        // /it/_
        return getParts().size() == 2 && OTHER_PART.equals(getParts().get(1));
    }

    public boolean isProperty() {
        // /it/CAR
        return getParts().size() == 2 && !isCountryOther();
    }

    public boolean isLocalDefault() {
        // /it/CAR/_
        return isLocalNamed(OTHER_PART);
    }

    public boolean isLocalNamed(final String name) {
        // /it/CAR/xx
        return getParts().size() == 3 && Objects.equals(name, getParts().get(2));
    }

    public boolean isPropertyOf(final ApplicationTenancyLevel countryLevel) {
        return countryLevel.isCountry() &&
                isProperty() &&
                childOf(countryLevel);
    }


    //endregion

    //region > getCountryPath

    public String getCountryPath() {
        if(isRoot()) {
            throw new IllegalArgumentException("Tenancy level is 'root'.");
        }
        if(isRootOther()) {
            throw new IllegalArgumentException("Tenancy level is 'root other'.");
        }
        return ROOT_PATH + getParts().get(0);
    }
    //endregion


    //region > equals, hashCode
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ApplicationTenancyLevel that = (ApplicationTenancyLevel) o;

        return !(path != null ? !path.equals(that.path) : that.path != null);

    }

    @Override
    public int hashCode() {
        return path != null ? path.hashCode() : 0;
    }
    //endregion

    //region > Comparable
    @Override
    public int compareTo(final ApplicationTenancyLevel o) {
        return path.compareTo(o.path);
    }
    //endregion

    //region > toString
    @Override
    public String toString() {
        return path;
    }
    //endregion


    public static final class Predicates {
        private Predicates(){}

        public final static Predicate<ApplicationTenancy> childrenOf(final ApplicationTenancyLevel level) {
            return input -> {
                final ApplicationTenancyLevel candidate = of(input.getPath());
                return candidate.childOf(level);
            };
        }

        public final static Predicate<ApplicationTenancy> parentsOf(final ApplicationTenancyLevel level) {
            return input -> {
                final ApplicationTenancyLevel candidate = of(input.getPath());
                return candidate.parentOf(level);
            };
        }

        public final static Predicate<ApplicationTenancy> isRoot() {
            return input -> ApplicationTenancyLevel.of(input).isRoot();
        }

        public final static Predicate<ApplicationTenancy> isCountry() {
            return input -> ApplicationTenancyLevel.of(input).isCountry();
        }

        public final static Predicate<ApplicationTenancy> isCountryOther() {
            return input -> ApplicationTenancyLevel.of(input).isCountryOther();
        }

        public final static Predicate<ApplicationTenancy> isProperty() {
            return input -> ApplicationTenancyLevel.of(input).isProperty();
        }

        public final static Predicate<ApplicationTenancy> isLocalDefault() {
            return input -> ApplicationTenancyLevel.of(input).isLocalDefault();
        }

        public final static Predicate<ApplicationTenancy> isLocal() {
            return input -> ApplicationTenancyLevel.of(input).isLocalNamed("ta");
        }

    }



}

