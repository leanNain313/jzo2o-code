package com.jzo2o.customer.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.common.expcetions.BadRequestException;
import com.jzo2o.common.expcetions.ForbiddenOperationException;
import com.jzo2o.customer.mapper.ChatMessageMapper;
import com.jzo2o.customer.mapper.ChatSessionMapper;
import com.jzo2o.customer.model.domain.ChatMessage;
import com.jzo2o.customer.model.domain.ChatSession;
import com.jzo2o.customer.model.dto.request.ChatMessageRecallReqDTO;
import com.jzo2o.customer.model.dto.request.ChatMessageSendReqDTO;
import com.jzo2o.customer.model.dto.request.ChatMessageScrollQueryReqDTO;
import com.jzo2o.customer.model.dto.response.ChatMessageListResDTO;
import com.jzo2o.customer.model.dto.response.ChatMessageSendResDTO;
import com.jzo2o.customer.service.IChatMessageService;
import com.jzo2o.mvc.utils.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息服务实现类
 */
@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements IChatMessageService {

    /**
     * 滚动查询默认条数
     */
    private static final long SCROLL_SIZE = 20L;

    /**
     * 用户角色
     */
    private static final int ROLE_USER = 1;

    /**
     * 服务人员角色
     */
    private static final int ROLE_STAFF = 2;

    /**
     * 文本消息
     */
    private static final int MSG_TYPE_TEXT = 1;

    /**
     * 图片消息
     */
    private static final int MSG_TYPE_IMAGE = 2;

    /**
     * 撤回时间限制（分钟）
     */
    private static final long RECALL_LIMIT_MINUTES = 2L;

    /**
     * 会话最后一条消息展示长度
     */
    private static final int LAST_MESSAGE_MAX_LENGTH = 500;

    @Resource
    private ChatSessionMapper chatSessionMapper;

    /**
     * 发送消息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChatMessageSendResDTO sendMessage(ChatMessageSendReqDTO reqDTO) {
        ChatSession chatSession = chatSessionMapper.selectById(reqDTO.getSessionId());
        if (ObjectUtil.isNull(chatSession)) {
            throw new BadRequestException("会话不存在");
        }

        if (ObjectUtil.notEqual(reqDTO.getRole(), ROLE_USER) && ObjectUtil.notEqual(reqDTO.getRole(), ROLE_STAFF)) {
            throw new BadRequestException("发送角色只能为1或2");
        }

        if (ObjectUtil.notEqual(reqDTO.getMsgType(), MSG_TYPE_TEXT) && ObjectUtil.notEqual(reqDTO.getMsgType(), MSG_TYPE_IMAGE)) {
            throw new BadRequestException("消息类型只能为1或2");
        }

        Long currentUserId = UserContext.currentUserId();
        Long creatorId;
        if (ObjectUtil.equal(reqDTO.getRole(), ROLE_USER)) {
            creatorId = chatSession.getUserId();
            if (ObjectUtil.notEqual(currentUserId, creatorId)) {
                throw new ForbiddenOperationException("当前用户无权以用户角色发送消息");
            }
        } else {
            creatorId = chatSession.getStaffId();
            if (ObjectUtil.notEqual(currentUserId, creatorId)) {
                throw new ForbiddenOperationException("当前用户无权以服务人员角色发送消息");
            }
        }

        LocalDateTime now = LocalDateTime.now();

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSessionId(reqDTO.getSessionId());
        chatMessage.setRole(reqDTO.getRole());
        chatMessage.setMsgType(reqDTO.getMsgType());
        chatMessage.setContent(reqDTO.getContent());
        chatMessage.setCreatorId(creatorId);
        chatMessage.setCreatedAt(now);
        save(chatMessage);

        ChatSession updateSession = new ChatSession();
        updateSession.setId(chatSession.getId());
        updateSession.setLastTime(now);
        updateSession.setLastMessage(buildLastMessage(reqDTO.getContent()));
        chatSessionMapper.updateById(updateSession);

        ChatMessageSendResDTO resDTO = new ChatMessageSendResDTO();
        resDTO.setMessageId(chatMessage.getId());
        resDTO.setContent(chatMessage.getContent());
        resDTO.setCreatedAt(chatMessage.getCreatedAt());
        resDTO.setMsgType(chatMessage.getMsgType());
        resDTO.setRole(chatMessage.getRole());
        return resDTO;
    }

    /**
     * 撤回消息（两分钟内可撤回）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recallMessage(ChatMessageRecallReqDTO reqDTO) {
        ChatMessage chatMessage = getById(reqDTO.getMessageId());
        if (ObjectUtil.isNull(chatMessage)) {
            throw new BadRequestException("消息不存在");
        }

        Long currentUserId = UserContext.currentUserId();
        if (ObjectUtil.notEqual(currentUserId, chatMessage.getCreatorId())) {
            throw new ForbiddenOperationException("只能撤回自己发送的消息");
        }

        LocalDateTime now = LocalDateTime.now();
        if (chatMessage.getCreatedAt().plusMinutes(RECALL_LIMIT_MINUTES).isBefore(now)) {
            throw new ForbiddenOperationException("消息发送超过两分钟，无法撤回");
        }

        removeById(reqDTO.getMessageId());

        refreshSessionLastMessage(chatMessage.getSessionId());
    }

    /**
     * 根据消息时间滚动查询消息记录
     */
    @Override
    public List<ChatMessageListResDTO> scrollList(ChatMessageScrollQueryReqDTO reqDTO) {
        ChatSession chatSession = chatSessionMapper.selectById(reqDTO.getSessionId());
        if (ObjectUtil.isNull(chatSession)) {
            throw new BadRequestException("会话不存在");
        }

        Long currentUserId = UserContext.currentUserId();
        if (ObjectUtil.notEqual(currentUserId, chatSession.getUserId())
                && ObjectUtil.notEqual(currentUserId, chatSession.getStaffId())) {
            throw new ForbiddenOperationException("无权查询该会话消息");
        }

        Page<ChatMessage> page = new Page<>(1, SCROLL_SIZE, false);
        LambdaQueryWrapper<ChatMessage> queryWrapper = Wrappers.<ChatMessage>lambdaQuery()
                .eq(ChatMessage::getSessionId, reqDTO.getSessionId())
                .lt(ObjectUtil.isNotNull(reqDTO.getLastTime()), ChatMessage::getCreatedAt, reqDTO.getLastTime())
                .orderByDesc(ChatMessage::getCreatedAt)
                .orderByDesc(ChatMessage::getId);

        Page<ChatMessage> messagePage = baseMapper.selectPage(page, queryWrapper);
        if (ObjectUtil.isEmpty(messagePage) || ObjectUtil.isEmpty(messagePage.getRecords())) {
            return Collections.emptyList();
        }

        LocalDateTime userReadLast = chatSession.getUserReadLastTime();
        LocalDateTime staffReadLast = chatSession.getStaffReadLastTime();
        boolean staffViewing = ObjectUtil.equal(currentUserId, chatSession.getStaffId());
        boolean userViewing = ObjectUtil.equal(currentUserId, chatSession.getUserId());

        return messagePage.getRecords().stream().map(message -> {
            ChatMessageListResDTO resDTO = new ChatMessageListResDTO();
            resDTO.setMessageId(message.getId());
            resDTO.setSessionId(message.getSessionId());
            resDTO.setContent(message.getContent());
            resDTO.setCreatedAt(message.getCreatedAt());
            resDTO.setMsgType(message.getMsgType());
            resDTO.setRole(message.getRole());
            resDTO.setCreatorId(message.getCreatorId());
            if (staffViewing && ObjectUtil.equal(message.getRole(), ROLE_STAFF)) {
                resDTO.setPeerRead(userReadLast != null && !message.getCreatedAt().isAfter(userReadLast));
            } else if (userViewing && ObjectUtil.equal(message.getRole(), ROLE_USER)) {
                resDTO.setPeerRead(staffReadLast != null && !message.getCreatedAt().isAfter(staffReadLast));
            }
            return resDTO;
        }).collect(Collectors.toList());
    }

    /**
     * 刷新会话最后一条消息
     */
    private void refreshSessionLastMessage(Long sessionId) {
        ChatSession chatSession = chatSessionMapper.selectById(sessionId);
        if (ObjectUtil.isNull(chatSession)) {
            return;
        }

        LambdaQueryWrapper<ChatMessage> queryWrapper = Wrappers.<ChatMessage>lambdaQuery()
                .eq(ChatMessage::getSessionId, sessionId)
                .orderByDesc(ChatMessage::getCreatedAt)
                .orderByDesc(ChatMessage::getId)
                .last("limit 1");
        ChatMessage latestMessage = baseMapper.selectOne(queryWrapper);

        ChatSession updateSession = new ChatSession();
        updateSession.setId(sessionId);
        if (ObjectUtil.isNull(latestMessage)) {
            updateSession.setLastMessage(null);
            updateSession.setLastTime(null);
        } else {
            updateSession.setLastMessage(buildLastMessage(latestMessage.getContent()));
            updateSession.setLastTime(latestMessage.getCreatedAt());
        }
        chatSessionMapper.updateById(updateSession);
    }

    /**
     * 构建会话摘要消息
     */
    private String buildLastMessage(String content) {
        if (ObjectUtil.isEmpty(content)) {
            return content;
        }
        if (content.length() <= LAST_MESSAGE_MAX_LENGTH) {
            return content;
        }
        return content.substring(0, LAST_MESSAGE_MAX_LENGTH);
    }
}
