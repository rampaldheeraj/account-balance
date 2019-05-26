# account-balance
This application is written in java11. 

To execute, run the main method in Application.java


The approach used here is as follows :
Since we have input in the form of cvs as 

```
transactionId, fromAccountId, toAccountId, createdAt, amount, transactionType, relatedTransaction
TX10001, ACC334455, ACC778899, 20/10/2018 12:47:55, 25.00, PAYMENT
TX10002, ACC334455, ACC998877, 20/10/2018 17:33:43, 10.50, PAYMENT
```

we can assume that each transaction is composed of 2 sub transactions, one for debit and one for credit.
Each data row in the csv is passed to a one-to-many function to get 2 sub-transactions out of it, which looks like 
```
TX10001, ACC334455,  20/10/2018 12:47:55, DEBIT, -25.00, PAYMENT
TX10001, ACC778899,  20/10/2018 12:47:55, CREDIT, 25.00, PAYMENT
```

Then the REVERSALS and PAYMENTS are extracted out in separate Collections.
The filter is then applied on PAYMENTS Collection as follows :
```
1) Filter on accountId
2) Ignore if the transaction has a reversal.
3) Filter on date range
```
This way we can get a flat view of all account transactions. We can do then filter and reduce the list to get the relative account balance for the given input.

We can have a more optimised solution as follows:
```
Start reading file from the end. 
When a REVERSAL is encountered store the relatedTransactionNumber.
When a transaction that has been reversed is encountered (from the above state store), mark it to be ignored.
```
This way we should be able to create some appropriate data structure in a single pass through the CSV. Maybe not so readable.
