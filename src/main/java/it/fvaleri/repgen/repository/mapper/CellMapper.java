/*
 * Copyright Federico Valeri.
 * License: Apache License 2.0 (see the file LICENSE).
 */
package it.fvaleri.repgen.repository.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import it.fvaleri.repgen.domain.Data;

public class CellMapper implements RowMapper<Data> {
    @Override
    public Data mapRow(ResultSet rs, int rowNum) throws SQLException {
        Data data = new Data();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnNum = rsmd.getColumnCount() - 2;
        List<String> cells = new ArrayList<String>();
        for (int i = 0; i < columnNum; i++) {
            cells.add(rs.getString("rtb_cell" + i));
        }
        data.setCells(cells);
        return data;
    }
}
