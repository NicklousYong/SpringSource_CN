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

import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

/**
 * Configurable JasperReports View, allowing to specify the JasperReports exporter
 * to be specified through bean properties rather than through the view class name.
 *
 * <p><b>This class is compatible with classic JasperReports releases back until 2.x.</b>
 * As a consequence, it keeps using the {@link net.sf.jasperreports.engine.JRExporter}
 * API which got deprecated as of JasperReports 5.5.2 (early 2014).
 *
 * <p>
 *  可配置的JasperReports View,允许指定要通过bean属性指定的JasperReports导出器,而不是通过视图类名称
 * 
 * <p> <b>此类与传统的JasperReports版本兼容,直到2x </b>因此,它会继续使用{@link netsfjasperreportsengineJRExporter} API,该API已
 * 被弃用为JasperReports 552(2014年初)。
 * 
 * 
 * @author Rob Harrop
 * @since 2.0
 * @see JasperReportsCsvView
 * @see JasperReportsHtmlView
 * @see JasperReportsPdfView
 * @see JasperReportsXlsView
 */
@SuppressWarnings({"deprecation", "rawtypes"})
public class ConfigurableJasperReportsView extends AbstractJasperReportsSingleFormatView {

	private Class<? extends net.sf.jasperreports.engine.JRExporter> exporterClass;

	private boolean useWriter = true;


	/**
	 * Set the {@code JRExporter} implementation {@code Class} to use. Throws
	 * {@link IllegalArgumentException} if the {@code Class} doesn't implement
	 * {@code JRExporter}. Required setting, as it does not have a default.
	 * <p>
	 *  如果{@code Class}不实施{@code JRExporter}必需设置,则设置{@code JRExporter}实现{@code Class}以使用Throws {@link IllegalArgumentException}
	 * ,因为它没有默认。
	 * 
	 */
	public void setExporterClass(Class<? extends net.sf.jasperreports.engine.JRExporter> exporterClass) {
		Assert.isAssignable(net.sf.jasperreports.engine.JRExporter.class, exporterClass);
		this.exporterClass = exporterClass;
	}

	/**
	 * Specifies whether or not the {@code JRExporter} writes to the {@link java.io.PrintWriter}
	 * of the associated with the request ({@code true}) or whether it writes directly to the
	 * {@link java.io.InputStream} of the request ({@code false}). Default is {@code true}.
	 * <p>
	 *  指定{@code JRExporter}是否写入与请求相关联的{@link javaioPrintWriter}({@code true}),还是直接写入请求的{@link javaioInputStream}
	 * ({@code false})默认为{@code true}。
	 * 
	 */
	public void setUseWriter(boolean useWriter) {
		this.useWriter = useWriter;
	}

	/**
	 * Checks that the {@link #setExporterClass(Class) exporterClass} property is specified.
	 * <p>
	 *  检查是否指定了{@link #setExporterClass(Class)exporterClass}属性
	 * 
	 */
	@Override
	protected void onInit() {
		if (this.exporterClass == null) {
			throw new IllegalArgumentException("exporterClass is required");
		}
	}


	/**
	 * Returns a new instance of the specified {@link net.sf.jasperreports.engine.JRExporter} class.
	 * <p>
	 * 返回指定的{@link netsfjasperreportsengineJRExporter}类的新实例
	 * 
	 * 
	 * @see #setExporterClass(Class)
	 * @see BeanUtils#instantiateClass(Class)
	 */
	@Override
	protected net.sf.jasperreports.engine.JRExporter createExporter() {
		return BeanUtils.instantiateClass(this.exporterClass);
	}

	/**
	 * Indicates how the {@code JRExporter} should render its data.
	 * <p>
	 *  指示{@code JRExporter}应如何呈现其数据
	 * 
	 * @see #setUseWriter(boolean)
	 */
	@Override
	protected boolean useWriter() {
		return this.useWriter;
	}

}
