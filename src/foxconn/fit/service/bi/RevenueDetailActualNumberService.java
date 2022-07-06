package foxconn.fit.service.bi;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.bi.RevenueDetailActualNumberDao;
import foxconn.fit.entity.bi.RevenueDetailActualNumber;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class RevenueDetailActualNumberService extends BaseService<RevenueDetailActualNumber>{

	@Autowired
	private RevenueDetailActualNumberDao managementReportDetailDao;
	
	@Override
	public BaseDaoHibernate<RevenueDetailActualNumber> getDao() {
		return managementReportDetailDao;
	}
	
	public void saveBatch(List<RevenueDetailActualNumber> list,String codeCondition) throws Exception{
		String deleteSql="delete from FIT_Revenue_Detail_Actual "+codeCondition;
		
		managementReportDetailDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			managementReportDetailDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				managementReportDetailDao.getHibernateTemplate().flush();
				managementReportDetailDao.getHibernateTemplate().clear();
			}
		}
	}
	
	public void saveExtractData(List<RevenueDetailActualNumber> list,String codeCondition) throws Exception{
		String deleteSql="delete from FIT_Revenue_Detail_Actual "+codeCondition;
		
		managementReportDetailDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			RevenueDetailActualNumber actualNumber = list.get(i);
			BigDecimal rate = BigDecimal.valueOf(Double.parseDouble(actualNumber.getRate()));
			BigDecimal sourceUntaxAmount = BigDecimal.valueOf(Double.parseDouble(actualNumber.getSourceUntaxAmount()));
			actualNumber.setCurrencyUntaxAmount(rate.multiply(sourceUntaxAmount).toString());
			managementReportDetailDao.save(actualNumber);
			
			if ((i+1)%1000==0) {
				managementReportDetailDao.getHibernateTemplate().flush();
				managementReportDetailDao.getHibernateTemplate().clear();
			}
		}
	}
	
}
