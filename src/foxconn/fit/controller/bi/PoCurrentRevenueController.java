package foxconn.fit.controller.bi;

import com.monitorjbl.xlsx.StreamingReader;
import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.bi.PoCurrentRevenue;
import foxconn.fit.entity.investment.DepreExpenBudget;
import foxconn.fit.service.bi.PlOfflineDataSupplementService;
import foxconn.fit.service.bi.PoCurrentRevenueService;
import foxconn.fit.service.bi.PoTableService;
import foxconn.fit.util.ExceptionUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author maggao
 */
@Controller
@RequestMapping("/bi/poCurrentRevenue")
public class PoCurrentRevenueController extends BaseController {
    
    @Autowired
    private PoCurrentRevenueService poCurrentRevenueService;

    @RequestMapping(value = "index")
    public String index(Model model) {
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        model.addAttribute("PERIOD",year+"-"+month);
        return "/bi/poCurrentRevenue/index";
    }

    @RequestMapping(value = "/list")
    @Log(name="非FIT體系當期收入表-->查詢")
    public String list(Model model, PageRequest pageRequest, HttpServletRequest request,@Log(name = "條件") String queryCondition) {
        try {
            String sql = poCurrentRevenueService.selectDataSql(queryCondition);
            Page<Object[]> page = poCurrentRevenueService.findPageBySql(pageRequest, sql);
            model.addAttribute("page", page);
            int index = 1;
            if (pageRequest.getPageNo() > 1) {
                index = 2;
            }
            model.addAttribute("index", index);
            model.addAttribute("page", page);
        } catch (Exception e) {
            logger.error("查詢數據失敗(Failed to query data)", e);
        }
        return "/bi/poCurrentRevenue/list";
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    @Log(name = "非FIT體系當期收入表-->上传")
    public String upload(HttpServletRequest request,HttpServletResponse response, AjaxResult result) {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        result.put("msg", getLanguage(locale, "上傳成功", "Upload success"));
        try {
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
                String str =poCurrentRevenueService.uploadFile(multipartHttpServletRequest,result,locale);
                return str;
        } catch (Exception e) {
            logger.error("保存文件失败", e);
            result.put("flag", "fail");
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }
        return result.getJson();
    }

    @RequestMapping(value = "download")
    @ResponseBody
    @Log(name = "非FIT體系當期收入表-->下载")
    public synchronized String download(HttpServletRequest request, HttpServletResponse response, PageRequest pageRequest, AjaxResult result,
            @Log(name = "查询条件") String queryCondition) {
        try {
            String fileName=poCurrentRevenueService.downloadFile(queryCondition,request,pageRequest);
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
    @Log(name = "非FIT體系當期收入表-->刪除")
    public String deleteAll(AjaxResult ajaxResult, HttpServletRequest request, @Log(name="刪除ID") String no) {
        ajaxResult= poCurrentRevenueService.deleteData(ajaxResult,no);
        return ajaxResult.getJson();
    }


    @RequestMapping(value = "template")
    @ResponseBody
    public synchronized String template(HttpServletRequest request, HttpServletResponse response, AjaxResult result,String tableName) {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        Map<String,String> map=poCurrentRevenueService.template(request);
        if(map.get("result").equals("Y")){
            result.put("fileName", map.get("file"));
        }else{
            result.put("flag", "fail");
            result.put("msg", getLanguage(locale, "下載模板文件失敗", "Fail to download template file") + " : " + map.get("str"));
        }
        return result.getJson();
    }

}
