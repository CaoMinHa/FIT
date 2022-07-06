package foxconn.fit.controller.hfm;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.service.hfm.HFMPackageService;
import foxconn.fit.util.DateUtil;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.SecurityUtils;

@Controller
@RequestMapping("/hfm/package")
@SuppressWarnings("unchecked")
public class HFMPackageController extends BaseController{
	
	@Value("${username}")
	private String hfmUsername;
	@Value("${password}")
	private String hfmPassword;
	@Value("${application}")
	private String application;
	@Value("${cluster}")
	private String cluster;
	
	@Autowired
	private HFMPackageService packageService;

	@RequestMapping(value = "index")
	public String index(HttpServletRequest request, Model model) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		String language=getLanguage(locale,"CN","EN");
		List<String> optionList = packageService.listBySql("select t.lov_code||','||t.tab_name||'|'||t.lov_desc from CUX_PKG_LOV_VALUES t where t.lov_type='HFM_CATEGORY' and t.enabled_flag='Y' and t.language='"+language+"' ORDER BY to_number(COL_SEQ)");
		model.addAttribute("optionList", optionList);
		return "/hfm/package/index";
	}
	
	@RequestMapping(value = "queryData")
	@ResponseBody
	public String queryData(HttpServletRequest request,HttpServletResponse response,AjaxResult result,String dataType){
		try {
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			Assert.hasText(dataType, getLanguage(locale,"数据类型不能为空","Data type can not be null"));
			String type=dataType.split(",")[0];
			String language=getLanguage(locale,"CN","EN");
			List<List<String>> queryList = packageService.listBySql("SELECT COL_NAME,COL_DESC FROM CUX_PKG_DATA_COLS WHERE CATEGORY = '"+type+"' AND LANGUAGE = '"+language+"' AND IS_QUERY = 'Y' AND ENABLED_FLAG = 'Y' ORDER BY to_number(COL_SEQ)");
			result.put("queryList", queryList);
		} catch (Exception e) {
			logger.error("查询信息失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}
		
		return result.getJson();
	}
	
	@RequestMapping(value = "dataImport")
	@ResponseBody
	@Log(name = "HFMPackage->数据导入")
	public String refresh(HttpServletRequest request,HttpServletResponse response,AjaxResult result,
			@Log(name = "公司编码") String entity,@Log(name = "年月") String date){
		Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		result.put("msg", getLanguage(locale,"数据导入成功","Data Import Success"));
		try {
			Assert.hasText(entity, getLanguage(locale,"公司编码不能为空","Entity can not be null"));
			Date dt = DateUtil.parseByYyyy_MM(date);
			Assert.notNull(dt, getLanguage(locale,"年月格式错误","Error Date Formats"));
			
			String[] split = date.split("-");
			String year = split[0];
			String period = split[1];
			if (period.length()<2) {
				period="0"+period;
			}
			String message = packageService.dataImport(entity,year,period,locale);
			if (StringUtils.isNotEmpty(message)) {
				result.put("flag", "fail");
				result.put("msg", getLanguage(locale,"数据导入失败 : ","Data import fail: ") +message);
			}
		} catch (Exception e) {
			logger.error("数据导入失败", e);
			result.put("flag", "fail");
			result.put("msg", getLanguage(locale,"数据导入失败 : ","Data import fail: ") +ExceptionUtil.getRootCauseMessage(e));
		}
		
		return result.getJson();
	}
	
	@RequestMapping(value = "dataSync")
	@ResponseBody
	@Log(name = "HFMPackage->数据同步")
	public String dataSync(HttpServletRequest request,HttpServletResponse response,AjaxResult result,
			@Log(name = "公司编码") String entity,@Log(name = "年月") String date){
		Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		result.put("msg", getLanguage(locale,"数据同步成功","Data Import Success"));
		try {
			Assert.hasText(entity, getLanguage(locale,"公司编码不能为空","Entity can not be null"));
			Date dt = DateUtil.parseByYyyy_MM(date);
			Assert.notNull(dt, getLanguage(locale,"年月格式错误","Error Date Formats"));
			
			String[] split = date.split("-");
			String year = split[0];
			String period = split[1];
			if (period.length()<2) {
				period="0"+period;
			}
			String message = packageService.dataSync(entity,year,period,locale);
			if (StringUtils.isNotEmpty(message)) {
				result.put("flag", "fail");
				result.put("msg", getLanguage(locale,"数据同步失败 : ","Data sync fail: ") +message);
				return result.getJson();
			}
			
			Map<String, String> dbInfo = packageService.getDBInfo();
			String dburl=dbInfo.get("dburl");
			String dbusername=dbInfo.get("dbusername");
			String dbpassword=dbInfo.get("dbpassword");
			logger.info("开始同步文本:");
			String url = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/bat/setText";
//			url="http://10.98.5.23:8080/bat/setText";
			String text = setText(url,dburl,dbusername,dbpassword,hfmUsername,hfmPassword,application,cluster);
			logger.info("同步文本结束,返回值:"+text);
			
			if (!"true".equals(text)) {
				result.put("flag", "fail");
				result.put("msg", getLanguage(locale,"同步文本失败","CellText sync fail: "));
				return result.getJson();
			}
		} catch (Exception e) {
			logger.error("数据同步失败", e);
			result.put("flag", "fail");
			result.put("msg", getLanguage(locale,"数据同步失败 : ","Data sync fail: ") +ExceptionUtil.getRootCauseMessage(e));
		}
		
		return result.getJson();
	}
	
	private String setText(String url,String dburl,String dbusername,String dbpassword,
			String hfmUsername,String hfmPassword,String application,String cluster) throws Exception{
		String result="false";
		CloseableHttpClient httpclient = HttpClients.createDefault();
		List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
		list.add(new BasicNameValuePair("dburl", dburl));
		list.add(new BasicNameValuePair("dbusername", dbusername));
		list.add(new BasicNameValuePair("dbpassword", dbpassword));
		list.add(new BasicNameValuePair("hfmUsername", hfmUsername));
		list.add(new BasicNameValuePair("hfmPassword", hfmPassword));
		list.add(new BasicNameValuePair("application", application));
		list.add(new BasicNameValuePair("cluster", cluster));
		String params = EntityUtils.toString(new UrlEncodedFormEntity(list,Consts.UTF_8));

		HttpGet httpget = new HttpGet(url+"?"+params);
		CloseableHttpResponse response = httpclient.execute(httpget);
		try {
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity,HTTP.UTF_8);
		} finally {
			response.close();
		}
		return result;
	}
	
	@RequestMapping(value="/list")
	public String list(Model model,HttpServletRequest request,PageRequest pageRequest,String dataType,String queryCondition) {
		try {
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			Assert.hasText(dataType, getLanguage(locale,"数据类型不能为空","Data type can not be null"));
			String type=dataType.split(",")[0];
			String tableName=dataType.split(",")[1];
			String language=getLanguage(locale,"CN","EN");
			List<Object[]> titleList = packageService.listBySql("SELECT COL_NAME,COL_DESC FROM CUX_PKG_DATA_COLS WHERE CATEGORY = '"+type+"' AND LANGUAGE = '"+language+"' AND IS_DISPLAY = 'Y' AND ENABLED_FLAG = 'Y' ORDER BY to_number(COL_SEQ)");
			model.addAttribute("titleList", titleList);
			
			String sql="select ";
			for (Object[] titleObjects : titleList) {
				Object column = titleObjects[0];
				sql+=column+",";
			}
			sql=sql.substring(0, sql.length()-1);
			sql+=" from "+tableName+" where 1=1 ";
			String entity=SecurityUtils.getEBS();
			if(StringUtils.isNotEmpty(entity)) {
				String entityCode="";
				String[] entitys = entity.split(",");
				for (String code : entitys) {
					if (code.startsWith("F_")) {
						entityCode+="'"+code.substring(2)+"',";
					}
				}
				entityCode=entityCode.substring(0, entityCode.length()-1);
				sql+=" and ENTITY_CODE in ("+entityCode+")";
			}
			if (StringUtils.isNotEmpty(queryCondition)) {
				String[] params = queryCondition.split("&");
				for (String param : params) {
					String columnName = param.substring(0,param.indexOf("="));
					String columnValue = param.substring(param.indexOf("=")+1).trim();
					if (StringUtils.isNotEmpty(columnValue)) {
						columnValue=URLDecoder.decode(columnValue, "UTF-8");
						sql+=" and "+columnName+" like '%"+columnValue+"%'";
					}
				}
				sql+=" order by ID";
			}
			Page<Object[]> page = packageService.findPageBySql(pageRequest, sql);
			model.addAttribute("page", page);
		} catch (Exception e) {
			logger.error("查询列表失败:", e);
		}
		return "/hfm/package/list";
	}
	
	@RequestMapping(value = "download")
	@ResponseBody
	public String download(HttpServletRequest request,HttpServletResponse response,AjaxResult result,@Log(name = "数据类型") String dataType,@Log(name = "查询条件") String queryCondition){
		try {			
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			String type=dataType.split(",")[0];
			String tableName=dataType.split(",")[1];
			String language=getLanguage(locale,"CN","EN");
			List<Object[]> titleList = packageService.listBySql("SELECT COL_NAME,COL_DESC FROM CUX_PKG_DATA_COLS WHERE CATEGORY = '"+type+"' AND LANGUAGE = '"+language+"' AND IS_DISPLAY = 'Y' AND ENABLED_FLAG = 'Y' ORDER BY to_number(COL_SEQ)");
			String sql="select ";
			List<String> columnDescList=new ArrayList<String>();
			for (Object[] titleObjects : titleList) {
				Object column = titleObjects[0];
				Object columnDesc = titleObjects[1];
				sql+=column+",";
				columnDescList.add(columnDesc==null?"":columnDesc.toString());
			}
			sql=sql.substring(0, sql.length()-1);
			sql+=" from "+tableName+" where 1=1 ";
			String entity=SecurityUtils.getEBS();
			if(StringUtils.isNotEmpty(entity)) {
				String entityCode="";
				String[] entitys = entity.split(",");
				for (String code : entitys) {
					if (code.startsWith("F_")) {
						entityCode+="'"+code.substring(2)+"',";
					}
				}
				entityCode=entityCode.substring(0, entityCode.length()-1);
				sql+=" and ENTITY_CODE in ("+entityCode+")";
			}
			if (StringUtils.isNotEmpty(queryCondition)) {
				String[] params = queryCondition.split("&");
				for (String param : params) {
					String columnName = param.substring(0,param.indexOf("="));
					String columnValue = param.substring(param.indexOf("=")+1).trim();
					if (StringUtils.isNotEmpty(columnValue)) {
						columnValue=URLDecoder.decode(columnValue, "UTF-8");
						sql+=" and "+columnName+" like '%"+columnValue+"%'";
					}
				}
				sql+=" order by ID";
			}
			
			List<Object[]> dataList = packageService.listBySql(sql);
			if (dataList.isEmpty()) {
				result.put("flag", "fail");
				result.put("msg", getLanguage(locale,"没有查询到可下载的数据","No data found"));
				return result.getJson();
			}
			
			String realPath = request.getRealPath("");
			File file=new File(request.getRealPath("")+File.separator+"static"+File.separator+"template"+File.separator+"hfm"+File.separator+"HFM package.xlsx");
			XSSFWorkbook workBook = new XSSFWorkbook(new FileInputStream(file));
			XSSFCellStyle titleStyle = workBook.createCellStyle();
			titleStyle.setLocked(true);
			titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			titleStyle.setFillForegroundColor(IndexedColors.BLACK.index);
			XSSFFont font = workBook.createFont();
			font.setColor(IndexedColors.WHITE.index);
			font.setBold(true);
			titleStyle.setFont(font);
			
			XSSFCellStyle lockStyle = workBook.createCellStyle();
			lockStyle.setLocked(true);
			lockStyle.setFillForegroundColor(new XSSFColor(new Color(217, 217, 217)));
			lockStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			
			SXSSFWorkbook sxssfWorkbook=new SXSSFWorkbook(workBook);
			String sheetName="HFM Package";
			sxssfWorkbook.setSheetName(0, sheetName);
			Sheet sheet = sxssfWorkbook.getSheetAt(0);
//			sheet.setColumnHidden(0, true);
//			sheet.protectSheet(new SimpleDateFormat("ddHHmmss").format(new Date()));
			
			Row columnDescRow = sheet.createRow(0);
			for (int i = 0; i < columnDescList.size(); i++) {
				String columnDesc = columnDescList.get(i);
				Cell cell = columnDescRow.createCell(i);
				cell.setCellValue(columnDesc);
				cell.setCellStyle(titleStyle);
			}
			
			for (int i = 0; i < dataList.size(); i++) {
				Object[] objects=dataList.get(i);
				Row row = sheet.createRow(i+1);
				for (int j = 0; j < objects.length; j++) {
					Cell cell = row.createCell(j);
					Object obj=objects[j];
					String value=(obj==null?"":obj.toString());
					cell.setCellStyle(lockStyle);
					cell.setCellValue(value);
				}
			}
			
			File outFile=new File(realPath+File.separator+"static"+File.separator+"download"+File.separator+System.currentTimeMillis()+".xlsx");
			OutputStream out = new FileOutputStream(outFile);
			sxssfWorkbook.write(out);
			sxssfWorkbook.close();
			out.flush();
			out.close();
			
			result.put("fileName", outFile.getName());
			result.put("templateName", sheetName+".xlsx");
			System.gc();
		} catch (Exception e) {
			logger.error("下载信息失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}
		
		return result.getJson();
	}
	
}
