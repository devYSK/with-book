package com.ys.cleanarchiecture.application.port.out;

import java.time.LocalDateTime;

import com.ys.cleanarchiecture.domain.Account;

public interface LoadAccountPort {

	Account loadAccount(Account.AccountId accountId, LocalDateTime baselineDate);
}
