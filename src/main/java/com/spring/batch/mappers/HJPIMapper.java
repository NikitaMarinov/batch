package com.spring.batch.mappers;

import com.spring.batch.domain.HLPI;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

public class HJPIMapper implements FieldSetMapper<HLPI> {

    @Override
    public HLPI mapFieldSet(FieldSet fieldSet) {
        HLPI hlpi = new HLPI();
        hlpi.setHLPIName(fieldSet.readString(0 ));
        hlpi.setSeriesReference(fieldSet.readString(1));
        hlpi.setQuarter(fieldSet.readString(2));
        hlpi.setHLPI(fieldSet.readString(3));
        hlpi.setNZHEC(fieldSet.readString(4));
        hlpi.setNZHECName(fieldSet.readString(5));
        hlpi.setNZHECShort(fieldSet.readString(6));
        hlpi.setLevel(fieldSet.readString(7));
        hlpi.setIndex(fieldSet.readInt(8));

        String bufferString = fieldSet.readString(9);
        hlpi.setQuarterlyChange(bufferString.equals("NA") ? 0.0 : Double.parseDouble(bufferString));

        bufferString = fieldSet.readString(10);
        hlpi.setAnnualChange(bufferString.equals("NA") ? 0.0 : Double.parseDouble(bufferString));

        return hlpi;
    }
}
