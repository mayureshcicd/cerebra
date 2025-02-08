package com.cerebra.fileprocessor.events;

import com.cerebra.fileprocessor.entity.ProcessFile;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class FileUploadedEvent extends ApplicationEvent {
    private final ProcessFile fileEntity;

    public FileUploadedEvent(Object source, ProcessFile fileEntity) {
        super(source);
        this.fileEntity = fileEntity;
    }
}
