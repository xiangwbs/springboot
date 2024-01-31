package com.xwbing.service.demo.sse;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Date;

/**
 * @author daofeng
 * @version $
 * @since 2024年01月26日 5:20 PM
 */
@Slf4j
public class SseEventSourceListener extends EventSourceListener {
    private final SseDao sseDao;
    @Getter
    private final SseEmitter sseEmitter = new SseEmitter(0L);
    private final SseChatDTO dto;

    public SseEventSourceListener(SseDao sseDao, SseChatDTO dto) {
        this.sseDao = sseDao;
        this.dto = dto;
        Long requestId = dto.getRequestId();
        sseEmitter.onError(throwable -> log.error("sseEvent sseEmitter requestId:{} error", requestId, throwable));
        sseEmitter.onTimeout(() -> log.info("sseEvent sseEmitter requestId:{} timeout", requestId));
        sseEmitter.onCompletion(() -> log.info("sseEvent sseEmitter requestId:{} complete", requestId));
    }

    @Override
    public void onOpen(EventSource eventSource, Response response) {
        // 记录响应时间
        dto.setResponseDate(new Date());
        log.info("sseEvent onOpen requestId:{}", dto.getRequestId());
    }

    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        try {
            log.info("sseEvent onEvent requestId:{} data:{}", dto.getRequestId(), data);
            sseEmitter.send(data);
            // 记录响应数据
            dto.setChatResult(data);
        } catch (Exception e) {
            // 关闭eventSource
            eventSource.cancel();
        }
    }

    @Override
    public void onClosed(EventSource eventSource) {
        log.info("sseEvent onClosed requestId:{}", dto.getRequestId());
        // 数据传输完成 保存响应数据
        sseDao.saveResponse(dto);
        // 关闭sseEmitter
        sseEmitter.complete();
    }

    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response) {
        log.info("sseEvent onFailure requestId:{} error:{}", dto.getRequestId(), t != null ? ExceptionUtils.getStackTrace(t) : response.message());
        // 数据传输过程失败(中断等) 也要保存响应数据
        sseDao.saveResponse(dto);
        // 关闭sseEmitter
        sseEmitter.complete();
    }
}
