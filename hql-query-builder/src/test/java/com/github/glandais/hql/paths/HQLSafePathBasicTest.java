package com.github.glandais.hql.paths;

import com.github.glandais.hql.HQLQueryBuilder;
import com.github.glandais.hql.HQLRestriction;
import com.github.glandais.hql.restrictions.HQLRestrictionEq;
import com.github.glandais.hql.restrictions.HQLRestrictionIn;
import com.github.glandais.hql.restrictions.HQLRestrictionIsNotNull;
import com.github.glandais.hql.restrictions.HQLRestrictionIsNull;
import com.github.glandais.hql.restrictions.HQLRestrictionNeq;
import com.github.glandais.hql.restrictions.HQLRestrictionNotIn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HQLSafePathBasicTest {

    private HQLQueryBuilder<?> queryBuilder;
    private HQLSafePathBasic<String> path;

    @BeforeEach
    void setUp() {
        queryBuilder = mock(HQLQueryBuilder.class);
        path = new HQLSafePathBasic<>(null, "this_.name", queryBuilder, String.class);
    }

    @Test
    void getTypeReturnsTheDeclaredType() {
        assertThat(path.getType()).isEqualTo(String.class);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void getListDistinctDelegatesToQueryBuilder() {
        when(queryBuilder.getListDistinct("this_.name")).thenReturn((List) List.of("a"));

        assertThat(path.getListDistinct()).containsExactly("a");
    }

    @Test
    void orderAscendingDelegatesToQueryBuilder() {
        path.order(true);

        verify(queryBuilder).addOrder("this_.name", true);
    }

    @Test
    void orderWithLowerCaseDelegatesToQueryBuilder() {
        path.order(false, true);

        verify(queryBuilder).addOrder("this_.name", false, true);
    }

    @Test
    void eqAddsEqRestriction() {
        path.eq("bob");

        ArgumentCaptor<HQLRestriction> captor = ArgumentCaptor.forClass(HQLRestriction.class);
        verify(queryBuilder).addRestriction(captor.capture());
        assertThat(captor.getValue()).isInstanceOf(HQLRestrictionEq.class);
    }

    @Test
    void eqIfValueNotNullSkipsWhenNull() {
        path.eqIfValueNotNull(null);

        verify(queryBuilder, never()).addRestriction(any());
    }

    @Test
    void eqIfValueNotNullAddsRestrictionWhenPresent() {
        path.eqIfValueNotNull("bob");

        verify(queryBuilder).addRestriction(any(HQLRestrictionEq.class));
    }

    @Test
    void neqAddsNeqRestriction() {
        path.neq("bob");

        verify(queryBuilder).addRestriction(any(HQLRestrictionNeq.class));
    }

    @Test
    void inAddsInRestriction() {
        path.in(List.of("a"));

        verify(queryBuilder).addRestriction(any(HQLRestrictionIn.class));
    }

    @Test
    void ninAddsNotInRestriction() {
        path.nin(List.of("a"));

        verify(queryBuilder).addRestriction(any(HQLRestrictionNotIn.class));
    }

    @Test
    void isNotNullAddsIsNotNullRestriction() {
        path.isNotNull();

        verify(queryBuilder).addRestriction(any(HQLRestrictionIsNotNull.class));
    }

    @Test
    void isNullAddsIsNullRestriction() {
        path.isNull();

        verify(queryBuilder).addRestriction(any(HQLRestrictionIsNull.class));
    }

    @Test
    void getCountOnPathDelegatesToQueryBuilder() {
        when(queryBuilder.getCountOnPath("this_.name")).thenReturn(5);

        assertThat(path.getCountOnPath()).isEqualTo(5);
    }

    @Test
    void getCountDistinctOnPathDelegatesToQueryBuilder() {
        when(queryBuilder.getCountDistinctOnPath("this_.name")).thenReturn(3);

        assertThat(path.getCountDistinctOnPath()).isEqualTo(3);
    }
}
