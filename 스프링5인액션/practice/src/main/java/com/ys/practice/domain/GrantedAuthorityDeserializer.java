package com.ys.practice.domain;

import java.io.IOException;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class GrantedAuthorityDeserializer extends JsonDeserializer<GrantedAuthority> {
	@Override
	public GrantedAuthority deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		JsonNode node = p.getCodec()
						 .readTree(p);
		final var authority1 = node.get("authority");
		String authority = authority1 == null ? "" : authority1.asText();

		return new SimpleGrantedAuthority(!Objects.equals(authority, "") ? authority : "ROLE_USER");
	}
}
