package com.multithread.service;

import com.multithread.domain.dto.ProcessingResult;

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

public interface FileProcessingService {

    /**
     * Process files using single-threaded approach
     *
     * @param filePattern Pattern to match files (e.g., "transactions/**.ndjson")
     * @return Processing result with statistics
     */
    ProcessingResult processSingleThreaded(String filePattern) throws Exception;

    /**
     * Process files using multi-threaded approach
     *
     * @param filePattern    Pattern to match files (e.g., "transactions/**.ndjson")
     * @param threadPoolSize Number of threads to use
     * @return Processing result with statistics
     */
    ProcessingResult processMultiThreaded(String filePattern, int threadPoolSize) throws Exception;
}
