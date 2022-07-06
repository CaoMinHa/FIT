package foxconn.fit.service.bi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.bi.SaleClassificationDao;
import foxconn.fit.entity.bi.SaleClassification;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class SaleClassificationService extends BaseService<SaleClassification>{

	@Autowired
	private SaleClassificationDao saleClassificationDao;
	
	@Override
	public BaseDaoHibernate<SaleClassification> getDao() {
		return saleClassificationDao;
	}
	
	public void saveBatch(List<SaleClassification> list) throws Exception{
		String deleteSql="delete from FIT_Sale_Classification";
		
		saleClassificationDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			saleClassificationDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				saleClassificationDao.getHibernateTemplate().flush();
				saleClassificationDao.getHibernateTemplate().clear();
			}
		}
	}

}
