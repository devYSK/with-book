package org.example.chap9;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ValidParentheses {

	private static final Map<Character, Character> table = new HashMap<>();

	static {
		table.put(')', '(');
		table.put('}', '{');
		table.put(']', '[');
	}

	public boolean isValid(String s) {
		final Deque<Character> stack = new ArrayDeque<>();

		for (int i = 0; i < s.length(); i++) {
			if (!table.containsKey(s.charAt(i))) { // 닫힌 괄호가 아닌경우 push
				stack.push(s.charAt(i));
			} else if (stack.isEmpty() || !Objects.equals(table.get(s.charAt(i)), stack.pop())) {
				return false;
			}
		}

		return stack.size() == 0;
	}
}
