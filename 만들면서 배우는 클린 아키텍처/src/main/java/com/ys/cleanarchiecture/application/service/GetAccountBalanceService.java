package com.ys.cleanarchiecture.application.service;

import java.time.LocalDateTime;

import com.ys.cleanarchiecture.application.port.in.GetAccountBalanceQuery;
import com.ys.cleanarchiecture.application.port.out.LoadAccountPort;
import com.ys.cleanarchiecture.domain.Account;
import com.ys.cleanarchiecture.domain.Money;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class GetAccountBalanceService implements GetAccountBalanceQuery {

	private final LoadAccountPort loadAccountPort;

	@Override
	public Money getAccountBalance(Account.AccountId accountId) {
		return loadAccountPort.loadAccount(accountId, LocalDateTime.now())
				.calculateBalance();
	}

}
