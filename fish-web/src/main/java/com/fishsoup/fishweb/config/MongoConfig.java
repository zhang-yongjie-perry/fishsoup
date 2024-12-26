package com.fishsoup.fishweb.config;

import com.fishsoup.fishweb.converter.*;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.host}")
    private String mongodbHost;

    @Override
    protected String getDatabaseName() {
        return "fish";
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory factory) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, new MongoMappingContext());
        converter.setCustomConversions(customConversions());
        converter.afterPropertiesSet();
        return new MongoTemplate(factory, converter);
    }

    @Bean
    @Override
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converterList = new ArrayList<>();
        converterList.add(new CreationClassifyEnumToStringConverter());
        converterList.add(new VisibleRangeEnumToStringConverters());
        converterList.add(new StringToObjConverter());
        converterList.add(new StringToCreationClassifyEnumConverter());
        converterList.add(new StringToVisibleRangeEnumConverters());
        return new MongoCustomConversions(converterList);
    }

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        builder.applyToClusterSettings(builder1 -> {
            builder1.hosts(Collections.singletonList(new ServerAddress(mongodbHost, 27017)));
        });
    }
}
