package foxconn.fit.service.bi;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;

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
        if(string.length()>0){
            return string.substring(0,string.length()-1);
        }else{
            return string;
        }
    }

    public String getLanguage(Locale locale, String zh, String en){
        if ("en_US".equals(locale.toString())) {
            return en;
        }else{
            return zh;
        }
    }
}
