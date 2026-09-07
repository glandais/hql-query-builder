package com.github.glandais.hql.restrictions;

import com.github.glandais.hql.HQLQueryBuilder;
import com.github.glandais.hql.NullValue;
import com.github.glandais.hql.beans.HQLRestrictionValues;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class HQLRestrictionsToHQLTest {

    private HQLQueryBuilder<?> queryBuilder;
    private HQLRestrictionValues values;
    private StringBuilder sb;

    @BeforeEach
    void setUp() {
        queryBuilder = mock(HQLQueryBuilder.class);
        lenient().when(queryBuilder.getShortProperty(anyString())).thenAnswer(invocation -> "this_." + invocation.getArgument(0));
        values = new HQLRestrictionValues();
        sb = new StringBuilder();
    }

    @Test
    void eqWithValueBindsParameter() {
        HQLRestrictions.eq("name", "bob").toHQL(queryBuilder, values, sb);

        assertThat(sb.toString()).isEqualTo("this_.name = :restriction_0");
        assertThat(values).containsEntry("restriction_0", "bob");
    }

    @Test
    void eqWithNullValueIsIsNull() {
        HQLRestrictions.eq("name", null).toHQL(queryBuilder, values, sb);

        assertThat(sb.toString()).isEqualTo("this_.name is null");
        assertThat(values).isEmpty();
    }

    @Test
    void eqWithNullValueMarkerIsIsNull() {
        HQLRestrictions.eq("name", NullValue.NULL_VALUE).toHQL(queryBuilder, values, sb);

        assertThat(sb.toString()).isEqualTo("this_.name is null");
        assertThat(values).isEmpty();
    }

    @Test
    void neqWithValueBindsParameter() {
        HQLRestrictions.neq("name", "bob").toHQL(queryBuilder, values, sb);

        assertThat(sb.toString()).isEqualTo("this_.name != :restriction_0");
        assertThat(values).containsEntry("restriction_0", "bob");
    }

    @Test
    void neqWithNullValueIsIsNotNull() {
        HQLRestrictions.neq("name", null).toHQL(queryBuilder, values, sb);

        assertThat(sb.toString()).isEqualTo("this_.name is not null");
    }

    @Test
    void inWithElementsBindsCollection() {
        HQLRestrictions.in("name", List.of("a", "b")).toHQL(queryBuilder, values, sb);

        assertThat(sb.toString()).isEqualTo("this_.name in (:restriction_0)");
        assertThat(values).containsEntry("restriction_0", List.of("a", "b"));
    }

    @Test
    void inWithEmptyCollectionIsIsNull() {
        HQLRestrictions.in("name", List.of()).toHQL(queryBuilder, values, sb);

        assertThat(sb.toString()).isEqualTo("this_.name is null");
    }

    @Test
    void inWithNullCollectionIsIsNull() {
        HQLRestrictions.in("name", null).toHQL(queryBuilder, values, sb);

        assertThat(sb.toString()).isEqualTo("this_.name is null");
    }

    @Test
    void ninWithElementsBindsCollection() {
        HQLRestrictions.nin("name", List.of("a", "b")).toHQL(queryBuilder, values, sb);

        assertThat(sb.toString()).isEqualTo("this_.name not in (:restriction_0)");
    }

    @Test
    void ninWithEmptyCollectionIsIsNotNull() {
        HQLRestrictions.nin("name", List.of()).toHQL(queryBuilder, values, sb);

        assertThat(sb.toString()).isEqualTo("this_.name is not null");
    }

    @Test
    void likeDefaultsToAnywhereAndLowerCases() {
        HQLRestrictions.like("name", "Bob").toHQL(queryBuilder, values, sb);

        assertThat(sb.toString()).isEqualTo("lower(this_.name) like :restriction_0");
        assertThat(values).containsEntry("restriction_0", "%bob%");
    }

    @Test
    void likeStartMatchMode() {
        HQLRestrictions.like("name", "Bob", HQLRestrictionLikeMatchMode.START).toHQL(queryBuilder, values, sb);

        assertThat(values).containsEntry("restriction_0", "bob%");
    }

    @Test
    void likeEndMatchMode() {
        HQLRestrictions.like("name", "Bob", HQLRestrictionLikeMatchMode.END).toHQL(queryBuilder, values, sb);

        assertThat(values).containsEntry("restriction_0", "%bob");
    }

    @Test
    void likeExactMatchMode() {
        HQLRestrictions.like("name", "Bob", HQLRestrictionLikeMatchMode.EXACT).toHQL(queryBuilder, values, sb);

        assertThat(values).containsEntry("restriction_0", "bob");
    }

    @Test
    void isNull() {
        HQLRestrictions.isNull("name").toHQL(queryBuilder, values, sb);

        assertThat(sb.toString()).isEqualTo("this_.name is null");
    }

    @Test
    void isNotNull() {
        HQLRestrictions.isNotNull("name").toHQL(queryBuilder, values, sb);

        assertThat(sb.toString()).isEqualTo("this_.name is not null");
    }

    @Test
    void isEmpty() {
        HQLRestrictions.isEmpty("items").toHQL(queryBuilder, values, sb);

        assertThat(sb.toString()).isEqualTo("this_.items is empty");
    }

    @Test
    void isNotEmpty() {
        HQLRestrictions.isNotEmpty("items").toHQL(queryBuilder, values, sb);

        assertThat(sb.toString()).isEqualTo("this_.items is not empty");
    }

    @Test
    void compareGe() {
        HQLRestrictions.ge("age", 18).toHQL(queryBuilder, values, sb);

        assertThat(sb.toString()).isEqualTo("this_.age >= :restriction_0");
    }

    @Test
    void compareLe() {
        HQLRestrictions.le("age", 18).toHQL(queryBuilder, values, sb);

        assertThat(sb.toString()).isEqualTo("this_.age <= :restriction_0");
    }

    @Test
    void compareGt() {
        HQLRestrictions.gt("age", 18).toHQL(queryBuilder, values, sb);

        assertThat(sb.toString()).isEqualTo("this_.age > :restriction_0");
    }

    @Test
    void compareLt() {
        HQLRestrictions.lt("age", 18).toHQL(queryBuilder, values, sb);

        assertThat(sb.toString()).isEqualTo("this_.age < :restriction_0");
    }

    @Test
    void compareWithNullValueProducesNoOutput() {
        HQLRestrictions.ge("age", null).toHQL(queryBuilder, values, sb);

        assertThat(sb.toString()).isEmpty();
    }

    @Test
    void memberOfWithNonStringValue() {
        new HQLRestrictionMemberOf(42, "items").toHQL(queryBuilder, values, sb);

        assertThat(sb.toString()).isEqualTo(" :restriction_0 MEMBER OF this_.items");
        assertThat(values).containsEntry("restriction_0", 42);
    }

    @Test
    void memberOfWithStringValueUsesElements() {
        new HQLRestrictionMemberOf("x", "items").toHQL(queryBuilder, values, sb);

        assertThat(sb.toString()).isEqualTo(" :restriction_0 IN elements(this_.items)");
    }

    @Test
    void memberOfWithNullValueProducesNoOutput() {
        new HQLRestrictionMemberOf(null, "items").toHQL(queryBuilder, values, sb);

        assertThat(sb.toString()).isEmpty();
    }

    @Test
    void pathEqual() {
        new HQLRestrictionPathEqual("a", "b").toHQL(queryBuilder, values, sb);

        assertThat(sb.toString()).isEqualTo("this_.a = this_.b");
    }
}
