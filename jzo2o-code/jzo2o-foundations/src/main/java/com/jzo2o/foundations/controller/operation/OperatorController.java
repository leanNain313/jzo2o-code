package com.jzo2o.foundations.controller.operation;

import com.jzo2o.foundations.model.dto.OperatorAddDTO;
import com.jzo2o.foundations.service.IOperatorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 运营人员管理
 */
@RestController("operationOperatorController")
@RequestMapping("/operation/operator")
@Api(tags = "运营端 - 运营人员管理")
@Validated
public class OperatorController {

    @Resource
    private IOperatorService operatorService;

    @PostMapping
    @ApiOperation("新增运营管理员账号")
    public void add(@RequestBody @Valid OperatorAddDTO operatorAddDTO) {
        operatorService.add(operatorAddDTO);
    }
}
