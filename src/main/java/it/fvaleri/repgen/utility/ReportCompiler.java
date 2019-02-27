/*
 * Copyright 2018 Federico Valeri.
 * Licensed under the Apache License 2.0 (see LICENSE file).
 */
package it.fvaleri.repgen.utility;

import java.util.Locale;
import java.util.Map;

/** 
 * Report compiler API.
 */
public interface ReportCompiler {
    /**
     * Compile a new report from a pre-defined template.
     * 
     * @param type Report's type.
     * @param reps Map containing template's name and associated data.
     * @return Compiled report bytes.
     */
    public byte[] compile(ReportType type, Map<String, Object> reps);

    public enum ReportType {
        PDF("pdf"), XLS("xls"), DOC("doc"), RTF("rtf"), TXT("txt");
        private final String value;

        private ReportType(String value) {
            this.value = value;
        }

        public static ReportType getEnumByValue(String value) {
            for (ReportType e : ReportType.values()) {
                if (e.value.equalsIgnoreCase(value)) {
                    return e;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public interface Builder {
        Builder withLocale(Locale locale);

        Builder withTemplateHome(String home);

        Builder withInMemoryComp(boolean enable);

        ReportCompiler build();
    }
}
