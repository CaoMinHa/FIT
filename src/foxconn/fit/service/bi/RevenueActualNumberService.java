package foxconn.fit.service.bi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.bi.RevenueActualNumberDao;
import foxconn.fit.entity.bi.RevenueActualNumber;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class RevenueActualNumberService extends BaseService<RevenueActualNumber>{

	@Autowired
	private RevenueActualNumberDao revenueActualNumberDao;
	
	@Override
	public BaseDaoHibernate<RevenueActualNumber> getDao() {
		return revenueActualNumberDao;
	}
	
	public void saveBatch(List<RevenueActualNumber> list,String codeCondition) throws Exception{
		String deleteSql="delete from FIT_Revenue_Actual_Number "+codeCondition;
		
		revenueActualNumberDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			revenueActualNumberDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				revenueActualNumberDao.getHibernateTemplate().flush();
				revenueActualNumberDao.getHibernateTemplate().clear();
			}
		}
	}

}
