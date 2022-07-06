package foxconn.fit.controller.bi;

import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.bi.PoEmailLog;
import foxconn.fit.service.bi.PoEmailLogService;
import foxconn.fit.service.bi.PoTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * 郵件通知
 */
@Controller
@RequestMapping("/bi/poEmailLog")
public class PoEmailLogController extends BaseController {
    @Autowired
    private PoEmailLogService poEmailLogService;

    @Autowired
    private PoTableService poTableService;

    @RequestMapping(value = "index")
    public String index(Model model, HttpServletRequest request) {
        return "/bi/poEmailLog/index";
    }

    @RequestMapping(value="/list")
    public String list(Model model, PageRequest pageRequest,HttpServletRequest request,String title,String name,String date,String dateEnd) {
        try {
            Page<Object[]>  page= poEmailLogService.selectList(pageRequest,title,name,date,dateEnd);
            model.addAttribute("page", page);
        } catch (Exception e) {
            logger.error("查詢郵件日志列表失敗：", e);
        }
        return "/bi/poEmailLog/list";
    }

    @RequestMapping(value = "/details")
    public String details(Model model, HttpServletRequest request,String id){
        try {
            String sql="select * from CUX_PO_EMAIL where ID='"+id+"'";
            List<PoEmailLog> list=poTableService.listBySql(sql,PoEmailLog.class);
            if(null!=list.get(0) && list.size()>0){
                if(null!=list.get(0).getFileName() && !list.get(0).getFileName().isEmpty()){
                    List<String> fileList=Arrays.asList(list.get(0).getFileName().split("\\*\\*"));
                    model.addAttribute("fileList", fileList);
                }
                model.addAttribute("poEmailLog", list.get(0));
            }
        } catch (Exception e) {
            logger.error("查詢郵件日志詳情失敗：", e);
        }
        return "/bi/poEmailLog/details";
    }
}
