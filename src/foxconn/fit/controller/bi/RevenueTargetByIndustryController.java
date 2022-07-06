package foxconn.fit.controller.bi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
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
import foxconn.fit.entity.base.EnumVersion;
import foxconn.fit.entity.bi.RevenueTargetByIndustry;
import foxconn.fit.service.bi.RevenueTargetByIndustryService;
import foxconn.fit.util.DateUtil;
import foxconn.fit.util.ExcelUtil;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.NumberUtil;

@Controller
@RequestMapping("/bi/revenueTarget")
public class RevenueTargetByIndustryController extends BaseController {

	private static String EXCEL_NAME="營收目標by產業";
	private static int COLUMN_NUM=18;
	
	@Autowired
	private RevenueTargetByIndustryService revenueTargetService;

	@RequestMapping(value = "index")
	public String index(Model model) {
		List<String> list = revenueTargetService.listBySql("select distinct version from FIT_Revenue_Target_Industry order by version");
		model.addAttribute("codelist", list);
		return "/bi/revenueTarget/index";
	}
	
	@RequestMapping(value="/list")
	public String list(Model model, PageRequest pageRequest,String year,String version) {
		try {
			List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
			
			if (StringUtils.isNotEmpty(year)) {
				filters.add(new PropertyFilter("EQS_year",year));
			}
			
			if (StringUtils.isNotEmpty(version)) {
				filters.add(new PropertyFilter("EQS_version",EnumVersion.valueOf(version).getValue()));
			}
			
			Page<RevenueTargetByIndustry> page = revenueTargetService.findPageByHQL(pageRequest, filters);
			for (RevenueTargetByIndustry target : page.getResult()) {
				NumberUtil.FixedToTwoBit(target, new String[]{"month1","month2","month3","month4","month5","month6","month7","month8","month9","month10","month11","month12"});
			}
			model.addAttribute("page", page);
		} catch (Exception e) {
			logger.error("查询營收預估by產業列表失败:", e);
		}
		return "/bi/revenueTarget/list";
	}

	@RequestMapping(value = "upload")
	@ResponseBody
	@Log(name = "營收目標by產業-->上传")
	public String upload(HttpServletRequest request,HttpServletResponse response, AjaxResult result,
			@Log(name = "年") String year,@Log(name = "版本") String version) {
		result.put("msg", "上传成功(Upload Success)");
		try {
			Assert.isTrue(StringUtils.isNotEmpty(year) && year.length()==4, "年格式错误(Error Year Formats)");
			Date date = DateUtil.parseByYyyy(year);
			Assert.notNull(date, "年格式错误(Error Year Formats)");
			
			version=EnumVersion.valueOf(version).getValue();
			
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
				
				List<RevenueTargetByIndustry> list=new ArrayList<RevenueTargetByIndustry>();
				for (int sheetIndex = 0; sheetIndex < 2; sheetIndex++) {
					Sheet sheet = wb.getSheetAt(sheetIndex);
					Row firstRow = sheet.getRow(2);
					Assert.notNull(firstRow, "第三行为标题行，不允许为空(First Row Not Empty)");
					int column = firstRow.getPhysicalNumberOfCells();
					
					if(column<COLUMN_NUM){
						result.put("flag", "fail");
						result.put("msg", "Excel列数不能小于"+COLUMN_NUM+"(Number Of Columns Can Not Less Than"+COLUMN_NUM+")");
						return result.getJson();
					}
					
					int rowNum = sheet.getPhysicalNumberOfRows();
					if (rowNum<4) {
						result.put("flag", "fail");
						result.put("msg", "检测到Excel没有行数据(Row Data Not Empty)");
						return result.getJson();
					}
					
					for (int i = 3; i < rowNum; i++) {
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
						RevenueTargetByIndustry detail = null;
						String year1 = ExcelUtil.getCellStringValue(row.getCell(n++),i);
						String version1 = ExcelUtil.getCellStringValue(row.getCell(n++),i);
						String organization = ExcelUtil.getCellStringValue(row.getCell(n++),i);
						String system = ExcelUtil.getCellStringValue(row.getCell(n++),i);
						String scene = ExcelUtil.getCellStringValue(row.getCell(n++),i);
						String industry = ExcelUtil.getCellStringValue(row.getCell(n++),i);
						if (StringUtils.isEmpty(year1) || StringUtils.isEmpty(version1) || StringUtils.isEmpty(industry)) {
							continue;
						}
						
						Assert.isTrue(StringUtils.isNotEmpty(year1) && year1.length()==4, "第"+(i+1)+"行数据年格式错误(The Year Of The "+(i+1)+"th Row Has Error Format)");
						Date tempDate = DateUtil.parseByYyyy(year1);
						
						Assert.notNull(tempDate, "第"+(i+1)+"行数据年格式错误(The Date Of The "+(i+1)+"th Row Has Error Format)");
						
						if (date.compareTo(tempDate)==0 && version.equals(version1)) {
							detail=new RevenueTargetByIndustry();
							
							detail.setYear(year);
							detail.setVersion(version1);
							detail.setOrganization(organization);
							detail.setSystem(system);
							detail.setScene(scene);
							detail.setIndustry(industry);
							detail.setMonth1(ExcelUtil.getCellStringValue(row.getCell(n++),i));
							detail.setMonth2(ExcelUtil.getCellStringValue(row.getCell(n++),i));
							detail.setMonth3(ExcelUtil.getCellStringValue(row.getCell(n++),i));
							detail.setMonth4(ExcelUtil.getCellStringValue(row.getCell(n++),i));
							detail.setMonth5(ExcelUtil.getCellStringValue(row.getCell(n++),i));
							detail.setMonth6(ExcelUtil.getCellStringValue(row.getCell(n++),i));
							detail.setMonth7(ExcelUtil.getCellStringValue(row.getCell(n++),i));
							detail.setMonth8(ExcelUtil.getCellStringValue(row.getCell(n++),i));
							detail.setMonth9(ExcelUtil.getCellStringValue(row.getCell(n++),i));
							detail.setMonth10(ExcelUtil.getCellStringValue(row.getCell(n++),i));
							detail.setMonth11(ExcelUtil.getCellStringValue(row.getCell(n++),i));
							detail.setMonth12(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						}
						
						if (detail!=null) {
							list.add(detail);
						}
					}
				}
				
				if (!list.isEmpty()) {
					String condition="where year='"+year+"' and version='"+version+"'";
					revenueTargetService.saveBatch(list,condition);
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
	@Log(name = "營收目標by產業-->下载")
	public synchronized String download(HttpServletRequest request,HttpServletResponse response,PageRequest pageRequest,AjaxResult result,
			@Log(name = "年") String year,@Log(name = "版本") String version){
		try {
			Assert.isTrue(StringUtils.isNotEmpty(year) && year.length()==4, "年格式错误(Error Year Formats)");
			Date date = DateUtil.parseByYyyy(year);
			Assert.notNull(date, "年格式错误(Error Year Formats)");
			version=EnumVersion.valueOf(version).getValue();
			
			List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
			pageRequest.setOrderBy("year");
			pageRequest.setOrderDir(PageRequest.Sort.ASC);
			filters.add(new PropertyFilter("EQS_year",year));
			filters.add(new PropertyFilter("EQS_version",version));
			List<RevenueTargetByIndustry> list = revenueTargetService.listByHQL(filters, pageRequest);
			
			String realPath = request.getRealPath("");
			if (CollectionUtils.isNotEmpty(list)) {
				long time = System.currentTimeMillis();
				File file=new File(realPath+File.separator+"static"+File.separator+"template"+File.separator+"bi"+File.separator+EXCEL_NAME+".xlsx");
				XSSFWorkbook workBook = new XSSFWorkbook(new FileInputStream(file));
				XSSFCellStyle style = workBook.createCellStyle();
				style.setAlignment(HorizontalAlignment.CENTER);
				
				SXSSFWorkbook sxssfWorkbook=new SXSSFWorkbook(workBook);
				Sheet sheet = sxssfWorkbook.getSheetAt(0);
				for (int i = 0; i < list.size(); i++) {
					RevenueTargetByIndustry detail = list.get(i);
					Row row = sheet.createRow(i+1);
					ExcelUtil.createCell(style, detail, row);
				}
				
				File outFile=new File(realPath+File.separator+"static"+File.separator+"download"+File.separator+time+".xlsx");
				OutputStream out = new FileOutputStream(outFile);
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
