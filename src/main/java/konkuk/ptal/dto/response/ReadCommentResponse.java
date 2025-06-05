package konkuk.ptal.dto.response;

import konkuk.ptal.domain.AuthorInfo;
import konkuk.ptal.domain.enums.ReviewCommentType;
import konkuk.ptal.entity.ReviewComment;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class ReadCommentResponse {
    String id;
    Long sessionId;
    String content;
    String filepath;
    AuthorInfo author;
    String parentCommentId;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public static ReadCommentResponse from(ReviewComment comment) {
        ReadCommentResponseBuilder builder = ReadCommentResponse.builder()
                .id(comment.getId())
                .sessionId(comment.getReviewSubmission().getId())
                .content(comment.getContent())
                .author(AuthorInfo.builder()
                        .id(comment.getUser().getId())
                        .name(comment.getUser().getName())
                        .email(comment.getUser().getEmail())
                        .build())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt());

        if (comment.getCommentType() == ReviewCommentType.CODE_COMMENT) {
            builder.filepath(comment.getCodeFile().getRelativePath());
        }

        if (comment.getParentComment() != null) {
            builder.parentCommentId(comment.getParentComment().getId());
        }

        return builder.build();
    }

    public static List<ReadCommentResponse> from(List<ReviewComment> comments) {
        if (comments == null) {
            return new ArrayList<>();
        }
        return comments.stream()
                .map(ReadCommentResponse::from)
                .collect(Collectors.toList());
    }
}
