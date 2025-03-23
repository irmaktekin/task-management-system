package com.irmaktekin.task.management.system.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface AttachmentService {
    void addAttachmentToTask(UUID taskId, MultipartFile file) throws Exception;
}
