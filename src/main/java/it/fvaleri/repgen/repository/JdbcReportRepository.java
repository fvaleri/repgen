/*
 * Copyright 2018 Federico Valeri.
 * Licensed under the Apache License 2.0 (see LICENSE file).
 */
package it.fvaleri.repgen.repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import it.fvaleri.repgen.domain.Data;
import it.fvaleri.repgen.domain.Report;
import it.fvaleri.repgen.domain.Request;
import it.fvaleri.repgen.domain.Search;
import it.fvaleri.repgen.domain.Text;
import it.fvaleri.repgen.repository.mapper.ReportMapper;
import it.fvaleri.repgen.repository.mapper.CellMapper;
import it.fvaleri.repgen.repository.mapper.TextMapper;

@Repository("jdbc")
public class JdbcReportRepository implements ReportRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<String> loadActiveModels() {
        List<String> result = jdbcTemplate.queryForList(
                "select m.rmd_name from rg_models m where (m.rmd_end is null or m.rmd_end > sysdate)", String.class);
        return result;
    }

    @Override
    public Long savePdfRequest(Request request) {
        Long modelId = checkPdfModel(request);
        String refId = request.getDataset().get(0).getRefId();
        Long reportId = saveReport(modelId, refId);
        for (Text text : request.getDataset().get(0).getTexts()) {
            Long textTypeId = findTextType(text.getName());
            saveText(reportId, textTypeId, text.getValue());
        }
        return reportId;
    }

    @Override
    public Long saveXlsRequest(Request request) {
        Long modelId = checkXlsModel(request);
        String refId = request.getDataset().get(0).getRefId();
        Long reportId = saveReport(modelId, refId);
        Long tableTypeId = 1L;
        for (Data row : request.getDataset()) {
            saveRow(reportId, tableTypeId, row.getCells());
        }
        return reportId;
    }

    private Long checkPdfModel(Request request) {
        try {
            Long modelId = jdbcTemplate.queryForObject(
                    "select rmd_id from (select m.rmd_id, rmd_name, "
                            + "(select count(1) from rg_text_types where rtt_rmd_id = m.rmd_id) as max_texts_size "
                            + "from rg_models m where (m.rmd_end is null or m.rmd_end > sysdate) "
                            + ") where rmd_name = ? and max_texts_size >= ?",
                    new Object[] {request.getModel().getName(), request.getTextsSize()}, Long.class);
            return modelId;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private Long checkXlsModel(Request request) {
        try {
            Long modelId = jdbcTemplate.queryForObject(
                    "select rmd_id from (select m.rmd_id, m.rmd_name, 30 as max_row_size "
                            + "from rg_models m where (m.rmd_end is null or m.rmd_end > sysdate) "
                            + ") where rmd_name = ? and max_row_size >= ?",
                    new Object[] {request.getModel().getName(), request.getMaxRowSize()}, Long.class);
            return modelId;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private Long saveReport(Long modelId, String refId) {
        Long id = nextValReports();
        jdbcTemplate.update("insert into rg_reports (rre_id, rre_rmd_id, rre_ref_id) values (?, ?, ?)", id, modelId,
                refId);
        return id;
    }

    private void saveText(Long reportId, Long textTypeId, String value) {
        jdbcTemplate.update("insert into rg_texts (rte_id, rte_rre_id, rte_rtt_id, rte_value) values (?, ?, ?, ?)",
                nextValTexts(), reportId, textTypeId, value);
    }

    private void saveRow(Long reportId, Long tableTypeId, List<String> values) {
        Object[] params = new Object[33];
        params[0] = nextValTables();
        params[1] = reportId;
        params[2] = tableTypeId;
        int i = 3;
        for (String value : values) {
            params[i++] = value;
        }
        jdbcTemplate.update("insert into rg_tables (rtb_id, rtb_rre_id, rtb_rtp_id, rtb_cell0, rtb_cell1, rtb_cell2, "
                + "rtb_cell3, rtb_cell4, rtb_cell5, rtb_cell6, rtb_cell7, rtb_cell8, rtb_cell9, rtb_cell10, rtb_cell11, "
                + "rtb_cell12, rtb_cell13, rtb_cell14, rtb_cell15, rtb_cell16, rtb_cell17, rtb_cell18, rtb_cell19, "
                + "rtb_cell20, rtb_cell21, rtb_cell22, rtb_cell23, rtb_cell24, rtb_cell25, rtb_cell26, rtb_cell27, "
                + "rtb_cell28, rtb_cell29) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                + " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", params);
    }

    private Long findTextType(String name) {
        try {
            return jdbcTemplate.queryForObject("select rtt_id from rg_text_types where rtt_name = ?",
                    new Object[] {name}, Long.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private Long nextValReports() {
        try {
            return jdbcTemplate.queryForObject("select rg_reports_seq.nextval from dual", Long.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private Long nextValTexts() {
        try {
            return jdbcTemplate.queryForObject("select rg_texts_seq.nextval from dual", Long.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private Object nextValTables() {
        try {
            return jdbcTemplate.queryForObject("select rg_tables_seq.nextval from dual", Long.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Report> findNextRequests(Integer num) {
        return findRequests("%", num);
    }

    private List<Report> findRequests(String model, Integer num) {
        List<Report> result = jdbcTemplate.query(
                "select * from (select r.rre_id, r.rre_ref_id, r.rre_start, r.rre_end, r.rre_filename, "
                        + "null as rre_binary, r.rre_error, m.rmd_id, m.rmd_name, m.rmd_format from rg_reports r  "
                        + "join rg_models m on r.rre_rmd_id = m.rmd_id where r.rre_binary is null  "
                        + "and (r.rre_error is null or substr(r.rre_error,0,1) in ('0','1','2')) and m.rmd_name like ? "
                        + "and (m.rmd_end is null or m.rmd_end > sysdate) order by r.rre_id asc) where rownum <= ?",
                new Object[] {model, num}, new ReportMapper());
        return result;
    }

    @Override
    public List<Report> findReports(Search search) {
        ArrayList<Object> params = new ArrayList<Object>();
        String query = "select * from (select r.rre_id, r.rre_ref_id, r.rre_start, r.rre_end, r.rre_filename, "
                + "null as rre_binary, r.rre_error, m.rmd_id, m.rmd_name, m.rmd_format "
                + "from rg_reports r join rg_models m on r.rre_rmd_id = m.rmd_id "
                + "where (r.rre_binary is not null or r.rre_error is not null) ";
        if (search.getId() != null) {
            query += "and r.rre_id = ?) ";
            params.add(search.getId());
        } else {
            if (search.getName() != null) {
                query += "and r.rre_filename like ? ";
                params.add("%" + search.getName() + "%");
            }
            if (search.getFrom() != null) {
                query += "and r.rre_start >= ? ";
                params.add(search.getFrom());
            }
            if (search.getTo() != null) {
                query += "and r.rre_start <= ? ";
                params.add(search.getTo());
            }
            // server side pagination
            if (search.getDirection().equals("next")) {
                query += "and r.rre_id > ? order by r.rre_id asc) where rownum <= ? ";
            }
            if (search.getDirection().equals("prev")) {
                query += "and r.rre_id < ? order by r.rre_id desc) where rownum <= ? ";
            }
            params.add(search.getLastSeen());
            params.add(search.getPageSize());
        }
        if (search.isContAttached()) {
            query = query.replaceAll("null as rre_binary,", "r.rre_binary,");
        }
        List<Report> result = jdbcTemplate.query(query, params.toArray(), new ReportMapper());
        return result;
    }

    @Override
    public void buildStart(Report report) {
        jdbcTemplate.update("update rg_reports set rre_start = ? where rre_id = ?",
                new Timestamp(report.getStart().getTime()), report.getId());
    }

    @Override
    public void buildSuccess(Report report) {
        jdbcTemplate.update(
                "update rg_reports set rre_end = ?, rre_binary = ?, "
                        + "rre_filename = ?, rre_error = null where rre_id = ?",
                new Timestamp(report.getEnd().getTime()), report.getContent(), report.getFilename(), report.getId());
    }

    @Override
    public void buildError(Report report) {
        jdbcTemplate.update("update rg_reports set rre_end = ?, rre_error = ? where rre_id = ?",
                new Timestamp(report.getEnd().getTime()), report.getShortMessage(), report.getId());
    }

    @Override
    public List<Data> loadPdfDataset(Report report) {
        Data result = new Data();
        List<Text> texts = jdbcTemplate.query(
                "select p.rtt_name, t.rte_value from rg_reports r join rg_texts t on t.rte_rre_id = r.rre_id "
                        + "join rg_text_types p on t.rte_rtt_id = p.rtt_id where r.rre_id = ?",
                new Object[] {report.getId()}, new TextMapper());
        if (!texts.isEmpty()) {
            result.setTexts(texts);
        }
        return Arrays.asList(result);
    }

    @Override
    public List<Data> loadXlsDataset(Report report) {
        List<Data> result = jdbcTemplate.query(
                "select b.rtb_cell0, b.rtb_cell1, b.rtb_cell2, b.rtb_cell3, b.rtb_cell4, b.rtb_cell5, "
                        + "b.rtb_cell6, b.rtb_cell7, b.rtb_cell8, b.rtb_cell9, b.rtb_cell10, b.rtb_cell11, "
                        + "b.rtb_cell12, b.rtb_cell13, b.rtb_cell14, b.rtb_cell15, b.rtb_cell16, b.rtb_cell17, "
                        + "b.rtb_cell18, b.rtb_cell19, b.rtb_cell20, b.rtb_cell21, b.rtb_cell22, b.rtb_cell23, "
                        + "b.rtb_cell24, b.rtb_cell25, b.rtb_cell26, b.rtb_cell27, b.rtb_cell28, b.rtb_cell29  "
                        + "from rg_reports r join rg_tables b on b.rtb_rre_id = r.rre_id "
                        + "join rg_table_types t on b.rtb_rtp_id = t.rtp_id where r.rre_id = ?",
                new Object[] {report.getId()}, new CellMapper());
        return result;
    }
}
