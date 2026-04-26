package com.jzo2o.customer.controller.operation;

import com.jzo2o.common.model.PageResult;
import com.jzo2o.customer.model.dto.request.AnnouncementPageQueryReqDTO;
import com.jzo2o.customer.model.dto.request.AnnouncementSaveReqDTO;
import com.jzo2o.customer.model.dto.response.AnnouncementResDTO;
import com.jzo2o.customer.service.ISystemAnnouncementService;
import com.jzo2o.mvc.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController("operationSystemAnnouncementController")
@RequestMapping("/operation/announcements")
@Api(tags = "运营端 - 系统公告相关接口")
public class SystemAnnouncementController {

    @Resource
    private ISystemAnnouncementService announcementService;

    @PostMapping
    @ApiOperation("新增公告")
    public void save(@RequestBody @Validated AnnouncementSaveReqDTO reqDTO) {
        announcementService.saveAnnouncement(reqDTO, UserContext.currentUser());
    }

    @PutMapping("/{id}")
    @ApiOperation("编辑公告")
    public void update(@PathVariable("id") Long id, @RequestBody @Validated AnnouncementSaveReqDTO reqDTO) {
        announcementService.updateAnnouncement(id, reqDTO, UserContext.currentUser());
    }

    @GetMapping("/page")
    @ApiOperation("公告分页查询")
    public PageResult<AnnouncementResDTO> page(AnnouncementPageQueryReqDTO queryReqDTO) {
        return announcementService.operationPage(queryReqDTO);
    }

    @GetMapping("/{id}")
    @ApiOperation("公告详情")
    public AnnouncementResDTO detail(@PathVariable("id") Long id) {
        return announcementService.operationDetail(id);
    }

    @PutMapping("/{id}/publish")
    @ApiOperation("发布公告")
    public void publish(@PathVariable("id") Long id) {
        announcementService.publish(id, UserContext.currentUser());
    }

    @PutMapping("/{id}/offline")
    @ApiOperation("下线公告")
    public void offline(@PathVariable("id") Long id) {
        announcementService.offline(id, UserContext.currentUser());
    }
}
