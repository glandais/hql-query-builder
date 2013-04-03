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
package net.ihe.gazelle.hql.generator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.processing.FilerException;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.FileObject;

import net.ihe.gazelle.hql.generator.model.MetaAttribute;
import net.ihe.gazelle.hql.generator.model.MetaEntity;
import net.ihe.gazelle.hql.generator.model.MetaSingleAttribute;
import net.ihe.gazelle.hql.generator.util.Constants;
import net.ihe.gazelle.hql.generator.util.TypeUtils;

/**
 * Helper class to write the actual meta model class using the
 * {@link javax.annotation.processing.Filer} API.
 * 
 * @author Emmanuel Bernard
 * @author Hardy Ferentschik
 */
public final class ClassWriter {

	public static enum ClassType {
		QUERY("Query"),

		ENTITY("Entity"),

		ATTRIBUTES("Attributes");

		public final String suffix;

		ClassType(String suffix) {
			this.suffix = suffix;
		}
	}

	private ClassWriter() {
	}

	public static void writeFile(MetaEntity entity, Context context) {
		writeFile(entity, context, ClassType.ATTRIBUTES);
		writeFile(entity, context, ClassType.ENTITY);
		writeFile(entity, context, ClassType.QUERY);
	}

	public static void writeFile(MetaEntity entity, Context context, ClassType classType) {
		entity.clearImports();
		try {
			String metaModelPackage = entity.getPackageName();
			// need to generate the body first, since this will also update the
			// required imports which need to
			// be written out first
			String body = generateBody(entity, context, classType).toString();

			FileObject fo = context.getProcessingEnvironment().getFiler()
					.createSourceFile(getFullyQualifiedClassName(entity, metaModelPackage, classType));
			OutputStream os = fo.openOutputStream();
			PrintWriter pw = new PrintWriter(os);

			if (!metaModelPackage.isEmpty()) {
				pw.println("package " + metaModelPackage + ";");
				pw.println();
			}
			pw.println(entity.generateImports());
			pw.println(body);

			pw.flush();
			pw.close();
		} catch (FilerException filerEx) {
			context.logMessage(Diagnostic.Kind.ERROR, "Problem with Filer: " + filerEx.getMessage());
		} catch (IOException ioEx) {
			context.logMessage(Diagnostic.Kind.ERROR,
					"Problem opening file to write MetaModel for " + entity.getSimpleName() + ioEx.getMessage());
		}
	}

	/**
	 * Generate everything after import statements.
	 * 
	 * @param entity
	 *            The meta entity for which to write the body
	 * @param context
	 *            The processing context
	 * @param queryBuilder
	 * 
	 * @return body content
	 * @throws IOException
	 */
	private static StringBuffer generateBody(MetaEntity entity, Context context, ClassType classType)
			throws IOException {
		StringWriter sw = new StringWriter();
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(sw);

			Map<String, String> genParams = new HashMap<String, String>();
			genParams.put("TYPE", entity.getSimpleName());
			genParams.put("CLASSNAME", entity.getSimpleName() + classType.suffix);

			switch (classType) {
			case QUERY:
				genParams.put("SUPER", entity.getSimpleName() + ClassType.ENTITY.suffix);

				entity.importType("javax.persistence.EntityManager");
				entity.importType("net.ihe.gazelle.hql.providers.EntityManagerService");

				addFilteredResource("/genQueryHeader.java", pw, genParams);
				addFilteredResource("/genQueryBody.java", pw, genParams);
				addFilteredResource("/genPathHQLQueryBuilder.java", pw, genParams);

				break;
			case ENTITY:
				genParams.put("SUPER", entity.getSimpleName() + ClassType.ATTRIBUTES.suffix);

				addFilteredResource("/genPathHeader.java", pw, genParams);
				addFilteredResource("/genPathBody.java", pw, genParams);
				addFilteredResource("/genEntityBody.java", pw, genParams);

				break;
			case ATTRIBUTES:
				String superClassName = findMappedSuperClass(entity, context);
				if (superClassName != null) {
					genParams.put("SUPER", superClassName + ClassType.ATTRIBUTES.suffix);
				} else {
					genParams.put("SUPER", "HQLSafePathEntity");
				}

				addFilteredResource("/genPathHeader.java", pw, genParams);
				addFilteredResource("/genPathBody.java", pw, genParams);
				addFilteredResource("/genAttributesBody.java", pw, genParams);

				List<MetaAttribute> members = entity.getMembers();
				for (MetaAttribute metaMember : members) {
					pw.println();
					metaMember.getDeclarationString(pw);
				}

				List<String> uniqueColumnNames = entity.getUniqueColumnNames();
				if (uniqueColumnNames == null || uniqueColumnNames.size() == 0) {
					pw.println();
					pw.write("	public List<String> getUniqueAttributeColumns() {");
					pw.println();
					pw.write("		return super.getUniqueAttributeColumns();");
					pw.println();
					pw.write("	}");
					pw.println();
					pw.println();
				} else {
					pw.println();
					pw.write("	private static final List<String> UNIQUE_ATTRIBUTES = new ArrayList<String>();");
					pw.println();
					pw.write("	static {");
					pw.println();
					for (String uniqueColumnName : uniqueColumnNames) {
						pw.write("		UNIQUE_ATTRIBUTES.add(\"" + uniqueColumnName + "\");");
						pw.println();
					}
					pw.write("	}");
					pw.println();
					pw.write("	public List<String> getUniqueAttributeColumns() {");
					pw.println();
					pw.write("		return UNIQUE_ATTRIBUTES;");
					pw.println();
					pw.write("	}");
					pw.println();
				}

				pw.println();
				pw.write("	public Map<String, HQLSafePath<?>> getSingleAttributes() {");
				pw.println();
				pw.write("		Map<String, HQLSafePath<?>> paths = super.getSingleAttributes();");
				pw.println();
				for (MetaAttribute metaMember : members) {
					if (metaMember instanceof MetaSingleAttribute) {
						MetaSingleAttribute metaSingleAttribute = (MetaSingleAttribute) metaMember;
						String columnName = metaSingleAttribute.getColumnName();
						if (columnName != null) {
							pw.write("		paths.put(\"" + columnName + "\", " + metaMember.getPropertyName() + "());");
							pw.println();
						}
					}
				}
				pw.write("		return paths;");
				pw.println();
				pw.write("	}");
				pw.println();
				pw.println();

				pw.println();
				pw.write("	public Set<HQLSafePath<?>> getAllAttributes() {");
				pw.println();
				pw.write("		Set<HQLSafePath<?>> paths = super.getAllAttributes();");
				pw.println();
				for (MetaAttribute metaMember : members) {
					pw.write("		paths.add(" + metaMember.getPropertyName() + "());");
					pw.println();
				}
				pw.write("		return paths;");
				pw.println();
				pw.write("	}");
				pw.println();
				pw.println();

				pw.println();
				pw.write("	public Set<HQLSafePath<?>> getIdAttributes() {");
				pw.println();
				pw.write("		Set<HQLSafePath<?>> paths = super.getIdAttributes();");
				pw.println();
				for (MetaAttribute metaMember : members) {
					if (metaMember instanceof MetaSingleAttribute) {
						MetaSingleAttribute metaSingleAttribute = (MetaSingleAttribute) metaMember;
						if (metaSingleAttribute.isId()) {
							pw.write("		paths.add(" + metaMember.getPropertyName() + "());");
							pw.println();
						}
					}
				}
				pw.write("		return paths;");
				pw.println();
				pw.write("	}");
				pw.println();
				pw.println();

				pw.println();
				pw.write("	public Set<HQLSafePath<?>> getNotExportedAttributes() {");
				pw.println();
				pw.write("		Set<HQLSafePath<?>> paths = super.getNotExportedAttributes();");
				pw.println();
				for (MetaAttribute metaMember : members) {
					if (metaMember.isNotExported()) {
						pw.write("		paths.add(" + metaMember.getPropertyName() + "());");
						pw.println();
					}
				}
				pw.write("		return paths;");
				pw.println();
				pw.write("	}");
				pw.println();

				pw.println();
				pw.println();

				break;
			}

			pw.println();
			pw.println("}");
			return sw.getBuffer();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	private static void addFilteredResource(String resource, PrintWriter pw, Map<String, String> genParams)
			throws IOException {
		InputStream is = ClassWriter.class.getResourceAsStream(resource);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		Set<Entry<String, String>> entrySet = genParams.entrySet();

		String line;
		while ((line = br.readLine()) != null) {
			for (Entry<String, String> entry : entrySet) {
				line = line.replaceAll(entry.getKey(), entry.getValue());
			}
			pw.println(line);
		}
		br.close();
	}

	private static String findMappedSuperClass(MetaEntity entity, Context context) {
		TypeMirror superClass = entity.getTypeElement().getSuperclass();
		// superclass of Object is of NoType which returns some other kind
		while (superClass.getKind() == TypeKind.DECLARED) {
			// F..king Ch...t Have those people used their horrible APIs even
			// once?
			final Element superClassElement = ((DeclaredType) superClass).asElement();
			String superClassName = ((TypeElement) superClassElement).getQualifiedName().toString();
			if (extendsSuperMetaModel(superClassElement, entity.isMetaComplete(), context)) {
				return superClassName;
			}
			superClass = ((TypeElement) superClassElement).getSuperclass();
		}
		return null;
	}

	/**
	 * Checks whether this metamodel class needs to extend another metamodel
	 * class. This methods checks whether the processor has generated a
	 * metamodel class for the super class, but it also allows for the
	 * possibility that the metamodel class was generated in a previous
	 * compilation (eg it could be part of a separate jar. See also METAGEN-35).
	 * 
	 * @param superClassElement
	 *            the super class element
	 * @param entityMetaComplete
	 *            flag indicating if the entity for which the metamodel should
	 *            be generarted is metamodel complete. If so we cannot use
	 *            reflection to decide whether we have to add the extend clause
	 * @param context
	 *            the execution context
	 * 
	 * @return {@code true} in case there is super class meta model to extend
	 *         from {@code false} otherwise.
	 */
	private static boolean extendsSuperMetaModel(Element superClassElement, boolean entityMetaComplete, Context context) {
		// if we processed the superclass in the same run we definitely need to
		// extend
		String superClassName = ((TypeElement) superClassElement).getQualifiedName().toString();
		if (context.containsMetaEntity(superClassName) || context.containsMetaEmbeddable(superClassName)) {
			return true;
		}

		// to allow for the case that the metamodel class for the super entity
		// is for example contained in another
		// jar file we use reflection. However, we need to consider the fact
		// that there is xml configuration
		// and annotations should be ignored
		if (!entityMetaComplete
				&& (TypeUtils.containsAnnotation(superClassElement, Constants.ENTITY) || TypeUtils.containsAnnotation(
						superClassElement, Constants.MAPPED_SUPERCLASS))) {
			return true;
		}

		return false;
	}

	private static String getFullyQualifiedClassName(MetaEntity entity, String metaModelPackage, ClassType classType) {
		String fullyQualifiedClassName = "";
		if (!metaModelPackage.isEmpty()) {
			fullyQualifiedClassName = fullyQualifiedClassName + metaModelPackage + ".";
		}
		fullyQualifiedClassName = fullyQualifiedClassName + entity.getSimpleName() + classType.suffix;
		return fullyQualifiedClassName;
	}

}
