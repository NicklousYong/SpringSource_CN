/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.servlet.view.document;

import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.LocalizedResourceHelper;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.AbstractView;

/**
 * Convenient superclass for Excel document views.
 * Compatible with Apache POI 3.5 and higher, as of Spring 4.0.
 *
 * <p>Properties:
 * <ul>
 * <li>url (optional): The url of an existing Excel document to pick as a starting point.
 * It is done without localization part nor the ".xls" extension.
 * </ul>
 *
 * <p>The file will be searched with locations in the following order:
 * <ul>
 * <li>[url]_[language]_[country].xls
 * <li>[url]_[language].xls
 * <li>[url].xls
 * </ul>
 *
 * <p>For working with the workbook in the subclass, see
 * <a href="http://poi.apache.org">Apache's POI site</a>
 *
 * <p>As an example, you can try this snippet:
 *
 * <pre class="code">
 * protected void buildExcelDocument(
 *     Map&lt;String, Object&gt; model, HSSFWorkbook workbook,
 *     HttpServletRequest request, HttpServletResponse response) {
 *
 *   // Go to the first sheet.
 *   // getSheetAt: only if workbook is created from an existing document
 * 	 // HSSFSheet sheet = workbook.getSheetAt(0);
 * 	 HSSFSheet sheet = workbook.createSheet("Spring");
 * 	 sheet.setDefaultColumnWidth(12);
 *
 *   // Write a text at A1.
 *   HSSFCell cell = getCell(sheet, 0, 0);
 *   setText(cell, "Spring POI test");
 *
 *   // Write the current date at A2.
 *   HSSFCellStyle dateStyle = workbook.createCellStyle();
 *   dateStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
 *   cell = getCell(sheet, 1, 0);
 *   cell.setCellValue(new Date());
 *   cell.setCellStyle(dateStyle);
 *
 *   // Write a number at A3
 *   getCell(sheet, 2, 0).setCellValue(458);
 *
 *   // Write a range of numbers.
 *   HSSFRow sheetRow = sheet.createRow(3);
 *   for (short i = 0; i < 10; i++) {
 *     sheetRow.createCell(i).setCellValue(i * 10);
 *   }
 * }</pre>
 *
 * This class is similar to the AbstractPdfView class in usage style.
 *
 * <p>
 *  适用于Excel文档视图的便捷超类与Spring 40中的Apache POI 35及更高版本兼容
 * 
 *  <P>属性：
 * <ul>
 * <li> url(可选)：现有Excel文档的URL作为起始点完成没有本地化部分或"xls"扩展名
 * </ul>
 * 
 *  <p>该文件将按照以下顺序搜索位置：
 * <ul>
 *  <li> [url] _ [language] _ [country] xls <li> [url] _ [language] xls <li> [url] xls
 * </ul>
 * 
 *  <p>要在子类中使用工作簿,请参见<a href=\"http://poiapacheorg\"> Apache的POI站点</a>
 * 
 *  例如,您可以尝试此代码段：
 * 
 * <pre class="code">
 *  protected void buildExcelDocument(Map&lt; String,Object&gt; model,HSSFWorkbook workbook,HttpServletR
 * equest request,HttpServletResponse response){。
 * 
 * //转到第一页// getSheetAt：仅当从现有文档创建工作簿// HSSFSheet sheet = workbookgetSheetAt(0); HSSFSheet sheet = workbo
 * okcreateSheet("Spring"); sheetsetDefaultColumnWidth(12);。
 * 
 *  //在A1 HSSFCell cell = getCell(sheet,0,0)上写一个文本; setText(单元格,"Spring POI测试");
 * 
 *  //将当前日期写入A2 HSSFCellStyle dateStyle = workbookcreateCellStyle(); dateStylesetDataFormat(HSSFDataForm
 * atgetBuiltinFormat( "M / D / YY")); cell = getCell(sheet,1,0); cellsetCellValue(new Date()); cellsetC
 * ellStyle(DATESTYLE);。
 * 
 * 
 * @author Jean-Pierre Pawlak
 * @author Juergen Hoeller
 * @see AbstractPdfView
 * @deprecated as of Spring 4.2, in favor of {@link AbstractXlsView} and its
 * {@link AbstractXlsxView} and {@link AbstractXlsxStreamingView} variants
 */
@Deprecated
public abstract class AbstractExcelView extends AbstractView {

	/** The content type for an Excel response */
	private static final String CONTENT_TYPE = "application/vnd.ms-excel";

	/** The extension to look for existing templates */
	private static final String EXTENSION = ".xls";


	private String url;


	/**
	 * Default Constructor.
	 * Sets the content type of the view to "application/vnd.ms-excel".
	 * <p>
	 *  //在A3 getCell上写一个数字(sheet,2,0)setCellValue(458);
	 * 
	 *  //写一个数字范围HSSFRow sheetRow = sheetcreateRow(3); for(short i = 0; i <10; i ++){sheetRowcreateCell(i)setCellValue(i * 10); }} </pre>
	 * 。
	 * 
	 * 该类与使用风格中的AbstractPdfView类相似
	 * 
	 */
	public AbstractExcelView() {
		setContentType(CONTENT_TYPE);
	}

	/**
	 * Set the URL of the Excel workbook source, without localization part nor extension.
	 * <p>
	 *  默认构造函数将视图的内容类型设置为"application / vndms-excel"
	 * 
	 */
	public void setUrl(String url) {
		this.url = url;
	}


	@Override
	protected boolean generatesDownloadContent() {
		return true;
	}

	/**
	 * Renders the Excel view, given the specified model.
	 * <p>
	 *  设置Excel工作簿源的URL,不包括本地化部分或扩展名
	 * 
	 */
	@Override
	protected final void renderMergedOutputModel(
			Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HSSFWorkbook workbook;
		if (this.url != null) {
			workbook = getTemplateSource(this.url, request);
		}
		else {
			workbook = new HSSFWorkbook();
			logger.debug("Created Excel Workbook from scratch");
		}

		buildExcelDocument(model, workbook, request, response);

		// Set the content type.
		response.setContentType(getContentType());

		// Should we set the content length here?
		// response.setContentLength(workbook.getBytes().length);

		// Flush byte array to servlet output stream.
		ServletOutputStream out = response.getOutputStream();
		workbook.write(out);
		out.flush();
	}

	/**
	 * Creates the workbook from an existing XLS document.
	 * <p>
	 *  给定指定的模型,呈现Excel视图
	 * 
	 * 
	 * @param url the URL of the Excel template without localization part nor extension
	 * @param request current HTTP request
	 * @return the HSSFWorkbook
	 * @throws Exception in case of failure
	 */
	protected HSSFWorkbook getTemplateSource(String url, HttpServletRequest request) throws Exception {
		LocalizedResourceHelper helper = new LocalizedResourceHelper(getApplicationContext());
		Locale userLocale = RequestContextUtils.getLocale(request);
		Resource inputFile = helper.findLocalizedResource(url, EXTENSION, userLocale);

		// Create the Excel document from the source.
		if (logger.isDebugEnabled()) {
			logger.debug("Loading Excel workbook from " + inputFile);
		}
		return new HSSFWorkbook(inputFile.getInputStream());
	}

	/**
	 * Subclasses must implement this method to create an Excel HSSFWorkbook document,
	 * given the model.
	 * <p>
	 *  从现有的XLS文档创建工作簿
	 * 
	 * 
	 * @param model the model Map
	 * @param workbook the Excel workbook to complete
	 * @param request in case we need locale etc. Shouldn't look at attributes.
	 * @param response in case we need to set cookies. Shouldn't write to it.
	 */
	protected abstract void buildExcelDocument(
			Map<String, Object> model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response)
			throws Exception;


	/**
	 * Convenient method to obtain the cell in the given sheet, row and column.
	 * <p>Creates the row and the cell if they still doesn't already exist.
	 * Thus, the column can be passed as an int, the method making the needed downcasts.
	 * <p>
	 *  子类必须实现此方法来创建Excel HSSFWorkbook文档,给出该模型
	 * 
	 * 
	 * @param sheet a sheet object. The first sheet is usually obtained by workbook.getSheetAt(0)
	 * @param row the row number
	 * @param col the column number
	 * @return the HSSFCell
	 */
	protected HSSFCell getCell(HSSFSheet sheet, int row, int col) {
		HSSFRow sheetRow = sheet.getRow(row);
		if (sheetRow == null) {
			sheetRow = sheet.createRow(row);
		}
		HSSFCell cell = sheetRow.getCell(col);
		if (cell == null) {
			cell = sheetRow.createCell(col);
		}
		return cell;
	}

	/**
	 * Convenient method to set a String as text content in a cell.
	 * <p>
	 *  在给定的表单,行和列中获取单元格的便捷方法<p>创建行和单元格,如果它们仍然不存在,那么列可以作为int传递,该方法可以进行所需的downcast
	 * 
	 * 
	 * @param cell the cell in which the text must be put
	 * @param text the text to put in the cell
	 */
	protected void setText(HSSFCell cell, String text) {
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(text);
	}

}
