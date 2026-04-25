package com.jzo2o.api.customer;

import com.jzo2o.api.customer.dto.response.AddressBookResDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * contextId 指定FeignClient实例的上下文id，如果不设置默认为类名，value指定微服务的名称，path:指定接口地址
 */
@FeignClient(contextId = "jzo2o-customer",value = "jzo2o-customer",path = "/customer/inner/address-book")
public interface AddressBookApi {

    /**
     * 根据地址簿ID获取地址详情信息
     * @return
     */
    @GetMapping("/{id}")
    AddressBookResDTO detail(@PathVariable("id") Long id);

    /**
     * 根据用户ID查询该用户的全部地址列表
     * <p>
     * 微服务边界说明：地址数据归属 jzo2o-customer 管理，
     * 智能体等内部服务通过此 Feign 接口获取用户地址，
     * 避免跨服务直接访问数据库（SRP + 依赖倒置）。
     * </p>
     *
     * @param userId 用户ID
     * @return 该用户的全部地址列表
     */
    @GetMapping("/listByUserId")
    List<AddressBookResDTO> listByUserId(@RequestParam("userId") Long userId);

    /**
     * 查询指定用户的默认地址
     *
     * @param userId 用户ID
     * @return 默认地址，不存在则返回 null
     */
    @GetMapping("/defaultAddress")
    AddressBookResDTO defaultAddress(@RequestParam("userId") Long userId);
}