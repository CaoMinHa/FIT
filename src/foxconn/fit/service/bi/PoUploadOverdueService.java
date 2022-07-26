package foxconn.fit.service.bi;

import foxconn.fit.dao.bi.PoTableDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author maggao
 */
@Service
public class PoUploadOverdueService {

    @Autowired
    private PoTableDao poTableDao;

    public void updateState(String userId,String type){
        String[] ids = userId.split(",");
        String sql="";
        if("Y".equals(type)){
            for (String s : ids) {
                sql ="insert into FIT_USER_PO_UPLOAD(USER_ID) values("+s+")";
                poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
            }
        }else{
            sql ="update FIT_USER_PO_UPLOAD set STATE='N' where USER_ID in ("+userId+")";
            poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
        }
    }
}