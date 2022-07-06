package foxconn.fit.controller.bi;

import java.awt.Color;
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
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.dao.base.PropertyFilter;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.base.EnumRevenveDetailType;
import foxconn.fit.entity.bi.RevenueDetailManual;
import foxconn.fit.service.bi.RevenueDetailManualService;
import foxconn.fit.util.DateUtil;
import foxconn.fit.util.ExcelUtil;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.NumberUtil;
import foxconn.fit.util.SecurityUtils;

@Controller
@RequestMapping("/bi/revenueDetailManual")
public class RevenueDetailManualController extends BaseController {

	private static String EXCEL_NAME="營收明細手工處理";
	private static int COLUMN_NUM=54;
	
	@Autowired
	private RevenueDetailManualService revenueDetailManualService;

	@RequestMapping(value = "index")
	public String index(Model model) {
		List<String> tarList=new ArrayList<String>();
		String corporationCode = SecurityUtils.getCorporationCode();
		if (StringUtils.isNotEmpty(corporationCode)) {
			for (String string : corporationCode.split(",")) {
				tarList.add(string.substring(2, string.length()));
			}
		}
		List<String> codeList = null;
		List<String> list = revenueDetailManualService.listBySql("select distinct legal_code from FIT_Revenue_Detail_Manual order by legal_code");
		if (list.size()>0) {
			codeList=new ArrayList<String>();
			for (String code : list) {
				if (tarList.contains(code)) {
					codeList.add(code);
				}
			}
		}
		model.addAttribute("codelist", codeList);
		return "/bi/revenueDetailManual/index";
	}
	
	@RequestMapping(value="/list")
	public String list(Model model, PageRequest pageRequest,String corporationCode,String period,String version) {
		try {
			List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
			
			if (StringUtils.isNotEmpty(period)) {
				Date date = DateUtil.parseByYyyy_MM(period);
				Assert.notNull(date, "年月格式错误(Error Date Formats)");
				String[] split = period.split("-");
				String year = split[0];
				String month = split[1];
				filters.add(new PropertyFilter("EQS_year",year));
				filters.add(new PropertyFilter("EQS_period",month));
			}
			
			if (StringUtils.isNotEmpty(version)) {
				filters.add(new PropertyFilter("EQS_version",version));
			}
			
			List<String> tarList=new ArrayList<String>();
			String corporationCode2 = SecurityUtils.getCorporationCode();
			if (StringUtils.isNotEmpty(corporationCode2)) {
				for (String string : corporationCode2.split(",")) {
					tarList.add(string.substring(2, string.length()));
				}
			}
			
			if (StringUtils.isNotEmpty(corporationCode) && tarList.contains(corporationCode)) {
				filters.add(new PropertyFilter("EQS_legalCode",corporationCode));
			}else{
				String corCode="";
				for (String string : tarList) {
					corCode+=string+",";
				}
				if (corCode.endsWith(",")) {
					corCode=corCode.substring(0,corCode.length()-1);
				}
				if (corCode.indexOf(",") > 0) {
					filters.add(new PropertyFilter("OREQS_legalCode",corCode));
				} else {
					filters.add(new PropertyFilter("EQS_legalCode",corCode));
				}
			}
			
			Page<RevenueDetailManual> page = revenueDetailManualService.findPageByHQL(pageRequest, filters);
			for (RevenueDetailManual mannul : page.getResult()) {
				NumberUtil.FixedToTwoBit(mannul, new String[]{"currentyRate","monthRevenueAmount","monthRate","monthRevenueRate"});
			}
			model.addAttribute("page", page);
		} catch (Exception e) {
			logger.error("查询營收明細手工處理列表失败:", e);
		}
		return "/bi/revenueDetailManual/list";
	}
	
	@RequestMapping(value = "synchronize")
	@ResponseBody
	@Log(name = "營收明細手工處理-->数据同步")
	public String synchronize(HttpServletRequest request,HttpServletResponse response, AjaxResult result,@Log(name = "年月") String period,@Log(name = "版本") String version) {
		result.put("msg", "数据同步成功");
		try {
			EnumRevenveDetailType.valueOf(version);
			Date date = DateUtil.parseByYyyy_MM(period);
			Assert.notNull(date, "年月格式错误(Error Date Formats)");
			String[] split = period.split("-");
			String year = split[0];
			String month = split[1];
			if (month.length()==1) {
				month="0"+month;
			}
			
			Map<String, String> map = revenueDetailManualService.synchronize(year, month,version);
			result.put("msg", map.get("message"));
		} catch (Exception e) {
			logger.error("数据同步失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}

		return result.getJson();
	}
	
	@RequestMapping(value = "mapping")
	@ResponseBody
	@Log(name = "營收明細手工處理-->执行映射")
	public String mapping(HttpServletRequest request,HttpServletResponse response, AjaxResult result,@Log(name = "年月") String month,@Log(name = "版本") String version,@Log(name = "管報法人編碼") String corporCode) {
		result.put("msg", "执行映射成功(Mapping Success)");
		try {
			EnumRevenveDetailType.valueOf(version);
			String[] split = month.split("-");
			String year = split[0];
			String period = split[1];
			
			Assert.hasText(corporCode, "管報法人編碼不能为空(Entity Name Not Null)");
			String code = SecurityUtils.getCorporationCode();
			String[] codes = code.split(",");
			
			List<String> tarList=new ArrayList<String>();
			for (String string : codes) {
				if (string.startsWith("F_")) {
					tarList.add(string.substring(2, string.length()));
				}
			}
			
			if (corporCode.endsWith(",")) {
				corporCode=corporCode.substring(0, corporCode.length()-1);
			}
			String[] corCodes = corporCode.split(",");
			List<String> list=new ArrayList<String>();
			for (String string : corCodes) {
				if (tarList.contains(string)) {
					list.add(string);
				}
			}
			Assert.isTrue(!list.isEmpty(), "错误的管報法人編碼(Error Entity Name)");
			
			Map<String, String> map = revenueDetailManualService.mapping(list,year, period, version);
			if (!"S".equals(map.get("status"))) {
				result.put("flag", "fail");
				result.put("msg", "映射失败(Mapping Fail):"+map.get("message"));
			}
		} catch (Exception e) {
			logger.error("执行映射失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}
		
		return result.getJson();
	}

	@RequestMapping(value = "upload")
	@ResponseBody
	@Log(name = "營收明細手工處理-->上传")
	public String upload(HttpServletRequest request,HttpServletResponse response, AjaxResult result,
			@Log(name = "年月") String month,@Log(name = "管報法人編碼") String[] Upcode,@Log(name = "版本") String version) {
		result.put("msg", "上传成功(Upload Success)");
		try {
			Assert.isTrue(Upcode!=null && Upcode.length>0, "管報法人編碼不能为空(Entity Name Not Null)");
			EnumRevenveDetailType.valueOf(version);
			
			String code = SecurityUtils.getCorporationCode();
			String[] codes = code.split(",");
			
			List<String> tarList=new ArrayList<String>();
			for (String string : codes) {
				if (string.startsWith("F_")) {
					tarList.add(string.substring(2, string.length()));
				}
			}
			
			Map<String,String> codeList=new HashMap<String,String>();
			for (String string : Upcode) {
				if (tarList.contains(string)) {
					codeList.put(string.toLowerCase(),string);
				}
			}
			
			Assert.isTrue(!codeList.values().isEmpty(), "错误的管報法人編碼(Error Entity Name)");
			
			Date date = DateUtil.parseByYyyy_MM(month);
			Assert.notNull(date, "年月格式错误(Error Date Formats)");
			
			String[] split = month.split("-");
			String year = split[0];
			String period = split[1];
			
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
					result.put("msg", "请您上传正确格式的Excel文件(Error File Formats)");
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
				Assert.notNull(firstRow, "第一行为标题行，不允许为空(First Row Not Empty)");
				int column = firstRow.getPhysicalNumberOfCells();
				
				if(column<COLUMN_NUM){
					result.put("flag", "fail");
					result.put("msg", "Excel列数不能小于"+COLUMN_NUM+"(Number Of Columns Can Not Less Than"+COLUMN_NUM+")");
					return result.getJson();
				}

				int rowNum = sheet.getPhysicalNumberOfRows();
				if (rowNum<2) {
					result.put("flag", "fail");
					result.put("msg", "检测到Excel没有行数据(Row Data Not Empty)");
					return result.getJson();
				}
				
				List<RevenueDetailManual> list=new ArrayList<RevenueDetailManual>();
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
					
					RevenueDetailManual detail=null;
					int n=5;
					String legalCode = ExcelUtil.getCellStringValue(row.getCell(4),i).toLowerCase();
					String tempYear = ExcelUtil.getCellStringValue(row.getCell(1),i);
					String tempPeriod = ExcelUtil.getCellStringValue(row.getCell(3),i);
					
					Date tempDate = DateUtil.parseByYyyy_MM(tempYear+"-"+tempPeriod);
					
					Assert.notNull(tempDate, "第"+(i+1)+"行数据年或期间格式错误(The Date Of The "+(i+1)+"th Row Has Error Format)");
					
					if ((date.compareTo(tempDate)==0) && codeList.containsKey(legalCode)) {
						String corporationCode = codeList.get(legalCode);
						codeMap.put(legalCode,corporationCode);
						
						detail=new RevenueDetailManual();
						
						detail.setSerialNumber(Integer.parseInt(ExcelUtil.getCellStringValue(row.getCell(0),i)));
						detail.setYear(year);
						detail.setQuarter(ExcelUtil.getCellStringValue(row.getCell(2),i));
						detail.setPeriod(period);
						detail.setLegalCode(corporationCode);
						detail.setCorporationCode(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setCustomerCode(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setCustomerName(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setDepartment(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setCategory(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setInvoiceItem(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setInvoiceNo(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setInvoiceDate(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setInvoiceSignDate(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setSaleItem(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setSaleNo(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setSaleDate(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setStoreNo(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setSbu(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setProductNo(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setCustomerProductNo(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setQuantity(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setPrice(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setCurrency(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setRate(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setSourceUntaxAmount(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setCurrencyUntaxAmount(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setCurrentyRate(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setMonthRevenueAmount(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setMonthRate(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setMonthRevenueRate(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setSupplierCode(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setSupplierName(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setProductionUnit(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setCd(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setSaleCategory(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setCustomerInfo(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setSegment(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setLeadingIndustry1(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setLeadingIndustry2(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setLeadingIndustry3(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setLeadingIndustry4(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setLeadingIndustry5(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setSecondaryIndustry(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setIsUnique(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setSimpleSpecification(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setFullSpecification(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setGroupSpecification(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setGrade(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setArea(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setChannel(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setBCG(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setStrategy(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setInvoiceNumber(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setVersion(version);
					}

					if (detail!=null) {
						list.add(detail);
					}
				}
				
				if (!list.isEmpty()) {
					Collection<String> values = codeMap.values();
					String codeCondition="where year='"+year+"' and period='"+period+"' and version='"+version+"' and legal_Code in (";
					for (String string : values) {
						codeCondition+="'"+string+"',";
					}
					
					codeCondition=codeCondition.substring(0, codeCondition.length()-1)+")";
					revenueDetailManualService.saveBatch(list, codeCondition);
				}else{
					result.put("flag", "fail");
					result.put("msg", "无有效数据行(Unreceived Valid Row Data)");
				}
			} else {
				result.put("flag", "fail");
				result.put("msg", "对不起,未接收到上传的文件(Unreceived File)");
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
	@Log(name = "營收明細手工處理-->下载")
	public synchronized String download(HttpServletRequest request,HttpServletResponse response,PageRequest pageRequest,AjaxResult result,
			@Log(name = "年月") String month,@Log(name = "管報法人編碼") String corporCode,@Log(name = "版本") String version){
		try {
			Assert.hasText(corporCode, "管報法人編碼不能为空(Entity Name Not Null)");
			
			EnumRevenveDetailType.valueOf(version);
			
			if (corporCode.endsWith(",")) {
				corporCode=corporCode.substring(0, corporCode.length()-1);
			}
			
			Date date = DateUtil.parseByYyyy_MM(month);
			Assert.notNull(date, "年月格式错误(Error Date Formats)");
			String[] split = month.split("-");
			String year = split[0];
			String period = split[1];
			
			String[] corCodes = corporCode.split(",");
			
			String corporationCode = SecurityUtils.getCorporationCode();
			String[] tarCodes = corporationCode.split(",");
			
			List<String> tarList=new ArrayList<String>();
			for (String string : tarCodes) {
				tarList.add(string.substring(2));
			}
			
			List<String> readList=new ArrayList<String>();
			
			for (String string : corCodes) {
				if (tarList.contains(string)) {
					readList.add(string);
				}
			}
			
			Assert.isTrue(!readList.isEmpty(), "错误的管報法人編碼(Error Entity Name)");
			
			String corCode="";
			for (String string : readList) {
				corCode+=string+",";
			}
			corCode=corCode.substring(0,corCode.length()-1);
			
			List<PropertyFilter> filters = new ArrayList<PropertyFilter>();

			pageRequest.setOrderBy("serialNumber");
			pageRequest.setOrderDir(PageRequest.Sort.ASC);
			
			filters.add(new PropertyFilter("EQS_version",version));
			filters.add(new PropertyFilter("EQS_year",year));
			filters.add(new PropertyFilter("EQS_period",period));
			
			if (corCode.indexOf(",") > 0) {
				filters.add(new PropertyFilter("OREQS_legalCode",corCode));
			} else {
				filters.add(new PropertyFilter("EQS_legalCode",corCode));
			}
			
			List<RevenueDetailManual> list = revenueDetailManualService.listByHQL(filters, pageRequest);
			
			String realPath = request.getRealPath("");
			if (CollectionUtils.isNotEmpty(list)) {
				long time = System.currentTimeMillis();
				File file=new File(realPath+File.separator+"static"+File.separator+"template"+File.separator+"bi"+File.separator+EXCEL_NAME+".xlsx");
				XSSFWorkbook workBook = new XSSFWorkbook(new FileInputStream(file));
				XSSFCellStyle lockStyle = workBook.createCellStyle();
				lockStyle.setLocked(true);
				lockStyle.setAlignment(HorizontalAlignment.CENTER);
				lockStyle.setFillForegroundColor(new XSSFColor(new Color(217, 217, 217)));
				lockStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				XSSFCellStyle unlockStyle = workBook.createCellStyle();
				unlockStyle.setLocked(false);
				unlockStyle.setAlignment(HorizontalAlignment.CENTER);
				
				File outFile=new File(realPath+File.separator+"static"+File.separator+"download"+File.separator+time+".xlsx");
				OutputStream out = new FileOutputStream(outFile);
				SXSSFWorkbook sxssfWorkbook=new SXSSFWorkbook(workBook);
				Sheet sheet = sxssfWorkbook.getSheetAt(0);
				for (int i = 0; i < list.size(); i++) {
					RevenueDetailManual detail = list.get(i);
					Row row = sheet.createRow(i+1);
					ExcelUtil.createCell(lockStyle, COLUMN_NUM-1, unlockStyle, detail, row, 1);
				}
				sheet.protectSheet("FIT");
				sxssfWorkbook.write(out);
				sxssfWorkbook.close();
				out.flush();
				out.close();
				
				result.put("fileName", outFile.getName());
				System.gc();
			}else {
				result.put("flag", "fail");
				result.put("msg", "没有查询到可下载的数据(No data can be downloaded)");
			}
		} catch (Exception e) {
			logger.error("下载Excel失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}
		
		return result.getJson();
	}

}
