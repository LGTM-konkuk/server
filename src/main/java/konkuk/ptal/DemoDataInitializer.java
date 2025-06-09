package konkuk.ptal;

import konkuk.ptal.domain.UserPrincipal;
import konkuk.ptal.dto.request.*;
import konkuk.ptal.entity.*;
import konkuk.ptal.service.IAuthenticationService;
import konkuk.ptal.service.IReviewService;
import konkuk.ptal.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("demo") // demo 프로필에서만 실행
public class DemoDataInitializer implements ApplicationRunner {

    // 서비스 의존성 주입
    private final IUserService userService;
    private final IReviewService reviewService;

    // =================================
    // 🔧 설정 값들 - 여기서 쉽게 수정 가능
    // =================================

    // 대기 시간 설정
    private static final int INITIALIZATION_DELAY_MS = 3000;

    // 리뷰어 계정 설정
    private static final String REVIEWER_EMAIL = "reviewer@demo.com";
    private static final String REVIEWER_PASSWORD = "password123";
    private static final String REVIEWER_NAME = "스벅에서 밤새고있는 리뷰어";
    private static final List<String> REVIEWER_PREFERENCES = Arrays.asList("Java", "Spring Boot", "React");
    private static final String REVIEWER_BIO = "10년차 풀스택 개발자입니다. Java/Spring과 React를 주로 사용합니다.";
    private static final List<String> REVIEWER_TAGS = Arrays.asList("backend", "frontend", "fullstack");

    // 리뷰이 계정 설정
    private static final String REVIEWEE_EMAIL = "reviewee@demo.com";
    private static final String REVIEWEE_PASSWORD = "password123";
    private static final String REVIEWEE_NAME = "일감호 위에 떠다니는 리뷰이";
    private static final List<String> REVIEWEE_PREFERENCES = Arrays.asList("Java", "Spring Boot", "MySQL");

    // 리뷰 요청 설정
    private static final String REVIEW_GIT_URL = "https://github.com/LGTM-konkuk/server";
    private static final String REVIEW_BRANCH = "main";
    private static final String REVIEW_REQUEST_DETAILS = "Spring Boot 프로젝트의 핵심 기능 구현 부분을 리뷰해주세요. 특히 자동 설정과 스타터 모듈 부분에 대한 피드백을 부탁드립니다.";

    // 댓글 설정
    private static final String SESSION_COMMENT_CONTENT = "프로젝트 전반적으로 잘 구성되어 있네요! 특히 Spring Boot의 자동 설정 부분이 인상적입니다. 몇 가지 세부사항에 대해 코멘트 남겨드릴게요.";
    private static final String CODE_COMMENT_CONTENT = "이 부분의 예외 처리를 좀 더 구체적으로 하면 어떨까요? 현재는 일반적인 Exception을 catch하고 있는데, 특정 예외 타입별로 다른 처리를 하면 더 좋을 것 같습니다.";
    private static final String CODE_FILE_PATH = "src/main/java/konkuk/ptal/PtalApplication.java";
    private static final int CODE_LINE_NUMBER = 10;
    private static final String REPLY_COMMENT_CONTENT = "피드백 감사합니다! 말씀해주신 예외 처리 부분은 다음 버전에서 반영하겠습니다. 구체적으로 어떤 예외 타입들을 고려하면 좋을지 조언해주실 수 있나요?";

    // =================================
    // 📊 상태 변수들
    // =================================
    private Reviewer demoReviewer;
    private Reviewee demoReviewee;
    private UserPrincipal reviewerPrincipal;
    private UserPrincipal revieweePrincipal;
    private ReviewSubmission demoSubmission;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("=== MVP 시연용 데모 데이터 초기화 시작 ===");

        try {
            // 잠시 대기 (애플리케이션 완전 시작 대기)
            Thread.sleep(INITIALIZATION_DELAY_MS);

            // 1. 리뷰어 회원가입
            createReviewer();

            // 2. 리뷰이 회원가입
            createReviewee();

            // 3. UserPrincipal 객체 생성 (로그인 시뮬레이션)
            createUserPrincipals();

            // 4. 리뷰 요청 생성
            createReviewSubmission();

            // 5. 리뷰어가 리뷰 작성
            createReview();

            // 6. 댓글 작성
            createComments();

            // 7. 답글 작성
            createReplies();

            logCompletionSummary();

        } catch (Exception e) {
            log.error("데모 데이터 초기화 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    // =================================
    // 🔧 리뷰어 관련 메서드들
    // =================================

    /**
     * 리뷰어 계정 생성
     */
    private void createReviewer() {
        try {
            CreateReviewerRequest request = buildReviewerRequest();
            demoReviewer = userService.registerReviewer(request);
            log.info("✅ 리뷰어 회원가입 성공 - ID: {}, User ID: {}", demoReviewer.getId(), demoReviewer.getUser().getId());
        } catch (Exception e) {
            log.error("❌ 리뷰어 회원가입 실패: {}", e.getMessage());
        }
    }

    /**
     * 리뷰어 요청 객체 생성
     */
    private CreateReviewerRequest buildReviewerRequest() {
        return CreateReviewerRequest.builder().email(REVIEWER_EMAIL).password(REVIEWER_PASSWORD).name(REVIEWER_NAME).preferences(REVIEWER_PREFERENCES).bio(REVIEWER_BIO).tags(REVIEWER_TAGS).build();
    }

    // =================================
    // 🔧 리뷰이 관련 메서드들
    // =================================

    /**
     * 리뷰이 계정 생성
     */
    private void createReviewee() {
        try {
            CreateRevieweeRequest request = buildRevieweeRequest();
            demoReviewee = userService.registerReviewee(request);
            log.info("✅ 리뷰이 회원가입 성공 - ID: {}, User ID: {}", demoReviewee.getId(), demoReviewee.getUser().getId());
        } catch (Exception e) {
            log.error("❌ 리뷰이 회원가입 실패: {}", e.getMessage());
        }
    }

    /**
     * 리뷰이 요청 객체 생성
     */
    private CreateRevieweeRequest buildRevieweeRequest() {
        return CreateRevieweeRequest.builder().email(REVIEWEE_EMAIL).password(REVIEWEE_PASSWORD).name(REVIEWEE_NAME).preferences(REVIEWEE_PREFERENCES).build();
    }

    /**
     * UserPrincipal 객체들 생성 (로그인 시뮬레이션)
     */
    private void createUserPrincipals() {
        try {
            // 리뷰어 UserPrincipal 생성
            reviewerPrincipal = UserPrincipal.create(demoReviewer.getUser());
            log.info("✅ 리뷰어 UserPrincipal 생성 성공");

            // 리뷰이 UserPrincipal 생성
            revieweePrincipal = UserPrincipal.create(demoReviewee.getUser());
            log.info("✅ 리뷰이 UserPrincipal 생성 성공");

        } catch (Exception e) {
            log.error("❌ UserPrincipal 생성 실패: {}", e.getMessage());
        }
    }

    /**
     * 리뷰 요청 생성
     */
    private void createReviewSubmission() {
        try {
            CreateReviewSubmissionRequest request = buildReviewSubmissionRequest();
            demoSubmission = reviewService.createReviewSubmission(request, revieweePrincipal);
            log.info("✅ 리뷰 요청 생성 성공 - Submission ID: {}", demoSubmission.getId());
        } catch (Exception e) {
            log.error("❌ 리뷰 요청 생성 실패: {}", e.getMessage());
        }
    }

    /**
     * 리뷰 요청 객체 생성
     */
    private CreateReviewSubmissionRequest buildReviewSubmissionRequest() {
        CreateReviewSubmissionRequest request = new CreateReviewSubmissionRequest();
        request.setReviewerId(demoReviewer.getId());
        request.setGitUrl(REVIEW_GIT_URL);
        request.setBranch(REVIEW_BRANCH);
        request.setRequestDetails(REVIEW_REQUEST_DETAILS);
        return request;
    }

    // =================================
    // 🔧 리뷰 관련 메서드들
    // =================================

    /**
     * 리뷰 작성
     */
    private void createReview() {
        try {
            CreateReviewRequest request = buildReviewRequest();
            Review review = reviewService.createReview(demoSubmission.getId(), request, reviewerPrincipal);
            log.info("✅ 리뷰 작성 성공 - Review ID: {}", review.getId());
        } catch (Exception e) {
            log.error("❌ 리뷰 작성 실패: {}", e.getMessage());
        }
    }

    /**
     * 리뷰 요청 객체 생성
     */
    private CreateReviewRequest buildReviewRequest() {
        CreateReviewRequest request = new CreateReviewRequest();
        request.setReviewSubmissionId(demoSubmission.getId());
        request.setReviewContent(getReviewContent());
        return request;
    }

    /**
     * 리뷰 내용 생성 (긴 텍스트이므로 별도 메서드로 분리)
     */
    private String getReviewContent() {
        return """
                ## 전체적인 코드 리뷰 결과
                
                **전반적으로 잘 구현된 프로젝트입니다.** 👍
                
                ### 👍 좋은 점들
                - **명확한 구조**: 패키지 구조가 잘 정리되어 있어 코드 탐색이 용이합니다
                - **일관된 네이밍**: 변수명과 메서드명이 직관적이고 일관성 있게 작성되었습니다
                - **적절한 추상화**: 인터페이스와 구현체의 분리가 잘 되어 있습니다
                
                ### 🔧 개선 제안사항
                1. **예외 처리 강화**: 보다 구체적인 예외 처리가 필요한 부분들이 있습니다
                2. **테스트 코드 보완**: 단위 테스트와 통합 테스트를 추가하면 좋겠습니다
                3. **성능 최적화**: 일부 쿼리 최적화 여지가 있어 보입니다
                
                ### 📝 상세 피드백
                - **보안**: JWT 토큰 처리 부분이 잘 구현되어 있습니다
                - **데이터 검증**: 입력값 검증 로직이 적절히 적용되어 있습니다
                - **API 설계**: RESTful 설계 원칙을 잘 따르고 있습니다
                
                **전체 점수: 85/100** ⭐⭐⭐⭐⭐
                """;
    }

    // =================================
    // 🔧 댓글 관련 메서드들
    // =================================

    /**
     * 댓글들 작성
     */
    private void createComments() {
        try {
            // 세션 댓글 작성 (리뷰어가 작성)
            createSessionComment();

            // 코드 댓글 작성 (리뷰어가 작성)
            createCodeComment();

        } catch (Exception e) {
            log.error("❌ 댓글 작성 실패: {}", e.getMessage());
        }
    }

    /**
     * 세션 댓글 작성 (파일과 관련없는 전체 댓글)
     */
    private void createSessionComment() {
        try {
            CreateReviewCommentRequest sessionComment = new CreateReviewCommentRequest();
            sessionComment.setContent(SESSION_COMMENT_CONTENT);

            ReviewComment comment = reviewService.createReviewComment(demoSubmission.getId(), sessionComment, reviewerPrincipal);
            log.info("✅ 세션 댓글 작성 성공 - Comment ID: {}", comment.getId());
        } catch (Exception e) {
            log.error("❌ 세션 댓글 작성 실패: {}", e.getMessage());
        }
    }

    /**
     * 코드 댓글 작성
     */
    private void createCodeComment() {
        try {
            CreateReviewCommentRequest codeComment = buildCodeCommentRequest();

            ReviewComment comment = reviewService.createReviewComment(1L, codeComment, reviewerPrincipal);
            log.info("✅ 코드 댓글 작성 성공 - Comment ID: {}", comment.getId());
        } catch (Exception e) {
            log.error("❌ 코드 댓글 작성 실패: {}", e.getMessage());
        }
    }

    /**
     * 코드 댓글 요청 객체 생성
     */
    private CreateReviewCommentRequest buildCodeCommentRequest() {
        CreateReviewCommentRequest codeComment = new CreateReviewCommentRequest();
        codeComment.setContent(CODE_COMMENT_CONTENT);
        codeComment.setFilePath(CODE_FILE_PATH);
        codeComment.setLineNumber(CODE_LINE_NUMBER);
        return codeComment;
    }

    /**
     * 답글 작성
     */
    private void createReplies() {
        try {
            CreateReviewCommentRequest reply = buildReplyRequest();

            // 실제 댓글에 대한 답글을 작성하려면 댓글 ID가 필요하므로
            // 여기서는 답글 준비만 완료하고 로그로 표시
            log.info("✅ 답글 작성 준비 완료");
            log.info("💬 답글 내용: {}", REPLY_COMMENT_CONTENT);
            log.info("ℹ️ 실제 답글은 댓글 ID를 사용하여 createReviewComment에 parentCommentId를 설정하면 됩니다");

        } catch (Exception e) {
            log.error("❌ 답글 작성 실패: {}", e.getMessage());
        }
    }

    /**
     * 답글 요청 객체 생성
     */
    private CreateReviewCommentRequest buildReplyRequest() {
        CreateReviewCommentRequest reply = new CreateReviewCommentRequest();
        reply.setContent(REPLY_COMMENT_CONTENT);
        // reply.setParentCommentId(parentCommentId); // 실제 댓글 ID 필요
        return reply;
    }

    // =================================
    // 🔧 공통 유틸리티 메서드들
    // =================================

    /**
     * 완료 요약 로그 출력
     */
    private void logCompletionSummary() {
        log.info("=== MVP 시연용 데모 데이터 초기화 완료 ===");
        log.info("🔑 리뷰어 계정: {} / {}", REVIEWER_EMAIL, REVIEWER_PASSWORD);
        log.info("🔑 리뷰이 계정: {} / {}", REVIEWEE_EMAIL, REVIEWEE_PASSWORD);
        log.info("📋 리뷰 요청 ID: {}", demoSubmission != null ? demoSubmission.getId() : "생성 실패");
        log.info("👤 리뷰어 ID: {}", demoReviewer != null ? demoReviewer.getId() : "생성 실패");
        log.info("👤 리뷰이 ID: {}", demoReviewee != null ? demoReviewee.getId() : "생성 실패");
        log.info("🌐 애플리케이션 URL: http://localhost:8080");
        log.info("📊 H2 Console: http://localhost:8080/h2-console");
        log.info("📚 API Documentation: http://localhost:8080/swagger-ui/index.html");
    }
}

