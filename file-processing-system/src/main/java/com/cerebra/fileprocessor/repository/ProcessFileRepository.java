package com.cerebra.fileprocessor.repository;

import com.cerebra.fileprocessor.entity.ProcessFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
@Repository
public interface ProcessFileRepository extends JpaRepository<ProcessFile, Long> {


    @Query("SELECT p FROM ProcessFile p WHERE p.user.id = :userId")
    Page<ProcessFile> findByUserId(@Param("userId") Long userId, Pageable pageable);


}