package foxconn.fit.service.hfm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.hfm.ARBalanceSaleDao;
import foxconn.fit.entity.hfm.ARBalanceSale;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class ARBalanceSaleService extends BaseService<ARBalanceSale>{

	@Autowired
	private ARBalanceSaleDao arBalanceSaleDao;
	
	@Override
	public BaseDaoHibernate<ARBalanceSale> getDao() {
		return arBalanceSaleDao;
	}
	
	public void saveBatch(List<ARBalanceSale> list,String codeCondition) throws Exception{
		String deleteSql="delete from FIT_AR_Balance_Sale "+codeCondition;
		
		arBalanceSaleDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			arBalanceSaleDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				arBalanceSaleDao.getHibernateTemplate().flush();
				arBalanceSaleDao.getHibernateTemplate().clear();
			}
		}
	}

}
