package com.github.glandais.hql.paths;

import com.github.glandais.hql.HQLQueryBuilder;
import com.github.glandais.hql.restrictions.HQLRestrictionLike;
import com.github.glandais.hql.restrictions.HQLRestrictionLikeMatchMode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HQLSafePathBasicStringTest {

    @Test
    void likeWithDefaultMatchModeAddsRestriction() {
        HQLQueryBuilder<?> queryBuilder = mock(HQLQueryBuilder.class);
        HQLSafePathBasicString<String> path = new HQLSafePathBasicString<>(null, "this_.name", queryBuilder, String.class);

        path.like("bob");

        verify(queryBuilder).addRestriction(any(HQLRestrictionLike.class));
    }

    @Test
    void likeWithExplicitMatchModeAddsRestriction() {
        HQLQueryBuilder<?> queryBuilder = mock(HQLQueryBuilder.class);
        HQLSafePathBasicString<String> path = new HQLSafePathBasicString<>(null, "this_.name", queryBuilder, String.class);

        path.like("bob", HQLRestrictionLikeMatchMode.START);

        verify(queryBuilder).addRestriction(any(HQLRestrictionLike.class));
    }

    @Test
    void likeRestrictionFactoryDoesNotMutateState() {
        HQLQueryBuilder<?> queryBuilder = mock(HQLQueryBuilder.class);
        HQLSafePathBasicString<String> path = new HQLSafePathBasicString<>(null, "this_.name", queryBuilder, String.class);

        assertThat(path.likeRestriction("bob")).isInstanceOf(HQLRestrictionLike.class);
        assertThat(path.likeRestriction("bob", HQLRestrictionLikeMatchMode.EXACT)).isInstanceOf(HQLRestrictionLike.class);
    }
}
