package com.ys.cleanarchiecture.application.port.out;

import com.ys.cleanarchiecture.domain.Account;

public interface AccountLock {

	void lockAccount(Account.AccountId accountId);

	void releaseAccount(Account.AccountId accountId);

}
