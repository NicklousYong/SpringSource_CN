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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;

import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

/**
 * {@link org.springframework.web.servlet.ViewResolver} implementation that
 * resolves instances of {@link AbstractJasperReportsView} by translating
 * the supplied view name into the URL of the report file.
 *
 * <p>
 *  {@link orgspringframeworkwebservletViewResolver}实现,通过将提供的视图名称转换为报告文件的URL来解析{@link AbstractJasperReportsView}
 * 的实例。
 * 
 * 
 * @author Rob Harrop
 * @since 1.2.6
 */
public class JasperReportsViewResolver extends UrlBasedViewResolver {

	private String reportDataKey;

	private Properties subReportUrls;

	private String[] subReportDataKeys;

	private Properties headers;

	private Map<String, Object> exporterParameters = new HashMap<String, Object>();

	private DataSource jdbcDataSource;


	/**
	 * Requires the view class to be a subclass of {@link AbstractJasperReportsView}.
	 * <p>
	 * 需要视图类作为{@link AbstractJasperReportsView}的子类
	 * 
	 */
	@Override
	protected Class<?> requiredViewClass() {
		return AbstractJasperReportsView.class;
	}

	/**
	 * Set the {@code reportDataKey} the view class should use.
	 * <p>
	 *  设置视图类应该使用的{@code reportDataKey}
	 * 
	 * 
	 * @see AbstractJasperReportsView#setReportDataKey
	 */
	public void setReportDataKey(String reportDataKey) {
		this.reportDataKey = reportDataKey;
	}

	/**
	 * Set the {@code subReportUrls} the view class should use.
	 * <p>
	 *  设置视图类应该使用的{@code subReportUrls}
	 * 
	 * 
	 * @see AbstractJasperReportsView#setSubReportUrls
	 */
	public void setSubReportUrls(Properties subReportUrls) {
		this.subReportUrls = subReportUrls;
	}

	/**
	 * Set the {@code subReportDataKeys} the view class should use.
	 * <p>
	 *  设置视图类应该使用的{@code subReportDataKeys}
	 * 
	 * 
	 * @see AbstractJasperReportsView#setSubReportDataKeys
	 */
	public void setSubReportDataKeys(String... subReportDataKeys) {
		this.subReportDataKeys = subReportDataKeys;
	}

	/**
	 * Set the {@code headers} the view class should use.
	 * <p>
	 *  设置视图类应使用的{@code标头}
	 * 
	 * 
	 * @see AbstractJasperReportsView#setHeaders
	 */
	public void setHeaders(Properties headers) {
		this.headers = headers;
	}

	/**
	 * Set the {@code exporterParameters} the view class should use.
	 * <p>
	 *  设置视图类应使用的{@code exporterParameters}
	 * 
	 * 
	 * @see AbstractJasperReportsView#setExporterParameters
	 */
	public void setExporterParameters(Map<String, Object> exporterParameters) {
		this.exporterParameters = exporterParameters;
	}

	/**
	 * Set the {@link DataSource} the view class should use.
	 * <p>
	 *  设置视图类应使用的{@link DataSource}
	 * 
	 * @see AbstractJasperReportsView#setJdbcDataSource
	 */
	public void setJdbcDataSource(DataSource jdbcDataSource) {
		this.jdbcDataSource = jdbcDataSource;
	}


	@Override
	protected AbstractUrlBasedView buildView(String viewName) throws Exception {
		AbstractJasperReportsView view = (AbstractJasperReportsView) super.buildView(viewName);
		view.setReportDataKey(this.reportDataKey);
		view.setSubReportUrls(this.subReportUrls);
		view.setSubReportDataKeys(this.subReportDataKeys);
		view.setHeaders(this.headers);
		view.setExporterParameters(this.exporterParameters);
		view.setJdbcDataSource(this.jdbcDataSource);
		return view;
	}

}
