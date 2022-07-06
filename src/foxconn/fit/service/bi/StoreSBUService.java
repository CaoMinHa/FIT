package foxconn.fit.service.bi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.bi.StoreSBUDao;
import foxconn.fit.entity.bi.StoreSBU;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class StoreSBUService extends BaseService<StoreSBU>{

	@Autowired
	private StoreSBUDao storeSBUDao;
	
	@Override
	public BaseDaoHibernate<StoreSBU> getDao() {
		return storeSBUDao;
	}
	
	public void saveBatch(List<StoreSBU> list) throws Exception{
		String deleteSql="delete from FIT_Store_SBU";
		
		storeSBUDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			storeSBUDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				storeSBUDao.getHibernateTemplate().flush();
				storeSBUDao.getHibernateTemplate().clear();
			}
		}
	}

}
