package io.github.glandais.hql.beans;

import io.github.glandais.hql.HQLRestriction;

import java.io.Serializable;

public class HQLStatisticItem implements Serializable {

    private String path;

    private HQLRestriction restriction;

    public HQLStatisticItem(String path, HQLRestriction restriction) {
        super();
        init(path, restriction);
    }

    public HQLStatisticItem(String path) {
        super();
        init(path, null);
    }

    private void init(String path, HQLRestriction restriction) {
        this.path = path;
        this.restriction = restriction;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public HQLRestriction getRestriction() {
        return restriction;
    }

    public void setRestriction(HQLRestriction restriction) {
        this.restriction = restriction;
    }

}
