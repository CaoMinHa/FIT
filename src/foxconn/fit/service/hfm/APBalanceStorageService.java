package foxconn.fit.service.hfm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.hfm.APBalanceStorageDao;
import foxconn.fit.entity.hfm.APBalanceStorage;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class APBalanceStorageService extends BaseService<APBalanceStorage>{

	@Autowired
	private APBalanceStorageDao apBalanceStorageDao;
	
	@Override
	public BaseDaoHibernate<APBalanceStorage> getDao() {
		return apBalanceStorageDao;
	}
	
	public void saveBatch(List<APBalanceStorage> list,String codeCondition) throws Exception{
		String deleteSql="delete from FIT_AP_Balance_Storage "+codeCondition;
		
		apBalanceStorageDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			apBalanceStorageDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				apBalanceStorageDao.getHibernateTemplate().flush();
				apBalanceStorageDao.getHibernateTemplate().clear();
			}
		}
	}

}
