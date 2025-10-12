package com.multithread.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@AllArgsConstructor
@NoArgsConstructor
public class LogEntryDto {

    @JsonProperty("timestamp")
    private long timestamp;

    @JsonProperty("level")
    private String level;

    @JsonProperty("service")
    private String service;

    @JsonProperty("message")
    private String message;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("requestId")
    private String requestId;

    @JsonProperty("duration")
    private Integer duration;
}
