package com.chinamobile.zj.flowProcess.service.definition;

import com.chinamobile.zj.comm.InternalException;
import com.chinamobile.zj.comm.ParamException;
import com.chinamobile.zj.flowProcess.bo.definition.FlowProcessDefinitionBO;
import com.chinamobile.zj.flowProcess.bo.definition.ResourceDefinitionBO;
import com.chinamobile.zj.flowProcess.enums.StencilEnum;
import com.chinamobile.zj.flowProcess.service.resource.BaseUserTaskService;
import com.chinamobile.zj.util.JsonConvertUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FlowDefinitionResourceService {

//    private Map<String, BaseUserTaskService> userTaskServiceMap; todo zj 不设置，避免循环依赖

    private FlowProcessDefinitionBO flowProcessBo;

    /**
     * 用于流程走向
     * todo 1、流程定义 json 应该持久化到数据库。比较文件是否一致。通过sha256区别。每个order实例保存flow_definition_id + sha256 确定当时的json定义。避免流程修改后，原工单执行不下去
     */
    private Map<String, ResourceDefinitionBO> resourceDefinitionMap;

    /**
     * 倒序的节点执行 todo zj 执行环导致对象循环引用，执行不报错但元素报错overflow
     */
    private LinkedHashMap<String, ResourceDefinitionBO> orderedResourceBOMap = new LinkedHashMap<>();

    /**
     * 便于在变化的流程定义中，找到不变的操作实现类userTask
     */
    private Map<String, ResourceDefinitionBO> userTaskDefinitionMap;

    public FlowProcessDefinitionBO getFlowProcessBo() {
        return flowProcessBo;
    }

    public Map<String, ResourceDefinitionBO> getResourceDefinitionMap() {
        return resourceDefinitionMap;
    }

    public LinkedHashMap<String, ResourceDefinitionBO> getOrderedResourceBOMap() {
        return orderedResourceBOMap;
    }

    public Map<String, ResourceDefinitionBO> getUserTaskDefinitionMap() {
        return userTaskDefinitionMap;
    }

    public FlowDefinitionResourceService(String jsonXml) {
        setUserTaskDefinitionMap(jsonXml);
    }

    private void setUserTaskDefinitionMap(String jsonXml) {
        // todo zj 項目初始化時，自动加载并解析json文件作为入参 jsonXml的值。load 文件 /precheck_install_process.json
        // todo zj 难道每个都加载一遍吗：不同json流程定义怎么定义bean...\本类型的子类
        flowProcessBo = JsonConvertUtil.parseToObject(jsonXml, FlowProcessDefinitionBO.class);
        resourceDefinitionMap = flowProcessBo.getChildShapes().stream().collect(Collectors.toMap(ResourceDefinitionBO::getResourceId, Function.identity()));

        {
            // 获取终点
            List<ResourceDefinitionBO> endEventList = flowProcessBo.getChildShapes().stream()
                    .filter(resourceBO -> StencilEnum.THROW_NONE_EVENT.equals(resourceBO.getStencilEnum()) && CollectionUtils.isEmpty(resourceBO.getOutGoing()))
                    .collect(Collectors.toList());
            // 检查：流程无终点。不允许
            InternalException.isTrue(CollectionUtils.isEmpty(endEventList),
                    String.format("end event not found! Please contact the administrator to check flow definition[flowDefinitionKey=%s]", flowProcessBo.getProperties().getProcessId()));

            List<ResourceDefinitionBO> lastResourceBOList = endEventList; // 可能存在多个并行步骤
            while (CollectionUtils.isNotEmpty(lastResourceBOList)) {

                lastResourceBOList.forEach(lastResourceBO -> orderedResourceBOMap.put(lastResourceBO.getResourceId(), lastResourceBO));

                List<ResourceDefinitionBO> newLastResourceBOList = new ArrayList<>();

                lastResourceBOList.forEach(lastResourceBO -> {
                    // 找到所有前序节点
                    List<ResourceDefinitionBO> formerResourceBOList = resourceDefinitionMap.entrySet().stream().filter(entry -> {
                        ResourceDefinitionBO resourceBO = entry.getValue();
                        // 检查：流程中的非终点类型的资源，必须指定下一跳。可能画图的时候箭头线没连上，检查一下
                        InternalException.isTrue(!StencilEnum.THROW_NONE_EVENT.equals(resourceBO.getStencilEnum()) && CollectionUtils.isEmpty(resourceBO.getOutGoing()),
                                String.format("It's not allowed non end type resource has no outGoing[resourceId=%s, resourceStencilType=%s]. Please contact the administrator to check flow definition[flowDefinitionKey=%s]",
                                        resourceBO.getResourceId(), resourceBO.getStencilEnum().getId(),
                                        flowProcessBo.getProperties().getProcessId()));
                        return resourceBO.getOutGoing().stream().anyMatch(outGoingResourceBO -> lastResourceBO.getResourceId().equals(outGoingResourceBO.getResourceId()) // 找到前序节点
                                && Objects.isNull(outGoingResourceBO.getStencilEnum()) // 去除已经赋值过的outGoing资源节点，避免重复赋值，无法跳出循环
                        );
                    }).map(entry -> {
                        ResourceDefinitionBO formerResourceBO = entry.getValue();
                        // 对outGoing 后续元素的属性赋值。避免每次获取outGoing后，资源的属性都是空，要透传流程定义，每次找很麻烦
                        formerResourceBO.getOutGoing().forEach(outGoing -> {
                            if (outGoing.getResourceId().equals(lastResourceBO.getResourceId())) {
                                BeanUtils.copyProperties(lastResourceBO, outGoing);
                            }
                        });
                        return formerResourceBO;
                    }).collect(Collectors.toList());

                    // 将整理过outGoing属性的resource对象，重新放到集合中。保障 resourceDefinitionMap 中的内容最新
                    formerResourceBOList.forEach(formerResourceBO -> resourceDefinitionMap.put(formerResourceBO.getResourceId(), formerResourceBO));

                    newLastResourceBOList.addAll(formerResourceBOList);
                });
                lastResourceBOList = newLastResourceBOList;
            }
        }

        userTaskDefinitionMap = flowProcessBo.getChildShapes().stream()
                .filter(resourceBO -> StencilEnum.USER_TASK.equals(resourceBO.getStencilEnum())) // 操作节点必须定义 overrideid，便于userTaskService对应唯一的操作步骤；resourceId可能变化，故不适合
                .collect(Collectors.toMap(obj -> obj.getProperties().getOverrideid(), Function.identity()));
    }
}
