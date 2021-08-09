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

package org.springframework.web.servlet.view.jasperreports;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataSourceProvider;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.springframework.context.ApplicationContextException;
import org.springframework.context.support.MessageSourceResourceBundle;
import org.springframework.core.io.Resource;
import org.springframework.ui.jasperreports.JasperReportsUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

/**
 * Base class for all JasperReports views. Applies on-the-fly compilation
 * of report designs as required and coordinates the rendering process.
 * The resource path of the main report needs to be specified as {@code url}.
 *
 * <p>This class is responsible for getting report data from the model that has
 * been provided to the view. The default implementation checks for a model object
 * under the specified {@code reportDataKey} first, then falls back to looking
 * for a value of type {@code JRDataSource}, {@code java.util.Collection},
 * object array (in that order).
 *
 * <p>If no {@code JRDataSource} can be found in the model, then reports will
 * be filled using the configured {@code javax.sql.DataSource} if any. If neither
 * a {@code JRDataSource} or {@code javax.sql.DataSource} is available then
 * an {@code IllegalArgumentException} is raised.
 *
 * <p>Provides support for sub-reports through the {@code subReportUrls} and
 * {@code subReportDataKeys} properties.
 *
 * <p>When using sub-reports, the master report should be configured using the
 * {@code url} property and the sub-reports files should be configured using
 * the {@code subReportUrls} property. Each entry in the {@code subReportUrls}
 * Map corresponds to an individual sub-report. The key of an entry must match up
 * to a sub-report parameter in your report file of type
 * {@code net.sf.jasperreports.engine.JasperReport},
 * and the value of an entry must be the URL for the sub-report file.
 *
 * <p>For sub-reports that require an instance of {@code JRDataSource}, that is,
 * they don't have a hard-coded query for data retrieval, you can include the
 * appropriate data in your model as would with the data source for the parent report.
 * However, you must provide a List of parameter names that need to be converted to
 * {@code JRDataSource} instances for the sub-report via the
 * {@code subReportDataKeys} property. When using {@code JRDataSource}
 * instances for sub-reports, you <i>must</i> specify a value for the
 * {@code reportDataKey} property, indicating the data to use for the main report.
 *
 * <p>Allows for exporter parameters to be configured declatively using the
 * {@code exporterParameters} property. This is a {@code Map} typed
 * property where the key of an entry corresponds to the fully-qualified name
 * of the static field for the {@code JRExporterParameter} and the value
 * of an entry is the value you want to assign to the exporter parameter.
 *
 * <p>Response headers can be controlled via the {@code headers} property. Spring
 * will attempt to set the correct value for the {@code Content-Diposition} header
 * so that reports render correctly in Internet Explorer. However, you can override this
 * setting through the {@code headers} property.
 *
 * <p><b>This class is compatible with classic JasperReports releases back until 2.x.</b>
 * As a consequence, it keeps using the {@link net.sf.jasperreports.engine.JRExporter}
 * API which got deprecated as of JasperReports 5.5.2 (early 2014).
 *
 * <p>
 *  所有JasperReports视图的基类适用于需要的报表设计的即时编译,并协调渲染过程主报表的资源路径需要指定为{@code url}
 * 
 * <p>此类负责从已提供给视图的模型获取报告数据。
 * 首先,在指定的{@code reportDataKey}下,默认实现检查模型对象,然后返回寻找类型为{ @code JRDataSource},{@code javautilCollection},对象
 * 数组(按顺序)。
 * <p>此类负责从已提供给视图的模型获取报告数据。
 * 
 *  <p>如果在模型中找不到{@code JRDataSource},则使用配置的{@code javaxsqlDataSource}填充报告(如果有)如果没有{@code JRDataSource}或{@code javaxsqlDataSource}
 * 可用,则{@code IllegalArgumentException}被提出。
 * 
 *  <p>通过{@code subReportUrls}和{@code subReportDataKeys}属性为子报表提供支持
 * 
 * <p>使用子报表时,应使用{@code url}属性配置主报表,并使用{@code subReportUrls}属性配置子报表文件{@code subReportUrls}映射中的每个条目对应于单独的子
 * 报表条目的关键字必须与报告文件类型为{@code netsfjasperreportsengineJasperReport}的子报表参数相匹配,并且条目的值必须是子报表文件的URL。
 * 
 * <p>对于需要{@code JRDataSource}实例的子报表,也就是说,它们没有用于数据检索的硬编码查询,您可以在模型中包含适当的数据,与数据源一样对于父报告但是,必须通过{@code subReportDataKeys}
 * 属性提供需要转换为子报表的{@code JRDataSource}实例的参数名称列表当使用{@code JRDataSource}报告中,您必须</i>指定{@code reportDataKey}属性
 * 的值,表示要用于主报表的数据。
 * 
 * <p>允许使用{@code exporterParameters}属性声明配置导出器参数。
 * 这是一个{@code Map}类型的属性,其中条目的键对应于{@code的静态字段的完全限定名称,代码JRExporterParameter},并且条目的值是您要分配给exporter参数的值。
 * 
 *  <p>可以通过{@code headers}属性控制响应头部件Spring将尝试为{@code Content-Diposition}头设置正确的值,以便报告在Internet Explorer中正确
 * 呈现但是,您可以通过{@code标头}属性。
 * 
 * <p> <b>此类与传统的JasperReports版本兼容,直到2x </b>因此,它会继续使用{@link netsfjasperreportsengineJRExporter} API,该API已
 * 被弃用为JasperReports 552(2014年初)。
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 1.1.3
 * @see #setUrl
 * @see #setReportDataKey
 * @see #setSubReportUrls
 * @see #setSubReportDataKeys
 * @see #setHeaders
 * @see #setExporterParameters
 * @see #setJdbcDataSource
 */
@SuppressWarnings({"deprecation", "rawtypes"})
public abstract class AbstractJasperReportsView extends AbstractUrlBasedView {

	/**
	 * Constant that defines "Content-Disposition" header.
	 * <p>
	 *  常量定义"Content-Disposition"头
	 * 
	 */
	protected static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";

	/**
	 * The default Content-Disposition header. Used to make IE play nice.
	 * <p>
	 *  默认的Content-Disposition头用来使IE玩得很好
	 * 
	 */
	protected static final String CONTENT_DISPOSITION_INLINE = "inline";


	/**
	 * A String key used to lookup the {@code JRDataSource} in the model.
	 * <p>
	 *  用于查找模型中的{@code JRDataSource}的String键
	 * 
	 */
	private String reportDataKey;

	/**
	 * Stores the paths to any sub-report files used by this top-level report,
	 * along with the keys they are mapped to in the top-level report file.
	 * <p>
	 *  存储此顶级报表使用的任何子报表文件的路径,以及它们在顶层报表文件中映射到的关键字
	 * 
	 */
	private Properties subReportUrls;

	/**
	 * Stores the names of any data source objects that need to be converted to
	 * {@code JRDataSource} instances and included in the report parameters
	 * to be passed on to a sub-report.
	 * <p>
	 *  存储需要转换为{@code JRDataSource}实例的任何数据源对象的名称,并将其包含在要传递给子报表的报表参数中
	 * 
	 */
	private String[] subReportDataKeys;

	/**
	 * Stores the headers to written with each response
	 * <p>
	 *  存储每个响应写入的标题
	 * 
	 */
	private Properties headers;

	/**
	 * Stores the exporter parameters passed in by the user as passed in by the user. May be keyed as
	 * {@code String}s with the fully qualified name of the exporter parameter field.
	 * <p>
	 * 存储由用户传入的用户传递的导出器参数可以使用导出器参数字段的完全限定名称键入{@code String}
	 * 
	 */
	private Map<?, ?> exporterParameters = new HashMap<Object, Object>();

	/**
	 * Stores the converted exporter parameters - keyed by {@code JRExporterParameter}.
	 * <p>
	 *  存储转换的导出器参数 - 由{@code JRExporterParameter}
	 * 
	 */
	private Map<net.sf.jasperreports.engine.JRExporterParameter, Object> convertedExporterParameters;

	/**
	 * Stores the {@code DataSource}, if any, used as the report data source.
	 * <p>
	 *  存储用作报表数据源的{@code DataSource}(如果有)
	 * 
	 */
	private DataSource jdbcDataSource;

	/**
	 * The {@code JasperReport} that is used to render the view.
	 * <p>
	 *  用于呈现视图的{@code JasperReport}
	 * 
	 */
	private JasperReport report;

	/**
	 * Holds mappings between sub-report keys and {@code JasperReport} objects.
	 * <p>
	 *  保存子报表键和{@code JasperReport}对象之间的映射
	 * 
	 */
	private Map<String, JasperReport> subReports;


	/**
	 * Set the name of the model attribute that represents the report data.
	 * If not specified, the model map will be searched for a matching value type.
	 * <p>A {@code JRDataSource} will be taken as-is. For other types, conversion
	 * will apply: By default, a {@code java.util.Collection} will be converted
	 * to {@code JRBeanCollectionDataSource}, and an object array to
	 * {@code JRBeanArrayDataSource}.
	 * <p><b>Note:</b> If you pass in a Collection or object array in the model map
	 * for use as plain report parameter, rather than as report data to extract fields
	 * from, you need to specify the key for the actual report data to use, to avoid
	 * mis-detection of report data by type.
	 * <p>
	 * 设置表示报告数据的模型属性的名称如果未指定,将搜索模型映射以获取匹配的值类型<p> {@code JRDataSource}将被替换为其他类型,转换将适用：默认情况下,{@code javautilCollection}
	 * 将被转换为{@code JRBeanCollectionDataSource},并将一个对象数组转换为{@code JRBeanArrayDataSource} <p> <b>注意：</b>如果传入Co
	 * llection或对象数组在模型图中作为普通报表参数,而不是作为提取字段的报表数据,需要指定实际报表数据使用的关键字,以避免按类型错误检测报表数据。
	 * 
	 * 
	 * @see #convertReportData
	 * @see net.sf.jasperreports.engine.JRDataSource
	 * @see net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
	 * @see net.sf.jasperreports.engine.data.JRBeanArrayDataSource
	 */
	public void setReportDataKey(String reportDataKey) {
		this.reportDataKey = reportDataKey;
	}

	/**
	 * Specify resource paths which must be loaded as instances of
	 * {@code JasperReport} and passed to the JasperReports engine for
	 * rendering as sub-reports, under the same keys as in this mapping.
	 * <p>
	 * 指定必须作为{@code Jasper Report}的实例加载的资源路径,并将其传递到JasperReports引擎,作为子报表进行呈现,与此映射相同的密钥
	 * 
	 * 
	 * @param subReports mapping between model keys and resource paths
	 * (Spring resource locations)
	 * @see #setUrl
	 * @see org.springframework.context.ApplicationContext#getResource
	 */
	public void setSubReportUrls(Properties subReports) {
		this.subReportUrls = subReports;
	}

	/**
	 * Set the list of names corresponding to the model parameters that will contain
	 * data source objects for use in sub-reports. Spring will convert these objects
	 * to instances of {@code JRDataSource} where applicable and will then
	 * include the resulting {@code JRDataSource} in the parameters passed into
	 * the JasperReports engine.
	 * <p>The name specified in the list should correspond to an attribute in the
	 * model Map, and to a sub-report data source parameter in your report file.
	 * If you pass in {@code JRDataSource} objects as model attributes,
	 * specifying this list of keys is not required.
	 * <p>If you specify a list of sub-report data keys, it is required to also
	 * specify a {@code reportDataKey} for the main report, to avoid confusion
	 * between the data source objects for the various reports involved.
	 * <p>
	 * 设置与包含用于子报表的数据源对象的模型参数相对应的名称列表,Spring将会将这些对象转换为适用的{@code JRDataSource}实例,然后将其中的{@code JRDataSource}包含在
	 * 传递给JasperReports引擎的参数<p>列表中指定的名称应对应于模型Map中的属性以及报告文件中的子报表数据源参数如果将{@code JRDataSource}对象作为模型传入属性,不需要指定这
	 * 个键列表<p>如果指定子报表数据键的列表,则还需要为主报表指定{@code reportDataKey},以避免数据源对象之间的混淆涉及的各种报告。
	 * 
	 * 
	 * @param subReportDataKeys list of names for sub-report data source objects
	 * @see #setReportDataKey
	 * @see #convertReportData
	 * @see net.sf.jasperreports.engine.JRDataSource
	 * @see net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
	 * @see net.sf.jasperreports.engine.data.JRBeanArrayDataSource
	 */
	public void setSubReportDataKeys(String... subReportDataKeys) {
		this.subReportDataKeys = subReportDataKeys;
	}

	/**
	 * Specify the set of headers that are included in each of response.
	 * <p>
	 * 指定每个响应中包含的标题集
	 * 
	 * 
	 * @param headers the headers to write to each response.
	 */
	public void setHeaders(Properties headers) {
		this.headers = headers;
	}

	/**
	 * Set the exporter parameters that should be used when rendering a view.
	 * <p>
	 *  设置渲染视图时应使用的导出器参数
	 * 
	 * 
	 * @param parameters {@code Map} with the fully qualified field name
	 * of the {@code JRExporterParameter} instance as key
	 * (e.g. "net.sf.jasperreports.engine.export.JRHtmlExporterParameter.IMAGES_URI")
	 * and the value you wish to assign to the parameter as value
	 */
	public void setExporterParameters(Map<?, ?> parameters) {
		this.exporterParameters = parameters;
	}

	/**
	 * Return the exporter parameters that this view uses, if any.
	 * <p>
	 *  返回此视图使用的导出器参数(如果有)
	 * 
	 */
	public Map<?, ?> getExporterParameters() {
		return this.exporterParameters;
	}

	/**
	 * Allows subclasses to populate the converted exporter parameters.
	 * <p>
	 *  允许子类填充转换的导出器参数
	 * 
	 */
	protected void setConvertedExporterParameters(Map<net.sf.jasperreports.engine.JRExporterParameter, Object> parameters) {
		this.convertedExporterParameters = parameters;
	}

	/**
	 * Allows subclasses to retrieve the converted exporter parameters.
	 * <p>
	 *  允许子类检索转换的导出器参数
	 * 
	 */
	protected Map<net.sf.jasperreports.engine.JRExporterParameter, Object> getConvertedExporterParameters() {
		return this.convertedExporterParameters;
	}

	/**
	 * Specify the {@code javax.sql.DataSource} to use for reports with
	 * embedded SQL statements.
	 * <p>
	 *  指定用于具有嵌入式SQL语句的报表的{@code javaxsqlDataSource}
	 * 
	 */
	public void setJdbcDataSource(DataSource jdbcDataSource) {
		this.jdbcDataSource = jdbcDataSource;
	}

	/**
	 * Return the {@code javax.sql.DataSource} that this view uses, if any.
	 * <p>
	 *  返回此视图使用的{@code javaxsqlDataSource}(如果有)
	 * 
	 */
	protected DataSource getJdbcDataSource() {
		return this.jdbcDataSource;
	}


	/**
	 * JasperReports views do not strictly required a 'url' value.
	 * Alternatively, the {@link #getReport()} template method may be overridden.
	 * <p>
	 *  JasperReports视图不要严格要求一个"url"值或者也可以覆盖{@link #getReport()}模板方法
	 * 
	 */
	@Override
	protected boolean isUrlRequired() {
		return false;
	}

	/**
	 * Checks to see that a valid report file URL is supplied in the
	 * configuration. Compiles the report file is necessary.
	 * <p>Subclasses can add custom initialization logic by overriding
	 * the {@link #onInit} method.
	 * <p>
	 * 检查在配置中提供有效的报告文件URL必须编译报告文件<p>子类可以通过覆盖{@link #onInit}方法来添加自定义初始化逻辑
	 * 
	 */
	@Override
	protected final void initApplicationContext() throws ApplicationContextException {
		this.report = loadReport();

		// Load sub reports if required, and check data source parameters.
		if (this.subReportUrls != null) {
			if (this.subReportDataKeys != null && this.subReportDataKeys.length > 0 && this.reportDataKey == null) {
				throw new ApplicationContextException(
						"'reportDataKey' for main report is required when specifying a value for 'subReportDataKeys'");
			}
			this.subReports = new HashMap<String, JasperReport>(this.subReportUrls.size());
			for (Enumeration<?> urls = this.subReportUrls.propertyNames(); urls.hasMoreElements();) {
				String key = (String) urls.nextElement();
				String path = this.subReportUrls.getProperty(key);
				Resource resource = getApplicationContext().getResource(path);
				this.subReports.put(key, loadReport(resource));
			}
		}

		// Convert user-supplied exporterParameters.
		convertExporterParameters();

		if (this.headers == null) {
			this.headers = new Properties();
		}
		if (!this.headers.containsKey(HEADER_CONTENT_DISPOSITION)) {
			this.headers.setProperty(HEADER_CONTENT_DISPOSITION, CONTENT_DISPOSITION_INLINE);
		}

		onInit();
	}

	/**
	 * Subclasses can override this to add some custom initialization logic. Called
	 * by {@link #initApplicationContext()} as soon as all standard initialization logic
	 * has finished executing.
	 * <p>
	 *  只要所有标准初始化逻辑执行完毕,子类就可以重写这个来添加一些自定义的初始化逻辑,由{@link #initApplicationContext()}调用)
	 * 
	 * 
	 * @see #initApplicationContext()
	 */
	protected void onInit() {
	}

	/**
	 * Converts the exporter parameters passed in by the user which may be keyed
	 * by {@code String}s corresponding to the fully qualified name of the
	 * {@code JRExporterParameter} into parameters which are keyed by
	 * {@code JRExporterParameter}.
	 * <p>
	 *  将由{@code JRExporterParameter}的完全限定名称对应的{@code String}键入的用户传入的导出器参数转换为由{@code JRExporterParameter}键入的
	 * 参数。
	 * 
	 * 
	 * @see #getExporterParameter(Object)
	 */
	protected final void convertExporterParameters() {
		if (!CollectionUtils.isEmpty(this.exporterParameters)) {
			this.convertedExporterParameters =
					new HashMap<net.sf.jasperreports.engine.JRExporterParameter, Object>(this.exporterParameters.size());
			for (Map.Entry<?, ?> entry : this.exporterParameters.entrySet()) {
				net.sf.jasperreports.engine.JRExporterParameter exporterParameter = getExporterParameter(entry.getKey());
				this.convertedExporterParameters.put(
						exporterParameter, convertParameterValue(exporterParameter, entry.getValue()));
			}
		}
	}

	/**
	 * Convert the supplied parameter value into the actual type required by the
	 * corresponding {@code JRExporterParameter}.
	 * <p>The default implementation simply converts the String values "true" and
	 * "false" into corresponding {@code Boolean} objects, and tries to convert
	 * String values that start with a digit into {@code Integer} objects
	 * (simply keeping them as String if number conversion fails).
	 * <p>
	 * 将提供的参数值转换为相应{@code JRExporterParameter} <p>所需的实际类型。
	 * 默认实现将String值"true"和"false"转换为相应的{@code Boolean}对象,并尝试转换String值以数字开头为{@code Integer}对象(如果数字转换失败,只需将它们保
	 * 留为String即可)。
	 * 将提供的参数值转换为相应{@code JRExporterParameter} <p>所需的实际类型。
	 * 
	 * 
	 * @param parameter the parameter key
	 * @param value the parameter value
	 * @return the converted parameter value
	 */
	protected Object convertParameterValue(net.sf.jasperreports.engine.JRExporterParameter parameter, Object value) {
		if (value instanceof String) {
			String str = (String) value;
			if ("true".equals(str)) {
				return Boolean.TRUE;
			}
			else if ("false".equals(str)) {
				return Boolean.FALSE;
			}
			else if (str.length() > 0 && Character.isDigit(str.charAt(0))) {
				// Looks like a number... let's try.
				try {
					return Integer.valueOf(str);
				}
				catch (NumberFormatException ex) {
					// OK, then let's keep it as a String value.
					return str;
				}
			}
		}
		return value;
	}

	/**
	 * Return a {@code JRExporterParameter} for the given parameter object,
	 * converting it from a String if necessary.
	 * <p>
	 *  返回给定参数对象的{@code JRExporterParameter},如有必要,将其从String转换
	 * 
	 * 
	 * @param parameter the parameter object, either a String or a JRExporterParameter
	 * @return a JRExporterParameter for the given parameter object
	 * @see #convertToExporterParameter(String)
	 */
	protected net.sf.jasperreports.engine.JRExporterParameter getExporterParameter(Object parameter) {
		if (parameter instanceof net.sf.jasperreports.engine.JRExporterParameter) {
			return (net.sf.jasperreports.engine.JRExporterParameter) parameter;
		}
		if (parameter instanceof String) {
			return convertToExporterParameter((String) parameter);
		}
		throw new IllegalArgumentException(
				"Parameter [" + parameter + "] is invalid type. Should be either String or JRExporterParameter.");
	}

	/**
	 * Convert the given fully qualified field name to a corresponding
	 * JRExporterParameter instance.
	 * <p>
	 *  将给定的全限定字段名称转换为相应的JRExporterParameter实例
	 * 
	 * 
	 * @param fqFieldName the fully qualified field name, consisting
	 * of the class name followed by a dot followed by the field name
	 * (e.g. "net.sf.jasperreports.engine.export.JRHtmlExporterParameter.IMAGES_URI")
	 * @return the corresponding JRExporterParameter instance
	 */
	protected net.sf.jasperreports.engine.JRExporterParameter convertToExporterParameter(String fqFieldName) {
		int index = fqFieldName.lastIndexOf('.');
		if (index == -1 || index == fqFieldName.length()) {
			throw new IllegalArgumentException(
					"Parameter name [" + fqFieldName + "] is not a valid static field. " +
					"The parameter name must map to a static field such as " +
					"[net.sf.jasperreports.engine.export.JRHtmlExporterParameter.IMAGES_URI]");
		}
		String className = fqFieldName.substring(0, index);
		String fieldName = fqFieldName.substring(index + 1);

		try {
			Class<?> cls = ClassUtils.forName(className, getApplicationContext().getClassLoader());
			Field field = cls.getField(fieldName);

			if (net.sf.jasperreports.engine.JRExporterParameter.class.isAssignableFrom(field.getType())) {
				try {
					return (net.sf.jasperreports.engine.JRExporterParameter) field.get(null);
				}
				catch (IllegalAccessException ex) {
					throw new IllegalArgumentException(
							"Unable to access field [" + fieldName + "] of class [" + className + "]. " +
							"Check that it is static and accessible.");
				}
			}
			else {
				throw new IllegalArgumentException("Field [" + fieldName + "] on class [" + className +
						"] is not assignable from JRExporterParameter - check the type of this field.");
			}
		}
		catch (ClassNotFoundException ex) {
			throw new IllegalArgumentException(
					"Class [" + className + "] in key [" + fqFieldName + "] could not be found.");
		}
		catch (NoSuchFieldException ex) {
			throw new IllegalArgumentException("Field [" + fieldName + "] in key [" + fqFieldName +
					"] could not be found on class [" + className + "].");
		}
	}

	/**
	 * Load the main {@code JasperReport} from the specified {@code Resource}.
	 * If the {@code Resource} points to an uncompiled report design file then the
	 * report file is compiled dynamically and loaded into memory.
	 * <p>
	 * 从指定的{@code资源}加载主要的{@code JasperReport}如果{@code资源}指向一个未编译的报表设计文件,那么报表文件将被动态编译并加载到内存中
	 * 
	 * 
	 * @return a {@code JasperReport} instance, or {@code null} if no main
	 * report has been statically defined
	 */
	protected JasperReport loadReport() {
		String url = getUrl();
		if (url == null) {
			return null;
		}
		Resource mainReport = getApplicationContext().getResource(url);
		return loadReport(mainReport);
	}

	/**
	 * Loads a {@code JasperReport} from the specified {@code Resource}.
	 * If the {@code Resource} points to an uncompiled report design file then
	 * the report file is compiled dynamically and loaded into memory.
	 * <p>
	 *  从指定的{@code资源}加载{@code JasperReport}如果{@code资源}指向未编译的报告设计文件,则报告文件将被动态编译并加载到内存中
	 * 
	 * 
	 * @param resource the {@code Resource} containing the report definition or design
	 * @return a {@code JasperReport} instance
	 */
	protected final JasperReport loadReport(Resource resource) {
		try {
			String filename = resource.getFilename();
			if (filename != null) {
				if (filename.endsWith(".jasper")) {
					// Load pre-compiled report.
					if (logger.isInfoEnabled()) {
						logger.info("Loading pre-compiled Jasper Report from " + resource);
					}
					InputStream is = resource.getInputStream();
					try {
						return (JasperReport) JRLoader.loadObject(is);
					}
					finally {
						is.close();
					}
				}
				else if (filename.endsWith(".jrxml")) {
					// Compile report on-the-fly.
					if (logger.isInfoEnabled()) {
						logger.info("Compiling Jasper Report loaded from " + resource);
					}
					InputStream is = resource.getInputStream();
					try {
						JasperDesign design = JRXmlLoader.load(is);
						return JasperCompileManager.compileReport(design);
					}
					finally {
						is.close();
					}
				}
			}
			throw new IllegalArgumentException(
					"Report filename [" + filename + "] must end in either .jasper or .jrxml");
		}
		catch (IOException ex) {
			throw new ApplicationContextException(
					"Could not load JasperReports report from " + resource, ex);
		}
		catch (JRException ex) {
			throw new ApplicationContextException(
					"Could not parse JasperReports report from " + resource, ex);
		}
	}


	/**
	 * Finds the report data to use for rendering the report and then invokes the
	 * {@link #renderReport} method that should be implemented by the subclass.
	 * <p>
	 *  查找用于呈现报表的报表数据,然后调用应由子类实现的{@link #renderReport}方法
	 * 
	 * 
	 * @param model the model map, as passed in for view rendering. Must contain
	 * a report data value that can be converted to a {@code JRDataSource},
	 * according to the rules of the {@link #fillReport} method.
	 */
	@Override
	protected void renderMergedOutputModel(
			Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		if (this.subReports != null) {
			// Expose sub-reports as model attributes.
			model.putAll(this.subReports);

			// Transform any collections etc into JRDataSources for sub reports.
			if (this.subReportDataKeys != null) {
				for (String key : this.subReportDataKeys) {
					model.put(key, convertReportData(model.get(key)));
				}
			}
		}

		// Expose Spring-managed Locale and MessageSource.
		exposeLocalizationContext(model, request);

		// Fill the report.
		JasperPrint filledReport = fillReport(model);
		postProcessReport(filledReport, model);

		// Prepare response and render report.
		populateHeaders(response);
		renderReport(filledReport, model, response);
	}

	/**
	 * Expose current Spring-managed Locale and MessageSource to JasperReports i18n
	 * ($R expressions etc). The MessageSource should only be exposed as JasperReports
	 * resource bundle if no such bundle is defined in the report itself.
	 * <p>The default implementation exposes the Spring RequestContext Locale and a
	 * MessageSourceResourceBundle adapter for the Spring ApplicationContext,
	 * analogous to the {@code JstlUtils.exposeLocalizationContext} method.
	 * <p>
	 * 将当前的Spring管理的区域设置和MessageSource暴露给JasperReports i18n($ R表达式等)MessageSource应该仅作为JasperReports资源包公开,如果在
	 * 报表本身中没有定义此类捆绑包,则默认实现会公开Spring RequestContext Locale和MessageSourceResourceBundle适用于Spring ApplicationC
	 * ontext,类似于{@code JstlUtilsexposeLocalizationContext}方法。
	 * 
	 * 
	 * @see org.springframework.web.servlet.support.RequestContextUtils#getLocale
	 * @see org.springframework.context.support.MessageSourceResourceBundle
	 * @see #getApplicationContext()
	 * @see net.sf.jasperreports.engine.JRParameter#REPORT_LOCALE
	 * @see net.sf.jasperreports.engine.JRParameter#REPORT_RESOURCE_BUNDLE
	 * @see org.springframework.web.servlet.support.JstlUtils#exposeLocalizationContext
	 */
	protected void exposeLocalizationContext(Map<String, Object> model, HttpServletRequest request) {
		RequestContext rc = new RequestContext(request, getServletContext());
		Locale locale = rc.getLocale();
		if (!model.containsKey(JRParameter.REPORT_LOCALE)) {
			model.put(JRParameter.REPORT_LOCALE, locale);
		}
		TimeZone timeZone = rc.getTimeZone();
		if (timeZone != null && !model.containsKey(JRParameter.REPORT_TIME_ZONE)) {
			model.put(JRParameter.REPORT_TIME_ZONE, timeZone);
		}
		JasperReport report = getReport();
		if ((report == null || report.getResourceBundle() == null) &&
				!model.containsKey(JRParameter.REPORT_RESOURCE_BUNDLE)) {
			model.put(JRParameter.REPORT_RESOURCE_BUNDLE,
					new MessageSourceResourceBundle(rc.getMessageSource(), locale));
		}
	}

	/**
	 * Create a populated {@code JasperPrint} instance from the configured
	 * {@code JasperReport} instance.
	 * <p>By default, this method will use any {@code JRDataSource} instance
	 * (or wrappable {@code Object}) that can be located using {@link #setReportDataKey},
	 * a lookup for type {@code JRDataSource} in the model Map, or a special value
	 * retrieved via {@link #getReportData}.
	 * <p>If no {@code JRDataSource} can be found, this method will use a JDBC
	 * {@code Connection} obtained from the configured {@code javax.sql.DataSource}
	 * (or a DataSource attribute in the model). If no JDBC DataSource can be found
	 * either, the JasperReports engine will be invoked with plain model Map,
	 * assuming that the model contains parameters that identify the source
	 * for report data (e.g. Hibernate or JPA queries).
	 * <p>
	 * 从配置的{@code JasperReport}实例创建一个填充的{@code JasperPrint}实例<p>默认情况下,此方法将使用{@code JRDataSource}实例(或可封装的{@code Object}
	 * ),可以使用{ @link #setReportDataKey},在模型Map中查找类型为{@code JRDataSource},或通过{@link #getReportData} <p>检索的特殊值
	 * 如果没有找到{@code JRDataSource},则此方法将使用从配置的{@code javaxsqlDataSource}(或模型中的DataSource属性)获取的JDBC {@code连接}如
	 * 果还没有找到JDBC DataSource,则JasperReports引擎将使用纯模型Map调用,假设模型包含标识报告数据源的参数(例如Hibernate或JPA查询)。
	 * 
	 * 
	 * @param model the model for this request
	 * @throws IllegalArgumentException if no {@code JRDataSource} can be found
	 * and no {@code javax.sql.DataSource} is supplied
	 * @throws SQLException if there is an error when populating the report using
	 * the {@code javax.sql.DataSource}
	 * @throws JRException if there is an error when populating the report using
	 * a {@code JRDataSource}
	 * @return the populated {@code JasperPrint} instance
	 * @see #getReportData
	 * @see #setJdbcDataSource
	 */
	protected JasperPrint fillReport(Map<String, Object> model) throws Exception {
		// Determine main report.
		JasperReport report = getReport();
		if (report == null) {
			throw new IllegalStateException("No main report defined for 'fillReport' - " +
					"specify a 'url' on this view or override 'getReport()' or 'fillReport(Map)'");
		}

		JRDataSource jrDataSource = null;
		DataSource jdbcDataSourceToUse = null;

		// Try model attribute with specified name.
		if (this.reportDataKey != null) {
			Object reportDataValue = model.get(this.reportDataKey);
			if (reportDataValue instanceof DataSource) {
				jdbcDataSourceToUse = (DataSource) reportDataValue;
			}
			else {
				jrDataSource = convertReportData(reportDataValue);
			}
		}
		else {
			Collection<?> values = model.values();
			jrDataSource = CollectionUtils.findValueOfType(values, JRDataSource.class);
			if (jrDataSource == null) {
				JRDataSourceProvider provider = CollectionUtils.findValueOfType(values, JRDataSourceProvider.class);
				if (provider != null) {
					jrDataSource = createReport(provider);
				}
				else {
					jdbcDataSourceToUse = CollectionUtils.findValueOfType(values, DataSource.class);
					if (jdbcDataSourceToUse == null) {
						jdbcDataSourceToUse = this.jdbcDataSource;
					}
				}
			}
		}

		if (jdbcDataSourceToUse != null) {
			return doFillReport(report, model, jdbcDataSourceToUse);
		}
		else {
			// Determine JRDataSource for main report.
			if (jrDataSource == null) {
				jrDataSource = getReportData(model);
			}
			if (jrDataSource != null) {
				// Use the JasperReports JRDataSource.
				if (logger.isDebugEnabled()) {
					logger.debug("Filling report with JRDataSource [" + jrDataSource + "]");
				}
				return JasperFillManager.fillReport(report, model, jrDataSource);
			}
			else {
				// Assume that the model contains parameters that identify
				// the source for report data (e.g. Hibernate or JPA queries).
				logger.debug("Filling report with plain model");
				return JasperFillManager.fillReport(report, model);
			}
		}
	}

	/**
	 * Fill the given report using the given JDBC DataSource and model.
	 * <p>
	 * 使用给定的JDBC DataSource和模型填写给定的报告
	 * 
	 */
	private JasperPrint doFillReport(JasperReport report, Map<String, Object> model, DataSource ds) throws Exception {
		// Use the JDBC DataSource.
		if (logger.isDebugEnabled()) {
			logger.debug("Filling report using JDBC DataSource [" + ds + "]");
		}
		Connection con = ds.getConnection();
		try {
			return JasperFillManager.fillReport(report, model, con);
		}
		finally {
			try {
				con.close();
			}
			catch (Throwable ex) {
				logger.debug("Could not close JDBC Connection", ex);
			}
		}
	}

	/**
	 * Populates the headers in the {@code HttpServletResponse} with the
	 * headers supplied by the user.
	 * <p>
	 *  使用用户提供的标题填充{@code HttpServletResponse}中的标题
	 * 
	 */
	private void populateHeaders(HttpServletResponse response) {
		// Apply the headers to the response.
		for (Enumeration<?> en = this.headers.propertyNames(); en.hasMoreElements();) {
			String key = (String) en.nextElement();
			response.addHeader(key, this.headers.getProperty(key));
		}
	}

	/**
	 * Determine the {@code JasperReport} to fill.
	 * Called by {@link #fillReport}.
	 * <p>The default implementation returns the report as statically configured
	 * through the 'url' property (and loaded by {@link #loadReport()}).
	 * Can be overridden in subclasses in order to dynamically obtain a
	 * {@code JasperReport} instance. As an alternative, consider
	 * overriding the {@link #fillReport} template method itself.
	 * <p>
	 *  确定{@code JasperReport}填充{@link #fillReport}调用<p>默认实现通过'url'属性(由{@link #loadReport()加载)静态配置报告)可以在子类中被覆盖以动态获取{@code JasperReport}
	 * 实例作为替代,考虑覆盖{@link #fillReport}模板方法本身。
	 * 
	 * 
	 * @return an instance of {@code JasperReport}
	 */
	protected JasperReport getReport() {
		return this.report;
	}

	/**
	 * Create an appropriate {@code JRDataSource} for passed-in report data.
	 * Called by {@link #fillReport} when its own lookup steps were not successful.
	 * <p>The default implementation looks for a value of type {@code java.util.Collection}
	 * or object array (in that order). Can be overridden in subclasses.
	 * <p>
	 * 为传入的报告数据创建一个适当的{@code JRDataSource}当{@link #fillReport}自己的查找步骤不成功时调用{p>默认实现查找类型为{@code javautilCollection}
	 * 或对象数组的值(按顺序)可以在子类中被覆盖。
	 * 
	 * 
	 * @param model the model map, as passed in for view rendering
	 * @return the {@code JRDataSource} or {@code null} if the data source is not found
	 * @see #getReportDataTypes
	 * @see #convertReportData
	 */
	protected JRDataSource getReportData(Map<String, Object> model) {
		// Try to find matching attribute, of given prioritized types.
		Object value = CollectionUtils.findValueOfType(model.values(), getReportDataTypes());
		return (value != null ? convertReportData(value) : null);
	}

	/**
	 * Convert the given report data value to a {@code JRDataSource}.
	 * <p>The default implementation delegates to {@code JasperReportUtils} unless
	 * the report data value is an instance of {@code JRDataSourceProvider}.
	 * A {@code JRDataSource}, {@code JRDataSourceProvider},
	 * {@code java.util.Collection} or object array is detected.
	 * {@code JRDataSource}s are returned as is, whilst {@code JRDataSourceProvider}s
	 * are used to create an instance of {@code JRDataSource} which is then returned.
	 * The latter two are converted to {@code JRBeanCollectionDataSource} or
	 * {@code JRBeanArrayDataSource}, respectively.
	 * <p>
	 * 将给定的报告数据值转换为{@code JRDataSource} <p>除非报表数据值是{@code JRDataSourceProvider} A {@code JRDataSource} {@code的实例,否则默认实现将委托给{@code JasperReportUtils}
	 *  JRDataSourceProvider},{@code javautilCollection}或对象数组被检测到,{@code JRDataSource}被原样返回,而{@code JRDataSourceProvider}
	 * 用于创建一个{@code JRDataSource}的实例,然后返回。
	 * 后两个分别转换为{@code JRBeanCollectionDataSource}或{@code JRBeanArrayDataSource}。
	 * 
	 * 
	 * @param value the report data value to convert
	 * @return the JRDataSource
	 * @throws IllegalArgumentException if the value could not be converted
	 * @see org.springframework.ui.jasperreports.JasperReportsUtils#convertReportData
	 * @see net.sf.jasperreports.engine.JRDataSource
	 * @see net.sf.jasperreports.engine.JRDataSourceProvider
	 * @see net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
	 * @see net.sf.jasperreports.engine.data.JRBeanArrayDataSource
	 */
	protected JRDataSource convertReportData(Object value) throws IllegalArgumentException {
		if (value instanceof JRDataSourceProvider) {
			return createReport((JRDataSourceProvider) value);
		}
		else {
			return JasperReportsUtils.convertReportData(value);
		}
	}

	/**
	 * Create a report using the given provider.
	 * <p>
	 *  使用给定的提供者创建报告
	 * 
	 * 
	 * @param provider the JRDataSourceProvider to use
	 * @return the created report
	 */
	protected JRDataSource createReport(JRDataSourceProvider provider) {
		try {
			JasperReport report = getReport();
			if (report == null) {
				throw new IllegalStateException("No main report defined for JRDataSourceProvider - " +
						"specify a 'url' on this view or override 'getReport()'");
			}
			return provider.create(report);
		}
		catch (JRException ex) {
			throw new IllegalArgumentException("Supplied JRDataSourceProvider is invalid", ex);
		}
	}

	/**
	 * Return the value types that can be converted to a {@code JRDataSource},
	 * in prioritized order. Should only return types that the
	 * {@link #convertReportData} method is actually able to convert.
	 * <p>Default value types are: {@code java.util.Collection} and {@code Object} array.
	 * <p>
	 * 返回可以转换为{@code JRDataSource}的值类型,以优先顺序排列。
	 * 只应返回类型{@link #convertReportData}实际上可以转换<p>默认值类型的类型是：{@code javautilCollection}和{@code Object}数组。
	 * 
	 * 
	 * @return the value types in prioritized order
	 */
	protected Class<?>[] getReportDataTypes() {
		return new Class<?>[] {Collection.class, Object[].class};
	}


	/**
	 * Template method to be overridden for custom post-processing of the
	 * populated report. Invoked after filling but before rendering.
	 * <p>The default implementation is empty.
	 * <p>
	 *  被覆盖的模板方法用于自定义后处理填充报告在填充之后但在呈现之前调用<p>默认实现为空
	 * 
	 * 
	 * @param populatedReport the populated {@code JasperPrint}
	 * @param model the map containing report parameters
	 * @throws Exception if post-processing failed
	 */
	protected void postProcessReport(JasperPrint populatedReport, Map<String, Object> model) throws Exception {
	}

	/**
	 * Subclasses should implement this method to perform the actual rendering process.
	 * <p>Note that the content type has not been set yet: Implementers should build
	 * a content type String and set it via {@code response.setContentType}.
	 * If necessary, this can include a charset clause for a specific encoding.
	 * The latter will only be necessary for textual output onto a Writer, and only
	 * in case of the encoding being specified in the JasperReports exporter parameters.
	 * <p><b>WARNING:</b> Implementers should not use {@code response.setCharacterEncoding}
	 * unless they are willing to depend on Servlet API 2.4 or higher. Prefer a
	 * concatenated content type String with a charset clause instead.
	 * <p>
	 * 子类应实现此方法来执行实际的渲染过程<p>请注意,内容类型尚未设置：实现者应构建一个内容类型String,并通过{@code responsesetContentType}进行设置如果需要,可以包含一个
	 * 字符集子句对于特定的编码,后者只对文本输出到Writer是有必要的,并且只有在JasperReports导出器参数<p> <b>中指定编码的情况下才能使用。
	 * 
	 * @param populatedReport the populated {@code JasperPrint} to render
	 * @param model the map containing report parameters
	 * @param response the HTTP response the report should be rendered to
	 * @throws Exception if rendering failed
	 * @see #getContentType()
	 * @see javax.servlet.ServletResponse#setContentType
	 * @see javax.servlet.ServletResponse#setCharacterEncoding
	 */
	protected abstract void renderReport(
			JasperPrint populatedReport, Map<String, Object> model, HttpServletResponse response)
			throws Exception;

}
