package com.jzo2o.customer.controller.worker;

import cn.hutool.core.bean.BeanUtil;
import com.jzo2o.common.expcetions.ForbiddenOperationException;
import com.jzo2o.customer.model.domain.WorkerCertification;
import com.jzo2o.customer.model.dto.response.WorkerCertificationResDTO;
import com.jzo2o.customer.service.IWorkerCertificationService;
import com.jzo2o.mvc.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 服务人员端查询本人认证信息，与运营端
 * {@link com.jzo2o.customer.controller.operation.WorkerCertificationController#queryById(Long)} 使用相同 Service 与 DTO。
 */
@RestController("workerWorkerCertificationQueryController")
@RequestMapping("/worker/worker-certification")
@Api(tags = "服务端-服务人员认证信息")
public class WorkerCertificationQueryController {

    @Resource
    private IWorkerCertificationService workerCertificationService;

    @GetMapping("/{id}")
    @ApiOperation("根据服务人员id查询认证信息（仅本人）")
    public WorkerCertificationResDTO queryById(@PathVariable("id") Long id) {
        if (!Objects.equals(UserContext.currentUserId(), id)) {
            throw new ForbiddenOperationException("无权查看该服务人员的认证信息");
        }
        WorkerCertification entity = workerCertificationService.getById(id);
        if (entity == null) {
            return new WorkerCertificationResDTO();
        }
        return BeanUtil.toBean(entity, WorkerCertificationResDTO.class);
    }
}
