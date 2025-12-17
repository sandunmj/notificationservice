package com.codein.notificationservice.repository;

import com.codein.notificationservice.entity.EmailRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRecordRepository extends JpaRepository<EmailRecord, Long> {
}