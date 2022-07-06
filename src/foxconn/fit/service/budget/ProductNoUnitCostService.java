package foxconn.fit.service.budget;

import java.util.List;

import org.hibernate.classic.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.budget.ProductNoUnitCostDao;
import foxconn.fit.entity.base.EnumBudgetVersion;
import foxconn.fit.entity.base.EnumScenarios;
import foxconn.fit.entity.budget.ActualProductNoUnitCost;
import foxconn.fit.entity.budget.ProductNoUnitCost;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class ProductNoUnitCostService extends BaseService<ProductNoUnitCost>{

	@Autowired
	private ProductNoUnitCostDao productNoUnitCostDao;
	
	@Override
	public BaseDaoHibernate<ProductNoUnitCost> getDao() {
		return productNoUnitCostDao;
	}
	
	public void saveBatch(List<ProductNoUnitCost> list,String condition) throws Exception{
		String deleteSql="delete from FIT_Product_No_Unit_Cost "+condition;
		productNoUnitCostDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			productNoUnitCostDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				productNoUnitCostDao.getHibernateTemplate().flush();
				productNoUnitCostDao.getHibernateTemplate().clear();
			}
		}
	}

	public void saveActual(List<ActualProductNoUnitCost> list, String year,String period, String sbu) {
		for (int i = 0; i < list.size(); i++) {
			ActualProductNoUnitCost cost = list.get(i);
			String entity = cost.getEntity();
			String product = cost.getProduct();
			String materialStandardCost = cost.getMaterialStandardCost();
			String standardHours = cost.getStandardHours();
			String manualStandardRate = cost.getManualStandardRate();
			String manualCost = cost.getManualCost();
			String manufactureStandardRate = cost.getManufactureStandardRate();
			String manufactureCost = cost.getManufactureCost();
			String unitCost = cost.getUnitCost();
			String updateSql="update FIT_PRODUCT_NO_UNIT_COST set material_standard_cost"+period+"='"+materialStandardCost+"',standard_hours"+period+"='"+standardHours+"',manual_Standard_Rate"+period+"='"+manualStandardRate+"',manual_Cost"+period+"='"+manualCost+"',manufacture_Standard_Rate"+period+"='"+manufactureStandardRate+"',manufacture_Cost"+period+"='"+manufactureCost+"',unit_Cost"+period+"='"+unitCost+"' where year='"+year+"' and scenarios='"+EnumScenarios.Actual.getCode()+"' and sbu='"+sbu+"' and product='"+product+"' and entity='"+entity+"' and version='"+EnumBudgetVersion.V00.getCode()+"'";
			int update = productNoUnitCostDao.getSessionFactory().getCurrentSession().createSQLQuery(updateSql).executeUpdate();
			if (update<1) {
				String insertSql="insert into FIT_PRODUCT_NO_UNIT_COST(ID,PRODUCT,SBU,ENTITY,YEAR,SCENARIOS,Material_Standard_Cost"+period+",Standard_Hours"+period+",Manual_Standard_Rate"+period+",Manual_Cost"+period+",Manufacture_Standard_Rate"+period+",Manufacture_Cost"+period+",Unit_Cost"+period+") "+
								 "values(sys_guid(),'"+product+"','"+sbu+"','"+entity+"','"+year+"','"+EnumScenarios.Actual.getCode()+"','"+materialStandardCost+"','"+standardHours+"','"+manualStandardRate+"','"+manualCost+"','"+manufactureStandardRate+"','"+manufactureCost+"','"+unitCost+"')";
				productNoUnitCostDao.getSessionFactory().getCurrentSession().createSQLQuery(insertSql).executeUpdate();
			}
			//實際產品料號單位成本數據上傳后，替換“預測”中，相應月份的數據。
			String updateForecastSql="update FIT_PRODUCT_NO_UNIT_COST set material_standard_cost"+period+"='"+materialStandardCost+"',standard_hours"+period+"='"+standardHours+"',manual_Standard_Rate"+period+"='"+manualStandardRate+"',manual_Cost"+period+"='"+manualCost+"',manufacture_Standard_Rate"+period+"='"+manufactureStandardRate+"',manufacture_Cost"+period+"='"+manufactureCost+"',unit_Cost"+period+"='"+unitCost+"' where year='"+year+"' and scenarios='"+EnumScenarios.Forecast.getCode()+"' and sbu='"+sbu+"' and product='"+product+"' and entity='"+entity+"' and version='"+EnumBudgetVersion.V00.getCode()+"'";
			productNoUnitCostDao.getSessionFactory().getCurrentSession().createSQLQuery(updateForecastSql).executeUpdate();
			
			if ((i+1)%1000==0) {
				productNoUnitCostDao.getHibernateTemplate().flush();
				productNoUnitCostDao.getHibernateTemplate().clear();
			}
		}
	}

	public void deleteVersion(String entitys, String year, String scenarios) {
		String sbu="";
		for (String s : entitys.split(",")) {
			sbu+=s+"|";
		}
		sbu=sbu.substring(0,sbu.length()-1);
		Session session = productNoUnitCostDao.getSessionFactory().getCurrentSession();
		String deleteSql="delete from FIT_PRODUCT_NO_UNIT_COST where year='"+year+"' and scenarios='"+scenarios+"' and version='V00' and REGEXP_LIKE(sbu,'^("+sbu+")')";
		session.createSQLQuery(deleteSql).executeUpdate();
	}

}
