package com.ys.cleanarchiecture.domain;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class Activity {

	@Getter
	private ActivityId id;

	@Getter
	@NonNull
	private final Account.AccountId ownerAccountId;

	@Getter
	@NonNull
	private final Account.AccountId sourceAccountId;

	@Getter
	@NonNull
	private final Account.AccountId targetAccountId;

	@Getter
	@NonNull
	private final LocalDateTime timestamp;

	@Getter
	@NonNull
	private final Money money;

	public Activity(
		@NonNull Account.AccountId ownerAccountId,
		@NonNull Account.AccountId sourceAccountId,
		@NonNull Account.AccountId targetAccountId,
		@NonNull LocalDateTime timestamp,
		@NonNull Money money) {
		this.id = null;
		this.ownerAccountId = ownerAccountId;
		this.sourceAccountId = sourceAccountId;
		this.targetAccountId = targetAccountId;
		this.timestamp = timestamp;
		this.money = money;
	}

	@Value
	public static class ActivityId {
		private final Long value;
	}

}
