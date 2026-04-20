package com.jzo2o.customer.controller.consumer;

import com.jzo2o.common.model.Result;
import com.jzo2o.customer.model.dto.request.ChatMessageRecallReqDTO;
import com.jzo2o.customer.model.dto.request.ChatMessageSendReqDTO;
import com.jzo2o.customer.model.dto.request.ChatMessageScrollQueryReqDTO;
import com.jzo2o.customer.model.dto.request.ChatSessionCreateReqDTO;
import com.jzo2o.customer.model.dto.request.ChatSessionDeleteReqDTO;
import com.jzo2o.customer.model.dto.request.ChatSessionReadReportReqDTO;
import com.jzo2o.customer.model.dto.request.ChatSessionScrollQueryReqDTO;
import com.jzo2o.customer.model.dto.response.ChatMessageListResDTO;
import com.jzo2o.customer.model.dto.response.ChatMessageSendResDTO;
import com.jzo2o.customer.model.dto.response.ChatSessionListResDTO;
import com.jzo2o.customer.model.dto.response.ChatSessionReadStateResDTO;
import com.jzo2o.customer.service.IChatMessageService;
import com.jzo2o.customer.service.IChatSessionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 消息通讯相关接口
 */
@RestController("consumerChatController")
@RequestMapping("/consumer/chat")
@Api(tags = "用户端 - 消息通讯相关接口")
@Validated
public class ChatController {

    @Resource
    private IChatSessionService chatSessionService;

    @Resource
    private IChatMessageService chatMessageService;

    @PostMapping("/session")
    @ApiOperation("添加会话列表")
    public Result<Long> createSession(@Validated @RequestBody ChatSessionCreateReqDTO reqDTO) {
        Result<Long> result = new Result<>();
        result.setData(chatSessionService.createSession(reqDTO));
        return result;
    }

    @DeleteMapping("/session")
    @ApiOperation("根据会话id删除会话")
    public void deleteSession(@Validated @RequestBody ChatSessionDeleteReqDTO reqDTO) {
        chatSessionService.deleteBySessionId(reqDTO);
    }

    @GetMapping("/session/scroll")
    @ApiOperation("根据最后一条消息时间滚动查询会话列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "lastTime", value = "最后一条消息时间，格式：yyyy-MM-dd HH:mm:ss", dataTypeClass = String.class),
            @ApiImplicitParam(name = "userId", value = "用户 id（消费者端会话列表）", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "staffId", value = "服务人员 id（服务端会话列表）", dataTypeClass = Long.class)
    })
    public List<ChatSessionListResDTO> scrollSessionList(@Validated ChatSessionScrollQueryReqDTO reqDTO) {
        return chatSessionService.scrollList(reqDTO);
    }

    @GetMapping("/session/read-state")
    @ApiOperation("查询会话双方已读游标")
    @ApiImplicitParam(name = "sessionId", value = "会话 id", required = true, dataTypeClass = Long.class)
    public Result<ChatSessionReadStateResDTO> getSessionReadState(@RequestParam Long sessionId) {
        Result<ChatSessionReadStateResDTO> result = new Result<>();
        result.setData(chatSessionService.getReadState(sessionId));
        return result;
    }

    @PostMapping("/session/read")
    @ApiOperation("上报已读进度（客服更新 staffReadLastTime，用户更新 userReadLastTime）")
    public void reportSessionRead(@Validated @RequestBody ChatSessionReadReportReqDTO reqDTO) {
        chatSessionService.reportRead(reqDTO);
    }

    @PostMapping("/message/send")
    @ApiOperation("发送消息")
    public ChatMessageSendResDTO sendMessage(@Validated @RequestBody ChatMessageSendReqDTO reqDTO) {
        return chatMessageService.sendMessage(reqDTO);
    }

    @GetMapping("/message/scroll")
    @ApiOperation("根据最后一条消息时间滚动查询消息记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sessionId", value = "会话id", required = true, dataTypeClass = Long.class),
            @ApiImplicitParam(name = "lastTime", value = "最后一条消息时间，格式：yyyy-MM-dd HH:mm:ss", dataTypeClass = String.class)
    })
    public List<ChatMessageListResDTO> scrollMessageList(@Validated ChatMessageScrollQueryReqDTO reqDTO) {
        return chatMessageService.scrollList(reqDTO);
    }

    @PostMapping("/message/recall")
    @ApiOperation("撤回消息（两分钟以内可以撤回）")
    public void recallMessage(@Validated @RequestBody ChatMessageRecallReqDTO reqDTO) {
        chatMessageService.recallMessage(reqDTO);
    }
}
