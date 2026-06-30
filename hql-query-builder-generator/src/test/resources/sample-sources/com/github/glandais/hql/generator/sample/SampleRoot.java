package com.github.glandais.hql.generator.sample;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class SampleRoot {

    @Id
    private Long id;

    private String name;

    @ManyToOne
    private SampleChild child;

    @OneToMany
    private List<SampleChild> children;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SampleChild getChild() {
        return child;
    }

    public void setChild(SampleChild child) {
        this.child = child;
    }

    public List<SampleChild> getChildren() {
        return children;
    }

    public void setChildren(List<SampleChild> children) {
        this.children = children;
    }
}
