package foxconn.fit.service.bi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.bi.CustomerNameSpecificationDao;
import foxconn.fit.entity.bi.CustomerNameSpecification;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class CustomerNameSpecificationService extends BaseService<CustomerNameSpecification>{

	@Autowired
	private CustomerNameSpecificationDao customerNameSpecificationDao;
	
	@Override
	public BaseDaoHibernate<CustomerNameSpecification> getDao() {
		return customerNameSpecificationDao;
	}
	
	public void saveBatch(List<CustomerNameSpecification> list) throws Exception{
		String deleteSql="delete from FIT_Customer_Name_Specify";
		
		customerNameSpecificationDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			customerNameSpecificationDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				customerNameSpecificationDao.getHibernateTemplate().flush();
				customerNameSpecificationDao.getHibernateTemplate().clear();
			}
		}
	}

}
