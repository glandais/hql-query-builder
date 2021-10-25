package com.github.glandais.hql.beans;

public class HQLAlias {

    public static int LEFT_JOIN = 0;
    public static int INNER_JOIN = 1;

    private final String path;
    private final String alias;
    // Criteria.LEFT_JOIN or Criteria.INNER_JOIN
    private final int joinType;

    public HQLAlias(String path, String alias, int joinType) {
        super();
        this.path = path;
        this.alias = alias;
        this.joinType = joinType;
    }

    public String getPath() {
        return path;
    }

    public String getAlias() {
        return alias;
    }

    public int getJoinType() {
        return joinType;
    }

}
