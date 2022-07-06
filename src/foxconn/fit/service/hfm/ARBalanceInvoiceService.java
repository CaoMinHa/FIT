package foxconn.fit.service.hfm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.hfm.ARBalanceInvoiceDao;
import foxconn.fit.entity.hfm.ARBalanceInvoice;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class ARBalanceInvoiceService extends BaseService<ARBalanceInvoice>{

	@Autowired
	private ARBalanceInvoiceDao arBalanceInvoiceDao;
	
	@Override
	public BaseDaoHibernate<ARBalanceInvoice> getDao() {
		return arBalanceInvoiceDao;
	}
	
	public void saveBatch(List<ARBalanceInvoice> list,String codeCondition) throws Exception{
		String deleteSql="delete from FIT_AR_Balance_Invoice "+codeCondition;
		
		arBalanceInvoiceDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			arBalanceInvoiceDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				arBalanceInvoiceDao.getHibernateTemplate().flush();
				arBalanceInvoiceDao.getHibernateTemplate().clear();
			}
		}
	}

}
