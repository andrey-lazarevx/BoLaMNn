package com.budwk.app.web.controllers.platform.wx;

import com.budwk.app.base.constant.RedisConstant;
import com.budwk.app.base.result.Result;
import com.budwk.app.base.utils.PageUtil;
import com.budwk.app.web.commons.auth.utils.SecurityUtil;
import com.budwk.app.web.commons.slog.annotation.SLog;
import com.budwk.app.wx.models.Wx_config;
import com.budwk.app.wx.services.WxConfigService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.nutz.dao.Cnd;
import org.nutz.integration.jedis.pubsub.PubSubService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import javax.servlet.http.HttpServletRequest;

;

/**
 * Created by wizzer on 2016/7/3.
 */
@IocBean
@At("/platform/wx/conf/account")
public class WxConfigController {
    private static final Log log = Logs.get();
    @Inject
    private WxConfigService wxConfigService;
    @Inject
    private PubSubService pubSubService;

    @At("")
    @Ok("beetl:/platform/wx/account/index.html")
    @SaCheckPermission("wx.conf.account")
    public void index() {

    }

    @At
    @Ok("json")
    @SaCheckPermission("wx.conf.account.add")
    @SLog(tag = "添加帐号", msg = "帐号名称:${args[0].appname}")
    public Object addDo(@Param("..") Wx_config conf, HttpServletRequest req) {
        try {
            int num = wxConfigService.count(Cnd.where("id", "=", conf.getId()));
            if (num > 0) {
                return Result.error("唯一标识已存在,请更换");
            }
            conf.setCreatedBy(SecurityUtil.getUserId());
            wxConfigService.insert(conf);
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/edit/?")
    @Ok("json")
    @SaCheckPermission("wx.conf.account")
    public Object edit(String id) {
        try {
            return Result.success().addData(wxConfigService.fetch(id));
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At
    @Ok("json")
    @SaCheckPermission("wx.conf.account.edit")
    @SLog(tag = "修改帐号", msg = "帐号名称:${args[0].appname}")
    public Object editDo(@Param("..") Wx_config conf, HttpServletRequest req) {
        try {
            wxConfigService.updateIgnoreNull(conf);
            pubSubService.fire(RedisConstant.PLATFORM_REDIS_PREFIX + "web:platform", "sys_wx");
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/delete/?")
    @Ok("json")
    @SaCheckPermission("wx.conf.account.delete")
    @SLog(tag = "删除帐号", msg = "帐号名称:${args[1].getAttribute('appname')}")
    public Object delete(String id, HttpServletRequest req) {
        try {
            req.setAttribute("appname", wxConfigService.fetch(id).getAppname());
            wxConfigService.delete(id);
            pubSubService.fire(RedisConstant.PLATFORM_REDIS_PREFIX + "web:platform", "sys_wx");
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At
    @Ok("json:full")
    @SaCheckPermission("wx.conf.account")
    public Object data(@Param("searchName") String searchName, @Param("searchKeyword") String searchKeyword, @Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        try {
            Cnd cnd = Cnd.NEW();
            if (!Strings.isBlank(searchName) && !Strings.isBlank(searchKeyword)) {
                cnd.and(searchName, "like", "%" + searchKeyword + "%");
            }
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
            }
            return Result.success().addData(wxConfigService.listPage(pageNumber, pageSize, cnd));
        } catch (Exception e) {
            return Result.error();
        }
    }
}
