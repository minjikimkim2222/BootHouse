package likelion.eight.domain.review.controller.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
// 리뷰 생성 시 사용하는 DTO
public class ReviewCreateRequest {
    private String title;
    private String oneLineReview;
    private String advantages;
    private String disadvantages;
    private String instructorEvaluation;
    private Integer rating;
}
