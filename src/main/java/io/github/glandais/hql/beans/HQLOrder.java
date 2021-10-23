package io.github.glandais.hql.beans;

public class HQLOrder {

    private final String path;
    private final boolean ascending;
    private final boolean lowerCase;

    public HQLOrder(String path, boolean ascending) {
        super();
        this.path = path;
        this.ascending = ascending;
        this.lowerCase = false;
    }

    public HQLOrder(String path, boolean ascending, boolean lowerCase) {
        super();
        this.path = path;
        this.ascending = ascending;
        this.lowerCase = lowerCase;
    }

    public String getPath() {
        if (lowerCase) {
            return "lower(" + path + ")";
        } else {
            return path;
        }
    }

    public boolean isAscending() {
        return ascending;
    }

    public boolean isLowerCase() {
        return lowerCase;
    }

}
