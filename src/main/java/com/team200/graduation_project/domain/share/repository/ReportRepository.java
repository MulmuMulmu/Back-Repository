package com.team200.graduation_project.domain.share.repository;

import com.team200.graduation_project.domain.share.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

}
