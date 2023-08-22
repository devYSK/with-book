package com.ys.cleanarchiecture.adapter.web;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ys.cleanarchiecture.application.port.in.SendMoneyCommand;
import com.ys.cleanarchiecture.application.port.in.SendMoneyUseCase;
import com.ys.cleanarchiecture.domain.Account.AccountId;
import com.ys.cleanarchiecture.domain.Money;

@WebMvcTest(controllers = SendMoneyController.class)
class SendMoneyControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private SendMoneyUseCase sendMoneyUseCase;

	@Test
	void testSendMoney() throws Exception {

		mockMvc.perform(post("/accounts/send/{sourceAccountId}/{targetAccountId}/{amount}",
				41L, 42L, 500)
				.header("Content-Type", "application/json"))
			.andExpect(status().isOk());

		then(sendMoneyUseCase).should()
			.sendMoney(eq(new SendMoneyCommand(
				new AccountId(41L),
				new AccountId(42L),
				Money.of(500L))));
	}

}
