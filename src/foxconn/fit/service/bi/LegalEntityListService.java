package foxconn.fit.service.bi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.bi.LegalEntityListDao;
import foxconn.fit.entity.bi.LegalEntityList;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class LegalEntityListService extends BaseService<LegalEntityList>{

	@Autowired
	private LegalEntityListDao legalEntityListDao;
	
	@Override
	public BaseDaoHibernate<LegalEntityList> getDao() {
		return legalEntityListDao;
	}
	
	public void saveBatch(List<LegalEntityList> list) throws Exception{
		String deleteSql="delete from FIT_Legal_Entity_List";
		
		legalEntityListDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			legalEntityListDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				legalEntityListDao.getHibernateTemplate().flush();
				legalEntityListDao.getHibernateTemplate().clear();
			}
		}
	}

}
