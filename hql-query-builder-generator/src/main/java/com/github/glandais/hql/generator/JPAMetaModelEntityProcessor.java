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

import com.github.glandais.hql.HQLQueries;
import com.github.glandais.hql.generator.annotation.AnnotationEmbeddable;
import com.github.glandais.hql.generator.annotation.AnnotationMetaEntity;
import com.github.glandais.hql.generator.model.MetaEntity;
import com.github.glandais.hql.generator.util.Constants;
import com.github.glandais.hql.generator.util.StringUtil;
import com.github.glandais.hql.generator.util.TypeUtils;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.tools.Diagnostic;
import java.util.*;

/**
 * Main annotation processor.
 *
 * @author Max Andersen
 * @author Hardy Ferentschik
 * @author Emmanuel Bernard
 */
@SupportedAnnotationTypes({"javax.persistence.Entity", "javax.persistence.MappedSuperclass",
        "javax.persistence.Embeddable"})
@SupportedOptions({JPAMetaModelEntityProcessor.DEBUG_OPTION, JPAMetaModelEntityProcessor.PERSISTENCE_XML_OPTION,
        JPAMetaModelEntityProcessor.ORM_XML_OPTION, JPAMetaModelEntityProcessor.LAZY_XML_PARSING})
public class JPAMetaModelEntityProcessor extends AbstractProcessor {
    public static final String DEBUG_OPTION = "debug";
    public static final String PERSISTENCE_XML_OPTION = "persistenceXml";
    public static final String ORM_XML_OPTION = "ormXml";
    public static final String LAZY_XML_PARSING = "lazyXmlParsing";
    public static final String ADD_GENERATION_DATE = "addGenerationDate";

    private static final Boolean ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS = Boolean.FALSE;

    private Context context;
    private ProcessingEnvironment env;

    @Override
    public void init(ProcessingEnvironment env) {
        super.init(env);
        this.env = env;
        context = new Context(env);
        context.logMessage(Diagnostic.Kind.NOTE,
                "Hibernate JPA 2 Static-Metamodel Generator " + Version.getVersionString());

        boolean addGenerationDate = Boolean.parseBoolean(env.getOptions().get(JPAMetaModelEntityProcessor.ADD_GENERATION_DATE));
        context.setAddGenerationDate(addGenerationDate);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnvironment) {
        // see also METAGEN-45
        if (roundEnvironment.processingOver() || annotations.size() == 0) {
            return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
        }

        if (context.isPersistenceUnitCompletelyXmlConfigured()) {
            context.logMessage(Diagnostic.Kind.NOTE,
                    "Skipping the processing of annotations since persistence unit is purely xml configured.");
            return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
        }

        context.logMessage(Diagnostic.Kind.NOTE, JPAMetaModelEntityProcessor.class.getCanonicalName() + " processor");
        context.logMessage(Diagnostic.Kind.NOTE, "Generating HQL entities using JPA annotations");

        Set<? extends Element> elements = roundEnvironment.getRootElements();
        for (Element element : elements) {
            if (isJPAEntity(element)) {
                context.logMessage(Diagnostic.Kind.NOTE, "Processing annotated class " + element.toString());
                handleRootElementAnnotationMirrors(element);
            }
        }

        Collection<MetaEntity> metaModelClasses = createMetaModelClasses();

        for (Element hqlQueries : roundEnvironment.getElementsAnnotatedWith(HQLQueries.class)) {
            ClassWriter.writeQueries(hqlQueries, env, metaModelClasses, context);
        }

        return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
    }

    private Collection<MetaEntity> createMetaModelClasses() {
        // keep track of all classes for which model have been generated
        Collection<MetaEntity> generatedModelClasses = new ArrayList<>();

        for (MetaEntity entity : context.getMetaEntities()) {
            context.logMessage(Diagnostic.Kind.NOTE, "Writing meta model for entity " + entity);
            ClassWriter.writeFile(entity, context);
            generatedModelClasses.add(entity);
        }

        // we cannot process the delayed entities in any order. There might be
        // dependencies between them.
        // we need to process the top level entities first
        Collection<MetaEntity> toProcessEntities = context.getMetaEmbeddables();
        while (!toProcessEntities.isEmpty()) {
            Set<MetaEntity> processedEntities = new HashSet<MetaEntity>();
            int toProcessCountBeforeLoop = toProcessEntities.size();
            for (MetaEntity entity : toProcessEntities) {
                // see METAGEN-36
                if (generatedModelClasses.contains(entity)) {
                    processedEntities.add(entity);
                    continue;
                }
                if (modelGenerationNeedsToBeDeferred(toProcessEntities, entity)) {
                    continue;
                }
                context.logMessage(Diagnostic.Kind.NOTE, "Writing meta model for embeddable/mapped superclass"
                        + entity);
                ClassWriter.writeFile(entity, context);
                processedEntities.add(entity);
            }
            toProcessEntities.removeAll(processedEntities);
            if (toProcessEntities.size() >= toProcessCountBeforeLoop) {
                context.logMessage(Diagnostic.Kind.ERROR, "Potential endless loop in generation of entities.");
            }
        }
        return generatedModelClasses;
    }

    private boolean modelGenerationNeedsToBeDeferred(Collection<MetaEntity> entities, MetaEntity containedEntity) {
        ContainsAttributeTypeVisitor visitor = new ContainsAttributeTypeVisitor(containedEntity.getTypeElement(),
                context);
        for (MetaEntity entity : entities) {
            if (entity.equals(containedEntity)) {
                continue;
            }
            for (Element subElement : ElementFilter.fieldsIn(entity.getTypeElement().getEnclosedElements())) {
                TypeMirror mirror = subElement.asType();
                if (!TypeKind.DECLARED.equals(mirror.getKind())) {
                    continue;
                }
                boolean contains = mirror.accept(visitor, subElement);
                if (contains) {
                    return true;
                }
            }
            for (Element subElement : ElementFilter.methodsIn(entity.getTypeElement().getEnclosedElements())) {
                TypeMirror mirror = subElement.asType();
                if (!TypeKind.DECLARED.equals(mirror.getKind())) {
                    continue;
                }
                boolean contains = mirror.accept(visitor, subElement);
                if (contains) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isJPAEntity(Element element) {
        return TypeUtils.containsAnnotation(element, Constants.ENTITY, Constants.MAPPED_SUPERCLASS,
                Constants.EMBEDDABLE);
    }

    private void handleRootElementAnnotationMirrors(final Element element) {
        List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
        for (AnnotationMirror mirror : annotationMirrors) {
            if (!ElementKind.CLASS.equals(element.getKind())) {
                continue;
            }

            String fqn = ((TypeElement) element).getQualifiedName().toString();
            MetaEntity alreadyExistingMetaEntity = tryGettingExistingEntityFromContext(mirror, fqn);
            if (alreadyExistingMetaEntity != null && alreadyExistingMetaEntity.isMetaComplete()) {
                String msg = "Skipping processing of annotations for " + fqn
                        + " since xml configuration is metadata complete.";
                context.logMessage(Diagnostic.Kind.NOTE, msg);
                continue;
            }

            AnnotationMetaEntity metaEntity;
            if (TypeUtils.containsAnnotation(element, Constants.EMBEDDABLE)) {
                metaEntity = new AnnotationEmbeddable((TypeElement) element, context);
            } else {
                metaEntity = new AnnotationMetaEntity((TypeElement) element, context);
            }

            if (alreadyExistingMetaEntity != null) {
                metaEntity.mergeInMembers(alreadyExistingMetaEntity.getMembers());
            }
            addMetaEntityToContext(mirror, metaEntity);
        }
    }

    private MetaEntity tryGettingExistingEntityFromContext(AnnotationMirror mirror, String fqn) {
        MetaEntity alreadyExistingMetaEntity = null;
        if (TypeUtils.isAnnotationMirrorOfType(mirror, Constants.ENTITY)) {
            alreadyExistingMetaEntity = context.getMetaEntity(fqn);
        } else if (TypeUtils.isAnnotationMirrorOfType(mirror, Constants.MAPPED_SUPERCLASS)
                || TypeUtils.isAnnotationMirrorOfType(mirror, Constants.EMBEDDABLE)) {
            alreadyExistingMetaEntity = context.getMetaEmbeddable(fqn);
        }
        return alreadyExistingMetaEntity;
    }

    private void addMetaEntityToContext(AnnotationMirror mirror, AnnotationMetaEntity metaEntity) {
        if (TypeUtils.isAnnotationMirrorOfType(mirror, Constants.ENTITY)) {
            context.addMetaEntity(metaEntity.getQualifiedName(), metaEntity);
        } else if (TypeUtils.isAnnotationMirrorOfType(mirror, Constants.MAPPED_SUPERCLASS)) {
            context.addMetaEntity(metaEntity.getQualifiedName(), metaEntity);
        } else if (TypeUtils.isAnnotationMirrorOfType(mirror, Constants.EMBEDDABLE)) {
            context.addMetaEmbeddable(metaEntity.getQualifiedName(), metaEntity);
        }
    }

    class ContainsAttributeTypeVisitor extends SimpleTypeVisitor6<Boolean, Element> {

        private final Context context;
        private final TypeElement type;

        ContainsAttributeTypeVisitor(TypeElement elem, Context context) {
            this.context = context;
            this.type = elem;
        }

        @Override
        public Boolean visitDeclared(DeclaredType declaredType, Element element) {
            TypeElement returnedElement = (TypeElement) context.getTypeUtils().asElement(declaredType);

            String fqNameOfReturnType = returnedElement.getQualifiedName().toString();
            String collection = Constants.COLLECTIONS.get(fqNameOfReturnType);
            if (collection != null) {
                TypeMirror collectionElementType = TypeUtils.getCollectionElementType(declaredType, fqNameOfReturnType,
                        null, context);
                returnedElement = (TypeElement) context.getTypeUtils().asElement(collectionElementType);
            }

            if (type.getQualifiedName().toString().equals(returnedElement.getQualifiedName().toString())) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        }

        @Override
        public Boolean visitExecutable(ExecutableType t, Element element) {
            if (!element.getKind().equals(ElementKind.METHOD)) {
                return Boolean.FALSE;
            }

            String string = element.getSimpleName().toString();
            if (!StringUtil.isPropertyName(string)) {
                return Boolean.FALSE;
            }

            TypeMirror returnType = t.getReturnType();
            return returnType.accept(this, element);
        }
    }
}
