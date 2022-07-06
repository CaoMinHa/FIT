package foxconn.fit.controller.bi;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.bi.PoTable;
import foxconn.fit.service.bi.PoTableService;
import foxconn.fit.service.bi.RtActualEstimateService;
import foxconn.fit.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/bi/rtActualEstimate")
public class RtActualEstimateController extends BaseController {

    @Autowired
    private PoTableService poTableService;

    @Autowired
    private RtActualEstimateService rtActualEstimateService;

    @RequestMapping(value = "index")
    public String index(Model model, HttpServletRequest request) {
        try {
            List<Map> list=rtActualEstimateService.selectQuery(request);
            model.addAttribute("queryList",list);
        }catch (Exception e){
            logger.error("查詢頁面條件結果集失敗！", e);
        }
        return "/bi/rtActualEstimate/index";
    }

    @RequestMapping(value = "/list")
    public String list(Model model, PageRequest pageRequest, HttpServletRequest request,String queryCondition,String saleSorg) {
        try {
            PoTable poTable = poTableService.get("bidev.cux_actual_target_rev_v");
            String sql = rtActualEstimateService.selectDataSql(queryCondition,poTable,model,saleSorg);
            pageRequest.setPageSize(15);
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
        return "/bi/rtActualEstimate/list";
    }



    @RequestMapping(value = "download")
    @ResponseBody
    @Log(name = "實際+預估報表-->下载")
    public synchronized String download(HttpServletRequest request, HttpServletResponse response, PageRequest pageRequest, AjaxResult result,
            @Log(name = "查询条件") String queryCondition,String saleSorg) {
        try {
            PoTable poTable = poTableService.get("bidev.cux_actual_target_rev_v");
            String fileName=rtActualEstimateService.downloadFile(queryCondition,poTable,request,pageRequest,saleSorg);
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
