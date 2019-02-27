/*
 * Copyright Federico Valeri.
 * License: Apache License 2.0 (see the file LICENSE).
 */
package it.fvaleri.repgen.domain;

import it.fvaleri.repgen.utility.CommonUtils;

public class Text {
    private String name;
    private String value;

    public Text() {
    }

    public Text(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public String getValue(int max) {
        return CommonUtils.shorten(value, max);
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "{" + " name='" + getName() + "'" + ", value='" + getValue() + "'" + "}";
    }
}
