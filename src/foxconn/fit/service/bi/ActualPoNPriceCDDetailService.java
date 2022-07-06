package foxconn.fit.service.bi;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.bi.ActualPoNPriceCDDetailDao;
import foxconn.fit.entity.bi.ActualPoNPriceCDDetail;
import foxconn.fit.service.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class ActualPoNPriceCDDetailService extends BaseService<ActualPoNPriceCDDetail> {

    @Autowired
    ActualPoNPriceCDDetailDao actualPoNPriceCDDetailDao;
    @Override
    public BaseDaoHibernate<ActualPoNPriceCDDetail> getDao() {
        return actualPoNPriceCDDetailDao;
    }

    public void saveBatch(List<ActualPoNPriceCDDetail> list, String codeCondition) throws Exception{
        String deleteSql="delete from FIT_ACTUAL_PO_NPRICECD_DTL "+codeCondition;

        actualPoNPriceCDDetailDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();

        for (int i = 0; i < list.size(); i++) {
            actualPoNPriceCDDetailDao.save(list.get(i));

            if ((i+1)%1000==0) {
                actualPoNPriceCDDetailDao.getHibernateTemplate().flush();
                actualPoNPriceCDDetailDao.getHibernateTemplate().clear();
            }
        }
    }
}
