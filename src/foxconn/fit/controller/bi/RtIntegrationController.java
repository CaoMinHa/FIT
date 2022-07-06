package foxconn.fit.controller.bi;

import com.alibaba.fastjson.JSONObject;
import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.base.EnumGenerateType;
import foxconn.fit.entity.bi.PoColumns;
import foxconn.fit.entity.bi.PoKey;
import foxconn.fit.entity.bi.PoTable;
import foxconn.fit.service.bi.PoTableService;
import foxconn.fit.util.DateUtil;
import foxconn.fit.util.ExcelUtil;
import foxconn.fit.util.ExceptionUtil;
import net.sf.json.JSONArray;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;

@Controller
@RequestMapping("/bi/rtIntegration")
public class RtIntegrationController extends BaseController {
    @Autowired
    private PoTableService poTableService;

    @RequestMapping(value = "index")
    public String index(PageRequest pageRequest, Model model, HttpServletRequest request) {
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            String uploadSql = " select * from Fit_po_table where TYPE='RT' and Upload_flag='Y' order by serial";
            List<PoTable> poTableList = poTableService.listBySql(uploadSql, PoTable.class);
            List<PoTable> tableList = new ArrayList<PoTable>();
            for (PoTable poTable : poTableList) {
                tableList.add(new PoTable(poTable.getTableName(), getByLocale(locale, poTable.getComments())));
            }
            List<String> typeList = poTableService.listBySql("select distinct tablename from FIT_AUDIT_CONSOL_CONFIG order by tablename");
            model.addAttribute("typeList", typeList);
            model.addAttribute("poTableList", tableList);
            model.addAttribute("poTableOutList", tableList);
        } catch (Exception e) {
            logger.error("查询明细配置表列表信息失败", e);
        }
        return "/bi/rtIntegration/index";
    }

    @RequestMapping(value = "/list")
    public String list(Model model, PageRequest pageRequest, HttpServletRequest request, String tableName,String queryCondition) {
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            Assert.hasText(tableName, getLanguage(locale, "明細表不能為空", "The table cannot be empty"));
            PoTable poTable = poTableService.get(tableName);
            List<PoColumns> columns = poTable.getColumns();
            for (PoColumns poColumns : columns) {
                poColumns.setComments(getByLocale(locale, poColumns.getComments()));
            }
            if(poTable.getTableName().equalsIgnoreCase("CUX_RT_BUDGET_MANUAL")){
                PoColumns p=new PoColumns();
                p.setSerial(90);
                p.setColumnName("BU");
                p.setDataType("VARCHAR2");
                p.setComments("BU");
                columns.add(9,p);
            }
            String sql = "select ID,";
            for (PoColumns column : columns) {
                String columnName = column.getColumnName();
                if (column.getDataType().equalsIgnoreCase("date")) {
                    sql += "to_char(" + columnName + ",'dd/mm/yyyy'),";
                } else {
                    sql += columnName + ",";
                }
            }
            sql = sql.substring(0, sql.length() - 1);
            sql += " from " + poTable.getTableName() + " where 1=1";
            if (StringUtils.isNotEmpty(queryCondition)) {
                String[] params = queryCondition.split("&");
                for (String param : params) {
                    String columnName = param.substring(0, param.indexOf("="));
                    String columnValue = param.substring(param.indexOf("=") + 1).trim();
                    if (StringUtils.isNotEmpty(columnValue)) {
                        sql += " and " + columnName + " like '%" + columnValue + "%'";
                    }
                }
            }
            sql+=" order by CREATE_TIME desc";
            Page<Object[]> page = poTableService.findPageBySql(pageRequest, sql);
            int index = 1;
            if (pageRequest.getPageNo() > 1) {
                index = 2;
            }
            model.addAttribute("index", index);
            model.addAttribute("tableName", poTable.getTableName());
            model.addAttribute("page", page);
            model.addAttribute("columns", columns);
        } catch (Exception e) {
            logger.error("查询明细配置表列表失败:", e);
        }
        return "/bi/rtIntegration/list";
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    @Log(name = "营收模块-->上传")
    public String upload(HttpServletRequest request, HttpServletResponse response, AjaxResult result,
                         @Log(name = "年月") String date, @Log(name = "明细表名称") String[] tableNames) {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        result.put("msg", getLanguage(locale, "上傳成功", "Upload success"));
        String tableName = "" ;
        try {
            Assert.isTrue(tableNames != null && tableNames.length > 0, getLanguage(locale, "明細表不能為空", "The table cannot be empty"));
            if("CUX_RT_SALES_TARGET".equals(tableName)){
                Date d = DateUtil.parseByYyyy(date);
                Assert.notNull(d, getLanguage(locale, "年格式錯誤", "The format of year is error"));
            }
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
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
                    result.put("msg", getLanguage(locale, "請上傳正確格式的Excel文件", "The format of excel is error"));
                    return result.getJson();
                }

                Workbook wb = null;
                if ("xls".equals(suffix)) {
                    //Excel2003
                    wb = new HSSFWorkbook(file.getInputStream());
                } else {
                    //Excel2007
                    wb = new XSSFWorkbook(file.getInputStream());
                }
                wb.close();
                Assert.isTrue(wb.getNumberOfSheets() - 1 == tableNames.length, getLanguage(locale, "Excel文件內的sheet數量與頁面選中的明細表數量不一致", "The number of sheets in the Excel file is inconsistent with the number of detail tables selected on the page"));

                Map<PoTable, List<List<String>>> dataMap = new HashMap<PoTable, List<List<String>>>();
                for (int i = 0; i < tableNames.length; i++) {
                    tableName = tableNames[i];
                    PoTable poTable = poTableService.get(tableName);
                    List<PoColumns> columns = poTable.getColumns();
                    List<PoKey> keys = poTable.getKeys();
                    int COLUMN_NUM = columns.size();
                    Sheet sheet = wb.getSheetAt(i);
                    Row firstRow = sheet.getRow(0);
                    Assert.notNull(firstRow, getLanguage(locale, "第" + (i + 1) + "個sheet的第一行為標題行，不允許為空", "The title line of the " + (i + 1) + "th sheet cannot be empty"));
                    int columnNum = firstRow.getPhysicalNumberOfCells();

                    if (columnNum < COLUMN_NUM) {
                        result.put("flag", "fail");
                        result.put("msg", getLanguage(locale, "第" + (i + 1) + "個sheet的列數不能小於" + COLUMN_NUM, "The number of columns in sheet " + (i + 1) + " cannot be less than " + COLUMN_NUM));
                        return result.getJson();
                    }

                    int rowNum = sheet.getPhysicalNumberOfRows();
                    if (rowNum < 2) {
                        result.put("flag", "fail");
                        result.put("msg", getLanguage(locale, "第" + (i + 1) + "個sheet檢測到沒有行數據", "Sheet " + (i + 1) + " does not fill in the data"));
                        return result.getJson();
                    }

                    List<List<String>> dataList = new ArrayList<List<String>>();
                    for (int j = 1; j < rowNum; j++) {
                        Row row = sheet.getRow(j);
                        if (row == null) {
                            continue;
                        }

                        boolean isBlankRow = true;
                        for (int k = 0; k < COLUMN_NUM; k++) {
                            if (StringUtils.isNotEmpty(ExcelUtil.getCellStringValue(row.getCell(k), i, j))) {
                                isBlankRow = false;
                            }
                        }

                        if (isBlankRow) {
                            continue;
                        }
                        int n = 0;
                        List<String> data = new ArrayList<String>();
                        String recordsYear = ExcelUtil.getCellStringValue(row.getCell(0), i, j);
                        String recordsMonth = ExcelUtil.getCellStringValue(row.getCell(1), i, j);
                        if (recordsMonth.length() < 2) {
                            recordsMonth = "0" + recordsMonth;
                        }
                        String RYM = recordsYear + recordsMonth;
//                        if ("CUX_RT_SALES_TARGET".equalsIgnoreCase(tableName)) {
//                            data.add(date);
//                            Assert.isTrue(data.equals(recordsYear), getLanguage(locale, "錯誤的年份： " + recordsYear + "應為：" + date, "The year is error:" + RYM + "should be：" + date));
//                            n += 1;
//                        }
                        while (n < COLUMN_NUM) {
                            PoColumns column = columns.get(n);
                            if (column.getNullable() == false) {
                                if (column.getDataType().equalsIgnoreCase("date")) {
                                    try {
                                        data.add(DateUtil.formatByddSMMSyyyy(ExcelUtil.getCellDateValue(row.getCell(n), DateUtil.SDF_ddSMMSyyyy)));
                                    } catch (Exception e) {
                                        result.put("flag", "fail");
                                        result.put("msg", getLanguage(locale, "第" + (i + 1) + "個sheet第" + (j + 1) + "行第" + (n + 1) + "列日期格式錯誤", "The format of date in sheet " + (i + 1) + " row " + (j + 1) + " column " + (n + 1) + " is error"));
                                        return result.getJson();
                                    }
                                } else {
                                    String value = ExcelUtil.getCellStringValue(row.getCell(n), i, j);
                                    if (column.getDataType().equalsIgnoreCase("number")) {
                                        try {
                                            if ("".equalsIgnoreCase(value.trim())) {
                                                if (column.getComments().contains("NTD") || column.getComments().contains("金額")) {
                                                    value = "0";
                                                }
                                            }
                                            Double.parseDouble(value);
                                        } catch (Exception e) {
                                            result.put("flag", "fail");
                                            result.put("msg", getLanguage(locale, "第" + (i + 1) + "個sheet第" + (j + 1) + "行第" + (n + 1) + "列單元格數字格式錯誤【" + value + "】", "The number format of the cell in sheet " + (i + 1) + " row " + (j + 1) + " column " + (n + 1) + " is error)"));
                                            return result.getJson();
                                        }
                                    }
                                    value = value.replaceAll("'", "''");
                                    data.add(value);
                                }
                            } else {
                                if (column.getDataType().equalsIgnoreCase("date")) {
                                    try {
                                        Date date2 = ExcelUtil.getCellDateValue(row.getCell(n), DateUtil.SDF_ddSMMSyyyy);
                                        if (date2 != null) {
                                            data.add(DateUtil.formatByddSMMSyyyy(date2));
                                        } else {
                                            data.add("");
                                        }
                                    } catch (Exception e) {
                                        result.put("flag", "fail");
                                        result.put("msg", getLanguage(locale, "第" + (i + 1) + "個sheet第" + (j + 1) + "行第" + (n + 1) + "列日期格式錯誤", "The format of date in sheet " + (i + 1) + " row " + (j + 1) + " column " + (n + 1) + " is error"));
                                        return result.getJson();
                                    }
                                } else {
                                    String value = ExcelUtil.getCellStringValue(row.getCell(n), i, j);
                                    if (StringUtils.isNotEmpty(value)) {
                                        value = value.replaceAll("'", "''");
                                        data.add(value);
                                    } else {
                                        data.add("");
                                    }
                                }
                            }
                            n++;
                        }
                        dataList.add(data);
                    }
                    if (!dataList.isEmpty()) {
                        //校验需求类型是否存在
                        List<String> msg=new ArrayList<>();
                        if ("CUX_RT_SALES_TARGET".equalsIgnoreCase(tableName)) {
                             msg=salesTargetCheck(dataList);
                        }else if("CUX_RT_BUDGET_MANUAL".equalsIgnoreCase(tableName)){
                             msg=budgetManualCheck(dataList);
                        }

                        if(!"成功".equals(msg.get(0))){
                            result.put("flag", "fail");
                            result.put("msg", getLanguage(locale, msg.get(0),msg.get(1)));
                        }else{
                            dataMap.put(poTable, dataList);
                        }
                    } else {
                        result.put("flag", "fail");
                        result.put("msg", getLanguage(locale, "第" + (i + 1) + "個sheet無有效數據行", "The sheet " + (i + 1) + " has no valid data row"));
                    }
                }
                poTableService.saveRtData(dataMap, date);
            } else {
                result.put("flag", "fail");
                result.put("msg", getLanguage(locale, "對不起，未接收到上傳的文件", "Uploaded file not received"));
            }
        } catch (Exception e) {
            logger.error("保存文件失败", e);
            result.put("flag", "fail");
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }

        return result.getJson();
    }

    public  List<String> budgetManualCheck(List<List<String>> list){
        List msg=new ArrayList();
        List demandType=new ArrayList();
        List accountMgr=new ArrayList();
        for (List<String> s : list) {
            demandType.add(s.get(0));
            accountMgr.add(s.get(1));
            String str=s.get(15);
            if(str.length()!=10 || str.split("/").length!=3 ){
                msg.add("請填寫正確時間格式，例如:（01/12/2021）。");
                msg.add("Please fill in the correct time format, for example: (01/12/2021).");
                return msg;
            }
         }
        demandType=removeDuplicate(demandType);
        accountMgr=removeDuplicate(accountMgr);
        String demandTypeVal=JSONObject.toJSONString(JSONArray.fromObject(demandType)).replace('\"','\'');
        String accountMgrVal=JSONObject.toJSONString(JSONArray.fromObject(accountMgr)).replace('\"','\'');
        String demandTypeSql="select DEMAND_TYPE_NAME  from CUX_RT_BUDGET_DEMAND_TYPE where DEMAND_TYPE_NAME in ("+demandTypeVal.substring(1,demandTypeVal.length()-1)+")";
        String accountMgrSql="select ACCOUNT_MGR  from CUX_RT_ACCOUNT_MAPPING where ACCOUNT_MGR in ("+accountMgrVal.substring(1,accountMgrVal.length()-1)+")";
        List<String> demandTypeCount=poTableService.listBySql(demandTypeSql);
        List<String> accountMgrCount=poTableService.listBySql(accountMgrSql);
        if(demandTypeCount.size()!=demandType.size()){
            msg.add("("+getDiffrent(demandType,demandTypeCount)+")需求類型有誤，請檢查！");
            msg.add("Wrong requirement type, please check!");
            return msg;
        }
        if(accountMgrCount.size()!=accountMgr.size()){
            msg.add("("+getDiffrent(accountMgr,accountMgrCount)+")銷售區域主管錯誤，請檢查!");
            msg.add("Account Mgr error，please check!");
            return msg;
        }
        msg.add("成功");
        return msg;
    }

    public  List<String> salesTargetCheck(List<List<String>> list){
        List msg=new ArrayList();
        List sbu=new ArrayList();
        List salesOrg=new ArrayList();
        for (List<String> s : list) {
            sbu.add(s.get(13));
            salesOrg.add(s.get(3));
        }
        salesOrg=removeDuplicate(salesOrg);
        sbu=removeDuplicate(sbu);
        String sbuVal=JSONObject.toJSONString(JSONArray.fromObject(sbu)).replace('\"','\'');
        String salesOrgVal=JSONObject.toJSONString(JSONArray.fromObject(salesOrg)).replace('\"','\'');
        String sbuSql="select NEW_SBU_NAME from bidev.v_if_sbu_mapping where NEW_SBU_NAME in ("+sbuVal.substring(1,sbuVal.length()-1)+")";
        String salesOrgSql="select SALES_ORG from CUX_RT_ACCOUNT_MAPPING where SALES_ORG in ("+salesOrgVal.substring(1,salesOrgVal.length()-1)+")";
        List<String> sbuCount=removeDuplicate(poTableService.listBySql(sbuSql));
        List<String> salesOrgCount=removeDuplicate(poTableService.listBySql(salesOrgSql));
        if(salesOrgCount.size()!=salesOrg.size()){
            msg.add("("+getDiffrent(salesOrg,salesOrgCount)+")銷售組織有誤，請檢查！");
            msg.add("Wrong requirement type, please check!");
            return msg;
        }
        if(sbuCount.size()!=sbu.size()){
            msg.add("("+getDiffrent(sbu,sbuCount)+")SBU錯誤，請檢查!");
            msg.add("Sbu error，please check!");
            return msg;
        }
        msg.add("成功");
        return msg;
    }
    //list去重
    public static List<String> removeDuplicate(List<String> list) {
        HashSet h = new HashSet(list);
        list.clear();
        list.addAll(h);
        return list;
    }

    private static String getDiffrent(List<String> list1, List<String> list2) {
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

    @RequestMapping(value = "download")
    @ResponseBody
    @Log(name = "採購模块-->下载")
    public synchronized String download(HttpServletRequest request, HttpServletResponse response, PageRequest pageRequest, AjaxResult result,
                                         @Log(name = "公司编码") String entity, @Log(name = "明细表名称") String tableNames,
            @Log(name = "查询条件") String queryCondition) {
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            Assert.hasText(tableNames, getLanguage(locale, "明細表不能為空", "The table cannot be empty"));



            XSSFWorkbook workBook = new XSSFWorkbook();
            XSSFCellStyle titleStyle = workBook.createCellStyle();
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            titleStyle.setFillForegroundColor(IndexedColors.BLACK.index);
            XSSFCellStyle lockStyle = workBook.createCellStyle();
            lockStyle.setAlignment(HorizontalAlignment.CENTER);
            lockStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(217, 217, 217)));
            lockStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            XSSFCellStyle unlockStyle = workBook.createCellStyle();
            unlockStyle.setAlignment(HorizontalAlignment.CENTER);
            XSSFFont font = workBook.createFont();
            font.setColor(IndexedColors.WHITE.index);
            font.setBold(true);
            titleStyle.setFont(font);
            String[] tables = tableNames.split(",");
            SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(workBook);
            for (String tableName : tables) {

                if ("FIT_PO_CD_MONTH_DTL".equalsIgnoreCase(tableName)) {
                    tableName = "FIT_PO_CD_MONTH_DOWN";
                }
                PoTable poTable = poTableService.get(tableName);
                List<PoColumns> columns = poTable.getColumns();
                List<Integer> lockSerialList = new ArrayList<Integer>();
                String sql = "select ";
                Sheet sheet = sxssfWorkbook.createSheet(getByLocale(locale, poTable.getComments()));
                sheet.setDefaultColumnStyle(0, lockStyle);
                sheet.setDefaultColumnStyle(1, lockStyle);
                sheet.setDefaultColumnStyle(2, lockStyle);
                sheet.setDefaultColumnStyle(3, lockStyle);
                sheet.setDefaultColumnStyle(4, lockStyle);
                sheet.createFreezePane(0, 1, 0, 1);
                Row titleRow = sheet.createRow(0);
                List<Integer> numberList = new ArrayList<Integer>();
                for (int i = 0; i < columns.size(); i++) {
                    PoColumns poColumn = columns.get(i);
                    String columnName = poColumn.getColumnName();
                    String comments = poColumn.getComments();
                    comments = getByLocale(locale, comments);
                    if (poColumn.getLocked()) {
                        lockSerialList.add(poColumn.getSerial());
                    }
                    if (poColumn.getDataType().equalsIgnoreCase("number")) {
                        sql += "regexp_replace(to_char(" + columnName + ",'FM99999999999999.999999999'),'\\.$',''),";
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
                String orderBy = "";
                List<PoKey> keys = poTable.getKeys();
                if (keys != null && keys.size() > 0) {
                    orderBy = " order by ";
                    for (PoKey key : keys) {
                        orderBy += key.getColumnName() + ",";
                    }
                    orderBy = orderBy.substring(0, orderBy.length() - 1);
                }
                String whereSql = "";
                if (StringUtils.isNotEmpty(queryCondition)) {
                    whereSql+=" where 1=1 ";
                    String[] params = queryCondition.split("&");
                    for (String param : params) {
                        String columnName = param.substring(0,param.indexOf("="));
                        String columnValue = param.substring(param.indexOf("=")+1).trim();
                        if (StringUtils.isNotEmpty(columnValue)) {
                            whereSql+=" and "+columnName+" like '%"+columnValue+"%'";
                        }
                    }
                    whereSql+= " order by ID";
                }


                sql = sql.substring(0, sql.length() - 1) + " from " + tableName + whereSql + orderBy;
                System.out.println(sql);
                pageRequest.setPageSize(ExcelUtil.PAGE_SIZE);
                pageRequest.setPageNo(1);
                List<Object[]> dataList = poTableService.findPageBySql(pageRequest, sql).getResult();
                if (CollectionUtils.isNotEmpty(dataList)) {
                    int rowIndex = 1;
                    for (Object[] objects : dataList) {
                        Row contentRow = sheet.createRow(rowIndex++);
                        String generateType = objects[0].toString();
                        for (int i = 0; i < objects.length; i++) {
                            Cell cell = contentRow.createCell(i);
                            String text = (objects[i] != null ? objects[i].toString() : "");
                            if (StringUtils.isNotEmpty(text) && numberList.contains(i)) {
                                cell.setCellValue(Double.parseDouble(text));
                            } else {
                                cell.setCellValue(text);
                            }
                            if (i < 5 || EnumGenerateType.A.getCode().equals(generateType) || (EnumGenerateType.AM.getCode().equals(generateType) && lockSerialList.contains(new Integer(i)))) {
                                cell.setCellStyle(lockStyle);
                            } else {
                                cell.setCellStyle(unlockStyle);
                            }
                        }
                    }

                    while (dataList != null && dataList.size() >= ExcelUtil.PAGE_SIZE) {
                        pageRequest.setPageNo(pageRequest.getPageNo() + 1);
                        dataList = poTableService.findPageBySql(pageRequest, sql).getResult();
                        if (CollectionUtils.isNotEmpty(dataList)) {
                            for (Object[] objects : dataList) {
                                Row contentRow = sheet.createRow(rowIndex++);
                                String generateType = objects[0].toString();
                                for (int i = 0; i < objects.length; i++) {
                                    Cell cell = contentRow.createCell(i);
                                    String text = (objects[i] != null ? objects[i].toString() : "");
                                    if (StringUtils.isNotEmpty(text) && numberList.contains(i)) {
                                        cell.setCellValue(Double.parseDouble(text));
                                    } else {
                                        cell.setCellValue(text);
                                    }
                                    if (i < 5 || EnumGenerateType.A.getCode().equals(generateType) || (EnumGenerateType.AM.getCode().equals(generateType) && lockSerialList.contains(new Integer(i)))) {
                                        cell.setCellStyle(lockStyle);
                                    } else {
                                        cell.setCellStyle(unlockStyle);
                                    }
                                }
                            }
                        }
                    }
                }
                //sheet.setColumnHidden(0, true);
            }
            String fileName = tableNames;
//            if(tables.length < 4) {
//                fileName = tableNames;
//            }else{
//                fileName ="採購報表下載";
//            }
            String tableNameSql = "select * from fit_po_table";
            List<PoTable> list = poTableService.listBySql(tableNameSql, PoTable.class);
            for (int i = 0; i < list.size(); i++) {
                if (fileName.equalsIgnoreCase(list.get(i).getTableName())) {
                    fileName = list.get(i).getComments().split("_")[1];
                    break;
                }
            }

            File outFile = new File(request.getRealPath("") + File.separator + "static" + File.separator + "download" + File.separator + fileName + ".xlsx");
            OutputStream out = new FileOutputStream(outFile);
            sxssfWorkbook.write(out);
            sxssfWorkbook.close();
            out.flush();
            out.close();

            result.put("fileName", outFile.getName());

            System.gc();
        } catch (Exception e) {
            logger.error("下载Excel失败", e);
            result.put("flag", "fail");
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }

        return result.getJson();
    }

    /**
     * 下載模板
     *
     * @param request
     * @param response
     * @param pageRequest
     * @param result
     * @param tableNames
     * @param table_type
     * @return
     */
    @RequestMapping(value = "template")
    @ResponseBody
    public synchronized String template(HttpServletRequest request, HttpServletResponse response, PageRequest pageRequest, AjaxResult result, String tableNames, String table_type) {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        try {
            Assert.hasText(tableNames, getLanguage(locale, "明細表不能為空", "The table cannot be empty"));
            if (tableNames.endsWith(",")) {
                tableNames = tableNames.substring(0, tableNames.length() - 1);
            }

            XSSFWorkbook workBook = new XSSFWorkbook();
            XSSFCellStyle titleStyle = workBook.createCellStyle();
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            titleStyle.setFillForegroundColor(IndexedColors.BLACK.index);
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
            String fileName = "";
            for (String tableName : tableNames.split(",")) {
                fileName = tableName;
                PoTable poTable = poTableService.get(tableName);
                List<PoColumns> columns = poTable.getColumns();
                Sheet sheet = workBook.createSheet(getByLocale(locale, poTable.getComments()));
                //置灰一些表字段
                if ("CUX_RT_SALES_TARGET".equalsIgnoreCase(tableName)) {
                    sheet.setDefaultColumnStyle(1,lockStyle);
                    sheet.setDefaultColumnStyle(2,lockStyle);
                    sheet.setDefaultColumnStyle(14, lockStyle);
                    sheet.setDefaultColumnStyle(27, lockStyle);
                    sheet.setDefaultColumnStyle(28, lockStyle);
                    sheet.setDefaultColumnStyle(29, lockStyle);
//                    sheet.setDefaultColumnStyle(30, lockStyle);
//                    sheet.setDefaultColumnStyle(31, lockStyle);
                } else if ("CUX_RT_BUDGET_MANUAL".equalsIgnoreCase(tableName)) {
                    sheet.setDefaultColumnStyle(19, lockStyle);
                    sheet.setDefaultColumnStyle(20, lockStyle);
                    sheet.setDefaultColumnStyle(21, lockStyle);
                    sheet.setDefaultColumnStyle(22, lockStyle);
                }

                sheet.createFreezePane(0, 1, 0, 1);
                int rowIndex = 0;
                Row row = sheet.createRow(rowIndex++);
                for (int i = 0; i < columns.size(); i++) {
                    String comments = columns.get(i).getComments();
                    comments = getByLocale(locale, comments);
                    Cell cell = row.createCell(i);
                    cell.setCellValue(comments);
                    cell.setCellStyle(titleStyle);
                    sheet.setColumnWidth(i, comments.getBytes("GBK").length * 256 + 400);
                }
            }
            Sheet sheet = workBook.createSheet("數據字典");
            Row titleRow = sheet.createRow(0);
            Cell cell0 = titleRow.createCell(0);
            Cell cell1 = titleRow.createCell(1);
            cell0.setCellStyle(titleStyle);
            cell1.setCellStyle(titleStyle);
            List<String> listMapping=new ArrayList<>();
            List<String> listMapping1=new ArrayList<>();
            // 预算手工输入表
            if ("CUX_RT_BUDGET_MANUAL".equalsIgnoreCase(fileName)){
                cell0.setCellValue("客戶經理姓名");
                cell1.setCellValue("需求類型");
                listMapping1=poTableService.listBySql("select distinct DEMAND_TYPE_NAME from CUX_RT_BUDGET_DEMAND_TYPE order by DEMAND_TYPE_NAME");
                listMapping=poTableService.listBySql("select distinct ACCOUNT_MGR from CUX_RT_ACCOUNT_MAPPING order by ACCOUNT_MGR");
            }else if("CUX_RT_SALES_TARGET".equalsIgnoreCase(fileName)){ //sales目标输入表
                cell0.setCellValue("SBU");
                cell1.setCellValue("銷售組織");
                //sbu值级修改成新的值集
                listMapping=poTableService.listBySql("select distinct tie.NEW_SBU_NAME from bidev.v_if_sbu_mapping tie order by tie.NEW_SBU_NAME ");
//                listMapping = poTableService.listBySql("select   distinct  SBU from EPMEBS.CUX_SBU_BU_MAPPING order by SBU ");
                listMapping1=poTableService.listBySql("select distinct SALES_ORG from CUX_RT_ACCOUNT_MAPPING order by SALES_ORG");
            }
            int n = 1;
            for (int i = 0; i < Math.max(listMapping1.size(),listMapping.size()); i++) {
                String listValue = listMapping.size()-1<i?"":listMapping.get(i);
                String listValue1 = listMapping1.size()-1<i?"":listMapping1.get(i);
                Row row = sheet.createRow(n);
                Cell cell2 = row.createCell(0);
                Cell cell3 = row.createCell(1);
                cell2.setCellValue(listValue);
                cell3.setCellValue(listValue1);
                n++;
            }

            //獲取實際表名
            String tableNameSql = "select * from fit_po_table";
            List<PoTable> list = poTableService.listBySql(tableNameSql, PoTable.class);
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

            result.put("fileName", outFile.getName());
        } catch (Exception e) {
            logger.error("下载模板文件失败", e);
            result.put("flag", "fail");
            result.put("msg", getLanguage(locale, "下載模板文件失敗", "Fail to download template file") + " : " + ExceptionUtil.getRootCauseMessage(e));
        }

        return result.getJson();
    }


    @RequestMapping(value = "/delete")
    @ResponseBody
    public String deleteAll(AjaxResult ajaxResult, HttpServletRequest request, String id, String tableName) {
        try {
            String[] ids = id.split(",");
            String deleteSql = " delete from " + tableName + " where id in (";
            String whereSql = "";
            for (String s : ids) {
                whereSql = whereSql + "'" + s + "',";
            }
            whereSql = whereSql.substring(0, whereSql.length() - 1);
            deleteSql += whereSql + ")";
            poTableService.getDao().getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
        } catch (Exception e) {
            logger.error("刪除失敗", e);
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "刪除失敗(delete Fail) : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult.getJson();
    }



    @RequestMapping(value = "queryCondition")
    @ResponseBody
    public String queryMasterData(HttpServletRequest request,HttpServletResponse response,AjaxResult result,String tableName){
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            Assert.hasText(tableName, getLanguage(locale,"输入表不能为空","Master data can not be null"));
            String sql="SELECT COLUMN_NAME,COMMENTS FROM fit_po_table_columns WHERE  table_name='"+tableName+"'  AND IS_QUERY = 'Y'  ORDER BY to_number(SERIAL)";
            List<List<String>> queryList = poTableService.listBySql(sql);
            result.put("queryList", queryList);
        } catch (Exception e) {
            logger.error("查询採購映射表信息失败", e);
            result.put("flag", "fail");
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }

        return result.getJson();
    }

}
