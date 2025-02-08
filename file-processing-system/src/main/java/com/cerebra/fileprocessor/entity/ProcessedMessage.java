package com.cerebra.fileprocessor.entity;


import com.cerebra.fileprocessor.audit.AuditEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "processed_messages")
public class ProcessedMessage extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_processed_seq")
    @SequenceGenerator(name = "file_processed_seq", sequenceName = "file_processed_sequence", allocationSize = 1)
    private Long id;

    private String message;

    @ManyToOne
    @JoinColumn(name = "processed_file_id", nullable = false)
    private ProcessFile processFile;
}
