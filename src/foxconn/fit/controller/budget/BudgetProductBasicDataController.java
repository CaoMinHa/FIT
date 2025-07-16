package foxconn.fit.controller.budget;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springside.modules.orm.PageRequest;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.dao.base.PropertyFilter;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.task.bi.BudgetProductBasicDataTask;
import foxconn.fit.util.DateUtil;
import foxconn.fit.util.ExcelUtil;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.MyUUIDGenerator;
import foxconn.fit.util.SecurityUtils;
import foxconn.fit.util.SpringContextHelper;


@Controller
@RequestMapping("/bi/BudgetProductBasicData")
public class BudgetProductBasicDataController extends BaseController {

	private static int COLUMN_NUM=11;
	
	@Value("${BudgetProductBasicData_url}")
	private String url;
	

	@RequestMapping(value = "index")
	public String index(HttpServletRequest request, Model model) {
		return "/bi/BudgetProductBasicData/index";
	}

	@RequestMapping(value = "dataExtract")
	@ResponseBody
	@Log(name = "Planning-->料號拉取")
	public String dataExtract(HttpServletRequest request,HttpServletResponse response, AjaxResult result) {
		result.put("msg", "数据抽取成功");
		try{				
			List<String> extractList=new ArrayList<String>();
			String taskId=MyUUIDGenerator.getUUID();
			extractList.add(taskId);
            BudgetProductBasicDataTask task = (BudgetProductBasicDataTask) SpringContextHelper.getBean("budgetProductBasicDataTask");
			//task.setParam(taskId, request.getSession().getId(), url);
            task.setParam(taskId, url);
			new Thread(task).start();
			
			result.put("extractList", extractList);
		} catch (Exception e) {
			logger.error("数据抽取失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}

		return result.getJson();
	}		
}
