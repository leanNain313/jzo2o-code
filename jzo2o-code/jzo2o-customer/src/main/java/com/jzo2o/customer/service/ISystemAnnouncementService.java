package com.jzo2o.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jzo2o.common.model.CurrentUserInfo;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.customer.model.domain.SystemAnnouncement;
import com.jzo2o.customer.model.dto.request.AnnouncementPageQueryReqDTO;
import com.jzo2o.customer.model.dto.request.AnnouncementSaveReqDTO;
import com.jzo2o.customer.model.dto.response.AnnouncementResDTO;

public interface ISystemAnnouncementService extends IService<SystemAnnouncement> {

    void saveAnnouncement(AnnouncementSaveReqDTO reqDTO, CurrentUserInfo currentUser);

    void updateAnnouncement(Long id, AnnouncementSaveReqDTO reqDTO, CurrentUserInfo currentUser);

    PageResult<AnnouncementResDTO> operationPage(AnnouncementPageQueryReqDTO queryReqDTO);

    AnnouncementResDTO operationDetail(Long id);

    void publish(Long id, CurrentUserInfo currentUser);

    void offline(Long id, CurrentUserInfo currentUser);

    PageResult<AnnouncementResDTO> receiverPage(AnnouncementPageQueryReqDTO queryReqDTO, Long userId, Integer userType);

    AnnouncementResDTO receiverDetail(Long id, Long userId, Integer userType);

    void markRead(Long id, Long userId, Integer userType);

    Long unreadCount(Long userId, Integer userType);
}
