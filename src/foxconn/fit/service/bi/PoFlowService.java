package foxconn.fit.service.bi;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.bi.PoFlowDao;
import foxconn.fit.entity.bi.PoFlow;
import foxconn.fit.service.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.Locale;

/**
 * @author Yang DaiSheng
 * @program fit
 * @description
 * @create 2021-04-21 14:27
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class PoFlowService extends BaseService<PoFlow> {

    @Autowired
    private PoFlowDao poFlowDao;
    @Override
    public BaseDaoHibernate<PoFlow> getDao() {
        return poFlowDao;
    }


    public void updateData(String updateSql) {
        poFlowDao.getSessionFactory().getCurrentSession().createSQLQuery(updateSql).executeUpdate();
    }


    public void executeCpo(String year) throws Exception {
        try{
            Connection c = SessionFactoryUtils.getDataSource(poFlowDao.getSessionFactory()).getConnection();
            CallableStatement cs = c.prepareCall("{call fit_po_target_cpo_cd_pkg.main(?)}");
            cs.setString(1, year);
            cs.execute();
            cs.close();
            c.close();
        }catch (Exception e){
           e.printStackTrace();
        }
    }
}