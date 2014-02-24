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
package net.ihe.gazelle.hql.generator.annotation;

import javax.lang.model.element.Element;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import net.ihe.gazelle.hql.generator.model.MetaSingleAttribute;

/**
 * @author Max Andersen
 * @author Hardy Ferentschik
 * @author Emmanuel Bernard
 */
public class AnnotationMetaSingleAttribute extends AnnotationMetaAttribute
		implements MetaSingleAttribute {

	private String columnName = null;
	private boolean isUnique = false;
	private int uniqueSet = 0;
	private boolean isId = false;

	public AnnotationMetaSingleAttribute(AnnotationMetaEntity parent,
			Element element, String type, AttributeType attributeType) {
		super(parent, element, type, attributeType);

		Id id = element.getAnnotation(Id.class);
		isId = false;
		if (id != null) {
			isId = true;
		} else {
			EmbeddedId embeddedId = element.getAnnotation(EmbeddedId.class);
			if (embeddedId != null) {
				isId = true;
			}
		}
		Column column = element.getAnnotation(Column.class);
		isUnique = false;

		if (column != null) {
			columnName = column.name();
			if (column.unique()) {
				isUnique = true;
			}
		} else {
			JoinColumn joinColumn = element.getAnnotation(JoinColumn.class);
			if (joinColumn != null) {
				columnName = joinColumn.name();
				if (joinColumn.unique()) {
					isUnique = true;
				}
			}
		}

	}

	public String getColumnName() {
		return columnName;
	}

	public boolean isUnique() {
		return isUnique;
	}

	public void setUnique(boolean isUnique) {
		this.isUnique = isUnique;
	}

	public int getUniqueSet() {
		return uniqueSet;
	}

	public void setUniqueSet(int uniqueSet) {
		this.uniqueSet = uniqueSet;
	}

	public boolean isId() {
		return isId;
	}

}
