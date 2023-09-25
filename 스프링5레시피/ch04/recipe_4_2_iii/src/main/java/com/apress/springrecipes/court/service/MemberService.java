package com.apress.springrecipes.court.service;

import java.util.Collection;

import com.apress.springrecipes.court.domain.Member;

public interface MemberService {

    Collection<Member> findAll();
    Member find(long id);
}
