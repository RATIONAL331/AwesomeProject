package com.rational.awesomeproject.repository;

import com.rational.awesomeproject.repository.model.Storage;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface StorageReactiveRepository extends ReactiveMongoRepository<Storage, String> {
}
