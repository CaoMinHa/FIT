package foxconn.fit.service.budget;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.budget.BudgetProductBasicDataDao;
import foxconn.fit.entity.budget.BudgetProductBasicData;
import foxconn.fit.service.base.BaseService;


@Service
@Transactional(rollbackFor = Exception.class)

public class BudgetProductBasicDataService extends BaseService<BudgetProductBasicData>{

	@Autowired
	private BudgetProductBasicDataDao BudgetproductbasicDataDao;
	
	@Override
	public BaseDaoHibernate<BudgetProductBasicData> getDao() {
		return BudgetproductbasicDataDao;
	}
	
	public void saveBatch(List<BudgetProductBasicData> list) throws Exception{
		String deleteSql="delete from CUX_BUDGET_PRODUCT_BASIC_DATA where 1=1";
		
		BudgetproductbasicDataDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			BudgetproductbasicDataDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				BudgetproductbasicDataDao.getHibernateTemplate().flush();
				BudgetproductbasicDataDao.getHibernateTemplate().clear();
			}
		}
	}

}
