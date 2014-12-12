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
package org.estatio.dom.instance;

import java.util.List;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public final class Hierarchy implements Comparable<Hierarchy> {

    public static Hierarchy of(String path) {
        return new Hierarchy(path);
    }

    public Hierarchy(String path) {
        this.path = path;
    }

    //region > path

    private final String path;
    public String getPath() {
        return path;
    }

    //endregion

    //region > parentOf, childOf

    public boolean parentOf(final Hierarchy other) {
        return other != null && contains(other, this);
    }

    public boolean childOf(final Hierarchy other) {
        return other != null && contains(this, other);
    }

    private static boolean contains(Hierarchy parent, Hierarchy child) {
        return parent.path.startsWith(child.path) && parent.path.length() > child.path.length();
    }
    //endregion

    //region > parent
    public Hierarchy parent() {
        if(path.equals("/")) {
            return null;
        }
        final List<String> parts = getParts();
        final List<String> strings = parts.subList(0, parts.size() - 1);
        final String join = Joiner.on("/").join(strings);
        return new Hierarchy("/" + join);
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
        return Lists.newArrayList(Iterables.filter(Splitter.on('/').split(path), new Predicate<String>() {
                    @Override
                    public boolean apply(String input) {
                        return !Strings.isNullOrEmpty(input);
                    }
                }
        ));
    }
    //endregion


    //region > equals, hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hierarchy that = (Hierarchy) o;

        return !(path != null ? !path.equals(that.path) : that.path != null);

    }

    @Override
    public int hashCode() {
        return path != null ? path.hashCode() : 0;
    }
    //endregion

    //region > Comparable
    @Override
    public int compareTo(Hierarchy o) {
        return path.compareTo(o.path);
    }
    //endregion

    //region > toString
    @Override
    public String toString() {
        return path;
    }


    //endregion

}

