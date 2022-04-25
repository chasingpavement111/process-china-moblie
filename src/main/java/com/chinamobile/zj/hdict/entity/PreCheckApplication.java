package com.chinamobile.zj.hdict.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 预勘申请 信息表
 */
@Data
public class PreCheckApplication {

    /**
     * 用于确定同县市的审核人
     */
    @NotBlank
    private String areaId3;

    /**
     * 用于限制预勘的申请人
     */
    @NotBlank
    private String creatorId;
}
