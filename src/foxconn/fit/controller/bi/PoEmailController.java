package foxconn.fit.controller.bi;

import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.service.bi.PoEmailService;
import foxconn.fit.service.bi.PoTableService;
import foxconn.fit.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 郵件通知
 */
@Controller
@RequestMapping("/bi/poEmail")
public class PoEmailController extends BaseController {
    @Autowired
    private PoEmailService poEmailService;
    @Autowired
    private PoTableService poTableService;

    @RequestMapping(value = "index")
    public String index(Model model, HttpServletRequest request) {
        try {
            List<String> list=poTableService.listBySql("select distinct user_group from CUX_PO_EMAIL_GROUP order by user_group");
            List<List<String>> listGroup=poEmailService.selectGroup(list);
            model.addAttribute("EmailUserTeam",list);
            model.addAttribute("listGroup",listGroup);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("查詢郵件分組失敗！", e);
        }
        return "/bi/poEmail/index";
    }

    @RequestMapping(value="/sendEmail")
    @ResponseBody
    public String submitTask(AjaxResult ajaxResult, HttpServletRequest request,String emailGroup,String title,String content,String type,String endDate) {
        try {
            if(type.equalsIgnoreCase("1")){
                ajaxResult=poEmailService.sendEmail(ajaxResult,emailGroup,title,content,endDate);
            }else {
                Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
                ajaxResult.put("msg", getLanguage(locale, "發送成功！", "Send success"));
//                MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
//                MultiValueMap<String, MultipartFile> mutipartFiles=multipartHttpServletRequest.getMultiFileMap();
                List<MultipartFile> list = new ArrayList<>();
                MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
                Iterator<String> fileNames = multipartRequest.getFileNames();
                if (fileNames.hasNext()) {
                    //request.getFiles(fileName)通过fileName这个Key，获得文件集合列表
                    List<MultipartFile> fileList = multipartRequest.getFiles("files[]");
                    if (fileList.size() > 0) {
                        //遍历文件列表
                        Iterator<MultipartFile> fileIte = fileList.iterator();
                        while (fileIte.hasNext()) {
                            //获得每一个文件
                            MultipartFile multipartFile = fileIte.next();
                            list.add(multipartFile);
                        }
                    }

                    if (list != null && list.size() > 0) {
                        ajaxResult = poEmailService.sendEmail(ajaxResult, emailGroup, title, content, list,request,endDate);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("通知窗口郵件發送失敗：", e);
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "郵件發送失敗(Send Task Fail) : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult.getJson();
    }

}
