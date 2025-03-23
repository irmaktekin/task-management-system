package com.irmaktekin.task.management.system.dto.response;

import java.util.UUID;

public record CommentDto(
   UUID id,
   String content,
   UUID userId)
{}
