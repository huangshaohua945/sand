/**
 * 软件版权：流沙~~
 * 修改日期   修改人员     修改说明
 * =========  ===========  =====================
 * 2019/8/26    liusha   新增
 * =========  ===========  =====================
 */
package com.sand.base.core.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 功能说明：实体对象基类
 * 开发人员：@author liusha
 * 开发日期：2019/8/26 13:38
 * 功能描述：实体对象的功能字段
 */
@Data
@Accessors(chain = true)
public class BaseEntity implements Serializable {
  private static final long serialVersionUID = 2367225182033538004L;
  /**
   * 创建者
   */
  private String createBy;
  /**
   * 更新者
   */
  private String updateBy;
  /**
   * 创建时间
   */
  private Date createTime;

  /**
   * 更新时间
   */
  private Date updateTime;

  /**
   * 备注信息
   */
  private String remark;
}
