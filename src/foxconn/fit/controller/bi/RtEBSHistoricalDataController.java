package foxconn.fit.controller.bi;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.bi.PoColumns;
import foxconn.fit.entity.bi.PoTable;
import foxconn.fit.service.bi.PoTableService;
import foxconn.fit.service.bi.RtEBSHistoricalDataService;
import foxconn.fit.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import java.util.Map;

@Controller
@RequestMapping("/bi/rtEBSHistoricalData")
public class RtEBSHistoricalDataController extends BaseController {

    @Autowired
    private PoTableService poTableService;

    @Autowired
    private RtEBSHistoricalDataService rtEBSHistoricalDataService;

    @RequestMapping(value = "index")
    public String index(Model model, HttpServletRequest request) {
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            List<Map> list=rtEBSHistoricalDataService.selectQuery(request);
            PoTable poTable = poTableService.get("if_ebs_ar_revenue_dtl");
            List<PoColumns> columns = poTable.getColumns();
            for (PoColumns poColumns : columns) {
                poColumns.setComments(getByLocale(locale, poColumns.getComments()));
            }
            model.addAttribute("columns", columns);
            model.addAttribute("queryList",list);
        }catch (Exception e){
            logger.error("查詢頁面條件結果集失敗！", e);
        }
        return "/bi/rtEBSHistoricalData/index";
    }

    @RequestMapping(value = "/list")
    public String list(Model model, PageRequest pageRequest, HttpServletRequest request,String queryCondition,String columns) {
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            String sql = rtEBSHistoricalDataService.selectDataSql(queryCondition,columns,locale,model);
            Page<Object[]> page = poTableService.findPageBySql(pageRequest, sql);
            int index = 1;
            if (pageRequest.getPageNo() > 1) {
                index = 2;
            }
            model.addAttribute("index", index);
            model.addAttribute("page", page);
        } catch (Exception e) {
            logger.error("查詢數據失敗(Failed to query data)", e);
        }
        return "/bi/rtEBSHistoricalData/list";
    }



    @RequestMapping(value = "download")
    @ResponseBody
    @Log(name = "EBS營收歷史數據表-->下载")
    public synchronized String download(HttpServletRequest request, HttpServletResponse response, PageRequest pageRequest, AjaxResult result,
            @Log(name = "查询条件") String queryCondition,String columns) {
        try {
            PoTable poTable = poTableService.get("if_ebs_ar_revenue_dtl");
            String fileName=rtEBSHistoricalDataService.downloadFile(queryCondition,poTable,request,pageRequest,columns);
            result.put("fileName",fileName);
            System.gc();
        } catch (Exception e) {
            logger.error("下载Excel失败", e);
            result.put("flag", "fail");
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }

        return result.getJson();
    }

}
