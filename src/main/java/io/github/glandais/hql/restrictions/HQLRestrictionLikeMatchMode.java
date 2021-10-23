package io.github.glandais.hql.restrictions;

public enum HQLRestrictionLikeMatchMode {

    EXACT("", ""),

    START("", "%"),

    END("%", ""),

    ANYWHERE("%", "%");

    private final String prefix;
    private final String suffix;

    HQLRestrictionLikeMatchMode(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String toHQL(String value) {
        return prefix + value + suffix;
    }

}
