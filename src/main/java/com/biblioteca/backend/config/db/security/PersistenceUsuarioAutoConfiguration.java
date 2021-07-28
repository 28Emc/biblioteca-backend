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
@EnableJpaRepositories(entityManagerFactoryRef = "usuarioEntityManagerFactory",
        transactionManagerRef = "usuarioTransactionManager", basePackages = "com.biblioteca.backend.model.Usuario")
public class PersistenceUsuarioAutoConfiguration {

    @Bean(name = "usuarioDataSource")
    @ConfigurationProperties(prefix = "spring.second-datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "usuarioEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean usuarioEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("usuarioDataSource") DataSource dataSource) {
        return builder.dataSource(dataSource).packages("com.biblioteca.backend.model.Usuario").persistenceUnit("tb_usuario")
                .build();
    }

    @Bean(name = "usuarioTransactionManager")
    public PlatformTransactionManager usuarioTransactionManager(
            @Qualifier("usuarioEntityManagerFactory") EntityManagerFactory usuarioEntityManagerFactory) {
        return new JpaTransactionManager(usuarioEntityManagerFactory);
    }
}
