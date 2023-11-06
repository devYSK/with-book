package com.ys.application.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;

public class Statement {

	@Getter
	private final Customer customer;

	private List<Account> accounts = new ArrayList<>();

	public Statement(Customer customer, List<Account> accounts) {
		this.customer = customer;
		this.accounts.addAll(accounts);
	}

	public Statement(Customer customer) {
		this.customer = customer;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts.addAll(accounts);
	}

	public List<Account> getAccounts() {
		return Collections.unmodifiableList(accounts);
	}
}
