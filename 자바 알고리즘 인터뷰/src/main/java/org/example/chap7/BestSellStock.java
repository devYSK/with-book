package org.example.chap7;

public class BestSellStock {
	public int maxProfit(int[] prices) {
		// 최대 이익은 0, 저점은 임시로 첫 번째 값으로 지정
		int maxProfit = 0, minPrice = prices[0];

		// 현재 값을 우측으로 차례대로 이동하면서 계산
		for (int price : prices) {
			System.out.println("min : " + minPrice + " max : " + maxProfit);
			// 지금까지 저점 계산
			minPrice = Math.min(minPrice, price);
			// 현재 값과 저점의 차이가 최대 이익인 경우 교체
			maxProfit = Math.max(maxProfit, price - minPrice);

		}

		return maxProfit;
	}

	public static void main(String[] args) {
		final var i = new BestSellStock()
			.maxProfit(new int[] {8, 1, 5, 3, 6, 4});

		System.out.println(i);
	}
}
