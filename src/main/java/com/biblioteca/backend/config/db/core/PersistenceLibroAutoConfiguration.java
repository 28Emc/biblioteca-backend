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
@EnableJpaRepositories(entityManagerFactoryRef = "libroEntityManagerFactory",
        transactionManagerRef = "libroTransactionManager", basePackages = "com.biblioteca.backend.model.Libro")
public class PersistenceLibroAutoConfiguration {

    @Bean(name = "libroDataSource")
    @ConfigurationProperties(prefix = "spring.second-datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "libroEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean libroEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("libroDataSource") DataSource dataSource) {
        return builder.dataSource(dataSource).packages("com.biblioteca.backend.model.Libro").persistenceUnit("tb_libro")
                .build();
    }

    @Bean(name = "libroTransactionManager")
    public PlatformTransactionManager libroTransactionManager(
            @Qualifier("libroEntityManagerFactory") EntityManagerFactory libroEntityManagerFactory) {
        return new JpaTransactionManager(libroEntityManagerFactory);
    }
}
