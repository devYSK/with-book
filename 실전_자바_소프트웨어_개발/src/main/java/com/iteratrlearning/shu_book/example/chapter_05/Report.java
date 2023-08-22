package com.iteratrlearning.shu_book.example.chapter_05;

public class Report {

	private final ConditionalAction conditionalAction;
	private final Facts facts;
	private final boolean isPositive;

	public Report(final Facts facts,
		final ConditionalAction conditionalAction,
		final boolean isPositive) {
		this.facts = facts;
		this.conditionalAction = conditionalAction;
		this.isPositive= isPositive;
	}

	public ConditionalAction getConditionalAction() {
		return conditionalAction;
	}

	public Facts getFacts() {
		return facts;
	}

	public boolean isPositive() {
		return isPositive;
	}
}
