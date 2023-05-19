package foxconn.fit.controller.bi;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.service.bi.MappingDataService;
import foxconn.fit.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Controller
@RequestMapping("/bi/poMix")
@SuppressWarnings("unchecked")
public class PoMappingController extends BaseController{
	@Autowired
	private MappingDataService mappingDataService;

	@RequestMapping(value = "index")
	public String index(HttpServletRequest request, Model model) {
		model.addAttribute("supplierList", mappingDataService.index(request,"PO_CATEGORY"));
		return "/bi/poMix/index";
	}

	@RequestMapping(value = "queryPoMapping")
	@ResponseBody
	public String queryMasterData(HttpServletRequest request,AjaxResult result,String masterData){
		try {
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			Assert.hasText(masterData, getLanguage(locale,"採購映射表不能为空","Master data can not be null"));
			result.put("queryList", mappingDataService.queryMasterData(locale,masterData));
		} catch (Exception e) {
			logger.error("查询採購映射表信息失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}

		return result.getJson();
	}


	@RequestMapping(value = "update")
	@ResponseBody
	@Log(name = "更新採購映射表")
	public String update(HttpServletRequest request,AjaxResult result,@Log(name = "採購映射表数据") String masterData,@Log(name = "更新条件") String updateData){
		try {
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			Assert.hasText(masterData, getLanguage(locale,"採購映射表不能为空","Master data can not be null"));
			result.put("msg", getLanguage(locale,"更新成功","Update data success"));
			mappingDataService.updatePo(masterData,updateData);
		} catch (Exception e) {
			logger.error("更新採購映射表信息失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}

		return result.getJson();
	}


	@RequestMapping(value = "/delete")
	@ResponseBody
	@Log(name = "採購映射表-->刪除")
	public String delete(HttpServletRequest request,AjaxResult result,@Log(name = "採購映射表数据") String masterData,@Log(name = "条件") String updateData){
		try {
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			Assert.hasText(masterData, getLanguage(locale,"採購映射表不能为空","Master data can not be null"));
			result.put("msg", getLanguage(locale,"刪除成功","delete data success"));
			mappingDataService.delete(masterData,updateData);
		} catch (Exception e) {
			logger.error("刪除採購映射表信息失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}

		return result.getJson();
	}

	@RequestMapping(value="/list")
	public String list(Model model,HttpServletRequest request,PageRequest pageRequest,String masterData,String queryCondition) {
		try {
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			Assert.hasText(masterData, getLanguage(locale,"採購映射表不能为空","Master data can not be null"));
			String language=getLanguage(locale,"CN","EN");
			mappingDataService.list(model,language,pageRequest,masterData,queryCondition);
		} catch (Exception e) {
			logger.error("查询列表失败:", e);
		}
		return "/bi/poMix/list";
	}

	@RequestMapping(value = "upload")
	@ResponseBody
	@Log(name = "採購映射表-->上传")
	public String upload(HttpServletRequest request,AjaxResult result,@Log(name = "採購映射表") String masterData) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		result.put("msg", getLanguage(locale,"上传成功","Upload success"));
		try {
			Assert.hasText(masterData, getLanguage(locale,"採購映射表不能为空","Master data can not be null"));
			return mappingDataService.upload(result,masterData,request,locale);
		} catch (Exception e) {
			logger.error("保存文件失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}
		return result.getJson();
	}

	@RequestMapping(value = "download")
	@ResponseBody
	public String download(HttpServletRequest request,AjaxResult result,@Log(name = "採購映射表") String masterData,@Log(name = "查询条件") String queryCondition){
		try {
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			Assert.hasText(masterData, getLanguage(locale,"採購映射表不能为空","Master data can not be null"));
			mappingDataService.downloadPo(masterData,queryCondition,request,result);
			System.gc();
		} catch (Exception e) {
			logger.error("下载採購映射表信息失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}
		return result.getJson();
	}

}
