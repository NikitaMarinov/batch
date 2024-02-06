package com.spring.batch.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "hlpi_info")
@Entity
public class HLPIinfo {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "hlpi_name")
    private String HLPIName;

    @Column(name = "series_reference")
    private String seriesReference;

    @Column(name = "quarter")
    private String quarter;

    @Column(name = "hlpi")
    private String HLPI;

    @Column(name = "nzhec")
    private String NZHEC;

    @Column(name = "nzhec_name")
    private String NZHECName;

    @Column(name = "nzhec_short")
    private String NZHECShort;

    @Column(name = "level")
    private String level;
}
