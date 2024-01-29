package com.xwbing.service.demo.sse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author daofeng
 * @version $
 * @since 2024年01月26日 5:30 PM
 */
@Service
public class SseDao {
    public void saveResponse(SseChatDTO dto) {
        if (dto.getChatResult() != null) {
            // 保存数据
        }
    }

    public void saveRequest(SseChatDTO dto) {
        // 保存数据
        dto.setRequestId(0L);
    }

    public Long saveSession(String title) {
        if (StringUtils.isNotEmpty(title) && title.length() > 20) {
            title = title.substring(0, 20);
        }
        // 修改数据
        return 0L;
    }

    public void updateSessionTitle(Long sessionId, String title) {
        if (title.length() > 20) {
            title = title.substring(0, 20);
        }
        // 修改数据
    }

    public String getBySessionId(Long sessionId) {
        // 获取数据
        return null;
    }

    public String getDirect(String question) {
        // 获取数据
        return null;
    }
}