package konkuk.ptal.entity;

import jakarta.persistence.*;
import konkuk.ptal.dto.request.CreateReviewerRequest;
import konkuk.ptal.util.StringListConverter;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "reviewers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reviewer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String expertise;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String bio;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")  // 태그 길이가 가변적일 수 있으니 TEXT 권장
    private List<String> tags;

    @Column(nullable = false, updatable = false)
    LocalDateTime createdAt;

    @Column(nullable = false)
    LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public static Reviewer createReviewer(User user, CreateReviewerRequest dto){
        return Reviewer.builder()
                .user(user)
                .expertise(dto.getExpertise())
                .bio(dto.getBio())
                .tags(dto.getTags())
                .build();
    }
}