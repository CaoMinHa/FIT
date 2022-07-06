package foxconn.fit.service.base;

import java.sql.CallableStatement;
import java.sql.Connection;

import org.hibernate.classic.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.base.PlanningDao;
import foxconn.fit.entity.base.Planning;

@Service
@Transactional(rollbackFor = Exception.class)
public class PlanningService extends BaseService<Planning>{

	@Autowired
	private PlanningDao planningDao;
	
	@Override
	public BaseDaoHibernate<Planning> getDao() {
		return planningDao;
	}
	
	public String generatePlanning(String sbu, String year, String scenarios) throws Exception{
		Connection c = SessionFactoryUtils.getDataSource(planningDao.getSessionFactory()).getConnection();  
		CallableStatement cs = c.prepareCall("{call PKG_BI_MAPPING.generate_planning(?,?,?,?,?)}");  
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

	public void copy(String entitys, String year, String scenarios, String version) {
		String sbu="";
		for (String s : entitys.split(",")) {
			sbu+=s+"|";
		}
		sbu=sbu.substring(0,sbu.length()-1);
		Session session = planningDao.getSessionFactory().getCurrentSession();
		String deleteSql="delete from FIT_PRODUCT_NO_UNIT_COST where year='"+year+"' and scenarios='"+scenarios+"' and version='"+version+"' and REGEXP_LIKE(sbu,'^("+sbu+")')";
		session.createSQLQuery(deleteSql).executeUpdate();
		String insertSql="insert into FIT_PRODUCT_NO_UNIT_COST(id,product,sbu,entity,year,scenarios,version, "+
				 "      material_standard_cost1,material_adjust_cost1,material_cost1,standard_hours1,adjust_hours1,hours1,manual_standard_rate1,manual_adjust_rate1,manual_rate1,manual_cost1,manufacture_standard_rate1,manufacture_adjust_rate1,manufacture_rate1,manufacture_cost1,unit_cost1,"+
				 "      material_standard_cost2,material_adjust_cost2,material_cost2,standard_hours2,adjust_hours2,hours2,manual_standard_rate2,manual_adjust_rate2,manual_rate2,manual_cost2,manufacture_standard_rate2,manufacture_adjust_rate2,manufacture_rate2,manufacture_cost2,unit_cost2,"+
				 "      material_standard_cost3,material_adjust_cost3,material_cost3,standard_hours3,adjust_hours3,hours3,manual_standard_rate3,manual_adjust_rate3,manual_rate3,manual_cost3,manufacture_standard_rate3,manufacture_adjust_rate3,manufacture_rate3,manufacture_cost3,unit_cost3,"+
				 "      material_standard_cost4,material_adjust_cost4,material_cost4,standard_hours4,adjust_hours4,hours4,manual_standard_rate4,manual_adjust_rate4,manual_rate4,manual_cost4,manufacture_standard_rate4,manufacture_adjust_rate4,manufacture_rate4,manufacture_cost4,unit_cost4,"+
				 "      material_standard_cost5,material_adjust_cost5,material_cost5,standard_hours5,adjust_hours5,hours5,manual_standard_rate5,manual_adjust_rate5,manual_rate5,manual_cost5,manufacture_standard_rate5,manufacture_adjust_rate5,manufacture_rate5,manufacture_cost5,unit_cost5,"+
				 "      material_standard_cost6,material_adjust_cost6,material_cost6,standard_hours6,adjust_hours6,hours6,manual_standard_rate6,manual_adjust_rate6,manual_rate6,manual_cost6,manufacture_standard_rate6,manufacture_adjust_rate6,manufacture_rate6,manufacture_cost6,unit_cost6,"+
				 "      material_standard_cost7,material_adjust_cost7,material_cost7,standard_hours7,adjust_hours7,hours7,manual_standard_rate7,manual_adjust_rate7,manual_rate7,manual_cost7,manufacture_standard_rate7,manufacture_adjust_rate7,manufacture_rate7,manufacture_cost7,unit_cost7,"+
				 "      material_standard_cost8,material_adjust_cost8,material_cost8,standard_hours8,adjust_hours8,hours8,manual_standard_rate8,manual_adjust_rate8,manual_rate8,manual_cost8,manufacture_standard_rate8,manufacture_adjust_rate8,manufacture_rate8,manufacture_cost8,unit_cost8,"+
				 "      material_standard_cost9,material_adjust_cost9,material_cost9,standard_hours9,adjust_hours9,hours9,manual_standard_rate9,manual_adjust_rate9,manual_rate9,manual_cost9,manufacture_standard_rate9,manufacture_adjust_rate9,manufacture_rate9,manufacture_cost9,unit_cost9,"+
				 "      material_standard_cost10,material_adjust_cost10,material_cost10,standard_hours10,adjust_hours10,hours10,manual_standard_rate10,manual_adjust_rate10,manual_rate10,manual_cost10,manufacture_standard_rate10,manufacture_adjust_rate10,manufacture_rate10,manufacture_cost10,unit_cost10,"+
				 "      material_standard_cost11,material_adjust_cost11,material_cost11,standard_hours11,adjust_hours11,hours11,manual_standard_rate11,manual_adjust_rate11,manual_rate11,manual_cost11,manufacture_standard_rate11,manufacture_adjust_rate11,manufacture_rate11,manufacture_cost11,unit_cost11,"+
				 "      material_standard_cost12,material_adjust_cost12,material_cost12,standard_hours12,adjust_hours12,hours12,manual_standard_rate12,manual_adjust_rate12,manual_rate12,manual_cost12,manufacture_standard_rate12,manufacture_adjust_rate12,manufacture_rate12,manufacture_cost12,unit_cost12)"+
				 " select sys_guid(),product,sbu,entity,year,scenarios,'"+version+"',"+
				 "      material_standard_cost1,material_adjust_cost1,material_cost1,standard_hours1,adjust_hours1,hours1,manual_standard_rate1,manual_adjust_rate1,manual_rate1,manual_cost1,manufacture_standard_rate1,manufacture_adjust_rate1,manufacture_rate1,manufacture_cost1,unit_cost1,"+
				 "      material_standard_cost2,material_adjust_cost2,material_cost2,standard_hours2,adjust_hours2,hours2,manual_standard_rate2,manual_adjust_rate2,manual_rate2,manual_cost2,manufacture_standard_rate2,manufacture_adjust_rate2,manufacture_rate2,manufacture_cost2,unit_cost2,"+
				 "      material_standard_cost3,material_adjust_cost3,material_cost3,standard_hours3,adjust_hours3,hours3,manual_standard_rate3,manual_adjust_rate3,manual_rate3,manual_cost3,manufacture_standard_rate3,manufacture_adjust_rate3,manufacture_rate3,manufacture_cost3,unit_cost3,"+
				 "      material_standard_cost4,material_adjust_cost4,material_cost4,standard_hours4,adjust_hours4,hours4,manual_standard_rate4,manual_adjust_rate4,manual_rate4,manual_cost4,manufacture_standard_rate4,manufacture_adjust_rate4,manufacture_rate4,manufacture_cost4,unit_cost4,"+
				 "      material_standard_cost5,material_adjust_cost5,material_cost5,standard_hours5,adjust_hours5,hours5,manual_standard_rate5,manual_adjust_rate5,manual_rate5,manual_cost5,manufacture_standard_rate5,manufacture_adjust_rate5,manufacture_rate5,manufacture_cost5,unit_cost5,"+
				 "      material_standard_cost6,material_adjust_cost6,material_cost6,standard_hours6,adjust_hours6,hours6,manual_standard_rate6,manual_adjust_rate6,manual_rate6,manual_cost6,manufacture_standard_rate6,manufacture_adjust_rate6,manufacture_rate6,manufacture_cost6,unit_cost6,"+
				 "      material_standard_cost7,material_adjust_cost7,material_cost7,standard_hours7,adjust_hours7,hours7,manual_standard_rate7,manual_adjust_rate7,manual_rate7,manual_cost7,manufacture_standard_rate7,manufacture_adjust_rate7,manufacture_rate7,manufacture_cost7,unit_cost7,"+
				 "      material_standard_cost8,material_adjust_cost8,material_cost8,standard_hours8,adjust_hours8,hours8,manual_standard_rate8,manual_adjust_rate8,manual_rate8,manual_cost8,manufacture_standard_rate8,manufacture_adjust_rate8,manufacture_rate8,manufacture_cost8,unit_cost8,"+
				 "      material_standard_cost9,material_adjust_cost9,material_cost9,standard_hours9,adjust_hours9,hours9,manual_standard_rate9,manual_adjust_rate9,manual_rate9,manual_cost9,manufacture_standard_rate9,manufacture_adjust_rate9,manufacture_rate9,manufacture_cost9,unit_cost9,"+
				 "      material_standard_cost10,material_adjust_cost10,material_cost10,standard_hours10,adjust_hours10,hours10,manual_standard_rate10,manual_adjust_rate10,manual_rate10,manual_cost10,manufacture_standard_rate10,manufacture_adjust_rate10,manufacture_rate10,manufacture_cost10,unit_cost10,"+
				 "      material_standard_cost11,material_adjust_cost11,material_cost11,standard_hours11,adjust_hours11,hours11,manual_standard_rate11,manual_adjust_rate11,manual_rate11,manual_cost11,manufacture_standard_rate11,manufacture_adjust_rate11,manufacture_rate11,manufacture_cost11,unit_cost11,"+
				 "      material_standard_cost12,material_adjust_cost12,material_cost12,standard_hours12,adjust_hours12,hours12,manual_standard_rate12,manual_adjust_rate12,manual_rate12,manual_cost12,manufacture_standard_rate12,manufacture_adjust_rate12,manufacture_rate12,manufacture_cost12,unit_cost12"+
				 " from FIT_PRODUCT_NO_UNIT_COST t where year='"+year+"' and scenarios='"+scenarios+"' and version='V00' and REGEXP_LIKE(sbu,'^("+sbu+")')";
		session.createSQLQuery(insertSql).executeUpdate();
		
		deleteSql="delete from FIT_FORECAST_DETAIL_REV_SRC where year='"+year+"' and scenarios='"+scenarios+"' and version='"+version+"' and REGEXP_LIKE(ENTITY,'^("+sbu+")')";
		session.createSQLQuery(deleteSql).executeUpdate();
		insertSql="insert into FIT_FORECAST_DETAIL_REV_SRC(id,version,entity,industry,product,combine,customer,type,currency,scenarios,year,industry_demand_trend,industry_demand_trend_served,"+
				 "		component_usage,average_sales_price,total_available_market,served_available_market,allocation,revenue,quantity,activity,"+
				 "		quantity_month1,quantity_month2,quantity_month3,quantity_month4,quantity_month5,quantity_month6,quantity_month7,quantity_month8,quantity_month9,quantity_month10,quantity_month11,quantity_month12,"+
				 "		revenue_month1,revenue_month2,revenue_month3,revenue_month4,revenue_month5,revenue_month6,revenue_month7,revenue_month8,revenue_month9,revenue_month10,revenue_month11,revenue_month12,"+
				 "		material_month1,material_month2,material_month3,material_month4,material_month5,material_month6,material_month7,material_month8,material_month9,material_month10,material_month11,material_month12,"+
				 "		manual_month1,manual_month2,manual_month3,manual_month4,manual_month5,manual_month6,manual_month7,manual_month8,manual_month9,manual_month10,manual_month11,manual_month12,"+
				 "		manufacture_month1,manufacture_month2,manufacture_month3,manufacture_month4,manufacture_month5,manufacture_month6,manufacture_month7,manufacture_month8,manufacture_month9,manufacture_month10,manufacture_month11,manufacture_month12,"+
				 "		price_month1,price_month2,price_month3,price_month4,price_month5,price_month6,price_month7,price_month8,price_month9,price_month10,price_month11,price_month12)"+
				 " select sys_guid(),'"+version+"',entity,industry,product,combine,customer,type,currency,scenarios,year,industry_demand_trend,industry_demand_trend_served,"+
				 "		component_usage,average_sales_price,total_available_market,served_available_market,allocation,revenue,quantity,activity,"+
				 "		quantity_month1,quantity_month2,quantity_month3,quantity_month4,quantity_month5,quantity_month6,quantity_month7,quantity_month8,quantity_month9,quantity_month10,quantity_month11,quantity_month12,"+
				 "		revenue_month1,revenue_month2,revenue_month3,revenue_month4,revenue_month5,revenue_month6,revenue_month7,revenue_month8,revenue_month9,revenue_month10,revenue_month11,revenue_month12,"+
				 "		material_month1,material_month2,material_month3,material_month4,material_month5,material_month6,material_month7,material_month8,material_month9,material_month10,material_month11,material_month12,"+
				 "		manual_month1,manual_month2,manual_month3,manual_month4,manual_month5,manual_month6,manual_month7,manual_month8,manual_month9,manual_month10,manual_month11,manual_month12,"+
				 "		manufacture_month1,manufacture_month2,manufacture_month3,manufacture_month4,manufacture_month5,manufacture_month6,manufacture_month7,manufacture_month8,manufacture_month9,manufacture_month10,manufacture_month11,manufacture_month12,"+
				 "		price_month1,price_month2,price_month3,price_month4,price_month5,price_month6,price_month7,price_month8,price_month9,price_month10,price_month11,price_month12"+
				 " from FIT_FORECAST_DETAIL_REV_SRC t where year='"+year+"' and scenarios='"+scenarios+"' and version='V00' and REGEXP_LIKE(ENTITY,'^("+sbu+")')";
		session.createSQLQuery(insertSql).executeUpdate();
	}

}
