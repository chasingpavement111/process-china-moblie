package com.chinamobile.zj.flowProcess.service.resource.userTask.hdict;

import com.chinamobile.zj.comm.ParamException;
import com.chinamobile.zj.flowProcess.bo.ExecutionResult;
import com.chinamobile.zj.flowProcess.enums.OrderInstanceStatusEnum;
import com.chinamobile.zj.flowProcess.enums.ReviewOperationResultEnum;
import com.chinamobile.zj.flowProcess.service.resource.userTask.InheritParam;
import com.chinamobile.zj.flowProcess.service.resource.userTask.OutputParam;
import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;

public class ReviewConstructionAndSubmitDocByCountyHdictUserTaskService extends AbstractHdictReviewUserTaskService {

    /**
     * 县HDICT用户CRM编号
     */
    @InheritParam
    private String countyHdictUserId;

    @OutputParam
    private Boolean constructionPassedByCountyHdict;

    @Override
    public String getDefinitionKey() {
        return "submit_doc_by_county_hdict";
    }

    @Override
    public String getDefinitionKeyDesc() {
        return "县HDICT专员审核施工与提交验收资料";
    }

    @Override
    public ExecutionResult execute() {
        if (ReviewOperationResultEnum.REJECTED.getNameEn().equals(getOperationResult())) {
            // 审核不通过，直接返回
            constructionPassedByCountyHdict = false;
            return new ExecutionResult(ExecutionResult.RESULT_CODE.SUCCESS);
        }
        constructionPassedByCountyHdict = true;

        return new ExecutionResult(ExecutionResult.RESULT_CODE.SUCCESS);
    }

    @Override
    public Map<String, String> supportedOperatorRoleMap() {
        return Collections.singletonMap("hdict006", "HDICT专员-县市");
    }

    @Override
    public String getOperationOutputDesc() {
        // 步骤未结束时，status==null
        String operationStatus = StringUtils.isBlank(getStatus()) ? OrderInstanceStatusEnum.PROCESSING.getNameCh() :
                OrderInstanceStatusEnum.getByNameEn(getStatus()).getNameCh();
        return MessageFormat.format("{0}（{1}）审核施工与提交验收资料{2}", getOperatorRoleName(), getOperatorName(),
                operationStatus);
    }

    @Override
    public void checkOperatorAccessRight() {
        super.checkOperatorAccessRight();

        // 只允许 (同一个)县Hdict 操作
        ParamException.isTrue(!countyHdictUserId.equals(getOperatorId()),
                String.format("operator[CRMId=%s] is not the county hdict user, only user[CRMId=%s] can operator this step!",
                        getOperatorId(), countyHdictUserId));
    }

    public String getCountyHdictUserId() {
        return countyHdictUserId;
    }

    public void setCountyHdictUserId(String countyHdictUserId) {
        this.countyHdictUserId = countyHdictUserId;
    }

    public Boolean getConstructionPassedByCountyHdict() {
        return constructionPassedByCountyHdict;
    }

    public void setConstructionPassedByCountyHdict(Boolean constructionPassedByCountyHdict) {
        this.constructionPassedByCountyHdict = constructionPassedByCountyHdict;
    }
}
