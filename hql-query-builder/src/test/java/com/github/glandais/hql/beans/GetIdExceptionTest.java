package com.github.glandais.hql.beans;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GetIdExceptionTest {

    @Test
    void isAStandardCheckedException() {
        assertThat(new GetIdException()).isInstanceOf(Exception.class);
    }
}
