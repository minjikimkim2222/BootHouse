package likelion.eight.domain.notice.model.request;

import lombok.*;

@Getter
@Setter
public class NoticeReq {
    private String title;
    private String content;
    private String postType;
    private Boolean importance;
}
