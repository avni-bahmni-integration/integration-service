package org.bahmni_avni_integration.domain;

import java.util.ArrayList;
import java.util.List;

public class Constants {
    private final List<Constant> list = new ArrayList<>();;

    public Constants(Iterable<Constant> iterable) {
        iterable.forEach(list::add);
    }

    public String getValue(ConstantKey key) {
        Constant c = list.stream().filter(constant -> constant.getKey().equals(key)).findAny().orElse(null);
        if (c == null) return null;
        return c.getValue();
    }
}