/*
 * Copyright Federico Valeri.
 * License: Apache License 2.0 (see the file LICENSE).
 */
package it.fvaleri.repgen.controller;

import java.nio.charset.StandardCharsets;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import it.fvaleri.repgen.Application;
import it.fvaleri.repgen.utility.CommonUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class ReportControllerIT {
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void pdfRequest() throws Exception {
        MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);
        String content = CommonUtils.fileToString("simple-letter.json");

        mockMvc.perform(post("/api/reports")
                .contentType(contentType)
                .content(content))
            .andExpect(status().isOk())
            .andExpect(content().contentType(contentType));
    }

    @Test
    public void xlsRequest() throws Exception {
        MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);
        String content = CommonUtils.fileToString("simple-table.json");

        mockMvc.perform(post("/api/reports")
                .contentType(contentType)
                .content(content))
            .andExpect(status().isOk())
            .andExpect(content().contentType(contentType));
    }

    @Test
    public void badRequest() throws Exception {
        String content = "{\"invalid\":\"request\"}";
        MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

        mockMvc.perform(post("/api/reports")
                .contentType(contentType)
                .content(content))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void reportById() throws Exception {
        MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

        mockMvc.perform(get("/api/reports/111111")
                .contentType(contentType))
            .andExpect(status().isNotFound());
    }

    @Test
    public void searchReport() throws Exception {
        MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);
        String content = CommonUtils.fileToString("report-search.json");

        mockMvc.perform(post("/api/reports/search")
                .contentType(contentType)
                .content(content))
            .andExpect(status().isOk())
            .andExpect(content().contentType(contentType));
    }
}
