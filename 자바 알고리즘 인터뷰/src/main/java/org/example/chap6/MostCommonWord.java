package org.example.chap6;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MostCommonWord {

	public String mostCommonWord(String paragraph, String[] banned) {
		// 금지어 목록이 String 배열이므로, 비교 메소드를 제공하는 Set으로 변경한다.
		Set<String> ban = new HashSet<>(Arrays.asList(banned));
		// 각 단어별 개수가 저장될 키-값 맵
		Map<String, Integer> counts = new HashMap<>();

		// 전처리 작업 후 단어 목록을 배열로 저장
		String[] words = paragraph.replaceAll("\\W+", " ").toLowerCase().split(" ");

		for (String w : words) {
			// 금지된 단어가 아닌 경우 개수 처리
			if (!ban.contains(w)) {
				// 존재하지 않는 단어라면 기본값을 0으로 지정. 추출한 값에 +1하여 저장
				counts.put(w, counts.getOrDefault(w, 0) + 1);
			}
		}
		// 가장 흔하게 등장하는 단어 추출
		return Collections.max(counts.entrySet(), Map.Entry.comparingByValue()).getKey();
	}

}
