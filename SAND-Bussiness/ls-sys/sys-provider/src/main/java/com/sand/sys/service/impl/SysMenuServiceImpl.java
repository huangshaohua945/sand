/**
 * 软件版权：流沙~~
 * 修改日期   修改人员     修改说明
 * =========  ===========  =====================
 * 2019/8/26    liusha   新增
 * =========  ===========  =====================
 */
package com.sand.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sand.base.enums.OperateEnum;
import com.sand.base.enums.ResultEnum;
import com.sand.base.exception.LsException;
import com.sand.base.util.lang3.StringUtil;
import com.sand.base.util.tree.Tree;
import com.sand.base.util.tree.TreeUtil;
import com.sand.base.util.tree.builder.ITreeBuilder;
import com.sand.sys.entity.SysMenu;
import com.sand.sys.entity.SysRoleMenu;
import com.sand.sys.enums.MenuEnum;
import com.sand.sys.mapper.SysMenuMapper;
import com.sand.sys.model.SysMenuModel;
import com.sand.sys.service.ISysMenuService;
import com.sand.sys.service.ISysRoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 功能说明：系统菜单
 * 开发人员：@author liusha
 * 开发日期：2019/8/26 16:12
 * 功能描述：系统菜单
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {
  @Autowired
  private ISysRoleMenuService roleMenuService;

  @Override
  public Tree buildMenuTree(boolean needButton, boolean isAdmin) {
    return buildMenuTree(needButton, isAdmin, new Object[0]);
  }

  @Override
  public Tree buildMenuTree(boolean needButton, boolean isAdmin, Object[] roleIds) {
    List<SysMenu> menuList = super.list();
    // 筛选出除按钮级别的菜单
    if (!needButton) {
      menuList = menuList.stream().filter(this::cancelButton).collect(Collectors.toList());
    }
    // 查询角色已经拥有的菜单
    List<Object> menuIds = isAdmin
        ? super.listObjs(new QueryWrapper<SysMenu>().select("menu_id"))
        : roleMenuService.listObjs(new QueryWrapper<SysRoleMenu>().select("menu_id").in("role_id", roleIds));
    // 过滤掉重复的菜单ID
    menuIds = new ArrayList<>(menuIds.stream().collect(Collectors.groupingBy(Object::toString, Collectors.toList())).keySet());
    // 筛选出需要的菜单
    List<SysMenu> newMenuList = new ArrayList<>();
    List<Object> finalMenuIds = menuIds;
    menuList.forEach(menu ->
        finalMenuIds.forEach(menuId -> {
          if (Objects.equals(menuId.toString(), menu.getMenuId())) {
            newMenuList.add(menu);
          }
        })
    );
    // 构建菜单树
    Tree menuTree = buildTree(newMenuList, menuIds);
    TreeUtil.addRoot(menuTree, "菜单权限");
    return menuTree;
  }

  @Override
  @Transactional(rollbackFor = LsException.class)
  public int add(SysMenuModel model) {
    // 参数校验
    checkedSysMenu(model, OperateEnum.INSERT);
    // 信息入库
    if (!super.save(model)) {
      throw new LsException("新增菜单信息入库异常！");
    }
    return 0;
  }

  @Override
  @Transactional(rollbackFor = LsException.class)
  public int edit(SysMenuModel model) {
    // 参数校验
    checkedSysMenu(model, OperateEnum.UPDATE);
    // 信息入库
    if (!super.updateById(model)) {
      throw new LsException("修改菜单信息入库异常！");
    }
    return 0;
  }

  /**
   * 筛选出除按钮级别的菜单
   *
   * @param menu
   * @return
   */
  private boolean cancelButton(SysMenu menu) {
    return !Objects.equals(menu.getMenuType(), MenuEnum.MenuType.F);
  }

  /**
   * 构建树
   *
   * @param menuList 菜单集合
   * @param viewIds  已有菜单
   * @return
   */
  private Tree buildTree(List<SysMenu> menuList, List<Object> viewIds) {
    return new Tree().buildTree(menuList, viewIds, new ITreeBuilder<SysMenu>() {
      @Override
      public String getId(SysMenu menu) {
        return menu.getMenuId();
      }

      @Override
      public String getPid(SysMenu menu) {
        return menu.getParentId();
      }

      @Override
      public String getName(SysMenu menu) {
        return menu.getMenuName();
      }
    });
  }

  /**
   * 参数校验
   *
   * @param model
   */
  private void checkedSysMenu(SysMenu model, OperateEnum operate) {
    // 新增/修改通用参数非空校验
    if (StringUtil.isBlank(model.getParentId())) {
      throw new LsException(ResultEnum.PARAM_MISSING_ERROR, "父级菜单ID不能为空！");
    }
    if (StringUtil.isBlank(model.getMenuName())) {
      throw new LsException(ResultEnum.PARAM_MISSING_ERROR, "菜单名称不能为空！");
    }
    if (Objects.isNull(model.getMenuType())) {
      throw new LsException(ResultEnum.PARAM_MISSING_ERROR, "菜单类型不能为空！");
    }
    // 不为主目录时需要校验父级菜单
    if (!Objects.equals(model.getParentId(), TreeUtil.TREE_ROOT)) {
      SysMenu parentMenu = super.getById(model.getParentId());
      if (Objects.isNull(parentMenu)) {
        throw new LsException(ResultEnum.PARAM_CHECKED_ERROR, "父级菜单不存在！");
      }
    }
    // 校验菜单类型是否存在
    if (Objects.nonNull(model.getMenuType())) {
      MenuEnum.MenuType menuType = MenuEnum.MenuType.getByType(model.getMenuType());
      if (Objects.isNull(menuType)) {
        throw new LsException(ResultEnum.PARAM_CHECKED_ERROR, "此菜单类型不存在！");
      }
    }
    // 校验打开方式是否存在
    if (Objects.nonNull(model.getTarget())) {
      MenuEnum.Target target = MenuEnum.Target.getByTarget(model.getTarget());
      if (Objects.isNull(target)) {
        throw new LsException(ResultEnum.PARAM_CHECKED_ERROR, "此打开方式不存在！");
      }
    }
    // 校验菜单状态是否存在
    if (Objects.nonNull(model.getVisible())) {
      MenuEnum.Visible visible = MenuEnum.Visible.getByVisible(model.getVisible());
      if (Objects.isNull(visible)) {
        throw new LsException(ResultEnum.PARAM_CHECKED_ERROR, "此菜单状态不存在！");
      }
    }
    // 菜单名查询条件组装
    QueryWrapper<SysMenu> menuNameWrapper = new QueryWrapper<>();
    menuNameWrapper.eq("parent_id", model.getParentId()).eq("menu_name", model.getMenuName());
    if (Objects.equals(operate, OperateEnum.INSERT)) {
      if (StringUtil.isNotBlank(model.getMenuId())) {
        SysMenu dbMenu = super.getById(model.getMenuId());
        if (Objects.nonNull(dbMenu)) {
          throw new LsException(ResultEnum.PARAM_CHECKED_ERROR, "此菜单信息已存在！");
        }
      }
    } else if (Objects.equals(operate, OperateEnum.UPDATE)) {
      if (StringUtil.isBlank(model.getMenuId())) {
        throw new LsException(ResultEnum.PARAM_MISSING_ERROR, "菜单ID不能为空！");
      }
      SysMenu dbMenu = super.getById(model.getMenuId());
      if (Objects.isNull(dbMenu)) {
        throw new LsException(ResultEnum.PARAM_CHECKED_ERROR, "此菜单信息不存在！");
      }
      menuNameWrapper.ne("menu_id", model.getMenuId());
    }
    // 校验同级目录下菜单名是否重复
    List<SysMenu> menuNameList = super.list(menuNameWrapper);
    if (menuNameList.size() > 0) {
      throw new LsException(ResultEnum.PARAM_CHECKED_ERROR, "此菜单名称已存在！");
    }
  }
}
