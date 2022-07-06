package foxconn.fit.service.bi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.bi.RevenueEstimateByIndustryDao;
import foxconn.fit.entity.bi.RevenueEstimateByIndustry;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class RevenueEstimateByIndustryService extends BaseService<RevenueEstimateByIndustry>{

	@Autowired
	private RevenueEstimateByIndustryDao revenueEstimateByIndustryDao;
	
	@Override
	public BaseDaoHibernate<RevenueEstimateByIndustry> getDao() {
		return revenueEstimateByIndustryDao;
	}
	
	public void saveBatch(List<RevenueEstimateByIndustry> list,String condition) throws Exception{
		String deleteSql="delete from FIT_Revenue_Estimate_Industry "+condition;
		
		revenueEstimateByIndustryDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			revenueEstimateByIndustryDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				revenueEstimateByIndustryDao.getHibernateTemplate().flush();
				revenueEstimateByIndustryDao.getHibernateTemplate().clear();
			}
		}
	}

}
