package com.cerebra.fileprocessor.repository;

import com.cerebra.fileprocessor.entity.ProcessedMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
@Repository
public interface ProcessedMessageRepository extends JpaRepository<ProcessedMessage, Long> {

    @Modifying
    @Query("DELETE FROM ProcessedMessage pm WHERE pm.processFile.id = :processFileId")
    void deleteByProcessFileId(@Param("processFileId") Long processFileId);
}

