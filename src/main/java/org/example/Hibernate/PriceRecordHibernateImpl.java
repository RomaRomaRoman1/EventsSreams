package org.example.Hibernate;

import org.example.entity.PriceRecord;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManagerFactory;
import java.util.List;

@Repository
@EnableTransactionManagement
public class PriceRecordHibernateImpl {
    //Поле sessionFactory для внедрение
    private final SessionFactory sessionFactory;
    //Использование конструктора для внедрения зависимости EntityManagerFactory и сразу же преобразование его в
    //SessionFactory. Поэтому поле EntityManagerFactory нам не нужно
    @Autowired
    public PriceRecordHibernateImpl(EntityManagerFactory entityManagerFactory) {
        this.sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
    }
    @Transactional
    public void addAllFromXml(List<PriceRecord> records) {
        Session session = sessionFactory.getCurrentSession();
        Transaction tx = session.beginTransaction();
        int batchSize = 50;

        try {
            for (int i = 0; i < records.size(); i++) {
                session.save(records.get(i));
                if (i % batchSize == 0) {
                    session.flush();
                    session.clear();
                }
            }
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }
    }
}
