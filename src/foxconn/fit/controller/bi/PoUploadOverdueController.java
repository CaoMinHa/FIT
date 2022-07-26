package foxconn.fit.controller.bi;

import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.base.User;
import foxconn.fit.service.base.UserService;
import foxconn.fit.service.bi.PoUploadOverdueService;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.SecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/bi/poUploadOverdue")
public class PoUploadOverdueController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private PoUploadOverdueService poUploadOverdueService;

    @RequestMapping(value = "index")
    public String index(PageRequest pageRequest, Model model, HttpServletRequest request) {
        try {
            pageRequest.setOrderBy("serial");
            pageRequest.setOrderDir("asc");
            User user = userService.getByUsername(SecurityUtils.getLoginUsername());
            model.addAttribute("attribute", user.getAttribute());
        } catch (Exception e) {
            logger.error("查询明细配置表列表信息失败", e);
        }
        return "/bi/poUploadOverdue/index";
    }

    @RequestMapping(value="/list")
    public String userList(Model model,PageRequest pageRequest,HttpServletRequest request,String query) {
        try {
            String sql="select * from FIT_USER_PO_UPLOAD_V where 1=1";
            if (StringUtils.isNotEmpty(query)) {
                String[] params = query.split("&");
                for (String param : params) {
                    String columnName = param.substring(0, param.indexOf("=")).trim();
                    String columnValue = param.substring(param.indexOf("=") + 1).trim();
                    if (StringUtils.isNotEmpty(columnValue)) {
                        if ("username".equalsIgnoreCase(columnName)) {
                            sql += " and USERNAME like '%" + columnValue + "%' or  REALNAME like '%" + columnValue + "%'";
                        } else if ("state".equalsIgnoreCase(columnName)) {
                            sql += " and state='" + columnValue + "'";
                        } else {
                            sql += " and instr(','||" + columnName + "||',','," + columnValue + ",') > 0";
                        }
                    }
                }
            }
            pageRequest.setOrderBy("ID");
            System.out.println(sql);
            Page<Object[]> page = userService.findPageBySql(pageRequest,sql);
            int index=1;
            if(pageRequest.getPageNo()>1){
                index=2;
            }
            model.addAttribute("index", index);
            model.addAttribute("tableName", "FIT_USER");
            model.addAttribute("page", page);
        } catch (Exception e) {
            logger.error("查询明细配置表列表失败:", e);
        }
        return "/bi/poUploadOverdue/list";
    }

    @RequestMapping(value="/allocate")
    @ResponseBody
    public String delete(HttpServletRequest request,AjaxResult ajaxResult,String userId,String type){
        try {
            poUploadOverdueService.updateState(userId,type);
        } catch (Exception e) {
            logger.error("删除失败:", e);
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg","操作失敗 : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult.getJson();
    }
}