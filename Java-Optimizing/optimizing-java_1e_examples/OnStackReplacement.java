package optjava;

public class OnStackReplacement {
	// method called once
	public static void main(String[] args) {
		java.util.Random r = new java.util.Random();

		long sum = 0;

		// first long-running loop
		for (int i = 0; i < 1_000_000; i++) {
			sum += r.nextInt(100);
		}
		
		// second long-running loop
		for (int i = 0; i < 1_000_000; i++) {
			sum += r.nextInt(100);
		}

		System.out.println(sum);
	}
}
