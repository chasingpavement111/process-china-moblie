package com.chinamobile.zj.flowProcess.bo.dto;

import com.baomidou.mybatisplus.annotations.TableField;
import lombok.Data;

import java.util.List;

@Data
public class OrderInfoResultDTO {

    private String orderUuid;
    /**
     * 状态
     */
    private String status;
    /**
     * 创建时间，格式：yyyy-MM-dd HH24:mi:ss
     */
    private String createTime;
    /**
     * 更新时间，格式：yyyy-MM-dd HH24:mi:ss
     */
    private String updateTime;

    /**
     * 当前流转用户角色
     * 流转中的步骤可能有多个，故当前流转用户角色也可能有多个
     * todo zj 根据当前处于“流转中”状态的instance获取，由 userTask service 设置
     */
    private List<String> currentOperatorRoleList;

    private List<OrderResourceInstanceInfoResultDTO> instanceExecutionHistory;
}
