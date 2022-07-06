package foxconn.fit.service.hfm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.hfm.HFMCustomerMappingDao;
import foxconn.fit.entity.hfm.HFMCustomerMapping;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class HFMCustomerMappingService extends BaseService<HFMCustomerMapping>{

	@Autowired
	private HFMCustomerMappingDao customerMappingDao;
	
	@Override
	public BaseDaoHibernate<HFMCustomerMapping> getDao() {
		return customerMappingDao;
	}
	
	public void saveBatch(List<HFMCustomerMapping> list,String codeCondition) throws Exception{
		String deleteSql="delete from FIT_HFM_Customer_Mapping "+codeCondition;
		
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
