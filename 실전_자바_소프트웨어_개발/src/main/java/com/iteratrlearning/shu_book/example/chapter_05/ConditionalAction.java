package com.iteratrlearning.shu_book.example.chapter_05;

public interface ConditionalAction {
    void perform(Facts facts);
    boolean evaluate(Facts facts);

}
