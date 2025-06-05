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
    private ReviewSubmission sessionId;

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

    public static CodeFile createCodeFile(ReviewSubmission reviewSubmission, String relativePath){
        return CodeFile.builder()
                .sessionId(reviewSubmission)
                .relativePath(relativePath)
                .fileType(getFileExtension(relativePath))
                .build();
    }
    private static String getFileExtension(String filePath) {
        int dotIndex = filePath.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filePath.length() - 1) {
            return filePath.substring(dotIndex + 1);
        }
        return "unknown";
    }
}
