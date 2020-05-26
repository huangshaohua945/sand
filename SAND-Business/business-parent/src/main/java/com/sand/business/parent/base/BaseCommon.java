/**
 * 软件版权：流沙~~
 * 修改日期   修改人员     修改说明
 * =========  ===========  =====================
 * 2019/8/27    liusha   新增
 * =========  ===========  =====================
 */
package com.sand.business.parent.base;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sand.common.exception.BusinessException;
import com.sand.common.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能说明：提供公共服务
 * 开发人员：@author liusha
 * 开发日期：2019/8/27 8:37
 * 功能描述：提供公共服务
 */
@Slf4j
public class BaseCommon {
  /**
   * 业务处理异常
   */
  protected void newBusinessException(String msg) {
    throw new BusinessException(ResultVO.Code.BUSINESS_ERROR, msg);
  }
  /**
   * 将mybatis-plus的分页插件转换成需要的展示格式
   *
   * @param page 分页信息
   * @return 分页信息
   */
  protected Map<String, Object> page2map(Page<?> page) {
    return page2map(page, page.getRecords());
  }

  /**
   * 将mybatis-plus的分页插件转换成需要的展示格式
   *
   * @param page    分页信息
   * @param records 分页数据
   * @return 分页信息
   */
  protected Map<String, Object> page2map(Page<?> page, List records) {
    Map<String, Object> map = new HashMap<>();
    map.put("records", records);
    map.put("total", page.getTotal());
    map.put("pages", page.getPages());
    map.put("current", page.getCurrent());
    map.put("size", page.getSize());
    return map;
  }

}