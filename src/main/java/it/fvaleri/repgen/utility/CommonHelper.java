/*
 * Copyright Federico Valeri.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package it.fvaleri.repgen.utility;

import static java.util.stream.Collectors.toList;
import static java.util.Comparator.comparing;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import it.fvaleri.repgen.domain.Report;

public class CommonHelper {
    private CommonHelper() {
        throw new AssertionError("Utility class can't be instantiated or extended");
    }

    public static String normalize(String value) {
        if (value == null || value.trim().isEmpty()) {
            return value;
        }
        return value.trim();
    }

    public static String shorten(String msg, int max) {
        if (msg != null && msg.length() > max) {
            return msg.substring(0, max);
        } else {
            return msg;
        }
    }

    public static List<Report> sortReports(List<Report> reports) {
        if (reports != null && !reports.isEmpty()) {
            return reports.parallelStream().sorted(comparing(Report::getId)).collect(toList());
        }
        return reports;
    }

    public static String sha256(byte[] data) throws Exception {
        if (data == null) {
            return null;
        }
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data);
        StringBuffer hexSB = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexSB.append('0');
            }
            hexSB.append(hex);
        }
        return hexSB.toString();
    }

    public static Date parseDate(String value) {
        if (value != null && !value.isEmpty()) {
            LocalDate localDate = LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
            Date date = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            return date;
        }
        return null;
    }

    public static String formatDate(Date date) {
        if (date != null) {
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String value = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
            return value;
        }
        return null;
    }

    public static void bytesToFile(byte[] content, String path) throws Exception {
        if (content == null || path == null || path.trim().isEmpty()) {
            return;
        }
        FileOutputStream fos = new FileOutputStream(new File(path));
        fos.write(content);
        fos.close();
    }

    public static byte[] streamToBytes(InputStream is) throws Exception {
        if (is == null) {
            return null;
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    public static String fileToString(String path) throws Exception {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }
        String filePath = CommonHelper.class.getClassLoader().getResource(path).getFile();
        InputStream is = new FileInputStream(filePath);
        BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
        String line = buffer.readLine();
        StringBuilder sb = new StringBuilder();
        while (line != null) {
            sb.append(line).append("\n");
            line = buffer.readLine();
        }
        buffer.close();
        return sb.toString();
    }
}
