/**
 * 软件版权：流沙~~
 * 修改日期   修改人员     修改说明
 * =========  ===========  =====================
 * 2019/9/2    liusha   新增
 * =========  ===========  =====================
 */
package com.sand.sys.model;

import com.sand.sys.entity.SysUser;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 功能说明：系统用户Model
 * 开发人员：@author liusha
 * 开发日期：2019/9/2 17:47
 * 功能描述：用作DTO、VO
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SysUserModel extends SysUser {
  private static final long serialVersionUID = 5782380708741685834L;
  /**
   * 当前页码
   */
  private long current = 1L;
  /**
   * 每页记录数
   */
  private long size = 10L;
}