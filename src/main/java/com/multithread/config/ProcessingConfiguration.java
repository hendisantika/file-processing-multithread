package com.multithread.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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
@Configuration
@ConfigurationProperties(prefix = "processing")
public class ProcessingConfiguration {

    private ThreadPool threadPool = new ThreadPool();

    @Data
    public static class ThreadPool {
        private int defaultSize = 5;
        private int maxSize = 20;
        private int minSize = 1;
    }
}
