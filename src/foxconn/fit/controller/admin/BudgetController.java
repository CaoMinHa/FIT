package foxconn.fit.controller.admin;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.service.base.BudgetService;
import foxconn.fit.service.base.UserDetailImpl;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequestMapping("/bi/budget")
public class BudgetController extends BaseController {

	@Autowired
	private BudgetService budgetService;

	@RequestMapping(value = "download")
	@ResponseBody
	@Log(name = "提交銷售預算/預測")
	public synchronized String download(HttpServletRequest request,HttpServletResponse response,PageRequest pageRequest,AjaxResult result,
			@Log(name = "SBU") String sbu,@Log(name = "年") String year,@Log(name="場景")String scenarios){
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		result.put("msg", getLanguage(locale, "提交成功", "Submit successfully"));
		try {
			List<Map> list =new ArrayList<>();
			if (sbu.endsWith(",")) {
				sbu=sbu.substring(0,sbu.length()-1);
			}
			String entity="";
			for (String s : sbu.split(",")) {
				entity+=s+"|";
			}
			entity=entity.substring(0,entity.length()-1);
			List<Map> lists;
			String message = budgetService.generatePlanning(entity,year,scenarios);
			if (StringUtils.isNotEmpty(message)) {
				throw new RuntimeException(getLanguage(locale, "计算Budget数据出错 : ","There was an error calculating the Budget data:")+message);
			}
			UserDetailImpl loginUser = SecurityUtils.getLoginUser();
			String userName=loginUser.getUsername();
			String roleSql="select count(1) from  fit_user u \n" +
					" left join FIT_PO_AUDIT_ROLE_USER ur on u.id=ur.user_id \n" +
					" left join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id\n" +
					" WHERE  u.username="+"'"+userName+"' and code='BudgetForecast' ";
			List<BigDecimal> countList = (List<BigDecimal>)budgetService.listBySql(roleSql);
			result.put("role","NO");
			if(countList.get(0).intValue()>0){
				result.put("role","YES");
			}else{
				return result.getJson();
			}
			if(scenarios.equals("budget")){
				String sql="select ACCOUNT,JAN,FEB, MAR,APR,MAY,JUN,JUL,AUG,SEP,OCT,NOV,DEC,YT,POINT_OF_VIEW,DATA_LOAD_CUBE_NAME from CUX_FIT_PLANNING_V " +
						"where sbu in('"+sbu.replace(",","','")+"') and current_year='"+year+"'";
				lists = budgetService.listMapBySql(sql);
			}else{
				String sql="select ACCOUNT,JAN,FEB, MAR,APR,MAY,JUN,JUL,AUG,SEP,OCT,NOV,DEC,YT,POINT_OF_VIEW,DATA_LOAD_CUBE_NAME from cux_forecast_planning_v";
				lists = budgetService.listMapBySql(sql);
			}
			list.addAll(lists);
			String realPath = request.getRealPath("");
			if (CollectionUtils.isNotEmpty(list)) {
				String filePath=realPath+"static"+File.separator+"download"+File.separator+"Hyperion.xlsx";
				InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"budget"+File.separator+"Hyperion.xlsx");
				XSSFWorkbook workBook = new XSSFWorkbook(ins);
				Sheet sheet = workBook.getSheetAt(0);
				Row row =sheet.createRow(0);
				row.createCell(0).setCellValue("Account");
				row.createCell(1).setCellValue("Jan");
				row.createCell(2).setCellValue("Feb");
				row.createCell(3).setCellValue("Mar");
				row.createCell(4).setCellValue("Apr");
				row.createCell(5).setCellValue("May");
				row.createCell(6).setCellValue("Jun");
				row.createCell(7).setCellValue("Jul");
				row.createCell(8).setCellValue("Aug");
				row.createCell(9).setCellValue("Sep");
				row.createCell(10).setCellValue("Oct");
				row.createCell(11).setCellValue("Nov");
				row.createCell(12).setCellValue("Dec");
				row.createCell(13).setCellValue("YT");
				row.createCell(14).setCellValue("Point-of-View");
				row.createCell(15).setCellValue("Data Load Cube Name");
				int i=1;
				for (Map map : list) {
					row =sheet.createRow(i);
					i++;
					if(null==map.get("DATA_LOAD_CUBE_NAME")){
						row.createCell(0).setCellValue(map.get("ACCOUNT").toString());
						row.createCell(1).setCellValue(map.get("JAN").toString());
						row.createCell(2).setCellValue(map.get("FEB").toString());
						row.createCell(3).setCellValue(map.get("MAR").toString());
						row.createCell(4).setCellValue(map.get("APR").toString());
						row.createCell(5).setCellValue(map.get("MAY").toString());
						row.createCell(6).setCellValue(map.get("JUN").toString());
						row.createCell(7).setCellValue(map.get("JUL").toString());
						row.createCell(8).setCellValue(map.get("AUG").toString());
						row.createCell(9).setCellValue(map.get("SEP").toString());
						row.createCell(10).setCellValue(map.get("OCT").toString());
						row.createCell(11).setCellValue(map.get("NOV").toString());
						row.createCell(12).setCellValue(map.get("DEC").toString());
						row.createCell(13).setCellValue(map.get("YT").toString());
						row.createCell(14).setCellValue(map.get("POINT_OF_VIEW").toString());
						row.createCell(16).setCellValue("");
					}else{
						row.createCell(0).setCellValue(map.get("ACCOUNT").toString());
						row.createCell(1).setCellValue(map.get("JAN").toString());
						row.createCell(2).setCellValue(map.get("FEB").toString());
						row.createCell(3).setCellValue(map.get("MAR").toString());
						row.createCell(4).setCellValue(map.get("APR").toString());
						row.createCell(5).setCellValue(map.get("MAY").toString());
						row.createCell(6).setCellValue(map.get("JUN").toString());
						row.createCell(7).setCellValue(map.get("JUL").toString());
						row.createCell(8).setCellValue(map.get("AUG").toString());
						row.createCell(9).setCellValue(map.get("SEP").toString());
						row.createCell(10).setCellValue(map.get("OCT").toString());
						row.createCell(11).setCellValue(map.get("NOV").toString());
						row.createCell(12).setCellValue(map.get("DEC").toString());
						row.createCell(13).setCellValue(map.get("YT").toString());
						row.createCell(14).setCellValue(map.get("POINT_OF_VIEW").toString());
						row.createCell(16).setCellValue("DATA_LOAD_CUBE_NAME");
					}
				}
				File outFile = new File(filePath);
				OutputStream out = new FileOutputStream(outFile);
				workBook.write(out);
				workBook.close();
				out.flush();
				out.close();
				result.put("fileName",outFile.getName());
				System.gc();
			}else {
				result.put("flag", "fail");
				result.put("msg", getLanguage(locale,"没有查询到可下载的数据","No downloadable data was queried"));
			}
		} catch (Exception e) {
			logger.error("下载Excel失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}
		return result.getJson();
	}


	@RequestMapping(value = "create")
	@ResponseBody
	@Log(name = "專案 投資 折舊預算/預測")
	public synchronized String create(HttpServletRequest request,AjaxResult result,
										@Log(name = "SBU") String sbu,@Log(name = "年") String year,@Log(name="場景")String scenarios,
									    @Log(name = "類型") String type){
		try {
			Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			result.put("msg", getLanguage(locale, "提交成功", "Submit successfully"));
			if (sbu.endsWith(",")) {
				sbu=sbu.substring(0,sbu.length()-1);
			}
			String entity="";
			for (String s : sbu.split(",")) {
				entity+=s+"|";
			}
			entity=entity.substring(0,entity.length()-1);
			String message = budgetService.generatePlanning(entity,year,scenarios,type);
			if (StringUtils.isNotEmpty(message)) {
				throw new RuntimeException(getLanguage(locale, "计算Budget数据出错 : ","There was an error calculating the Budget data:")+message);
			}
				result.put("role","NO");
		} catch (Exception e) {
			logger.error("下载Excel失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}
		return result.getJson();
	}


}
