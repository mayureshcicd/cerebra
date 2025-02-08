package com.cerebra.fileprocessor.audit;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.cerebra.fileprocessor.common.ResponseUtil;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@MappedSuperclass
@EqualsAndHashCode
public abstract class AuditEntity {

    @Column(name = "create_by", updatable = false)
    private String createdBy;
    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Column(name = "create_on", updatable = false)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdOn;

    @Column(name = "update_on")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime updatedOn;

    @PrePersist
    protected void onCreate() {
        createdOn = LocalDateTime.now();
        createdBy = ResponseUtil.getAuthUser();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedOn = LocalDateTime.now();
        lastModifiedBy = ResponseUtil.getAuthUser();
    }


}
