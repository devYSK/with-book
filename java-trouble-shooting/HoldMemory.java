import java.util.HashMap;

public class HoldMemory {
	private final static HashMap<String, String> leakMap = new HashMap<>();
	private final static String STORE_DATA = "STORE DATA";

	public static void main(String[] args) {
		HoldMemory holdMemory = new HoldMemory();
		holdMemory.addObject(50000);
		try {
			System.out.println("Holding memory. It will be stopped after 10 min.");
			Thread.sleep(60000); // Wait for 10 min
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public void addObject(int objectCount) {
		int mapSize = leakMap.size();
		int maxCount = mapSize + objectCount;
		for (int loop = mapSize; loop < maxCount; loop++) {
			leakMap.put(STORE_DATA + loop, STORE_DATA);
		}

	}
}