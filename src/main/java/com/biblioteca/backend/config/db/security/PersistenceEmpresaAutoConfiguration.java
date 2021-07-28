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
@EnableJpaRepositories(entityManagerFactoryRef = "empresaEntityManagerFactory",
        transactionManagerRef = "empresaTransactionManager", basePackages = "com.biblioteca.backend.model")
public class PersistenceEmpresaAutoConfiguration {

    @Bean(name = "empresaDataSource")
    @ConfigurationProperties(prefix = "spring.second-datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "empresaEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean empresaEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("empresaDataSource") DataSource dataSource) {
        return builder.dataSource(dataSource).packages("com.biblioteca.backend.model").persistenceUnit("tb_empresa")
                .build();
    }

    @Bean(name = "empresaTransactionManager")
    public PlatformTransactionManager empresaTransactionManager(
            @Qualifier("empresaEntityManagerFactory") EntityManagerFactory empresaEntityManagerFactory) {
        return new JpaTransactionManager(empresaEntityManagerFactory);
    }
}
