package com.ys.practice.webflux;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ys.practice.domain.Taco;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/design", produces = "application/json")
@CrossOrigin(origins = "*")
public class DesignTacoController {
	private TacoRepository tacoRepo;

	public DesignTacoController(TacoRepository tacoRepo) {
		this.tacoRepo = tacoRepo;
	}

	@GetMapping("/recent")
	public Flux<Taco> recentTacos() {
		return tacoRepo.findAll()
					   .take(12);
	}

	@PostMapping(consumes = "application/json")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Taco> postTaco(@RequestBody Taco taco) {
		return tacoRepo.save(taco);
	}

	@GetMapping("/{id}")
	public Mono<Taco> tacoById(@PathVariable("id") String id) {
		return tacoRepo.findById(id);
	}

}
