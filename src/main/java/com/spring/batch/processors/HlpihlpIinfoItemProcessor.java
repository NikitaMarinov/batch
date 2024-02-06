package com.spring.batch.processors;

import com.spring.batch.domain.HLPI;
import com.spring.batch.domain.HLPIinfo;
import org.springframework.batch.item.ItemProcessor;

public class HlpihlpIinfoItemProcessor implements ItemProcessor<HLPI, HLPIinfo> {
    @Override
    public HLPIinfo process(HLPI item) throws Exception {
        HLPIinfo hlpIinfo = new HLPIinfo();

        hlpIinfo.setHLPIName(item.getHLPIName());
        hlpIinfo.setHLPI(item.getHLPI());
        hlpIinfo.setNZHEC(item.getNZHEC());
        hlpIinfo.setSeriesReference(item.getSeriesReference());
        hlpIinfo.setNZHECName(item.getNZHECName());
        hlpIinfo.setNZHECShort(item.getNZHECShort());
        hlpIinfo.setLevel(item.getLevel());
        hlpIinfo.setQuarter(item.getQuarter());

        return hlpIinfo;
    }
}
