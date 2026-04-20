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
import com.jzo2o.customer.model.domain.CommonUser;
import com.jzo2o.customer.model.domain.ServeProvider;
import com.jzo2o.customer.model.dto.request.ChatSessionCreateReqDTO;
import com.jzo2o.customer.model.dto.request.ChatSessionDeleteReqDTO;
import com.jzo2o.customer.model.dto.request.ChatSessionReadReportReqDTO;
import com.jzo2o.customer.model.dto.request.ChatSessionScrollQueryReqDTO;
import com.jzo2o.customer.model.dto.response.ChatSessionListResDTO;
import com.jzo2o.customer.model.dto.response.ChatSessionReadStateResDTO;
import com.jzo2o.customer.service.IChatSessionService;
import com.jzo2o.customer.service.ICommonUserService;
import com.jzo2o.customer.service.IServeProviderService;
import com.jzo2o.mvc.utils.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 会话服务实现类
 */
@Service
public class ChatSessionServiceImpl extends ServiceImpl<ChatSessionMapper, ChatSession> implements IChatSessionService {

    /**
     * 滚动查询默认条数
     */
    private static final long SCROLL_SIZE = 20L;

    /**
     * 用户角色（消息 role）
     */
    private static final int ROLE_USER = 1;

    /**
     * 服务人员角色（消息 role）
     */
    private static final int ROLE_STAFF = 2;

    @Resource
    private ICommonUserService commonUserService;

    @Resource
    private IServeProviderService serveProviderService;

    @Resource
    private ChatMessageMapper chatMessageMapper;

    /**
     * 添加会话
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSession(ChatSessionCreateReqDTO reqDTO) {
        Long currentUserId = UserContext.currentUserId();
        boolean allowedUser = ObjectUtil.equal(currentUserId, reqDTO.getUserId());
        boolean allowedStaff = ObjectUtil.equal(currentUserId, reqDTO.getStaffId());
        if (!allowedUser && !allowedStaff) {
            throw new ForbiddenOperationException("无权创建该会话");
        }

        CommonUser commonUser = commonUserService.getById(reqDTO.getUserId());
        if (ObjectUtil.isNull(commonUser)) {
            throw new BadRequestException("用户不存在");
        }

        ServeProvider serveProvider = serveProviderService.getById(reqDTO.getStaffId());
        if (ObjectUtil.isNull(serveProvider)) {
            throw new BadRequestException("服务人员不存在");
        }

        ChatSession existSession = lambdaQuery()
                .eq(ChatSession::getUserId, reqDTO.getUserId())
                .eq(ChatSession::getStaffId, reqDTO.getStaffId())
                .one();
        if (ObjectUtil.isNotNull(existSession)) {
            return existSession.getId();
        }

        ChatSession chatSession = new ChatSession();
        chatSession.setUserId(reqDTO.getUserId());
        chatSession.setStaffId(reqDTO.getStaffId());
        chatSession.setUserImage(commonUser.getAvatar());
        chatSession.setUserName(commonUser.getNickname());
        chatSession.setStaffImage(serveProvider.getAvatar());
        chatSession.setStaffName(serveProvider.getName());
        chatSession.setLastTime(LocalDateTime.now());
        chatSession.setLastMessage(null);
        save(chatSession);
        return chatSession.getId();
    }

    /**
     * 根据会话id删除会话
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBySessionId(ChatSessionDeleteReqDTO reqDTO) {
        ChatSession chatSession = getById(reqDTO.getSessionId());
        if (ObjectUtil.isNull(chatSession)) {
            return;
        }

        Long currentUserId = UserContext.currentUserId();
        if (ObjectUtil.notEqual(currentUserId, chatSession.getUserId())
                && ObjectUtil.notEqual(currentUserId, chatSession.getStaffId())) {
            throw new ForbiddenOperationException("无权删除该会话");
        }

        removeById(reqDTO.getSessionId());

        chatMessageMapper.delete(Wrappers.<com.jzo2o.customer.model.domain.ChatMessage>lambdaQuery()
                .eq(com.jzo2o.customer.model.domain.ChatMessage::getSessionId, reqDTO.getSessionId()));
    }

    /**
     * 根据最后一条消息时间滚动查询会话列表
     */
    @Override
    public List<ChatSessionListResDTO> scrollList(ChatSessionScrollQueryReqDTO reqDTO) {
        Long currentUserId = UserContext.currentUserId();
        boolean matchUser = ObjectUtil.isNotNull(reqDTO.getUserId())
                && ObjectUtil.equal(currentUserId, reqDTO.getUserId());
        boolean matchStaff = ObjectUtil.isNotNull(reqDTO.getStaffId())
                && ObjectUtil.equal(currentUserId, reqDTO.getStaffId());
        if (!matchUser && !matchStaff) {
            throw new ForbiddenOperationException("无权查询会话列表");
        }

        Page<ChatSession> page = new Page<>(1, SCROLL_SIZE, false);

        LambdaQueryWrapper<ChatSession> queryWrapper = Wrappers.<ChatSession>lambdaQuery()
                .eq(matchUser, ChatSession::getUserId, reqDTO.getUserId())
                .eq(matchStaff && !matchUser, ChatSession::getStaffId, reqDTO.getStaffId())
                .lt(ObjectUtil.isNotNull(reqDTO.getLastTime()), ChatSession::getLastTime, reqDTO.getLastTime())
                .orderByDesc(ChatSession::getLastTime)
                .orderByDesc(ChatSession::getId);

        Page<ChatSession> sessionPage = baseMapper.selectPage(page, queryWrapper);
        if (ObjectUtil.isEmpty(sessionPage) || ObjectUtil.isEmpty(sessionPage.getRecords())) {
            return Collections.emptyList();
        }

        boolean listAsStaff = matchStaff && !matchUser;

        return sessionPage.getRecords().stream().map(session -> {
            ChatSessionListResDTO resDTO = new ChatSessionListResDTO();
            resDTO.setSessionId(session.getId());
            resDTO.setUserId(session.getUserId());
            resDTO.setUserImage(session.getUserImage());
            resDTO.setUserName(session.getUserName());
            resDTO.setStaffId(session.getStaffId());
            resDTO.setStaffImage(session.getStaffImage());
            resDTO.setStaffName(session.getStaffName());
            resDTO.setLastMessage(session.getLastMessage());
            resDTO.setLastTime(session.getLastTime());
            resDTO.setUserReadLastTime(session.getUserReadLastTime());
            resDTO.setStaffReadLastTime(session.getStaffReadLastTime());
            if (listAsStaff) {
                resDTO.setUnreadCount(countUnreadUserMessages(session.getId(), session.getStaffReadLastTime()));
            } else {
                resDTO.setUnreadCount(countUnreadStaffMessages(session.getId(), session.getUserReadLastTime()));
            }
            return resDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public ChatSessionReadStateResDTO getReadState(Long sessionId) {
        ChatSession session = getById(sessionId);
        if (ObjectUtil.isNull(session)) {
            throw new BadRequestException("会话不存在");
        }
        Long currentUserId = UserContext.currentUserId();
        if (ObjectUtil.notEqual(currentUserId, session.getUserId())
                && ObjectUtil.notEqual(currentUserId, session.getStaffId())) {
            throw new ForbiddenOperationException("无权查看该会话");
        }
        ChatSessionReadStateResDTO dto = new ChatSessionReadStateResDTO();
        dto.setSessionId(session.getId());
        dto.setUserReadLastTime(session.getUserReadLastTime());
        dto.setStaffReadLastTime(session.getStaffReadLastTime());
        if (ObjectUtil.equal(currentUserId, session.getStaffId())) {
            dto.setUnreadCount(countUnreadUserMessages(session.getId(), session.getStaffReadLastTime()));
        } else {
            dto.setUnreadCount(countUnreadStaffMessages(session.getId(), session.getUserReadLastTime()));
        }
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reportRead(ChatSessionReadReportReqDTO reqDTO) {
        ChatSession session = getById(reqDTO.getSessionId());
        if (ObjectUtil.isNull(session)) {
            throw new BadRequestException("会话不存在");
        }
        Long currentUserId = UserContext.currentUserId();
        LocalDateTime incoming = reqDTO.getLastReadMessageCreatedAt();

        ChatSession update = new ChatSession();
        update.setId(session.getId());

        if (ObjectUtil.equal(currentUserId, session.getStaffId())) {
            LocalDateTime cur = session.getStaffReadLastTime();
            LocalDateTime next = maxTime(cur, incoming);
            update.setStaffReadLastTime(next);
            updateById(update);
            return;
        }
        if (ObjectUtil.equal(currentUserId, session.getUserId())) {
            LocalDateTime cur = session.getUserReadLastTime();
            LocalDateTime next = maxTime(cur, incoming);
            update.setUserReadLastTime(next);
            updateById(update);
            return;
        }
        throw new ForbiddenOperationException("无权上报该会话已读");
    }

    private static LocalDateTime maxTime(LocalDateTime a, LocalDateTime b) {
        if (ObjectUtil.isNull(a)) {
            return b;
        }
        if (ObjectUtil.isNull(b)) {
            return a;
        }
        return a.isBefore(b) ? b : a;
    }

    /**
     * 客服视角：用户发来、且晚于客服已读游标的消息条数
     */
    private long countUnreadUserMessages(Long sessionId, LocalDateTime staffReadLastTime) {
        LambdaQueryWrapper<ChatMessage> qw = Wrappers.<ChatMessage>lambdaQuery()
                .eq(ChatMessage::getSessionId, sessionId)
                .eq(ChatMessage::getRole, ROLE_USER);
        if (ObjectUtil.isNotNull(staffReadLastTime)) {
            qw.gt(ChatMessage::getCreatedAt, staffReadLastTime);
        }
        return chatMessageMapper.selectCount(qw);
    }

    /**
     * 用户视角：客服发来、且晚于用户已读游标的消息条数
     */
    private long countUnreadStaffMessages(Long sessionId, LocalDateTime userReadLastTime) {
        LambdaQueryWrapper<ChatMessage> qw = Wrappers.<ChatMessage>lambdaQuery()
                .eq(ChatMessage::getSessionId, sessionId)
                .eq(ChatMessage::getRole, ROLE_STAFF);
        if (ObjectUtil.isNotNull(userReadLastTime)) {
            qw.gt(ChatMessage::getCreatedAt, userReadLastTime);
        }
        return chatMessageMapper.selectCount(qw);
    }
}
