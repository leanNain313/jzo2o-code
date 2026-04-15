package com.jzo2o.customer.controller.consumer;

import cn.hutool.core.bean.BeanUtil;
import com.jzo2o.api.customer.dto.response.CommonUserResDTO;
import com.jzo2o.common.expcetions.ForbiddenOperationException;
import com.jzo2o.customer.model.domain.CommonUser;
import com.jzo2o.customer.model.dto.request.UpdateUserRequest;
import com.jzo2o.customer.service.ICommonUserService;
import com.jzo2o.mvc.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 普通用户相关接口
 *
 * @author itcast
 * @create 2023/7/7 19:34
 **/
@RestController("consumerCommonUserController")
@RequestMapping("/consumer/common-user")
@Api(tags = "用户端 - 普通用户相关接口")
public class CommonUserController {
    @Resource
    private ICommonUserService commonUserService;

    @PutMapping
    @ApiOperation("更新用户手机号")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phoneCode", value = "微信手机号授权码", required = true, dataTypeClass = String.class)
    })
    public void update(@RequestParam("phoneCode") String phoneCode, String type) {
        commonUserService.updatePhone(phoneCode, type);
    }

    @PutMapping("/update/user")
    @ApiOperation("修改用户信息接口")
    public void updateUserMessage(UpdateUserRequest request) {
        if (request == null) {
            throw new ForbiddenOperationException("参数为空");
        }
        commonUserService.updateUserMessage(request);
    }

    @ApiOperation("获取当前登录用户的信息")
    @GetMapping("/msg")
    public CommonUserResDTO getUserMeg() {
        Long userId = UserContext.currentUserId();
        CommonUser commonUser = commonUserService.getById(userId);
        if (commonUser == null) {
            throw new ForbiddenOperationException("该用户不存在");
        }
        return BeanUtil.copyProperties(commonUser,CommonUserResDTO.class);
    }

}
