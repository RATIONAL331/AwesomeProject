package com.rational.awesomeproject.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(basePackages = "com.rational.awesomeproject")
public class MongoRepositoryConfig extends AbstractReactiveMongoConfiguration {
	@Value("${mongo.url}")
	private String mongoUrl;

	@Override
	public ReactiveMongoTemplate reactiveMongoTemplate(ReactiveMongoDatabaseFactory databaseFactory, MappingMongoConverter mongoConverter) {
		return new ReactiveMongoTemplate(reactiveMongoClient(), this.getDatabaseName());
	}

	@Override
	public MongoClient reactiveMongoClient() {
		return MongoClients.create(mongoUrl);
	}

	@Bean
	ReactiveMongoTransactionManager transactionManager(ReactiveMongoDatabaseFactory reactiveMongoDatabaseFactory) {
		return new ReactiveMongoTransactionManager(reactiveMongoDatabaseFactory);
	}

	@Override
	protected String getDatabaseName() {
		return "awesome";
	}
}
