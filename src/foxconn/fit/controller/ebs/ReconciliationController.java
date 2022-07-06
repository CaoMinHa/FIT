package foxconn.fit.controller.ebs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.dao.base.PropertyFilter;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.ebs.Reconciliation;
import foxconn.fit.service.ebs.ReconciliationService;
import foxconn.fit.util.DateUtil;
import foxconn.fit.util.ExcelUtil;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.SecurityUtils;

@Controller
@RequestMapping("/hfm/reconciliation")
@SuppressWarnings("unchecked")
public class ReconciliationController extends BaseController{

	private static String EXCEL_NAME="科目餘額表";
	private static int COLUMN_NUM=19;
	
	@Autowired
	private ReconciliationService reconciliationService;

	@RequestMapping(value = "index")
	public String index(HttpServletRequest request, Model model) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		model.addAttribute("locale", locale.toString());
		List<String> targetList=new ArrayList<String>();
		String entity1 = SecurityUtils.getEBS();
		if (StringUtils.isNotEmpty(entity1)) {
			for (String string : entity1.split(",")) {
				targetList.add(string.substring(2));
			}
		}
		List<String> entityList = new ArrayList<String>();
		List<String> list = reconciliationService.listBySql("select distinct ENTITY_CODE from CUX_BAL order by ENTITY_CODE");
		if (list.size()>0) {
			for (String code : list) {
				if (targetList.contains(code)) {
					entityList.add(code);
				}
			}
		}
		model.addAttribute("entityList", entityList);
		model.addAttribute("yearList", reconciliationService.listBySql("SELECT flv.lookup_code FROM CUX_LOOKUP_VALUES flv WHERE flv.lookup_type = 'YEAR' AND flv.enabled = 'Y' ORDER BY 1"));
		model.addAttribute("monthList", reconciliationService.listBySql("SELECT flv.lookup_code FROM CUX_LOOKUP_VALUES flv WHERE flv.lookup_type = 'PERIOD' AND flv.enabled = 'Y' ORDER BY 1"));
		String entitys="";
		if (!entityList.isEmpty()) {
			for (String string : entityList) {
				entitys=entitys+"'"+string+"'"+",";
			}
			entitys=entitys.substring(0, entitys.length()-1);
			model.addAttribute("ACCOUNTCODEList", reconciliationService.listBySql("select distinct ACCOUNT_CODE from CUX_BAL where ENTITY_CODE in ("+entitys+")"));
			model.addAttribute("ACCOUNTDESCList", reconciliationService.listBySql("select distinct ACCOUNT_NAME from CUX_BAL where ENTITY_CODE in ("+entitys+")"));
			model.addAttribute("CUSTNAMEList", reconciliationService.listBySql("select distinct CUST_NAME from CUX_BAL where ENTITY_CODE in ("+entitys+")"));
			model.addAttribute("TCList", reconciliationService.listBySql("select distinct TC from CUX_BAL where TC is not null and ENTITY_CODE in ("+entitys+")"));
		}
		return "/ebs/reconciliation/index";
	}
	
	/**
	 * 
	 * @param entity 公司编码
	 * @param date	年月
	 * @param ACCOUNTCODE	科目编码
	 * @param ACCOUNTDESC	科目名称
	 * @param ISICP	是否关联方
	 * @param CUSTNAME	客商名称
	 * @param TC	交易币种
	 * @return
	 */
	@RequestMapping(value="/list")
	public String list(Model model,HttpServletRequest request,PageRequest pageRequest,String entity,String date,String ACCOUNTCODE,String ACCOUNTDESC,String ISICP,String CUSTNAME,String TC) {
		try {
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			model.addAttribute("locale", locale.toString());
			pageRequest.setPageSize(20);
			List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
			
			if (StringUtils.isNotEmpty(date)) {
				Date dt = DateUtil.parseByYyyy_MM(date);
				Assert.notNull(dt, "年月格式错误");
				String[] split = date.split("-");
				String year = split[0];
				String period = split[1];
				if (period.length()<2) {
					period="0"+period;
				}
				filters.add(new PropertyFilter("EQS_YEAR",year));
				filters.add(new PropertyFilter("EQS_PERIOD",period));
			}
			
			List<String> targetList=new ArrayList<String>();
			String entity1 = SecurityUtils.getEBS();
			if (StringUtils.isNotEmpty(entity1)) {
				for (String string : entity1.split(",")) {
					targetList.add(string.substring(2));
				}
			}
			String queryEntity="";
			if (StringUtils.isEmpty(entity)) {
				for (String string : targetList) {
					queryEntity+=string+",";
				}
			}else{
				if (entity.endsWith(",")) {
					entity=entity.substring(0, entity.length()-1);
				}
				
				String[] entitys = entity.split(",");
				List<String> entityList=new ArrayList<String>();
				
				for (String string : entitys) {
					if (targetList.contains(string)) {
						entityList.add(string);
					}
				}
				
				for (String string : entityList) {
					queryEntity+=string+",";
				}
			}
			queryEntity=queryEntity.substring(0,queryEntity.length()-1);
			if (queryEntity.indexOf(",") > 0) {
				filters.add(new PropertyFilter("OREQS_ENTITY_CODE",queryEntity));
			} else {
				filters.add(new PropertyFilter("EQS_ENTITY_CODE",queryEntity));
			}
			
			if (StringUtils.isNotEmpty(ACCOUNTCODE)) {
				filters.add(new PropertyFilter("LLIKES_ACCOUNT_CODE",ACCOUNTCODE));
			}
			if (StringUtils.isNotEmpty(ACCOUNTDESC)) {
				filters.add(new PropertyFilter("LIKES_ACCOUNT_NAME",ACCOUNTDESC));
			}
			if (StringUtils.isNotEmpty(ISICP)) {
				filters.add(new PropertyFilter("EQS_ISICP",ISICP));
			}
			if (StringUtils.isNotEmpty(CUSTNAME)) {
				filters.add(new PropertyFilter("LIKES_CUST_NAME",CUSTNAME));
			}
			if (StringUtils.isNotEmpty(TC)) {
				filters.add(new PropertyFilter("EQS_TC",TC));
			}
			
			Page<Reconciliation> page = reconciliationService.findPageByHQL(pageRequest, filters);
			model.addAttribute("page", page);
		} catch (Exception e) {
			logger.error("查询科目余额表列表失败:", e);
		}
		return "/ebs/reconciliation/list";
	}
	
	@RequestMapping(value = "dataImport")
	@ResponseBody
	@Log(name = "科目余额表-->数据导入")
	public String dataImport(HttpServletRequest request,AjaxResult result,@Log(name = "年月") String date,@Log(name = "公司编码") String entitys) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		result.put("msg", getLanguage(locale,"数据导入成功","Data Import Success"));
		try {
			if (entitys.endsWith(",")) {
				entitys=entitys.substring(0, entitys.length()-1);
			}
			
			Date dt = DateUtil.parseByYyyy_MM(date);
			Assert.notNull(dt, getLanguage(locale,"年月格式错误","Error Date Formats"));
			
			String[] split = date.split("-");
			String year = split[0];
			String period = split[1];
			if (period.length()<2) {
				period="0"+period;
			}
			String message = reconciliationService.dataImport(entitys,year,period,locale);
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
	
	@RequestMapping(value = "upload")
	@ResponseBody
	@Log(name = "科目余额表-->上传")
	public String upload(HttpServletRequest request,AjaxResult result,@Log(name = "年月") String date,@Log(name = "公司编码") String[] entitys) {
		result.put("msg", "上传成功(Upload Success)");
		try {
			Assert.isTrue(entitys!=null && entitys.length>0, "公司编码不能为空");
			
			List<String> tarList=new ArrayList<String>();
			String entity1 = SecurityUtils.getEBS();
			if (StringUtils.isNotEmpty(entity1)) {
				for (String string : entity1.split(",")) {
					tarList.add(string.substring(2));
				}
			}
			Map<String,String> codeList=new HashMap<String,String>();
			for (String string : entitys) {
				if (tarList.contains(string)) {
					codeList.put(string.toLowerCase(),string);
				}
			}
			Date dt = DateUtil.parseByYyyy_MM(date);
			Assert.isTrue(!codeList.values().isEmpty(), "错误的公司编码");
			Assert.notNull(dt, "年月格式错误(Error Date Formats)");
			
			String[] split = date.split("-");
			String year = split[0];
			String period = split[1];
			if (period.length()<2) {
				period="0"+period;
			}
			
			MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
			Map<String, MultipartFile> mutipartFiles = multipartHttpServletRequest.getFileMap();
			if (mutipartFiles != null && mutipartFiles.size() > 0) {
				MultipartFile file = (MultipartFile) mutipartFiles.values().toArray()[0];

				String suffix = "";
				if (file.getOriginalFilename().lastIndexOf(".") != -1) {
					suffix = file.getOriginalFilename().substring(
							file.getOriginalFilename().lastIndexOf(".") + 1,
							file.getOriginalFilename().length());
					suffix = suffix.toLowerCase();
				}
				if (!"xls".equals(suffix) && !"xlsx".equals(suffix)) {
					result.put("flag", "fail");
					result.put("msg", "请您上传正确格式的Excel文件");
					return result.getJson();
				}

				Workbook wb=null;
				if ("xls".equals(suffix)) {
					//Excel2003
					wb=new HSSFWorkbook(file.getInputStream());
				}else {
					//Excel2007
					wb=new XSSFWorkbook(file.getInputStream());
				}
				wb.close();
				
				Sheet sheet = wb.getSheetAt(0);
				Row firstRow = sheet.getRow(0);
				Assert.notNull(firstRow, "第一行为标题行，不允许为空");
				int column = firstRow.getPhysicalNumberOfCells();
				
				if(column<COLUMN_NUM){
					result.put("flag", "fail");
					result.put("msg", "Excel列数不能小于"+COLUMN_NUM);
					return result.getJson();
				}

				int rowNum = sheet.getPhysicalNumberOfRows();
				if (rowNum<2) {
					result.put("flag", "fail");
					result.put("msg", "检测到Excel没有行数据");
					return result.getJson();
				}
				
				List<Reconciliation> list=new ArrayList<Reconciliation>();
				Map<String, String> codeMap=new HashMap<String, String>();
				
				for (int i = 1; i < rowNum; i++) {
					Row row = sheet.getRow(i);
					
					if (row==null) {
						continue;
					}
					
					boolean isBlankRow=true;
					for (int j = 0; j < COLUMN_NUM; j++) {
						if (StringUtils.isNotEmpty(ExcelUtil.getCellStringValue(row.getCell(j),i))) {
							isBlankRow=false;
						}
					}
					
					if (isBlankRow) {
						continue;
					}
					
					int n=0;
					String tempYear = ExcelUtil.getCellStringValue(row.getCell(n++),i);
					String tempPeriod = ExcelUtil.getCellStringValue(row.getCell(n++),i);
					Date tempDate = DateUtil.parseByYyyy_MM(tempYear+"-"+tempPeriod);
					String srcEntity = ExcelUtil.getCellStringValue(row.getCell(n++),i);
					String lowerEntity = srcEntity.toLowerCase();
					
					Assert.notNull(tempDate, "第"+(i+1)+"行数据年或期间格式错误");
					Assert.isTrue(dt.compareTo(tempDate)==0, "第"+(i+1)+"行数据年月与页面选择的不一致");
					Assert.isTrue(codeList.containsKey(lowerEntity), "第"+(i+1)+"行数据公司编码【"+srcEntity+"】不在用户配置的公司编码中");
					
					String entity = codeList.get(lowerEntity);
					codeMap.put(lowerEntity,entity);
					Reconciliation detail=new Reconciliation();
					detail.setYEAR(year);
					detail.setPERIOD(period);
					detail.setENTITY_CODE(entity);
					detail.setENTITY_NAME(ExcelUtil.getCellStringValue(row.getCell(n++),i));
					detail.setACCOUNT_CODE(ExcelUtil.getCellStringValue(row.getCell(n++),i));
					detail.setACCOUNT_NAME(ExcelUtil.getCellStringValue(row.getCell(n++),i));
					detail.setCUST_CODE(ExcelUtil.getCellStringValue(row.getCell(n++),i));
					detail.setCUST_NAME(ExcelUtil.getCellStringValue(row.getCell(n++),i));
					detail.setISICP(ExcelUtil.getCellStringValue(row.getCell(n++),i));
					detail.setSOURSYS(ExcelUtil.getCellStringValue(row.getCell(n++),i));
					detail.setLC(ExcelUtil.getCellStringValue(row.getCell(n++),i));
					detail.setLC_BBAL(ExcelUtil.getCellDoubleValue(row.getCell(n++),i));
					detail.setLC_DR(ExcelUtil.getCellDoubleValue(row.getCell(n++),i));
					detail.setLC_CR(ExcelUtil.getCellDoubleValue(row.getCell(n++),i));
					detail.setLC_EBAL(ExcelUtil.getCellDoubleValue(row.getCell(n++),i));
					detail.setTC(ExcelUtil.getCellStringValue(row.getCell(n++),i));
					detail.setTC_BBAL(ExcelUtil.getCellDoubleValue(row.getCell(n++),i));
					detail.setTC_DR(ExcelUtil.getCellDoubleValue(row.getCell(n++),i));
					detail.setTC_CR(ExcelUtil.getCellDoubleValue(row.getCell(n++),i));
					detail.setTC_EBAL(ExcelUtil.getCellDoubleValue(row.getCell(n++),i));
					detail.setCREATION_DATE(new Date());
					detail.setCREATED_BY(SecurityUtils.getLoginUsername());

					list.add(detail);
				}
				
				if (!list.isEmpty()) {
					Collection<String> values = codeMap.values();
					String codeCondition="where YEAR='"+year+"' and PERIOD='"+period+"' and ENTITY_CODE in (";
					for (String string : values) {
						codeCondition+="'"+string+"',";
					}
					codeCondition=codeCondition.substring(0, codeCondition.length()-1)+")";
					reconciliationService.saveBatch(list, codeCondition);
				}else{
					result.put("flag", "fail");
					result.put("msg", "无有效数据行");
				}
			} else {
				result.put("flag", "fail");
				result.put("msg", "对不起,未接收到上传的文件");
			}
		} catch (Exception e) {
			logger.error("保存文件失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}

		return result.getJson();
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping(value = "download")
	@ResponseBody
	@Log(name = "科目余额表-->下载")
	public synchronized String download(HttpServletRequest request,HttpServletResponse response,PageRequest pageRequest,AjaxResult result,
			@Log(name = "年月") String date,@Log(name = "公司编码") String entity,@Log(name = "科目编码") String ACCOUNTCODE,
			@Log(name = "科目名称") String ACCOUNTDESC,@Log(name = "是否关联方") String ISICP,@Log(name = "客商名称") String CUSTNAME,@Log(name = "交易币种") String TC){
		try {
			List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
			pageRequest.setOrderBy("id");
			pageRequest.setOrderDir(PageRequest.Sort.ASC);
			
			Date dt = DateUtil.parseByYyyy_MM(date);
			Assert.notNull(dt, "年月格式错误");
			String[] split = date.split("-");
			String year = split[0];
			String period = split[1];
			if (period.length()<2) {
				period="0"+period;
			}
			
			filters.add(new PropertyFilter("EQS_YEAR",year));
			filters.add(new PropertyFilter("EQS_PERIOD",period));
			
			List<String> targetList=new ArrayList<String>();
			String entity1 = SecurityUtils.getEBS();
			if (StringUtils.isNotEmpty(entity1)) {
				for (String string : entity1.split(",")) {
					targetList.add(string.substring(2));
				}
			}
			String queryEntity="";
			if (StringUtils.isEmpty(entity)) {
				for (String string : targetList) {
					queryEntity+=string+",";
				}
			}else{
				if (entity.endsWith(",")) {
					entity=entity.substring(0, entity.length()-1);
				}
				
				String[] entitys = entity.split(",");
				List<String> entityList=new ArrayList<String>();
				
				for (String string : entitys) {
					if (targetList.contains(string)) {
						entityList.add(string);
					}
				}
				
				for (String string : entityList) {
					queryEntity+=string+",";
				}
			}
			queryEntity=queryEntity.substring(0,queryEntity.length()-1);
			if (queryEntity.indexOf(",") > 0) {
				filters.add(new PropertyFilter("OREQS_ENTITY_CODE",queryEntity));
			} else {
				filters.add(new PropertyFilter("EQS_ENTITY_CODE",queryEntity));
			}
			
			if (StringUtils.isNotEmpty(ACCOUNTCODE)) {
				filters.add(new PropertyFilter("LLIKES_ACCOUNT_CODE",ACCOUNTCODE));
			}
			if (StringUtils.isNotEmpty(ACCOUNTDESC)) {
				filters.add(new PropertyFilter("LIKES_ACCOUNT_NAME",ACCOUNTDESC));
			}
			if (StringUtils.isNotEmpty(ISICP)) {
				filters.add(new PropertyFilter("EQS_ISICP",ISICP));
			}
			if (StringUtils.isNotEmpty(CUSTNAME)) {
				filters.add(new PropertyFilter("LIKES_CUST_NAME",CUSTNAME));
			}
			if (StringUtils.isNotEmpty(TC)) {
				filters.add(new PropertyFilter("EQS_TC",TC));
			}
			
			pageRequest.setPageSize(ExcelUtil.PAGE_SIZE);
			pageRequest.setPageNo(1);
			List<Reconciliation> list = reconciliationService.findPageByHQL(pageRequest, filters).getResult();
			
			if (CollectionUtils.isNotEmpty(list)) {
				String realPath=request.getRealPath("");
				File file=new File(request.getRealPath("")+File.separator+"static"+File.separator+"template"+File.separator+"ebs"+File.separator+EXCEL_NAME+"_download.xlsx");
				XSSFWorkbook workBook = new XSSFWorkbook(new FileInputStream(file));
				XSSFCellStyle style = workBook.createCellStyle();
				style.setAlignment(HorizontalAlignment.CENTER);
				
				SXSSFWorkbook sxssfWorkbook=new SXSSFWorkbook(workBook);
				Sheet sheet = sxssfWorkbook.getSheetAt(0);
				
				int rowIndex=1;
				for (Reconciliation reconciliation : list) {
					Row row = sheet.createRow(rowIndex++);
					ExcelUtil.createCellParseValueType(style, reconciliation, row);
				}
				while (list !=null && list.size()>=ExcelUtil.PAGE_SIZE) {
					pageRequest.setPageNo(pageRequest.getPageNo()+1);
					list = reconciliationService.findPageByHQL(pageRequest, filters).getResult();
					if (CollectionUtils.isNotEmpty(list)) {
						for (Reconciliation reconciliation : list) {
							Row row = sheet.createRow(rowIndex++);
							ExcelUtil.createCellParseValueType(style, reconciliation, row);
						}
					}
				}
				
				File outFile=new File(realPath+File.separator+"static"+File.separator+"download"+File.separator+System.currentTimeMillis()+".xlsx");
				OutputStream out = new FileOutputStream(outFile);
				sxssfWorkbook.write(out);
				sxssfWorkbook.close();
				out.flush();
				out.close();
				
				result.put("fileName", outFile.getName());
				System.gc();
			}else {
				result.put("flag", "fail");
				result.put("msg", "没有查询到可下载的数据");
			}
		} catch (Exception e) {
			logger.error("下载Excel失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}
		
		return result.getJson();
	
	}
	
}
