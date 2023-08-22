package com.ys.cleanarchiecture.application.port.in;

import com.ys.cleanarchiecture.domain.Account;
import com.ys.cleanarchiecture.domain.Money;

public interface GetAccountBalanceQuery {

	Money getAccountBalance(Account.AccountId accountId);

}
