package foxconn.fit.service.base;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.base.PlanningDao;
import foxconn.fit.entity.base.Planning;
import foxconn.fit.util.SecurityUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.CallableStatement;
import java.sql.Connection;

@Service
@Transactional(rollbackFor = Exception.class)
public class BudgetService extends BaseService<Planning>{

	@Autowired
	private PlanningDao planningDao;
	
	@Override
	public BaseDaoHibernate<Planning> getDao() {
		return planningDao;
	}
	
	public String generatePlanning(String sbu, String year,String scenarios) throws Exception{
		sbu.length();
		Connection c = SessionFactoryUtils.getDataSource(planningDao.getSessionFactory()).getConnection();
		CallableStatement cs;
		if("budget".equals(scenarios)){
			cs = c.prepareCall("{call cux_budget_cost_pkg.generate_planning(?,?,?,?)}");
		}else{
			cs = c.prepareCall("{call cux_forecast_cost_pkg.generate_planning(?,?,?,?)}");
		}
		cs.setString(1, sbu);
		cs.setString(2, year);
		cs.registerOutParameter(3, java.sql.Types.VARCHAR);
		cs.registerOutParameter(4, java.sql.Types.VARCHAR);
		cs.execute();  
		String status = cs.getString(3);
		String message = cs.getString(4);
		cs.close();
		c.close();
		if (!"S".equals(status)) {
			return message;
		}
		return "";
		
	}

	public String generatePlanning(String sbu, String year,String scenarios,String type) throws Exception{
		sbu.length();
		Connection c = SessionFactoryUtils.getDataSource(planningDao.getSessionFactory()).getConnection();
		CallableStatement cs;
		if("budget".equalsIgnoreCase(scenarios)){
			scenarios="Budget";
		}else{
			scenarios="Forecast";
		}
		if("investment".equals(type)){
			cs = c.prepareCall("{call cux_budget_inter_pkg.investment_main(?,?,?,?,?)}");
		}else if("depreExpen".equals(type)){
			cs = c.prepareCall("{call cux_budget_inter_pkg.depre_main(?,?,?,?,?)}");
		}else{
			cs = c.prepareCall("{call cux_budget_inter_pkg.project_main(?,?,?,?,?)}");
		}
		cs.setString(1, sbu);
		cs.setString(2, year);
		cs.setString(3, scenarios);
		cs.registerOutParameter(4, java.sql.Types.VARCHAR);
		cs.registerOutParameter(5, java.sql.Types.VARCHAR);
		cs.execute();
		String status = cs.getString(4);
		String message = cs.getString(5);
		cs.close();
		c.close();
		if (!"S".equals(status)) {
			return message;
		}
		return "";
	}
	//專案折舊投資check20240304
	public String generatePlanning_check(String sbu, String year,String scenarios,String type) throws Exception{
		sbu.length();
		Connection c = SessionFactoryUtils.getDataSource(planningDao.getSessionFactory()).getConnection();
		CallableStatement cs;
		if("budget".equalsIgnoreCase(scenarios)){
			scenarios="Budget";
		}else{
			scenarios="Forecast";
		}
		if("investment".equals(type)){
			cs = c.prepareCall("{call cux_budget_inter_pkg.check_investment(?,?,?,?,?)}");
		}else if("depreExpen".equals(type)){
			cs = c.prepareCall("{call cux_budget_inter_pkg.check_depre(?,?,?,?,?)}");
		}else{
			cs = c.prepareCall("{call cux_budget_inter_pkg.check_project(?,?,?,?,?)}");
		}
		cs.setString(1, sbu);
		cs.setString(2, year);
		cs.setString(3, scenarios);
		cs.registerOutParameter(4, java.sql.Types.VARCHAR);
		cs.registerOutParameter(5, java.sql.Types.VARCHAR);
		cs.execute();
		String status = cs.getString(4);
		String message = cs.getString(5);
		cs.close();
		c.close();
		if (!"S".equals(status)) {
			return message;
		}
		return "";
	}
	//TYPE為投資時，增加投資卡控，不能低於已花費金額 ，check20250103
		public String generatePlanning_check1(String sbu, String year,String scenarios,String type) throws Exception{
			sbu.length();
			Connection c = SessionFactoryUtils.getDataSource(planningDao.getSessionFactory()).getConnection();
			CallableStatement cs = null;
			UserDetailImpl loginUser = SecurityUtils.getLoginUser();
			if("budget".equalsIgnoreCase(scenarios)){
				scenarios="Budget";
			}else{
				scenarios="Forecast";
			}
			if("investment".equals(type)){
				cs = c.prepareCall("{call cux_budget_inter_pkg.check_investment_amount(?,?,?,?,?,?)}");
			}
			
			cs.setString(1, sbu);
			cs.setString(2, year);
			cs.setString(3, scenarios);
			cs.setString(4, loginUser.getUsername());
			cs.registerOutParameter(5, java.sql.Types.VARCHAR);
			cs.registerOutParameter(6, java.sql.Types.VARCHAR);
			cs.execute();
			String status = cs.getString(5);
			String message = cs.getString(6);
			cs.close();
			c.close();
			if (!"S".equals(status)) {
				return message;
			}
			return "";
		}
	//销货预算收入及成本上传后点提交(增加检查:匹配成功后,再执行三段式内交&生成HYPERION數據)   cxj20240228
	public String Planning_check(String sbu, String year,String scenarios) throws Exception{
		sbu.length();
		Connection c = SessionFactoryUtils.getDataSource(planningDao.getSessionFactory()).getConnection();
		CallableStatement cs;
		if("budget".equals(scenarios)){
			cs = c.prepareCall("{call cux_budget_cost_pkg.check_sales(?,?,?,?)}");
		}else{
			cs = c.prepareCall("{call cux_forecast_cost_pkg.check_sales(?,?,?,?)}");
		}
		cs.setString(1, sbu);
		cs.setString(2, year);
		cs.registerOutParameter(3, java.sql.Types.VARCHAR);
		cs.registerOutParameter(4, java.sql.Types.VARCHAR);
		cs.execute();  
		String status = cs.getString(3);
		String message = cs.getString(4);
		cs.close();
		c.close();
		if (!"S".equals(status)) {
			return message;
		}
		return "";
		
	}
	//销货预算收入及成本上传后点提交(增加检查:sbu相同法人不同，交易為外售是否有交易路徑，若無報錯，否則再执行三段式内交&生成HYPERION數據)   cxj20240229
		public String Planning_check1(String sbu, String year,String scenarios) throws Exception{
			sbu.length();
			Connection c = SessionFactoryUtils.getDataSource(planningDao.getSessionFactory()).getConnection();
			CallableStatement cs;
			if("budget".equals(scenarios)){
				cs = c.prepareCall("{call cux_budget_cost_pkg.check_transaction_path(?,?,?,?)}");
			}else{
				cs = c.prepareCall("{call cux_forecast_cost_pkg.check_transaction_path(?,?,?,?)}");
			}
			cs.setString(1, sbu);
			cs.setString(2, year);
			cs.registerOutParameter(3, java.sql.Types.VARCHAR);
			cs.registerOutParameter(4, java.sql.Types.VARCHAR);
			cs.execute();  
			String status = cs.getString(3);
			String message = cs.getString(4);
			cs.close();
			c.close();
			if (!"S".equals(status)) {
				return message;
			}
			return "";
			
		}
}
