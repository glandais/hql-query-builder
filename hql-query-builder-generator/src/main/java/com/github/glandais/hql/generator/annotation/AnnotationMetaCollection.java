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
package com.github.glandais.hql.generator.annotation;

import com.github.glandais.hql.generator.model.MetaCollection;

import javax.lang.model.element.*;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Max Andersen
 * @author Hardy Ferentschik
 * @author Emmanuel Bernard
 */
public class AnnotationMetaCollection extends AnnotationMetaAttribute implements MetaCollection {
    private final String collectionType;
    private String dbSynchronizedSet = null;

    public AnnotationMetaCollection(AnnotationMetaEntity parent, Element element, String collectionType,
                                    String elementType, AttributeType attributeType) {
        super(parent, element, elementType, attributeType);
        this.collectionType = collectionType;

        List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
        for (AnnotationMirror annotationMirror : annotationMirrors) {
            Name simpleName = annotationMirror.getAnnotationType().asElement().getSimpleName();
            if (simpleName.contentEquals("DBSynchronized")) {
                Map<ExecutableElement, AnnotationValue> elementValues = (Map<ExecutableElement, AnnotationValue>) annotationMirror
                        .getElementValues();
                Set<Entry<ExecutableElement, AnnotationValue>> entrySet = elementValues.entrySet();
                for (Entry<ExecutableElement, AnnotationValue> entry : entrySet) {
                    if (entry.getKey().getSimpleName().contentEquals("set")) {
                        dbSynchronizedSet = entry.getValue().getValue().toString();
                    }
                }
            }
        }

    }

    public String getDbSynchronizedSet() {
        return dbSynchronizedSet;
    }

}
