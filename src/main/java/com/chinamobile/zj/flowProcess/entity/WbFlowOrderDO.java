package com.chinamobile.zj.flowProcess.entity;

import com.alibaba.fastjson.TypeReference;
import com.chinamobile.zj.util.JsonConvertUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.Map;

@Data
@Accessors(chain = true)
public class WbFlowOrderDO extends WbFlowOrder {

    /**
     * 入参
     */
    private Map<String, Object> inputVariablesMap;

    @Override
    public WbFlowOrder setInputVariables(String inputVariables) {
        super.setInputVariables(inputVariables);

        if (MapUtils.isEmpty(getInputVariablesMap())) {
            Map<String, Object> map = JsonConvertUtil.parseToParameterizedType(inputVariables, new TypeReference<Map<String, Object>>() {
            });
            setInputVariablesMap(map);
        }
        return this;
    }

    public void setInputVariablesMap(Map<String, Object> inputVariablesMap) {
        if (MapUtils.isEmpty(inputVariablesMap)) {
            this.inputVariablesMap = Collections.EMPTY_MAP;
        } else {
            this.inputVariablesMap = inputVariablesMap;
        }

        if (StringUtils.isBlank(getInputVariables())) {
            String jsonStr = JsonConvertUtil.toJsonString(inputVariablesMap);
            setInputVariables(jsonStr);
        }
    }
}
