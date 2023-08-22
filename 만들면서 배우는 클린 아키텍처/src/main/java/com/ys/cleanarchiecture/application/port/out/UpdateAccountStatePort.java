package com.ys.cleanarchiecture.application.port.out;

import com.ys.cleanarchiecture.domain.Account;

public interface UpdateAccountStatePort {

	void updateActivities(Account account);

}
