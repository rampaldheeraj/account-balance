package org.rampal.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class AccountBalanceCheckRequest {

    private String accountId;

    private LocalDateTime from;

    private LocalDateTime to;
}
