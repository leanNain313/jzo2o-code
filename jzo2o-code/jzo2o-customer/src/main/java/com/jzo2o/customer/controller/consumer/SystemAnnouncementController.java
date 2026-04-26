package com.jzo2o.customer.controller.consumer;

import com.jzo2o.common.constants.UserType;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.customer.model.dto.request.AnnouncementPageQueryReqDTO;
import com.jzo2o.customer.model.dto.response.AnnouncementResDTO;
import com.jzo2o.customer.service.ISystemAnnouncementService;
import com.jzo2o.mvc.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController("consumerSystemAnnouncementController")
@RequestMapping("/consumer/announcements")
@Api(tags = "用户端 - 系统公告相关接口")
public class SystemAnnouncementController {

    @Resource
    private ISystemAnnouncementService announcementService;

    @GetMapping("/page")
    @ApiOperation("公告分页查询")
    public PageResult<AnnouncementResDTO> page(AnnouncementPageQueryReqDTO queryReqDTO) {
        return announcementService.receiverPage(queryReqDTO, UserContext.currentUserId(), UserType.C_USER);
    }

    @GetMapping("/{id}")
    @ApiOperation("公告详情")
    public AnnouncementResDTO detail(@PathVariable("id") Long id) {
        return announcementService.receiverDetail(id, UserContext.currentUserId(), UserType.C_USER);
    }

    @PutMapping("/{id}/read")
    @ApiOperation("标记公告已读")
    public void read(@PathVariable("id") Long id) {
        announcementService.markRead(id, UserContext.currentUserId(), UserType.C_USER);
    }

    @GetMapping("/unread-count")
    @ApiOperation("未读公告数")
    public Long unreadCount() {
        return announcementService.unreadCount(UserContext.currentUserId(), UserType.C_USER);
    }
}
