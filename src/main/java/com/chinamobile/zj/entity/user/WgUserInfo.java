package com.chinamobile.zj.entity.user;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author zhangjie
 * @since 2022-01-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("WG_USER_INFO")
public class WgUserInfo extends Model<WgUserInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 人员id，主键
     */
    @TableField("USER_ID")
    private String userId;
    /**
     * 登录名 -- 即CRM编号
     */
    @TableField("LOGIN_ID")
    private String loginId;
    /**
     * 人员系统编号
     */
    @TableField("OP_ID")
    private String opId;
    /**
     * 组织编号
     */
    @TableField("ORG_ID")
    private String orgId;
    /**
     * 登录用户中文名
     */
    @TableField("NAME")
    private String name;
    /**
     * 登录用户号码
     */
    @TableField("TEL")
    private String tel;
    /**
     * 县市ID
     */
    @TableField("AREA_ID3")
    private String areaId3;
    /**
     * 县市名称
     */
    @TableField("AREA_NAME3")
    private String areaName3;
    /**
     * 网格ID
     */
    @TableField("AREA_ID4")
    private String areaId4;
    /**
     * 网格名称
     */
    @TableField("AREA_NAME4")
    private String areaName4;
    /**
     * 乡镇ID
     */
    @TableField("AREA_ID5")
    private String areaId5;
    /**
     * 乡镇名称
     */
    @TableField("AREA_NAME5")
    private String areaName5;
    /**
     * 网点ID
     */
    @TableField("WDBH")
    private String chlId;
    /**
     * 网点名称
     */
    @TableField("WDMC")
    private String chlName;
    /**
     * 0系统账号人员，1非系统账号人员
     */
    @TableField("XTZHRYCODE")
    private String userCodeOfSystemAccount;
    /**
     * 邮箱
     */
    @TableField("EML")
    private String eml;
    /**
     * 人力编号
     */
    @TableField("RLBH")
    private String rlBh;
    /**
     * 头像路径 -- 网格长的证件照
     */
    @TableField("IMG")
    private String img;
    /**
     * 座右铭
     */
    @TableField("MOTTO")
    private String motto;
    /**
     * 是否是党员，0代表是，1代表不是
     */
    @TableField("IS_DY")
    private Double isDy;
    /**
     * 状态。0正常，1为删除
     */
    @TableField("STATUS")
    private String status;
    /**
     * 操作人编号
     */
    @TableField("EDITPERSON_ID")
    private String editPersonId;
    /**
     * 新增时间
     */
    @TableField("CREATE_DATE")
    private Date createDate;
    /**
     * 最后更新时间
     */
    @TableField("LAST_UPDATE_DATE")
    private Date lastUpdateDate;
    /**
     * 挂帅领导  0 是 1 不是
     */
    @TableField("IS_GS")
    private String isGs;
    /**
     * 挂帅网格
     */
    @TableField("GS_AREA")
    private String gsArea;


    @Override
    protected Serializable pkVal() {
        return null;
    }

}
