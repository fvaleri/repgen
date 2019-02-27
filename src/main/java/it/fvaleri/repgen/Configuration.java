/*
 * Copyright 2018 Federico Valeri.
 * Licensed under the Apache License 2.0 (see LICENSE file).
 */
package it.fvaleri.repgen;

import org.springframework.boot.context.properties.ConfigurationProperties;

@org.springframework.context.annotation.Configuration
@ConfigurationProperties(prefix = "repgen")
public class Configuration {
    private String locale;
    private String templateHome;
    private boolean inMemoryComp;

    public Configuration() {
    }

    public Configuration(String locale, String templateHome, boolean inMemoryComp) {
        this.locale = locale;
        this.templateHome = templateHome;
        this.inMemoryComp = inMemoryComp;
    }

    public String getLocale() {
        return this.locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getTemplateHome() {
        return this.templateHome;
    }

    public void setTemplateHome(String templateHome) {
        this.templateHome = templateHome;
    }

    public boolean isInMemoryComp() {
        return this.inMemoryComp;
    }

    public void setInMemory(boolean inMemoryComp) {
        this.inMemoryComp = inMemoryComp;
    }

    @Override
    public String toString() {
        return "Config{" +
                "locale='" + locale + '\'' +
                ", templateHome='" + templateHome + '\'' +
                ", inMemoryComp=" + inMemoryComp +
                '}';
    }
}
