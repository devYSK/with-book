package com.ys.practice;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

// @SpringBootTest
class PracticeApplicationTests {

	@Test
	void test() {

		Flux<String> flux = Flux.just(
			"Apple", "Orange", "Graph"
		);

		StepVerifier.create(flux)
					.expectNext("Apple")
					.expectNext("Orange")
					.expectNext("Graph2")
					.verifyComplete();
	}

	@Test
	void fromArray() {
		String[] strings = new String[] {
			"Apple", "Orange", "Graph"
		};

		Stream<String> stream = Stream.of("Apple", "Orange", "Graph");

		Flux<String> flux = Flux.fromArray(strings);
		StepVerifier.create(flux)
					.expectNext("Apple")
					.expectNext("Orange")
					.expectNext("Graph")
					.verifyComplete();
	}

	@Test
	void test3() {

		final var range = Flux.range(1, 5);

		range.subscribe(System.out::println);
	}

	@DisplayName("")
	@Test
	void interval() {
		Flux<Long> take = Flux.interval(Duration.ofSeconds(1))
							  .take(5);

		StepVerifier.create(take)
					.expectNext(0L)
					.expectNext(1L)
					.expectNext(2L)
					.expectNext(3L)
					.expectNext(4L)
					.verifyComplete();
	}

	@Test
	void mergeFluxes() {

		Flux<String> characterFlux = Flux
			.just("Garfield", "Kojak", "Barbossa")
			.delayElements(Duration.ofMillis(500));

		Flux<String> foodFlux = Flux
			.just("Lasagna", "Lollipops", "Apples")
			.delaySubscription(Duration.ofMillis(250))
			.delayElements(Duration.ofMillis(500));

		Flux<String> mergedFlux = characterFlux.mergeWith(foodFlux);

		StepVerifier.create(mergedFlux)
					.expectNext("Garfield")
					.expectNext("Lasagna")
					.expectNext("Kojak")
					.expectNext("Lollipops")
					.expectNext("Barbossa")
					.expectNext("Apples")
					.verifyComplete();
	}

	@Test
	public void zipFluxes() {
		Flux<String> characterFlux = Flux
			.just("Garfield", "Kojak", "Barbossa");
		Flux<String> foodFlux = Flux
			.just("Lasagna", "Lollipops", "Apples");

		Flux<Tuple2<String, String>> zippedFlux =
			Flux.zip(characterFlux, foodFlux);

		StepVerifier.create(zippedFlux)
					.expectNextMatches(p ->
						p.getT1()
						 .equals("Garfield") &&
							p.getT2()
							 .equals("Lasagna"))
					.expectNextMatches(p ->
						p.getT1()
						 .equals("Kojak") &&
							p.getT2()
							 .equals("Lollipops"))
					.expectNextMatches(p ->
						p.getT1()
						 .equals("Barbossa") &&
							p.getT2()
							 .equals("Apples"))
					.verifyComplete();
	}

	@Test
	public void zipFluxesToObject() {
		Flux<String> characterFlux = Flux
			.just("Garfield", "Kojak", "Barbossa");
		Flux<String> foodFlux = Flux
			.just("Lasagna", "Lollipops", "Apples");

		Flux<String> zippedFlux =
			Flux.zip(characterFlux, foodFlux, (c, f) -> c + " eats " + f);

		StepVerifier.create(zippedFlux)
					.expectNext("Garfield eats Lasagna")
					.expectNext("Kojak eats Lollipops")
					.expectNext("Barbossa eats Apples")
					.verifyComplete();
	}

	@Test
	public void firstFlux() {
		// delay needed to "slow down" the slow Flux

		Flux<String> slowFlux = Flux.just("tortoise", "snail", "sloth")
									.delaySubscription(Duration.ofMillis(100));
		Flux<String> fastFlux = Flux.just("hare", "cheetah", "squirrel");

		Flux<String> firstFlux = Flux.firstWithSignal(slowFlux, fastFlux);

		StepVerifier.create(firstFlux)
					.expectNext("hare")
					.expectNext("cheetah")
					.expectNext("squirrel")
					.verifyComplete();
	}

	@Test
	public void skipAFew() {
		Flux<String> countFlux = Flux.just(
										 "one", "two", "skip a few", "ninety nine", "one hundred")
									 .skip(3);

		StepVerifier.create(countFlux)
					.expectNext("ninety nine", "one hundred")
					.verifyComplete();
	}

	@Test
	public void skipAFewSeconds() {
		Flux<String> countFlux = Flux.just(
										 "one", "two", "skip a few", "ninety nine", "one hundred")
									 .delayElements(Duration.ofSeconds(1))
									 .skip(Duration.ofSeconds(4));

		StepVerifier.create(countFlux)
					.expectNext("ninety nine", "one hundred")
					.verifyComplete();
	}

	@Test
	public void take() {
		Flux<String> nationalParkFlux = Flux.just(
												"Yellowstone", "Yosemite", "Grand Canyon", "Zion", "Acadia")
											.take(3);

		StepVerifier.create(nationalParkFlux)
					.expectNext("Yellowstone", "Yosemite", "Grand Canyon")
					.verifyComplete();
	}

	@Test
	public void takeForAwhile() {
		Flux<String> nationalParkFlux = Flux.just(
												"Yellowstone", "Yosemite", "Grand Canyon", "Zion", "Grand Teton")
											.delayElements(Duration.ofSeconds(1))
											.take(Duration.ofMillis(3500));

		StepVerifier.create(nationalParkFlux)
					.expectNext("Yellowstone", "Yosemite", "Grand Canyon")
					.verifyComplete();
	}

	@Test
	public void filter() {
		Flux<String> nationalParkFlux = Flux.just(
												"Yellowstone", "Yosemite", "Grand Canyon", "Zion", "Grand Teton")
											.filter(np -> !np.contains(" "));

		StepVerifier.create(nationalParkFlux)
					.expectNext("Yellowstone", "Yosemite", "Zion")
					.verifyComplete();
	}

	@Test
	public void distinct() {
		Flux<String> animalFlux = Flux.just(
										  "dog", "cat", "bird", "dog", "bird", "anteater")
									  .distinct();

		StepVerifier.create(animalFlux)
					.expectNext("dog", "cat", "bird", "anteater")
					.verifyComplete();
	}

	@Test
	public void map() {
		Flux<Player> playerFlux = Flux
			.just("Michael Jordan", "Scottie Pippen", "Steve Kerr")
			.map(n -> {
				String[] split = n.split("\\s");
				return new Player(split[0], split[1]);
			});

		StepVerifier.create(playerFlux)
					.expectNext(new Player("Michael", "Jordan"))
					.expectNext(new Player("Scottie", "Pippen"))
					.expectNext(new Player("Steve", "Kerr"))
					.verifyComplete();
	}

	@Test
	public void flatMap() {
		Flux<Player> playerFlux = Flux
			.just("Michael Jordan", "Scottie Pippen", "Steve Kerr")
			.flatMap(n -> Mono.just(n)
							  .map(p -> {
								  String[] split = p.split("\\s");
								  return new Player(split[0], split[1]);
							  })
							  .subscribeOn(Schedulers.parallel())
			);

		List<Player> playerList = Arrays.asList(
			new Player("Michael", "Jordan"),
			new Player("Scottie", "Pippen"),
			new Player("Steve", "Kerr"));

		StepVerifier.create(playerFlux)
					.expectNextMatches(playerList::contains)
					.expectNextMatches(playerList::contains)
					.expectNextMatches(playerList::contains)
					.verifyComplete();
	}

	// @Data
	private record Player(String firstName, String lastName) {
	}

	@Test
	public void buffer() {
		Flux<String> fruitFlux = Flux.just(
			"apple", "orange", "banana", "kiwi", "strawberry");

		Flux<List<String>> bufferedFlux = fruitFlux.buffer(3);

		bufferedFlux.subscribe(System.out::println);

		StepVerifier
			.create(bufferedFlux)
			.expectNext(Arrays.asList("apple", "orange", "banana"))
			.expectNext(Arrays.asList("kiwi", "strawberry"))
			.verifyComplete();
	}

	@Test
	public void bufferAndFlatMap() throws Exception {
		Flux.just(
				"apple", "orange", "banana", "kiwi", "strawberry")
			.buffer(3)
			.flatMap(x ->
				Flux.fromIterable(x)
					.map(String::toUpperCase)
					.subscribeOn(Schedulers.parallel())
					.log()
			).subscribe();
	}

	@Test
	public void collectList() {
		Flux<String> fruitFlux = Flux.just(
			"apple", "orange", "banana", "kiwi", "strawberry");

		Mono<List<String>> fruitListMono = fruitFlux.collectList();

		StepVerifier
			.create(fruitListMono)
			.expectNext(Arrays.asList(
				"apple", "orange", "banana", "kiwi", "strawberry"))
			.verifyComplete();
	}

	@Test
	public void collectMap() {
		Flux<String> animalFlux = Flux.just(
			"aardvark", "elephant", "koala", "eagle", "kangaroo");

		Mono<Map<Character, String>> animalMapMono =
			animalFlux.collectMap(a -> a.charAt(0));

		StepVerifier
			.create(animalMapMono)
			.expectNextMatches(map -> {
				return
					map.size() == 3 &&
						map.get('a').equals("aardvark") &&
						map.get('e').equals("eagle") &&
						map.get('k').equals("kangaroo");
			})
			.verifyComplete();
	}

	@Test
	public void all() {
		Flux<String> animalFlux = Flux.just(
			"aardvark", "elephant", "koala", "eagle", "kangaroo");

		Mono<Boolean> hasAMono = animalFlux.all(a -> a.contains("a"));
		StepVerifier.create(hasAMono)
					.expectNext(true)
					.verifyComplete();

		Mono<Boolean> hasKMono = animalFlux.all(a -> a.contains("k"));
		StepVerifier.create(hasKMono)
					.expectNext(false)
					.verifyComplete();
	}

	@Test
	public void any() {
		Flux<String> animalFlux = Flux.just(
			"aardvark", "elephant", "koala", "eagle", "kangaroo");

		Mono<Boolean> hasAMono = animalFlux.any(a -> a.contains("a"));

		StepVerifier.create(hasAMono)
					.expectNext(true)
					.verifyComplete();

		Mono<Boolean> hasZMono = animalFlux.any(a -> a.contains("z"));
		StepVerifier.create(hasZMono)
					.expectNext(false)
					.verifyComplete();
	}
}
