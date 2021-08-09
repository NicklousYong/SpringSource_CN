/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2013 the original author or authors.
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

import java.io.OutputStream;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.write.WritableWorkbook;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.LocalizedResourceHelper;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.AbstractView;

/**
 * Convenient superclass for Excel document views.
 *
 * <p>This class uses the <i>JExcelAPI</i> instead of <i>POI</i>.
 * More information on <i>JExcelAPI</i> can be found on their
 * <a href="http://www.andykhan.com/jexcelapi/" target="_blank">website</a>.
 *
 * <p>Properties:
 * <ul>
 * <li>url (optional): The url of an existing Excel document to pick as a
 * starting point. It is done without localization part nor the .xls extension.
 * </ul>
 *
 * <p>The file will be searched with locations in the following order:
 * <ul>
 * <li>[url]_[language]_[country].xls
 * <li>[url]_[language].xls
 * <li>[url].xls
 * </ul>
 *
 * <p>For working with the workbook in the subclass, see <a
 * href="http://www.andykhan.com/jexcelapi/">Java Excel API site</a>
 *
 * <p>As an example, you can try this snippet:
 *
 * <pre class="code">
 * protected void buildExcelDocument(
 *     Map&lt;String, Object&gt; model, WritableWorkbook workbook,
 *     HttpServletRequest request, HttpServletResponse response) {
 *
 * 	 if (workbook.getNumberOfSheets() == 0) {
 * 	   workbook.createSheet(&quot;Spring&quot;, 0);
 *   }
 *
 * 	 WritableSheet sheet = workbook.getSheet(&quot;Spring&quot;);
 * 	 Label label = new Label(0, 0, &quot;This is a nice label&quot;);
 * 	 sheet.addCell(label);
 * }</pre>
 *
 * The use of this view is close to the {@link AbstractExcelView} class,
 * just using the JExcel API instead of the Apache POI API.
 *
 * <p>
 *  Excel文档视图的方便的超类
 * 
 * <p>此类使用<i> JExcelAPI </i>而不是<i> POI </i>有关<i> JExcelAPI </i>的更多信息可以在他们的<a href ="http：// wwwandykhancom / jexcelapi /"target ="_ blank">
 * 网站</a>。
 * 
 *  <P>属性：
 * <ul>
 *  <li> url(可选)：现有Excel文档的URL作为起始点完成没有本地化部分或xls扩展名
 * </ul>
 * 
 *  <p>该文件将按照以下顺序搜索位置：
 * <ul>
 *  <li> [url] _ [language] _ [country] xls <li> [url] _ [language] xls <li> [url] xls
 * </ul>
 * 
 *  <p>要在子类中使用工作簿,请参阅<a href=\"http://wwwandykhancom/jexcelapi/\"> Java Excel API网站</a>
 * 
 *  例如,您可以尝试此代码段：
 * 
 * <pre class="code">
 * protected void buildExcelDocument(Map&lt; String,Object&gt; model,WritableWorkbook workbook,HttpServl
 * etRequest request,HttpServletResponse response){。
 * 
 * 
 * @author Bram Smeets
 * @author Alef Arendsen
 * @author Juergen Hoeller
 * @since 1.2.5
 * @see AbstractExcelView
 * @see AbstractPdfView
 * @deprecated as of Spring 4.0, since JExcelAPI is an abandoned project
 * (no release since 2009, with serious bugs remaining)
 */
@Deprecated
public abstract class AbstractJExcelView extends AbstractView {

	/** The content type for an Excel response */
	private static final String CONTENT_TYPE = "application/vnd.ms-excel";

	/** The extension to look for existing templates */
	private static final String EXTENSION = ".xls";


	/** The url at which the template to use is located */
	private String url;


	/**
	 * Default Constructor.
	 * Sets the content type of the view to "application/vnd.ms-excel".
	 * <p>
	 *  if(workbookgetNumberOfSheets()== 0){workbookcreateSheet("Spring",0); }
	 * 
	 *  WritableSheet sheet = workbookgetSheet("Spring"); Label label = new Label(0,0,"这是一个不错的标签"); sheetadd
	 * Cell(标签); } </PRE>。
	 * 
	 *  使用此视图接近{@link AbstractExcelView}类,只需使用JExcel API而不是Apache POI API
	 * 
	 */
	public AbstractJExcelView() {
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

		// Set the content type and get the output stream.
		response.setContentType(getContentType());
		OutputStream out = response.getOutputStream();

		WritableWorkbook workbook;
		if (this.url != null) {
			Workbook template = getTemplateSource(this.url, request);
			workbook = Workbook.createWorkbook(out, template);
		}
		else {
			logger.debug("Creating Excel Workbook from scratch");
			workbook = Workbook.createWorkbook(out);
		}

		buildExcelDocument(model, workbook, request, response);

		// Should we set the content length here?
		// response.setContentLength(workbook.getBytes().length);

		workbook.write();
		out.flush();
		workbook.close();
	}

	/**
	 * Create the workbook from an existing XLS document.
	 * <p>
	 *  给定指定的模型,呈现Excel视图
	 * 
	 * 
	 * @param url the URL of the Excel template without localization part nor extension
	 * @param request current HTTP request
	 * @return the template workbook
	 * @throws Exception in case of failure
	 */
	protected Workbook getTemplateSource(String url, HttpServletRequest request) throws Exception {
		LocalizedResourceHelper helper = new LocalizedResourceHelper(getApplicationContext());
		Locale userLocale = RequestContextUtils.getLocale(request);
		Resource inputFile = helper.findLocalizedResource(url, EXTENSION, userLocale);

		// Create the Excel document from the source.
		if (logger.isDebugEnabled()) {
			logger.debug("Loading Excel workbook from " + inputFile);
		}
		return Workbook.getWorkbook(inputFile.getInputStream());
	}

	/**
	 * Subclasses must implement this method to create an Excel Workbook
	 * document, given the model.
	 * <p>
	 * 从现有的XLS文档创建工作簿
	 * 
	 * 
	 * @param model the model Map
	 * @param workbook the Excel workbook to complete
	 * @param request in case we need locale etc. Shouldn't look at attributes.
	 * @param response in case we need to set cookies. Shouldn't write to it.
	 * @throws Exception in case of failure
	 */
	protected abstract void buildExcelDocument(Map<String, Object> model, WritableWorkbook workbook,
			HttpServletRequest request, HttpServletResponse response) throws Exception;

}
