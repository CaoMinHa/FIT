package foxconn.fit.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.bi.PoColumns;
import foxconn.fit.entity.bi.PoKey;
import foxconn.fit.entity.bi.PoTable;
import foxconn.fit.service.base.UserDetailImpl;
import foxconn.fit.service.base.UserService;
import foxconn.fit.service.bi.PoTableService;
import foxconn.fit.util.DateUtil;
import foxconn.fit.util.ExcelUtil;
import foxconn.fit.util.SecurityUtils;
import net.sf.json.JSONArray;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetProtection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.PageRequest;

public abstract class BaseController {
	
	public Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	public SpringValidatorAdapter validator;

	@Autowired
	private PoTableService poTableService;
	
	protected void out(Object result, HttpServletResponse response) throws IOException {
		ServletOutputStream out = response.getOutputStream();

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.writeValue(out, result);
		out.flush();
	}
	
	protected String getByLocale(Locale locale,String value){
		if (StringUtils.isNotEmpty(value) && value.indexOf("_")>0) {
			if (locale!=null && "en_US".equals(locale.toString())) {
				return value.substring(0,value.lastIndexOf("_"));
			}else{
				return value.substring(value.lastIndexOf("_")+1, value.length());
			}
		}
		return value;
	}
	
	protected String getLanguage(Locale locale,String zh,String en){
		if ("en_US".equals(locale.toString())) {
			return en;
		}else{
			return zh;
		}
	}

	//生成导入模板
	public void getSheet (XSSFSheet sheet, List<PoColumns> columns, Locale locale, XSSFWorkbook workBook) throws UnsupportedEncodingException {
		//标题样式
		XSSFCellStyle titleStyle = workBook.createCellStyle();
		titleStyle.setAlignment(HorizontalAlignment.CENTER);
		titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		titleStyle.setFillForegroundColor(IndexedColors.BLACK.index);
		titleStyle.setBorderBottom(BorderStyle.THIN);
		titleStyle.setBorderColor(XSSFCellBorder.BorderSide.BOTTOM,new XSSFColor(new java.awt.Color(255, 255, 255)));

		XSSFFont font = workBook.createFont();
		font.setColor(IndexedColors.WHITE.index);
		font.setBold(true);
		titleStyle.setFont(font);
		sheet.createFreezePane(0, 2, 0, 2);
		int rowIndex = 0;
		Row row = sheet.createRow(rowIndex++);
		Row row1 = sheet.createRow(rowIndex++);
		for (int i = 0; i < columns.size()-2; i++) {
			String comments = columns.get(i).getComments();
			String examples = columns.get(i).getExamples();
			comments = getByLocale(locale, comments);
			Cell cell  = row.createCell(i);
			Cell cell1 = row1.createCell(i);
			cell.setCellValue(comments);
			cell1.setCellValue(examples);
			cell.setCellStyle(titleStyle);
			cell1.setCellStyle(titleStyle);
			sheet.setColumnWidth(i, comments.getBytes("GBK").length * 300 + 300);
			if(null!=examples && comments.getBytes("GBK").length<examples.getBytes("GBK").length){
				sheet.setColumnWidth(i, examples.getBytes("GBK").length * 300 + 300);
			}
		}
		//	设置锁定的单元格为写保护
		//	可以单独设置密码
		//	sheet.protectSheet(new String("GaoMinYa"));
		sheet.enableLocking();
		CTSheetProtection sheetProtection = sheet.getCTWorksheet().getSheetProtection();
		sheetProtection.setSelectLockedCells(false);
		sheetProtection.setSelectUnlockedCells(false);
		sheetProtection.setFormatCells(true);
		sheetProtection.setFormatColumns(true);
		sheetProtection.setFormatRows(true);
		sheetProtection.setInsertColumns(true);
		sheetProtection.setInsertRows(false);
		sheetProtection.setInsertHyperlinks(true);
		sheetProtection.setDeleteColumns(true);
		sheetProtection.setDeleteRows(true);
		sheetProtection.setSort(false);
		sheetProtection.setAutoFilter(false);
		sheetProtection.setPivotTables(true);
		sheetProtection.setObjects(true);
		sheetProtection.setScenarios(true);
	}

	//上传模板校验
	public AjaxResult  uploadExcelCheck(Map<String, MultipartFile> mutipartFiles, HttpServletRequest request, AjaxResult result,String[] tableNames) throws Exception {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		String tableName = "" ;
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
			result.put("msg", getLanguage(locale, "請上傳正確格式的Excel文件", "The format of excel is error"));
			return result;
		}

		Workbook wb = null;
		if ("xls".equals(suffix)) {
			//Excel2003
			wb = new HSSFWorkbook(file.getInputStream());
		} else {
			//Excel2007
			wb = new XSSFWorkbook(file.getInputStream());
		}
		wb.close();
//                Assert.isTrue(wb.getNumberOfSheets() - 1 == tableNames.length, getLanguage(locale, "Excel文件內的sheet數量與頁面選中的明細表數量不一致", "The number of sheets in the Excel file is inconsistent with the number of detail tables selected on the page"));
		for (int i = 0; i < tableNames.length; i++) {
			tableName = tableNames[i];
			PoTable poTable = poTableService.get(tableName);
			List<PoColumns> columns = poTable.getColumns();
			List<PoKey> keys = poTable.getKeys();
			int COLUMN_NUM = columns.size()-2;
			Sheet sheet = wb.getSheetAt(i);
			Row firstRow = sheet.getRow(0);
			Assert.notNull(firstRow, getLanguage(locale, "第" + (i + 1) + "個sheet的第一行為標題行，不允許為空", "The title line of the " + (i + 1) + "th sheet cannot be empty"));
			int columnNum = firstRow.getPhysicalNumberOfCells();
			if (columnNum < COLUMN_NUM) {
				result.put("flag", "fail");
				result.put("msg", getLanguage(locale, "第" + (i + 1) + "個sheet的列數不能小於" + COLUMN_NUM, "The number of columns in sheet " + (i + 1) + " cannot be less than " + COLUMN_NUM));
				return result;
			}
			int rowNum = sheet.getPhysicalNumberOfRows();
			if (rowNum < 3) {
				result.put("flag", "fail");
				result.put("msg", getLanguage(locale, "第" + (i + 1) + "個sheet檢測到沒有行數據", "Sheet " + (i + 1) + " does not fill in the data"));
				return result;
			}
			List<List<String>> dataList = new ArrayList<List<String>>();
			for (int j = 2; j < rowNum; j++) {
				Row row = sheet.getRow(j);
				if (row == null) {
					continue;
				}

				boolean isBlankRow = true;
				for (int k = 0; k < COLUMN_NUM; k++) {
					if (StringUtils.isNotEmpty(ExcelUtil.getCellStringValue(row.getCell(k), i, j))) {
						isBlankRow = false;
					}
				}

				if (isBlankRow) {
					continue;
				}
				int n = 0;
				List<String> data = new ArrayList<String>();
				while (n < COLUMN_NUM) {
					PoColumns column = columns.get(n);
					if (column.getNullable() == false) {
						if (column.getDataType().equalsIgnoreCase("date")) {
							try {
								data.add(foxconn.fit.util.DateUtil.formatByddSMMSyyyy(ExcelUtil.getCellDateValue(row.getCell(n), foxconn.fit.util.DateUtil.SDF_ddSMMSyyyy)));
							} catch (Exception e) {
								result.put("flag", "fail");
								result.put("msg", getLanguage(locale, "第" + (i + 1) + "個sheet第" + (j + 1) + "行第" + (n + 1) + "列日期格式錯誤", "The format of date in sheet " + (i + 1) + " row " + (j + 1) + " column " + (n + 1) + " is error"));
								return result;
							}
						} else {
							String value = ExcelUtil.getCellStringValue(row.getCell(n), i, j);
							if (column.getDataType().equalsIgnoreCase("number")) {
								try {
									if ("".equals(value.trim())) {
										if (column.getComments().contains("NTD") || column.getComments().contains("金額")) {
											value = "0";
										}
									}
									Double.parseDouble(value);
								} catch (Exception e) {
									result.put("flag", "fail");
									result.put("msg", getLanguage(locale, "第" + (i + 1) + "個sheet第" + (j + 1) + "行第" + (n + 1) + "列單元格數字格式錯誤【" + value + "】", "The number format of the cell in sheet " + (i + 1) + " row " + (j + 1) + " column " + (n + 1) + " is error)"));
									return result;
								}
							}
							value = value.replaceAll("'", "''");
							data.add(value);
						}
					} else {
						if (column.getDataType().equalsIgnoreCase("date")) {
							try {
								Date date2 = ExcelUtil.getCellDateValue(row.getCell(n), foxconn.fit.util.DateUtil.SDF_ddSMMSyyyy);
								if (date2 != null) {
									data.add(DateUtil.formatByddSMMSyyyy(date2));
								} else {
									data.add("");
								}
							} catch (Exception e) {
								result.put("flag", "fail");
								result.put("msg", getLanguage(locale, "第" + (i + 1) + "個sheet第" + (j + 1) + "行第" + (n + 1) + "列日期格式錯誤", "The format of date in sheet " + (i + 1) + " row " + (j + 1) + " column " + (n + 1) + " is error"));
								return result;
							}
						} else {
							String value = ExcelUtil.getCellStringValue(row.getCell(n), i, j);
							if (StringUtils.isNotEmpty(value)) {
								value = value.replaceAll("'", "''");
								data.add(value);
							} else {
								data.add("");
							}
						}
					}
					n++;
				}
				dataList.add(data);
			}
			result.put("flag", "success");
			result.put(tableName,dataList);
		}
		return result;
	}

	//返回高级查询语句
	public String querySql(List<PoColumns> columns,String queryCondition,String tableName){
		String sql = "select ID,";
		for (PoColumns column : columns) {
			String columnName = column.getColumnName();
			if (column.getDataType().equalsIgnoreCase("number")) {
				sql += "to_char(" + columnName + "),";
			} else if (column.getDataType().equalsIgnoreCase("date")) {
				sql += "to_char(" + columnName + ",'dd/mm/yyyy'),";
			} else {
				sql += columnName + ",";
			}
		}
		sql = sql.substring(0, sql.length() - 1);
		sql += " from " + tableName + " where 1=1";
		if (StringUtils.isNotEmpty(queryCondition)) {
			String[] params = queryCondition.split("&");
			for (String param : params) {
				String columnName = param.substring(0, param.indexOf("="));
				String columnValue = param.substring(param.indexOf("=") + 1).trim();
				if(columnName.equalsIgnoreCase("YEAR_MONTH")){
					columnValue = param.substring(param.indexOf("=") + 1).trim();
					String regEx="[^0-9]";
					Pattern p = Pattern.compile(regEx);
					Matcher m = p.matcher(columnValue);
					columnValue=m.replaceAll("").trim();
				}
				if (StringUtils.isNotEmpty(columnValue)) {
					sql += " and " + columnName + " like '%" + columnValue + "%'";
				}
			}
		}
		return sql;
	}


	//获取用户配置的SBU权限
	public String selectSBU(){
		UserDetailImpl loginUser = SecurityUtils.getLoginUser();
		String userName=loginUser.getUsername();
		String userSql="select sbu from fit_user where username="+"'"+userName+"'";
		List<String> userSBU=poTableService.listBySql(userSql);
		if(null==userSBU||userSBU.size()<1||null==userSBU.get(0)||userSBU.get(0).length()<1){
			return "'1'";
		}else{
			userSql=changeQuerySql(userSBU.get(0));
			return userSql;
		}
	}

	//根据字符串返回数据库查询可用的字符串
	public String changeQuerySql(String value){
		String val="";
		String[] vals=value.split(",");
		for (String v:vals) {
			val+="'"+v+"',";
		}
		val=val.substring(0,val.length()-1);
		return val;
	}

	//根据SBU匹配到BM_SBU
	public String selectBMSBU(){
		String sbu =this.selectSBU();
		String userSql="select distinct BM_SBU from  EPMEBS.CUX_SBU_BU_MAPPING where sbu in ("+sbu+")";
		List<String> userSBU=poTableService.listBySql(userSql);
		if(null==userSBU||userSBU.size()<1){
			return "'1'";
		}else{
			sbu=JSONObject.toJSONString(JSONArray.fromObject(userSBU)).replace('\"','\'');
			return sbu.substring(1,sbu.length()-1);
		}
	}

	//下载sql 可选字段的
	public void downloadExcel(String checkedVal,String tableNames,Locale locale, SXSSFWorkbook sxssfWorkbook, XSSFWorkbook workBook, String queryCondition, PageRequest pageRequest,String special) throws Exception {
		//表头字样
		XSSFCellStyle titleStyle = workBook.createCellStyle();
		titleStyle.setAlignment(HorizontalAlignment.CENTER);
		titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		titleStyle.setFillForegroundColor(IndexedColors.BLACK.index);
		XSSFFont font = workBook.createFont();
		font.setColor(IndexedColors.WHITE.index);
		font.setBold(true);
		titleStyle.setFont(font);

		String[] tables = tableNames.split(",");
		for (String tableName : tables) {
			PoTable poTable = poTableService.get(tableName);
			List<PoColumns> columns = poTable.getColumns();
			List<Integer> lockSerialList = new ArrayList<Integer>();

			String sql = "select ID,";
			Sheet sheet = sxssfWorkbook.createSheet(getByLocale(locale, poTable.getComments()));
			sheet.createFreezePane(0, 1, 0, 1);
			Row titleRow = sheet.createRow(0);
			List<Integer> numberList = new ArrayList<Integer>();

			//生成表头
			for (int i = 0; i < columns.size(); i++) {
				PoColumns poColumn = columns.get(i);
				String columnName = poColumn.getColumnName();
				if (checkedVal.indexOf(columnName)==-1){
					System.out.print(i);
					continue;
				}
				String comments = poColumn.getComments();
				comments = getByLocale(locale, comments);
				if (poColumn.getLocked()) {
					lockSerialList.add(poColumn.getSerial());
				}
				if (poColumn.getDataType().equalsIgnoreCase("number")) {
					sql += "regexp_replace(to_char(" + columnName + ",'FM99999999999999.999999999'),'\\.$',''),";
					numberList.add(i);
				} else if (poColumn.getDataType().equalsIgnoreCase("date")) {
					sql += "to_char(" + columnName + ",'dd/mm/yyyy'),";
				} else {
					sql += columnName + ",";
				}

				Cell cell = titleRow.createCell(i);
				cell.setCellValue(comments);
				cell.setCellStyle(titleStyle);
				sheet.setColumnWidth(i, comments.getBytes("GBK").length * 400 + 400);
			}

//			//排序列
//			String orderBy = "";
//			List<PoKey> keys = poTable.getKeys();
//			if (keys != null && keys.size() > 0) {
//				orderBy = " order by ";
//				for (PoKey key : keys) {
//					orderBy += key.getColumnName() + ",";
//				}
//				orderBy = orderBy.substring(0, orderBy.length() - 1);
//			}

			//页面条件
			String regEx="[^0-9]";
			Pattern p = Pattern.compile(regEx);
			String whereSql = "";
			if (StringUtils.isNotEmpty(queryCondition)) {
				whereSql += " where 1=1 "+special;
				String[] params = queryCondition.split("&");
				for (String param : params) {
					String columnName = param.substring(0, param.indexOf("="));
					String columnValue = param.substring(param.indexOf("=") + 1).trim();
					if(columnName.equalsIgnoreCase("YEAR_MONTH")){
						columnValue = param.substring(param.indexOf("=") + 1).trim();
						Matcher m = p.matcher(columnValue);
						columnValue=m.replaceAll("").trim();
					}
					if (StringUtils.isNotEmpty(columnValue)) {
						whereSql += " and " + columnName + " like '%" + columnValue + "%'";
					}
				}
				whereSql += " order by ID";
			}

			sql = sql.substring(0, sql.length() - 1) + " from " + tableName + whereSql;
			System.out.println(sql);
			pageRequest.setPageSize(ExcelUtil.PAGE_SIZE);
			pageRequest.setPageNo(1);
			List<Object[]> dataList = poTableService.findPageBySql(pageRequest, sql).getResult();
			if (CollectionUtils.isNotEmpty(dataList)) {
				int rowIndex = 1;
				for (Object[] objects : dataList) {
					Row contentRow = sheet.createRow(rowIndex++);
					for (int i = 1; i < objects.length; i++) {
						Cell cell = contentRow.createCell(i-1);
						String text = (objects[i] != null ? objects[i].toString() : "");
						if (StringUtils.isNotEmpty(text) && numberList.contains(i-1)) {
							cell.setCellValue(Double.parseDouble(text));
						} else {
							cell.setCellValue(text);
						}
					}
				}

				while (dataList != null && dataList.size() >= ExcelUtil.PAGE_SIZE) {
					pageRequest.setPageNo(pageRequest.getPageNo() + 1);
					dataList = poTableService.findPageBySql(pageRequest, sql).getResult();
					if (CollectionUtils.isNotEmpty(dataList)) {
						for (Object[] objects : dataList) {
							Row contentRow = sheet.createRow(rowIndex++);
							String generateType = objects[0].toString();
							for (int i = 0; i < objects.length; i++) {
								Cell cell = contentRow.createCell(i);
								String text = (objects[i] != null ? objects[i].toString() : "");
								if (StringUtils.isNotEmpty(text) && numberList.contains(i-1)) {
									cell.setCellValue(Double.parseDouble(text));
								} else {
									cell.setCellValue(text);
								}
							}
						}
					}
				}
			}
		}
	}
}
