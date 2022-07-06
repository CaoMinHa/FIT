package foxconn.fit.controller.bi;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.base.EnumRevenveDetailType;
import foxconn.fit.service.bi.SBUMappingService;
import foxconn.fit.util.DateUtil;
import foxconn.fit.util.ExceptionUtil;

@Controller
@RequestMapping("/bi/sbuProfitAndLoss")
public class SBUProfitAndLossController extends BaseController{

	@Autowired
	private SBUMappingService sbuMappingService;

	@RequestMapping(value = "index")
	public String index(Model model) {
		return "/bi/sbuProfitAndLoss/index";
	}
	
	@RequestMapping(value = "synchronize")
	@ResponseBody
	@Log(name = "SBU損益表抽取-->数据同步")
	public String synchronize(HttpServletRequest request,HttpServletResponse response, AjaxResult result,@Log(name = "年月") String period,@Log(name = "版本") String version) {
		result.put("msg", "数据同步成功");
		try {
			Date date = DateUtil.parseByYyyy_MM(period);
			Assert.notNull(date, "年月格式错误(Error Date Formats)");
			String[] split = period.split("-");
			String year = split[0];
			String month = split[1];
			if (month.length()==1) {
				month="0"+month;
			}
			
			Map<String, String> map = sbuMappingService.synchronize(year, month,version);
			result.put("msg", map.get("message"));
		} catch (Exception e) {
			logger.error("数据同步失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}

		return result.getJson();
	}
	
}
