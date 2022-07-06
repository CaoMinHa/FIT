package foxconn.fit.service.bi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.bi.RegionDao;
import foxconn.fit.entity.bi.Region;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class RegionService extends BaseService<Region>{

	@Autowired
	private RegionDao regionDao;
	
	@Override
	public BaseDaoHibernate<Region> getDao() {
		return regionDao;
	}
	
	public void saveBatch(List<Region> list) throws Exception{
		String deleteSql="delete from FIT_Region";
		
		regionDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			regionDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				regionDao.getHibernateTemplate().flush();
				regionDao.getHibernateTemplate().clear();
			}
		}
	}

}
