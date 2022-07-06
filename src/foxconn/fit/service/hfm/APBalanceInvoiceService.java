package foxconn.fit.service.hfm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.hfm.APBalanceInvoiceDao;
import foxconn.fit.entity.hfm.APBalanceInvoice;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class APBalanceInvoiceService extends BaseService<APBalanceInvoice>{

	@Autowired
	private APBalanceInvoiceDao apBalanceInvoiceDao;
	
	@Override
	public BaseDaoHibernate<APBalanceInvoice> getDao() {
		return apBalanceInvoiceDao;
	}
	
	public void saveBatch(List<APBalanceInvoice> list,String codeCondition) throws Exception{
		String deleteSql="delete from FIT_AP_Balance_Invoice "+codeCondition;
		
		apBalanceInvoiceDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			apBalanceInvoiceDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				apBalanceInvoiceDao.getHibernateTemplate().flush();
				apBalanceInvoiceDao.getHibernateTemplate().clear();
			}
		}
	}

}
