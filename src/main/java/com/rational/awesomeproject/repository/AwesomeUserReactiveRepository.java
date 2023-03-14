package com.rational.awesomeproject.repository;

import com.rational.awesomeproject.repository.model.AwesomeUser;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AwesomeUserReactiveRepository extends ReactiveMongoRepository<AwesomeUser, String> {
	Mono<AwesomeUser> findByUsername(String userName);
}
