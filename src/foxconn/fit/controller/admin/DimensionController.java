package foxconn.fit.controller.admin;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.dao.base.PropertyFilter;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.base.Dimension;
import foxconn.fit.entity.base.EnumDimensionType;
import foxconn.fit.service.base.DimensionService;
import foxconn.fit.util.ExcelUtil;
import foxconn.fit.util.ExceptionUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/dimension")
public class DimensionController extends BaseController {

	private static int COLUMN_NUM=3;
	
	@Autowired
	private DimensionService dimensionService;

	@RequestMapping(value = "index")
	public String index(Model model) {
		return "/admin/dimension/index";
	}

	@RequestMapping(value="/list")
	public String list(Model model, PageRequest pageRequest,String type) {
		try {
			List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
			
			if (StringUtils.isNotEmpty(type)) {
				filters.add(new PropertyFilter("EQS_type",type));
			}
			
			Page<Dimension> page = dimensionService.findPageByHQL(pageRequest, filters);
			model.addAttribute("page", page);
		} catch (Exception e) {
			logger.error("查询維度表列表失败:", e);
		}
		return "/admin/dimension/list";
	}
	
	@RequestMapping(value = "upload")
	@ResponseBody
	@Log(name = "維度表-->上传")
	public String upload(HttpServletRequest request,HttpServletResponse response, AjaxResult result,@Log(name = "維度类型") String type) {
		result.put("msg", "上传成功(Upload Success)");
		try {
			EnumDimensionType dimensionType = EnumDimensionType.valueOf(type);
			
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
					result.put("msg", "請上傳正確格式的Excel文件(The format of excel is error)");
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
				Sheet sheet = wb.getSheetAt(0);
				Row firstRow = sheet.getRow(0);
				Assert.notNull(firstRow, "第一行为标题行，不允许为空(First Row Not Empty)");
				int rowNum = sheet.getPhysicalNumberOfRows();
				if (rowNum<2) {
					result.put("flag", "fail");
					result.put("msg", "检测到Excel没有行数据(Row Data Not Empty)");
					return result.getJson();
				}
				int column = firstRow.getPhysicalNumberOfCells();
				int  COLUMN_NUM=0;
				if(type.equals("Entity")){
					COLUMN_NUM=4;
				}else{
					COLUMN_NUM=3;
				}
				if(column<COLUMN_NUM){
					result.put("flag", "fail");
					result.put("msg", "Excel列数不能小于"+COLUMN_NUM+"(Number Of Columns Can Not Less Than"+COLUMN_NUM+")");
					return result.getJson();
				}

				List<Dimension> list=new ArrayList<Dimension>();
				Row row1 = sheet.getRow(0);
				if(!type.equals(ExcelUtil.getCellStringValue(row1.getCell(0),0))){
					result.put("flag", "fail");
					result.put("msg", "選中的【維度類型】與上傳的文件不匹配");
					return result.getJson();
				}
				for (int i = 1; i < rowNum; i++) {
					Row row = sheet.getRow(i);
					if (row==null) {
						continue;
					}
					int n=0;
					Dimension dimension=new Dimension();
					dimension.setDimension(ExcelUtil.getCellStringValue(row.getCell(n++),i));
					dimension.setParent(ExcelUtil.getCellStringValue(row.getCell(n++),i));
					dimension.setAlias(ExcelUtil.getCellStringValue(row.getCell(n++),i));
					dimension.setType(type);
					if(type.equals("Entity")){
						dimension.setOuName(ExcelUtil.getCellStringValue(row.getCell(n++),i));
					}
					if (dimension!=null) {
						list.add(dimension);
					}
				}

//				ArrayList<String[]> csvList = new ArrayList<String[]>();
//				CsvReader reader=new CsvReader(file.getInputStream(), Charset.forName("UTF-8"));
//				reader.readHeaders();// 跳过表头   如果需要表头的话，不要写这句。
//				String[] headers = reader.getHeaders();
//				String title = headers[0].trim();
//				byte[] bytes1 = type.getBytes();
//				byte[] bytes2 = title.getBytes();
//				int length1=bytes1.length;
//				int length2=bytes2.length;
//				boolean eq=true;
//				for (int i = 0; i < length1; i++) {
//					byte b1 = bytes1[length1-i-1];
//					byte b2 = bytes2[length2-i-1];
//					if (b1!=b2) {
//						eq=false;
//						break;
//					}
//				}
//				Assert.isTrue(eq, "选中的【维度类型】与上传的CSV文件不匹配");
//				while(reader.readRecord()){ //逐行读入除表头的数据
//					csvList.add(reader.getValues());
//			    }
//			    reader.close();
//
//
//			    for(int row=0;row<csvList.size();row++){
//					String[] strings = csvList.get(row);
//					Assert.isTrue(strings.length>=COLUMN_NUM, "第"+(row+1)+"行数据列数小于"+COLUMN_NUM);
//					list.add(new Dimension(strings[0].trim(), strings[1].trim(), strings[2].trim(), type));
//				}

				if (!list.isEmpty()) {
					dimensionService.saveBatch(list,dimensionType);
				}else{
					result.put("flag", "fail");
					result.put("msg", "无有效数据行(Unreceived Valid Row Data)");
				}
			} else {
				result.put("flag", "fail");
				result.put("msg", "对不起,未接收到上传的文件(Unreceived File)");
			}
		} catch (Exception e) {
			logger.error("保存文件失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}

		return result.getJson();
	}
	
	@RequestMapping(value = "download")
	@ResponseBody
	@Log(name = "維度表-->下载")
	public synchronized String download(HttpServletRequest request,HttpServletResponse response,PageRequest pageRequest,AjaxResult result,@Log(name = "維度类型") String type){
		try {
			EnumDimensionType.valueOf(type);
			List<PropertyFilter> filters = new ArrayList<PropertyFilter>();

			pageRequest.setOrderBy("dimension");
			pageRequest.setOrderDir(PageRequest.Sort.ASC);
			filters.add(new PropertyFilter("EQS_type",type));
			
			List<Dimension> list = dimensionService.listByHQL(filters, pageRequest);
			
			String realPath = request.getRealPath("");
			if (CollectionUtils.isNotEmpty(list)) {
				long time = System.currentTimeMillis();
				File file=new File(realPath+File.separator+"static"+File.separator+"template"+File.separator+"bi"+File.separator+"维度表"+".xlsx");
				XSSFWorkbook workBook = new XSSFWorkbook(new FileInputStream(file));
				XSSFCellStyle style = workBook.createCellStyle();
				style.setAlignment(HorizontalAlignment.CENTER);

				SXSSFWorkbook sxssfWorkbook=new SXSSFWorkbook(workBook);
				Sheet sheet = sxssfWorkbook.getSheetAt(0);
				sheet.createFreezePane(0, 1, 0, 1);
				Row titleRow = sheet.createRow(0);
				if(type.equals("Entity")){
					String[] str=new String[]{type," Parent","Alias: Default","ouName"};
					for(int i=0;i<str.length;i++){
						Cell cell = titleRow.createCell(i);
						cell.setCellValue(str[i]);
						sheet.setColumnWidth(i, str[i].getBytes("GBK").length * 256 + 400);
					}
					for (int i = 0; i < list.size(); i++) {
						Dimension d = list.get(i);
						Row row = sheet.createRow(i+1);
						Cell cell = row.createCell(0);
						cell.setCellValue(d.getDimension());
						Cell cell1 = row.createCell(1);
						cell1.setCellValue(d.getParent());
						Cell cell2 = row.createCell(2);
						cell2.setCellValue(d.getAlias());
						Cell cell3 = row.createCell(3);
						cell3.setCellValue(d.getOuName());
					}
				}else{
					String[] str=new String[]{type," Parent","Alias: Default"};
					for(int i=0;i<str.length;i++){
						Cell cell = titleRow.createCell(i);
						cell.setCellValue(str[i]);
						sheet.setColumnWidth(i, str[i].getBytes("GBK").length * 256 + 400);
					}
					for (int i = 0; i < list.size(); i++) {
						Dimension d = list.get(i);
						Row row = sheet.createRow(i+1);
						Cell cell = row.createCell(0);
						cell.setCellValue(d.getDimension());
						Cell cell1 = row.createCell(1);
						cell1.setCellValue(d.getParent());
						Cell cell2 = row.createCell(2);
						cell2.setCellValue(d.getAlias());
					}
				}


				File outFile=new File(realPath+File.separator+"static"+File.separator+"download"+File.separator+time+".xlsx");
				OutputStream out = new FileOutputStream(outFile);
				sxssfWorkbook.write(out);
				sxssfWorkbook.close();
				out.flush();
				out.close();

				result.put("fileName", outFile.getName());
				System.gc();

//				long time = System.currentTimeMillis();
//				String filePath=realPath+File.separator+"static"+File.separator+"download"+File.separator+time+".csv";
//				CsvWriter writer=new CsvWriter(filePath, ',', Charset.forName("utf-8"));
//				writer.write(new String(new byte[] { (byte) 0xEF, (byte) 0xBB,(byte) 0xBF }));
//				if(type.equals("Entity")){
//					writer.writeRecord(new String[]{type," Parent","Alias: Default","ouName"});
//					for (Dimension dimension : list) {
//						writer.write(new String(new byte[] { (byte) 0xEF, (byte) 0xBB,(byte) 0xBF }));
//						writer.writeRecord(new String[]{dimension.getDimension(),dimension.getParent(),dimension.getAlias(),dimension.getOuName()});
//					}
//				}else{
//					writer.writeRecord(new String[]{type," Parent","Alias: Default"});
//					for (Dimension dimension : list) {
//						writer.write(new String(new byte[] { (byte) 0xEF, (byte) 0xBB,(byte) 0xBF }));
//						writer.writeRecord(new String[]{dimension.getDimension(),dimension.getParent(),dimension.getAlias()});
//					}
//				}
//
//				writer.flush();
//				writer.close();
//				result.put("fileName", time+".csv");
//				System.gc();
			}else {
				result.put("flag", "fail");
				result.put("msg", "没有查询到可下载的数据(No data can be downloaded)");
			}
		} catch (Exception e) {
			logger.error("下载Excel失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}
		
		return result.getJson();
	}

}
