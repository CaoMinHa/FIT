package foxconn.fit.service.hfm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.hfm.ARTradeSaleDao;
import foxconn.fit.entity.hfm.ARTradeSale;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class ARTradeSaleService extends BaseService<ARTradeSale>{

	@Autowired
	private ARTradeSaleDao arTradeSaleDao;
	
	@Override
	public BaseDaoHibernate<ARTradeSale> getDao() {
		return arTradeSaleDao;
	}
	
	public void saveBatch(List<ARTradeSale> list,String codeCondition) throws Exception{
		String deleteSql="delete from FIT_AR_Trade_Sale "+codeCondition;
		
		arTradeSaleDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			arTradeSaleDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				arTradeSaleDao.getHibernateTemplate().flush();
				arTradeSaleDao.getHibernateTemplate().clear();
			}
		}
	}

}
