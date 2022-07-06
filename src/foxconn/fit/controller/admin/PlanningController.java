package foxconn.fit.controller.admin;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.PageRequest;

import com.csvreader.CsvWriter;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.dao.base.PropertyFilter;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.base.EnumBudgetVersion;
import foxconn.fit.entity.base.EnumDimensionType;
import foxconn.fit.entity.base.EnumScenarios;
import foxconn.fit.entity.base.Planning;
import foxconn.fit.service.base.PlanningService;
import foxconn.fit.service.base.UserService;
import foxconn.fit.util.DateUtil;
import foxconn.fit.util.ExceptionUtil;

@Controller
@RequestMapping("/admin/planning")
public class PlanningController extends BaseController {

	@Autowired
	private PlanningService planningService;
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(value = "index")
	public String index(Model model) {
		List<String> yearsList = userService.listBySql("select distinct dimension from FIT_DIMENSION where type='"+EnumDimensionType.Years.getCode()+"' order by dimension");
		List<String> sbuList = userService.listBySql("select distinct parent from fit_dimension where type='"+EnumDimensionType.Entity+"' order by parent");
		model.addAttribute("yearsList", yearsList);
		model.addAttribute("sbuList", sbuList);
		return "/admin/planning/index";
	}
	
	@RequestMapping(value="/copy")
	@ResponseBody
	@Log(name = "复制版本数据")
	public String copy(HttpServletRequest request,AjaxResult ajaxResult,Model model,@Log(name = "SBU") String entitys,@Log(name = "年") String year,@Log(name = "场景") String scenarios,@Log(name = "版本") String version){
		Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		ajaxResult.put("msg", getLanguage(locale, "复制成功", "Copy Success"));
		String[] years=year.split(",");
		for (int i=0;i<years.length;i++) {
			year=years[i];
			try {
				Assert.hasText(version, getLanguage(locale, "版本不能为空", "Version can not be null"));
				EnumBudgetVersion.valueOf(version);
				EnumScenarios.valueOf(scenarios);
				Assert.hasText(year, getLanguage(locale, "年不能为空", "Year can not be null"));
				String yr = "20" + year.substring(2);
				Assert.hasText(entitys, getLanguage(locale, "SBU不能为空", "SBU can not be null"));
				Date date = DateUtil.parseByYyyy(yr);
				Assert.notNull(date, getLanguage(locale, "年格式错误", "Error Year Formats"));
				if (entitys.endsWith(",")) {
					entitys = entitys.substring(0, entitys.length() - 1);
				}
				planningService.copy(entitys, yr, scenarios, version);
			} catch (Exception e) {
				logger.error(year+"年复制失败:", e);
				ajaxResult.put("flag", "fail");
				ajaxResult.put("msg", getLanguage(locale, year+"年复制失败", year+"Year Copy Fail") + " : " + ExceptionUtil.getRootCauseMessage(e));
			}
		}
		return ajaxResult.getJson();
	}

	@RequestMapping(value = "download")
	@ResponseBody
	@Log(name = "下载Planning")
	public synchronized String download(HttpServletRequest request,HttpServletResponse response,PageRequest pageRequest,AjaxResult result,
			@Log(name = "SBU") String sbu,@Log(name = "场景") String scenarios,@Log(name = "年") String year,@Log(name = "期间") Integer period){
		try {
			if (EnumScenarios.Forecast.getCode().equals(scenarios) || EnumScenarios.Actual.getCode().equals(scenarios)) {
				Assert.isTrue(period!=null && period>0, "期间不能为空");
			}
			String[] years=year.split(",");
			List<Planning> list =new ArrayList<>();
			for (int i=0;i<years.length;i++){
				String yr="20"+years[i].substring(2);
				Date date = DateUtil.parseByYyyy(yr);
				Assert.notNull(date, "年格式错误(Error Year Formats)");
				if (sbu.endsWith(",")) {
					sbu=sbu.substring(0,sbu.length()-1);
				}
				String entity="";
				for (String s : sbu.split(",")) {
					entity+=s+"|";
				}
				entity=entity.substring(0,entity.length()-1);

				String message = planningService.generatePlanning(entity,yr,scenarios);
				if (StringUtils.isNotEmpty(message)) {
					throw new RuntimeException("计算Planning数据出错 : "+message);
				}

				List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
				filters.add(new PropertyFilter("LLIKES_POINT_OF_VIEW", years[i]));
				pageRequest.setOrderBy("ACCOUNT");
				pageRequest.setOrderDir(PageRequest.Sort.ASC);

				List<Planning> lists = planningService.listByHQL(filters, pageRequest);
				list.addAll(lists);
			};

			String realPath = request.getRealPath("");
			if (CollectionUtils.isNotEmpty(list)) {
				long time = System.currentTimeMillis();
				String filePath=realPath+File.separator+"static"+File.separator+"download"+File.separator+time+".csv";
				CsvWriter writer=new CsvWriter(filePath, ',', Charset.forName("UTF8"));
				String[] periodFields=new String[]{"JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"};
				if (EnumScenarios.Forecast.getCode().equals(scenarios)) {
					writer.writeRecord(new String[]{"Account","Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec","YT","Point-of-View","Data Load Cube Name"});
					for (Planning planning : list) {
						for (int i = 0; i < period; i++) {
							Field field = Planning.class.getDeclaredField(periodFields[i]);
							field.setAccessible(true);
							field.set(planning, "");
						}
						writer.writeRecord(new String[]{planning.getACCOUNT(),planning.getJAN(),planning.getFEB(),planning.getMAR(),planning.getAPR(),planning.getMAY(),planning.getJUN(),planning.getJUL(),
								planning.getAUG(),planning.getSEP(),planning.getOCT(),planning.getNOV(),planning.getDEC(),planning.getYT(),planning.getPOINT_OF_VIEW(),planning.getDATA_LOAD_CUBE_NAME()});
					}
				}else if(EnumScenarios.Actual.getCode().equals(scenarios)){
					String month = periodFields[period-1];
					writer.writeRecord(new String[]{"Account",month,"YT","Point-of-View","Data Load Cube Name"});
					for (Planning planning : list) {
						Field field = Planning.class.getDeclaredField(periodFields[period-1]);
						field.setAccessible(true);
						String value = (String) field.get(planning);
						writer.writeRecord(new String[]{planning.getACCOUNT(),value,planning.getYT(),planning.getPOINT_OF_VIEW(),planning.getDATA_LOAD_CUBE_NAME()});
					}
				}else{
					writer.writeRecord(new String[]{"Account","Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec","YT","Point-of-View","Data Load Cube Name"});
					for (Planning planning : list) {
						for (int i = 0; i < periodFields.length; i++) {
							Field field = Planning.class.getDeclaredField(periodFields[i]);
							field.setAccessible(true);
							String value = (String) field.get(planning);
							if ("0".equals(value)) {
								field.set(planning, "");
							}
						}
						writer.writeRecord(new String[]{planning.getACCOUNT(),planning.getJAN(),planning.getFEB(),planning.getMAR(),planning.getAPR(),planning.getMAY(),planning.getJUN(),planning.getJUL(),
								planning.getAUG(),planning.getSEP(),planning.getOCT(),planning.getNOV(),planning.getDEC(),planning.getYT(),planning.getPOINT_OF_VIEW(),planning.getDATA_LOAD_CUBE_NAME()});
					}
				}
				
				writer.flush();
				writer.close();
				result.put("fileName", time+".csv");
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
