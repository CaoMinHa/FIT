package foxconn.fit.service.bi;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.bi.PoTableDao;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.bi.PoColumns;
import foxconn.fit.entity.bi.PoKey;
import foxconn.fit.entity.bi.PoTable;
import foxconn.fit.service.base.BaseService;
import foxconn.fit.service.base.UserDetailImpl;
import foxconn.fit.util.DateUtil;
import foxconn.fit.util.ExcelUtil;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetProtection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class PoTableService extends BaseService<PoTable> {

    @Autowired
    private PoTableDao poTableDao;

    @Autowired
    private InstrumentClassService instrumentClassService;

    @Override
    public BaseDaoHibernate<PoTable> getDao() {
        return poTableDao;
    }

    /**
     * 采購页面初始加载
     */
    public Model index(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        model = this.detailsTsak(model, session);
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        UserDetailImpl loginUser = SecurityUtils.getLoginUser();
        String userName = loginUser.getUsername();
        /**获取用户对采购表的操作权限（增删改查）*/
        String permSql = "  select listagg(r.TABLE_PERMS, ',') within GROUP(ORDER BY r.id) as TABLE_PERMS  from  fit_user u \n" +
                " left join FIT_PO_AUDIT_ROLE_USER ur on u.id=ur.user_id \n" +
                " left join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id\n" +
                " where u.username=" + "'" + userName + "'";
        List<String> perms = this.listBySql(permSql);
        String whereSql = "'a',";
        if (perms.get(0) != null && perms.get(0).length() != 0) {
            String perm = perms.get(0);
            String[] berSplit = perm.split(",");
            List list = Arrays.asList(berSplit);
            Set set = new HashSet(list);
            String[] split = (String[]) set.toArray(new String[0]);
            for (String s : split) {
                whereSql += "'" + s + "',";
            }
        }
        whereSql = whereSql.substring(0, whereSql.length() - 1);
        session.setAttribute("dataRange",this.dataRange(userName));
        model = this.selectData(model, userName, whereSql, locale);
        model.addAttribute("commodityMap", this.selectCommodity());
        model.addAttribute("sbuMap", this.selectSBU());
        return model;
    }

    /**
     * 獲取用戶權限SBU 或 commodity
     * */
    public List<String> dataRange(String userName){
        //取值范围 1 采购源 物料大类值集 2 企划员 sbu值集
        List<String> dataRange = new ArrayList<>();
        String userSql = "";
        String roleSql = " select distinct r.code  from  fit_user u \n" +
                " left join FIT_PO_AUDIT_ROLE_USER ur on u.id=ur.user_id \n" +
                " left join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id\n" +
                " WHERE r.grade='1' and u.username='" + userName + "'";
        List<String> roleCode = this.listBySql(roleSql);
        if (null != roleCode && roleCode.size() > 0) {
            String roles = roleCode.get(0) == null ? "" : roleCode.get(0);
            if (roles.equalsIgnoreCase("MM")||roles.equalsIgnoreCase("specialSourcer")) {
                userSql = "select SBU  from fit_user where username='" + userName + "' and sbu is not null " +
                        "union all " +
                        "select listagg(t.NEW_SBU_NAME, ',') within group(order by t.NEW_SBU_NAME) as sbu " +
                        "  from " +
                        "(select distinct tie.NEW_SBU_NAME from bidev.v_if_sbu_mapping tie " +
                        " where exists (select SBU  from fit_user where username='" + userName + "' and sbu is null) " +
                        " ) t ";
            } else if (roles.equalsIgnoreCase("SOURCER")) {
                userSql = " select COMMODITY_MAJOR  from fit_user where username='" + userName + "' and COMMODITY_MAJOR is not null " +
                        "union all " +
                        "select listagg(t.COMMODITY_NAME, ',') within group(order by t.COMMODITY_NAME) as COMMODITY_MAJOR " +
                        "  from " +
                        "(select distinct tie.COMMODITY_NAME from CUX_FUNCTION_COMMODITY_MAPPING tie " +
                        " where exists (select COMMODITY_MAJOR  from fit_user where username='" + userName + "' and COMMODITY_MAJOR is null) " +
                        " ) t ";
            }
        }
        if (!userSql.equalsIgnoreCase("")) {
            List<String> ranges = this.listBySql(userSql);
            String[] split = ranges.get(0).split(",");
            dataRange = Arrays.asList(split);
        }
        return dataRange;
    }

    /**
     * 采購數據保存
     */
    public String update(HttpSession session,MultipartHttpServletRequest multipartHttpServletRequest,Locale locale, AjaxResult result, String tableName) {
        String dataRangeStr = "";
        if (session != null) {
            dataRangeStr = session.getAttribute("dataRange").toString();
        } else {
            result.put("msg", "系统繁忙，请重新登录");
        }
        List<String> subs = this.listBySql("select distinct SBU_NAME from BIDEV.DM_D_ENTITY_SBU where FLAG='1' order by SBU_NAME");
        List<String> commoditys = this.listBySql("select distinct COMMODITY_NAME from CUX_FUNCTION_COMMODITY_MAPPING");
        String sbu = "";
        String commodity = "";
        try {
            Map<String, MultipartFile> mutipartFiles = multipartHttpServletRequest.getFileMap();
            if (mutipartFiles != null && mutipartFiles.size() > 0) {
                MultipartFile file = (MultipartFile) mutipartFiles.values().toArray()[0];
                String suffix = "";
                if (file.getOriginalFilename().lastIndexOf(".") != -1) {
                    suffix = file.getOriginalFilename().substring(
                            file.getOriginalFilename().lastIndexOf(".") + 1,
                            file.getOriginalFilename().length());
                    suffix = suffix.toLowerCase();
                }
                if (!"xls".equals(suffix) && !"xlsx".equals(suffix)) {
                    result.put("flag", "fail");
                    result.put("msg", instrumentClassService.getLanguage(locale, "請上傳正確格式的Excel文件", "The format of excel is error"));
                    return result.getJson();
                }
                Workbook wb = null;
                if ("xls".equals(suffix)) {
                    wb = new HSSFWorkbook(file.getInputStream());
                } else {
                    wb = new XSSFWorkbook(file.getInputStream());
                }
                wb.close();
                PoTable poTable = this.get(tableName);
                List<PoColumns> columns = poTable.getColumns();
                int COLUMN_NUM = columns.size();
                Sheet sheet = wb.getSheetAt(0);
                Row firstRow = sheet.getRow(0);
                Assert.notNull(firstRow, instrumentClassService.getLanguage(locale, "第一行為標題行，不允許為空", "The first action is the header line, which cannot be empty"));
                int columnNum = firstRow.getPhysicalNumberOfCells();
                if (columnNum < COLUMN_NUM) {
                    result.put("flag", "fail");
                    result.put("msg", instrumentClassService.getLanguage(locale, "列數不能小於" + COLUMN_NUM, "The number of columns cannot be less than " + COLUMN_NUM));
                    return result.getJson();
                }
                int rowNum = sheet.getPhysicalNumberOfRows();
                if (rowNum < 3) {
                    result.put("flag", "fail");
                    result.put("msg", instrumentClassService.getLanguage(locale, "檢測到沒有行數據", "No row data detected"));
                    return result.getJson();
                }
                List<List<String>> dataList = new ArrayList<List<String>>();
                UserDetailImpl loginUser = SecurityUtils.getLoginUser();
                List<BigDecimal> countList = (List<BigDecimal>)poTableDao.listBySql("select count(1) from  fit_user u inner join FIT_PO_AUDIT_ROLE_USER ur on u.id=ur.user_id inner join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id WHERE r.code='specialSourcer' and u.username='"+loginUser.getUsername()+"'");
                for (int j = 2; j < rowNum; j++) {
                    if (null == sheet.getRow(j)) {
                        continue;
                    }
                    Row row = sheet.getRow(j);
                    int n = 0;
                    List<String> data = new ArrayList<String>();
                    while (n < COLUMN_NUM) {
                        PoColumns column = columns.get(n);
                        /**字段不能爲空*/
                        if (column.getNullable() == false) {
                            if (StringUtils.isEmpty(ExcelUtil.getCellStringValue(row.getCell(n), 0, j))) {
                                result.put("flag", "fail");
                                result.put("msg", instrumentClassService.getLanguage(locale, "第" + (j + 1) + "行第" + (n + 1) + "列單元格內容不能為空", "The contents of the cell in row " + (j + 1) + " column " + (n + 1) + " cannot be empty"));
                                return result.getJson();
                            }
                        }
                        /**數據校驗*/
                        if (column.getDataType().equalsIgnoreCase("date")) {
                            try {
                                if(column.getNullable()==false){
                                    data.add(DateUtil.formatByddSMMSyyyy(ExcelUtil.getCellDateValue(row.getCell(n), DateUtil.SDF_ddSMMSyyyy)));
                                }else{
                                    Date date2 = ExcelUtil.getCellDateValue(row.getCell(n), DateUtil.SDF_ddSMMSyyyy);
                                    data.add(date2 != null?DateUtil.formatByddSMMSyyyy(date2):"");
                                }
                            } catch (Exception e) {
                                result.put("flag", "fail");
                                result.put("msg", instrumentClassService.getLanguage(locale, "第" + (j + 1) + "行第" + (n + 1) + "列日期格式錯誤", "The format of date in row " + (j + 1) + " column " + (n + 1) + " is error"));
                                return result.getJson();
                            }
                        } else {
                            String value = ExcelUtil.getCellStringValue(row.getCell(n), 0, j);
                            if (column.getDataType().equalsIgnoreCase("number")) {
                                try {
                                    if(column.getNullable()!=false){
                                        if (null==value||"".equalsIgnoreCase(value.trim())) {
                                            value = "0";
                                        }
                                    }
                                    Double.parseDouble(value);
                                } catch (Exception e) {
                                    result.put("flag", "fail");
                                    result.put("msg", instrumentClassService.getLanguage(locale, "第" + (j + 1) + "行第" + (n + 1) + "列單元格數字格式錯誤【" + value + "】", "The number format of the cell in row " + (j + 1) + " column " + (n + 1) + " is error)"));
                                    return result.getJson();
                                }
                            }
                            value = value.replaceAll("'", "''");
                            if ("SBU".equalsIgnoreCase(column.getColumnName())) {
                                sbu = value;
                                if (!subs.contains(sbu)) {
                                    result.put("flag", "fail");
                                    result.put("msg", instrumentClassService.getLanguage(locale, "第" + (j + 1) + "行第" + (n + 1) + "列單元格sbu输入錯誤【" + value + "】", "The sbu of the cell in row " + (j + 1) + " column " + (n + 1) + " is error)"));
                                    return result.getJson();
                                }
                               if ("FIT_PO_SBU_YEAR_CD_SUM".equalsIgnoreCase(tableName)||countList.get(0).intValue()>0) {
                                    if (!dataRangeStr.contains(sbu)) {
                                        result.put("flag", "fail");
                                        result.put("msg", instrumentClassService.getLanguage(locale, "第" + (j + 1) + "行第" + (n + 1) + "列單元格sbu输入錯誤【" + value + "】,用户没有维护该sbu的权限", "The sbu of the cell in row " + (j + 1) + " column " + (n + 1) + " is error)"));
                                        return result.getJson();
                                    }
                                }
                            }
                            if ("COMMODITY_MAJOR".equalsIgnoreCase(column.getColumnName()) || "COMMODITY".equalsIgnoreCase(column.getColumnName())) {
                                commodity = value;
                                if (!commoditys.contains(commodity)) {
                                    result.put("flag", "fail");
                                    result.put("msg", instrumentClassService.getLanguage(locale, "第" + (j + 1) + "行第" + (n + 1) + "列單元格commodity输入錯誤【" + value + "】", "The commodity of the cell in row " + (j + 1) + " column " + (n + 1) + " is error)"));
                                    return result.getJson();
                                }
                                if (!"FIT_PO_SBU_YEAR_CD_SUM".equalsIgnoreCase(tableName)&&countList.get(0).intValue()<1) {
                                    if (!dataRangeStr.contains(commodity)) {
                                        result.put("flag", "fail");
                                        result.put("msg", instrumentClassService.getLanguage(locale, "第" + (j + 1) + "行第" + (n + 1) + "列單元格commodity输入錯誤【" + value + "】,用户没有维护该commodity的权限", "The commodity of the cell in row " + (j + 1) + " column " + (n + 1) + " is error)"));
                                        return result.getJson();
                                    }
                                }
                            }
                            if ("month".equalsIgnoreCase(column.getColumnName())) {
                                if (value.length() < 2) {
                                    value = "0" + value;
                                }
                            }
                            if ("PRICE_CONTROL".equalsIgnoreCase(column.getColumnName())) {
                                if (!"非客指".equals(value) && !"客指".equals(value)) {
                                    result.put("flag", "fail");
                                    result.put("msg", instrumentClassService.getLanguage(locale, "第" + (j + 1) + "行第" + (n + 1) + "列單元格是否客指输入錯誤【" + value + "】", "The PRICE_CONTROL of the cell in  row " + (j + 1) + " column " + (n + 1) + " is error)"));
                                    return result.getJson();
                                }
                            }
                            data.add(value);
                        }
                        n++;
                    }
                    dataList.add(data);
                }
                if (!dataList.isEmpty()) {
                    if(countList.get(0).intValue()>0&&poTable.getTableName()!="FIT_PO_SBU_YEAR_CD_SUM"){
                        return special(poTable,dataList,result,locale);
                    }
                    return repetition(poTable,dataList,result,locale);
                } else {
                    result.put("flag", "fail");
                    result.put("msg", instrumentClassService.getLanguage(locale, "無有效數據行", "There is no valid data row"));
                }
            } else {
                result.put("flag", "fail");
                result.put("msg", instrumentClassService.getLanguage(locale, "對不起，未接收到上傳的文件", "Uploaded file not received"));
            }
        } catch (Exception e) {
            result.put("flag", "fail");
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }
        return result.getJson();
    }

    public AjaxResult checkDate(String tableName,String date,Locale locale,AjaxResult result){
        /**获取当前时间往后推60天*/
        LocalDate localDate = LocalDate.now();
        String year=localDate.plus(60, ChronoUnit.DAYS).toString().substring(0,4);
        String period = localDate.minusMonths(1).toString().substring(5, 7);
        /**上传管控*/
        if ("FIT_PO_SBU_YEAR_CD_SUM".equalsIgnoreCase(tableName)) {
            //測試後續放開
            if (!this.updateState(tableName,date)) {
                Assert.isTrue(year.equals(date), instrumentClassService.getLanguage(locale, "錯誤的年份： " + date + "應為：" + year, "The year is error:" + date + "should be：" + year));
            }
        } else if ("FIT_PO_CD_MONTH_DTL".equalsIgnoreCase(tableName)) {
            //測試後續放開
            if (!this.updateState(tableName,date)) {
                Assert.isTrue(year.equals(date), instrumentClassService.getLanguage(locale, "錯誤的年份： " + date + "應為：" + year, "The year is error:" + date + "should be：" + year));
            }
            if (!this.checkCPO(date)) {
                result.put("flag", "fail");
                result.put("msg", instrumentClassService.getLanguage(locale, "採購CD 目標CPO核准還未完成審批，暫無法上傳數據。", "The CPO approval of the purchase CD target has not been completed, so the data cannot be uploaded temporarily"));
                return result;
            }
        } else {
            if (!this.updateState(tableName,date)) {
                year = localDate.minusMonths(1).toString().substring(0, 4);
                if ("FIT_ACTUAL_PO_NPRICECD_DTL".equalsIgnoreCase(tableName) || "FIT_PO_BUDGET_CD_DTL".equalsIgnoreCase(tableName)) {
                    if (Integer.parseInt(localDate.toString().substring(8, 10)) > 10) {
                        result.put("flag", "fail");
                        result.put("msg", instrumentClassService.getLanguage(locale, "上傳時間為每月1-10號，現已逾期，請聯係管理員。", "The upload time is from the 1st to the 10th of each month, it is overdue, please contact the administrator"));
                        return result;
                    }
                }
                Assert.isTrue((year + period).equals(date), instrumentClassService.getLanguage(locale, "錯誤的月份： " + date + "應為：" + (year + period), "The year，period is error:" + date + "should be：" + (year + period)));
            }
        }
        return result;
    }



    /**
     * 遍历数组，去重
     * tableName 表格名决定key
     * dataList 之前的数据
     * data 本次数据
     */
    private String repetition(PoTable poTable, List<List<String>> dataList, AjaxResult result,Locale locale) throws Exception {
        List<String> listVal=new ArrayList<>();
        Set<String> list=new HashSet<>();
        for (int i=0;i<dataList.size();i++) {
            switch (poTable.getTableName()){
                case "FIT_PO_SBU_YEAR_CD_SUM"://SBU年度CD目標匯總表
                    listVal.add(dataList.get(i).get(0)+"_"+dataList.get(i).get(2)+"_"+dataList.get(i).get(3)+"_"+dataList.get(i).get(4));
                    list.add(dataList.get(i).get(0)+"_"+dataList.get(i).get(3)+"_"+poTable.getComments().split("_")[1]);
                    break;
                case "FIT_PO_CD_MONTH_DTL"://採購CD目標by月展開表
                    listVal.add(dataList.get(i).get(0)+"_"+dataList.get(i).get(1)+"_"+dataList.get(i).get(2)+"_"+dataList.get(i).get(3)+"_"+dataList.get(i).get(4));
                    list.add(dataList.get(i).get(0)+"_"+dataList.get(i).get(2)+"_"+poTable.getComments().split("_")[1]);
                    break;
                default://採購CD手動匯總表 實際採購非價格CD匯總表
                    listVal.add(dataList.get(i).get(0)+dataList.get(i).get(1)+"_"+dataList.get(i).get(3)+"_"+dataList.get(i).get(5)+"_"+dataList.get(i).get(6));
                    list.add(dataList.get(i).get(0)+dataList.get(i).get(1)+"_"+dataList.get(i).get(3)+"_"+poTable.getComments().split("_")[1]);
                    break;
            }
        }
        List<String> collect = listVal.stream().filter(i -> i != "")               // list 对应的 Stream 并过滤""
                .collect(Collectors.toMap(e -> e, e -> 1, Integer::sum)) // 获得元素出现频率的 Map，键为元素，值为元素出现的次数
                .entrySet()
                .stream()                       // 所有 entry 对应的 Stream
                .filter(e -> e.getValue() > 1)         // 过滤出元素出现次数大于 1 (重复元素）的 entry
                .map(Map.Entry::getKey)                // 获得 entry 的键（重复元素）对应的 Stream
                .collect(Collectors.toList());
        String nameList="";
        String msg="";
        if(collect.isEmpty()){
                for (String str:list) {
                    result=this.checkDate(poTable.getTableName(),str.split("_")[0],locale,result);
                    if(result.getResult().get("flag").equals("fail")){
                        return result.getJson();
                    }
                    List<List<String>> dataList1=new ArrayList<>();
                    for (List<String> data:dataList) {
                        switch (poTable.getTableName()){
                            case "FIT_PO_SBU_YEAR_CD_SUM"://SBU年度CD目標匯總表
                                if(str.equals(data.get(0)+"_"+data.get(3)+"_"+poTable.getComments().split("_")[1])){
                                    dataList1.add(data);
                                }
                                break;
                            case "FIT_PO_CD_MONTH_DTL"://採購CD目標by月展開表
                                if(str.equals(data.get(0)+"_"+data.get(2)+"_"+poTable.getComments().split("_")[1])){
                                    dataList1.add(data);
                                }
                                break;
                            default://採購CD手動匯總表 實際採購非價格CD匯總表
                                if(str.equals(data.get(0)+data.get(1)+"_"+data.get(3)+"_"+poTable.getComments().split("_")[1])){
                                    dataList1.add(data);
                                }
                                break;
                        }
                    }
                    String taskId = this.savePoData(poTable,dataList1,str);
                    if ("".equals(taskId)) {
                        nameList+=str+",";
                    } else{
                        if ("FIT_PO_CD_MONTH_DTL".equalsIgnoreCase(poTable.getTableName())&&!this.validateMonth(taskId).equals("Y")) {
                            msg+= this.validateMonth(taskId)+",";
                        }
                    }
                }
                if(!msg.isEmpty()){
                    result.put("flag", "fail");
                    result.put("msg", msg.substring(0,msg.length()-1) + "配置的CD比例過低,請重新維護上傳");
                }
                if(!nameList.isEmpty()){
                    result.put("flag", "fail");
                    result.put("msg", nameList.substring(0,nameList.length()-1)+"該維度的數據已存在，不能重複上傳");
                }

        }else{
            result.put("flag", "fail");
            result.put("msg", instrumentClassService.getLanguage(locale, "上傳失敗，有重複維度數據："+collect.toString(), "Upload failed, Duplicate dimension data："+collect.toString()));
            return result.getJson();
        }
        return result.getJson();
    }

    /**采购特殊任务*/
    private String special(PoTable poTable, List<List<String>> dataList, AjaxResult result,Locale locale) throws Exception {
        List<String> listVal=new ArrayList<>();
        Set<String> list=new HashSet<>();
        for (int i=0;i<dataList.size();i++) {
            switch (poTable.getTableName()){
                case "FIT_PO_CD_MONTH_DTL"://採購CD目標by月展開表
                    listVal.add(dataList.get(i).get(0)+"_"+dataList.get(i).get(1)+"_"+dataList.get(i).get(2)+"_"+dataList.get(i).get(3)+"_"+dataList.get(i).get(4));
                    list.add(dataList.get(i).get(0)+"_"+dataList.get(i).get(3)+"_"+poTable.getComments().split("_")[1]);
                    break;
                default://採購CD手動匯總表 實際採購非價格CD匯總表
                    listVal.add(dataList.get(i).get(0)+dataList.get(i).get(1)+"_"+dataList.get(i).get(3)+"_"+dataList.get(i).get(5)+"_"+dataList.get(i).get(6));
                    list.add(dataList.get(i).get(0)+dataList.get(i).get(1)+"_"+dataList.get(i).get(5)+"_"+poTable.getComments().split("_")[1]);
                    break;
            }
        }
        List<String> collect = listVal.stream().filter(i -> i != "")               // list 对应的 Stream 并过滤""
                .collect(Collectors.toMap(e -> e, e -> 1, Integer::sum)) // 获得元素出现频率的 Map，键为元素，值为元素出现的次数
                .entrySet()
                .stream()                       // 所有 entry 对应的 Stream
                .filter(e -> e.getValue() > 1)         // 过滤出元素出现次数大于 1 (重复元素）的 entry
                .map(Map.Entry::getKey)                // 获得 entry 的键（重复元素）对应的 Stream
                .collect(Collectors.toList());
        String nameList="";
        String msg="";
        if(collect.isEmpty()){
            for (String str:list) {
                result=this.checkDate(poTable.getTableName(),str.split("_")[0],locale,result);
                if(result.getResult().get("flag").equals("fail")){
                    return result.getJson();
                }
                List<List<String>> dataList1=new ArrayList<>();
                for (List<String> data:dataList) {
                    switch (poTable.getTableName()){
                        case "FIT_PO_CD_MONTH_DTL"://採購CD目標by月展開表
                            if(str.equals(data.get(0)+"_"+data.get(3)+"_"+poTable.getComments().split("_")[1])){
                                dataList1.add(data);
                            }
                            break;
                        default://採購CD手動匯總表 實際採購非價格CD匯總表
                            if(str.equals(data.get(0)+data.get(1)+"_"+data.get(5)+"_"+poTable.getComments().split("_")[1])){
                                dataList1.add(data);
                            }
                            break;
                    }
                }
                String taskId = this.savePoData(poTable,dataList1,str);
                if ("".equals(taskId)) {
                    nameList+=str+",";
                } else{
                    if ("FIT_PO_CD_MONTH_DTL".equalsIgnoreCase(poTable.getTableName())&&!this.validateMonth(taskId).equals("Y")) {
                        msg+= this.validateMonth(taskId)+",";
                    }
                }
            }
            if(!msg.isEmpty()){
                result.put("flag", "fail");
                result.put("msg", msg.substring(0,msg.length()-1) + "配置的CD比例過低,請重新維護上傳");
            }
            if(!nameList.isEmpty()){
                result.put("flag", "fail");
                result.put("msg", nameList.substring(0,nameList.length()-1)+"該維度的數據已存在，不能重複上傳");
            }

        }else{
            result.put("flag", "fail");
            result.put("msg", instrumentClassService.getLanguage(locale, "上傳失敗，有重複維度數據："+collect.toString(), "Upload failed, Duplicate dimension data："+collect.toString()));
            return result.getJson();
        }
        return result.getJson();
    }

    /**
     * 采購下載模板
     */
    public String template(HttpServletRequest request, String tableName, Locale locale) throws Exception {
        XSSFWorkbook workBook = new XSSFWorkbook();
        XSSFCellStyle titleStyle = workBook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setFillForegroundColor(IndexedColors.BLACK.index);
        titleStyle.setBorderBottom(BorderStyle.THIN);
        titleStyle.setBorderColor(XSSFCellBorder.BorderSide.BOTTOM, new XSSFColor(new java.awt.Color(255, 255, 255)));

        XSSFCellStyle lockStyle = workBook.createCellStyle();
        lockStyle.setLocked(true);
        lockStyle.setAlignment(HorizontalAlignment.CENTER);
        lockStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(217, 217, 217)));
        lockStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFCellStyle unlockStyle = workBook.createCellStyle();
        unlockStyle.setAlignment(HorizontalAlignment.CENTER);

        XSSFFont font = workBook.createFont();
        font.setColor(IndexedColors.WHITE.index);
        font.setBold(true);
        titleStyle.setFont(font);
        //	单元格不锁定的样式
        XSSFCellStyle unlockstyle = workBook.createCellStyle();
        unlockstyle.setLocked(false);
        PoTable poTable = this.get(tableName);
        List<PoColumns> columns = poTable.getColumns();
        XSSFSheet sheet = workBook.createSheet(instrumentClassService.getByLocale(locale, poTable.getComments()));
        sheet.setDefaultColumnStyle(0, unlockStyle);
        sheet.setDefaultColumnStyle(1, unlockStyle);
        sheet.setDefaultColumnStyle(2, unlockStyle);
        sheet.setDefaultColumnStyle(3, unlockStyle);
        sheet.setDefaultColumnStyle(4, unlockStyle);

        sheet.createFreezePane(0, 2, 0, 2);
        int rowIndex = 0;
        Row row = sheet.createRow(rowIndex++);
        Row row1 = sheet.createRow(rowIndex++);
        for (int i = 0; i < columns.size(); i++) {
            String comments = columns.get(i).getComments();
            String examples = columns.get(i).getExamples();
            comments = instrumentClassService.getByLocale(locale, comments);
            Cell cell = row.createCell(i);
            Cell cell1 = row1.createCell(i);
            cell.setCellValue(comments);
            cell1.setCellValue(examples);
            cell.setCellStyle(titleStyle);
            cell1.setCellStyle(titleStyle);
            sheet.setColumnWidth(i, comments.getBytes("GBK").length * 300 + 300);
            if (null != examples && comments.getBytes("GBK").length < examples.getBytes("GBK").length) {
                sheet.setColumnWidth(i, examples.getBytes("GBK").length * 300 + 300);
            }
        }
        sheet.enableLocking();
        CTSheetProtection sheetProtection = sheet.getCTWorksheet().getSheetProtection();
        sheetProtection.setSelectLockedCells(false);
        sheetProtection.setSelectUnlockedCells(false);
        sheetProtection.setFormatCells(true);
        sheetProtection.setFormatColumns(true);
        sheetProtection.setFormatRows(true);
        sheetProtection.setInsertColumns(true);
        sheetProtection.setInsertRows(false);
        sheetProtection.setInsertHyperlinks(true);
        sheetProtection.setDeleteColumns(true);
        sheetProtection.setDeleteRows(true);
        sheetProtection.setSort(false);
        sheetProtection.setAutoFilter(false);
        sheetProtection.setPivotTables(true);
        sheetProtection.setObjects(true);
        sheetProtection.setScenarios(true);
        //统一先去掉表锁定
        for (short i = 0; i < columns.size(); i++) {
            sheet.setDefaultColumnStyle(i, unlockstyle);
        }
        //置灰一些表字段
        if ("FIT_ACTUAL_PO_NPRICECD_DTL".equalsIgnoreCase(tableName)) {
            sheet.setDefaultColumnStyle(2, lockStyle);
            sheet.setDefaultColumnStyle(4, lockStyle);
            sheet.setDefaultColumnStyle(14, lockStyle);
            sheet.setDefaultColumnStyle(20, lockStyle);
            sheet.setDefaultColumnStyle(21, lockStyle);
            sheet.setDefaultColumnStyle(22, lockStyle);
        } else if ("FIT_PO_SBU_YEAR_CD_SUM".equalsIgnoreCase(tableName)) {
            sheet.setDefaultColumnStyle(1, lockStyle);
            sheet.setDefaultColumnStyle(14, lockStyle);
        } else if ("FIT_PO_BUDGET_CD_DTL".equalsIgnoreCase(tableName)) {
            sheet.setDefaultColumnStyle(2, lockStyle);
            sheet.setDefaultColumnStyle(4, lockStyle);
            sheet.setDefaultColumnStyle(9, lockStyle);
        }

        this.sbuCommodity(workBook, titleStyle);
        //獲取實際表名
        String tableNameSql = "select * from fit_po_table";
        List<PoTable> list = this.listBySql(tableNameSql, PoTable.class);
        String fileName = tableName;
        for (int i = 0; i < list.size(); i++) {
            if (fileName.equalsIgnoreCase(list.get(i).getTableName())) {
                fileName = list.get(i).getComments().split("_")[1];
                break;
            }
        }
        File outFile = new File(request.getRealPath("") + File.separator + "static" + File.separator + "download/" + fileName + ".xlsx");
        OutputStream out = new FileOutputStream(outFile);
        workBook.write(out);
        workBook.close();
        out.flush();
        out.close();
        return outFile.getName();
    }

    /**
     * 下載模板sheet頁2顯示主數據
     */
    public void sbuCommodity(XSSFWorkbook workBook, XSSFCellStyle titleStyle) {
        List<String> commodityList = this.listBySql("select distinct tie.COMMODITY_NAME from CUX_FUNCTION_COMMODITY_MAPPING tie order by tie.COMMODITY_NAME");
        List<String> sbuList = this.listBySql("select distinct SBU_NAME from BIDEV.DM_D_ENTITY_SBU where FLAG='1' order by SBU_NAME");
        Sheet sheet = workBook.createSheet("Commodity大類和SBU");
        Row titleRow = sheet.createRow(0);
        Cell cell = titleRow.createCell(0);
        Cell cellt = titleRow.createCell(1);
        cell.setCellStyle(titleStyle);
        cellt.setCellStyle(titleStyle);
        cell.setCellValue("Commodity Major");
        cellt.setCellValue("SBU");
        int n = 1;
        if (sbuList.size() >= commodityList.size()) {
            for (int i = 0; i < sbuList.size(); i++) {
                String commodity = "";
                String sbu = sbuList.get(i);
                if (i < commodityList.size()) {
                    commodity = commodityList.get(i);
                }
                Row row = sheet.createRow(n);
                Cell cell1 = row.createCell(0);
                Cell cell2 = row.createCell(1);
                cell1.setCellValue(commodity);
                cell2.setCellValue(sbu);
                n++;
            }
        } else {
            for (int i = 0; i < commodityList.size(); i++) {
                String sbu = "";
                if (n <= sbuList.size()) {
                    sbu = sbuList.get(i);
                }
                String commodity = commodityList.get(i);
                Row row = sheet.createRow(n);
                Cell cell1 = row.createCell(0);
                Cell cell2 = row.createCell(1);
                cell1.setCellValue(commodity);
                cell2.setCellValue(sbu);
                n++;
            }
        }
    }

    /**
     * 生成采购下载文件
     * */
    public String download(HttpServletRequest request, PageRequest pageRequest,
                   String dateYear,
                   String date, String dateEnd, String tableName,String flag,
                   String poCenter, String sbuVal, String priceControl,String commodity,String founderVal) throws Exception {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        XSSFWorkbook workBook = new XSSFWorkbook();
        XSSFCellStyle titleStyle = workBook.createCellStyle();
        XSSFCellStyle a = workBook.createCellStyle();
        XSSFDataFormat format=workBook.createDataFormat();
        a.setDataFormat(format.getFormat("_ * #,##0.00_ ;_ * -#,##0.00_ ;_ * \"-\"??_ ;_ @_ "));
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setFillForegroundColor(IndexedColors.BLACK.index);
        XSSFFont font = workBook.createFont();
        font.setColor(IndexedColors.WHITE.index);
        font.setBold(true);
        titleStyle.setFont(font);
        SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(workBook);
        tableName=tableName.equals("FIT_PO_CD_MONTH_DTL")?"FIT_PO_CD_MONTH_DOWN":tableName;
        PoTable poTable = get(tableName);
        Sheet sheet = sxssfWorkbook.createSheet(instrumentClassService.getByLocale(locale, poTable.getComments()));
        List<PoColumns> columns = poTable.getColumns();
        List<Integer> lockSerialList = new ArrayList<Integer>();
        String sql = "select ";
        sheet.createFreezePane(0, 1, 0, 1);
        Row titleRow = sheet.createRow(0);
        List<Integer> numberList = new ArrayList<Integer>();
        for (int i = 0; i < columns.size(); i++) {
            PoColumns poColumn = columns.get(i);
            String columnName = poColumn.getColumnName();
            String comments = poColumn.getComments();
            comments = instrumentClassService.getByLocale(locale, comments);
            if (poColumn.getLocked()) {
                lockSerialList.add(poColumn.getSerial());
            }
            if (poColumn.getDataType().equalsIgnoreCase("number")) {
                //改成原样输出
                sql += columnName + ",";
                numberList.add(i);
            } else if (poColumn.getDataType().equalsIgnoreCase("date")) {
                sql += "to_char(" + columnName + ",'dd/mm/yyyy'),";
            } else {
                sql += columnName + ",";
            }
            Cell cell = titleRow.createCell(i);
            cell.setCellValue(comments);
            cell.setCellStyle(titleStyle);
            sheet.setColumnWidth(i, comments.getBytes("GBK").length * 256 + 400);
        }
        if(tableName.equals("FIT_PO_Target_CPO_CD_DTL")){
            sql="select YEAR,PO_CENTER,COMMODITY_MAJOR ,NO_PO_TOTAL,NO_CD_AMOUNT,NO_CPO,PO_TOTAL ,CD_AMOUNT,CPO from FIT_PO_TARGET_CPO_CD_DTL_V1 where YEAR='"+dateYear+"'";
        }else {
            sql = sql.substring(0, sql.length() - 1) + " from " + tableName + this.whereSql(poTable, columns, locale, dateYear, date, dateEnd, flag, poCenter, sbuVal, priceControl, commodity, founderVal);
        }
        pageRequest.setPageSize(ExcelUtil.PAGE_SIZE);
        pageRequest.setPageNo(1);
        List<Object[]> dataList = this.findPageBySql(pageRequest, sql).getResult();
        if (CollectionUtils.isNotEmpty(dataList)) {
            int rowIndex = 1;
            for (Object[] objects : dataList) {
                Row contentRow = sheet.createRow(rowIndex++);
                for (int i = 0; i < objects.length; i++) {
                    Cell cell = contentRow.createCell(i);
                    String text = (objects[i] != null ? objects[i].toString() : "");
                    if (StringUtils.isNotEmpty(text) && numberList.contains(i)) {
                        cell.setCellStyle(a);
                        cell.setCellValue(Double.parseDouble(text));
                    } else {
                        cell.setCellValue(text);
                    }
                }
            }

            while (dataList != null && dataList.size() >= ExcelUtil.PAGE_SIZE) {
                pageRequest.setPageNo(pageRequest.getPageNo() + 1);
                dataList = this.findPageBySql(pageRequest, sql).getResult();
                if (CollectionUtils.isNotEmpty(dataList)) {
                    for (Object[] objects : dataList) {
                        Row contentRow = sheet.createRow(rowIndex++);
                        for (int i = 0; i < objects.length-1; i++) {
                            Cell cell = contentRow.createCell(i);
                            String text = (objects[i] != null ? objects[i].toString() : "");
                            if (StringUtils.isNotEmpty(text) && numberList.contains(i)) {
                                cell.setCellStyle(a);
                                cell.setCellValue(Double.parseDouble(text));
                            } else {
                                cell.setCellValue(text);
                            }
                        }
                    }
                }
            }
        }
        String fileName = tableName;
        String tableNameSql = "select * from fit_po_table";
        List<PoTable> listName = this.listBySql(tableNameSql, PoTable.class);
        for (int i = 0; i < listName.size(); i++) {
            if (fileName.equalsIgnoreCase(listName.get(i).getTableName())) {
                fileName = listName.get(i).getComments().split("_")[1];
                break;
            }
        }

        File outFile = new File(request.getRealPath("") + File.separator + "static" + File.separator + "download" + File.separator + fileName + ".xlsx");
        OutputStream out = new FileOutputStream(outFile);
        sxssfWorkbook.write(out);
        sxssfWorkbook.close();
        out.flush();
        out.close();
        System.gc();
        return outFile.getName();
    }

    /**
     * 下载sql
     */
    public String whereSql(PoTable poTable,List<PoColumns> columns , Locale locale, String dateYear, String date, String dateEnd, String flag,
                           String poCenter, String sbuVal, String priceControl, String commodity, String founderVal){
        String whereSql=" where 1=1 ";
        if (StringUtils.isNotEmpty(date) && StringUtils.isNotEmpty(dateEnd)) {
            Date d = DateUtil.parseByYyyy_MM(date);
            Assert.notNull(d, instrumentClassService.getLanguage(locale, "年月格式錯誤", "The format of year/month is error"));
            StringBuffer str;
            date=date.replace("-","");
            dateEnd=dateEnd.replace("-","");
            if (date.length() < 6) {
                str=new StringBuffer(date);
                date=str.insert(4,"0").toString();
            }
            if (dateEnd.length() < 6) {
                str=new StringBuffer(dateEnd);
                dateEnd=str.insert(4,"0").toString();
            }
            whereSql += " and " + columns.get(0).getColumnName() +"||"+ columns.get(1).getColumnName() + ">=" + date
                    + " and " + columns.get(0).getColumnName() +"||"+ columns.get(1).getColumnName() + "<=" + dateEnd;
        }
        if (StringUtils.isNotEmpty(poCenter)) {
            whereSql += " and " + columns.get(2).getColumnName() + "='" + poCenter + "'";
        }
        switch (poTable.getTableName()){
            //採購CD手動匯總表是否客指
            case "FIT_PO_BUDGET_CD_DTL":
                if (StringUtils.isNotEmpty(priceControl)) {
                    whereSql += " and PRICE_CONTROL ='"+priceControl+"' ";
                }
                break;
            //實際採購非價格CD匯總表增加創建人篩選條件
            case "FIT_ACTUAL_PO_NPRICECD_DTL":
                if (StringUtils.isNotEmpty(founderVal)) {
                    whereSql += " and TASK_ID in( select ID from FIT_PO_TASK where CREATE_USER ='" + founderVal + "') ";
                }
                break;
            //採購CD目標by月展開表
            case "FIT_PO_CD_MONTH_DOWN":
                whereSql = " where 1=1";
                whereSql += " and year= " + dateYear;
                if (StringUtils.isNotEmpty(priceControl)) {
                    whereSql += " and  PRICE_CONTROL='" + priceControl + "'";
                }
                break;
            //SBU年度CD目標匯總表
            case "FIT_PO_SBU_YEAR_CD_SUM":
                whereSql = " where 1=1 and PO_CENTER not in('Buy-sell','資訊採購')";
                if (StringUtils.isNotEmpty(dateYear)) {
                    whereSql += " and " + columns.get(0).getColumnName() + "='" + dateYear + "'";
                }
                if(poCenter.equals("Buy-sell")||poCenter.equals("資訊採購")){
                    whereSql += " and " + columns.get(1).getColumnName() + "='1'";
                }else if (StringUtils.isNotEmpty(poCenter)) {
                    whereSql += " and " + columns.get(1).getColumnName() + "='" + poCenter + "'";
                }
                if (StringUtils.isNotEmpty(priceControl)) {
                    whereSql += " and  PRICE_CONTROL='" + priceControl + "'";
                }
                break;
        }

        if(null!=commodity && !"".equalsIgnoreCase(commodity)){
            String commotityVal="";
            for (int i=0;i<commodity.split(",").length;i++) {
                commotityVal+="'"+commodity.split(",")[i]+"',";
            }
            if ("FIT_PO_SBU_YEAR_CD_SUM".equalsIgnoreCase(poTable.getTableName())||"FIT_PO_CD_MONTH_DOWN".equalsIgnoreCase(poTable.getTableName())) {
                whereSql += " and COMMODITY_MAJOR in(" + commotityVal.substring(0,commotityVal.length()-1) + ")";
            }else{
                whereSql += " and COMMODITY in (" + commotityVal.substring(0,commotityVal.length()-1) + ")";
            }
        }
        UserDetailImpl loginUser = SecurityUtils.getLoginUser();
        String sbuList="select ''''||REPLACE(SBU,',',''',''')||'''' as SBU from fit_user u,FIT_PO_AUDIT_ROLE r ,FIT_PO_AUDIT_ROLE_USER ur where u.id=ur.user_id and r.id=ur.role_id and r.code in('MM','PD','SBUCompetent') and u.sbu is not null and u.username='"+loginUser.getUsername()+"'";
        List<Map> list=this.listMapBySql(sbuList);
        if(!list.isEmpty()){
            whereSql+=" and sbu in("+list.get(0).get("SBU").toString()+")";
        }
        if(null!=sbuVal && !"".equalsIgnoreCase(sbuVal)) {
            String sbu = "";
            for (int i = 0; i < sbuVal.split(",").length; i++) {
                sbu += "'" + sbuVal.split(",")[i] + "',";
            }
            if(sbuVal.indexOf(",")==-1){
                whereSql += " and sbu LIKE " + "'%" + sbuVal + "%'";
            }else {
                whereSql += " and sbu in(" + sbu.substring(0,sbu.length()-1) + ")";
            }
        }
        if (StringUtils.isNotEmpty(flag)) {
            whereSql += " and flag = '" + flag + "'";
        }
        whereSql+=" and flag in('1','2','10','3') ";
        //獲取配置排序順序
        List<PoKey> keys = poTable.getKeys();
        String orderBy ="";
        if (keys != null && keys.size() > 0) {
            for(int i=0;i<keys.size();i++){
                orderBy = orderBy + keys.get(i).getColumnName() + ",";
            }
            whereSql=whereSql+" order by "+orderBy.substring(0,orderBy.length()-1);
        }

        return whereSql;
    }


    /**
     * 判断是否跳转路径
     */
    private Model detailsTsak(Model model, HttpSession session) {
        if (String.valueOf(session.getAttribute("detailsTsak")).equalsIgnoreCase("ok")) {
            List<String> listSBU = this.listBySql(" select SBU from FIT_PO_SBU_YEAR_CD_SUM where TASK_ID='" + session.getAttribute("taskId") + "'" +
                    "  and rownum=1 ");
            List<String> listYear = this.listBySql(" select year from FIT_PO_SBU_YEAR_CD_SUM where TASK_ID='" + session.getAttribute("taskId") + "'" +
                    "  and rownum=1 ");
            String sbuVal = listSBU.get(0);
            String dateYear = listYear.get(0);
            model.addAttribute("sbuVal", sbuVal);
            model.addAttribute("dateYear", dateYear);
        }
        return model;
    }

    /**
     * 查询一些页面加载需要用到的数据
     */
    private Model selectData(Model model, String userName, String whereSql, Locale locale) {
        /**判断是否为关键用户*/
        String keyUserSql = " select count(1)  from  fit_user u \n" +
                " left join FIT_PO_AUDIT_ROLE_USER ur on u.id=ur.user_id \n" +
                " left join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id\n" +
                " WHERE r.code='KEYUSER' and u.username='" + userName + "'";
        List<BigDecimal> countList = (List<BigDecimal>) this.listBySql(keyUserSql);
        String hasKey = "0";
        if (countList.get(0).intValue() > 0) {
            hasKey = "1";
        }
        model.addAttribute("hasKey", hasKey);

        /**增刪改查權限 按鈕分組(2:上傳；1：下載；3：查詢)*/
        String uploadSql = "select a.* from FIT_PO_TABLE a,FIT_PO_BUTTON_ROLE b" +
                " where a.table_name=b.form_name and type in('PO','CPO') and b.BUTTONS_TYPE=2 and b.role_id in (" + whereSql + ") order by serial";
        List<PoTable> poTableList = this.listBySql(uploadSql, PoTable.class);
        List<PoTable> tableList = new ArrayList<PoTable>();
        for (PoTable poTable : poTableList) {
            tableList.add(new PoTable(poTable.getTableName(), instrumentClassService.getByLocale(locale, poTable.getComments())));
        }
        model.addAttribute("poTableList", tableList);

        String selectSql = "select a.* from FIT_PO_TABLE a,FIT_PO_BUTTON_ROLE b" +
                " where a.table_name=b.form_name and type in('PO','CPO') and b.BUTTONS_TYPE=3 and b.role_id in (" + whereSql + ") order by serial";
        List<PoTable> poTableListSelect = this.listBySql(selectSql, PoTable.class);
        List<PoTable> tableListSelect = new ArrayList<PoTable>();
        for (PoTable poTable : poTableListSelect) {
            tableListSelect.add(new PoTable(poTable.getTableName(), instrumentClassService.getByLocale(locale, poTable.getComments())));
        }
        model.addAttribute("tableListSelect", tableListSelect);

        String exportSql = "select a.* from FIT_PO_TABLE a,FIT_PO_BUTTON_ROLE b" +
                " where a.table_name=b.form_name and type in('PO','CPO') and b.BUTTONS_TYPE=1 and b.role_id in (" + whereSql + ") order by serial";
        List<PoTable> poTableOutList = this.listBySql(exportSql, PoTable.class);
        List<PoTable> tableOutList = new ArrayList<PoTable>();
        for (PoTable poTable : poTableOutList) {
            if (poTable.getTableName().equalsIgnoreCase("FIT_PO_CD_MONTH_DTL")) {
                tableOutList.add(new PoTable("FIT_PO_CD_MONTH_DOWN", instrumentClassService.getByLocale(locale, poTable.getComments())));
            } else if (!poTable.getTableName().equalsIgnoreCase("FIT_PO_CD_MONTH_DOWN")) {
                tableOutList.add(new PoTable(poTable.getTableName(), instrumentClassService.getByLocale(locale, poTable.getComments())));
            }
        }
        model.addAttribute("poTableOutList", tableOutList);
        return model;
    }

    /**
     * 保存采购数据
     */
    @Transactional
    private String savePoData(PoTable poTable,List<List<String>> dataList,String name) {
        int cnt = 1;
        String id = UUID.randomUUID().toString();
        String count = " select count(id) from fit_po_task where name='" + name + "' and flag not in('0','-1','-3')";
        List<BigDecimal> countList = (List<BigDecimal>)poTableDao.listBySql(count);
        if(countList.get(0).intValue()<1){
            List<PoColumns> columns = poTable.getColumns();
            //先删掉数据
            String deleteSql="delete "+poTable.getTableName()+" where task_id=(select id from FIT_PO_TASK where NAME='"+name+"') and flag in('0','-1','-3')";
            poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
            deleteSql="delete FIT_PO_TASK where NAME='"+name+"' and flag in('0','-1','-3')";
            poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
            UserDetailImpl loginUser = SecurityUtils.getLoginUser();
            String user = loginUser.getUsername();
            List<String> userName = this.listBySql("select realname from FIT_USER where username='" + user + "'");
            if (null == userName.get(0)) {
                userName.set(0, user);
            }
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (List<String> data : dataList) {
                String columnStr = "";
                for (PoColumns column : columns) {
                    columnStr += column.getColumnName() + ",";
                }
                columnStr = columnStr + "flag,TASK_ID ";
                String valueStr = "";
                for (int i = 0; i < data.size(); i++) {
                    if (columns.get(i).getDataType().equalsIgnoreCase("number")) {
                        valueStr += "to_number('" + data.get(i) + "'),";
                    } else if (columns.get(i).getDataType().equalsIgnoreCase("date")) {
                        valueStr += "to_date('" + data.get(i) + "','dd/mm/yyyy'),";
                    } else {
                        valueStr += "'" + data.get(i) + "',";
                    }
                }
                valueStr = valueStr.substring(0, valueStr.length() - 1) + ",'0'," + "'" + id + "'";
                String insertSql = "insert into " + poTable.getTableName() + "(" + columnStr + ") values(" + valueStr + ")";
                poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(insertSql).executeUpdate();
                cnt++;
                if (cnt % 1000 == 0) {
                    poTableDao.getHibernateTemplate().flush();
                    poTableDao.getHibernateTemplate().clear();
                }
            }
            String signTimet = df.format(new Date());
            String sql = " insert into FIT_PO_TASK (ID,TYPE,NAME,FLAG,CREATE_USER,CREATE_TIME,UPDATE_USER,UPDTAE_TIME,CREATE_USER_REAL,UPDATE_USER_REAL) " +
                    " values ('" + id + "'," + "'" + poTable.getTableName() + "'," + "'" + name + "'," + "'0'," + "'" + user + "'," + "'" + signTimet + "'," + "'" + user + "'," + "'" + signTimet + "'" +
                    ",'" + userName.get(0) + "','" + userName.get(0) + "')";
            if ("FIT_PO_BUDGET_CD_DTL".equalsIgnoreCase(poTable.getTableName())
                    || "FIT_PO_CD_MONTH_DTL".equalsIgnoreCase(poTable.getTableName())
                    || "FIT_ACTUAL_PO_NPRICECD_DTL".equalsIgnoreCase(poTable.getTableName())) {
                sql = " insert into FIT_PO_TASK (ID,TYPE,NAME,FLAG,CREATE_USER,CREATE_TIME,UPDATE_USER,UPDTAE_TIME,COMMODITY_MAJOR,CREATE_USER_REAL,UPDATE_USER_REAL ) " +
                        " values ( ";
                sql = sql + "'" + id + "'," + "'" + poTable.getTableName() + "'," + "'" + name + "'," + "'0'," + "'" + user + "'," + "'" + signTimet + "'," + "'" + user + "'," + "'" + signTimet
                        + "','" + name.split("_")[1] + "'" + ",'" + userName.get(0) + "','" + userName.get(0) + "')";
            }else if ("FIT_PO_SBU_YEAR_CD_SUM".equalsIgnoreCase(poTable.getTableName())) {
                sql = " insert into FIT_PO_TASK (ID,TYPE,NAME,FLAG,CREATE_USER,CREATE_TIME,UPDATE_USER,UPDTAE_TIME,CREATE_USER_REAL,UPDATE_USER_REAL,SBU) " +
                        " values ( ";
                sql = sql + "'" + id + "'," + "'" + poTable.getTableName() + "'," + "'" + name + "'," + "'0'," + "'" + user + "'," + "'" + signTimet + "'," + "'" + user + "'," + "'" + signTimet + "'" +
                        ",'" + userName.get(0) + "','" + userName.get(0) + "','" + name.split("_")[1] + "')";
            }
            poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
        }else {
            return "";
        }
        return id;
    }


    /**
     * 校驗月份展開cd校驗
     */
    public String validateMonth(String taskId) throws Exception {
        Connection c = SessionFactoryUtils.getDataSource(poTableDao.getSessionFactory()).getConnection();
        CallableStatement cs = c.prepareCall("{ CALL fit_po_cd_month_down_pkg.main(?,?)}");
        cs.setString(1, taskId);
        //需要获取输出参数时，必须注册输出参数，否则直接条用cs.getInt(4)时会报索引列无效
        cs.registerOutParameter(2, java.sql.Types.VARCHAR);
        cs.execute();
        String message = cs.getString(2);
        cs.close();
        c.close();
        if (StringUtils.isNotEmpty(message)) {
           return message;
        }
        return "Y";
    }

    public String validate(String tableName, String year, String period, String entity, String type, Locale locale) throws Exception {
        Connection c = SessionFactoryUtils.getDataSource(poTableDao.getSessionFactory()).getConnection();
        CallableStatement cs = c.prepareCall("{call CUX_PO_DATA_PKG.validation(?,?,?,?,?,?,?,?)}");
        cs.setString(1, tableName);
        cs.setString(2, type);
        cs.setString(3, locale != null ? locale.toString() : "zh_CN");
        cs.setString(4, year);
        cs.setString(5, period);
        cs.setString(6, entity);
        //需要获取输出参数时，必须注册输出参数，否则直接条用cs.getInt(4)时会报索引列无效
        cs.registerOutParameter(7, java.sql.Types.INTEGER);
        cs.registerOutParameter(8, java.sql.Types.VARCHAR);
        cs.execute();
        int code = cs.getInt(7);
        String message = cs.getString(8);

        cs.close();
        c.close();

        if (code != 0) {
            return message;
        }

        return "";
    }

    /**
     * 系统管理员可以删除数据
     * 每张表任务处于已提交后都不能删除
     * 特殊 sbu删除规则，当cpo当年度任务新建则不能删除
     * by month表删除，连带删除该年度对应sbu的数据
     */
    public void deleteAll(String idStr, String tableName) {
        if ("FIT_PO_CD_MONTH_DOWN".equalsIgnoreCase(tableName)) {
            String sql = " delete from FIT_PO_CD_MONTH_DTL where (year,sbu) " +
                    " in (select year,sbu from FIT_PO_CD_MONTH_DOWN where id in (" + idStr + "))";
            System.out.println(sql);
            poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
        }
        String deleteSql = " delete from " + tableName + " where id in (";
        deleteSql += idStr + ")";
        System.out.println(deleteSql);
        poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();

    }

    public void updateSql(String sql) {
        poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
    }

    /**
     * 判斷采購用戶是否有超過時間上傳的權限 數據上傳逾期處理
     */
    public Boolean updateState(String tableName,String date) {
        UserDetailImpl loginUser = SecurityUtils.getLoginUser();
        String userName=loginUser.getUsername();
        String sql="";
        switch (tableName){
            case "FIT_PO_SBU_YEAR_CD_SUM"://SBU年度CD目標匯總表
                sql = "select count(1) from fit_user_po_upload_v v where v.state='Y' and instr(','||TABLE_CODE||',',',"+tableName+",')> 0 and substr(PERIOD_START,0,4)<='"+date+"' and substr(PERIOD_END,0,4)>='"+date+"' and v.USERNAME='" + userName + "' and ROLECODE='MM'";
                break;
            case "FIT_PO_CD_MONTH_DTL"://採購CD目標by月展開表
                sql = "select count(1) from fit_user_po_upload_v v where v.state='Y' and instr(','||TABLE_CODE||',',',"+tableName+",')> 0 and substr(PERIOD_START,0,4)<='"+date+"' and substr(PERIOD_END,0,4)>='"+date+"' and v.USERNAME='" + userName + "' and ROLECODE in('specialSourcer','SOURCER')";
                break;
            default://採購CD手動匯總表 實際採購非價格CD匯總表
                sql = "select count(1) from fit_user_po_upload_v v where v.state='Y' and instr(','||TABLE_CODE||',',',"+tableName+",')> 0 and Replace(PERIOD_START,'-','') <='"+date+"' and Replace(PERIOD_END,'-','') >='"+date+"' and v.USERNAME='" + userName + "' and ROLECODE in('specialSourcer','SOURCER')";
                break;
        }
        List<Map> maps = poTableDao.listMapBySql(sql);
        if (maps != null && !"0".equals(maps.get(0).get("COUNT(1)").toString())) {
            return true;
        }
        return false;
    }

    /**
     * 判断採購CD目標CPO核准是否完成審批
     */
    public Boolean checkCPO(String year) {
        Boolean b = false;
        String sql = "select * from FIT_PO_TASK where TYPE='FIT_PO_Target_CPO_CD_DTL' and FLAG='3' and NAME like '" + year + "_%'";
        List list = poTableDao.listBySql(sql);
        if (null != list && list.size() > 0) {
            b = true;
        }
        return b;
    }

    /**
     * 获取採購中心對應物料大類的map
     *
     * @return
     */
    public Map<String, List> selectCommodity() {
        Map<String, List> map = new LinkedHashMap<>();
        List<Map> list = poTableDao.listMapBySql("select distinct FUNCTION_NAME,SORTVAL from  BIDEV.v_dm_d_commodity_major order by SORTVAL");
       for (int i=0;i<list.size();i++){
           String val=list.get(i).get("FUNCTION_NAME").toString();
            List<String> listCommodity = poTableDao.listBySql("select distinct COMMODITY_MAJOR  from BIDEV.v_dm_d_commodity_major where FUNCTION_NAME='" + val+ "'");
            map.put(val, listCommodity);
        }
        return map;
    }

    /**
     * 获取SBUmapping
     */
    public Map<String, List> selectSBU() {
        Map<String, List> map = new HashMap<>();
        List<String> list = poTableDao.listBySql("select distinct tie.BU_NAME from BIDEV.DM_D_ENTITY_SBU tie,BIDEV.DM_D_ENTITY_SBUFACTORY e  where e.sbu_name=tie.SBU_NAME and tie.flag='1' ");
        for (String m : list) {
            List<String> listCommodity = poTableDao.listBySql("select distinct tie.SBU_NAME from BIDEV.DM_D_ENTITY_SBU tie,BIDEV.DM_D_ENTITY_SBUFACTORY e where e.sbu_name=tie.SBU_NAME " +
                    "and tie.flag='1' and e.BU_NAME='" + m + "'");
            map.put(m, listCommodity);
        }
        return map;
    }


    /**
     *獲取SBU年度CD目標核准表任務的詳細信息
     * @param date
     * @param pageRequest
     * @return
     */
    public  Page<Object[]> cpo(String date,PageRequest pageRequest){
        String sql="select PO_CENTER,COMMODITY_MAJOR ,NO_PO_TOTAL,NO_CD_AMOUNT,NO_CPO,PO_TOTAL ,CD_AMOUNT,CPO from FIT_PO_TARGET_CPO_CD_DTL_V1 where YEAR='"+date+"'";
        System.out.println(sql);
        String sql1="select count(1) from FIT_PO_TARGET_CPO_CD_DTL_V1 where YEAR='"+date+"'";
        List<BigDecimal> count= (List<BigDecimal>)this.listBySql(sql1);
        pageRequest.setPageSize(count.get(0).intValue());
        Page<Object[]> page = this.findPageBySql(pageRequest, sql);
        return page;
    }

    /**獲取查詢結果**/
    public Model list(Model model, PageRequest pageRequest, Locale locale, String dateYear,
                      String date, String dateEnd, String tableName,String flag,
                      String poCenter, String sbuVal, String priceControl,String commodity,String founderVal) throws Exception {
        if ("FIT_PO_CD_MONTH_DTL".equalsIgnoreCase(tableName)) {
            tableName = "FIT_PO_CD_MONTH_DOWN";
        }
        PoTable poTable = get(tableName);
        List<PoColumns> columns = poTable.getColumns();
        for (PoColumns poColumns : columns) {
            poColumns.setComments(instrumentClassService.getByLocale(locale, poColumns.getComments()));
        }
        String sql = "select ID,";
        String sqlSum = "select '' ID,";
        for (PoColumns column : columns) {
            String columnName = column.getColumnName();
            if (column.getDataType().equalsIgnoreCase("number")) {
                //改成原样输出
                sql += columnName + ",";
                if ("FIT_PO_CD_MONTH_DOWN".equalsIgnoreCase(poTable.getTableName())) {
                    switch (columnName) {
                        case "PO_TARGET_CPO":
                            sqlSum += "to_char(decode((sum(YEAR_TOTAL)+sum(PO_TARGET_CD)),0,null,sum(PO_TARGET_CD)/(sum(YEAR_TOTAL)+sum(PO_TARGET_CD))*100),9999999999.9999) PO_TARGET_CPO,";
                            break;
                        case "ONE_CPO":
                            sqlSum += "to_char(decode((sum(ONE_PO_MONEY)+sum(ONE_CD)),0,null,sum(ONE_CD)/(sum(ONE_PO_MONEY)+sum(ONE_CD))*100),9999999999.9999) ONE_CPO,";
                            break;
                        case "TWO_CPO":
                            sqlSum += "to_char(decode((sum(TWO_PO_MONEY)+sum(TWO_CD)),0,null,sum(TWO_CD)/(sum(TWO_PO_MONEY)+sum(TWO_CD))*100),9999999999.9999) TWO_CPO,";
                            break;
                        case "THREE_CPO":
                            sqlSum += "to_char(decode((sum(THREE_PO_MONEY)+sum(THREE_CD)),0,null,sum(THREE_CD)/(sum(THREE_PO_MONEY)+sum(THREE_CD))*100),9999999999.9999) THREE_CPO,";
                            break;
                        case "FOUR_CPO":
                            sqlSum += "to_char(decode((sum(FOUR_PO_MONEY)+sum(FOUR_CD)),0,null,sum(FOUR_CD)/(sum(FOUR_PO_MONEY)+sum(FOUR_CD))*100),9999999999.9999) FOUR_CPO,";
                            break;
                        case "FIVE_CPO":
                            sqlSum += "to_char(decode((sum(FIVE_PO_MONEY)+sum(FIVE_CD)),0,null,sum(FIVE_CD)/(sum(FIVE_PO_MONEY)+sum(FIVE_CD))*100),9999999999.9999) FIVE_CPO,";
                            break;
                        case "SIX_CPO":
                            sqlSum += "to_char(decode((sum(SIX_PO_MONEY)+sum(SIX_CD)),0,null,sum(SIX_CD)/(sum(SIX_PO_MONEY)+sum(SIX_CD))*100),9999999999.9999) SIX_CPO,";
                            break;
                        case "SEVEN_CPO":
                            sqlSum += "to_char(decode((sum(SEVEN_PO_MONEY)+sum(SEVEN_CD)),0,null,sum(SEVEN_CD)/(sum(SEVEN_PO_MONEY)+sum(SEVEN_CD))*100),9999999999.9999) SEVEN_CPO,";
                            break;
                        case "EIGHT_CPO":
                            sqlSum += "to_char(decode((sum(EIGHT_PO_MONEY)+sum(EIGHT_CD)),0,null,sum(EIGHT_CD)/(sum(EIGHT_PO_MONEY)+sum(EIGHT_CD))*100),9999999999.9999) EIGHT_CPO,";
                            break;
                        case "NINE_CPO":
                            sqlSum += "to_char(decode((sum(NINE_PO_MONEY)+sum(NINE_CD)),0,null,sum(NINE_CD)/(sum(NINE_PO_MONEY)+sum(NINE_CD))*100),9999999999.9999) NINE_CPO,";
                            break;
                        case "TEN_CPO":
                            sqlSum += "to_char(decode((sum(TEN_PO_MONEY)+sum(TEN_CD)),0,null,sum(TEN_CD)/(sum(TEN_PO_MONEY)+sum(TEN_CD))*100),9999999999.9999) TEN_CPO,";
                            break;
                        case "ELEVEN_CPO":
                            sqlSum += "to_char(decode((sum(ELEVEN_PO_MONEY)+sum(ELEVEN_CD)),0,null,sum(ELEVEN_CD)/(sum(ELEVEN_PO_MONEY)+sum(ELEVEN_CD))*100),9999999999.9999) ELEVEN_CPO,";
                            break;
                        case "TWELVE_CPO":
                            sqlSum += "to_char(decode((sum(TWELVE_PO_MONEY)+sum(TWELVE_CD)),0,null,sum(TWELVE_CD)/(sum(TWELVE_PO_MONEY)+sum(TWELVE_CD))*100),9999999999.9999) TWELVE_CPO,";
                            break;
                        case "PO_CPO":
                            sqlSum += "to_char(decode((sum(PO_TOTAL)+sum(PO_CD)),0,null,sum(PO_CD)/(sum(PO_TOTAL)+sum(PO_CD))*100),9999999999.9999) PO_CPO,";
                            break;
                        default:
                            sqlSum += "sum(" + columnName + "),";
                            break;
                    }
                }else if("FIT_PO_SBU_YEAR_CD_SUM".equalsIgnoreCase(poTable.getTableName())){
                    switch (columnName) {
                        case "YEAR_CD":
                            sqlSum += " case when sum(YEAR_CD_AMOUNT) = 0 then 0 else sum(YEAR_CD_AMOUNT)/(sum(YEAR_CD_AMOUNT)+sum(PO_AMOUNT))*100 end YEAR_CD ,";
                            break;
                        default:
                            sqlSum += "sum(" + columnName + "),";
                            break;
                    }
                }else if("FIT_PO_BUDGET_CD_DTL".equalsIgnoreCase(poTable.getTableName())){
                    switch (columnName) {
                        case "CD_RATIO":
                            sqlSum += " sum(CD_AMOUNT)/(sum(PO_AMOUNT)+sum(CD_AMOUNT))*100 CD_RATIO ,";
                            break;
                        default:
                            sqlSum += "sum(" + columnName + "),";
                            break;
                    }
                }else{
                    sqlSum += "sum(" + columnName + "),";
                }
            } else if (column.getDataType().equalsIgnoreCase("date")) {
                sql += "to_char(" + columnName + ",'dd/mm/yyyy'),";
                sqlSum += "'' " + columnName + ",";
            } else {
                sql += columnName + ",";
                sqlSum += "'' " + columnName + ",";
            }
        }
        sql = sql.substring(0, sql.length() - 1);
        sql += " from " + poTable.getTableName();
        sqlSum = sqlSum.substring(0, sqlSum.length() - 1);
        sqlSum += " from " + poTable.getTableName();
        String whereSql = " where 1=1";
        String orderBy = " order by ";
        if (StringUtils.isNotEmpty(date) && StringUtils.isNotEmpty(dateEnd)) {
            Date d = DateUtil.parseByYyyy_MM(date);
            Assert.notNull(d, instrumentClassService.getLanguage(locale, "年月格式錯誤", "The format of year/month is error"));
            StringBuffer str;
            date=date.replace("-","");
            dateEnd=dateEnd.replace("-","");
            if (date.length() < 6) {
                str=new StringBuffer(date);
                date=str.insert(4,"0").toString();
            }
            if (dateEnd.length() < 6) {
                str=new StringBuffer(dateEnd);
                dateEnd=str.insert(4,"0").toString();
            }
            orderBy += columns.get(1).getColumnName() + ", ";
            whereSql += " and " + columns.get(0).getColumnName() +"||"+ columns.get(1).getColumnName() + ">=" + date
                    + " and " + columns.get(0).getColumnName() +"||"+ columns.get(1).getColumnName() + "<=" + dateEnd;
        }
        if (StringUtils.isNotEmpty(poCenter)) {
            whereSql += " and " + columns.get(2).getColumnName() + "='" + poCenter + "'";
        }

        switch (poTable.getTableName()){
            //採購CD手動匯總表是否客指
            case "FIT_PO_BUDGET_CD_DTL":
                if (StringUtils.isNotEmpty(priceControl)) {
                    whereSql += " and PRICE_CONTROL ='"+priceControl+"' ";
                }
                break;
            //實際採購非價格CD匯總表增加創建人篩選條件
            case "FIT_ACTUAL_PO_NPRICECD_DTL":
                if (StringUtils.isNotEmpty(founderVal)) {
                    whereSql += " and TASK_ID in( select ID from FIT_PO_TASK where CREATE_USER ='" + founderVal + "') ";
                }
                break;
            //採購CD目標by月展開表
            case "FIT_PO_CD_MONTH_DOWN":
                whereSql = " where 1=1";
                whereSql += " and year= " + dateYear;
                if (StringUtils.isNotEmpty(priceControl)) {
                    whereSql += " and  PRICE_CONTROL='" + priceControl + "'";
                }
                break;
            //SBU年度CD目標匯總表
            case "FIT_PO_SBU_YEAR_CD_SUM":
                whereSql = " where 1=1 and PO_CENTER not in('Buy-sell','資訊採購')";
                if (StringUtils.isNotEmpty(dateYear)) {
                    whereSql += " and " + columns.get(0).getColumnName() + "='" + dateYear + "'";
                }
                if(poCenter.equals("Buy-sell")||poCenter.equals("資訊採購")){
                    whereSql += " and " + columns.get(1).getColumnName() + "='1'";
                }else if (StringUtils.isNotEmpty(poCenter)) {
                    whereSql += " and " + columns.get(1).getColumnName() + "='" + poCenter + "'";
                }
                if (StringUtils.isNotEmpty(priceControl)) {
                    whereSql += " and  PRICE_CONTROL='" + priceControl + "'";
                }
                break;
        }
        if(null!=commodity && !"".equalsIgnoreCase(commodity)){
            String commotityVal="";
            for (int i=0;i<commodity.split(",").length;i++) {
                commotityVal+="'"+commodity.split(",")[i]+"',";
            }
            if ("FIT_PO_SBU_YEAR_CD_SUM".equalsIgnoreCase(poTable.getTableName())||
                    "FIT_PO_Target_CPO_CD_DTL".equalsIgnoreCase(poTable.getTableName())||
                    "FIT_PO_CD_MONTH_DOWN".equalsIgnoreCase(poTable.getTableName())) {
                whereSql += " and COMMODITY_MAJOR in(" + commotityVal.substring(0,commotityVal.length()-1) + ")";
            }else{
                whereSql += " and COMMODITY in (" + commotityVal.substring(0,commotityVal.length()-1) + ")";
            }
        }
        UserDetailImpl loginUser = SecurityUtils.getLoginUser();
        String sbuList="select ''''||REPLACE(SBU,',',''',''')||'''' as SBU from fit_user u,FIT_PO_AUDIT_ROLE r ,FIT_PO_AUDIT_ROLE_USER ur where u.id=ur.user_id and r.id=ur.role_id and r.code in('MM','PD','SBUCompetent') and u.sbu is not null and u.username='"+loginUser.getUsername()+"'";
        List<Map> list=this.listMapBySql(sbuList);
        if(!list.isEmpty()){
            whereSql+=" and sbu in("+list.get(0).get("SBU").toString()+")";
        }
        if(null!=sbuVal && !"".equalsIgnoreCase(sbuVal)) {
            String sbu = "";
            for (int i = 0; i < sbuVal.split(",").length; i++) {
                sbu += "'" + sbuVal.split(",")[i] + "',";
            }
            if(sbuVal.indexOf(",")==-1){
                whereSql += " and sbu LIKE " + "'%" + sbuVal + "%'";
            }else {
                whereSql += " and sbu in(" + sbu.substring(0,sbu.length()-1) + ")";
            }
        }
        if (StringUtils.isNotEmpty(flag)) {
            whereSql += " and flag = '" + flag + "'";
        }
        orderBy += " ID,SBU";
        sql += whereSql+" and flag in('1','2','10','3') " +orderBy;
        sqlSum += whereSql+" and flag in('1','2','10','3') ";
        System.out.println(sql + "合計：" + sqlSum);

        Page<Object[]> page = this.findPageBySql(pageRequest, sql);
        PageRequest pageRequest1 = new PageRequest();
        pageRequest1.setPageNo(1);
        pageRequest1.setPageSize(1);
        Page<Object[]> pages = this.findPageBySql(pageRequest1, sqlSum);
        page.getResult().addAll(pages.getResult());
        model.addAttribute("tableName", poTable.getTableName());
        model.addAttribute("page", page);
        model.addAttribute("columns", columns);
        return model;
    }
}


