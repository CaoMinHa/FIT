package foxconn.fit.service.bi;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class InstrumentClassService{
    //list去重
    public List<String> removeDuplicate(List<String> list) {
        HashSet h = new HashSet(list);
        list.clear();
        list.addAll(h);
        return list;
    }
    //找出两个list不同值
    public String getDiffrent(List<String> list1, List<String> list2) {
        String string="";
        for(String str:list1)
        {
            if(!list2.contains(str))
            {
                string+=str+",";
            }
        }
        return string.substring(0,string.length()-1);
    }
}
