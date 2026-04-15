package com.jzo2o.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jzo2o.customer.model.domain.ChatMessage;
import com.jzo2o.customer.model.dto.request.ChatMessageRecallReqDTO;
import com.jzo2o.customer.model.dto.request.ChatMessageSendReqDTO;
import com.jzo2o.customer.model.dto.response.ChatMessageSendResDTO;

/**
 * 消息服务
 */
public interface IChatMessageService extends IService<ChatMessage> {

    /**
     * 发送消息
     *
     * @param reqDTO 请求参数
     * @return 消息回执
     */
    ChatMessageSendResDTO sendMessage(ChatMessageSendReqDTO reqDTO);

    /**
     * 撤回消息
     *
     * @param reqDTO 请求参数
     */
    void recallMessage(ChatMessageRecallReqDTO reqDTO);
}
