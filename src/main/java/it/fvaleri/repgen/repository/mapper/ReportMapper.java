/*
 * Copyright Federico Valeri.
 * License: Apache License 2.0 (see the file LICENSE).
 */
package it.fvaleri.repgen.repository.mapper;

import static it.fvaleri.repgen.utility.CommonUtils.streamToBytes;

import java.io.IOException;
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
        Model model = new Model(
                rs.getLong("rmd_id"),
                rs.getString("rmd_name"),
                ReportType.getEnumByValue(rs.getString("rmd_format"))
        );
        Report report = new Report(model);
        report.setId(rs.getLong("rre_id"));
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
            } catch (IOException e) {
                e.printStackTrace();
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
