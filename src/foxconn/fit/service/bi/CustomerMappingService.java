package foxconn.fit.service.bi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.bi.CustomerMappingDao;
import foxconn.fit.entity.bi.CustomerMapping;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class CustomerMappingService extends BaseService<CustomerMapping>{

	@Autowired
	private CustomerMappingDao customerMappingDao;
	
	@Override
	public BaseDaoHibernate<CustomerMapping> getDao() {
		return customerMappingDao;
	}
	
	public void saveBatch(List<CustomerMapping> list) throws Exception{
		String deleteSql="delete from FIT_Customer_Mapping";
		
		customerMappingDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			customerMappingDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				customerMappingDao.getHibernateTemplate().flush();
				customerMappingDao.getHibernateTemplate().clear();
			}
		}
	}

}
