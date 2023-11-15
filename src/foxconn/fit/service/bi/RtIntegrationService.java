package foxconn.fit.service.bi;

import com.alibaba.fastjson.JSONObject;
import foxconn.fit.advice.Log;
import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.bi.PoTableDao;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.base.EnumGenerateType;
import foxconn.fit.entity.bi.PoColumns;
import foxconn.fit.entity.bi.PoKey;
import foxconn.fit.entity.bi.PoTable;
import foxconn.fit.service.base.BaseService;
import foxconn.fit.service.base.UserDetailImpl;
import foxconn.fit.util.DateUtil;
import foxconn.fit.util.ExcelUtil;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.SecurityUtils;
import net.sf.json.JSONArray;
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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class RtIntegrationService extends BaseService<PoTable> {

    @Autowired
    private PoTableDao poTableDao;

    @Autowired
    private InstrumentClassService instrumentClassService;

    @Override
    public BaseDaoHibernate<PoTable> getDao() {
        return poTableDao;
    }

    /**
     * 页面初始加载
     */
    public void index(Model model, HttpServletRequest request) {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        String uploadSql = " select * from Fit_po_table where TYPE='RT' and Upload_flag='Y' order by serial";
        List<PoTable> poTableList = this.listBySql(uploadSql, PoTable.class);
        List<PoTable> tableList = new ArrayList<PoTable>();
        for (PoTable poTable : poTableList) {
            tableList.add(new PoTable(poTable.getTableName(), instrumentClassService.getByLocale(locale, poTable.getComments())));
        }
        model.addAttribute("poTableList", tableList);
    }

    /**
     * 查询list
     * */
    public String list(Model model,HttpServletRequest request,String tableName,String queryCondition) throws Exception {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        Assert.hasText(tableName, instrumentClassService.getLanguage(locale, "明細表不能為空", "The table cannot be empty"));
        PoTable poTable = this.get(tableName);
        List<PoColumns> columns = poTable.getColumns();
        for (PoColumns poColumns : columns) {
            poColumns.setComments(instrumentClassService.getByLocale(locale, poColumns.getComments()));
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
        sql = sql.substring(0, sql.length() - 1)+" from " + poTable.getTableName() + " where 1=1";
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
        String[] sbu = SecurityUtils.getSBU();
        if(null!=sbu){
            String sbuSql=" and sbu in (";
            for (String s:sbu) {
                sbuSql+="'"+s+"',";
            }
            sql+=sbuSql.substring(0,sbuSql.length()-1)+")";
        }
        sql+=" order by ID,CREATE_TIME desc";
        model.addAttribute("tableName", poTable.getTableName());
        model.addAttribute("columns", columns);
        return sql;
    }

    /**上傳**/
    public String upload(HttpServletRequest request,AjaxResult result,String[] tableNames) throws Exception {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> mutipartFiles = multipartHttpServletRequest.getFileMap();
        String tableName="";
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
                //Excel2003
                wb = new HSSFWorkbook(file.getInputStream());
            } else {
                //Excel2007
                wb = new XSSFWorkbook(file.getInputStream());
            }
            wb.close();
            Assert.isTrue(wb.getNumberOfSheets() - 1 == tableNames.length, instrumentClassService.getLanguage(locale, "Excel文件內的sheet數量與頁面選中的明細表數量不一致", "The number of sheets in the Excel file is inconsistent with the number of detail tables selected on the page"));

            Map<PoTable, List<List<String>>> dataMap = new HashMap<PoTable, List<List<String>>>();
            for (int i = 0; i < tableNames.length; i++) {
                tableName = tableNames[i];
                PoTable poTable = this.get(tableName);
                List<PoColumns> columns = poTable.getColumns();
                int COLUMN_NUM = columns.size();
                Sheet sheet = wb.getSheetAt(i);
                Row firstRow = sheet.getRow(0);
                Assert.notNull(firstRow, instrumentClassService.getLanguage(locale, "第" + (i + 1) + "個sheet的第一行為標題行，不允許為空", "The title line of the " + (i + 1) + "th sheet cannot be empty"));
                int columnNum = firstRow.getPhysicalNumberOfCells();

                if (columnNum < COLUMN_NUM) {
                    result.put("flag", "fail");
                    result.put("msg", instrumentClassService.getLanguage(locale, "第" + (i + 1) + "個sheet的列數不能小於" + COLUMN_NUM, "The number of columns in sheet " + (i + 1) + " cannot be less than " + COLUMN_NUM));
                    return result.getJson();
                }

                int rowNum = sheet.getPhysicalNumberOfRows();
                if (rowNum < 2) {
                    result.put("flag", "fail");
                    result.put("msg", instrumentClassService.getLanguage(locale, "第" + (i + 1) + "個sheet檢測到沒有行數據", "Sheet " + (i + 1) + " does not fill in the data"));
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
                            break;
                        }
                    }
                    if (isBlankRow) {
                        continue;
                    }
                    int n = 0;
                    List<String> data = new ArrayList<String>();
                    while (n < COLUMN_NUM) {
                        PoColumns column = columns.get(n);
                        if (column.getNullable() == false) {
                            if (column.getDataType().equalsIgnoreCase("date")) {
                                try {
                                    data.add(DateUtil.formatByddSMMSyyyy(ExcelUtil.getCellDateValue(row.getCell(n), DateUtil.SDF_ddSMMSyyyy)));
                                } catch (Exception e) {
                                    result.put("flag", "fail");
                                    result.put("msg", instrumentClassService.getLanguage(locale, "第" + (i + 1) + "個sheet第" + (j + 1) + "行第" + (n + 1) + "列日期格式錯誤", "The format of date in sheet " + (i + 1) + " row " + (j + 1) + " column " + (n + 1) + " is error"));
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
                                        result.put("msg", instrumentClassService.getLanguage(locale, "第" + (i + 1) + "個sheet第" + (j + 1) + "行第" + (n + 1) + "列單元格數字格式錯誤【" + value + "】", "The number format of the cell in sheet " + (i + 1) + " row " + (j + 1) + " column " + (n + 1) + " is error)"));
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
                                    result.put("msg", instrumentClassService.getLanguage(locale, "第" + (i + 1) + "個sheet第" + (j + 1) + "行第" + (n + 1) + "列日期格式錯誤", "The format of date in sheet " + (i + 1) + " row " + (j + 1) + " column " + (n + 1) + " is error"));
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
                        result.put("msg", instrumentClassService.getLanguage(locale, msg.get(0),msg.get(1)));
                    }else{
                        dataMap.put(poTable, dataList);
                    }
                } else {
                    result.put("flag", "fail");
                    result.put("msg", instrumentClassService.getLanguage(locale, "第" + (i + 1) + "個sheet無有效數據行", "The sheet " + (i + 1) + " has no valid data row"));
                }
            }
            this.saveRtData(dataMap);
        } else {
            result.put("flag", "fail");
            result.put("msg", instrumentClassService.getLanguage(locale, "對不起，未接收到上傳的文件", "Uploaded file not received"));
        }
        return result.getJson();
    }

    /**下載數據**/
    public AjaxResult download(HttpServletRequest request,PageRequest pageRequest, AjaxResult result, @Log(name = "明细表名称") String tableNames,
                               String queryCondition) throws Exception {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        Assert.hasText(tableNames, instrumentClassService.getLanguage(locale, "明細表不能為空", "The table cannot be empty"));
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
            PoTable poTable = this.get(tableName);
            List<PoColumns> columns = poTable.getColumns();
            List<Integer> lockSerialList = new ArrayList<Integer>();
            String sql = "select ";
            Sheet sheet = sxssfWorkbook.createSheet(instrumentClassService.getByLocale(locale, poTable.getComments()));
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
                comments = instrumentClassService.getByLocale(locale, comments);
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
            }

            String[] sbu = SecurityUtils.getSBU();
            if(null!=sbu){
                String sbuSql=" and sbu in (";
                for (String s:sbu) {
                    sbuSql+="'"+s+"',";
                }
                whereSql+=sbuSql.substring(0,sbuSql.length()-1)+")";
            }

            sql = sql.substring(0, sql.length() - 1) + " from " + tableName + whereSql + " order by ID";
            System.out.println(sql);
            pageRequest.setPageSize(ExcelUtil.PAGE_SIZE);
            pageRequest.setPageNo(1);
            List<Object[]> dataList = this.findPageBySql(pageRequest, sql).getResult();
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
                    dataList = this.findPageBySql(pageRequest, sql).getResult();
                    if (CollectionUtils.isNotEmpty(dataList)) {
                        for (Object[] objects : dataList) {
                            Row contentRow = sheet.createRow(rowIndex++);
                            String generateType = objects[0].toString();
                            for (int i = 0; i < objects.length-1; i++) {
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
        }
        String fileName = tableNames;
        String tableNameSql = "select * from fit_po_table";
        List<PoTable> list = this.listBySql(tableNameSql, PoTable.class);
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
        return result;
    }

    /**下載模板**/
    public AjaxResult template(HttpServletRequest request,AjaxResult result, String tableNames,Locale locale) throws Exception {
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
            PoTable poTable = this.get(tableName);
            List<PoColumns> columns = poTable.getColumns();
            Sheet sheet = workBook.createSheet(instrumentClassService.getByLocale(locale, poTable.getComments()));
            //置灰一些表字段
            if ("CUX_RT_SALES_TARGET".equalsIgnoreCase(tableName)) {
                sheet.setDefaultColumnStyle(1,lockStyle);
                sheet.setDefaultColumnStyle(2,lockStyle);
                sheet.setDefaultColumnStyle(14, lockStyle);
                sheet.setDefaultColumnStyle(27, lockStyle);
                sheet.setDefaultColumnStyle(28, lockStyle);
                sheet.setDefaultColumnStyle(29, lockStyle);
            } else if ("CUX_RT_BUDGET_MANUAL".equalsIgnoreCase(tableName)) {
                sheet.setDefaultColumnStyle(19, lockStyle);
                sheet.setDefaultColumnStyle(20, lockStyle);
            }

            sheet.createFreezePane(0, 1, 0, 1);
            int rowIndex = 0;
            Row row = sheet.createRow(rowIndex++);
            for (int i = 0; i < columns.size(); i++) {
                String comments = columns.get(i).getComments();
                comments = instrumentClassService.getByLocale(locale, comments);
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
            listMapping1=this.listBySql("select distinct DEMAND_TYPE_NAME from CUX_RT_BUDGET_DEMAND_TYPE order by DEMAND_TYPE_NAME");
            listMapping=this.listBySql("select distinct ACCOUNT_MGR from CUX_RT_ACCOUNT_MAPPING order by ACCOUNT_MGR");
        }else if("CUX_RT_SALES_TARGET".equalsIgnoreCase(fileName)){ //sales目标输入表
            cell0.setCellValue("SBU");
            cell1.setCellValue("銷售組織");
            //sbu值级修改成新的值集
            listMapping=this.listBySql("select distinct tie.NEW_SBU_NAME from bidev.v_if_sbu_mapping tie order by tie.NEW_SBU_NAME ");
            listMapping1=this.listBySql("select distinct SALES_ORG from CUX_RT_ACCOUNT_MAPPING order by SALES_ORG");
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
        List<PoTable> list = this.listBySql(tableNameSql, PoTable.class);
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
        return result;
    }

    /**
     * 保存营收数据
     */
    @Transactional
    public void saveRtData(Map<PoTable, List<List<String>>> dataMap) {
        int cnt = 1;
        for (PoTable poTable : dataMap.keySet()) {
            List<PoColumns> columns = poTable.getColumns();
            String tableName = poTable.getTableName();
            List<List<String>> dataList = dataMap.get(poTable);
            for (List<String> data : dataList) {
                String generateType = data.get(0);
                String columnStr = "";
                for (PoColumns column : columns) {
                    columnStr += column.getColumnName() + ",";
                }
                columnStr = columnStr.substring(0, columnStr.length() - 1);
                String valueStr = "'" + generateType + "',";
                String deleteStr = " and year='" + data.get(0) + "'";
                //这里减少创建人 创建时间 更新人 更新时间
                for (int i = 1; i < data.size() - 2; i++) {
                    if ("CUX_RT_SALES_TARGET".equalsIgnoreCase(tableName) && i < 14 && i != 1 && i != 2) {
                        deleteStr += " and " + columns.get(i).getColumnName() + "='" + data.get(i) + "'";
                    }
                    if (columns.get(i).getDataType().equalsIgnoreCase("number")) {
                        valueStr += "ROUND('" + data.get(i) + "',2),";
                    } else if (columns.get(i).getDataType().equalsIgnoreCase("date")) {
                        valueStr += "to_date('" + data.get(i) + "','dd/mm/yyyy'),";
                    } else {
                        valueStr += "'" + data.get(i) + "',";
                    }
                }
                System.out.println(valueStr);
                if ("CUX_RT_SALES_TARGET".equalsIgnoreCase(tableName)) {
                    deleteStr = "delete " + poTable.getTableName() + " where 1=1 " + deleteStr;
                    poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteStr).executeUpdate();
                }
                UserDetailImpl loginUser = SecurityUtils.getLoginUser();
                String userName = loginUser.getUsername();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String signTimet = df.format(new Date());
                valueStr += "'" + userName + "','" + signTimet + "'";
                String insertSql = "insert into " + poTable.getTableName() + "(" + columnStr + ") values(" + valueStr + ")";
                System.out.println(insertSql);
                poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(insertSql).executeUpdate();
                cnt++;
                if (cnt % 1000 == 0) {
                    poTableDao.getHibernateTemplate().flush();
                    poTableDao.getHibernateTemplate().clear();
                }
            }
        }
    }

    /**刪除**/
    public void delete(String id, String tableName){
        String[] ids = id.split(",");
        String deleteSql = " delete from " + tableName + " where id in (";
        String whereSql = "";
        for (String s : ids) {
            whereSql = whereSql + "'" + s + "',";
        }
        whereSql = whereSql.substring(0, whereSql.length() - 1);
        deleteSql += whereSql + ")";
        this.getDao().getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
    }

    /**
     * 獲取當前表的高級查詢字段
     * @param result
     * @param tableName
     * @return
     */
    public AjaxResult queryMasterData(AjaxResult result,String tableName){
        String sql="SELECT COLUMN_NAME,COMMENTS FROM fit_po_table_columns WHERE  table_name='"+tableName+"'  AND IS_QUERY = 'Y'  ORDER BY to_number(SERIAL)";
        List<List<String>> queryList = this.listBySql(sql);
        result.put("queryList", queryList);
        return result;
    }

    public  List<String> salesTargetCheck(List<List<String>> list){
        List msg=new ArrayList();
        List sbu=new ArrayList();
        List salesOrg=new ArrayList();
        for (List<String> s : list) {
            sbu.add(s.get(13));
            salesOrg.add(s.get(3));
        }
        salesOrg=instrumentClassService.removeDuplicate(salesOrg);
        sbu=instrumentClassService.removeDuplicate(sbu);
        String sbuVal= JSONObject.toJSONString(JSONArray.fromObject(sbu)).replace('\"','\'');
        String salesOrgVal=JSONObject.toJSONString(JSONArray.fromObject(salesOrg)).replace('\"','\'');
        String sbuSql="select distinct NEW_SBU_NAME from bidev.v_if_sbu_mapping where NEW_SBU_NAME in ("+sbuVal.substring(1,sbuVal.length()-1)+")";
        String salesOrgSql="select SALES_ORG from CUX_RT_ACCOUNT_MAPPING where SALES_ORG in ("+salesOrgVal.substring(1,salesOrgVal.length()-1)+")";
        List<String> sbuCount=instrumentClassService.removeDuplicate(this.listBySql(sbuSql));
        List<String> salesOrgCount=instrumentClassService.removeDuplicate(this.listBySql(salesOrgSql));
        if(salesOrgCount.size()!=salesOrg.size()){
            msg.add("("+instrumentClassService.getDiffrent(salesOrg,salesOrgCount)+")銷售組織有誤，請檢查！");
            msg.add("Wrong requirement type, please check!");
            return msg;
        }
        if(sbuCount.size()!=sbu.size()){
            msg.add("("+instrumentClassService.getDiffrent(sbu,sbuCount)+")SBU錯誤，請檢查!");
            msg.add("Sbu error，please check!");
            return msg;
        }
        sbuCount=Arrays.asList(SecurityUtils.getSBU());
        if(instrumentClassService.getDiffrent(sbu,sbuCount).length()>1){
            msg.add("("+instrumentClassService.getDiffrent(sbu,sbuCount)+")沒有SBU權限，請聯係管理員維護!");
            msg.add("("+instrumentClassService.getDiffrent(sbu,sbuCount)+")No SBU permission, please contact the administrator for maintenance!");
            return msg;
        }
        msg.add("成功");
        return msg;
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
        demandType=instrumentClassService.removeDuplicate(demandType);
        accountMgr=instrumentClassService.removeDuplicate(accountMgr);
        String demandTypeVal=JSONObject.toJSONString(JSONArray.fromObject(demandType)).replace('\"','\'');
        String accountMgrVal=JSONObject.toJSONString(JSONArray.fromObject(accountMgr)).replace('\"','\'');
        String demandTypeSql="select DEMAND_TYPE_NAME  from CUX_RT_BUDGET_DEMAND_TYPE where DEMAND_TYPE_NAME in ("+demandTypeVal.substring(1,demandTypeVal.length()-1)+")";
        String accountMgrSql="select ACCOUNT_MGR  from CUX_RT_ACCOUNT_MAPPING where ACCOUNT_MGR in ("+accountMgrVal.substring(1,accountMgrVal.length()-1)+")";
        List<String> demandTypeCount=this.listBySql(demandTypeSql);
        List<String> accountMgrCount=this.listBySql(accountMgrSql);
        if(demandTypeCount.size()!=demandType.size()){
            msg.add("("+instrumentClassService.getDiffrent(demandType,demandTypeCount)+")需求類型有誤，請檢查！");
            msg.add("Wrong requirement type, please check!");
            return msg;
        }
        if(accountMgrCount.size()!=accountMgr.size()){
            msg.add("("+instrumentClassService.getDiffrent(accountMgr,accountMgrCount)+")銷售區域主管錯誤，請檢查!");
            msg.add("Account Mgr error，please check!");
            return msg;
        }
        msg.add("成功");
        return msg;
    }
}


