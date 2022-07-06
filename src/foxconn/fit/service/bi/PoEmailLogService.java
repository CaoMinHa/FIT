package foxconn.fit.service.bi;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.entity.bi.PoEmailLog;
import foxconn.fit.service.base.BaseService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

/**
 * @author Yang DaiSheng
 * @program fit
 * @description
 * @create 2021-04-21 14:27
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class PoEmailLogService extends BaseService<PoEmailLog> {
    @Autowired
    private PoTableService poTableService;
    /**
     * @param pageRequest
     * @param title
     * @param name
     * @param date
     * @param dateEnd
     * @return
     */
    public Page<Object[]> selectList(PageRequest pageRequest, String title, String name, String date, String dateEnd){
        pageRequest.setPageSize(14);
        String sql="select CREATED_Name,to_char(CREATION_DATE,'yyyy-mm-dd hh24:mi') as CREATION_DATE,EMAIL_TITLE,EMAIL_TEAM,ID from EPMODS.CUX_PO_EMAIL where 1=1";
        if(!StringUtils.isBlank(title)){
            title="%"+title+"%";
            sql=sql+" and EMAIL_TITLE like "+"'"+title+"'";
        }
        if(!StringUtils.isBlank(date)&&!StringUtils.isBlank(dateEnd)){
            sql=sql+" and CREATION_DATE between  to_date('"+date+"','yyyy-mm-dd hh24:mi') and to_date('"+dateEnd+"','yyyy-mm-dd hh24:mi')";
        }
        if(!StringUtils.isBlank(name)){
            name="%"+name+"%";
            sql=sql+" and CREATED_Name like "+"'"+name+"'";
            sql=sql+" or EMAIL_TEAM like "+"'"+name+"'";
        }
        sql+=" order by id desc ";
        System.out.println(sql);
        Page<Object[]> page = poTableService.findPageBySql(pageRequest, sql);
        return  page;
    }

    public PoEmailLog selectDetails(String Id){
        PoEmailLog poEmailLog=this.selectDetails(Id);
        return poEmailLog;
    }
    @Override
    public BaseDaoHibernate<PoEmailLog> getDao() {
        return null;
    }
}