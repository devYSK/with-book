package com.iteratrlearning.shu_book.example.chapter_05;

import static org.mockito.Mockito.*;

import org.junit.Test;

import junit.framework.TestCase;

public class BusinessRuleEngineTest extends TestCase {

	@Test
	void shouldHaveNoRulesInitially() {
		final BusinessRuleEngine businessRuleEngine = new BusinessRuleEngine();

		final Action mockAction = mock(Action.class);

		assertEquals(0, businessRuleEngine.count());
	}

	@Test
	void shouldExecuteOneAction() {
		final BusinessRuleEngine businessRuleEngine = new BusinessRuleEngine();
		final Action mockAction = mock(Action.class);
		businessRuleEngine.addAction(mockAction);
		businessRuleEngine.run();
		verify(mockAction);
	}

	@Test
	void shouldAddwoActions() {
		// final BusinessRuleEngine businessRuleEngine = new BusinessRuleEngine();
		// businessRuleEngine.addAction(() -> {
		// });
		// businessRuleEngine.addAction(() -> {
		// });
		// assertEquals(2, businessRuleEngine.count());
	}
}