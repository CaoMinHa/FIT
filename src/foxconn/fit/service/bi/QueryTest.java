package foxconn.fit.service.bi;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import foxconn.fit.dao.bi.PoTableDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.orm.PageRequest;

@Service
//@Transactional(rollbackFor = Exception.class)
public class QueryTest {
    @Autowired
    private PoTableService poTableService;

    public  Map<Integer,List<Object[]>> getMaxResult(String countSql, String sql,PageRequest pageRequest1) throws Exception{
        long start = System.currentTimeMillis();
        Map<Integer,List<Object[]>> result=new HashMap<>(); //返回结果

        List<Map> countMaps = poTableService.listMapBySql(countSql);
        if(countMaps!=null && !"0".equals(countMaps.get(0).get("COUNT(ENTERNO)").toString())){
            String str=countMaps.get(0).get("COUNT(ENTERNO)").toString();
            long count=Long.parseLong(str);//mydao.getCount(); 通过count查到数据总量
            long num = 10000;    //每次查询的条数
            //需要查询的次数
            long times=count / num;
            if(count%num !=0) {
                times=times+1;
            }
            //开始查询的行数
            long bindex = 0;
            List<Callable<Map<Integer,List<Object[]>>>> tasks = new ArrayList<>();//添加任务
            for(int i = 0; i <times ; i++){
                PageRequest pageRequest=new PageRequest();
                pageRequest.setPageSize(10000);
                pageRequest.setPageNo(i);
                Callable<Map<Integer,List<Object[]>>> qfe = new ThredQuery(pageRequest,sql,poTableService);
                tasks.add(qfe);
                bindex=bindex+num;
            }
            //定义固定长度的线程池  防止线程过多
            ExecutorService execservice = Executors.newFixedThreadPool(15);
            try {
                List<Future<Map<Integer,List<Object[]>>>> futures = execservice.invokeAll(tasks);
                // 处理线程返回结果
                if (futures != null && futures.size() > 0) {
                    for(Future<Map<Integer,List<Object[]>>> future : futures) {
                        result.putAll(future.get());
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                execservice.shutdown();  // 关闭线程池
                long end = System.currentTimeMillis();
                System.out.println("用时"+(start-end));
            }
        }
        return result;
    }
}
