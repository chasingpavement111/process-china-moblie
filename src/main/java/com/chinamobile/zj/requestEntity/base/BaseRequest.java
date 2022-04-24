package com.chinamobile.zj.requestEntity.base;

import com.chinamobile.zj.comm.ParamException;
import com.chinamobile.zj.requestEntity.validation.group.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

@Data
public abstract class BaseRequest implements MapOfFieldChineseName2Value {
    /**
     * 分页页索引，从 1 开始
     */
    private Integer pageNum;

    /**
     * 分页的单页记录数量
     */
    private Integer pageSize;

    /**
     * 分页的排序字段名
     */
    @NotBlank(groups = Order.class)
    public String orderColumnName;

    /**
     * 排序类型：true 升序；false 降序
     * 默认降序
     */
    private Boolean asc;

    // 以下是基本的范围概念：目前入参格式不统一，待整改
    // 地市 -> 县市area_id3 -> 分组fz_id -> 网格area_id4 -> 乡镇街道area_id5 -> 场景类型scene_id -> 微格\场景(poi_id\wg_scene_id)
    /**
     * 县市编号
     */
    @JsonProperty("area_id3")
    @NotBlank(groups = County.class)
    private String areaId3;

    /**
     * 网格编号
     */
    @JsonProperty("area_id4")
    @NotBlank(groups = NetGrid.class)
    private String areaId4;

    /**
     * 乡镇街道编号
     */
    @JsonProperty("area_id5")
    @NotBlank(groups = TownAndStreet.class)
    private String areaId5;

    /**
     * 微格\场景编号
     */
    @JsonProperty("poi_id")
    @NotBlank(groups = ScenePoi.class)
    private String poiId;

    /**
     * 渠道编号
     */
    private String chlId;
    /**
     * 渠道名称
     * 支持双边模糊查询
     */
    private String chlName;

    public void setOrderColumnName(String orderColumnName) {
        this.orderColumnName = orderColumnName;
        validOrderColumnName();
    }

    public void validOrderColumnName() {
        if (StringUtils.isBlank(this.orderColumnName)) {
            return;
        }
        throw new ParamException(String.format("unsupported orderColumnName: %s", this.orderColumnName));
    }


    @Override
    public Map<String, Object> mapOfFieldChineseName2Value() {
        Map<String, Object> map = new HashMap<>();
        map.put("分页页索引", pageNum);
        map.put("分页的单页记录数量", pageSize);
        map.put("排序字段名", orderColumnName);
        map.put("排序类型", asc);
        map.put("县市编号", areaId3);
        map.put("网格编号", areaId4);
        map.put("乡镇街道编号", areaId5);
        map.put("微格\\场景编号", poiId);
        map.put("渠道编号", chlId);
        map.put("渠道名称", chlName);
        return map;
    }
}
