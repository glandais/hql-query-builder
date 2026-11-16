package com.github.glandais.hql.generator.sample;

import jakarta.persistence.Entity;

@Entity
public class SampleChild extends SampleBase {

    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
