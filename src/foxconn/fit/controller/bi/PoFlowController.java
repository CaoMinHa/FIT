package foxconn.fit.controller.bi;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.dao.base.PropertyFilter;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.base.User;
import foxconn.fit.entity.bi.PoColumns;
import foxconn.fit.entity.bi.PoTable;
import foxconn.fit.service.base.MasterDataService;
import foxconn.fit.service.base.UserService;
import foxconn.fit.service.bi.PoFlowService;
import foxconn.fit.service.bi.PoTableService;
import foxconn.fit.util.DateUtil;
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
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Locale;

/**
 * @author Yang DaiSheng
 * @program fit
 * @description 作爲采購審核模塊開發
 * @create 2021-04-20 16:04
 **/
@Controller
@RequestMapping("/bi/poFlow")
public class PoFlowController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private PoTableService poTableService;
    @Autowired
    private PoFlowService poFlowService;


    @RequestMapping(value = "index")
    public String index(PageRequest pageRequest, Model model, HttpServletRequest request) {
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            pageRequest.setOrderBy("serial");
            pageRequest.setOrderDir("asc");

            User user = userService.getByUsername(SecurityUtils.getLoginUsername());
            model.addAttribute("attribute", user.getAttribute());

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("查询明细配置表列表信息失败", e);
        }
        return "/bi/poFlow/index";
    }
    @RequestMapping(value="/list")
    public String list(Model model, PageRequest pageRequest,HttpServletRequest request,String date,String tableName) {
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            Assert.hasText(tableName, getLanguage(locale, "明細表不能為空", "The table cannot be empty"));
            String flagSql="select  flag  " + "from "+tableName+ " where year='"+date+"'  and flag is null";
            List <String> flags= poTableService.listBySql(flagSql);
            if(flags!=null&&flags.size()>0){
                String flag=flags.get(0);
                if(flag==null){
                    poFlowService.executeCpo(date);
                    System.out.println("實時計算date");
                }
            }else{
                poFlowService.executeCpo(date);
            }
            String sql="select ID,PO_CENTER,COMMODITY_MAJOR,NO_PO_TOTAL, NO_CD ,NO_CPO ,PO_TOTAL,CD,CPO  " +
                    "from "+tableName+ " where year="+date+" order by ID,PO_CENTER";

            if("FIT_PO_Target_CPO_CD_DTL".equals(tableName)){
                sql="select ID,PO_CENTER,COMMODITY_MAJOR ,NO_PO_TOTAL,NO_CD_AMOUNT, NO_CD ,NO_CPO,PO_TOTAL ,CD_AMOUNT, CD,CPO from FIT_PO_TARGET_CPO_CD_DTL_V  where year="+date;
            }
            pageRequest.setPageSize(poTableService.listBySql(sql).size());
            Page<Object[]> page = poTableService.findPageBySql(pageRequest, sql);
            int index=0;
            if(pageRequest.getPageNo()>1){
                index=1;
            }

            String countSbu=" select distinct tie.NEW_SBU_NAME from bidev.v_if_sbu_mapping tie  where tie.NEW_SBU_NAME IN ('PSS','ASD','FIAD','ACD','FAS','CRD','EMS','TMTS','IoT','ABS','Tengyang','FAB','TSC','IDS','APS','CW','AEC','CIDA','ACE','OLU','Other','FAD') ";
            String countNotUpload="select distinct tie.NEW_SBU_NAME from bidev.v_if_sbu_mapping tie where" +
                    "  tie.NEW_SBU_NAME  IN ('PSS','ASD','FIAD','ACD','FAS','CRD','EMS','TMTS','IoT','ABS','Tengyang','FAB','TSC','IDS','APS','CW','AEC','CIDA','ACE','OLU','Other','FAD')" +
                    " and tie.NEW_SBU_NAME not in" +
                    "(select distinct a.sbu from FIT_PO_SBU_YEAR_CD_SUM a where  a.flag=3 and a.year='"+date+"')";
            List <String> countSbulist= poTableService.listBySql(countSbu);
            List <String> countNotUploadlist= poTableService.listBySql(countNotUpload);

            model.addAttribute("countSUM",countSbulist.size());
            model.addAttribute("countNotUploadList",countNotUploadlist);
            model.addAttribute("countNotUploadNumber",countNotUploadlist.size());

            model.addAttribute("index", index);
            model.addAttribute("year", date);
            model.addAttribute("tableName", tableName);
            model.addAttribute("total", page.getTotalItems());
            model.addAttribute("page", page);
        } catch (Exception e) {
            logger.error("查询明细配置表列表失败:", e);
        }
        return "/bi/poFlow/list";
    }

    @RequestMapping(value = "update")
    @ResponseBody
    public String update(HttpServletRequest request, HttpServletResponse response, AjaxResult result,  String tableName, @Log(name = "更新条件") String updateData){
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            result.put("msg", getLanguage(locale,"更新成功","Update data success"));
            if (StringUtils.isNotEmpty(updateData)) {
                String updateSql="update "+tableName+" set ";
                String where="";
                String[] params = updateData.split("&");
                for (String param : params) {
                    String columnName = param.substring(0,param.indexOf("="));
                    String columnValue = param.substring(param.indexOf("=")+1).trim();
                    if ("ID".equalsIgnoreCase(columnName)) {
                        where=" where ID='"+columnValue+"'";
                    }else if("NO_CPO".equalsIgnoreCase(columnName)){
                        if (StringUtils.isNotEmpty(columnValue)) {
                            updateSql+="NO_CD='"+columnValue+"',"+columnName+"='"+columnValue+"',";
                        }
                    }else if("CPO".equalsIgnoreCase(columnName)){
                        if (StringUtils.isNotEmpty(columnValue)) {
                            updateSql+="CD='"+columnValue+"',"+columnName+"='"+columnValue+"',";
                        }
                    }
                }
                updateSql=updateSql.substring(0, updateSql.length()-1);
                updateSql+=where;
                System.out.println(updateSql);
                poFlowService.updateData(updateSql);
            }
        } catch (Exception e) {
            logger.error("更新採購映射表信息失败", e);
            result.put("flag", "fail");
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }

        return result.getJson();
    }
}