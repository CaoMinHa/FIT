package foxconn.fit.service.base;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.base.PlanningDao;
import foxconn.fit.entity.base.Planning;
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
}
