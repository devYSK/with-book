package org.example.chap7;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ArrayPartition {

	public int arrayPairSum(int[] nums) {
		int sum = 0;
		List<Integer> pair = new ArrayList<>();
		Arrays.sort(nums);

		// 앞에서부터 오름차순으로 반복
		for (int n : nums) {
			pair.add(n);
			// 페어 변수에 값이 2개 채워지면 min()을 합산하고 변수 초기화
			if (pair.size() == 2) {
				sum += Collections.min(pair);
				pair.clear();
			}
		}
		return sum;
	}

	public int arrayPairSum2(int[] nums) {
		int sum = 0;
		Arrays.sort(nums);

		// 앞에서부터 오름차순으로 인덱스 반복
		for (int i = 0; i < nums.length; i++) {
			// 짝수 번째일때 값의 합 계산
			if (i % 2 == 0)
				sum += nums[i];
		}
		return sum;
	}
}
