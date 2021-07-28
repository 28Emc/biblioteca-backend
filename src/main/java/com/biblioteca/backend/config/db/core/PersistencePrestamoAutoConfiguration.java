package com.biblioteca.backend.config.db.core;

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
@EnableJpaRepositories(entityManagerFactoryRef = "prestamoEntityManagerFactory",
        transactionManagerRef = "prestamoTransactionManager", basePackages = "com.biblioteca.backend.model.Prestamo")
public class PersistencePrestamoAutoConfiguration {

    @Bean(name = "prestamoDataSource")
    @ConfigurationProperties(prefix = "spring.second-datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "prestamoEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean prestamoEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("prestamoDataSource") DataSource dataSource) {
        return builder.dataSource(dataSource).packages("com.biblioteca.backend.model.Prestamo").persistenceUnit("tb_prestamo")
                .build();
    }

    @Bean(name = "prestamoTransactionManager")
    public PlatformTransactionManager prestamoTransactionManager(
            @Qualifier("prestamoEntityManagerFactory") EntityManagerFactory prestamoEntityManagerFactory) {
        return new JpaTransactionManager(prestamoEntityManagerFactory);
    }
}
