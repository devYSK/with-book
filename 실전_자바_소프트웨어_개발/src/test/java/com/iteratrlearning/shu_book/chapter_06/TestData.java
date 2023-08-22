package com.iteratrlearning.shu_book.chapter_06;

import com.iteratrlearning.shu_book.example.chapter_06.KeyGenerator;
import com.iteratrlearning.shu_book.example.chapter_06.Position;
import com.iteratrlearning.shu_book.example.chapter_06.Twoot;

class TestData
{
    static final String USER_ID = "Joe";
    static final String OTHER_USER_ID = "John";
    static final String NOT_A_USER = "Jack";

    static final byte[] SALT = KeyGenerator.newSalt();
    static final String PASSWORD = "ahc5ez";
    static final byte[] PASSWORD_BYTES = KeyGenerator.hash(PASSWORD, SALT);

    static final String TWOOT = "Hello World!";
    static final String TWOOT_2 = "Bye World!";

    static Twoot twootAt(final String id, final Position position)
    {
        return new Twoot(id, OTHER_USER_ID, TWOOT, position);
    }
}
