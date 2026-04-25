package com.jzo2o.api.foundations.dto.response;

/**
 * 服务简略响应信息（内部接口专用）
 * <p>
 * 用于智能体等内部服务通过 Feign 进行服务关键词搜索时返回的精简结果。
 * 字段与 foundations 模块中 ES 搜索结果对齐。
 * </p>
 */
public class ServeSimpleResDTO {

    /**
     * 服务id
     */
    private Long id;

    /**
     * 服务项id
     */
    private Long serveItemId;

    /**
     * 服务项名称
     */
    private String serveItemName;

    /**
     * 服务项图标
     */
    private String serveItemIcon;

    /**
     * 服务项排序字段
     */
    private Integer serveItemSortNum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getServeItemId() {
        return serveItemId;
    }

    public void setServeItemId(Long serveItemId) {
        this.serveItemId = serveItemId;
    }

    public String getServeItemName() {
        return serveItemName;
    }

    public void setServeItemName(String serveItemName) {
        this.serveItemName = serveItemName;
    }

    public String getServeItemIcon() {
        return serveItemIcon;
    }

    public void setServeItemIcon(String serveItemIcon) {
        this.serveItemIcon = serveItemIcon;
    }

    public Integer getServeItemSortNum() {
        return serveItemSortNum;
    }

    public void setServeItemSortNum(Integer serveItemSortNum) {
        this.serveItemSortNum = serveItemSortNum;
    }
}
