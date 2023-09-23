package com.ys.practice.cache;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ys.practice.domain.FixedDepositDetails;

@Service(value = "fixedDepositService")
public class FixedDepositServiceImpl implements FixedDepositService {
	@Override
	public void createFixedDeposit(FixedDepositDetails fdd) throws Exception {

	}

	@Override
	public FixedDepositDetails getFixedDeposit(int fixedDepositId) {
		return null;
	}

	@Override
	public List<FixedDepositDetails> findFixedDepositsByBankAccount(int bankAccountId) {
		return null;
	}

	@Override
	public FixedDepositDetails getFixedDepositFromCache(int fixedDepositId) {
		return null;
	}
	// private static Logger logger = LogManager.getLogger(FixedDepositServiceImpl.class);
	//
	// @Autowired
	// private JmsMessagingTemplate jmsMessagingTemplate;
	//
	// // @Autowired
	// // @Qualifier(value = "fixedDepositDao")
	// // private FixedDepositDao myFixedDepositDao;
	//
	// @Override
	// @Transactional(transactionManager = "jmsTxManager")
	// @CacheEvict(value = { "fixedDepositList" }, allEntries = true, beforeInvocation = true)
	// public void createFixedDeposit(final FixedDepositDetails fdd) throws Exception {
	// 	logger.info("createFixedDeposit method invoked");
	// 	jmsMessagingTemplate.send("emailQueueDestination", MessageBuilder.withPayload(fdd.getEmail()).build());
	// 	// --this JMS message goes to the default destination configured for the
	// 	// JmsTemplate
	// 	jmsMessagingTemplate.send(MessageBuilder.withPayload(fdd).build());
	// }
	//
	// @Override
	// @CachePut(value = { "fixedDeposit" }, key = "#fixedDepositId")
	// public FixedDepositDetails getFixedDeposit(int fixedDepositId) {
	// 	logger.info("getFixedDeposit method invoked with fixedDepositId " + fixedDepositId);
	// 	return myFixedDepositDao.getFixedDeposit(fixedDepositId);
	// }
	//
	// @Override
	// @Cacheable(value = { "fixedDeposit" }, key = "#fixedDepositId")
	// public FixedDepositDetails getFixedDepositFromCache(int fixedDepositId) {
	// 	logger.info("getFixedDepositFromCache method invoked with fixedDepositId " + fixedDepositId);
	// 	throw new RuntimeException(
	// 			"This method throws exception because FixedDepositDetails object must come from the cache");
	// }
	//
	// @Cacheable(value = { "fixedDepositList" })
	// public List<FixedDepositDetails> findFixedDepositsByBankAccount(int bankAccountId) {
	// 	logger.info("findFixedDepositsByBankAccount method invoked");
	// 	return myFixedDepositDao.findFixedDepositsByBankAccount(bankAccountId);
	// }
}
