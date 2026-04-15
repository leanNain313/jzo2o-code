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
import com.jzo2o.customer.model.domain.ChatSession;
import com.jzo2o.customer.model.domain.CommonUser;
import com.jzo2o.customer.model.domain.ServeProvider;
import com.jzo2o.customer.model.dto.request.ChatSessionCreateReqDTO;
import com.jzo2o.customer.model.dto.request.ChatSessionDeleteReqDTO;
import com.jzo2o.customer.model.dto.request.ChatSessionScrollQueryReqDTO;
import com.jzo2o.customer.model.dto.response.ChatSessionListResDTO;
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
    public void createSession(ChatSessionCreateReqDTO reqDTO) {
        Long currentUserId = UserContext.currentUserId();
        if (ObjectUtil.notEqual(currentUserId, reqDTO.getUserId())) {
            throw new ForbiddenOperationException("无权为其他用户创建会话");
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
            return;
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
        if (ObjectUtil.notEqual(currentUserId, reqDTO.getUserId())) {
            throw new ForbiddenOperationException("无权查询其他用户会话");
        }

        Page<ChatSession> page = new Page<>(1, SCROLL_SIZE, false);

        LambdaQueryWrapper<ChatSession> queryWrapper = Wrappers.<ChatSession>lambdaQuery()
                .eq(ChatSession::getUserId, reqDTO.getUserId())
                .lt(ObjectUtil.isNotNull(reqDTO.getLastTime()), ChatSession::getLastTime, reqDTO.getLastTime())
                .orderByDesc(ChatSession::getLastTime)
                .orderByDesc(ChatSession::getId);

        Page<ChatSession> sessionPage = baseMapper.selectPage(page, queryWrapper);
        if (ObjectUtil.isEmpty(sessionPage) || ObjectUtil.isEmpty(sessionPage.getRecords())) {
            return Collections.emptyList();
        }

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
            return resDTO;
        }).collect(Collectors.toList());
    }
}
