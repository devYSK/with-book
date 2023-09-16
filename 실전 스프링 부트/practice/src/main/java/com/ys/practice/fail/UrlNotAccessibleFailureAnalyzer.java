package com.ys.practice.fail;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

public class UrlNotAccessibleFailureAnalyzer extends AbstractFailureAnalyzer<UrlNotAccessibleException> {

	@Override
	protected FailureAnalysis analyze(Throwable rootFailure, UrlNotAccessibleException cause) {
		return new FailureAnalysis("액세스 할 수 없습니다. URL : " + cause.getUrl(),
			"URL의 유효성을 검사하고 액세스 가능한지 확인하세요", cause);
	}

}
