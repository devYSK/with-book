package com.iteratrlearning.shu_book.chapter_02;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;

import com.iteratrlearning.shu_book.example.chapter_02.BankStatementCSVParser;
import com.iteratrlearning.shu_book.example.chapter_02.BankStatementParser;
import com.iteratrlearning.shu_book.example.chapter_02.BankTransaction;

public class BankTransactionCSVParserTest {

    private BankStatementParser statementParser = new BankStatementCSVParser();

    @Test
    public void shouldParseOneCorrectLine() throws Exception {
        String line = "30-01-2017,-50,Tesco";

        BankTransaction result = statementParser.parseFrom(line);

        BankTransaction expected = new BankTransaction(LocalDate.of(2017, Month.JANUARY, 30), -50, "Tesco");
        Assert.assertEquals(expected.getDate(), result.getDate());
        Assert.assertEquals(expected.getAmount(), result.getAmount(), 0.0d);
        Assert.assertEquals(expected.getDescription(), result.getDescription());
    }

}
