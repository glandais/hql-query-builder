package com.github.glandais.hql.testsupport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.reflect.Field;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(StaticCacheResetExtension.class)
class StaticCacheResetExtensionTest {

    @Test
    void clearsHqlQueryBuilderStaticCache() throws Exception {
        Class<?> builderClass = Class.forName("com.github.glandais.hql.HQLQueryBuilder");
        Field cacheField = builderClass.getDeclaredField("cache");
        cacheField.setAccessible(true);
        Map cache = (Map) cacheField.get(null);
        cache.put("pollution", new Object());

        new StaticCacheResetExtension().beforeEach(null);

        assertThat(cache).isEmpty();
    }

    @Test
    void resetsHqlQueryBuilderCacheStaticMetamodel() throws Exception {
        Class<?> cacheClass = Class.forName("com.github.glandais.hql.HQLQueryBuilderCache");
        Field metamodelField = cacheClass.getDeclaredField("metamodel");
        metamodelField.setAccessible(true);
        metamodelField.set(null, Map.of());

        new StaticCacheResetExtension().beforeEach(null);

        assertThat(metamodelField.get(null)).isNull();
    }
}
