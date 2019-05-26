package org.rampal.service;

import org.rampal.data.TransactionData;
import org.rampal.domain.SubTransaction;
import org.rampal.request.AccountBalanceCheckRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RelativeAccountBalanceCheckerService {

    public List<SubTransaction> transactionsForAccountBalance(AccountBalanceCheckRequest request) {
        var allAccountTransactions = TransactionData.PAYMENTS.stream()
                .filter(payment -> payment.getAccountId().equals(request.getAccountId()));
        return allAccountTransactions
                .filter(onlyPayments())
                .filter(withinDateRange(request.getFrom(), request.getTo()))
                .collect(Collectors.toList());


    }

    private Predicate<SubTransaction> withinDateRange(LocalDateTime fromDate, LocalDateTime toDate) {
        return payment -> payment.getTransactionTime().isAfter(fromDate)
                && payment.getTransactionTime().isBefore(toDate);
    }

    private Predicate<SubTransaction> onlyPayments() {
        return payment -> TransactionData.REVERSALS.stream()
                .noneMatch(reversal -> reversal.getRelatedTransactionReference().equals(payment.getTransactionReference()));
    }
}
