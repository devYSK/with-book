package com.ys.practice.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;

import com.ys.practice.domain.BankAccountDetails;

public class BankAccountRepositoryImpl implements BankAccountRepositoryCustom {
	@Autowired
	private MongoOperations mongoOperations;
	
	@Override
	public void subtractFromAccount(String bankAccountId, int amount) {
		BankAccountDetails bankAccountDetails = mongoOperations.findById(bankAccountId, BankAccountDetails.class);
		if (bankAccountDetails.getBalance() < amount) {
			throw new RuntimeException("Insufficient balance amount in bank account");
		}
		bankAccountDetails.setBalance(bankAccountDetails.getBalance() - amount);
		mongoOperations.save(bankAccountDetails);
	}
}
