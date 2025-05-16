package cn.wizzer.app.sys.modules.services;

import cn.wizzer.app.sys.modules.models.Sys_menu;
import cn.wizzer.app.sys.modules.models.Sys_user;
import cn.wizzer.framework.base.service.BaseService;

import java.util.List;

/**
 * Created by wizzer on 2016/12/22.
 */
public interface SysUserService extends BaseService<Sys_user> {
    /**
     * 查询用户的角色
     * @param user
     * @return
     */
    List<String> getRoleCodeList(Sys_user user);

    /**
     * 通过用户ID获取菜单及权限
     * @param userId
     * @return
     */
    List<Sys_menu> getMenusAndButtons(String userId);

    /**
     * 通过用户ID获取菜单
     * @param userId
     * @return
     */
    List<Sys_menu> getDatas(String userId);

    /**
     * 绑定菜单到用户
     * @param user
     */
    void fillMenu(Sys_user user);

    /**
     * 通过用户ID删除用户
     * @param userId
     */
    void deleteById(String userId);

    /**
     * 批量删除用户
     * @param userIds
     */
    void deleteByIds(String[] userIds);
}
