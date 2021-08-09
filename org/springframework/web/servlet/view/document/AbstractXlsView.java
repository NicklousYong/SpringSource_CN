/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2016 the original author or authors.
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

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import org.springframework.web.servlet.view.AbstractView;

/**
 * Convenient superclass for Excel document views in traditional XLS format.
 * Compatible with Apache POI 3.5 and higher.
 *
 * <p>For working with the workbook in the subclass, see
 * <a href="http://poi.apache.org">Apache's POI site</a>
 *
 * <p>
 *  传统XLS格式的Excel文档视图的便捷超类兼容Apache POI 35及更高版本
 * 
 *  <p>要在子类中使用工作簿,请参见<a href=\"http://poiapacheorg\"> Apache的POI站点</a>
 * 
 * 
 * @author Juergen Hoeller
 * @since 4.2
 */
public abstract class AbstractXlsView extends AbstractView {

	/**
	 * Default Constructor.
	 * Sets the content type of the view to "application/vnd.ms-excel".
	 * <p>
	 * 默认构造函数将视图的内容类型设置为"application / vndms-excel"
	 * 
	 */
	public AbstractXlsView() {
		setContentType("application/vnd.ms-excel");
	}


	@Override
	protected boolean generatesDownloadContent() {
		return true;
	}

	/**
	 * Renders the Excel view, given the specified model.
	 * <p>
	 *  给定指定的模型,呈现Excel视图
	 * 
	 */
	@Override
	protected final void renderMergedOutputModel(
			Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		// Create a fresh workbook instance for this render step.
		Workbook workbook = createWorkbook(model, request);

		// Delegate to application-provided document code.
		buildExcelDocument(model, workbook, request, response);

		// Set the content type.
		response.setContentType(getContentType());

		// Flush byte array to servlet output stream.
		renderWorkbook(workbook, response);;
	}


	/**
	 * Template method for creating the POI {@link Workbook} instance.
	 * <p>The default implementation creates a traditional {@link HSSFWorkbook}.
	 * Spring-provided subclasses are overriding this for the OOXML-based variants;
	 * custom subclasses may override this for reading a workbook from a file.
	 * <p>
	 *  创建POI的模板方法{@link Workbook}实例默认实现创建一个传统的{@link HSSFWorkbook} Spring提供的子类覆盖了基于OOXML的变体;自定义子类可以覆盖从文件读取工
	 * 作簿。
	 * 
	 * 
	 * @param model the model Map
	 * @param request current HTTP request (for taking the URL or headers into account)
	 * @return the new {@link Workbook} instance
	 */
	protected Workbook createWorkbook(Map<String, Object> model, HttpServletRequest request) {
		return new HSSFWorkbook();
	}

	/**
	 * The actual render step: taking the POI {@link Workbook} and rendering
	 * it to the given response.
	 * <p>
	 *  实际渲染步骤：采用POI {@link Workbook}并将其呈现给给定的响应
	 * 
	 * 
	 * @param workbook the POI Workbook to render
	 * @param response current HTTP response
	 * @throws IOException when thrown by I/O methods that we're delegating to
	 */
	protected void renderWorkbook(Workbook workbook, HttpServletResponse response) throws IOException {
		ServletOutputStream out = response.getOutputStream();
		workbook.write(out);

		// Closeable only implemented as of POI 3.10
		if (workbook instanceof Closeable) {
			((Closeable) workbook).close();
		}
	}

	/**
	 * Application-provided subclasses must implement this method to populate
	 * the Excel workbook document, given the model.
	 * <p>
	 *  应用程序提供的子类必须实现此方法来填充Excel工作簿文档,给定模型
	 * 
	 * @param model the model Map
	 * @param workbook the Excel workbook to populate
	 * @param request in case we need locale etc. Shouldn't look at attributes.
	 * @param response in case we need to set cookies. Shouldn't write to it.
	 */
	protected abstract void buildExcelDocument(
			Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response)
			throws Exception;

}
