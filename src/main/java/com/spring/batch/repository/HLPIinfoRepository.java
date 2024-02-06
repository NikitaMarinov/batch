package com.spring.batch.repository;

import com.spring.batch.domain.HLPIinfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HLPIinfoRepository extends JpaRepository<HLPIinfo,Long> {
}
