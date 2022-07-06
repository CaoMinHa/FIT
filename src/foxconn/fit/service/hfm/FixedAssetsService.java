package foxconn.fit.service.hfm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.hfm.FixedAssetsDao;
import foxconn.fit.entity.hfm.FixedAssets;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class FixedAssetsService extends BaseService<FixedAssets>{

	@Autowired
	private FixedAssetsDao fixedAssetsDao;
	
	@Override
	public BaseDaoHibernate<FixedAssets> getDao() {
		return fixedAssetsDao;
	}
	
	public void saveBatch(List<FixedAssets> list,String codeCondition) throws Exception{
		String deleteSql="delete from FIT_Fixed_Assets "+codeCondition;
		
		fixedAssetsDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			fixedAssetsDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				fixedAssetsDao.getHibernateTemplate().flush();
				fixedAssetsDao.getHibernateTemplate().clear();
			}
		}
	}

}
