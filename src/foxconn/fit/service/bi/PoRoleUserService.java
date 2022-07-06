package foxconn.fit.service.bi;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.bi.PoRoleUserDao;
import foxconn.fit.entity.bi.PoRoleUser;
import foxconn.fit.service.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
/**
 * @author Yang DaiSheng
 * @program fit
 * @description 采購功能審批流用戶添加角色業務類
 * @create 2021-05-11 15:47
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class PoRoleUserService extends BaseService<PoRoleUser> {

    @Autowired
    private PoRoleUserDao poRoleUserDao;

    @Override
    public BaseDaoHibernate<PoRoleUser> getDao() {
        return poRoleUserDao;
    }


    public void updateData(String updateSql) {
        poRoleUserDao.getSessionFactory().getCurrentSession().createSQLQuery(updateSql).executeUpdate();
    }

    public void addCpoMenu(String roleId,String[] ids){

        for (int i = 0; i < ids.length; i++) {
            String uuId=UUID.randomUUID().toString();
            String sql="insert into FIT_PO_AUDIT_ROLE_USER (ID,ROLE_ID,USER_Id) values ( ";
            sql=sql+"'"+uuId+"',"+"'"+roleId+"',"+"'"+ids[i]+"')";
            poRoleUserDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
            String updateSql=" update fit_user set menus=menus||',poFlow' where id="+"'"+ids[i]+"'";
            poRoleUserDao.getSessionFactory().getCurrentSession().createSQLQuery(updateSql).executeUpdate();
        }

    }
    public void rmoveCpoMenu(String userId,String roleId){
        String sql="delete from FIT_PO_AUDIT_ROLE_USER where user_id="+"'"+userId+"'"+" and role_id="+"'"+roleId+"'";;
        poRoleUserDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
        String updateSql="update fit_user set menus=Replace(menus,',poFlow','') where ID="+"'"+userId+"'";
        poRoleUserDao.getSessionFactory().getCurrentSession().createSQLQuery(updateSql).executeUpdate();
    }

}