package org.example.chap9;

public class RemoveDuplicateLetters {

	// 각 문자의 등장 횟수를 저장할 배열
	private int[] count = new int[26];
	// 해당 문자가 스택에 있는지 여부를 저장할 배열
	private boolean[] inStack = new boolean[26];
	// 문자들을 저장할 스택
	private StringBuilder stack = new StringBuilder();

	public String removeDuplicateLetters(String s) {

		// 문자열에서 각 문자의 등장 횟수 계산
		for (char c : s.toCharArray()) {
			count[c - 'a']++;
		}

		for (char c : s.toCharArray()) {
			// 현재 문자의 등장 횟수를 하나 줄임
			count[c - 'a']--;

			// 스택에 이미 있는 문자라면 다음 문자로 넘어감
			if (inStack[c - 'a']) {
				continue;
			}

			// 스택의 top 문자가 현재 문자보다 사전 순서에서 뒤에 있고, 뒤에 다시 나타나는 경우 스택에서 제거
			while (shouldPop(c)) {
				inStack[stack.charAt(stack.length() - 1) - 'a'] = false;
				stack.deleteCharAt(stack.length() - 1);
			}

			// 현재 문자를 스택에 추가
			stack.append(c);
			inStack[c - 'a'] = true;
		}

		return stack.toString();
	}

	// 스택에서 문자를 제거해야 하는지를 결정하는 조건을 체크하는 메소드
	private boolean shouldPop(char c) {
		return !stack.isEmpty()
			&& stack.charAt(stack.length() - 1) > c
			&& count[stack.charAt(stack.length() - 1) - 'a'] > 0;
	}

}
