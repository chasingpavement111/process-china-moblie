package com.chinamobile.zj.flowProcess.bo;

import com.chinamobile.zj.flowProcess.bo.definition.ResourceDefinitionBO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 获取下一流程的返回体
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultOfGetNextResourceBO {

    /**
     * 下一待执行userTask类型 资源
     * currentUserTaskFinished==true && nextUserTaskResourceBO==null 时，可能存在合并分支，需要多个并行执行的userTask同时完成，才能进入到下一个userTask
     */
    private ResourceDefinitionBO nextUserTaskResourceBO;

    /**
     * 存在下一跳节点
     * 存在不代表会执行。可能执行条件未满足，待执行
     */
    private Boolean hasNextResourceBO;

    /**
     * 下一跳为终止节点。
     * 若非空，则整个流程结束
     */
    private ResourceDefinitionBO nextThrowNoneEventResourceBO;
}