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
package com.github.glandais.hql.generator;

import com.github.glandais.hql.generator.model.MetaAttribute;
import com.github.glandais.hql.generator.model.MetaEntity;
import com.github.glandais.hql.generator.util.Constants;
import com.github.glandais.hql.generator.util.TypeUtils;

import javax.annotation.processing.FilerException;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * Helper class to write the actual meta model class using the {@link javax.annotation.processing.Filer} API.
 *
 * @author Emmanuel Bernard
 * @author Hardy Ferentschik
 */
public final class ClassWriter {

    public static final String SUFFIX = "_";

    private ClassWriter() {
    }

    public static void writeQueries(Element hqlQuery, ProcessingEnvironment env, Collection<MetaEntity> generatedModelClasses, Context context) {

        PackageElement packageElement = env.getElementUtils().getPackageOf(hqlQuery);
        String metaModelPackage = packageElement.getQualifiedName().toString();

        try {
            String fullyQualifiedClassName = metaModelPackage + "." + hqlQuery.getSimpleName() + "Provider";
            FileObject fo = context.getProcessingEnvironment().getFiler().createSourceFile(fullyQualifiedClassName);
            OutputStream os = fo.openOutputStream();
            PrintWriter pw = new PrintWriter(os);
            pw.println("package " + metaModelPackage + ";");
            pw.println();
            pw.println("import javax.enterprise.context.ApplicationScoped;");
            pw.println("import javax.inject.Inject;");
            pw.println("import javax.persistence.EntityManager;");
            Set<String> imports = new TreeSet<>();
            for (MetaEntity generatedModelClass : generatedModelClasses) {
                imports.add(generatedModelClass.getPackageName());
            }
            for (String impotPackage : imports) {
                pw.println("import " + impotPackage + ".*;");
            }
            pw.println();
            pw.println("@ApplicationScoped");
            pw.println("public class " + hqlQuery.getSimpleName() + "Provider implements " + hqlQuery.getSimpleName() + " {");
            pw.println();
            pw.println("\t@Inject");
            pw.println("\tEntityManager em;");
            for (MetaEntity generatedModelClass : generatedModelClasses) {
                pw.println();
                String simpleName = generatedModelClass.getSimpleName();
                pw.println("\tpublic " + simpleName + SUFFIX + "<" + simpleName + "> " +
                        simpleName.toLowerCase() + "() {");
                pw.println("\t\treturn new " + simpleName + SUFFIX + "<>(em);");
                pw.println("\t}");
            }
            pw.println();
            pw.println("}");
            pw.close();
        } catch (FilerException filerEx) {
            context.logMessage(Diagnostic.Kind.ERROR, "Problem with Filer: "
                    + filerEx.getMessage());
        } catch (IOException ioEx) {
            context.logMessage(
                    Diagnostic.Kind.ERROR,
                    "Problem opening file to write MetaModel for "
                            + hqlQuery.getSimpleName() + ioEx.getMessage());
        }
    }

    public static void writeFile(MetaEntity entity, Context context) {
        entity.clearImports();
        try {
            String metaModelPackage = entity.getPackageName();

            String fullyQualifiedClassName = getFullyQualifiedClassName(entity, metaModelPackage);
            FileObject fo = context.getProcessingEnvironment().getFiler()
                    .createSourceFile(fullyQualifiedClassName);
            OutputStream os = fo.openOutputStream();
            PrintWriter pw = new PrintWriter(os);

            if (!metaModelPackage.isEmpty()) {
                pw.println("package " + metaModelPackage + ";");
                pw.println();
            }
            // need to generate the body first, since this will also update the
            // required imports which need to
            // be written out first
            String body = generateBody(entity, context).toString();
            pw.println(entity.generateImports());
            pw.println(body);

            pw.flush();
            pw.close();

            context.logMessage(Diagnostic.Kind.NOTE, "Generated " + fullyQualifiedClassName);
        } catch (FilerException filerEx) {
            context.logMessage(Diagnostic.Kind.ERROR, "Problem with Filer: "
                    + filerEx.getMessage());
        } catch (IOException ioEx) {
            context.logMessage(
                    Diagnostic.Kind.ERROR,
                    "Problem opening file to write MetaModel for "
                            + entity.getSimpleName() + ioEx.getMessage());
        }
    }

    /**
     * Generate everything after import statements.
     *
     * @param entity  The meta entity for which to write the body
     * @param context The processing context
     * @return body content
     * @throws IOException
     */
    private static StringBuffer generateBody(MetaEntity entity,
                                             Context context) throws IOException {
        StringWriter sw = new StringWriter();
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(sw);

            Map<String, String> genParams = new HashMap<String, String>();
            genParams.put("TYPE", entity.getSimpleName());
            genParams.put("CLASSNAME", entity.getSimpleName() + SUFFIX);

            String superClassNameUnique = findMappedSuperClass(entity, context);
            if (superClassNameUnique != null) {
                genParams.put("SUPER", superClassNameUnique
                        + SUFFIX);
            } else {
                genParams.put("SUPER", "HQLSafePathEntity");
            }

            entity.importType("javax.persistence.EntityManager");

            addFilteredResource("/template.java", pw, genParams);

            List<MetaAttribute> membersUnique = entity.getMembers();
            for (MetaAttribute metaMember : membersUnique) {
                pw.println();
                metaMember.getDeclarationString(pw);
            }

            pw.println();
            pw.println();

            pw.println();
            pw.println("}");
            return sw.getBuffer();
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }

    private static void addFilteredResource(String resource, PrintWriter pw,
                                            Map<String, String> genParams) throws IOException {
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

    private static String findMappedSuperClass(MetaEntity entity,
                                               Context context) {
        TypeMirror superClass = entity.getTypeElement().getSuperclass();
        // superclass of Object is of NoType which returns some other kind
        while (superClass.getKind() == TypeKind.DECLARED) {
            // F..king Ch...t Have those people used their horrible APIs even
            // once?
            final Element superClassElement = ((DeclaredType) superClass)
                    .asElement();
            String superClassName = ((TypeElement) superClassElement)
                    .getQualifiedName().toString();
            if (extendsSuperMetaModel(superClassElement,
                    entity.isMetaComplete(), context)) {
                return superClassName;
            }
            superClass = ((TypeElement) superClassElement).getSuperclass();
        }
        return null;
    }

    /**
     * Checks whether this metamodel class needs to extend another metamodel class. This methods checks whether the processor has generated a metamodel class for the super class, but it also allows
     * for the possibility that the metamodel class was generated in a previous compilation (eg it could be part of a separate jar. See also METAGEN-35).
     *
     * @param superClassElement  the super class element
     * @param entityMetaComplete flag indicating if the entity for which the metamodel should be generarted is metamodel complete. If so we cannot use reflection to decide whether we have to add the extend clause
     * @param context            the execution context
     * @return {@code true} in case there is super class meta model to extend from {@code false} otherwise.
     */
    private static boolean extendsSuperMetaModel(Element superClassElement,
                                                 boolean entityMetaComplete, Context context) {
        // if we processed the superclass in the same run we definitely need to
        // extend
        String superClassName = ((TypeElement) superClassElement)
                .getQualifiedName().toString();
        if (context.containsMetaEntity(superClassName)
                || context.containsMetaEmbeddable(superClassName)) {
            return true;
        }

        // to allow for the case that the metamodel class for the super entity
        // is for example contained in another
        // jar file we use reflection. However, we need to consider the fact
        // that there is xml configuration
        // and annotations should be ignored
        return !entityMetaComplete
                && (TypeUtils.containsAnnotation(superClassElement,
                Constants.ENTITY) || TypeUtils.containsAnnotation(
                superClassElement, Constants.MAPPED_SUPERCLASS));
    }

    private static String getFullyQualifiedClassName(MetaEntity entity,
                                                     String metaModelPackage) {
        String fullyQualifiedClassName = "";
        if (!metaModelPackage.isEmpty()) {
            fullyQualifiedClassName = fullyQualifiedClassName
                    + metaModelPackage + ".";
        }
        fullyQualifiedClassName = fullyQualifiedClassName
                + entity.getSimpleName() + SUFFIX;
        return fullyQualifiedClassName;
    }

}
