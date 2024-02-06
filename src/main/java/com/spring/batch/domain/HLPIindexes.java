package com.spring.batch.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "hlpi_indexes")
@Entity
public class HLPIindexes {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "index")
    private Integer index;

    @Column(name = "quarterly_change")
    private Double quarterlyChange;

    @Column(name = "annual_change")
    private Double annualChange;
}
