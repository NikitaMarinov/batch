package com.spring.batch.repository;

import com.spring.batch.domain.HLPIindexes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HLPIindexisRepository extends JpaRepository<HLPIindexes,Long> {
}
