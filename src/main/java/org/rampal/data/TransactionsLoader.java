package org.rampal.data;

import org.rampal.domain.SubTransaction;
import org.rampal.domain.SubTransactionType;
import org.rampal.domain.TransactionType;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TransactionsLoader {


    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static List<SubTransaction> loadFromCsv(URI csvURI) throws IOException {
        Path pathToCsv = Paths.get(csvURI);

        var subTransactions = Files.lines(pathToCsv)
                .skip(1)
                .map(toSubTransactions())
                .flatMap(List::stream)
                .collect(Collectors.toList());

        TransactionData.SUB_TRANSACTIONS = subTransactions;

        var transactionTypeListMap = subTransactions.stream()
                .collect(Collectors.groupingBy(SubTransaction::getTransactionType));

        TransactionData.PAYMENTS = transactionTypeListMap.get(TransactionType.PAYMENT);

        TransactionData.REVERSALS = transactionTypeListMap.get(TransactionType.REVERSAL);


        return subTransactions;
    }


    private static Function<String, List<SubTransaction>> toSubTransactions() {
        return csvRow -> {
            List<String> values = new ArrayList<>(Arrays.asList(csvRow.split(",")));

            if (values.get(5).trim().equals(TransactionType.PAYMENT.name()))
                values.add("");

            SubTransactionType type1;
            BigDecimal amount1;

            SubTransactionType type2;
            BigDecimal amount2;

            if (values.get(5).trim().equals(TransactionType.PAYMENT.name())) {
                type1 = SubTransactionType.DEBIT;
                amount1 = new BigDecimal(values.get(4).trim()).negate();
                type2 = SubTransactionType.CREDIT;
                amount2 = new BigDecimal(values.get(4).trim());
            } else {
                type1 = SubTransactionType.CREDIT;
                amount1 = new BigDecimal(values.get(4).trim());
                type2 = SubTransactionType.DEBIT;
                amount2 = new BigDecimal(values.get(4).trim()).negate();
            }
            return List.of(SubTransaction.builder()
                            .transactionReference(values.get(0).trim())
                            .accountId(values.get(1).trim())
                            .amount(amount1)
                            .transactionTime(LocalDateTime.parse(values.get(3).trim(), FORMATTER))
                            .transactionType(TransactionType.valueOf(values.get(5).trim()))
                            .subTransactionType(type1)
                            .relatedTransactionReference(values.get(6).trim())
                            .build(),
                    SubTransaction.builder()
                            .transactionReference(values.get(0).trim())
                            .accountId(values.get(2).trim())
                            .amount(amount2)
                            .transactionTime(LocalDateTime.parse(values.get(3).trim(), FORMATTER))
                            .transactionType(TransactionType.valueOf(values.get(5).trim()))
                            .subTransactionType(type2)
                            .relatedTransactionReference(values.get(6).trim())
                            .build());
        };
    }
}
