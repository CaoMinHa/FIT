package foxconn.fit.controller.admin;

import com.csvreader.CsvWriter;
import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.base.EnumDimensionType;
import foxconn.fit.entity.base.Planning;
import foxconn.fit.service.base.BudgetService;
import foxconn.fit.service.base.UserService;
import foxconn.fit.util.ExceptionUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/budget")
public class BudgetController extends BaseController {

	@Autowired
	private BudgetService budgetService;
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(value = "index")
	public String index(Model model) {
		List<String> yearsList = userService.listBySql("select distinct dimension from FIT_DIMENSION where type='"+EnumDimensionType.Years.getCode()+"' order by dimension");
		List<String> sbuList = userService.listBySql("select distinct parent from fit_dimension where type='"+EnumDimensionType.Entity+"' order by parent");
		model.addAttribute("yearsList", yearsList);
		model.addAttribute("sbuList", sbuList);
		return "/admin/budget/index";
	}


	@RequestMapping(value = "download")
	@ResponseBody
	@Log(name = "下载Budget")
	public synchronized String download(HttpServletRequest request,HttpServletResponse response,PageRequest pageRequest,AjaxResult result,
			@Log(name = "SBU") String sbu,@Log(name = "年") String year){
		try {
			String[] years=year.split(",");
			List<Planning> list =new ArrayList<>();
			for (int i=0;i<years.length;i++){
				String yr=years[i];
				if (sbu.endsWith(",")) {
					sbu=sbu.substring(0,sbu.length()-1);
				}
				String entity="";
				for (String s : sbu.split(",")) {
					entity+=s+"|";
				}
				entity=entity.substring(0,entity.length()-1);

				String message = budgetService.generatePlanning(entity,yr);
				if (StringUtils.isNotEmpty(message)) {
					throw new RuntimeException("计算Budget数据出错 : "+message);
				}
				String sql="select * from CUX_FIT_PLANNING_V ";
				List<Planning> lists = budgetService.listBySql(sql,Planning.class);
				list.addAll(lists);
			};

			String realPath = request.getRealPath("");
			if (CollectionUtils.isNotEmpty(list)) {
				long time = System.currentTimeMillis();
				String filePath=realPath+File.separator+"static"+File.separator+"download"+File.separator+time+".csv";
				CsvWriter writer=new CsvWriter(filePath, ',', Charset.forName("UTF8"));
				String[] periodFields=new String[]{"JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"};
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
