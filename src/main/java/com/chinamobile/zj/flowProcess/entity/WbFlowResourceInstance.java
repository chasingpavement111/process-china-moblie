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
 * 流程步骤实例表
 * </p>
 *
 * @author zhangjie
 * @since 2022-04-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wb_flow_resource_instance")
public class WbFlowResourceInstance extends Model<WbFlowResourceInstance> {

    private static final long serialVersionUID = 1L;

    /**
     * 步骤实例唯一标识
     */
    @TableId("resource_instance_uuid")
    private String resourceInstanceUuid;
    /**
     * 步骤所属工单的工单标识
     */
    @TableField("order_uuid")
    private String orderUuid;
    /**
     * 实例对应的流程步骤定义标识
     */
    @TableField("flow_resource_definition_key")
    private String flowResourceDefinitionKey;
    /**
     * 上一步的步骤实例的实例唯一标识
     */
    @TableField("former_resource_instance_uuid")
    private String formerResourceInstanceUuid;
    /**
     * 下一步的步骤实例的实例唯一标识
     */
    @TableField("latter_resource_instance_uuid")
    private String latterResourceInstanceUuid;
    /**
     * 创建时间，格式：yyyy-MM-dd HH24:mi:ss
     */
    @TableField("create_time")
    private String createTime;
    /**
     * 入参结构体json
     */
    @TableField("input_variables")
    private String inputVariables;
    /**
     * 操作人CRM编号
     */
    @TableField("operator_id")
    private String operatorId;
    /**
     * 更新时间，格式：yyyy-MM-dd HH24:mi:ss
     */
    @TableField("operate_time")
    private String operateTime;
    /**
     * 出参结构体json
     */
    @TableField("output_variables")
    private String outputVariables;
    /**
     * 状态
     */
    private String status;
    /**
     * 是否有效：1（有效）/ 0（无效，被驳回需要重新执行）
     * 1 对应java true;
     * 0 对应java false;
     */
    private Boolean valid;

    @Override
    protected Serializable pkVal() {
        return this.resourceInstanceUuid;
    }

}
