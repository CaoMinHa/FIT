package foxconn.fit.service.bi;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.bi.RevenueDetailManualDao;
import foxconn.fit.entity.bi.RevenueDetailManual;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class RevenueDetailManualService extends BaseService<RevenueDetailManual>{

	@Autowired
	private RevenueDetailManualDao managementReportManualDao;
	
	@Override
	public BaseDaoHibernate<RevenueDetailManual> getDao() {
		return managementReportManualDao;
	}
	
	public void saveBatch(List<RevenueDetailManual> list,String codeCondition) throws Exception{
		String deleteSql="delete from FIT_Revenue_Detail_Manual "+codeCondition;
		
		managementReportManualDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			managementReportManualDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				managementReportManualDao.getHibernateTemplate().flush();
				managementReportManualDao.getHibernateTemplate().clear();
			}
		}
	}
	
	public Map<String,String> mapping(List<String> codes,String year, String month,String version) throws Exception {
		String entity="";
		for (String code : codes) {
			entity+="'"+code+"',";
		}
		entity=entity.substring(0, entity.length()-1);
		
		Map<String,String> map=new HashMap<String, String>();
		Connection c = SessionFactoryUtils.getDataSource(managementReportManualDao.getSessionFactory()).getConnection();  
		CallableStatement cs = c.prepareCall("{call PKG_BI_MAPPING.Revenue_Detail_Actual_Mapping(?,?,?,?,?,?)}");  
		cs.setString(1, year);
		cs.setString(2, month);
		cs.setString(3, version);
		cs.setString(4, entity);
		//需要获取输出参数时，必须注册输出参数，否则直接条用cs.getInt(4)时会报索引列无效  
		cs.registerOutParameter(5, java.sql.Types.VARCHAR);  
		cs.registerOutParameter(6, java.sql.Types.VARCHAR);  
		cs.execute();  
		String status = cs.getString(5);
		String message = cs.getString(6);
		
		cs.close();
		c.close();
		
		map.put("status", status);
		map.put("message", message);
		
		return map;
	}
	
	public Map<String,String> synchronize(String year, String month,String version) throws Exception {
		Map<String,String> map=new HashMap<String, String>();
		Connection c = SessionFactoryUtils.getDataSource(managementReportManualDao.getSessionFactory()).getConnection();  
		CallableStatement cs = c.prepareCall("{call BIDEV.PETL_MAIN.REV_LOAD_MONTHLY_EX(?,?,?,?)}");  
		cs.setInt(1, Integer.parseInt(year+month));
		cs.setInt(2, Integer.parseInt(version.substring(1)));
		cs.registerOutParameter(3, java.sql.Types.INTEGER);  
		cs.registerOutParameter(4, java.sql.Types.VARCHAR);  
		cs.execute();  
		int code = cs.getInt(3);
		String message = cs.getString(4);
		
		cs.close();
		c.close();
		
		map.put("code", code+"");
		map.put("message", message);
		
		return map;
	}

}
