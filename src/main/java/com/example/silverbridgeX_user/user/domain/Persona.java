package com.example.silverbridgeX_user.user.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import org.hibernate.type.SqlTypes;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "persona")
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

    private String tone;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> preferredKeywords;

    @Column(name = "preferred_embedding", columnDefinition = "vector(384)")
    private String preferredEmbedding;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> dislikeKeywords;

}
