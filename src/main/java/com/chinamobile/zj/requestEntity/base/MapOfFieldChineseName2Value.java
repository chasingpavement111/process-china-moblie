package com.chinamobile.zj.requestEntity.base;

import java.util.Map;

/**
 * 使用场景：controller方法的入参，为自定义类型。则该类型应该实现本接口<br/>
 * 类似于toString()，但字段名是中文<br/>
 * 因为表字段 CLY_T_ALL_DOLOG.cont 要求入参的字段名称为中文，不然影响开发绩效考评。<br/>
 * 强烈建议提高审核人员英语水平<br/>
 */
public interface MapOfFieldChineseName2Value {
    Map<String, Object> mapOfFieldChineseName2Value();
}
