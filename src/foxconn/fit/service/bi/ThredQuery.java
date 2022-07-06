package foxconn.fit.service.bi;

import org.springside.modules.orm.PageRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class ThredQuery  implements Callable<Map<Integer,List<Object[]>>> {

    private PageRequest pageRequest;
    private String sql;
    private PoTableService poTableService;

    public ThredQuery(PageRequest pageRequest, String sql,PoTableService poTableService) {
        this.pageRequest = pageRequest;
        this.sql = sql;
        this.poTableService=poTableService;
    }

    @Override
    public Map<Integer,List<Object[]>> call() {
        System.out.print("嘿 进来呀~");
        System.out.print(pageRequest.toString());
        System.out.print("sql:"+sql);
        //通过service查询得到对应结果
        List<Object[]> dataList = poTableService.findPageBySql(pageRequest, sql).getResult();
        Map<Integer,List<Object[]>> map=new HashMap<>();
        map.put(pageRequest.getPageNo(),dataList);
        return map;
    }
}