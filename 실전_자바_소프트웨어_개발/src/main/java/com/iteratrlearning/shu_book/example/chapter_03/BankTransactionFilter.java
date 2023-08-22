package com.iteratrlearning.shu_book.example.chapter_03;

@FunctionalInterface
public interface BankTransactionFilter {
    boolean test(BankTransaction bankTransaction);
}