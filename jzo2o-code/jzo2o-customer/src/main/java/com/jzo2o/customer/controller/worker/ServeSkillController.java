package com.jzo2o.customer.controller.worker;


import com.jzo2o.api.foundations.dto.response.ServeItemSimpleResDTO;
import com.jzo2o.api.foundations.dto.response.ServeTypeSimpleResDTO;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.customer.model.domain.ServeSkill;
import com.jzo2o.customer.model.dto.request.AuditPageRequest;
import com.jzo2o.customer.model.dto.request.AuditRequest;
import com.jzo2o.customer.model.dto.request.ServeSkillAddReqDTO;
import com.jzo2o.customer.model.dto.response.ServeSkillCategoryResDTO;
import com.jzo2o.customer.service.IServeSkillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 服务技能表 前端控制器
 * </p>
 *
 * @author itcast
 * @since 2023-07-18
 */
@RestController("workerServeSkillController")
@RequestMapping("/worker/serve-skill")
@Api(tags = "服务端 - 服务技能相关接口")
public class ServeSkillController {
    @Resource
    private IServeSkillService serveSkillService;

    @Deprecated
    @PostMapping("/batchUpsert")
    @ApiOperation("批量新增或修改服务技能")
    public void listServeType(@RequestBody List<ServeSkillAddReqDTO> serveSkillAddReqDTOList) {
        serveSkillService.batchUpsert(serveSkillAddReqDTOList);
    }

    /**
     * 发送添加技能申请
     * @param request 请求参数
     */
    @ApiOperation("发送添加技能申请")
    @PostMapping("/send/audit")
    public void sendAudit(@RequestBody ServeSkillAddReqDTO request) {
        serveSkillService.sendAudit(request);
    }

    /**
     * 审核服务人员技能
     * @param request 请求参数
     */
    @ApiOperation("审核服务人员技能")
    @PostMapping("/message/audit")
    public void messageAudit(@RequestBody AuditRequest request) {
        serveSkillService.messageAudit(request);
    }

    @ApiOperation("服务人员技能审核分页")
    @GetMapping("/page")
    public PageResult<ServeSkill> skillPage(AuditPageRequest request) {
        return serveSkillService.skillPage(request);
    }

    @GetMapping("/category")
    @ApiOperation("查询服务技能目录")
    public List<ServeSkillCategoryResDTO> category() {
        return serveSkillService.category();
    }

    @GetMapping("/queryCurrentUserServeSkillTypeList")
    @ApiOperation("查询当前用户的服务技能类型")
    public List<ServeTypeSimpleResDTO> queryCurrentUserServeSkillTypeList() {
        return serveSkillService.queryCurrentUserServeSkillTypeList();
    }

    @GetMapping("/queryCurrentUserServeSkillItemList")
    @ApiOperation("查询当前用户的服务技能")
    public List<ServeItemSimpleResDTO> queryCurrentUserServeSkillItemList() {
        return serveSkillService.queryCurrentUserServeSkillItemList();
    }
}
