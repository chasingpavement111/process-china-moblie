package com.chinamobile.zj.flowProcess.service.resource.userTask.hdict;

import com.chinamobile.zj.comm.ParamException;
import com.chinamobile.zj.flowProcess.bo.ExecutionResult;
import com.chinamobile.zj.flowProcess.enums.OrderInstanceStatusEnum;
import com.chinamobile.zj.flowProcess.enums.ReviewOperationResultEnum;
import com.chinamobile.zj.flowProcess.service.resource.userTask.InheritParam;
import com.chinamobile.zj.flowProcess.service.resource.userTask.OutputParam;
import com.chinamobile.zj.hdict.entity.HdictUserInfoDO;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class ReviewDocByCityHdictUserTaskService extends AbstractHdictReviewUserTaskService {

    @InheritParam
    private String cityHdictUserId;

    @OutputParam
    private Boolean docPassedByCityHdict;

    @Override
    public String getDefinitionKey() {
        return "review_doc_by_city_hdict";
    }

    @Override
    public String getDefinitionKeyDesc() {
        return "市HDICT督导审核验收";
    }

    @Override
    public ExecutionResult execute() {
        ReviewOperationResultEnum.getByNameEn(getOperationResult()); // 检查必须经过审核
        if (ReviewOperationResultEnum.REJECTED.getNameEn().equals(getOperationResult())) {
            // 审核不通过，直接返回
            docPassedByCityHdict = false;
        } else {
            docPassedByCityHdict = true;
        }

        return new ExecutionResult(ExecutionResult.RESULT_CODE.SUCCESS);
    }

    @Override
    public Map<String, String> supportedOperatorRoleMap() {
        return Collections.singletonMap("hdict007", "HDICT专员-地市");
    }

    @Override
    public String getOperationOutputDesc() {
        // 步骤未结束时，status==null
        String operationStatus = StringUtils.isBlank(getStatus()) ? OrderInstanceStatusEnum.PROCESSING.getNameCh() :
                OrderInstanceStatusEnum.getByNameEn(getStatus()).getNameCh();
        return MessageFormat.format("{0}（{1}）对方案审核{2}", getOperatorRoleName(), getOperatorName(),
                operationStatus);
    }

    @Override
    public void checkOperatorAccessRight() {
        Optional<HdictUserInfoDO> operatorInfoOpt = getUserInfoService().getByUserCRMId(getOperatorId());
        ParamException.isTrue(BooleanUtils.isNotTrue(operatorInfoOpt.isPresent()),
                String.format("invalid userId[%s], user not exist.", getOperatorId()));
        HdictUserInfoDO operatorInfo = operatorInfoOpt.get();
        // 只允许 （同一个）地市Hdict 进行审核 todo zj test 待解除注释
        ParamException.isTrue(!cityHdictUserId.equals(getOperatorId()),
                String.format("operator[CRMId=%s] is not the city hdict user, only user[CRMId=%s] can operator this step!",
                        getOperatorId(), cityHdictUserId));

        setOperatorName(operatorInfo.getName());
        setOperatorRoleName(operatorInfo.getRoleName());
    }

    public String getCityHdictUserId() {
        return cityHdictUserId;
    }

    public void setCityHdictUserId(String cityHdictUserId) {
        this.cityHdictUserId = cityHdictUserId;
    }

    public Boolean getDocPassedByCityHdict() {
        return docPassedByCityHdict;
    }

    public void setDocPassedByCityHdict(Boolean docPassedByCityHdict) {
        this.docPassedByCityHdict = docPassedByCityHdict;
    }
}
