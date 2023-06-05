package foxconn.fit.controller.bi;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.service.base.UserDetailImpl;
import foxconn.fit.service.bi.PoTableService;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.SecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequestMapping("/bi/poIntegrationList")
public class PoIntegrationListController extends BaseController {
    @Autowired
    private PoTableService poTableService;


    @RequestMapping(value = "index")
    public String index(Model model, HttpServletRequest request) {
        model=poTableService.index(model,request);
        return "/bi/poIntegrationList/index";
    }

    @RequestMapping(value = "/list")
    public String list(Model model, PageRequest pageRequest, HttpServletRequest request, String dateYear,
                       String date, String dateEnd, String tableName,String flag,
                       String poCenter, String sbuVal, String priceControl,String commodity,String founderVal) {
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            Assert.hasText(tableName, getLanguage(locale, "明細表不能為空", "The table cannot be empty"));
            HttpSession session = request.getSession(false);
            if(String.valueOf(session.getAttribute("detailsTsak")).equalsIgnoreCase("ok")){
                session.setAttribute("detailsTsak","");
            }
            model=poTableService.list(model,pageRequest,locale,dateYear,date,dateEnd,tableName,flag,poCenter,sbuVal,priceControl,commodity,founderVal);
            int index = 1;
            if (pageRequest.getPageNo() > 1) {
                index = 2;
            }
            model.addAttribute("index", index);
        } catch (Exception e) {
            logger.error("查询明细配置表列表失败:", e);
        }
        return "/bi/poIntegrationList/list";
    }

    @RequestMapping(value = "/cpo")
    public String cpo(Model model, PageRequest pageRequest,String date) {
        try {
            Page<Object[]> page=poTableService.cpo(date,pageRequest);
            model.addAttribute("page", page);
            int index=1;
            if(pageRequest.getPageNo()>1){
                index=2;
            }
            model.addAttribute("index", index);
        } catch (Exception e) {
            logger.error("查询明细配置表列表失败:", e);
        }
        return "/bi/poIntegrationList/cpo";
    }


    @RequestMapping(value = "/delete")
    @ResponseBody
    @Log(name = "採購-->数据删除")
    public String delete(AjaxResult ajaxResult, @Log(name = "Id") String id, @Log(name = "表名") String tableName) {
        try {
            if (StringUtils.isNotEmpty(id)) {
                String[] ids = id.split(",");
                String idStr = "";
                for (String s : ids) {
                    idStr = idStr + "'" + s + "',";
                }
                idStr = idStr.substring(0, idStr.length() - 1);
                if ("FIT_PO_CD_MONTH_DTL".equalsIgnoreCase(tableName)) {
                    tableName = "FIT_PO_CD_MONTH_DOWN";
                } else if ("FIT_PO_SBU_YEAR_CD_SUM".equalsIgnoreCase(tableName)) {
                    String cpoSql = " select COUNT(*) FROM FIT_PO_Target_CPO_CD_DTL " +
                            " WHERE FLAG='0' AND YEAR IN (SELECT YEAR FROM FIT_PO_SBU_YEAR_CD_SUM " +
                            " WHERE ID IN (" + idStr + "))";
                    List<Map> maps = poTableService.listMapBySql(cpoSql);
                    if (maps != null && !"0".equals(maps.get(0).get("COUNT(*)").toString())) {
                        ajaxResult.put("flag", "fail");
                        ajaxResult.put("msg", "删除的sbu数据所对应的CPO任务已流转，不能删除");
                        return ajaxResult.getJson();
                    }
                }
                String sql = " SELECT COUNT(*) FROM "
                        + tableName + " WHERE ID IN ( "
                        + idStr + ") AND FLAG!='0'";
                List<Map> countMaps = poTableService.listMapBySql(sql);
                if (countMaps != null && !"0".equals(countMaps.get(0).get("COUNT(*)").toString())) {
                    ajaxResult.put("flag", "fail");
                    ajaxResult.put("msg", "删除数据对应的任务已提交，数据不能删除");
                    return ajaxResult.getJson();
                } else {
                    poTableService.deleteAll(idStr, tableName);
                }
            } else {
                ajaxResult.put("flag", "fail");
                ajaxResult.put("msg", "删除数据异常,请稍后再试");
            }
        } catch (Exception e) {
            logger.error("刪除" + tableName + "数据失败", e);
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "刪除" + tableName + "失败(delete " + tableName + " Fail) : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult.getJson();
    }


    @RequestMapping(value = "/selectCommdity")
    @ResponseBody
    public Map<String,List> selectCommdity(String functionName){
        if(functionName.isEmpty()){
            return  poTableService.selectCommodity();
        }
        Map<String,List> map=new HashMap<>();
        List<String> listCommodity=poTableService.listBySql("select distinct COMMODITY_MAJOR  from BIDEV.v_dm_d_commodity_major where FUNCTION_NAME='"+functionName+"'");
        map.put(functionName,listCommodity);
        return map;
    }

    /**
     * 页面下载权限把控
     * @param ajaxResult
     * @param tableName
     * @return
     */
    @RequestMapping(value = "/downloadCheck")
    @ResponseBody
    public String downloadCheck(AjaxResult ajaxResult, String tableName) {
        try {
            UserDetailImpl loginUser = SecurityUtils.getLoginUser();
            String userName = loginUser.getUsername();
            String permSql = "select listagg(r.TABLE_PERMS, ',') within GROUP(ORDER BY r.id) as TABLE_PERMS  from  fit_user u \n" +
                    " left join FIT_PO_AUDIT_ROLE_USER ur on u.id=ur.user_id \n" +
                    " left join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id\n" +
                    " where u.username=" + "'" + userName + "'";
            List<String> perms = poTableService.listBySql(permSql);
            String whereSql="";
            if (perms.get(0) != null && perms.get(0).length() != 0) {
                String perm = perms.get(0);
                String[] berSplit = perm.split(",");
                List list = Arrays.asList(berSplit);
                Set set = new HashSet(list);
                String[] split = (String[]) set.toArray(new String[0]);
                for (String s : split) {
                    whereSql += "'" + s + "',";
                }
            }
            whereSql += "'a'";
            String uploadSql = "select count(1) from FIT_PO_TABLE a,FIT_PO_BUTTON_ROLE b" +
                    " where a.table_name=b.form_name and type in('PO','CPO') and b.BUTTONS_TYPE=1 and b.role_id in ("+whereSql+") and a.TABLE_NAME ='"+tableName+"'";
            List<Map> maps = poTableService.listMapBySql(uploadSql);
            if (maps == null || "0".equals(maps.get(0).get("COUNT(1)").toString())) {
                ajaxResult.put("flag", "fail");
                return ajaxResult.getJson();
            }
        } catch (Exception e) {
            ajaxResult.put("flag", "fail");
        }
        return ajaxResult.getJson();
    }
}
