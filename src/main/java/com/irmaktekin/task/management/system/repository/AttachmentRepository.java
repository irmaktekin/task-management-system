package com.irmaktekin.task.management.system.repository;

import com.irmaktekin.task.management.system.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AttachmentRepository  extends JpaRepository<Attachment, UUID> {
}
