package org.rampal.domain;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class SubTransaction {

    private String transactionReference;
    private String accountId;
    private BigDecimal amount;
    private LocalDateTime transactionTime;
    private TransactionType transactionType;
    private SubTransactionType subTransactionType;
    private String relatedTransactionReference;
}
