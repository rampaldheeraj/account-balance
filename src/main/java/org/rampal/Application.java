package org.rampal;

import org.rampal.data.TransactionsLoader;
import org.rampal.domain.SubTransaction;
import org.rampal.request.AccountBalanceCheckRequest;
import org.rampal.service.RelativeAccountBalanceCheckerService;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import static org.rampal.data.TransactionsLoader.FORMATTER;

public class Application {


    public static void main(String[] args) throws URISyntaxException, IOException {
        var csvUri = Objects.requireNonNull(Application.class.getClassLoader()
                .getResource("transactions.csv")).toURI();


        TransactionsLoader.loadFromCsv(csvUri);

        Scanner scanner = new Scanner(System.in).useDelimiter("\n");
        ;

        while (true) {
            System.out.println(" Account Number : ");

            String accountId = scanner.next();

            System.out.println(" From Date (dd/MM/yyyy HH:mm:ss) : ");

            LocalDateTime from = null;
            LocalDateTime to = null;
            try {
                var fromText = scanner.next();
                if(fromText.isBlank()) {
                    System.err.println("from date must be entered");
                    scanner.close();
                    return;
                }
                from = LocalDateTime.parse(fromText, FORMATTER);

                System.out.println(" To Date (dd/MM/yyyy HH:mm:ss) : ");

                var toText = scanner.next();

                if(toText.isBlank()) {
                    System.err.println("to date must be entered");
                    scanner.close();
                    return;
                }
                to = LocalDateTime.parse(toText, FORMATTER);
            } catch (DateTimeParseException e) {
                System.err.println("Please enter the date in 'dd/MM/yyyy HH:mm:ss' format");
                scanner.close();
                return;
            }

            AccountBalanceCheckRequest request = new AccountBalanceCheckRequest(accountId, from, to);

            List<SubTransaction> response = new RelativeAccountBalanceCheckerService().transactionsForAccountBalance(request);

            var total = response.stream()
                    .map(SubTransaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            System.out.println("The account balance for the period is " + total);

            System.out.println("Number of transactions included is " + response.size());

        }
    }


}
