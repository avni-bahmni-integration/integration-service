package org.avni_integration_service.integration_data.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Constants {
    private final List<Constant> list = new ArrayList<>();

    public Constants(Iterable<Constant> iterable) {
        iterable.forEach(list::add);
    }

    public String getValue(ConstantKey key) {
        Constant c = list.stream().filter(constant -> constant.getKey().equals(key)).findAny().orElse(null);
        if (c == null) return null;
        return c.getValue();
    }

    public List<Constant> getValues(ConstantKey key) {
        return list.stream().filter(constant -> constant.getKey().equals(key)).collect(Collectors.toList());
    }
}
