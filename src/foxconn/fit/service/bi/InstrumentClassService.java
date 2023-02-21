package foxconn.fit.service.bi;

import foxconn.fit.util.SecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InstrumentClassService{
    //list去重
    public List<String> removeDuplicate(List<String> list) {
        HashSet h = new HashSet(list);
        list=new ArrayList();
        list.addAll(h);
        return list;
    }
    //找出两个list不同值
    public String getDiffrent(List<String> list1, List<String> list2) {
        list1=this.removeDuplicate(list1);
        list2=this.removeDuplicate(list2);
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

    public List<String> getBudgetSBU(){
        //獲取當前用戶的SBU權限
        List<String> tarList = new ArrayList<String>();
        String corporationCode = SecurityUtils.getCorporationCode();
        if (StringUtils.isNotEmpty(corporationCode)) {
            for (String string : corporationCode.split(",")) {
                tarList.add(string);
            }
        }
        return tarList;
    }

    public String getBudgetSBUStr(){
        //獲取當前用戶的SBU權限
        String tarList = "";
        String corporationCode = SecurityUtils.getCorporationCode();
        if (StringUtils.isNotEmpty(corporationCode)) {
            for (String string : corporationCode.split(",")) {
                tarList+="'"+string+"',";
            }
        }
        if(!tarList.isEmpty()){
            return tarList.substring(0,tarList.length()-1);
        }
        return "'1,1'";
    }

    //預算預測SBU權限sql拼接
    public String querySbuSql(String entity,List<Map> sbuMap){
        String sql="";
        if(StringUtils.isNotEmpty(entity)){
            entity=","+entity+",";
            int i=0;
            sql+=" and ( ENTITY like '0' ";
            for (Map map:sbuMap) {
                if(entity.indexOf(map.get("PARENT").toString())!=-1){
                    sql+=" or ENTITY like '"+map.get("ALIAS").toString()+"%'";
                }
            }
            sql+=")";
        }else{
            if (!sbuMap.isEmpty()){
                sql+=" and (ENTITY like '0' ";
                for (int i = 0; i < sbuMap.size(); i++) {
                    String sbu=sbuMap.get(i).get("ALIAS").toString();
                        sql+=" or ENTITY like '"+sbu+"%'";
                }
                sql+=")";
            }else{
                sql+=" and ENTITY ='0' ";
            }
        }
        return  sql;
    }

    public String mapValString(Object o){
        if(null == o || o.toString().length()==0){
            return "";
        }
        return o.toString();
    }
}
