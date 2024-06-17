package org.example.Hibernate;

import org.example.entity.PriceRecord;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PriceRecordHibernateImpl {
    private final SessionFactory sessionFactory;
    // Внедрение SessionFactory с помощью Spring для управления сессиями Hibernate.
    @Autowired
    public PriceRecordHibernateImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

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
