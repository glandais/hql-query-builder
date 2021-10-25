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

import com.github.glandais.hql.generator.ClassWriter;
import com.github.glandais.hql.generator.model.MetaAttribute;
import com.github.glandais.hql.generator.model.MetaEntity;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.beans.Introspector;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.Date;

/**
 * Captures all information about an annotated persistent attribute.
 *
 * @author Max Andersen
 * @author Hardy Ferentschik
 * @author Emmanuel Bernard
 */
public abstract class AnnotationMetaAttribute implements MetaAttribute {

    private final Element element;
    private final AnnotationMetaEntity parent;
    private final String type;
    private final AttributeType attributeType;

    public AnnotationMetaAttribute(AnnotationMetaEntity parent, Element element, String type,
                                   AttributeType attributeType) {
        this.element = element;
        this.parent = parent;
        this.type = type;
        this.attributeType = attributeType;
    }

    @Override
    public void getDeclarationString(PrintWriter pw) {
        String typeDeclaration = getTypeDeclaration();
        String propertyName = getPropertyName();
        boolean doReference = false;

        doReference = true;
        ElementKind elementKind = null;

        TypeMirror type = element.asType();
        if (type instanceof DeclaredType) {
            DeclaredType declaredType = (DeclaredType) type;
            Element declaredElement = null;

            if (attributeType == AttributeType.COLLECTION) {
                TypeMirror listType = declaredType.getTypeArguments().get(0);
                if (type instanceof DeclaredType) {
                    DeclaredType listDeclaredType = (DeclaredType) listType;
                    declaredElement = listDeclaredType.asElement();
                }
            } else {
                declaredElement = declaredType.asElement();
            }

            if (declaredElement != null) {
                elementKind = declaredElement.getKind();
            }
        }

        if (ElementKind.ENUM.equals(elementKind) || attributeType == AttributeType.PRIMITIVE
                || attributeType == AttributeType.ARRAY) {
            doReference = false;
        } else {
            if (typeDeclaration.startsWith("java.")) {
                doReference = false;
            }
        }

        pw.println("	/**");
        pw.println("	 * @return Path to " + propertyName + " of type " + typeDeclaration);
        pw.println("	 */");

        if (doReference) {
            String declaredType = parent.importType(typeDeclaration);
            String importType = parent.importType(typeDeclaration + ClassWriter.SUFFIX);
            pw.println("	public " + importType + "<" + declaredType + "> " + propertyName + "() {");
            pw.println("		return new " + importType + "<" + declaredType + ">(path + \"."
                    + propertyName + "\", queryBuilder);");
            pw.println("	}");
        } else {
            String hqlSafePathBasicType = "HQLSafePathBasic";

            try {
                Class<?> clazz;
                try {
                    clazz = Class.forName(typeDeclaration);
                } catch (ClassNotFoundException e) {
                    clazz = Class.forName("java.lang." + typeDeclaration);
                }
                if (Date.class.isAssignableFrom(clazz)) {
                    hqlSafePathBasicType = "HQLSafePathBasicDate";
                } else if (String.class.isAssignableFrom(clazz)) {
                    hqlSafePathBasicType = "HQLSafePathBasicString";
                } else if (Number.class.isAssignableFrom(clazz)) {
                    hqlSafePathBasicType = "HQLSafePathBasicNumber";
                } else if (Instant.class.isAssignableFrom(clazz)) {
                    hqlSafePathBasicType = "HQLSafePathBasicDate";
                }
            } catch (ClassNotFoundException e) {
                System.out.println("Failed to get type for " + typeDeclaration);
            }

            String declaredType = parent.importType(typeDeclaration);
            pw.println("	public " + hqlSafePathBasicType + "<" + declaredType + "> " + propertyName + "() {");
            pw.println("		return new " + hqlSafePathBasicType + "<" + declaredType + ">(this, path + \"."
                    + propertyName + "\", queryBuilder, " + declaredType + ".class);");
            pw.println("	}");
        }
    }

    public AttributeType getAttributeType() {
        return attributeType;
    }

    private void describeClass(Class<?> clazz, StringBuilder sb) {
        sb.append(clazz.getCanonicalName());
        Class<?>[] interfaces = clazz.getInterfaces();
        sb.append(" [");
        for (Class<?> interf : interfaces) {
            sb.append(interf.getCanonicalName());
        }
        sb.append("]");
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) {
            sb.append(" (");
            describeClass(superclass, sb);
            sb.append(")");
        }
    }

    public String getPropertyName() {
        Elements elementsUtil = parent.getContext().getElementUtils();
        if (element.getKind() == ElementKind.FIELD) {
            return element.getSimpleName().toString();
        } else if (element.getKind() == ElementKind.METHOD) {
            String name = element.getSimpleName().toString();
            if (name.startsWith("get")) {
                return elementsUtil.getName(Introspector.decapitalize(name.substring("get".length()))).toString();
            } else if (name.startsWith("is")) {
                return (elementsUtil.getName(Introspector.decapitalize(name.substring("is".length())))).toString();
            }
            return elementsUtil.getName(Introspector.decapitalize(name)).toString();
        } else {
            return elementsUtil.getName(element.getSimpleName() + "/* " + element.getKind() + " */").toString();
        }
    }

    public MetaEntity getHostingEntity() {
        return parent;
    }

    public String getTypeDeclaration() {
        return type;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("AnnotationMetaAttribute");
        sb.append("{element=").append(element);
        sb.append(", type='").append(type).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
