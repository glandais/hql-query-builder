/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.glandais.hql.generator.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hardy Ferentschik
 */
public final class Constants {
    // we are trying to to reference jpa annotations directly
    public static final String ENTITY = "javax.persistence.Entity";
    public static final String MAPPED_SUPERCLASS = "javax.persistence.MappedSuperclass";
    public static final String EMBEDDABLE = "javax.persistence.Embeddable";
    public static final String ID = "javax.persistence.Id";
    public static final String EMBEDDED_ID = "javax.persistence.EmbeddedId";
    public static final String TRANSIENT = "javax.persistence.Transient";
    public static final String BASIC = "javax.persistence.Basic";
    public static final String ONE_TO_ONE = "javax.persistence.OneToOne";
    public static final String ONE_TO_MANY = "javax.persistence.OneToMany";
    public static final String MANY_TO_ONE = "javax.persistence.ManyToOne";
    public static final String MANY_TO_MANY = "javax.persistence.ManyToMany";
    public static final String MAP_KEY_CLASS = "javax.persistence.MapKeyClass";
    public static final String ELEMENT_COLLECTION = "javax.persistence.ElementCollection";
    public static final String ACCESS = "javax.persistence.Access";
    public static final String PATH_SEPARATOR = "/";
    public static Map<String, String> COLLECTIONS = new HashMap<String, String>();
    public static List<String> BASIC_TYPES = new ArrayList<String>();
    public static List<String> BASIC_ARRAY_TYPES = new ArrayList<String>();

    static {
        COLLECTIONS.put(java.util.Collection.class.getName(), "javax.persistence.metamodel.CollectionAttribute");
        COLLECTIONS.put(java.util.Set.class.getName(), "javax.persistence.metamodel.SetAttribute");
        COLLECTIONS.put(java.util.List.class.getName(), "javax.persistence.metamodel.ListAttribute");
        COLLECTIONS.put(java.util.Map.class.getName(), "javax.persistence.metamodel.MapAttribute");

        // Hibernate also supports the SortedSet and SortedMap interfaces
        COLLECTIONS.put(java.util.SortedSet.class.getName(), "javax.persistence.metamodel.SetAttribute");
        COLLECTIONS.put(java.util.SortedMap.class.getName(), "javax.persistence.metamodel.MapAttribute");
    }

    static {
        BASIC_TYPES.add(java.lang.String.class.getName());
        BASIC_TYPES.add(java.lang.Boolean.class.getName());
        BASIC_TYPES.add(java.lang.Byte.class.getName());
        BASIC_TYPES.add(java.lang.Character.class.getName());
        BASIC_TYPES.add(java.lang.Short.class.getName());
        BASIC_TYPES.add(java.lang.Integer.class.getName());
        BASIC_TYPES.add(java.lang.Long.class.getName());
        BASIC_TYPES.add(java.lang.Float.class.getName());
        BASIC_TYPES.add(java.lang.Double.class.getName());
        BASIC_TYPES.add(java.math.BigInteger.class.getName());
        BASIC_TYPES.add(java.math.BigDecimal.class.getName());
        BASIC_TYPES.add(java.util.Date.class.getName());
        BASIC_TYPES.add(java.util.Calendar.class.getName());
        BASIC_TYPES.add(java.sql.Date.class.getName());
        BASIC_TYPES.add(java.sql.Time.class.getName());
        BASIC_TYPES.add(java.sql.Timestamp.class.getName());
        BASIC_TYPES.add(java.sql.Blob.class.getName());
    }

    static {
        BASIC_ARRAY_TYPES.add(java.lang.Character.class.getName());
        BASIC_ARRAY_TYPES.add(java.lang.Byte.class.getName());
    }

    private Constants() {
    }
}


