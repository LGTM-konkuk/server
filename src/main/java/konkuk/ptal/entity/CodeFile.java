package konkuk.ptal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "code_files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codefile_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "review_session_id",nullable = false)
    private ReviewSession sessionId;

    @Column(nullable = false)
    private String relativePath;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
