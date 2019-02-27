/*
 * Copyright Federico Valeri.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package it.fvaleri.repgen.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import it.fvaleri.repgen.domain.Report;

public class CommonHelperTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void normalize() {
        String text = "  hello world      ";
        assertEquals("hello world", CommonHelper.normalize(text));
    }

    @Test
    public void shorten() {
        String text = "hello world";
        assertEquals("hello", CommonHelper.shorten(text, 5));
        assertEquals("hello world", CommonHelper.shorten(text, 15));
    }

    @Test
    public void sortReports() {
        List<Report> reports = new ArrayList<>(Arrays.asList(new Report(2L), new Report(0L), new Report(1L)));
        reports = CommonHelper.sortReports(reports);
        assertEquals(0L, reports.get(0).getId().longValue());
        assertEquals(1, reports.get(1).getId().longValue());
        assertEquals(2, reports.get(2).getId().longValue());
    }

    @Test
    public void sha256() throws Exception {
        String hash = CommonHelper.sha256("test1".getBytes());
        assertEquals("1b4f0e9851971998e732078544c96b36c3d01cedf7caa332359d6f1d83567014", hash);
    }

    @Test
    public void parseDate() {
        LocalDateTime time = LocalDateTime.of(2016, Month.MAY, 12, 00, 00, 00);
        Date expected = Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
        Date actual = CommonHelper.parseDate("2016-05-12");
        assertEquals(expected, actual);
    }

    @Test
    public void formatDate() {
        LocalDateTime time = LocalDateTime.of(2016, Month.MAY, 12, 16, 40, 00);
        Date date = Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
        String actual = CommonHelper.formatDate(date);
        assertEquals("2016-05-12", actual);
    }

    @Test
    public void bytesToFile() throws Exception {
        String content = "this is a test";
        String path = tmp.getRoot() + "/test.txt";
        CommonHelper.bytesToFile(content.getBytes(), path);
        assertEquals(14, new File(path).length());
    }

    @Test
    public void streamToBytes() throws Exception {
        String content = "this is a test";
        InputStream stream = new ByteArrayInputStream(content.getBytes());
        byte[] result = CommonHelper.streamToBytes(stream);
        assertEquals(14, result.length);
    }

    @Test
    public void fileToString() throws Exception {
        String str0 = CommonHelper.fileToString("simple-letter.json");
        assertTrue(str0.contains("texts"));
        String str1 = CommonHelper.fileToString(null);
        assertNull(str1);
    }
}
