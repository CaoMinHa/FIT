package foxconn.fit.service.hfm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.hfm.CoaMappingDao;
import foxconn.fit.entity.hfm.CoaMapping;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class CoaMappingService extends BaseService<CoaMapping>{

	@Autowired
	private CoaMappingDao coaMappingDao;
	
	@Override
	public BaseDaoHibernate<CoaMapping> getDao() {
		return coaMappingDao;
	}
	
	public void saveBatch(List<CoaMapping> list,String codeCondition) throws Exception{
		String deleteSql="delete from FIT_COA_MAPPING "+codeCondition;
		
		coaMappingDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			coaMappingDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				coaMappingDao.getHibernateTemplate().flush();
				coaMappingDao.getHibernateTemplate().clear();
			}
		}
	}

}
