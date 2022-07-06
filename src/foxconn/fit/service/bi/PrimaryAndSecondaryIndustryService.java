package foxconn.fit.service.bi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.bi.PrimaryAndSecondaryIndustryDao;
import foxconn.fit.entity.bi.PrimaryAndSecondaryIndustry;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class PrimaryAndSecondaryIndustryService extends BaseService<PrimaryAndSecondaryIndustry>{

	@Autowired
	private PrimaryAndSecondaryIndustryDao primaryAndSecondaryIndustryDao;
	
	@Override
	public BaseDaoHibernate<PrimaryAndSecondaryIndustry> getDao() {
		return primaryAndSecondaryIndustryDao;
	}
	
	public void saveBatch(List<PrimaryAndSecondaryIndustry> list) throws Exception{
		String deleteSql="delete from FIT_Primary_Secondary_Industry";
		
		primaryAndSecondaryIndustryDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			primaryAndSecondaryIndustryDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				primaryAndSecondaryIndustryDao.getHibernateTemplate().flush();
				primaryAndSecondaryIndustryDao.getHibernateTemplate().clear();
			}
		}
	}

}
