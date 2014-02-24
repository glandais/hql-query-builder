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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import net.ihe.gazelle.hql.generator.Context;
import net.ihe.gazelle.hql.generator.ImportContextImpl;
import net.ihe.gazelle.hql.generator.model.ImportContext;
import net.ihe.gazelle.hql.generator.model.MetaAttribute;
import net.ihe.gazelle.hql.generator.model.MetaEntity;
import net.ihe.gazelle.hql.generator.model.MetaSingleAttribute;
import net.ihe.gazelle.hql.generator.util.AccessType;
import net.ihe.gazelle.hql.generator.util.AccessTypeInformation;
import net.ihe.gazelle.hql.generator.util.Constants;
import net.ihe.gazelle.hql.generator.util.TypeUtils;

/**
 * Class used to collect meta information about an annotated entity.
 * 
 * @author Max Andersen
 * @author Hardy Ferentschik
 * @author Emmanuel Bernard
 */
public class AnnotationMetaEntity implements MetaEntity {

	private static Set<String> WRONG_UNIQUE = Collections.synchronizedSet(new HashSet<String>());

	private final ImportContext importContext;
	private final TypeElement element;
	private final Map<String, MetaAttribute> members;
	private Context context;

	private AccessTypeInformation entityAccessTypeInfo;
	private String dbSynchronizedSet = null;

	public AnnotationMetaEntity(TypeElement element, Context context) {
		this(element, context, false);
	}

	protected AnnotationMetaEntity(TypeElement element, Context context, boolean lazilyInitialised) {
		this.element = element;
		this.context = context;
		this.members = new HashMap<String, MetaAttribute>();
		this.importContext = new ImportContextImpl(getPackageName());
		if (!lazilyInitialised) {
			init();
		}
	}

	public String getDbSynchronizedSet() {
		return dbSynchronizedSet;
	}

	public AccessTypeInformation getEntityAccessTypeInfo() {
		return entityAccessTypeInfo;
	}

	public final Context getContext() {
		return context;
	}

	public final String getSimpleName() {
		return element.getSimpleName().toString();
	}

	public final String getQualifiedName() {
		return element.getQualifiedName().toString();
	}

	public final String getPackageName() {
		PackageElement packageOf = context.getElementUtils().getPackageOf(element);
		return context.getElementUtils().getName(packageOf.getQualifiedName()).toString();
	}

	public List<MetaAttribute> getMembers() {
		return new ArrayList<MetaAttribute>(members.values());
	}

	@Override
	public boolean isMetaComplete() {
		return false;
	}

	public void mergeInMembers(Collection<MetaAttribute> attributes) {
		for (MetaAttribute attribute : attributes) {
			members.put(attribute.getPropertyName(), attribute);
		}
	}

	public final String generateImports() {
		return importContext.generateImports();
	}

	@Override
	public void clearImports() {
		importContext.clearImports();
	}

	public final String importType(String fqcn) {
		return importContext.importType(fqcn);
	}

	public final String staticImport(String fqcn, String member) {
		return importContext.staticImport(fqcn, member);
	}

	public final TypeElement getTypeElement() {
		return element;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("AnnotationMetaEntity");
		sb.append("{element=").append(element);
		sb.append(", members=").append(members);
		sb.append('}');
		return sb.toString();
	}

	protected TypeElement getElement() {
		return element;
	}

	protected final void init() {
		List<List<String>> uniqueColumnNames = new ArrayList<List<String>>();

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

		boolean hasUniqueConstraintsOnTable = false;
		boolean hasUniqueConstraintsOnColumn = false;

		Entity entity = element.getAnnotation(Entity.class);
		if (entity != null) {
			Table table = element.getAnnotation(Table.class);
			if (table != null) {
				UniqueConstraint[] uniqueConstraints = table.uniqueConstraints();
				if (uniqueConstraints != null) {
					for (UniqueConstraint uniqueConstraint : uniqueConstraints) {
						hasUniqueConstraintsOnTable = true;
						String[] columnNames = uniqueConstraint.columnNames();
						if (columnNames != null) {
							uniqueColumnNames.add(Arrays.asList(columnNames));
						}
					}
				}
			}
		}
		if (uniqueColumnNames.size() > 1) {
			System.out.println(getQualifiedName());
		}

		TypeUtils.determineAccessTypeForHierarchy(element, context);
		entityAccessTypeInfo = context.getAccessTypeInfo(getQualifiedName());

		List<? extends Element> fieldsOfClass = ElementFilter.fieldsIn(element.getEnclosedElements());
		addPersistentMembers(fieldsOfClass, AccessType.FIELD);

		List<? extends Element> methodsOfClass = ElementFilter.methodsIn(element.getEnclosedElements());
		addPersistentMembers(methodsOfClass, AccessType.PROPERTY);

		int uniqueSet = 0;

		Set<Entry<String, MetaAttribute>> entrySet = members.entrySet();
		for (Entry<String, MetaAttribute> entry : entrySet) {
			if (entry.getValue() instanceof MetaSingleAttribute) {
				MetaSingleAttribute metaSingleAttribute = (MetaSingleAttribute) entry.getValue();

				if (metaSingleAttribute.isUnique()) {
					hasUniqueConstraintsOnColumn = true;
				}

				if (hasUniqueConstraintsOnTable) {

					metaSingleAttribute.setUniqueSet(-1);
					for (int i = 0; i < uniqueColumnNames.size(); i++) {
						for (String uniqueColumnName : uniqueColumnNames.get(i)) {
							if (uniqueColumnName.equals(metaSingleAttribute.getColumnName())) {
								metaSingleAttribute.setUnique(true);
								metaSingleAttribute.setUniqueSet(i);
							}
						}
					}

					if (metaSingleAttribute.getUniqueSet() == -1) {
						// Override column uniqueness as it is ignored by Hibernate!
						metaSingleAttribute.setUnique(false);
						metaSingleAttribute.setUniqueSet(0);
					}
				} else {
					if (metaSingleAttribute.isUnique()) {
						metaSingleAttribute.setUniqueSet(uniqueSet);
						uniqueSet++;
					}
				}

			}
		}

		if (hasUniqueConstraintsOnTable && hasUniqueConstraintsOnColumn) {
			if (!WRONG_UNIQUE.contains(getQualifiedName())) {
				WRONG_UNIQUE.add(getQualifiedName());
				// context.logMessage(Kind.WARNING, getQualifiedName()
				// +
				// " contains uniqueConstraints in @Table AND unique = true on @Column/@JoinColumn");
			}
		}
	}

	private void addPersistentMembers(List<? extends Element> membersOfClass, AccessType membersKind) {
		for (Element memberOfClass : membersOfClass) {
			AccessType forcedAccessType = TypeUtils.determineAnnotationSpecifiedAccessType(memberOfClass);
			if (entityAccessTypeInfo.getAccessType() != membersKind && forcedAccessType == null) {
				continue;
			}

			if (TypeUtils.containsAnnotation(memberOfClass, Constants.TRANSIENT)
					|| memberOfClass.getModifiers().contains(Modifier.TRANSIENT)
					|| memberOfClass.getModifiers().contains(Modifier.STATIC)) {
				continue;
			}

			MetaAttributeGenerationVisitor visitor = new MetaAttributeGenerationVisitor(this, context);
			AnnotationMetaAttribute result = memberOfClass.asType().accept(visitor, memberOfClass);
			if (result != null) {
				members.put(result.getPropertyName(), result);
			}
		}
	}

}
