package com.rational.awesomeproject.repository;

import com.rational.awesomeproject.repository.model.AwesomeStorage;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AwesomeStorageReactiveRepository extends ReactiveMongoRepository<AwesomeStorage, String> {
	Flux<AwesomeStorage> findAllByParentStorageIdAndDeletedAtIsNull(String parentStorageId);

	Flux<AwesomeStorage> findAllByUserIdAndDeletedAtIsNull(String userId);

	Mono<AwesomeStorage> findByIdAndUserId(String id, String userId);
}
