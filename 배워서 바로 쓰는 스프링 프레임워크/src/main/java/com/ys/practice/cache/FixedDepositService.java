package com.ys.practice.cache;

import java.util.List;

import com.ys.practice.domain.FixedDepositDetails;


public interface FixedDepositService {
	void createFixedDeposit(FixedDepositDetails fdd) throws Exception;
	FixedDepositDetails getFixedDeposit(int fixedDepositId);
	List<FixedDepositDetails> findFixedDepositsByBankAccount(int bankAccountId);
	FixedDepositDetails getFixedDepositFromCache(int fixedDepositId);
}
