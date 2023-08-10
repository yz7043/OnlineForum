package org.forum.historyservice.entity;

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
@Table(name = "history")
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Integer history_id;

    @Column(name = "user_id")
    private Integer user_id;

    @Column(name = "post_id")
    private String post_id;

    @Column(name = "viewdate")
    private Date viewdate;
}
