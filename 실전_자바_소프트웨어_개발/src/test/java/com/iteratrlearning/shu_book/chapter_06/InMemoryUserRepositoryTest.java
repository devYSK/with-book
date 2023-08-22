package com.iteratrlearning.shu_book.chapter_06;

import com.iteratrlearning.shu_book.example.chapter_06.UserRepository;
import com.iteratrlearning.shu_book.example.chapter_06.in_memory.InMemoryUserRepository;

public class InMemoryUserRepositoryTest extends AbstractUserRepositoryTest
{
    private InMemoryUserRepository inMemoryUserRepository = new InMemoryUserRepository();

    @Override
    protected UserRepository newRepository()
    {
        return inMemoryUserRepository;
    }
}
