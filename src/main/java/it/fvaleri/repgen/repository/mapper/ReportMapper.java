/*
 * Copyright Federico Valeri.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package it.fvaleri.repgen.repository.mapper;

import static it.fvaleri.repgen.utility.CommonHelper.streamToBytes;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import it.fvaleri.repgen.domain.Model;
import it.fvaleri.repgen.domain.Report;
import it.fvaleri.repgen.utility.ReportCompiler.ReportType;

public class ReportMapper implements RowMapper<Report> {
    @Override
    public Report mapRow(ResultSet rs, int rowNum) throws SQLException {
        Report report = new Report();
        report.setId(rs.getLong("rre_id"));

        Model model = new Model();
        report.setModel(model);
        model.setId(rs.getLong("rmd_id"));
        model.setName(rs.getString("rmd_name"));
        ReportType modelFormat = ReportType.getEnumByValue(rs.getString("rmd_format"));
        model.setReportType(modelFormat != null ? modelFormat : ReportType.TXT);

        if (rs.getObject("rre_ref_id") != null) {
            report.setRefId(rs.getString("rre_ref_id"));
        }
        if (rs.getObject("rre_start") != null) {
            report.setStart(new Date(rs.getTimestamp("rre_start").getTime()));
        }
        if (rs.getObject("rre_end") != null) {
            report.setEnd(new Date(rs.getTimestamp("rre_end").getTime()));
        }
        if (rs.getObject("rre_binary") != null) {
            InputStream is = rs.getBinaryStream("rre_binary");
            try {
                report.setContent(streamToBytes(is));
            } catch (Exception e) {
            }
        }
        if (rs.getObject("rre_error") != null) {
            report.setMessage(rs.getString("rre_error"));
        }
        if (rs.getObject("rre_filename") != null) {
            report.setFilename(rs.getString("rre_filename"));
        }
        return report;
    }
}
