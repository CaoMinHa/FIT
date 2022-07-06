package foxconn.fit.service.hfm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.hfm.APTradeStorageDao;
import foxconn.fit.entity.hfm.APTradeStorage;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class APTradeStorageService extends BaseService<APTradeStorage>{

	@Autowired
	private APTradeStorageDao apTradeStorageDao;
	
	@Override
	public BaseDaoHibernate<APTradeStorage> getDao() {
		return apTradeStorageDao;
	}
	
	public void saveBatch(List<APTradeStorage> list,String codeCondition) throws Exception{
		String deleteSql="delete from FIT_AP_Trade_Storage "+codeCondition;
		
		apTradeStorageDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			apTradeStorageDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				apTradeStorageDao.getHibernateTemplate().flush();
				apTradeStorageDao.getHibernateTemplate().clear();
			}
		}
	}

}
