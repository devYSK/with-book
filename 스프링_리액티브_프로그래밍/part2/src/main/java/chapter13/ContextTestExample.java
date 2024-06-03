package chapter13;

import java.util.Base64;

import reactor.core.publisher.Mono;

public class ContextTestExample {
	public static Mono<String> getSecretMessage(Mono<String> keySource) {
		return keySource
			.zipWith(Mono.deferContextual(ctx ->
				Mono.just((String)ctx.get("secretKey"))))
			.filter(tp ->
				tp.getT1().equals(decode(tp.getT2()))
			)
			.transformDeferredContextual(
				(mono, ctx) -> mono.map(notUse -> ctx.get("secretMessage"))
			);
	}

	private static String decode(String key) {
		return new String(Base64.getDecoder()
			.decode(key));
	}

}
