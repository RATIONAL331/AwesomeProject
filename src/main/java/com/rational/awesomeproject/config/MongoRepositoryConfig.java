package com.rational.awesomeproject.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.rational.awesomeproject.repository.converter.OffsetDateTimeReadConverter;
import com.rational.awesomeproject.repository.converter.OffsetDateTimeWriteConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import java.util.Arrays;

@Configuration
@EnableReactiveMongoRepositories(basePackages = "com.rational.awesomeproject")
public class MongoRepositoryConfig extends AbstractReactiveMongoConfiguration {
	@Bean
	public MongoClient mongoClient() {
		return MongoClients.create("mongodb://localhost:27017");
	}

	@Bean
	public ReactiveMongoTemplate reactiveMongoTemplate() {
		return new ReactiveMongoTemplate(this.mongoClient(), this.getDatabaseName());
	}

	@Override
	protected String getDatabaseName() {
		return "awesome";
	}

	@Override
	public MongoCustomConversions customConversions() {
		return new MongoCustomConversions(Arrays.asList(
				new OffsetDateTimeReadConverter(),
				new OffsetDateTimeWriteConverter()
		));
	}
}
