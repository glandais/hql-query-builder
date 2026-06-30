package com.github.glandais.hql.generator.sample;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class SampleChild {

    @Id
    private Long id;

    private String label;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
