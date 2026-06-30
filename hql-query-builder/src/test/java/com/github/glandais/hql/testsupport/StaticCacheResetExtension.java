package com.github.glandais.hql.testsupport;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Field;
import java.util.Map;

public class StaticCacheResetExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        Class<?> builderClass = Class.forName("com.github.glandais.hql.HQLQueryBuilder");
        Field cacheField = builderClass.getDeclaredField("cache");
        cacheField.setAccessible(true);
        ((Map<?, ?>) cacheField.get(null)).clear();

        Class<?> cacheClass = Class.forName("com.github.glandais.hql.HQLQueryBuilderCache");
        Field metamodelField = cacheClass.getDeclaredField("metamodel");
        metamodelField.setAccessible(true);
        metamodelField.set(null, null);
    }
}
