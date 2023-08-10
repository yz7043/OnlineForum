package org.forum.messageservice.entity;

import lombok.*;

import javax.persistence.*;
import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Integer message_id;

    @Column(name = "user_id")
    private Integer user_id;

    @Column(name = "email")
    private String email;

    @Column(name = "message")
    private String message;

    @Column(name = "date_created")
    private Date date_created;

    @Column(name = "status")
    private String status;

    @Column(name = "subject")
    private String subject;
}
