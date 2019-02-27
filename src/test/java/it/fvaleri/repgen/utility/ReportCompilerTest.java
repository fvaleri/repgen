/*
 * Copyright Federico Valeri.
 * License: Apache License 2.0 (see the file LICENSE).
 */
package it.fvaleri.repgen.utility;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import it.fvaleri.repgen.domain.Data;
import it.fvaleri.repgen.domain.Text;
import it.fvaleri.repgen.utility.ReportCompiler.ReportType;

public class ReportCompilerTest {
    private static ReportCompiler cut;

    @Before
    public void setUp() {
        cut = new JasperReportCompiler.Builder()
            .withLocale(Locale.ITALY)
            .build();
    }

    @Test
    public void testPDF() throws Exception {
        List<Data> dataList = new ArrayList<>();
        JSONObject obj = new JSONObject(CommonUtils.fileToString("simple-letter.json"));
        JSONArray arr0 = obj.getJSONArray("dataset");
        for (int i = 0; i < arr0.length(); i++) {
            Data data = new Data();
            dataList.add(data);
            data.setRefId(arr0.getJSONObject(i).getString("refId"));
            List<Text> texts = new ArrayList<>();
            data.setTexts(texts);
            JSONArray arr1 = arr0.getJSONObject(i).getJSONArray("texts");
            for (int j = 0; j < arr1.length(); j++) {
                texts.add(new Text(arr1.getJSONObject(j).getString("name"), arr1.getJSONObject(j).optString("value")));
            }
        }

        Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
        dataMap.put("simple-letter", dataList);
        byte[] report = cut.compile(ReportType.PDF, dataMap);

        //CommonHelper.bytesToFile(report, "/tmp/test.pdf");
        assertNotNull(report);
    }

    @Test
    public void testXLS() throws Exception {
        List<Data> dataList = new ArrayList<>();
        JSONObject obj = new JSONObject(CommonUtils.fileToString("simple-table.json"));
        JSONArray arr0 = obj.getJSONArray("dataset");
        for (int i = 0; i < arr0.length(); i++) {
            Data data = new Data();
            dataList.add(data);
            data.setRefId(arr0.getJSONObject(i).getString("refId"));
            List<String> cells = new ArrayList<>();
            data.setCells(cells);
            JSONArray arr1 = arr0.getJSONObject(i).getJSONArray("cells");
            for (int j = 0; j < arr1.length(); j++) {
                cells.add(arr1.optString(j));
            }
        }

        Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
        dataMap.put("simple-table", dataList);
        byte[] report = cut.compile(ReportType.XLS, dataMap);

        //CommonHelper.bytesToFile(report, "/tmp/test.xls");
        assertNotNull(report);
    }
}
