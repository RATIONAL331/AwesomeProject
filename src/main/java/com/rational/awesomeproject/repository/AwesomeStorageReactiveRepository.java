package com.rational.awesomeproject.repository;

import com.rational.awesomeproject.repository.model.AwesomeStorage;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AwesomeStorageReactiveRepository extends ReactiveMongoRepository<AwesomeStorage, String> {
}
