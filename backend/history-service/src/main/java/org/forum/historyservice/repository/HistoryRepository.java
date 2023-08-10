package org.forum.historyservice.repository;

import org.forum.historyservice.dto.request.ViewRequest;
import org.forum.historyservice.entity.History;
import org.forum.historyservice.security.JwtProvider;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Repository
public class HistoryRepository extends BaseRepository {
    private JwtProvider jwtProvider;

    @Autowired
    public HistoryRepository(SessionFactory sessionFactory, JwtProvider jwtProvider) {
        super(sessionFactory);
        this.jwtProvider = jwtProvider;
    }

    public void createHistory(ViewRequest dto, String token) {
        Session session = sessionFactory.getCurrentSession();
        int user_id = jwtProvider.extractUserID(token);
        String hql = "FROM History WHERE user_id = :user_id AND post_id = :post_id";
        Query query = session.createQuery(hql);
        query.setParameter("user_id", user_id);
        query.setParameter("post_id", dto.getPost_id());
        List<History> list = query.getResultList();
        if(list == null || list.size() == 0){
            History history = History.builder()
                    .user_id(user_id)
                    .post_id(dto.getPost_id())
                    .viewdate(Date.valueOf(LocalDate.now()))
                    .build();
            session.save(history);
        }
        else{
            History history = list.get(0);
            history.setViewdate(Date.valueOf(LocalDate.now()));
        }
    }

    public List<History> getHistoryByUserID(String token) {
        Session session = sessionFactory.getCurrentSession();
        int user_id = jwtProvider.extractUserID(token);
        String hql = "FROM History WHERE user_id = :user_id ORDER BY viewdate DESC";
        Query query = session.createQuery(hql);
        query.setParameter("user_id", user_id);
        return query.getResultList();
    }
}
