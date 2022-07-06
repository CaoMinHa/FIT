package foxconn.fit.service.bi;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.bi.PoRoleDao;
import foxconn.fit.entity.bi.PoRole;
import foxconn.fit.service.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Yang DaiSheng
 * @program fit
 * @description
 * @create 2021-04-21 14:27
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class PoRoleService extends BaseService<PoRole> {

    @Autowired
    private PoRoleDao poRoleDao;
    @Override
    public BaseDaoHibernate<PoRole> getDao() {
        return poRoleDao;
    }


    public void updateData(String updateSql) {
        System.out.println(updateSql);
        poRoleDao.getSessionFactory().getCurrentSession().createSQLQuery(updateSql).executeUpdate();
    }

}