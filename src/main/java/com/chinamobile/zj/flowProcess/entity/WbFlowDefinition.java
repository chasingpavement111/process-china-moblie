package com.chinamobile.zj.flowProcess.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 流程定义表
 * </p>
 *
 * @author zhangjie
 * @since 2022-04-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wb_flow_definition")
public class WbFlowDefinition extends Model<WbFlowDefinition> {

    private static final long serialVersionUID = 1L;

    /**
     * 流程定义唯一标识
     */
    @TableId("flow_uuid")
    private String flowUuid;
    /**
     * 流程定义标识
     */
    @TableField("flow_definition_key")
    private String flowDefinitionKey;
    /**
     * 版本号（flow_definistion_key + flow_definition_version 唯一确定一份流程定义）
     */
    @TableField("flow_definition_version")
    private String flowDefinitionVersion;
    /**
     * 定义结构体json
     */
    private String json;


    @Override
    protected Serializable pkVal() {
        return this.flowUuid;
    }

}
