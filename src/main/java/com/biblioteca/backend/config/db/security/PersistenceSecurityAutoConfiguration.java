package com.biblioteca.backend.config.db.security;

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
        entityManagerFactoryRef = "securityEntityManagerFactory",
        transactionManagerRef = "securityTransactionManager",
        basePackages = {"com.biblioteca.backend.repository.security"})
public class PersistenceSecurityAutoConfiguration {

    @Bean(name = "securityDataSource")
    @ConfigurationProperties(prefix = "spring.second-datasource")
    public DataSource dataSource() {
        return DataSourceBuilder
                .create()
                .build();
    }

    @Bean(name = "securityEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean securityEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("securityDataSource") DataSource dataSource) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("spring.jpa.database-platform", "org.hibernate.dialect.MySQL5InnoDBDialect");
        properties.put("spring.jpa.hibernate.ddl-auto", "validate");
        return builder
                .dataSource(dataSource)
                .properties(properties)
                .packages("com.biblioteca.backend.model.Acceso", "com.biblioteca.backend.model.Empresa",
                        "com.biblioteca.backend.model.Persona", "com.biblioteca.backend.model.Rol",
                        "com.biblioteca.backend.model.Sistema", "com.biblioteca.backend.model.Token",
                        "com.biblioteca.backend.model.Usuario")
                .build();
    }

    @Bean(name = "securityTransactionManager")
    public PlatformTransactionManager securityTransactionManager(
            @Qualifier("securityEntityManagerFactory") EntityManagerFactory securityEntityManagerFactory) {
        return new JpaTransactionManager(securityEntityManagerFactory);
    }
}
