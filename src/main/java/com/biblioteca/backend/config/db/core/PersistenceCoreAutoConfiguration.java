package com.biblioteca.backend.config.db.core;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "coreEntityManagerFactory",
        transactionManagerRef = "coreTransactionManager",
        basePackages = {"com.biblioteca.backend.repository.core"})
public class PersistenceCoreAutoConfiguration {

    @Primary
    @Bean(name = "coreDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder
                .create()
                .build();
    }

    @Primary
    @Bean(name = "coreEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean coreEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("coreDataSource") DataSource dataSource) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("spring.jpa.database-platform", "org.hibernate.dialect.MySQL5InnoDBDialect");
        properties.put("spring.jpa.hibernate.ddl-auto", "validate");
        return builder
                .dataSource(dataSource)
                .properties(properties)
                .packages("com.biblioteca.backend.model.Categoria", "com.biblioteca.backend.model.Empleado",
                        "com.biblioteca.backend.model.Libro", "com.biblioteca.backend.model.Local",
                        "com.biblioteca.backend.model.Prestamo")
                .build();
    }

    @Primary
    @Bean(name = "coreTransactionManager")
    public PlatformTransactionManager coreTransactionManager(
            @Qualifier("coreEntityManagerFactory") EntityManagerFactory coreEntityManagerFactory) {
        return new JpaTransactionManager(coreEntityManagerFactory);
    }
}
