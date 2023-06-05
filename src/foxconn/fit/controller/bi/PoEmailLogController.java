package foxconn.fit.controller.bi;

import foxconn.fit.controller.BaseController;
import foxconn.fit.service.bi.PoEmailLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

/**
 * 郵件通知
 */
@Controller
@RequestMapping("/bi/poEmailLog")
public class PoEmailLogController extends BaseController {
    @Autowired
    private PoEmailLogService poEmailLogService;

    @RequestMapping(value = "index")
    public String index() {
        return "/bi/poEmailLog/index";
    }

    @RequestMapping(value="/list")
    public String list(Model model, PageRequest pageRequest,String title,String name,String date,String dateEnd) {
        try {
            Page<Object[]>  page= poEmailLogService.selectList(pageRequest,title,name,date,dateEnd);
            model.addAttribute("page", page);
        } catch (Exception e) {
            logger.error("查詢郵件日志列表失敗：", e);
        }
        return "/bi/poEmailLog/list";
    }

    @RequestMapping(value = "/details")
    public String details(Model model,String id){
        try {
            poEmailLogService.selectDetails(model,id);
        } catch (Exception e) {
            logger.error("查詢郵件日志詳情失敗：", e);
        }
        return "/bi/poEmailLog/details";
    }
}
