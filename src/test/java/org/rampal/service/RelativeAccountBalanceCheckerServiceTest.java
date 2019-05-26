package org.rampal.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rampal.data.TransactionsLoader;
import org.rampal.domain.SubTransaction;
import org.rampal.domain.TransactionType;
import org.rampal.request.AccountBalanceCheckRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

class RelativeAccountBalanceCheckerServiceTest {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private static RelativeAccountBalanceCheckerService checkerService;


    @BeforeAll
    static void setup() throws URISyntaxException, IOException {
        var csvUri = Objects.requireNonNull(RelativeAccountBalanceCheckerServiceTest.class.getClassLoader()
                .getResource("transactions-test.csv")).toURI();


        TransactionsLoader.loadFromCsv(csvUri);

        checkerService = new RelativeAccountBalanceCheckerService();
    }

    @Test
    @DisplayName("Testing reversal transactions are ignored")
    void givenATransactionsCSV_AccountBalanceCheckShouldIgnoreReversedTransactions() {
        AccountBalanceCheckRequest request = new AccountBalanceCheckRequest("ACC334455",
                LocalDateTime.parse("20/10/2018 12:00:00", FORMATTER), LocalDateTime.parse("20/10/2018 19:00:00", FORMATTER));

        var response = checkerService.transactionsForAccountBalance(request);

        response.forEach(st -> Assertions.assertEquals(st.getTransactionType(), TransactionType.PAYMENT));

    }

    @Test
    @DisplayName("Testing total relative account balance")
    void givenATransactionsCSV_AccountBalanceCheckShouldIncludeSubTxnsOnlyWithingGivenDateRange() {
        var toDate = LocalDateTime.parse("20/10/2018 19:30:00", FORMATTER);
        var fromDate = LocalDateTime.parse("20/10/2018 12:00:00", FORMATTER);
        AccountBalanceCheckRequest request = new AccountBalanceCheckRequest("ACC334455",
                fromDate, toDate);

        var response = checkerService.transactionsForAccountBalance(request);

        var maxLocalDateTime = response.stream().map(SubTransaction::getTransactionTime)
                .max(LocalDateTime::compareTo).orElse(null);

        var minLocalDateTime = response.stream().map(SubTransaction::getTransactionTime)
                .min(LocalDateTime::compareTo).orElse(null);

        Assertions.assertAll("Testing date range", () -> {
            Assertions.assertTrue(maxLocalDateTime.isBefore(toDate));
            Assertions.assertTrue(minLocalDateTime.isAfter(fromDate));
        });

    }

    @Test
    @DisplayName("Testing Transactions are withing given date range")
    void givenATransactionsCSV_AccountBalanceCheckShouldReturnCorrectBalance() {
        var toDate = LocalDateTime.parse("20/10/2018 19:30:00", FORMATTER);
        var fromDate = LocalDateTime.parse("20/10/2018 12:00:00", FORMATTER);
        AccountBalanceCheckRequest request = new AccountBalanceCheckRequest("ACC334455",
                fromDate, toDate);

        var response = checkerService.transactionsForAccountBalance(request);

        var total = response.stream()
                .map(SubTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Assertions.assertEquals(new BigDecimal("-50.00"), total);

    }

}