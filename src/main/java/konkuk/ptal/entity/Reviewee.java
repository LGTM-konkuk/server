package konkuk.ptal.entity;

import jakarta.persistence.*;
import konkuk.ptal.dto.request.CreateRevieweeRequest;
import konkuk.ptal.util.StringListConverter;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="reviewees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reviewee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, name="display_name")
    private String displayName;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> preferences;

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

    public static Reviewee createReviewee(User user, CreateRevieweeRequest dto){
        return Reviewee.builder()
                .user(user)
                .displayName(dto.getDisplayName())
                .preferences(dto.getPreferences())
                .build();
    }

}
