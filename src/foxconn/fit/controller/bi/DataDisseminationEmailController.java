package foxconn.fit.controller.bi;

import foxconn.fit.advice.Log;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.service.bi.DataDisseminationEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/bi/dataDisseminationEmail")
public class DataDisseminationEmailController {

    @Autowired
    private DataDisseminationEmailService dataDisseminationEmailService;

    @RequestMapping(value = "index")
    public String index(Model model) {
        model=dataDisseminationEmailService.index(model);
        return "/bi/dataDisseminationEmail/index";
    }

    @RequestMapping(value = "plEmail")
    @ResponseBody
    @Log(name = "損益表數據更新郵件通知觸發")
    public String  plEmail(AjaxResult ajaxResult,@Log(name = "期間") String date){
       return dataDisseminationEmailService.plBIEmail(ajaxResult,date).getJson();
    }

    @RequestMapping(value = "rawdataEmail")
    @ResponseBody
    @Log(name = "rawdata數據更新郵件通知觸發")
    public String  rawdataEmail(AjaxResult ajaxResult,@Log(name = "期間") String date){
        return dataDisseminationEmailService.rawdataEmail(ajaxResult,date).getJson();
    }

    @RequestMapping(value = "bsEmail")
    @ResponseBody
    @Log(name = "資產負債表數據更新郵件通知觸發")
    public String  bsEmail(AjaxResult ajaxResult,@Log(name = "期間") String date){
        return dataDisseminationEmailService.bsEmail(ajaxResult,date).getJson();
    }

    @RequestMapping(value = "cfEmail")
    @ResponseBody
    @Log(name = "現金流量表數據更新郵件通知觸發")
    public String  cfEmail(AjaxResult ajaxResult,@Log(name = "期間") String date){
        return dataDisseminationEmailService.cfEmail(ajaxResult,date).getJson();
    }
}
