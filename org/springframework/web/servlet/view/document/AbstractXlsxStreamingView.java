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

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * Convenient superclass for Excel document views in the Office 2007 XLSX format,
 * using POI's streaming variant. Compatible with Apache POI 3.9 and higher.
 *
 * <p>For working with the workbook in subclasses, see
 * <a href="http://poi.apache.org">Apache's POI site</a>.
 *
 * <p>
 *  Office 2007 XLSX格式的Excel文档视图的便捷超类,使用POI的流式变体兼容Apache POI 39及更高版本
 * 
 * <p>要在子类中使用工作簿,请参见<a href=\"http://poiapacheorg\"> Apache的POI站点</a>
 * 
 * 
 * @author Juergen Hoeller
 * @since 4.2
 */
public abstract class AbstractXlsxStreamingView extends AbstractXlsxView {

	/**
	 * This implementation creates a {@link SXSSFWorkbook} for streaming the XLSX format.
	 * <p>
	 *  此实现创建一个用于流式传输XLSX格式的{@link SXSSFWorkbook}
	 * 
	 */
	@Override
	protected SXSSFWorkbook createWorkbook(Map<String, Object> model, HttpServletRequest request) {
		return new SXSSFWorkbook();
	}

	/**
	 * This implementation disposes of the {@link SXSSFWorkbook} when done with rendering.
	 * <p>
	 *  这个实现在完成渲染时处理{@link SXSSFWorkbook}
	 * 
	 * @see org.apache.poi.xssf.streaming.SXSSFWorkbook#dispose()
	 */
	@Override
	protected void renderWorkbook(Workbook workbook, HttpServletResponse response) throws IOException {
		super.renderWorkbook(workbook, response);

		// Dispose of temporary files in case of streaming variant...
		((SXSSFWorkbook) workbook).dispose();
	}

}
