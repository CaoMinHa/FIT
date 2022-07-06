package foxconn.fit.service.hfm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.hfm.ARReceiveDao;
import foxconn.fit.entity.hfm.ARReceive;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class ARReceiveService extends BaseService<ARReceive>{

	@Autowired
	private ARReceiveDao arReceiveDao;
	
	@Override
	public BaseDaoHibernate<ARReceive> getDao() {
		return arReceiveDao;
	}
	
	public void saveBatch(List<ARReceive> list,String codeCondition) throws Exception{
		String deleteSql="delete from FIT_AR_Receive "+codeCondition;
		
		arReceiveDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			arReceiveDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				arReceiveDao.getHibernateTemplate().flush();
				arReceiveDao.getHibernateTemplate().clear();
			}
		}
	}

}
