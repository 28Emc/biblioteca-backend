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
@EnableJpaRepositories(entityManagerFactoryRef = "accesoEntityManagerFactory",
        transactionManagerRef = "accesoTransactionManager", basePackages = "com.biblioteca.backend.model")
public class PersistenceAccesoAutoConfiguration {

    @Bean(name = "accesoDataSource")
    @ConfigurationProperties(prefix = "spring.second-datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "accesoEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean accesoEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("accesoDataSource") DataSource dataSource) {
        return builder.dataSource(dataSource).packages("com.biblioteca.backend.model").persistenceUnit("tb_acceso")
                .build();
    }

    @Bean(name = "accesoTransactionManager")
    public PlatformTransactionManager accesoTransactionManager(
            @Qualifier("accesoEntityManagerFactory") EntityManagerFactory accesoEntityManagerFactory) {
        return new JpaTransactionManager(accesoEntityManagerFactory);
    }
}
