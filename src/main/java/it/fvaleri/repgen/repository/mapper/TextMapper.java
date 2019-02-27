/*
 * Copyright Federico Valeri.
 * License: Apache License 2.0 (see the file LICENSE).
 */
package it.fvaleri.repgen.repository.mapper;

import static it.fvaleri.repgen.utility.CommonUtils.normalize;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import it.fvaleri.repgen.domain.Text;

public class TextMapper implements RowMapper<Text> {
    @Override
    public Text mapRow(ResultSet rs, int rowNum) throws SQLException {
        Text text = new Text();
        text.setName(rs.getString("rtt_name"));
        text.setValue(normalize(rs.getString("rte_value")));
        return text;
    }
}
