package com.jzo2o.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jzo2o.customer.model.domain.ChatSession;
import com.jzo2o.customer.model.dto.request.ChatSessionCreateReqDTO;
import com.jzo2o.customer.model.dto.request.ChatSessionDeleteReqDTO;
import com.jzo2o.customer.model.dto.request.ChatSessionReadReportReqDTO;
import com.jzo2o.customer.model.dto.request.ChatSessionScrollQueryReqDTO;
import com.jzo2o.customer.model.dto.response.ChatSessionListResDTO;
import com.jzo2o.customer.model.dto.response.ChatSessionReadStateResDTO;

import java.util.List;

/**
 * 会话服务
 */
public interface IChatSessionService extends IService<ChatSession> {

    /**
     * 添加会话
     *
     * @param reqDTO 请求参数
     */
    Long createSession(ChatSessionCreateReqDTO reqDTO);

    /**
     * 根据会话id删除会话
     *
     * @param reqDTO 请求参数
     */
    void deleteBySessionId(ChatSessionDeleteReqDTO reqDTO);

    /**
     * 根据最后一条消息时间滚动查询会话列表
     *
     * @param reqDTO 请求参数
     * @return 会话列表
     */
    List<ChatSessionListResDTO> scrollList(ChatSessionScrollQueryReqDTO reqDTO);

    /**
     * 查询会话双方已读游标
     *
     * @param sessionId 会话 id
     */
    ChatSessionReadStateResDTO getReadState(Long sessionId);

    /**
     * 上报已读进度（客服更新 staffReadLastTime，用户更新 userReadLastTime）
     *
     * @param reqDTO 请求参数
     */
    void reportRead(ChatSessionReadReportReqDTO reqDTO);
}
