package org.example.Hibernate;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;


@Configuration//Указывает, что этот класс является классом конфигурации Spring.

@EnableTransactionManagement//Включает управление транзакциями в Spring.
public class HibernateConfig {

    @Bean
    // Метод, который создает и настраивает LocalSessionFactoryBean, который в свою очередь настраивает SessionFactory.
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setPackagesToScan("org.example.entity"); // Укажите ваш пакет с сущностями
        return sessionFactory;
    }

    //Метод, который создает и настраивает HibernateTransactionManager для управления транзакциями.
    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory);
        return transactionManager;
    }
}
