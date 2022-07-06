package foxconn.fit.service.bi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.bi.RevenueTargetByIndustryDao;
import foxconn.fit.entity.bi.RevenueTargetByIndustry;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class RevenueTargetByIndustryService extends BaseService<RevenueTargetByIndustry>{

	@Autowired
	private RevenueTargetByIndustryDao revenueTargetByIndustryDao;
	
	@Override
	public BaseDaoHibernate<RevenueTargetByIndustry> getDao() {
		return revenueTargetByIndustryDao;
	}
	
	public void saveBatch(List<RevenueTargetByIndustry> list,String condition) throws Exception{
		String deleteSql="delete from FIT_Revenue_Target_Industry "+condition;
		
		revenueTargetByIndustryDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			revenueTargetByIndustryDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				revenueTargetByIndustryDao.getHibernateTemplate().flush();
				revenueTargetByIndustryDao.getHibernateTemplate().clear();
			}
		}
	}

}
