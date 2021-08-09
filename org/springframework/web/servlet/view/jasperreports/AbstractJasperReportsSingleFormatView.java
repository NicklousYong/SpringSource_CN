/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
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

package org.springframework.web.servlet.view.jasperreports;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperPrint;

import org.springframework.ui.jasperreports.JasperReportsUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.WebUtils;

/**
 * Extends {@code AbstractJasperReportsView} to provide basic rendering logic
 * for views that use a fixed format, e.g. always PDF or always HTML.
 *
 * <p>Subclasses need to implement two template methods: {@code createExporter}
 * to create a JasperReports exporter for a specific output format, and
 * {@code useWriter} to determine whether to write text or binary content.
 *
 * <p><b>This class is compatible with classic JasperReports releases back until 2.x.</b>
 * As a consequence, it keeps using the {@link net.sf.jasperreports.engine.JRExporter}
 * API which got deprecated as of JasperReports 5.5.2 (early 2014).
 *
 * <p>
 *  扩展{@code AbstractJasperReportsView}以提供使用固定格式的视图的基本呈现逻辑,例如始终为PDF或始终为HTML
 * 
 * <p>子类需要实现两种模板方法：{@code createExporter}为特定的输出格式创建一个JasperReports导出器,{@code useWriter}来确定是否写入文本或二进制内容
 * 
 *  <p> <b>此类与传统的JasperReports版本兼容,直到2x </b>因此,它会继续使用{@link netsfjasperreportsengineJRExporter} API,该API
 * 已被弃用为JasperReports 552(2014年初)。
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 1.1.5
 * @see #createExporter()
 * @see #useWriter()
 */
@SuppressWarnings({"deprecation", "rawtypes"})
public abstract class AbstractJasperReportsSingleFormatView extends AbstractJasperReportsView {

	@Override
	protected boolean generatesDownloadContent() {
		return !useWriter();
	}

	/**
	 * Perform rendering for a single Jasper Reports exporter, that is,
	 * for a pre-defined output format.
	 * <p>
	 *  执行单个Jasper Reports导出器的渲染,也就是对于预定义的输出格式
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void renderReport(JasperPrint populatedReport, Map<String, Object> model, HttpServletResponse response)
			throws Exception {

		net.sf.jasperreports.engine.JRExporter exporter = createExporter();

		Map<net.sf.jasperreports.engine.JRExporterParameter, Object> mergedExporterParameters = getConvertedExporterParameters();
		if (!CollectionUtils.isEmpty(mergedExporterParameters)) {
			exporter.setParameters(mergedExporterParameters);
		}

		if (useWriter()) {
			renderReportUsingWriter(exporter, populatedReport, response);
		}
		else {
			renderReportUsingOutputStream(exporter, populatedReport, response);
		}
	}

	/**
	 * We need to write text to the response Writer.
	 * <p>
	 *  我们需要写入文本到响应作者
	 * 
	 * 
	 * @param exporter the JasperReports exporter to use
	 * @param populatedReport the populated {@code JasperPrint} to render
	 * @param response the HTTP response the report should be rendered to
	 * @throws Exception if rendering failed
	 */
	protected void renderReportUsingWriter(net.sf.jasperreports.engine.JRExporter exporter,
			JasperPrint populatedReport, HttpServletResponse response) throws Exception {

		// Copy the encoding configured for the report into the response.
		String contentType = getContentType();
		String encoding = (String) exporter.getParameter(net.sf.jasperreports.engine.JRExporterParameter.CHARACTER_ENCODING);
		if (encoding != null) {
			// Only apply encoding if content type is specified but does not contain charset clause already.
			if (contentType != null && !contentType.toLowerCase().contains(WebUtils.CONTENT_TYPE_CHARSET_PREFIX)) {
				contentType = contentType + WebUtils.CONTENT_TYPE_CHARSET_PREFIX + encoding;
			}
		}
		response.setContentType(contentType);

		// Render report into HttpServletResponse's Writer.
		JasperReportsUtils.render(exporter, populatedReport, response.getWriter());
	}

	/**
	 * We need to write binary output to the response OutputStream.
	 * <p>
	 *  我们需要将二进制输出写入响应OutputStream
	 * 
	 * 
	 * @param exporter the JasperReports exporter to use
	 * @param populatedReport the populated {@code JasperPrint} to render
	 * @param response the HTTP response the report should be rendered to
	 * @throws Exception if rendering failed
	 */
	protected void renderReportUsingOutputStream(net.sf.jasperreports.engine.JRExporter exporter,
			JasperPrint populatedReport, HttpServletResponse response) throws Exception {

		// IE workaround: write into byte array first.
		ByteArrayOutputStream baos = createTemporaryOutputStream();
		JasperReportsUtils.render(exporter, populatedReport, baos);
		writeToResponse(response, baos);
	}


	/**
	 * Create a JasperReports exporter for a specific output format,
	 * which will be used to render the report to the HTTP response.
	 * <p>The {@code useWriter} method determines whether the
	 * output will be written as text or as binary content.
	 * <p>
	 * 为特定的输出格式创建一个JasperReports导出器,它将用于将报表呈现给HTTP响应<p> {@code useWriter}方法确定输出是否将被写为文本或二进制内容
	 * 
	 * 
	 * @see #useWriter()
	 */
	protected abstract net.sf.jasperreports.engine.JRExporter createExporter();

	/**
	 * Return whether to use a {@code java.io.Writer} to write text content
	 * to the HTTP response. Else, a {@code java.io.OutputStream} will be used,
	 * to write binary content to the response.
	 * <p>
	 *  返回是否使用{@code javaioWriter}将文本内容写入HTTP响应Else,将使用{@code javaioOutputStream}将二进制内容写入响应
	 * 
	 * @see javax.servlet.ServletResponse#getWriter()
	 * @see javax.servlet.ServletResponse#getOutputStream()
	 */
	protected abstract boolean useWriter();

}
