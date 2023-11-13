package org.example.chap9;

import java.util.ArrayDeque;
import java.util.Deque;

public class DailyTemperatures {

	//매일의 온도 리스트를 받아서 따뜻한 날씨를 위해서는 며칠을 기다려야하는지 출력
	// 입력 temperatures = [23, 24, 25, 21, 19, 22, 26, 23]
	// 출력 [1, 1, 4, 2, 1, 1, 0, 0]

	public int[] dailyTemperatures(int[] temperatures) {
		int[] result = new int[temperatures.length];

		final Deque<Integer> stack = new ArrayDeque<>();

		for (int i = 0; i < temperatures.length; i++) {
			// 현재 온도가 스택에 있는 온도보다 높다면 꺼내서 결과를 업데이트 한다.
			while (!stack.isEmpty() && temperatures[i] > temperatures[stack.peek()]) {
				int last = stack.pop();

				result[last] = i - last;
			}

			stack.push(i);
		}

		return result;
	}

}
