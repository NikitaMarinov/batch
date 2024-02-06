package com.spring.batch.processors;

import com.spring.batch.domain.HLPI;
import com.spring.batch.domain.HLPIindexes;
import org.springframework.batch.item.ItemProcessor;

public class HlpihlpIindexisItemProcessor implements ItemProcessor<HLPI, HLPIindexes> {
    @Override
    public HLPIindexes process(HLPI item) throws Exception {
        HLPIindexes hlpIindexis = new HLPIindexes();

        hlpIindexis.setIndex(item.getIndex());
        hlpIindexis.setAnnualChange(item.getAnnualChange());
        hlpIindexis.setQuarterlyChange(item.getQuarterlyChange());

        return hlpIindexis;
    }
}
