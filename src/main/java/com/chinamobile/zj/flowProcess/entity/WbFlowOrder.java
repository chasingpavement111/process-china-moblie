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
 * 流程工单表
 * </p>
 *
 * @author zhangjie
 * @since 2022-04-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wb_flow_order")
public class WbFlowOrder extends Model<WbFlowOrder> {

    private static final long serialVersionUID = 1L;

    /**
     * 流程工单唯一标识
     */
    @TableId("order_uuid")
    private String orderUuid;
    /**
     * 流程定义的唯一标识
     */
    @TableField("flow_definition_key")
    private String flowDefinitionKey;
    /**
     * 流程定义的版本号（每次更新都会更新版本号）
     */
    @TableField("flow_definition_version")
    private String flowDefinitionVersion;
    /**
     * 创建时间，格式：yyyy-MM-dd HH24:mi:ss
     */
    @TableField("create_time")
    private String createTime;
    /**
     * 创建人CRM编号
     */
    @TableField("creator_id")
    private String creatorId;
    /**
     * 入参json串
     */
    @TableField("input_variables")
    private String inputVariables;
    /**
     * 最新操作人CRM编号
     */
    @TableField("update_operator_id")
    private String updateOperatorId;
    /**
     * 更新时间，格式：yyyy-MM-dd HH24:mi:ss
     */
    @TableField("update_time")
    private String updateTime;
    /**
     * 状态
     */
    private String status;


    @Override
    protected Serializable pkVal() {
        return this.orderUuid;
    }

}
