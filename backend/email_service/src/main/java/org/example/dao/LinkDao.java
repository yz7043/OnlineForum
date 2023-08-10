package org.example.dao;

import org.example.domain.Link;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.sql.Time;
import java.sql.Timestamp;

@Repository
@Transactional
public class LinkDao extends AbstractHibernateDao<Link> {
    public LinkDao() {
        setClazz(Link.class);
    }
    public Link getLinkByEmail(String email) {
        return (Link) this.getCurrentSession().createQuery("from Link where email = :email")
                .setParameter("email", email)
                .uniqueResult();
    }
    public void addLink(String email, String code) {
        Timestamp sentTime = new Timestamp(System.currentTimeMillis());
        Link link = new Link();
        link.setEmail(email);
        link.setCode(code);
        link.setSentTime(sentTime);
        this.add(link);
    }

    public boolean isLinkExist(String email) {
        return this.getCurrentSession().createQuery("from Link where email = :email")
                .setParameter("email", email)
                .uniqueResult() != null;
    }

    public void updateTime(String email) {
        Timestamp sentTime = new Timestamp(System.currentTimeMillis());
        Link link = this.getLinkByEmail(email);
        link.setSentTime(sentTime);
        this.getCurrentSession().update(link);
    }

    public boolean checkIfExpired(String email) {
        // current time - sent time < 3 hour
        Timestamp sentTime = this.getLinkByEmail(email).getSentTime();
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        return currentTime.getTime() - sentTime.getTime() < 3 * 60 * 60 * 1000;
    }

    public boolean isUuidExist(String uuid) {
        return this.getCurrentSession().createQuery("from Link where code = :uuid")
                .setParameter("uuid", uuid)
                .uniqueResult() != null;
    }

    public Link getLinkByUuid(String uuid) {
        return (Link) this.getCurrentSession().createQuery("from Link where code = :uuid")
                .setParameter("uuid", uuid)
                .uniqueResult();
    }
}
