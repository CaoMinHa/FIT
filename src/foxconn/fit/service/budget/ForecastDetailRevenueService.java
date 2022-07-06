package foxconn.fit.service.budget;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.budget.ForecastDetailRevenueDao;
import foxconn.fit.entity.base.EnumScenarios;
import foxconn.fit.entity.budget.ActualDetailRevenue;
import foxconn.fit.entity.budget.ForecastDetailRevenue;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class ForecastDetailRevenueService extends BaseService<ForecastDetailRevenue>{

	@Autowired
	private ForecastDetailRevenueDao forecastDetailRevenueDao;
	
	@Override
	public BaseDaoHibernate<ForecastDetailRevenue> getDao() {
		return forecastDetailRevenueDao;
	}
	
	public void saveActual(List<ActualDetailRevenue> list,String period) {
		for (int i = 0; i < list.size(); i++) {
			ActualDetailRevenue actual = list.get(i);
			String entity = actual.getEntity();
			String industry = actual.getIndustry();
			String product = actual.getProduct();
			String combine = actual.getCombine();
			String customer = actual.getCustomer();
			String type = actual.getType();
			String currency = actual.getCurrency();
			String version = actual.getVersion();
			String year = actual.getYear();
			String quantity = actual.getQuantity();
			String averageSalesPrice = actual.getAverageSalesPrice();
			String revenue = actual.getRevenue();
			String updateSql="update FIT_FORECAST_DETAIL_REV_SRC set QUANTITY_MONTH"+period+"='"+quantity+"',PRICE_MONTH"+period+"='"+averageSalesPrice+"',REVENUE_MONTH"+period+"='"+revenue+"' where ENTITY='"+entity+"' and INDUSTRY='"+industry+"' and PRODUCT='"+product+"' and COMBINE='"+combine+"' and CUSTOMER='"+customer+"' and TYPE='"+type+"' and CURRENCY='"+currency+"' and VERSION='"+version+"' and year='"+year+"' and scenarios='"+EnumScenarios.Actual.getCode()+"'";
			int update = forecastDetailRevenueDao.getSessionFactory().getCurrentSession().createSQLQuery(updateSql).executeUpdate();
			if (update<1) {
				String insertSql="insert into FIT_FORECAST_DETAIL_REV_SRC(ID,ENTITY,INDUSTRY,PRODUCT,COMBINE,CUSTOMER,TYPE,CURRENCY,VERSION,SCENARIOS,YEAR,QUANTITY_MONTH"+period+",PRICE_MONTH"+period+",REVENUE_MONTH"+period+") "+
								 "values(sys_guid(),'"+entity+"','"+industry+"','"+product+"','"+combine+"','"+customer+"','"+type+"','"+currency+"','"+version+"','"+EnumScenarios.Actual.getCode()+"','"+year+"','"+quantity+"','"+averageSalesPrice+"','"+revenue+"')";
				forecastDetailRevenueDao.getSessionFactory().getCurrentSession().createSQLQuery(insertSql).executeUpdate();
			}
			
			if ((i+1)%1000==0) {
				forecastDetailRevenueDao.getHibernateTemplate().flush();
				forecastDetailRevenueDao.getHibernateTemplate().clear();
			}
		}
	}
	
	public void saveBatch(List<ForecastDetailRevenue> list) throws Exception{
		String deleteSql="delete from FIT_FORECAST_DETAIL_REVENUE";
		
		forecastDetailRevenueDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			forecastDetailRevenueDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				forecastDetailRevenueDao.getHibernateTemplate().flush();
				forecastDetailRevenueDao.getHibernateTemplate().clear();
			}
		}
	}
	
	public void dataTransfer() throws Exception{
		Connection c = SessionFactoryUtils.getDataSource(forecastDetailRevenueDao.getSessionFactory()).getConnection();  
		CallableStatement cs = c.prepareCall("{call PKG_BI_MAPPING.Forecast_Revenue_Process(?,?)}");  
		//需要获取输出参数时，必须注册输出参数，否则直接条用cs.getInt(4)时会报索引列无效  
		cs.registerOutParameter(1, java.sql.Types.VARCHAR);  
		cs.registerOutParameter(2, java.sql.Types.VARCHAR);  
		cs.execute();  
		String status = cs.getString(1);
		String message = cs.getString(2);
		
		cs.close();
		c.close();
		
		if (!"S".equals(status)) {
			throw new RuntimeException("存储数据失败 : "+message);
		}
	}

}
