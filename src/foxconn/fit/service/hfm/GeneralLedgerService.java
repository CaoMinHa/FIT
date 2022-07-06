package foxconn.fit.service.hfm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.hfm.GeneralLedgerDao;
import foxconn.fit.entity.hfm.GeneralLedger;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class GeneralLedgerService extends BaseService<GeneralLedger>{

	@Autowired
	private GeneralLedgerDao generalLedgerDao;
	
	@Override
	public BaseDaoHibernate<GeneralLedger> getDao() {
		return generalLedgerDao;
	}
	
	public void saveBatch(List<GeneralLedger> list,String codeCondition) throws Exception{
		String deleteSql="delete from FIT_General_Ledger "+codeCondition;
		
		generalLedgerDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			generalLedgerDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				generalLedgerDao.getHibernateTemplate().flush();
				generalLedgerDao.getHibernateTemplate().clear();
			}
		}
	}

}
