package org.rampal.service;

import org.rampal.data.TransactionData;
import org.rampal.domain.SubTransaction;
import org.rampal.request.AccountBalanceCheckRequest;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RelativeAccountBalanceCheckerService {

    public List<SubTransaction> transactionsForAccountBalance(AccountBalanceCheckRequest request) {
        var allAccountPayments = TransactionData.PAYMENTS.stream()
                .filter(payment -> payment.getAccountId().equals(request.getAccountId()));
        var collect = allAccountPayments
                .filter(onlyPayments())
                .filter(payment -> payment.getTransactionTime().isAfter(request.getFrom())
                        && payment.getTransactionTime().isBefore(request.getTo()))
                .collect(Collectors.toList());
        return collect;


    }

    private Predicate<SubTransaction> onlyPayments() {
        return payment -> TransactionData.REVERSALS.stream()
                .noneMatch(reversal -> reversal.getRelatedTransactionReference().equals(payment.getTransactionReference()));
    }
}
