package org.example.chap6;
public class LongPalindrome {

	public String longestPalindrome(String s) {
		// 입력 문자열의 유효성 검사
		if (s == null || s.length() < 2) {
			return s;
		}

		int start = 0, end = 0;

		for (int i = 0; i < s.length(); i++) {
			System.out.println("\n중심으로 사용하는 문자: " + s.charAt(i));

			int len1 = expandAroundCenter(s, i, i);  // 홀수 길이 펠린드롬
			int len2 = expandAroundCenter(s, i, i + 1);  // 짝수 길이 펠린드롬

			System.out.println("홀수 길이 펠린드롬 길이: " + len1);
			System.out.println("짝수 길이 펠린드롬 길이: " + len2);

			int len = Math.max(len1, len2);

			if (len > end - start) {
				start = i - (len - 1) / 2;
				end = i + len / 2;

				System.out.println("새로 발견한 가장 긴 펠린드롬: " + s.substring(start, end + 1));
			}
		}

		return s.substring(start, end + 1);
	}

	private int expandAroundCenter(String s, int left, int right) {
		while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
			left--;
			right++;
		}

		// 펠린드롬 길이 반환
		return right - left - 1;
	}

	public static void main(String[] args) {
		LongPalindrome solver = new LongPalindrome();

		String[] examples = {"babad", "cbbd", "abacdfgdcabba", "abacdfgdcaba"};

		for (String example : examples) {
			System.out.println("\n입력 문자열: " + example);
			System.out.println("결과: " + solver.longestPalindrome(example));
			System.out.println("----------------------");
		}
	}
}
