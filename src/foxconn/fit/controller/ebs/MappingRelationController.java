package foxconn.fit.controller.ebs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
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
import foxconn.fit.dao.base.PropertyFilter;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.ebs.MappingRelation;
import foxconn.fit.service.ebs.MappingRelationService;
import foxconn.fit.util.ExcelUtil;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.SecurityUtils;

@Controller
@RequestMapping("/hfm/mappingRelation")
@SuppressWarnings("unchecked")
public class MappingRelationController extends BaseController{
	private static String EXCEL_NAME="映射關係表";
	
	@Autowired
	private MappingRelationService mappingRelationService;

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
		List<String> entityList =new ArrayList<String>();
		List<String> list = mappingRelationService.listBySql("select distinct ENTITY from CUX_DATAMAP order by ENTITY");
		if (list.size()>0) {
			for (String code : list) {
				if ("EBS".equals(code) && targetList.contains(code)) {
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
		return "/ebs/mappingRelation/index";
	}
	
	@RequestMapping(value="/list")
	public String list(Model model,HttpServletRequest request,PageRequest pageRequest,String entity,String dimensionName,String SRCKEY,String TARGKEY) {
		try {
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			model.addAttribute("locale", locale.toString());
			List<String> entityList=new ArrayList<String>();
			String entity1 = SecurityUtils.getEBS();
			if (StringUtils.isNotEmpty(entity1)) {
				for (String string : entity1.split(",")) {
					entityList.add(string.substring(2));
				}
			}
			
			Page<MappingRelation> page=new Page(pageRequest);;
			if (entityList.contains("EBS")) {
				pageRequest.setPageSize(20);
				List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
				if (StringUtils.isNotEmpty(dimensionName)) {
					filters.add(new PropertyFilter("EQS_DIMNAME",dimensionName));
				}
				if (StringUtils.isNotEmpty(SRCKEY)) {
					filters.add(new PropertyFilter("LIKES_SRCKEY",SRCKEY));
				}
				if (StringUtils.isNotEmpty(TARGKEY)) {
					filters.add(new PropertyFilter("LIKES_TARGKEY",TARGKEY));
				}
				
				page = mappingRelationService.findPageByHQL(pageRequest, filters);
			}
			model.addAttribute("page", page);
		} catch (Exception e) {
			logger.error("查询映射关系表列表失败:", e);
		}
		return "/ebs/mappingRelation/list";
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
			String entity1 = SecurityUtils.getEBS();
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
