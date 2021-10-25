package com.github.glandais.hql;

import java.io.Serializable;

public class NullValue implements Serializable {

    public static final NullValue NULL_VALUE = new NullValue();
    public static final String NULL_VALUE_STRING = "NullValue";
    private static final long serialVersionUID = 5057779027617620473L;

    private NullValue() {
        super();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        return getClass() == obj.getClass();
    }

}
