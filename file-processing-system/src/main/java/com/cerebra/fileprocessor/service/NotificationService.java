package com.cerebra.fileprocessor.service;


public interface NotificationService {

    void notifyFileProcessStatusToMonitor(String userEmail,String message);
}
