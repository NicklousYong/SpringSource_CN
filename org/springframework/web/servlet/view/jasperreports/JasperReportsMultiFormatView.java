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

package org.springframework.web.servlet.view.jasperreports;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperPrint;

import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

/**
 * JasperReports view class that allows for the actual rendering format
 * to be specified at runtime using a parameter contained in the model.
 *
 * <p>This view works on the concept of a format key and a mapping key.
 * The format key is used to pass the mapping key from your {@code Controller}
 * to Spring through as part of the model and the mapping key is used to map
 * a logical format to an actual JasperReports view class.
 *
 * <p>For example, you might add the following code to your {@code Controller}:
 *
 * <pre class="code">
 * Map<String, Object> model = new HashMap<String, Object>();
 * model.put("format", "pdf");</pre>
 *
 * Here {@code format} is the format key and {@code pdf} is the mapping key.
 * When rendering a report, this class looks for a model parameter under the
 * format key, which by default is {@code format}. It then uses the value of
 * this parameter to lookup the actual {@code View} class to use.
 *
 * <p>The default mappings for the format lookup are:
 *
 * <p><ul>
 * <li>{@code csv} - {@code JasperReportsCsvView}</li>
 * <li>{@code html} - {@code JasperReportsHtmlView}</li>
 * <li>{@code pdf} - {@code JasperReportsPdfView}</li>
 * <li>{@code xls} - {@code JasperReportsXlsView}</li>
 * <li>{@code xlsx} - {@code JasperReportsXlsxView}</li> (as of Spring 4.2)
 * </ul>
 *
 * <p>The format key can be changed using the {@code formatKey} property.
 * The applicable key-to-view-class mappings can be configured using the
 * {@code formatMappings} property.
 *
 * <p>
 *  JasperReports视图类,允许在运行时使用模型中包含的参数来指定实际的渲染格式
 * 
 * <p>此视图适用于格式键和映射键的概念格式键用于将您的{@code控制器}中的映射键传递到Spring,作为模型的一部分,映射键用于映射实际的JasperReports视图类的逻辑格式
 * 
 *  <p>例如,您可以将以下代码添加到您的{@code控制器}中：
 * 
 * <pre class="code">
 *  Map <String,Object> model = new HashMap <String,Object>(); modelput("format","pdf"); </pre>
 * 
 *  这里{@code format}是格式键,{@code pdf}是映射关键字在呈现报表时,此类将在格式键下查找一个模型参数,默认情况下为{@code format},然后使用该参数的值用于查找要使用的
 * 实际{@code View}类。
 * 
 *  <p>格式查找的默认映射有：
 * 
 * <p> <ul> <li> {@ code csv}  -  {@code JasperReportsCsvView} </li> <li> {@ code html}  -  {@code JasperReportsHtmlView}
 *  </li> <li> {@ code pdf} -  {@code JasperReportsPdfView} </li> <li> {@ code xls}  -  {@code JasperReportsXlsView}
 *  </li> <li> {@ code xlsx}  -  {@code JasperReportsXlsxView} </li> 42)。
 * </ul>
 * 
 *  <p>可以使用{@code formatKey}属性更改格式键。可以使用{@code formatMappings}属性配置适用的按键视图类映射
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 1.1.5
 * @see #setFormatKey
 * @see #setFormatMappings
 */
public class JasperReportsMultiFormatView extends AbstractJasperReportsView {

	/**
	 * Default value used for format key: "format"
	 * <p>
	 *  用于格式键的默认值："format"
	 * 
	 */
	public static final String DEFAULT_FORMAT_KEY = "format";


	/**
	 * The key of the model parameter that holds the format key.
	 * <p>
	 *  保存格式键的模型参数的键
	 * 
	 */
	private String formatKey = DEFAULT_FORMAT_KEY;

	/**
	 * Stores the format mappings, with the format discriminator
	 * as key and the corresponding view class as value.
	 * <p>
	 *  存储格式映射,格式标识符为关键字,相应的视图类为值
	 * 
	 */
	private Map<String, Class<? extends AbstractJasperReportsView>> formatMappings;

	/**
	 * Stores the mappings of mapping keys to Content-Disposition header values.
	 * <p>
	 *  存储映射密钥映射到Content-Disposition头值
	 * 
	 */
	private Properties contentDispositionMappings;


  /**
   * Creates a new {@code JasperReportsMultiFormatView} instance
   * with a default set of mappings.
   * <p>
   * 创建一个新的{@code JasperReportsMultiFormatView}实例与一组默认的映射
   * 
   */
	public JasperReportsMultiFormatView() {
		this.formatMappings = new HashMap<String, Class<? extends AbstractJasperReportsView>>(4);
		this.formatMappings.put("csv", JasperReportsCsvView.class);
		this.formatMappings.put("html", JasperReportsHtmlView.class);
		this.formatMappings.put("pdf", JasperReportsPdfView.class);
		this.formatMappings.put("xls", JasperReportsXlsView.class);
		this.formatMappings.put("xlsx", JasperReportsXlsxView.class);
	}


	/**
	 * Set the key of the model parameter that holds the format discriminator.
	 * Default is "format".
	 * <p>
	 *  设置保存格式标识符的模型参数的关键字默认为"格式"
	 * 
	 */
	public void setFormatKey(String formatKey) {
		this.formatKey = formatKey;
	}

	/**
	 * Set the mappings of format discriminators to view class names.
	 * The default mappings are:
	 * <p><ul>
	 * <li>{@code csv} - {@code JasperReportsCsvView}</li>
	 * <li>{@code html} - {@code JasperReportsHtmlView}</li>
	 * <li>{@code pdf} - {@code JasperReportsPdfView}</li>
	 * <li>{@code xls} - {@code JasperReportsXlsView}</li>
	 * <li>{@code xlsx} - {@code JasperReportsXlsxView}</li> (as of Spring 4.2)
	 * </ul>
	 * <p>
	 *  设置格式辨别器的映射以查看类名默认映射为：<p> <ul> <li> {@ code csv}  -  {@code JasperReportsCsvView} </li> <li> {@ code html}
	 *   -  {@代码JasperReportsHtmlView} </li> <li> {@ code pdf}  -  {@code JasperReportsPdfView} </li> <li> {@ code xls}
	 *   -  {@code JasperReportsXlsView} </li> <li> {@ code xlsx }  -  {@code JasperReportsXlsxView} </li>(截
	 * 至春季42)。
	 * </ul>
	 */
	public void setFormatMappings(Map<String, Class<? extends AbstractJasperReportsView>> formatMappings) {
		if (CollectionUtils.isEmpty(formatMappings)) {
			throw new IllegalArgumentException("'formatMappings' must not be empty");
		}
		this.formatMappings = formatMappings;
	}

	/**
	 * Set the mappings of {@code Content-Disposition} header values to
	 * mapping keys. If specified, Spring will look at these mappings to determine
	 * the value of the {@code Content-Disposition} header for a given
	 * format mapping.
	 * <p>
	 *  将{@code Content-Disposition}头值映射到映射键如果指定,Spring将查看这些映射以确定给定格式映射的{@code Content-Disposition}头的值
	 * 
	 */
	public void setContentDispositionMappings(Properties mappings) {
		this.contentDispositionMappings = mappings;
	}

	/**
	 * Return the mappings of {@code Content-Disposition} header values to
	 * mapping keys. Mainly available for configuration through property paths
	 * that specify individual keys.
	 * <p>
	 * 将{@code Content-Disposition}标题值的映射返回到映射关键字主要用于通过指定单个密钥的属性路径进行配置
	 * 
	 */
	public Properties getContentDispositionMappings() {
		if (this.contentDispositionMappings == null) {
			this.contentDispositionMappings = new Properties();
		}
		return this.contentDispositionMappings;
	}


	@Override
	protected boolean generatesDownloadContent() {
		return true;
	}

	/**
	 * Locates the format key in the model using the configured discriminator key and uses this
	 * key to lookup the appropriate view class from the mappings. The rendering of the
	 * report is then delegated to an instance of that view class.
	 * <p>
	 *  使用配置的标识符键找到模型中的格式键,并使用该键从映射中查找适当的视图类然后将报告的呈现委托给该视图类的实例
	 * 
	 */
	@Override
	protected void renderReport(JasperPrint populatedReport, Map<String, Object> model, HttpServletResponse response)
			throws Exception {

		String format = (String) model.get(this.formatKey);
		if (format == null) {
			throw new IllegalArgumentException("No format found in model");
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Rendering report using format mapping key [" + format + "]");
		}

		Class<? extends AbstractJasperReportsView> viewClass = this.formatMappings.get(format);
		if (viewClass == null) {
			throw new IllegalArgumentException("Format discriminator [" + format + "] is not a configured mapping");
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Rendering report using view class [" + viewClass.getName() + "]");
		}

		AbstractJasperReportsView view = BeanUtils.instantiateClass(viewClass);
		// Can skip most initialization since all relevant URL processing
		// has been done - just need to convert parameters on the sub view.
		view.setExporterParameters(getExporterParameters());
		view.setConvertedExporterParameters(getConvertedExporterParameters());

		// Prepare response and render report.
		populateContentDispositionIfNecessary(response, format);
		view.renderReport(populatedReport, model, response);
	}

	/**
	 * Adds/overwrites the {@code Content-Disposition} header value with the format-specific
	 * value if the mappings have been specified and a valid one exists for the given format.
	 * <p>
	 *  如果已经指定了映射并且对于给定的格式存在有效的一个,则添加/覆盖{@code Content-Disposition}头值与格式特定的值
	 * 
	 * @param response the {@code HttpServletResponse} to set the header in
	 * @param format the format key of the mapping
	 * @see #setContentDispositionMappings
	 */
	private void populateContentDispositionIfNecessary(HttpServletResponse response, String format) {
		if (this.contentDispositionMappings != null) {
			String header = this.contentDispositionMappings.getProperty(format);
			if (header != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("Setting Content-Disposition header to: [" + header + "]");
				}
				response.setHeader(HEADER_CONTENT_DISPOSITION, header);
			}
		}
	}

}
