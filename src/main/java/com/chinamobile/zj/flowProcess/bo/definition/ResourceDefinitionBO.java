package com.chinamobile.zj.flowProcess.bo.definition;

import com.chinamobile.zj.flowProcess.enums.StencilEnum;
import com.chinamobile.zj.flowProcess.service.resource.BaseUserTaskService;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * 流程图中的每一个元素详情
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourceDefinitionBO {

    /**
     * 流程图中的元素的ID
     * resourceDefinitionKey
     */
    private String resourceId;

    /**
     * 元素类型
     * todo zj 序列化 反序列化的字段名问题
     */
//    @JsonProperty("stencil") // spring框架序列化restful入參
//    @JSONField(name = "stencil") // fastJson 序列化別名
    private StencilEnum stencilEnum;
    private String stencil;

    /**
     * 元素属性
     */
    private ResourcePropertiesBO properties;

    /**
     * 元素的下一跳元素集合
     */
    private List<ResourceDefinitionBO> outGoing;

    public void setStencil(String stencil) {
        this.stencil = stencil;
        this.stencilEnum = StencilEnum.getById(stencil);
    }

    public void setStencilEnum(StencilEnum stencilEnum) {
        this.stencilEnum = stencilEnum;
    }

    public String getResourceDefinitionKey() {
        String resourceDefinitionKey = getProperties().getOverrideid();
        return resourceDefinitionKey;
    }
}