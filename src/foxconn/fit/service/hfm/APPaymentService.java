package foxconn.fit.service.hfm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.hfm.APPaymentDao;
import foxconn.fit.entity.hfm.APPayment;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class APPaymentService extends BaseService<APPayment>{

	@Autowired
	private APPaymentDao apPaymentDao;
	
	@Override
	public BaseDaoHibernate<APPayment> getDao() {
		return apPaymentDao;
	}
	
	public void saveBatch(List<APPayment> list,String codeCondition) throws Exception{
		String deleteSql="delete from FIT_AP_Payment "+codeCondition;
		
		apPaymentDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			apPaymentDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				apPaymentDao.getHibernateTemplate().flush();
				apPaymentDao.getHibernateTemplate().clear();
			}
		}
	}
	
}
