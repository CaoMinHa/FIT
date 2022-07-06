package foxconn.fit.service.bi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.bi.PriceDao;
import foxconn.fit.entity.bi.Price;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class PriceService extends BaseService<Price>{

	@Autowired
	private PriceDao priceDao;
	
	@Override
	public BaseDaoHibernate<Price> getDao() {
		return priceDao;
	}
	
	public void saveBatch(List<Price> list,String condition) throws Exception{
		String deleteSql="delete from FIT_Price "+condition;
		
		priceDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			priceDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				priceDao.getHibernateTemplate().flush();
				priceDao.getHibernateTemplate().clear();
			}
		}
	}

}
