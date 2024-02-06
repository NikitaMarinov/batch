package com.spring.batch.mappers;

import com.spring.batch.domain.HLPI;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HLPIRowMapper implements RowMapper<HLPI> {

    @Override
    public HLPI mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        HLPI hlpi = new HLPI();

        hlpi.setHLPIName(resultSet.getString("hlpi_name"));
        hlpi.setSeriesReference(resultSet.getString("series_reference"));
        hlpi.setQuarter(resultSet.getString("quarter"));
        hlpi.setHLPI(resultSet.getString("hlpi"));
        hlpi.setNZHEC(resultSet.getString("nzhec"));
        hlpi.setNZHECName(resultSet.getString("nzhec_name"));
        hlpi.setNZHECShort(resultSet.getString("nzhec_short"));
        hlpi.setLevel(resultSet.getString("level"));
        hlpi.setIndex(Integer.valueOf(resultSet.getString("index")));
        hlpi.setQuarterlyChange(Double.valueOf(resultSet.getString("quarterly_change")));
        hlpi.setAnnualChange(Double.valueOf(resultSet.getString("annual_change")));
        return hlpi;
    }
}