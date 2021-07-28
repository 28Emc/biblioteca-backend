package com.biblioteca.backend.config.db.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "personaEntityManagerFactory",
        transactionManagerRef = "personaTransactionManager", basePackages = "com.biblioteca.backend.model")
public class PersistencePersonaAutoConfiguration {

    @Bean(name = "personaDataSource")
    @ConfigurationProperties(prefix = "spring.second-datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "personaEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean personaEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("personaDataSource") DataSource dataSource) {
        return builder.dataSource(dataSource).packages("com.biblioteca.backend.model").persistenceUnit("tb_persona")
                .build();
    }

    @Bean(name = "personaTransactionManager")
    public PlatformTransactionManager personaTransactionManager(
            @Qualifier("personaEntityManagerFactory") EntityManagerFactory personaEntityManagerFactory) {
        return new JpaTransactionManager(personaEntityManagerFactory);
    }
}
