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
@RequestMapping("/bi/rtMix")

public class RtMappingController extends BaseController{
	@Autowired
	private MappingDataService mappingDataService;

	/**頁面初始加載**/
	@RequestMapping(value = "index")
	public String index(HttpServletRequest request, Model model) {
		model.addAttribute("supplierList", mappingDataService.index(request));
		return "/bi/rtMix/index";
	}

	/**
	 * 獲取選中映射表的高級查詢字段
	 * @param request
	 * @param result
	 * @param masterData
	 * @return
	 */
	@RequestMapping(value = "queryRtMapping")
	@ResponseBody
	public String queryMasterData(HttpServletRequest request,AjaxResult result,String masterData){
		try {
			result.put("queryList", mappingDataService.queryMasterData(request,masterData));
		} catch (Exception e) {
			logger.error("查询營收映射表信息失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}
		return result.getJson();
	}

	@RequestMapping(value="/list")
	public String list(Model model,HttpServletRequest request,PageRequest pageRequest,String masterData,String queryCondition) {
		try {
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			Assert.hasText(masterData, getLanguage(locale,"營收映射表不能为空","Master data can not be null"));
			String language=getLanguage(locale,"CN","EN");
			mappingDataService.list(model,language,pageRequest,masterData,queryCondition);
		} catch (Exception e) {
			logger.error("查询列表失败:", e);
		}
		return "/bi/rtMix/list";
	}


	@RequestMapping(value = "update")
	@ResponseBody
	@Log(name = "營收映射表單個修改")
	public String update(HttpServletRequest request,AjaxResult result,@Log(name = "營收映射表数据") String masterData,@Log(name = "更新条件") String updateData){
		try {
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			Assert.hasText(masterData, getLanguage(locale,"營收映射表不能为空","Master data can not be null"));
			result.put("msg", getLanguage(locale,"更新成功","Update data success"));
			mappingDataService.update(masterData,updateData);
		} catch (Exception e) {
			logger.error("更新營收映射表信息失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}
		return result.getJson();
	}

	@RequestMapping(value = "upload")
	@ResponseBody
	@Log(name = "營收映射表-->上传")
	public String upload(HttpServletRequest request,AjaxResult result,@Log(name = "營收映射表") String masterData) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		result.put("msg", getLanguage(locale,"上传成功","Upload success"));
		try {
			Assert.hasText(masterData, getLanguage(locale,"營收映射表不能爲空","Master data can not be null"));
			return mappingDataService.upload(result,masterData,request,locale);
		} catch (Exception e) {
			logger.error("保存文件失敗", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}

		return result.getJson();
	}

	@RequestMapping(value = "download")
	@ResponseBody
	@Log(name = "營收映射表-->下载")
	public String download(HttpServletRequest request,AjaxResult result,@Log(name = "營收映射表") String masterData,@Log(name = "查询条件") String queryCondition){
		try {
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			Assert.hasText(masterData, getLanguage(locale,"營收映射表不能為空","Master data can not be null"));
			mappingDataService.download(masterData,queryCondition,request,result);
			System.gc();
		} catch (Exception e) {
			logger.error("下载營收映射表信息失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}
		return result.getJson();
	}

	@RequestMapping(value = "/delete")
	@ResponseBody
	@Log(name = "營收映射表-->刪除")
	public String delete(HttpServletRequest request,AjaxResult result,@Log(name = "營收映射表数据") String masterData,@Log(name = "刪除条件") String updateData){
		try {
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			Assert.hasText(masterData, getLanguage(locale,"營收映射表不能为空","Master data can not be null"));
			result.put("msg", getLanguage(locale,"刪除成功","delete data success"));
			mappingDataService.delete(masterData,updateData);
		} catch (Exception e) {
			logger.error("刪除營收映射表信息失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}
		return result.getJson();
	}

	@RequestMapping(value = "/insertData")
	@ResponseBody
	@Log(name = "營收映射表-->單條新增")
	public String insert(AjaxResult result,@Log(name = "營收映射表数据單個新增") String formVal,String type){
		try {
			mappingDataService.insert(formVal,type);
		} catch (Exception e) {
			logger.error("單個新增營收映射表信息失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}
		return result.getJson();
	}

}
