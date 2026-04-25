package com.jzo2o.customer.controller.inner;

import cn.hutool.core.bean.BeanUtil;
import com.jzo2o.api.customer.dto.response.AddressBookResDTO;
import com.jzo2o.customer.model.domain.AddressBook;
import com.jzo2o.customer.service.IAddressBookService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 内部接口 - 地址薄相关接口
 * <p>
 * 提供给智能体等内部微服务通过 Feign 调用的地址数据访问接口。
 * 地址数据统一由 jzo2o-customer 管理，其他服务不应直接访问其数据库（SRP）。
 * </p>
 */
@RestController
@RequestMapping("inner/address-book")
public class InnerAddressBookController {
    @Resource
    private IAddressBookService addressBookService;

    @GetMapping("/{id}")
    public AddressBookResDTO detail(@PathVariable("id") Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        return BeanUtil.toBean(addressBook, AddressBookResDTO.class);
    }

    /**
     * 根据用户ID查询该用户的全部地址列表
     * <p>
     * 复用 MyBatis-Plus IService.list() 能力，按 userId 过滤。
     * 智能体在对话中需要展示用户可选地址列表，通过此接口获取真实数据，
     * 替代原有的硬编码 mock 地址（消除 mock 数据依赖）。
     * </p>
     */
    @GetMapping("/listByUserId")
    public List<AddressBookResDTO> listByUserId(@RequestParam("userId") Long userId) {
        List<AddressBook> addressBooks = addressBookService.lambdaQuery()
                .eq(AddressBook::getUserId, userId)
                .list();
        if (addressBooks == null || addressBooks.isEmpty()) {
            return Collections.emptyList();
        }
        return addressBooks.stream()
                .map(ab -> BeanUtil.toBean(ab, AddressBookResDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * 查询指定用户的默认地址
     * <p>
     * isDefault == 1 表示默认地址。若无默认地址则返回 null。
     * </p>
     */
    @GetMapping("/defaultAddress")
    public AddressBookResDTO defaultAddress(@RequestParam("userId") Long userId) {
        AddressBook addressBook = addressBookService.lambdaQuery()
                .eq(AddressBook::getUserId, userId)
                .eq(AddressBook::getIsDefault, 1)
                .one();
        if (addressBook == null) {
            return null;
        }
        return BeanUtil.toBean(addressBook, AddressBookResDTO.class);
    }
}