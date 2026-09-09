package se.lexicon.lexiconwsecommercejpa.entity;

import jakarta.persistence.*;
import lombok.*;

// these replacing the class boilerplate
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 100)
    private String nickName;

    @Column(nullable = false, length = 100)
    private String phoneNumber;

    @Column(length = 500)
    private String bio;

    @OneToOne(mappedBy = "profile")
    private Customer customer;

}