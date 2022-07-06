package foxconn.fit.service.ebs;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.ebs.ReconciliationDao;
import foxconn.fit.entity.ebs.Reconciliation;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class ReconciliationService extends BaseService<Reconciliation>{
	
	@Autowired
	private ReconciliationDao reconciliationDao;
	
	@Override
	public BaseDaoHibernate<Reconciliation> getDao() {
		return reconciliationDao;
	}
	
	public void saveBatch(List<Reconciliation> list,String condition) throws Exception{
		String deleteSql="delete from CUX_BAL "+condition;
		reconciliationDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			reconciliationDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				reconciliationDao.getHibernateTemplate().flush();
				reconciliationDao.getHibernateTemplate().clear();
			}
		}
	}

	public String dataImport(String entitys, String year, String period,Locale locale) throws Exception{
		Connection c = SessionFactoryUtils.getDataSource(reconciliationDao.getSessionFactory()).getConnection();  
		CallableStatement cs = c.prepareCall("{call CUX_DATA_INTEGRATION_PKG.import_bal_prc(?,?,?,?,?,?)}");  
		cs.setString(1, entitys);
		cs.setString(2, year);
		cs.setString(3, period);
		cs.setString(4, locale!=null?locale.toString():"zh_CN");
		cs.registerOutParameter(5, java.sql.Types.VARCHAR);  
		cs.registerOutParameter(6, java.sql.Types.VARCHAR); 
		cs.execute();  
		String status = cs.getString(5);
		String message = cs.getString(6);
		cs.close();
		c.close();
		
		if ("S".equals(status)) {
			message="";
		}
		return message;
	}

}
