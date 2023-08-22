package com.ys.cleanarchiecture.adapter.web;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ys.cleanarchiecture.application.port.in.GetAccountBalanceQuery;
import com.ys.cleanarchiecture.application.port.in.SendMoneyUseCase;

import lombok.RequiredArgsConstructor;

/**
 * 너무 클래스가 많고 코드가 많아질 수 있는 컨트롤러
 */
@RestController
@RequiredArgsConstructor
public class AccountController {
	//
	// private final GetAccountBalanceQuery getAccountBalanceQuery;
	// private final ListAccountsQuery listAccountsQuery;
	// private final LoadAccountQuery loadAccountQuery;
	// // query랑 usecase랑 분리
	// private final SendMoneyUseCase sendMoneyUseCase;
	// private final CreateAccountUseCase createAccountUseCase;
	//
	//
	//
	// @PostMapping("/accounts/send/{sourceAccountId}/{targetAccountId}/{amount}")
	// void sendMoney(
	// 	@PathVariable("sourceAccountId") Long sourceAccountId,
	// 	@PathVariable ("targetAccountId") Long targetAccountId,
	// 	@PathVariable("amount") Long amount) {
	//
	// }

}
