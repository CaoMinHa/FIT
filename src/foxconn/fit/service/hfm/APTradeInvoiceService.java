package foxconn.fit.service.hfm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.hfm.APTradeInvoiceDao;
import foxconn.fit.entity.hfm.APTradeInvoice;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class APTradeInvoiceService extends BaseService<APTradeInvoice>{

	@Autowired
	private APTradeInvoiceDao apTradeInvoiceDao;
	
	@Override
	public BaseDaoHibernate<APTradeInvoice> getDao() {
		return apTradeInvoiceDao;
	}
	
	public void saveBatch(List<APTradeInvoice> list,String codeCondition) throws Exception{
		String deleteSql="delete from FIT_AP_Trade_Invoice "+codeCondition;
		
		apTradeInvoiceDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			apTradeInvoiceDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				apTradeInvoiceDao.getHibernateTemplate().flush();
				apTradeInvoiceDao.getHibernateTemplate().clear();
			}
		}
	}

}
