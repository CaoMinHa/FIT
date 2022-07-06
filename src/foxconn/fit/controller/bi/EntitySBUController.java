package foxconn.fit.controller.bi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import foxconn.fit.controller.BaseController;
import foxconn.fit.service.bi.RegionService;

@Controller
@RequestMapping("/bi/entitySBU")
public class EntitySBUController extends BaseController {

	@Autowired
	private RegionService regionService;

	@RequestMapping(value = "index")
	public String index(Model model) {
		return "/bi/entitySBU/index";
	}
	
	@RequestMapping(value="/list")
	public String list(Model model, PageRequest pageRequest) {
		try {
			Page<Object[]> page = regionService.findPageBySql(pageRequest, "SELECT SBU_NAME,BU_NAME FROM BIDEV.DM_D_ENTITY_SBU ORDER BY SBU_NAME,BU_NAME");
			model.addAttribute("page", page);
		} catch (Exception e) {
			logger.error("查询SBU組織架構列表失败:", e);
		}
		return "/bi/entitySBU/list";
	}

}
