package foxconn.fit.controller.hfm;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.base.EnumGenerateType;
import foxconn.fit.entity.base.User;
import foxconn.fit.entity.hfm.AuditColumns;
import foxconn.fit.entity.hfm.AuditKey;
import foxconn.fit.entity.hfm.AuditTable;
import foxconn.fit.service.base.UserService;
import foxconn.fit.service.hfm.AuditTableService;
import foxconn.fit.util.DateUtil;
import foxconn.fit.util.ExcelUtil;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.SecurityUtils;

@SuppressWarnings("deprecation")
@Controller
@RequestMapping("/hfm/audit")
public class AuditController extends BaseController {

	@Autowired
	private AuditTableService auditTableService;
	
	@Autowired
	private UserService userService;
	
	@Value("${schema}")
	private String schema;
	
	@Value("${application}")
	private String application;

	@RequestMapping(value = "index")
	public String index(PageRequest pageRequest,Model model,HttpServletRequest request) {
		try {
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			pageRequest.setOrderBy("tableName");
			pageRequest.setOrderDir("asc");
			List<AuditTable> auditTableList = auditTableService.listByHQL(null, pageRequest);
			List<AuditTable> tableList=new ArrayList<AuditTable>();
			for (AuditTable auditTable : auditTableList) {
				tableList.add(new AuditTable(auditTable.getTableName(),getByLocale(locale, auditTable.getComments())));
			}
			model.addAttribute("auditTableList", tableList);
			
			User user = userService.getByUsername(SecurityUtils.getLoginUsername());
			model.addAttribute("attribute", user.getAttribute());
			List<String> typeList = auditTableService.listBySql("select distinct tablename from FIT_AUDIT_CONSOL_CONFIG order by tablename");
			model.addAttribute("typeList", typeList);
		} catch (Exception e) {
			logger.error("查询明细配置表列表信息失败", e);
		}
		return "/hfm/audit/index";
	}
	
	@RequestMapping(value="/list")
	public String list(Model model, PageRequest pageRequest,HttpServletRequest request,String date,String entity,String tableName) {
		try {
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			Assert.hasText(tableName, getLanguage(locale, "明細表不能為空", "The table cannot be empty"));
			AuditTable auditTable = auditTableService.get(tableName);
			List<AuditColumns> columns = auditTable.getColumns();
			for (AuditColumns auditColumns : columns) {
				auditColumns.setComments(getByLocale(locale, auditColumns.getComments()));
			}
			String sql="select ";
			for (AuditColumns column : columns) {
				String columnName = column.getColumnName();
				if (column.getDataType().equalsIgnoreCase("number")) {
					sql+="to_char("+columnName+"),";
				}else if (column.getDataType().equalsIgnoreCase("date")) {
					sql+="to_char("+columnName+",'dd/mm/yyyy'),";
				}else{
					sql+=columnName+",";
				}
			}
			sql=sql.substring(0,sql.length()-1);
			sql+=" from "+auditTable.getTableName()+" where 1=1";
			if (StringUtils.isNotEmpty(date)) {
				Date d = DateUtil.parseByYyyy_MM(date);
				Assert.notNull(d, getLanguage(locale, "年月格式錯誤", "The format of year/month is error"));
				String[] split = date.split("-");
				String year = split[0];
				String period = split[1];
				if (period.length()<2) {
					period="0"+period;
				}
				sql+=" and "+columns.get(1).getColumnName()+"='"+year+"' and "+columns.get(2).getColumnName()+"='"+period+"'";
			}
			if (StringUtils.isNotEmpty(entity)) {
				sql+=" and "+columns.get(3).getColumnName()+"='"+entity+"'";
			}
			pageRequest.setOrderBy(columns.get(3).getColumnName()+","+columns.get(1).getColumnName()+","+columns.get(2).getColumnName()+","+columns.get(0).getColumnName());
			pageRequest.setOrderDir("asc,asc,asc,asc");
			Page<Object[]> page = auditTableService.findPageBySql(pageRequest, sql);
			model.addAttribute("page", page);
			model.addAttribute("columns", columns);
		} catch (Exception e) {
			logger.error("查询明细配置表列表失败:", e);
		}
		return "/hfm/audit/list";
	}
	
	@RequestMapping(value = "dataImport")
	@ResponseBody
	@Log(name = "审计资料模块-->数据导入")
	public String dataImport(HttpServletRequest request,AjaxResult result,@Log(name = "年月") String date,@Log(name = "公司编码") String entity) {
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
			String message = auditTableService.dataImport(year,period,entity,locale);
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
	
	@RequestMapping(value = "check")
	@ResponseBody
	public String check(HttpServletRequest request,HttpServletResponse response, AjaxResult result,
			String date,String entity,String[] tableNames) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		result.put("msg", getLanguage(locale, "校驗通過", "Check pass"));
		try {
			Assert.isTrue(tableNames!=null && tableNames.length>0, getLanguage(locale, "明細表不能為空", "The table cannot be empty"));
			Date d = DateUtil.parseByYyyy_MM(date);
			Assert.notNull(d, getLanguage(locale, "年月格式錯誤", "The format of year/month is error"));
			String[] split = date.split("-");
			String year = split[0];
			String period = split[1];
			if (period.length()<2) {
				period="0"+period;
			}
			Assert.hasText(entity, getLanguage(locale, "公司編碼不能為空", "The company code cannot be empty"));
			
			List<String> tarList=new ArrayList<String>();
			String entity1 = SecurityUtils.getEntity();
			if (StringUtils.isNotEmpty(entity1)) {
				for (String string : entity1.split(",")) {
					tarList.add(string.substring(2, string.length()));
				}
			}
			Assert.isTrue(tarList.contains(entity), getLanguage(locale, "錯誤的公司編碼", "The company code is error"));
			String message="";
			for (String tableName : tableNames) {
				String msg = auditTableService.validate(tableName, year, period, entity, AuditTableService.DATA_VALIDATE,locale);
				if (StringUtils.isNotEmpty(msg)) {
					message+=msg+"</br>";
				}
			}
			if (StringUtils.isNotEmpty(message)) {
				result.put("flag", "fail");
				result.put("msg", message);
				return result.getJson();
			}
		} catch (Exception e) {
			logger.error("校验失败", e);
			result.put("flag", "fail");
			result.put("msg", getLanguage(locale, "校驗失敗", "Check failure") + " : "+ExceptionUtil.getRootCauseMessage(e));
		}

		return result.getJson();
	}
	
	@RequestMapping(value = "checkAll")
	@ResponseBody
	public String checkAll(HttpServletRequest request,HttpServletResponse response,PageRequest pageRequest, AjaxResult result,String date) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		result.put("msg", getLanguage(locale, "校驗通過", "Check pass"));
		try {
			Date d = DateUtil.parseByYyyy_MM(date);
			Assert.notNull(d, getLanguage(locale, "年月格式錯誤", "The format of year/month is error"));
			String[] split = date.split("-");
			String year = split[0];
			String period = split[1];
			if (period.length()<2) {
				period="0"+period;
			}
			
			pageRequest.setOrderBy("tableName");
			pageRequest.setOrderDir("asc");
			List<AuditTable> auditTableList = auditTableService.listByHQL(null, pageRequest);
			Assert.isTrue(auditTableList!=null && auditTableList.size()>0, getLanguage(locale, "明細表不能為空", "The table cannot be empty"));
			
			List<String> tarList=new ArrayList<String>();
			String entitys = SecurityUtils.getEntity();
			if (StringUtils.isNotEmpty(entitys)) {
				for (String string : entitys.split(",")) {
					tarList.add(string.substring(2, string.length()));
				}
			}
			Assert.isTrue(!tarList.isEmpty(), getLanguage(locale, "公司編碼不能為空", "The company code cannot be empty"));
			
			String message="";
			for (String entity : tarList) {
				String returnMsg="";
				for (AuditTable auditTable : auditTableList) {
					String msg = auditTableService.validate(auditTable.getTableName(), year, period, entity, AuditTableService.DATA_VALIDATE,locale);
					if (StringUtils.isNotEmpty(msg)) {
						if (msg.startsWith("</br>")) {
							msg=msg.substring(5);
						}
						returnMsg+=msg+"</br>";
					}
				}
				if (StringUtils.isEmpty(returnMsg)) {
					returnMsg=getLanguage(locale, "通過", "Pass")+"</br>";
					message+="<span style='font-weight:bold;font-size:24px;color:#1ddb1d;'>"+entity+"</span> --> "+returnMsg;
				}else{
					message+="<span style='font-weight:bold;font-size:24px;color:red;'>"+entity+"</span> --> "+returnMsg;
				}
			}
			
			result.put("flag", "fail");
			result.put("msg", message);
			return result.getJson();
		} catch (Exception e) {
			logger.error("校验失败", e);
			result.put("flag", "fail");
			result.put("msg", getLanguage(locale, "校驗失敗", "Check failure") + " : "+ExceptionUtil.getRootCauseMessage(e));
		}

		return result.getJson();
	}
	
	@RequestMapping(value = "upload")
	@ResponseBody
	@Log(name = "审计资料模块-->上传")
	public String upload(HttpServletRequest request,HttpServletResponse response, AjaxResult result,
			@Log(name = "年月") String date,@Log(name = "公司编码") String entity,@Log(name = "明细表名称") String[] tableNames) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		result.put("msg", getLanguage(locale, "上傳成功", "Upload success"));
		try {
			Assert.isTrue(tableNames!=null && tableNames.length>0, getLanguage(locale, "明細表不能為空", "The table cannot be empty"));
			Date d = DateUtil.parseByYyyy_MM(date);
			Assert.notNull(d, getLanguage(locale, "年月格式錯誤", "The format of year/month is error"));
			String[] split = date.split("-");
			String year = split[0];
			String period = split[1];
			if (period.length()<2) {
				period="0"+period;
			}
			Assert.hasText(entity, getLanguage(locale, "公司編碼不能為空", "The company code cannot be empty"));
			
			List<String> tarList=new ArrayList<String>();
			String entity1 = SecurityUtils.getEntity();
			if (StringUtils.isNotEmpty(entity1)) {
				for (String string : entity1.split(",")) {
					if (string.startsWith("F_")) {
						tarList.add(string.substring(2, string.length()));
					}
				}
			}
			Assert.isTrue(tarList.contains(entity), getLanguage(locale, "錯誤的公司編碼", "The company code is error"));
			
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
					result.put("msg", getLanguage(locale, "請上傳正確格式的Excel文件", "The format of excel is error"));
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
				Assert.isTrue(wb.getNumberOfSheets()==tableNames.length, getLanguage(locale, "Excel文件內的sheet數量與頁面選中的明細表數量不一致", "The number of sheets in the Excel file is inconsistent with the number of detail tables selected on the page"));
				
				List entityDescList = auditTableService.listBySql("select name from (select distinct to_char(d.description) as name from "+schema+"."+application+"_ENTITY_ITEM i,"+schema+"."+application+"_ENTITY_DESC d where i.itemid=d.itemid and i.label='"+entity+"' order by d.description) where rownum=1");
				Assert.isTrue(entityDescList.size()==1, getLanguage(locale, "未查詢到HFM公司描述", "The name of HFM company is not queried"));
				String entityName=entityDescList.get(0).toString();
				
				Map<AuditTable,List<List<String>>> dataMap=new HashMap<AuditTable, List<List<String>>>();
				for (int i=0;i<tableNames.length;i++) {
					String tableName = tableNames[i];
					AuditTable auditTable = auditTableService.get(tableName);
					List<AuditColumns> columns = auditTable.getColumns();
					List<AuditKey> keys = auditTable.getKeys();
					int COLUMN_NUM=columns.size();
					Sheet sheet = wb.getSheetAt(i);
					Row firstRow = sheet.getRow(0);
					Assert.notNull(firstRow, getLanguage(locale, "第"+(i+1)+"個sheet的第一行為標題行，不允許為空", "The title line of the "+(i+1)+"th sheet cannot be empty"));
					int columnNum = firstRow.getPhysicalNumberOfCells();
					
					if(columnNum<COLUMN_NUM){
						result.put("flag", "fail");
						result.put("msg", getLanguage(locale, "第"+(i+1)+"個sheet的列數不能小於"+COLUMN_NUM, "The number of columns in sheet "+(i+1)+" cannot be less than "+COLUMN_NUM));
						return result.getJson();
					}

					int rowNum = sheet.getPhysicalNumberOfRows();
					if (rowNum<2) {
						result.put("flag", "fail");
						result.put("msg", getLanguage(locale, "第"+(i+1)+"個sheet檢測到沒有行數據", "Sheet "+(i+1)+" does not fill in the data"));
						return result.getJson();
					}
					
					List<List<String>> dataList=new ArrayList<List<String>>();
					List<Integer> keySerialList=new ArrayList<Integer>();
					if (keys!=null && keys.size()>0) {
						for (AuditKey auditKey : keys) {
							keySerialList.add(auditKey.getSerial());
						}
					}
					Map<String,Integer> keyRepeatMap=new HashMap<String, Integer>();
					for (int j = 1; j < rowNum; j++) {
						Row row = sheet.getRow(j);
						if (row==null) {
							continue;
						}
						
						boolean isBlankRow=true;
						for (int k = 0; k < COLUMN_NUM; k++) {
							if (StringUtils.isNotEmpty(ExcelUtil.getCellStringValue(row.getCell(k),i,j))) {
								isBlankRow=false;
							}
						}
						
						if (isBlankRow) {
							continue;
						}
						
						int n=0;
						String generateType=ExcelUtil.getCellStringValue(row.getCell(n),i,j).toUpperCase();
						if (StringUtils.isNotEmpty(generateType)) {
							try {
								EnumGenerateType.valueOf(generateType);
							} catch (Exception e) {
								result.put("flag", "fail");
								result.put("msg", getLanguage(locale, "第"+(i+1)+"個sheet第"+(j+1)+"行第"+(n+1)+"列標識不符合約定(A-全自動,M-全手工,AM-半自動)", "The generate type of sheet "+(i+1)+" row "+(j+1)+" column "+(n+1)+" is error (A- automatic,M- manual,AM- semi-automatic)"));
								return result.getJson();
							}
							if (EnumGenerateType.A.getCode().equals(generateType)) {
								continue;
							}
						}else{
							generateType=EnumGenerateType.M.getCode();
						}
						List<String> data=new ArrayList<String>();
						data.add(generateType);
						data.add(year);
						data.add(period);
						data.add(entity);
						data.add(entityName);
						n+=5;
						String key=null;
						while (n<COLUMN_NUM) {
							AuditColumns column = columns.get(n);
							if (keySerialList.contains(column.getSerial())) {
								String value = ExcelUtil.getCellStringValue(row.getCell(n),i,j);
								if (StringUtils.isEmpty(value)) {
									result.put("flag", "fail");
									result.put("msg", getLanguage(locale, "第"+(i+1)+"個sheet第"+(j+1)+"行第"+(n+1)+"列單元格內容不能為空", "The contents of the cell in sheet "+(i+1)+" row "+(j+1)+" column "+(n+1)+" cannot be empty"));
									return result.getJson();
								}
								value = value.replaceAll("'", "''");
								data.add(value);
							}else{
								if (column.getNullable()==false) {
									if (column.getDataType().equalsIgnoreCase("date")) {
										try {
											data.add(DateUtil.formatByddSMMSyyyy(ExcelUtil.getCellDateValue(row.getCell(n), DateUtil.SDF_ddSMMSyyyy)));
										} catch (Exception e) {
											result.put("flag", "fail");
											result.put("msg", getLanguage(locale, "第"+(i+1)+"個sheet第"+(j+1)+"行第"+(n+1)+"列日期格式錯誤", "The format of date in sheet "+(i+1)+" row "+(j+1)+" column "+(n+1)+" is error"));
											return result.getJson();
										}
									}else{
										String value = ExcelUtil.getCellStringValue(row.getCell(n),i,j);
										if (StringUtils.isEmpty(value)) {
											result.put("flag", "fail");
											result.put("msg", getLanguage(locale, "第"+(i+1)+"個sheet第"+(j+1)+"行第"+(n+1)+"列單元格內容不能為空", "The contents of the cell in sheet "+(i+1)+" row "+(j+1)+" column "+(n+1)+" cannot be empty"));
											return result.getJson();
										}
										if(column.getDataType().equalsIgnoreCase("number")){
											try {
												Double.parseDouble(value);
											} catch (Exception e) {
												result.put("flag", "fail");
												result.put("msg", getLanguage(locale, "第"+(i+1)+"個sheet第"+(j+1)+"行第"+(n+1)+"列單元格數字格式錯誤【"+value+"】", "The number format of the cell in sheet "+(i+1)+" row "+(j+1)+" column "+(n+1)+" is error)"));
												return result.getJson();
											}
										}
										value = value.replaceAll("'", "''");
										data.add(value);
									}
								}else{
									if (column.getDataType().equalsIgnoreCase("date")) {
										try {
											Date date2 = ExcelUtil.getCellDateValue(row.getCell(n), DateUtil.SDF_ddSMMSyyyy);
											if (date2!=null) {
												data.add(DateUtil.formatByddSMMSyyyy(date2));
											}else{
												data.add("");
											}
										} catch (Exception e) {
											result.put("flag", "fail");
											result.put("msg", getLanguage(locale, "第"+(i+1)+"個sheet第"+(j+1)+"行第"+(n+1)+"列日期格式錯誤", "The format of date in sheet "+(i+1)+" row "+(j+1)+" column "+(n+1)+" is error"));
											return result.getJson();
										}
									}else{
										String value = ExcelUtil.getCellStringValue(row.getCell(n),i,j);
										if (StringUtils.isNotEmpty(value)) {
											if(column.getDataType().equalsIgnoreCase("number")){
												try {
													Double.parseDouble(value);
												} catch (Exception e) {
													result.put("flag", "fail");
													result.put("msg", getLanguage(locale, "第"+(i+1)+"個sheet第"+(j+1)+"行第"+(n+1)+"列單元格數字格式錯誤【"+value+"】", "The number format of the cell in sheet "+(i+1)+" row "+(j+1)+" column "+(n+1)+" is error)"));
													return result.getJson();
												}
											}
											value = value.replaceAll("'", "''");
											data.add(value);
										}else {
											data.add("");
										}
									}
								}
							}
							if (keySerialList.contains(column.getSerial())) {
								String value = ExcelUtil.getCellStringValue(row.getCell(n),i,j);
								key+=value;
							}
							n++;
						}
						if (StringUtils.isNotEmpty(key)) {
							String base64 = Base64.getEncoder().encodeToString(key.getBytes());
							if (keyRepeatMap.containsKey(base64)) {
								Integer rowIndex = keyRepeatMap.get(base64);
								List<String> keyNameList=new ArrayList<String>();
								for (AuditKey auditKey : keys) {
									if (auditKey.getSerial()>=5) {
										keyNameList.add(auditKey.getComments());
									}
								}
								result.put("flag", "fail");
								result.put("msg", getLanguage(locale, "第"+(i+1)+"個sheet第"+(j+1)+"行的Key值與第"+rowIndex+"行的Key值重複,Key包含的列有 : ", "The key value of the sheet "+(i+1)+" row "+(j+1)+" is repeated with the key value of row "+rowIndex+". The key contains the following columns : ") + Arrays.toString(keyNameList.toArray()));
								return result.getJson();
							}else{
								keyRepeatMap.put(base64, j+1);
							}
						}
						
						dataList.add(data);
					}
					
					if (!dataList.isEmpty()) {
						dataMap.put(auditTable, dataList);
					}else{
						result.put("flag", "fail");
						result.put("msg", getLanguage(locale, "第"+(i+1)+"個sheet無有效數據行", "The sheet "+(i+1)+" has no valid data row"));
					}
				}
				
				auditTableService.saveAuditTempData(dataMap, year, period, entity);
				String message="";
				for (String tableName : tableNames) {
					String msg = auditTableService.validate(tableName+"_T", year, period, entity, AuditTableService.UPLOAD_VALIDATE,locale);
					if (StringUtils.isNotEmpty(msg)) {
						message+=msg+"</br>";
					}
				}
				if (StringUtils.isNotEmpty(message)) {
					result.put("flag", "fail");
					result.put("msg", message);
					return result.getJson();
				}
				auditTableService.saveAuditData(dataMap,year,period,entity);
			} else {
				result.put("flag", "fail");
				result.put("msg", getLanguage(locale, "對不起，未接收到上傳的文件", "Uploaded file not received"));
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
	@Log(name = "审计资料模块-->下载")
	public synchronized String download(HttpServletRequest request,HttpServletResponse response,PageRequest pageRequest,AjaxResult result,
			@Log(name = "年月") String date,@Log(name = "公司编码") String entity,@Log(name = "明细表名称") String tableNames){
		try {
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			Assert.hasText(tableNames, getLanguage(locale, "明細表不能為空", "The table cannot be empty"));
			Date d = DateUtil.parseByYyyy_MM(date);
			Assert.notNull(d, getLanguage(locale, "年月格式錯誤", "The format of year/month is error"));
			String[] split = date.split("-");
			String year = split[0];
			String period = split[1];
			if (period.length()<2) {
				period="0"+period;
			}
			Assert.hasText(entity, getLanguage(locale, "公司編碼不能為空", "The company code cannot be empty"));
			List<String> tarList=new ArrayList<String>();
			String entity1 = SecurityUtils.getEntity();
			if (StringUtils.isNotEmpty(entity1)) {
				for (String string : entity1.split(",")) {
					tarList.add(string.substring(2));
				}
			}
			Assert.isTrue(tarList.contains(entity), getLanguage(locale, "錯誤的公司編碼", "The company code is error"));
			
			XSSFWorkbook workBook = new XSSFWorkbook();
			XSSFCellStyle titleStyle = workBook.createCellStyle();
			titleStyle.setAlignment(HorizontalAlignment.CENTER);
			titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			titleStyle.setFillForegroundColor(IndexedColors.BLACK.index);
			XSSFCellStyle lockStyle = workBook.createCellStyle();
			lockStyle.setAlignment(HorizontalAlignment.CENTER);
			lockStyle.setFillForegroundColor(new XSSFColor(new Color(217, 217, 217)));
			lockStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			XSSFCellStyle unlockStyle = workBook.createCellStyle();
			unlockStyle.setAlignment(HorizontalAlignment.CENTER);
			XSSFFont font = workBook.createFont();
			font.setColor(IndexedColors.WHITE.index);
			font.setBold(true);
			titleStyle.setFont(font);
			
			SXSSFWorkbook sxssfWorkbook=new SXSSFWorkbook(workBook);
			for (String tableName : tableNames.split(",")) {
				AuditTable auditTable = auditTableService.get(tableName);
				List<AuditColumns> columns = auditTable.getColumns();
				List<Integer> lockSerialList=new ArrayList<Integer>();
				String sql="select ";
				Sheet sheet = sxssfWorkbook.createSheet(getByLocale(locale, auditTable.getComments()));
				sheet.setDefaultColumnStyle(0, lockStyle);
				sheet.setDefaultColumnStyle(1, lockStyle);
				sheet.setDefaultColumnStyle(2, lockStyle);
				sheet.setDefaultColumnStyle(3, lockStyle);
				sheet.setDefaultColumnStyle(4, lockStyle);
				sheet.createFreezePane(0,1, 0, 1);
				Row titleRow = sheet.createRow(0);
				List<Integer> numberList=new ArrayList<Integer>();
				for (int i = 0; i < columns.size(); i++) {
					AuditColumns auditColumn = columns.get(i);
					String columnName = auditColumn.getColumnName();
					String comments = auditColumn.getComments();
					comments=getByLocale(locale, comments);
					if (auditColumn.getLocked()) {
						lockSerialList.add(auditColumn.getSerial());
					}
					if (auditColumn.getDataType().equalsIgnoreCase("number")) {
						sql+="regexp_replace(to_char("+columnName+",'FM99999999999999.999999999'),'\\.$',''),";
						numberList.add(i);
					}else if (auditColumn.getDataType().equalsIgnoreCase("date")) {
						sql+="to_char("+columnName+",'dd/mm/yyyy'),";
					}else{
						sql+=columnName+",";
					}
					
					Cell cell = titleRow.createCell(i);
					cell.setCellValue(comments);
					cell.setCellStyle(titleStyle);
					sheet.setColumnWidth(i, comments.getBytes("GBK").length*256+400);
				}
				String orderBy="";
				List<AuditKey> keys = auditTable.getKeys();
				if (keys!=null && keys.size()>0) {
					orderBy=" order by ";
					for (AuditKey key : keys) {
						orderBy+=key.getColumnName()+",";
					}
					orderBy=orderBy.substring(0, orderBy.length()-1);
				}
				String yearColumn = columns.get(1).getColumnName();
				String periodColumn = columns.get(2).getColumnName();
				String entityColumn = columns.get(3).getColumnName();
				
				sql=sql.substring(0, sql.length()-1) + " from " + tableName + " where " + yearColumn + "='" + year + "' and " + periodColumn + "='"+period + "' and " + entityColumn + "='" + entity + "'"+orderBy;
				pageRequest.setPageSize(ExcelUtil.PAGE_SIZE);
				pageRequest.setPageNo(1);
				List<Object[]> dataList = auditTableService.findPageBySql(pageRequest, sql).getResult();
				if (CollectionUtils.isNotEmpty(dataList)) {
					int rowIndex=1;
					for (Object[] objects : dataList) {
						Row contentRow = sheet.createRow(rowIndex++);
						String generateType = objects[0].toString();
						for (int i = 0; i < objects.length; i++) {
							Cell cell = contentRow.createCell(i);
							String text=(objects[i]!=null?objects[i].toString():"");
							if (StringUtils.isNotEmpty(text) && numberList.contains(i)) {
								cell.setCellValue(Double.parseDouble(text));
							}else{
								cell.setCellValue(text);
							}
							if (i<5 || EnumGenerateType.A.getCode().equals(generateType) || (EnumGenerateType.AM.getCode().equals(generateType) && lockSerialList.contains(new Integer(i)))) {
								cell.setCellStyle(lockStyle);
							}else{
								cell.setCellStyle(unlockStyle);
							}
						}
					}
					
					while (dataList !=null && dataList.size()>=ExcelUtil.PAGE_SIZE) {
						pageRequest.setPageNo(pageRequest.getPageNo()+1);
						dataList = auditTableService.findPageBySql(pageRequest, sql).getResult();
						if (CollectionUtils.isNotEmpty(dataList)) {
							for (Object[] objects : dataList) {
								Row contentRow = sheet.createRow(rowIndex++);
								String generateType = objects[0].toString();
								for (int i = 0; i < objects.length; i++) {
									Cell cell = contentRow.createCell(i);
									String text=(objects[i]!=null?objects[i].toString():"");
									if (StringUtils.isNotEmpty(text) && numberList.contains(i)) {
										cell.setCellValue(Double.parseDouble(text));
									}else{
										cell.setCellValue(text);
									}
									if (i<5 || EnumGenerateType.A.getCode().equals(generateType) || (EnumGenerateType.AM.getCode().equals(generateType) && lockSerialList.contains(new Integer(i)))) {
										cell.setCellStyle(lockStyle);
									}else{
										cell.setCellStyle(unlockStyle);
									}
								}
							}
						}
					}
				}
				sheet.setColumnHidden(0, true);
			}
			
			File outFile=new File(request.getRealPath("")+File.separator+"static"+File.separator+"download"+File.separator+System.currentTimeMillis()+".xlsx");
			OutputStream out = new FileOutputStream(outFile);
			sxssfWorkbook.write(out);
			sxssfWorkbook.close();
			out.flush();
			out.close();
			
			result.put("fileName", outFile.getName());
			System.gc();
		} catch (Exception e) {
			logger.error("下载Excel失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}
		
		return result.getJson();
	}
	
	@RequestMapping(value = "consolidationDataDownload")
	@ResponseBody
	@Log(name = "审计资料模块-->合并资料下载")
	public synchronized String consolidationDataDownload(HttpServletRequest request,HttpServletResponse response,PageRequest pageRequest,AjaxResult result,
			@Log(name = "年月") String date,@Log(name = "类型") String type){
		try {
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			Assert.hasText(type, getLanguage(locale, "类型不能为空", "Type can not be empty"));
			Date d = DateUtil.parseByYyyy_MM(date);
			Assert.notNull(d, getLanguage(locale, "年月格式錯誤", "The format of year/month is error"));
			String[] split = date.split("-");
			String year = split[0];
			String period = split[1];
			if (period.length()<2) {
				period="0"+period;
			}
			
			List<Object[]> tableList = auditTableService.listBySql("select FILENAME,PROCEDURENAME from FIT_AUDIT_CONSOL_CONFIG where TABLENAME='"+type+"'");
			Assert.isTrue(tableList!=null && tableList.size()>0, "FIT_AUDIT_CONSOL_CONFIG表未找到对应的记录");
			Object[] objs = tableList.get(0);
			String fileName=objs[0].toString();
			String procedureName=objs[1]==null?"":objs[1].toString();
			if (StringUtils.isNotEmpty(procedureName)) {
				auditTableService.generateConsolidation(year,period,procedureName);
			}
			
			String realPath = request.getRealPath("");
			File file=new File(request.getRealPath("")+File.separator+"static"+File.separator+"template"+File.separator+"hfm"+File.separator+fileName);
			XSSFWorkbook workBook = new XSSFWorkbook(new FileInputStream(file));
			Sheet sheet = workBook.getSheetAt(0);
			Map<String,String> tableMap=new HashMap<String, String>();
			int rowNum = sheet.getPhysicalNumberOfRows();
			for (int i = 2; i < rowNum; i++) {
				Row row = sheet.getRow(i);
				Cell cell = row.getCell(4);
				if (cell!=null) {
					String flag = cell.getStringCellValue();
					if ("Y".equals(flag)) {
						String tableName = row.getCell(5).getStringCellValue().trim().toUpperCase();
						String sheetName = row.getCell(1).getStringCellValue().trim();
						tableMap.put(tableName, sheetName);
					}
				}
			}
			SXSSFWorkbook sxssfWorkbook=new SXSSFWorkbook(workBook);
			for (String tableName : tableMap.keySet()) {
				String sheetName = tableMap.get(tableName);
				SXSSFSheet targetSheet = sxssfWorkbook.getSheet(sheetName);
				String sql="select t.DATA_TYPE,t.COLUMN_NAME from user_tab_columns t where t.TABLE_NAME='"+tableName+"' order by t.COLUMN_ID";
				List<Object[]> columnList = auditTableService.listBySql(sql);
				List<Integer> numberList=new ArrayList<Integer>();
				String querySql="select ";
				for(int i=0;i<columnList.size();i++){
					Object[] objects = columnList.get(i);
					String dataType=objects[0].toString();
					String columnName=objects[1].toString();
					if ("number".equalsIgnoreCase(dataType)) {
						querySql+="regexp_replace(to_char("+columnName+",'FM99999999999999.999999999'),'\\.$',''),";
						numberList.add(i);
					}else if ("date".equalsIgnoreCase(dataType)) {
						querySql+="to_char("+columnName+",'yyyy-MM-dd'),";
					}else{
						querySql+=columnName+",";
					}
				}
				querySql=querySql.substring(0, querySql.length()-1);
				
				List<Object[]> list = auditTableService.listBySql(querySql+" from "+tableName+" where year='"+year+"' and period='"+period+"'");
				logger.info("开始加载sheet页数据【"+sheetName+"】");
				for (int i = 0; i < list.size(); i++) {
					SXSSFRow row = targetSheet.createRow(i+1);
					Object[] objects = list.get(i);
					for (int j = 0; j < objects.length; j++) {
						String text=(objects[j]!=null?objects[j].toString():"");
						SXSSFCell cell = row.createCell(j);
						if (StringUtils.isNotEmpty(text) && numberList.contains(j)) {
							cell.setCellValue(Double.parseDouble(text));
						}else{
							cell.setCellValue(text);
						}
					}
				}
				targetSheet.setForceFormulaRecalculation(true);
			}
			File outFile=new File(realPath+File.separator+"static"+File.separator+"download"+File.separator+System.currentTimeMillis()+".xlsm");
			OutputStream out = new FileOutputStream(outFile);
			sxssfWorkbook.write(out);
			sxssfWorkbook.close();
			out.flush();
			out.close();
			
			result.put("fileName", outFile.getName());
			System.gc();
		} catch (Exception e) {
			logger.error("合并资料下载失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}
		
		return result.getJson();
	}
	
	@RequestMapping(value = "template")
	@ResponseBody
	public synchronized String template(HttpServletRequest request,HttpServletResponse response,PageRequest pageRequest,AjaxResult result,String tableNames){
		Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		try {
			Assert.hasText(tableNames, getLanguage(locale, "明細表不能為空", "The table cannot be empty"));
			if (tableNames.endsWith(",")) {
				tableNames=tableNames.substring(0, tableNames.length()-1);
			}
			
			XSSFWorkbook workBook = new XSSFWorkbook();
			XSSFCellStyle titleStyle = workBook.createCellStyle();
			titleStyle.setAlignment(HorizontalAlignment.CENTER);
			titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			titleStyle.setFillForegroundColor(IndexedColors.BLACK.index);
			XSSFCellStyle lockStyle = workBook.createCellStyle();
			lockStyle.setAlignment(HorizontalAlignment.CENTER);
			lockStyle.setFillForegroundColor(new XSSFColor(new Color(217, 217, 217)));
			lockStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			XSSFCellStyle unlockStyle = workBook.createCellStyle();
			unlockStyle.setAlignment(HorizontalAlignment.CENTER);
			XSSFFont font = workBook.createFont();
			font.setColor(IndexedColors.WHITE.index);
			font.setBold(true);
			titleStyle.setFont(font);
			
			for (String tableName : tableNames.split(",")) {
				AuditTable auditTable = auditTableService.get(tableName);
				List<AuditColumns> columns = auditTable.getColumns();
				Sheet sheet = workBook.createSheet(getByLocale(locale, auditTable.getComments()));
				sheet.setDefaultColumnStyle(0, lockStyle);
				sheet.setDefaultColumnStyle(1, lockStyle);
				sheet.setDefaultColumnStyle(2, lockStyle);
				sheet.setDefaultColumnStyle(3, lockStyle);
				sheet.setDefaultColumnStyle(4, lockStyle);
				sheet.createFreezePane(0,1, 0, 1);
//				sheet.createFreezePane(5,0, 6, 0);
				int rowIndex=0;
				Row row = sheet.createRow(rowIndex++);
				for (int i = 0; i < columns.size(); i++) {
					String comments = columns.get(i).getComments();
					comments = getByLocale(locale, comments);
					Cell cell = row.createCell(i);
					cell.setCellValue(comments);
					cell.setCellStyle(titleStyle);
					sheet.setColumnWidth(i, comments.getBytes("GBK").length*256+400);
				}
				sheet.setColumnHidden(0, true);
			}
			
			File outFile=new File(request.getRealPath("")+File.separator+"static"+File.separator+"download"+File.separator+System.currentTimeMillis()+".xlsx");
			OutputStream out = new FileOutputStream(outFile);
			workBook.write(out);
			workBook.close();
			out.flush();
			out.close();
			
			result.put("fileName", outFile.getName());
		} catch (Exception e) {
			logger.error("下载模板文件失败", e);
			result.put("flag", "fail");
			result.put("msg", getLanguage(locale, "下載模板文件失敗", "Fail to download template file") + " : "+ExceptionUtil.getRootCauseMessage(e));
		}
		
		return result.getJson();
	}

}
