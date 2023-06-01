package foxconn.fit.controller.bi;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.bi.PoTable;
import foxconn.fit.service.bi.PmrCommonService;
import foxconn.fit.service.bi.PmrPeerUpdatesService;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author maggao
 */
@Controller
@RequestMapping("/bi/pmrPeerUpdates")
public class PmrPeerUpdatesController extends BaseController {

    @Autowired
    private PmrCommonService pmrCommonService;

    @Autowired
    private PmrPeerUpdatesService pmrPeerUpdatesService;

    private static String tableName="PMR_PEER_UPDATES";
    @RequestMapping(value = "index")
    public String index(Model model, HttpServletRequest request) {
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            List<Map> list=pmrCommonService.selectQuery(locale,tableName);
            model.addAttribute("queryList",list);
        }catch (Exception e){
            logger.error("查詢頁面條件結果集失敗！", e);
        }
        return "/bi/pmrPeerUpdates/index";
    }

    @RequestMapping(value = "/list")
    public String list(Model model, PageRequest pageRequest, HttpServletRequest request,String queryCondition) {
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            PoTable poTable = pmrCommonService.get(tableName);
            String sql = pmrPeerUpdatesService.selectDataSql(queryCondition,locale,model,poTable);
            pageRequest.setPageSize(5);
            Page<Object[]> page = pmrCommonService.findPageBySql(pageRequest, sql);
            int index = 1;
            if (pageRequest.getPageNo() > 1) {
                index = 2;
            }
            model.addAttribute("index", index);
            model.addAttribute("page", page);
        } catch (Exception e) {
            logger.error("查詢數據失敗(Failed to query data)", e);
        }
        return "/bi/pmrPeerUpdates/list";
    }

    @RequestMapping(value = "/add")
    @ResponseBody
    @Log(name = "Peer Updates-->添加")
    public String add(AjaxResult result,String dateAdd,String peerUpdates) {
        try {
            result=pmrPeerUpdatesService.add(result,dateAdd,peerUpdates);
        } catch (Exception e) {
            logger.error("添加數據失敗(Failed to add data)", e);
        }
        return result.getJson();
    }

    @RequestMapping(value = "download")
    @ResponseBody
    @Log(name = "Peer Updates-->下载")
    public synchronized String download(HttpServletRequest request, PageRequest pageRequest, AjaxResult result,
            @Log(name = "下載條件") String queryCondition) {
        try {
            PoTable poTable = pmrCommonService.get(tableName);
            String fileName=pmrCommonService.downloadFile(queryCondition,poTable,request,pageRequest);
            result.put("fileName",fileName);
            System.gc();
        } catch (Exception e) {
            logger.error("下载Excel失败", e);
            result.put("flag", "fail");
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }

        return result.getJson();
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    @Log(name = "Peer Updates-->刪除")
    public String delete(AjaxResult ajaxResult, String no) {
        ajaxResult= pmrPeerUpdatesService.deleteData(ajaxResult,no);
        return ajaxResult.getJson();
    }
}
