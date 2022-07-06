package foxconn.fit.service.bi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.bi.CCTDao;
import foxconn.fit.entity.bi.CCT;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class CCTService extends BaseService<CCT>{

	@Autowired
	private CCTDao cctDao;
	
	@Override
	public BaseDaoHibernate<CCT> getDao() {
		return cctDao;
	}
	
	public void saveBatch(List<CCT> list,String codeCondition) throws Exception{
		String deleteSql="delete from FIT_CCT "+codeCondition;
		
		cctDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			cctDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				cctDao.getHibernateTemplate().flush();
				cctDao.getHibernateTemplate().clear();
			}
		}
	}

}
