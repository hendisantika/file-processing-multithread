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
public class TransactionDto {

    @JsonProperty("transactionId")
    private String transactionId;

    @JsonProperty("userId")
    private int userId;

    @JsonProperty("amount")
    private int amount;

    @JsonProperty("date")
    private long date;

    @JsonProperty("status")
    private String status;

    @JsonProperty("type")
    private String type;

    @JsonProperty("fromAccountNumber")
    private String fromAccountNumber;

    @JsonProperty("fromBankName")
    private String fromBankName;

    @JsonProperty("toAccountNumber")
    private String toAccountNumber;

    @JsonProperty("toBankName")
    private String toBankName;
}