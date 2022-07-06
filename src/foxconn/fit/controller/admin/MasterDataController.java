package foxconn.fit.controller.admin;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.service.base.MasterDataService;
import foxconn.fit.util.ExcelUtil;
import foxconn.fit.util.ExceptionUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

@Controller
@RequestMapping("/admin/masterData")
@SuppressWarnings("unchecked")
public class MasterDataController extends BaseController{
//	@Value("${queryCount}")
//	private int queryCount;

	@Autowired
	private MasterDataService masterDataService;

	@RequestMapping(value = "index")
	public String index(HttpServletRequest request, Model model) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		String language=getLanguage(locale,"CN","EN");
		List<String> supplierList = masterDataService.listBySql("select t.lov_code||','||t.tab_name||'|'||t.lov_desc from CUX_MD_LOV_VALUES t where t.lov_type='CATEGORY' and t.enabled_flag='Y' and t.language='"+language+"' ORDER BY to_number(COL_SEQ)");
		model.addAttribute("supplierList", supplierList);
		return "/admin/masterData/index";
	}
	
	@RequestMapping(value = "queryMasterData")
	@ResponseBody
	public String queryMasterData(HttpServletRequest request,HttpServletResponse response,AjaxResult result,String masterData){
		try {
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			Assert.hasText(masterData, getLanguage(locale,"主数据不能为空","Master data can not be null"));
			String masterType=masterData.split(",")[0];
			String language=getLanguage(locale,"CN","EN");
			List<List<String>> queryList = masterDataService.listBySql("SELECT COL_NAME,COL_DESC FROM CUX_MASTER_DATA_COLS WHERE CATEGORY = '"+masterType+"' AND LANGUAGE = '"+language+"' AND IS_QUERY = 'Y' AND ENABLED_FLAG = 'Y' ORDER BY to_number(COL_SEQ)");
			result.put("queryList", queryList);
		} catch (Exception e) {
			logger.error("查询主数据信息失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}
		
		return result.getJson();
	}
	
	@RequestMapping(value = "refresh")
	@ResponseBody
	public String refresh(HttpServletRequest request,HttpServletResponse response,AjaxResult result,@Log(name = "主数据") String masterData){
		try {
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			Assert.hasText(masterData, getLanguage(locale,"主数据不能为空","Master data can not be null"));
			String masterType=masterData.split(",")[0];
			result.put("msg", getLanguage(locale,"刷新成功","Refresh fail"));
			String message = masterDataService.refreshMasterData(masterType);
			if (!"S".equals(message)) {
				result.put("flag", "fail");
				result.put("msg", message);
				return result.getJson();
			}
		} catch (Exception e) {
			logger.error("刷新主数据信息失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}
		
		return result.getJson();
	}
	
	@RequestMapping(value = "update")
	@ResponseBody
	public String update(HttpServletRequest request,HttpServletResponse response,AjaxResult result,@Log(name = "主数据") String masterData,@Log(name = "更新条件") String updateData){
		try {			
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			Assert.hasText(masterData, getLanguage(locale,"主数据不能为空","Master data can not be null"));
			String tableName=masterData.split(",")[1];
			result.put("msg", getLanguage(locale,"更新成功","Update data success"));
			if (StringUtils.isNotEmpty(updateData)) {
				String updateSql="update "+tableName+" set ";
				String where="";
				String[] params = updateData.split("&");
				for (String param : params) {
					String columnName = param.substring(0,param.indexOf("="));
					String columnValue = param.substring(param.indexOf("=")+1).trim();
					if ("ID".equalsIgnoreCase(columnName)) {
						where=" where ID='"+columnValue+"'";
					}else{
						if (StringUtils.isNotEmpty(columnValue)) {
							updateSql+=columnName+"='"+columnValue+"',";
						}
					}
				}
				updateSql=updateSql.substring(0, updateSql.length()-1);
				updateSql+=where;
				
				masterDataService.updateMasterData(updateSql);
			}
		} catch (Exception e) {
			logger.error("更新主数据信息失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}
		
		return result.getJson();
	}
	
	@RequestMapping(value="/list")
	public String list(Model model,HttpServletRequest request,PageRequest pageRequest,String masterData,String queryCondition) {
		try {
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			Assert.hasText(masterData, getLanguage(locale,"主数据不能为空","Master data can not be null"));
			String masterType=masterData.split(",")[0];
			String tableName=masterData.split(",")[1];
			String language=getLanguage(locale,"CN","EN");
			List<Object[]> titleList = masterDataService.listBySql("SELECT COL_NAME,COL_DESC,READ_WRITE,LOV FROM CUX_MASTER_DATA_COLS WHERE CATEGORY = '"+masterType+"' AND LANGUAGE = '"+language+"' AND IS_DISPLAY = 'Y' AND ENABLED_FLAG = 'Y' ORDER BY to_number(COL_SEQ)");
			model.addAttribute("titleList", titleList);
			
			List<Object[]> optionList = masterDataService.listBySql("SELECT c.lov,v.lov_code,v.lov_desc FROM CUX_MASTER_DATA_COLS c,CUX_MD_LOV_VALUES v "+
																	"WHERE c.CATEGORY = '"+masterType+"' AND c.LANGUAGE = '"+language+"' AND c.IS_DISPLAY = 'Y' AND c.ENABLED_FLAG = 'Y' and c.LOV is not null and c.lov=v.lov_type and v.language='"+language+"' and v.enabled_flag='Y' order by c.lov,v.lov_code desc");
			Map<String,String> optionMap=new HashMap<String,String>();
			if (optionList!=null && optionList.size()>0) {
				for (Object[] objects : optionList) {
					String lov=(String) objects[0];
					String lovCode=(String) objects[1];
					String lovDesc=(String) objects[2];
					String value = optionMap.get(lov);
					if (StringUtils.isEmpty(value)) {
						value=lovCode+"-"+lovDesc;
					}else{
						value+=","+lovCode+"-"+lovDesc;
					}
					optionMap.put(lov, value);
				}
			}
			
			String sql="select id,";
			for (Object[] titleObjects : titleList) {
				Object column = titleObjects[0];
				Object read = titleObjects[2];
				Object lov = titleObjects[3];
				if (lov!=null && StringUtils.isNotEmpty(lov.toString())) {
					sql+="'"+column+"|"+read+"S|'||nvl("+column+",' ')||'|"+optionMap.get(lov)+"',";
				}else{
					sql+="'"+column+"|"+read+"|'||"+column+",";
				}
			}
			sql=sql.substring(0, sql.length()-1);
			sql+=" from "+tableName;
			if (StringUtils.isNotEmpty(queryCondition)) {
				sql+=" where 1=1 ";
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
			Page<Object[]> page = masterDataService.findPageBySql(pageRequest, sql);
			model.addAttribute("page", page);
		} catch (Exception e) {
			logger.error("查询列表失败:", e);
		}
		return "/admin/masterData/list";
	}
	
	@RequestMapping(value = "upload")
	@ResponseBody
	@Log(name = "主数据补录平台-->上传")
	public String upload(HttpServletRequest request,AjaxResult result,@Log(name = "主数据") String masterData) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		result.put("msg", getLanguage(locale,"上传成功","Upload success"));
		try {
			Assert.hasText(masterData, getLanguage(locale,"主数据不能为空","Master data can not be null"));
			String masterType=masterData.split(",")[0];
			String tableName=masterData.split(",")[1];
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
				int rowNum = sheet.getPhysicalNumberOfRows();
				if (rowNum<4) {
					result.put("flag", "fail");
					result.put("msg", getLanguage(locale,"未发现需要处理的数据","No valid data"));
					return result.getJson();
				}
				Row columnRow = sheet.getRow(0);
				Row readRow = sheet.getRow(1);
				Row lovRow = sheet.getRow(2);
				int columnNum = readRow.getPhysicalNumberOfCells();
				String language = columnRow.getCell(0).getStringCellValue();
				String tableNAME = readRow.getCell(0).getStringCellValue();
				Assert.isTrue(tableName.equals(tableNAME), getLanguage(locale,"主数据选择错误","The selected master data is error"));
				
				List<Integer> indexList=new ArrayList<Integer>();
				List<String> columnList=new ArrayList<String>();
				Map<Integer,String> readMap=new HashMap<Integer, String>();
				Map<Integer,String> lovMap=new HashMap<Integer, String>();
				for (int i = 1; i < columnNum; i++) {
					String read = readRow.getCell(i).getStringCellValue();
					if ("W".equals(read)) {
						String column = columnRow.getCell(i).getStringCellValue();
						columnList.add(column);
						String lov = lovRow.getCell(i).getStringCellValue();
						if (StringUtils.isNotEmpty(lov)) {
							readMap.put(Integer.valueOf(i), "WS");
							lovMap.put(Integer.valueOf(i), lov);
						}else{
							readMap.put(Integer.valueOf(i), "W");
						}
						indexList.add(Integer.valueOf(i));
					}
				}
				
				List<Object[]> optionList = masterDataService.listBySql("SELECT c.lov,v.lov_code,v.lov_desc FROM CUX_MASTER_DATA_COLS c,CUX_MD_LOV_VALUES v "+
						"WHERE c.CATEGORY = '"+masterType+"' AND c.LANGUAGE = '"+language+"' AND c.IS_DISPLAY = 'Y' AND c.ENABLED_FLAG = 'Y' and c.LOV is not null and c.lov=v.lov_type and v.language='"+language+"' and v.enabled_flag='Y' order by c.lov,v.lov_code desc");
				Map<String,String> optionMap=new HashMap<String,String>();
				if (optionList!=null && optionList.size()>0) {
					for (Object[] objects : optionList) {
						String lov=(String) objects[0];
						String lovCode=(String) objects[1];
						String lovDesc=(String) objects[2];
						optionMap.put(lov+"&"+lovDesc, lovCode);
					}
				}
				
				List<List<String>> dataList=new ArrayList<List<String>>();
				for (int i = 4; i < rowNum; i++) {
					Row row = sheet.getRow(i);
					
					if (row==null) {
						continue;
					}
					
					List<String> data=new ArrayList<String>();
					String idStr = ExcelUtil.getCellStringValue(row.getCell(0),i);
					String id=idStr.split(",")[0];
					String oldBase64=idStr.split(",")[1];
					data.add(id);
					
					StringBuffer key=new StringBuffer();
					for (Integer index : indexList) {
						String value = ExcelUtil.getCellStringValue(row.getCell(index),i);
						key.append(value);
						if ("WS".equals(readMap.get(index)) && StringUtils.isNotEmpty(value)) {
							value=optionMap.get(lovMap.get(index)+"&"+value);
							Assert.hasText(value, getLanguage(locale,"后台配置已更新，请重新下载","The background configuration has changed,please download again"));
						}
						value = value.replaceAll("'","''"); //added by chrisltan on 20200907 for ORA-00933: SQL 命令的結束有問題
						data.add(value);
					}
					String base64 = Base64.getEncoder().encodeToString(key.toString().getBytes());
					if (!base64.equals(oldBase64)) {
						dataList.add(data);
					}
				}
				
				if (!dataList.isEmpty()) {
					masterDataService.saveBatch(tableNAME,columnList,dataList);
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
	
	@RequestMapping(value = "download")
	@ResponseBody
	public String download(HttpServletRequest request,HttpServletResponse response,AjaxResult result,@Log(name = "主数据") String masterData,@Log(name = "查询条件") String queryCondition){
		try {			
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			Assert.hasText(masterData, getLanguage(locale,"主数据不能为空","Master data can not be null"));
			String masterType=masterData.split(",")[0];
			String tableName=masterData.split(",")[1];
			String language=getLanguage(locale,"CN","EN");
			List<Object[]> titleList = masterDataService.listBySql("SELECT COL_NAME,COL_DESC,READ_WRITE,LOV FROM CUX_MASTER_DATA_COLS WHERE CATEGORY = '"+masterType+"' AND LANGUAGE = '"+language+"' AND IS_DISPLAY = 'Y' AND ENABLED_FLAG = 'Y' ORDER BY to_number(COL_SEQ)");
			Map<Integer,String> readMap=new HashMap<Integer, String>();
			Map<Integer,String> lovMap=new HashMap<Integer, String>();
			List<String> columnList=new ArrayList<String>();
			List<String> columnDescList=new ArrayList<String>();
			List<String> readList=new ArrayList<String>();
			List<String> lovList=new ArrayList<String>();
			String sql="select id,";
			for (int i = 0; i < titleList.size(); i++) {
				Object[] titleObjects=titleList.get(i);
				Object column = titleObjects[0];
				Object columnDesc = titleObjects[1];
				Object read = titleObjects[2];
				Object lov = titleObjects[3];
				String columnStr=(column==null?"":column.toString());
				String columnDescStr=(columnDesc==null?"":columnDesc.toString());
				String readStr=(read==null?"":read.toString());
				String lovStr=(lov==null?"":lov.toString());
				
				readMap.put(i+1, readStr);
				lovMap.put(i+1, lovStr);
				
				columnList.add(columnStr);
				columnDescList.add(columnDescStr);
				readList.add(readStr);
				lovList.add(lovStr);
				
				sql+=column+",";
			}
			sql=sql.substring(0, sql.length()-1);
			sql+=" from "+tableName;
			if (StringUtils.isNotEmpty(queryCondition)) {
				sql+=" where 1=1 ";
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
			
			List<Object[]> optionList = masterDataService.listBySql("SELECT c.lov,v.lov_code,v.lov_desc FROM CUX_MASTER_DATA_COLS c,CUX_MD_LOV_VALUES v "+
					"WHERE c.CATEGORY = '"+masterType+"' AND c.LANGUAGE = '"+language+"' AND c.IS_DISPLAY = 'Y' AND c.ENABLED_FLAG = 'Y' and c.LOV is not null and c.lov=v.lov_type and v.language='"+language+"' and v.enabled_flag='Y' order by c.lov,v.lov_code desc");
			Map<String,String> optionMap=new HashMap<String,String>();
			Map<String,List<String>> selectMap=new HashMap<String, List<String>>();
			if (optionList!=null && optionList.size()>0) {
				for (Object[] objects : optionList) {
					String lov=(String) objects[0];
					String lovCode=(String) objects[1];
					String lovDesc=(String) objects[2];
					optionMap.put(lov+"&"+lovCode, lovDesc);
					
					List<String> lovDescList = selectMap.get(lov);
					if (lovDescList==null) {
						lovDescList=new ArrayList<String>();
					}
					lovDescList.add(lovDesc);
					selectMap.put(lov, lovDescList);
				}
			}
			
			List<BigDecimal> countList = (List<BigDecimal>)masterDataService.listBySql("select count(1) from ("+sql+")");
			int count = countList.get(0).intValue();
			
			List<Object[]> dataList = masterDataService.listBySql(sql);
			if (dataList.isEmpty()) {
				result.put("flag", "fail");
				result.put("msg", getLanguage(locale,"没有查询到可下载的数据","No data found"));
				return result.getJson();
			}
			
			String realPath = request.getRealPath("");
			File file=new File(request.getRealPath("")+File.separator+"static"+File.separator+"template"+File.separator+"admin"+File.separator+"主数据信息.xlsx");
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
			lockStyle.setFillForegroundColor(new XSSFColor(new Color(255, 247, 251)));
			lockStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			
			XSSFCellStyle unlockStyle = workBook.createCellStyle();
			unlockStyle.setLocked(false);
			unlockStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			unlockStyle.setFillForegroundColor(IndexedColors.WHITE.index);
			
			SXSSFWorkbook sxssfWorkbook=new SXSSFWorkbook(workBook);
			String sheetName=getLanguage(locale,"主数据信息","Master data info");
			sxssfWorkbook.setSheetName(0, sheetName);
			Sheet sheet = sxssfWorkbook.getSheetAt(0);
			sheet.setColumnHidden(0, true);
			sheet.protectSheet(new SimpleDateFormat("ddHHmmss").format(new Date()));
			
			Row columnRow = sheet.createRow(0);
			Cell columnCell = columnRow.createCell(0);
			columnCell.setCellValue(language);
			columnCell.setCellStyle(lockStyle);
			for (int i = 0; i < columnList.size(); i++) {
				String column = columnList.get(i);
				Cell cell = columnRow.createCell(i+1);
				cell.setCellValue(column);
				cell.setCellStyle(lockStyle);
			}
			columnRow.setZeroHeight(true);
			
			Row readRow = sheet.createRow(1);
			Cell readCell = readRow.createCell(0);
			readCell.setCellValue(tableName);
			readCell.setCellStyle(lockStyle);
			for (int i = 0; i < readList.size(); i++) {
				String read = readList.get(i);
				Cell cell = readRow.createCell(i+1);
				cell.setCellValue(read);
				cell.setCellStyle(lockStyle);
			}
			readRow.setZeroHeight(true);
			
			Row lovRow = sheet.createRow(2);
			Cell lovCell = lovRow.createCell(0);
			lovCell.setCellValue("");
			lovCell.setCellStyle(lockStyle);
			for (int i = 0; i < lovList.size(); i++) {
				String lov = lovList.get(i);
				Cell cell = lovRow.createCell(i+1);
				cell.setCellValue(lov);
				cell.setCellStyle(lockStyle);
			}
			lovRow.setZeroHeight(true);
			
			Row columnDescRow = sheet.createRow(3);
			for (int i = 0; i < columnDescList.size(); i++) {
				String columnDesc = columnDescList.get(i);
				Cell cell = columnDescRow.createCell(i+1);
				cell.setCellValue(columnDesc);
				cell.setCellStyle(titleStyle);
			}
			
			for (int j = 1; j < dataList.get(0).length; j++) {
				if ("W".equals(readMap.get(Integer.valueOf(j)))) {
					String lov = lovMap.get(Integer.valueOf(j));
					if (StringUtils.isNotEmpty(lov)) {
						List<String> lovDescList = selectMap.get(lov);
						if (lovDescList!=null && lovDescList.size()>0) {
							String [] subjects = new String[lovDescList.size()];
							lovDescList.toArray(subjects);
							DataValidationHelper helper = sheet.getDataValidationHelper();
							DataValidationConstraint constraint = helper.createExplicitListConstraint(subjects);
							CellRangeAddressList addressList = new CellRangeAddressList(4, dataList.size()+4, j, j);
							DataValidation dataValidation = helper.createValidation(constraint, addressList);
							sheet.addValidationData(dataValidation);
						}
					}
				}
			}
			
			for (int i = 0; i < dataList.size(); i++) {
				Object[] objects=dataList.get(i);
				String id = objects[0].toString();
				Row row = sheet.createRow(i+4);
				StringBuffer key=new StringBuffer();
				for (int j = 1; j < objects.length; j++) {
					Cell cell = row.createCell(j);
					Object obj=objects[j];
					String value=(obj==null?"":obj.toString());
					if ("W".equals(readMap.get(Integer.valueOf(j)))) {
						cell.setCellStyle(unlockStyle);
						String lov = lovMap.get(Integer.valueOf(j));
						if (StringUtils.isNotEmpty(lov)) {
							if (StringUtils.isNotEmpty(value)) {
								value=optionMap.get(lov+"&"+value);
							}
						}
						cell.setCellValue(value);
						key.append(value);
					}else{
						cell.setCellStyle(lockStyle);
						cell.setCellValue(value);
					}
				}
				String base64 = Base64.getEncoder().encodeToString(key.toString().getBytes());
				Cell idCell = row.createCell(0);
				idCell.setCellValue(id+","+base64);
				idCell.setCellStyle(lockStyle);
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
			logger.error("下载主数据信息失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}
		
		return result.getJson();
	}

	public static void main(String[] args) {
		String value = "CRI COUGLE'S RECYCLING, INC";
		System.out.println(value.replaceAll("'","''"));
		String  updateSql = "update CUX_ENTITIES set ADDRESS='$1',TERRITORY='$2',PAYMENT_TERMS='$3',ICP_TYPE='$4',IS_ICP='$5',CUST_GROUP='$6',GROUP_TYPE='$7',ENTITY_CODE='$8',ENTITY_NAME_CH='$9',ENTITY_NAME_EN='$10',ENABLED_FLAG='$11' where ID='98264C7CD7DF19EFE0531505620AD1AB'";
		updateSql = updateSql.replaceFirst("\\$1","");

		System.out.println(updateSql);
	}
}
