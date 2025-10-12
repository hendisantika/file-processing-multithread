package com.multithread.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Created by IntelliJ IDEA.
 * Project : file-processing-multithread
 * User: hendisantika
 * Link: s.id/hendisantika
 * Email: hendisantika@yahoo.co.id
 * Telegram : @hendisantika34
 * Date: 12/10/25
 * Time: 10.30
 * To change this template use File | Settings | File Templates.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingResult {
    private String processingType;
    private int totalFiles;
    private int recordsProcessed;
    private long processingTimeMs;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean useMultithreading;
    private int threadPoolSize;
    private String status;
    private String message;
}
