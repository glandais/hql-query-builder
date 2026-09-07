package com.github.glandais.hql.generator.sample;

import jakarta.persistence.Embeddable;

@Embeddable
public class SampleAddress {

    private String city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
