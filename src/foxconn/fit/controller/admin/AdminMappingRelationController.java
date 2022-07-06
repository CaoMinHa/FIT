package foxconn.fit.controller.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.dao.base.PropertyFilter;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.base.EnumDimensionName;
import foxconn.fit.entity.ebs.MappingRelation;
import foxconn.fit.service.ebs.MappingRelationService;
import foxconn.fit.util.ExcelUtil;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.SecurityUtils;

@Controller
@RequestMapping("/admin/mappingRelation")
@SuppressWarnings("unchecked")
public class AdminMappingRelationController extends BaseController{
	private static String EXCEL_NAME="映射關係表";
	private static int COLUMN_NUM=10;
	
	@Value("${account_valid_table}")
	private String ACCOUNT_VALID_TABLE;
	
	@Value("${entity_icp_valid_table}")
	private String ENTITY_ICP_VALID_TABLE;
	
	@Autowired
	private MappingRelationService mappingRelationService;

	@RequestMapping(value = "index")
	public String index(HttpServletRequest request, Model model) {
		List<String> targetList=new ArrayList<String>();
		String entity1 = SecurityUtils.getEntity();
		if (StringUtils.isNotEmpty(entity1)) {
			for (String string : entity1.split(",")) {
				targetList.add(string.substring(2));
			}
		}
		List<String> entityList =new ArrayList<String>();
		List<String> list = mappingRelationService.listBySql("select distinct ENTITY from CUX_DATAMAP order by ENTITY");
		if (list.size()>0) {
			for (String code : list) {
				if (targetList.contains(code)) {
					entityList.add(code);
				}
			}
		}
		if (!entityList.isEmpty()) {
			String entitys="";
			for (String string : entityList) {
				entitys=entitys+"'"+string+"'"+",";
			}
			entitys=entitys.substring(0, entitys.length()-1);
			model.addAttribute("SRCKEYList", mappingRelationService.listBySql("select distinct SRCKEY from CUX_DATAMAP where entity in ("+entitys+")"));
			model.addAttribute("TARGKEYList", mappingRelationService.listBySql("select distinct TARGKEY from CUX_DATAMAP where entity in ("+entitys+")"));
		}
		model.addAttribute("entityList", entityList);
		return "/admin/mappingRelation/index";
	}
	
	@RequestMapping(value="/list")
	public String list(Model model, PageRequest pageRequest,String entity,String dimensionName,String SRCKEY,String TARGKEY) {
		try {
			pageRequest.setPageSize(20);
			List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
			
			List<String> entityList=new ArrayList<String>();
			String entity1 = SecurityUtils.getEntity();
			if (StringUtils.isNotEmpty(entity1)) {
				for (String string : entity1.split(",")) {
					entityList.add(string.substring(2));
				}
			}
			
			if (entityList.contains(entity)) {
				filters.add(new PropertyFilter("EQS_ENTITY",entity));
			}else{
				if (entityList.size()==1) {
					filters.add(new PropertyFilter("EQS_ENTITY",entityList.get(0)));
				}else{
					String entityStr="";
					for (String en : entityList) {
						entityStr=entityStr+en+",";
					}
					entityStr=entityStr.substring(0, entityStr.length()-1);
					
					filters.add(new PropertyFilter("OREQS_ENTITY",entityStr));
				}
			}
			if (StringUtils.isNotEmpty(dimensionName)) {
				filters.add(new PropertyFilter("EQS_DIMNAME",dimensionName));
			}
			if (StringUtils.isNotEmpty(SRCKEY)) {
				filters.add(new PropertyFilter("LIKES_SRCKEY",SRCKEY));
			}
			if (StringUtils.isNotEmpty(TARGKEY)) {
				filters.add(new PropertyFilter("LIKES_TARGKEY",TARGKEY));
			}
			
			Page<MappingRelation> page = mappingRelationService.findPageByHQL(pageRequest, filters);
			model.addAttribute("page", page);
		} catch (Exception e) {
			logger.error("查询映射关系表列表失败:", e);
		}
		return "/admin/mappingRelation/list";
	}
	
	@RequestMapping(value = "upload")
	@ResponseBody
	@Log(name = "映射关系表-->上传")
	public String upload(HttpServletRequest request,AjaxResult result,@Log(name = "公司编码") String[] entitys,String dimensionName) {
		result.put("msg", "上传成功(Upload Success)");
		try {
			Assert.isTrue(entitys!=null && entitys.length>0, "公司编码不能为空");
			Assert.hasText(dimensionName, "映射类别不能为空");
			
			List<String> tarList=new ArrayList<String>();
			String entity1 = SecurityUtils.getEntity();
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
			Assert.isTrue(!codeList.values().isEmpty(), "错误的公司编码");
			
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
				
				List<MappingRelation> list=new ArrayList<MappingRelation>();
				Map<String, String> codeMap=new HashMap<String, String>();
				Map<String, String> accountMap=new HashMap<String, String>();
				Map<String, String> entityMap=new HashMap<String, String>();
				Map<String, String> icpMap=new HashMap<String, String>();
				
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
					String srcEntity = ExcelUtil.getCellStringValue(row.getCell(n++),i);
					String lowerEntity = srcEntity.toLowerCase();
					String DIMNAME = ExcelUtil.getCellStringValue(row.getCell(n++),i);
					String SRCKEY = ExcelUtil.getCellStringValue(row.getCell(n++),i);
					String SRCDESC = ExcelUtil.getCellStringValue(row.getCell(n++),i);
					String TARGKEY = ExcelUtil.getCellStringValue(row.getCell(n++),i);
					Assert.isTrue(codeList.containsKey(lowerEntity), "第"+(i+1)+"行数据公司编码【"+srcEntity+"】不在用户配置的公司编码中");
					Assert.hasText(DIMNAME, "第"+(i+1)+"行数据映射类别不能为空");
					Assert.isTrue(dimensionName.equals(DIMNAME), "第"+(i+1)+"行数据映射类别【"+DIMNAME+"】与页面选中的映射类别不一致");
					Assert.hasText(SRCKEY, "第"+(i+1)+"行数据【映射源值】不能为空");
					Assert.hasText(SRCKEY, "第"+(i+1)+"行数据【映射目标值】不能为空");
					if (!"IGNORE".equalsIgnoreCase(TARGKEY)) {
						if (EnumDimensionName.ACCOUNT.getCode().equals(DIMNAME)) {
							accountMap.put(TARGKEY,TARGKEY);
						}else if (EnumDimensionName.ENTITY.getCode().equals(DIMNAME)) {
							entityMap.put(TARGKEY, TARGKEY);
						}else if(EnumDimensionName.ICP.getCode().equals(DIMNAME)){
							icpMap.put(TARGKEY, TARGKEY);
						}
					}
					
					String entity = codeList.get(lowerEntity);
					codeMap.put(lowerEntity,entity);
					
					MappingRelation detail=new MappingRelation();
					detail.setENTITY(entity);
					detail.setDIMNAME(DIMNAME);
					detail.setSRCKEY(SRCKEY);
					detail.setSRCDESC(SRCDESC);
					detail.setTARGKEY(TARGKEY);
					detail.setTARGDESC(ExcelUtil.getCellStringValue(row.getCell(n++),i));
					detail.setCCT_ACCOUNT(ExcelUtil.getCellStringValue(row.getCell(n++),i));
					detail.setCCT_ACCOUNT_ATT(ExcelUtil.getCellStringValue(row.getCell(n++),i));
					detail.setCHANGESIGN("是".equals(ExcelUtil.getCellStringValue(row.getCell(n++),i))?true:false);
					detail.setCATEGORY(ExcelUtil.getCellStringValue(row.getCell(n++),i));
					detail.setCREATION_DATE(new Date());
					detail.setCREATED_BY(SecurityUtils.getLoginUsername());

					list.add(detail);
				}
				
				List<String> unMappedTargetKeyList=new ArrayList<String>();
				if (!accountMap.isEmpty()) {
					List<String> accountList = mappingRelationService.listBySql(ACCOUNT_VALID_TABLE);
					unMappedTargetKeyList.clear();
					for (String targkey : accountMap.values()) {
						if (!accountList.contains(targkey)) {
							unMappedTargetKeyList.add(targkey);
						}
					}
					
					if (!unMappedTargetKeyList.isEmpty()) {
						result.put("flag", "fail");
						result.put("msg", "以下【HFM映射目标值】在HFM系统ACCOUNT维度中不存在---------> "+Arrays.toString(unMappedTargetKeyList.toArray()));
						return result.getJson();
					}
				}
				
				if (!entityMap.isEmpty() || !icpMap.isEmpty()) {
					List<String> icpList = mappingRelationService.listBySql(ENTITY_ICP_VALID_TABLE);
					unMappedTargetKeyList.clear();
					for (String targkey : entityMap.values()) {
						if (!icpList.contains(targkey)) {
							unMappedTargetKeyList.add(targkey);
						}
					}
					
					if (!unMappedTargetKeyList.isEmpty()) {
						result.put("flag", "fail");
						result.put("msg", "以下【HFM映射目标值】在HFM系统ENTITY维度中不存在---------> "+Arrays.toString(unMappedTargetKeyList.toArray()));
						return result.getJson();
					}
					
					unMappedTargetKeyList.clear();
					for (String targkey : icpMap.values()) {
						if (!icpList.contains(targkey)) {
							unMappedTargetKeyList.add(targkey);
						}
					}
					
					if (!unMappedTargetKeyList.isEmpty()) {
						result.put("flag", "fail");
						result.put("msg", "以下【HFM映射目标值】在HFM系统ICP维度中不存在---------> "+Arrays.toString(unMappedTargetKeyList.toArray()));
						return result.getJson();
					}
				}
				
				if (!list.isEmpty()) {
					Collection<String> values = codeMap.values();
					String codeCondition="WHERE DIMNAME='"+dimensionName+"' AND ENTITY in (";
					for (String string : values) {
						codeCondition+="'"+string+"',";
					}
					codeCondition=codeCondition.substring(0, codeCondition.length()-1)+")";
					mappingRelationService.saveBatch(list, codeCondition);
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
	@Log(name = "映射关系表-->下载")
	public synchronized String download(HttpServletRequest request,HttpServletResponse response,PageRequest pageRequest,AjaxResult result,
			@Log(name = "公司编码") String entity,String dimensionName){
		try {
			Assert.hasText(entity, "公司编码不能为空");
			Assert.hasText(dimensionName, "映射类别不能为空");
			
			if (entity.endsWith(",")) {
				entity=entity.substring(0, entity.length()-1);
			}
			
			String[] entitys = entity.split(",");
			List<String> targetList=new ArrayList<String>();
			String entity1 = SecurityUtils.getEntity();
			if (StringUtils.isNotEmpty(entity1)) {
				for (String string : entity1.split(",")) {
					targetList.add(string.substring(2));
				}
			}
			List<String> entityList=new ArrayList<String>();
			
			for (String string : entitys) {
				if (targetList.contains(string)) {
					entityList.add(string);
				}
			}
			
			Assert.isTrue(!entityList.isEmpty(), "错误的公司编码");
			
			String queryEntity="";
			for (String string : entityList) {
				queryEntity+=string+",";
			}
			queryEntity=queryEntity.substring(0,queryEntity.length()-1);
			
			List<PropertyFilter> filters = new ArrayList<PropertyFilter>();

			pageRequest.setOrderBy("ENTITY");
			pageRequest.setOrderDir(PageRequest.Sort.ASC);
			
			if (queryEntity.indexOf(",") > 0) {
				filters.add(new PropertyFilter("OREQS_ENTITY",queryEntity));
			} else {
				filters.add(new PropertyFilter("EQS_ENTITY",queryEntity));
			}
			filters.add(new PropertyFilter("EQS_DIMNAME",dimensionName));
			
			List<MappingRelation> list = mappingRelationService.listByHQL(filters, pageRequest);
			
			String realPath = request.getRealPath("");
			if (CollectionUtils.isNotEmpty(list)) {
				File file=new File(realPath+File.separator+"static"+File.separator+"template"+File.separator+"ebs"+File.separator+EXCEL_NAME+"_download.xlsx");
				XSSFWorkbook workBook = new XSSFWorkbook(new FileInputStream(file));
				XSSFCellStyle style = workBook.createCellStyle();
				style.setAlignment(HorizontalAlignment.CENTER);
				
				SXSSFWorkbook sxssfWorkbook=new SXSSFWorkbook(workBook);
				Sheet sheet = sxssfWorkbook.getSheetAt(0);
				for (int i = 0; i < list.size(); i++) {
					MappingRelation detail = list.get(i);
					Row row = sheet.createRow(i+1);
					ExcelUtil.createCellParseValueType(style, detail, row);
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
