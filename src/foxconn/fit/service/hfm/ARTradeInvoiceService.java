package foxconn.fit.service.hfm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.hfm.ARTradeInvoiceDao;
import foxconn.fit.entity.hfm.ARTradeInvoice;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class ARTradeInvoiceService extends BaseService<ARTradeInvoice>{

	@Autowired
	private ARTradeInvoiceDao arTradeInvoiceDao;
	
	@Override
	public BaseDaoHibernate<ARTradeInvoice> getDao() {
		return arTradeInvoiceDao;
	}
	
	public void saveBatch(List<ARTradeInvoice> list,String codeCondition) throws Exception{
		String deleteSql="delete from FIT_AR_Trade_Invoice "+codeCondition;
		
		arTradeInvoiceDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			arTradeInvoiceDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				arTradeInvoiceDao.getHibernateTemplate().flush();
				arTradeInvoiceDao.getHibernateTemplate().clear();
			}
		}
	}

}
