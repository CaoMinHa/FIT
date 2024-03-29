package foxconn.fit.controller.hfm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
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
import foxconn.fit.entity.hfm.CoaMapping;
import foxconn.fit.service.hfm.CoaMappingService;
import foxconn.fit.util.ExcelUtil;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.SecurityUtils;

@Controller
@RequestMapping("/hfm/coaMapping")
public class CoaMappingController extends BaseController {

	private static int COLUMN_NUM=6;
	
	@Autowired
	private CoaMappingService coaMappingService;

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
		List<String> list = coaMappingService.listBySql("select distinct source_corporate_code as code from FIT_COA_MAPPING order by source_corporate_code");
		if (list.size()>0) {
			codeList=new ArrayList<String>();
			for (String code : list) {
				if (tarList.contains(code)) {
					codeList.add(code);
				}
			}
		}
		model.addAttribute("codelist", codeList);
		return "/hfm/coaMapping/index";
	}
	
	@RequestMapping(value="/list")
	public String list(Model model, PageRequest pageRequest,String corporationCode) {
		try {
			List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
			
			List<String> tarList=new ArrayList<String>();
			String corporationCode2 = SecurityUtils.getCorporationCode();
			if (StringUtils.isNotEmpty(corporationCode2)) {
				for (String string : corporationCode2.split(",")) {
					tarList.add(string.substring(2, string.length()));
				}
			}
			
			if (StringUtils.isNotEmpty(corporationCode) && tarList.contains(corporationCode)) {
				filters.add(new PropertyFilter("EQS_corporationCode",corporationCode));
			}else{
				String corCode="";
				for (String string : tarList) {
					corCode+=string+",";
				}
				if (corCode.endsWith(",")) {
					corCode=corCode.substring(0,corCode.length()-1);
				}
				if (corCode.indexOf(",") > 0) {
					filters.add(new PropertyFilter("OREQS_corporationCode",corCode));
				} else {
					filters.add(new PropertyFilter("EQS_corporationCode",corCode));
				}
			}
			
			Page<CoaMapping> page = coaMappingService.findPageByHQL(pageRequest, filters);
			model.addAttribute("page", page);
		} catch (Exception e) {
			logger.error("查询總賬科目映射維護列表失败:", e);
		}
		return "/hfm/coaMapping/list";
	}

	@RequestMapping(value = "upload")
	@ResponseBody
	@Log(name = "總賬科目映射維護-->上传")
	public String upload(HttpServletRequest request,HttpServletResponse response, AjaxResult result,@Log(name = "法人编码") String[] Upcode) {
		result.put("msg", "上传成功(Upload Success)");
		try {
			Assert.isTrue(Upcode!=null && Upcode.length>0, "法人编码不能为空(Entity Code Not Null)");
			
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
			
			Assert.isTrue(!codeList.values().isEmpty(), "错误的法人编码(Error Entity Code)");
			
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
				
				List<CoaMapping> list=new ArrayList<CoaMapping>();
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
					
					CoaMapping detail=null;
					int n=0;
					String lowerCorporationCode = ExcelUtil.getCellStringValue(row.getCell(n++),i).toLowerCase();
					
					if (codeList.containsKey(lowerCorporationCode)) {
						String corporationCode = codeList.get(lowerCorporationCode);
						codeMap.put(lowerCorporationCode,corporationCode);
						
						detail=new CoaMapping();
						
						detail.setCorporationCode(corporationCode);
						detail.setErpItemCode(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setErpItemDesc(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setHfmItemCode(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setHfmItemDesc(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setSymbol(ExcelUtil.getCellStringValue(row.getCell(n++),i));
					}

					if (detail!=null) {
						list.add(detail);
					}
				}
				
				if (!list.isEmpty()) {
					Collection<String> values = codeMap.values();
					String codeCondition="where SOURCE_CORPORATE_CODE in (";
					for (String string : values) {
						codeCondition+="'"+string+"',";
					}
					
					codeCondition=codeCondition.substring(0, codeCondition.length()-1)+")";
					
					coaMappingService.saveBatch(list, codeCondition);
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
	@Log(name = "總賬科目映射維護-->下载")
	public synchronized String download(HttpServletRequest request,HttpServletResponse response,PageRequest pageRequest,AjaxResult result,
			@Log(name = "法人编码") String corporCode,@Log(name = "excel名称") String excelName){
		try {
			if (!"General Ledger Account Mapping".equals(excelName)) {
				excelName="總賬科目映射維護";
			}
			Assert.hasText(corporCode, "法人编码不能为空(Entity Code Not Null)");
			
			if (corporCode.endsWith(",")) {
				corporCode=corporCode.substring(0, corporCode.length()-1);
			}
			
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
			
			Assert.isTrue(!readList.isEmpty(), "错误的法人编码(Error Entity Code)");
			
			String corCode="";
			for (String string : readList) {
				corCode+=string+",";
			}
			corCode=corCode.substring(0,corCode.length()-1);
			
			List<PropertyFilter> filters = new ArrayList<PropertyFilter>();

			pageRequest.setOrderBy("corporationCode");
			pageRequest.setOrderDir(PageRequest.Sort.ASC);
			
			if (corCode.indexOf(",") > 0) {
				filters.add(new PropertyFilter("OREQS_corporationCode",corCode));
			} else {
				filters.add(new PropertyFilter("EQS_corporationCode",corCode));
			}
			
			List<CoaMapping> list = coaMappingService.listByHQL(filters, pageRequest);
			
			String realPath = request.getRealPath("");
			if (CollectionUtils.isNotEmpty(list)) {
				long time = System.currentTimeMillis();
				File file=new File(realPath+File.separator+"static"+File.separator+"template"+File.separator+"hfm"+File.separator+excelName+".xlsx");
				XSSFWorkbook workBook = new XSSFWorkbook(new FileInputStream(file));
				XSSFCellStyle style = workBook.createCellStyle();
				style.setAlignment(HorizontalAlignment.CENTER);
				
				SXSSFWorkbook sxssfWorkbook=new SXSSFWorkbook(workBook);
				Sheet sheet = sxssfWorkbook.getSheetAt(0);
				for (int i = 0; i < list.size(); i++) {
					CoaMapping detail = list.get(i);
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
