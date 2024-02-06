package com.spring.batch.repository;

import com.spring.batch.domain.HLPI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HLPIRepository extends JpaRepository<HLPI,Long> {
}
