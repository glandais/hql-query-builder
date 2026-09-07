package com.github.glandais.hql.paths;

import com.github.glandais.hql.HQLQueryBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HQLSafePathEntityTest {

    private static class TestEntityPath extends HQLSafePathEntity<Object> {
        TestEntityPath(String path, HQLQueryBuilder<?> queryBuilder) {
            super(path, queryBuilder);
        }

        @Override
        public Class<?> getEntityClass() {
            return Object.class;
        }
    }

    @Test
    void addFetchDelegatesToQueryBuilder() {
        HQLQueryBuilder<?> queryBuilder = mock(HQLQueryBuilder.class);
        TestEntityPath path = new TestEntityPath("this_.child", queryBuilder);

        path.addFetch();

        verify(queryBuilder).addFetch("this_.child");
    }

    @Test
    void getEntityClassIsImplementedBySubclass() {
        TestEntityPath path = new TestEntityPath("this_.child", mock(HQLQueryBuilder.class));

        assertThat(path.getEntityClass()).isEqualTo(Object.class);
    }
}
