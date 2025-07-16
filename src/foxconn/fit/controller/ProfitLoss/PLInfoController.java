package foxconn.fit.controller.ProfitLoss;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;
import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.ProfitLoss.LegalProfitloss;
import foxconn.fit.service.ProfitLoss.LegalProfitlossService;


/**
 * @author cxj
 * 法人損益表
 */
@Controller
@RequestMapping("/bi/plInfo")
public class PLInfoController extends BaseController {

	@Autowired
	private LegalProfitlossService legalProfitlossService;

	@RequestMapping(value = "index")
	public String index(Model model,HttpServletRequest request) {
		//model=legalProfitlossService.index(model);
		model.addAttribute("entityList", legalProfitlossService.entityVal1());
		return "/bi/plInfo/index";
	}

	@RequestMapping(value="/list")
	@Log(name = "法人-->查詢")
	public String list(Model model,PageRequest pageRequest,@Log(name ="法人") String entitys) {
		try {
		   // pageRequest.setPageSize(20);
			String sql=legalProfitlossService.viewList1(entitys);
			//logger.info(sql);
			//Page<Object[]> page = legalProfitlossService.findPageBySql(pageRequest, sql, LegalProfitloss.class);
			Page<Object[]> page = legalProfitlossService.findPageBySql(pageRequest, sql);
			model.addAttribute("page", page);
		} catch (Exception e) {
			logger.error("查询法人失败:", e);
		}
		return "/bi/plInfo/list";
	}	
}