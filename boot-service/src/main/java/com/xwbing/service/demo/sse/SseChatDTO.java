package com.xwbing.service.demo.sse;

import lombok.Data;

import java.util.Date;

/**
 * @author daofeng
 * @version SseEventDTO$
 * @since 2023年08月14日 4:22 PM
 */
@Data
public class SseChatDTO {
    private Long sessionId;
    private String question;
    private boolean isDirect;

    private Long requestId;
    private Date responseDate;
    private Object chatResult;
}