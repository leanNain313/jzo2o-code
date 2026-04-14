package com.jzo2o.customer.controller.worker;

import cn.hutool.core.bean.BeanUtil;
import com.jzo2o.common.expcetions.ForbiddenOperationException;
import com.jzo2o.customer.model.domain.ServeProvider;
import com.jzo2o.customer.model.dto.response.ServeProviderInfoResDTO;
import com.jzo2o.customer.service.IServeProviderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author 86188
 */

@RestController("workerServeProviderController")
@RequestMapping("/worker/serve-provider")
@Api(tags = "服务端 - 服务人员相关接口")
public class ServeProviderController {

    @Resource
    private IServeProviderService serveProviderService;

    @GetMapping("/currentUserInfo")
    @ApiOperation("获取当前用户信息")
    public ServeProviderInfoResDTO currentUserInfo() {
        return serveProviderService.currentUserInfo();
    }

    @GetMapping("/{id}")
    public ServeProviderInfoResDTO getUserInfoById(@PathVariable Long id) {
        if (id == null) {
            throw new ForbiddenOperationException("服务人员id不能为空");
        }
        ServeProvider serveProvider = serveProviderService.getById(id);
        return BeanUtil.copyProperties(serveProvider, ServeProviderInfoResDTO.class);
    }
}
