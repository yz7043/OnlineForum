package org.forum.messageservice.repository;

import org.forum.messageservice.dto.request.ContactUsRequest;
import org.forum.messageservice.entity.Message;
import org.forum.messageservice.exceptions.MessageNotFoundException;
import org.forum.messageservice.security.JwtProvider;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;


@Repository
public class MessageRepository extends BaseRepository {
    private JwtProvider jwtProvider;

    @Autowired
    public MessageRepository(SessionFactory sessionFactory, JwtProvider jwtProvider) {
        super(sessionFactory);
        this.jwtProvider = jwtProvider;
    }

    public void createMessage(ContactUsRequest dto, String token) {
        Session session = sessionFactory.getCurrentSession();
        int user_id = jwtProvider.extractUserID(token);
        Message message = Message.builder()
                .user_id(user_id)
                .email(dto.getEmail())
                .message(dto.getMessage())
                .date_created(Date.valueOf(LocalDate.now()))
                .status("Open")
                .subject(dto.getSubject())
                .build();
        session.save(message);
    }

    public List<Message> getAllMessages() {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Message> cr = cb.createQuery(Message.class);
        Root<Message> root = cr.from(Message.class);
        cr.select(root);

        return session.createQuery(cr).getResultList();
    }

    public void changeStatus(int message_id) {
        Session session = sessionFactory.getCurrentSession();
        Message message = session.get(Message.class, message_id);
        if(message == null) throw new MessageNotFoundException("Message not found!");
        if(message.getStatus().equals("Open")) message.setStatus("Close");
        else if(message.getStatus().equals("Close")) message.setStatus("Open");
    }
}
