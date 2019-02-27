/*
 * Copyright Federico Valeri.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package it.fvaleri.repgen;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
public class Application {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    @Autowired
    private Config config;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void init() {
        LOG.info("Config {}", config.toString());
        LOG.info("Starting Repgen server");
    }

    @Configuration
    @ConfigurationProperties(prefix = "repgen")
    public static class Config {

        private String locale;
        private String accessToken;
        private String templateHome;
        private boolean inMemory;

        public Config() {
        }

        public Config(String locale, String accessToken, String templateHome, boolean inMemory) {
            this.locale = locale;
            this.accessToken = accessToken;
            this.templateHome = templateHome;
            this.inMemory = inMemory;
        }

        public String getLocale() {
            return this.locale;
        }

        public void setLocale(String locale) {
            this.locale = locale;
        }

        public String getAccessToken() {
            return this.accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getTemplateHome() {
            return this.templateHome;
        }

        public void setTemplateHome(String templateHome) {
            this.templateHome = templateHome;
        }

        public boolean isInMemory() {
            return this.inMemory;
        }

        public boolean getInMemory() {
            return this.inMemory;
        }

        public void setInMemory(boolean inMemory) {
            this.inMemory = inMemory;
        }

        @Override
        public String toString() {
            return "{" + " locale='" + getLocale() + "'" + ", accessToken='" + getAccessToken() + "'"
                    + ", templateHome='" + getTemplateHome() + "'" + ", inMemory='" + isInMemory() + "'" + "}";
        }
    }
}
