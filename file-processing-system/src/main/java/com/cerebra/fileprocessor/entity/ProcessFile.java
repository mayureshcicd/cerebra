package com.cerebra.fileprocessor.entity;


import com.cerebra.fileprocessor.audit.AuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "process-files")
public class ProcessFile extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_process_seq")
    @SequenceGenerator(name = "file_process_seq", sequenceName = "file_process_sequence", allocationSize = 1)
    private Long id;

    @Column(name = "original_file_name")
    private String originalFileName;
    @Column(name = "system_file_name")
    private String systemFileName;

    @Column(name = "file_size")
    private String fileSize;

    @Column(name = "upload_date")
    private LocalDateTime uploadDate;

    private boolean processed;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
