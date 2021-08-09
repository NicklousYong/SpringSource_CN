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

package org.springframework.validation;

import java.beans.PropertyEditor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessException;
import org.springframework.beans.PropertyAccessorUtils;
import org.springframework.beans.PropertyBatchUpdateException;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.Formatter;
import org.springframework.format.support.FormatterPropertyEditorAdapter;
import org.springframework.lang.UsesJava8;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;

/**
 * Binder that allows for setting property values onto a target object,
 * including support for validation and binding result analysis.
 * The binding process can be customized through specifying allowed fields,
 * required fields, custom editors, etc.
 *
 * <p>Note that there are potential security implications in failing to set an array
 * of allowed fields. In the case of HTTP form POST data for example, malicious clients
 * can attempt to subvert an application by supplying values for fields or properties
 * that do not exist on the form. In some cases this could lead to illegal data being
 * set on command objects <i>or their nested objects</i>. For this reason, it is
 * <b>highly recommended to specify the {@link #setAllowedFields allowedFields} property</b>
 * on the DataBinder.
 *
 * <p>The binding results can be examined via the {@link BindingResult} interface,
 * extending the {@link Errors} interface: see the {@link #getBindingResult()} method.
 * Missing fields and property access exceptions will be converted to {@link FieldError FieldErrors},
 * collected in the Errors instance, using the following error codes:
 *
 * <ul>
 * <li>Missing field error: "required"
 * <li>Type mismatch error: "typeMismatch"
 * <li>Method invocation error: "methodInvocation"
 * </ul>
 *
 * <p>By default, binding errors get resolved through the {@link BindingErrorProcessor}
 * strategy, processing for missing fields and property access exceptions: see the
 * {@link #setBindingErrorProcessor} method. You can override the default strategy
 * if needed, for example to generate different error codes.
 *
 * <p>Custom validation errors can be added afterwards. You will typically want to resolve
 * such error codes into proper user-visible error messages; this can be achieved through
 * resolving each error via a {@link org.springframework.context.MessageSource}, which is
 * able to resolve an {@link ObjectError}/{@link FieldError} through its
 * {@link org.springframework.context.MessageSource#getMessage(org.springframework.context.MessageSourceResolvable, java.util.Locale)}
 * method. The list of message codes can be customized through the {@link MessageCodesResolver}
 * strategy: see the {@link #setMessageCodesResolver} method. {@link DefaultMessageCodesResolver}'s
 * javadoc states details on the default resolution rules.
 *
 * <p>This generic data binder can be used in any kind of environment.
 * It is typically used by Spring web MVC controllers, via the web-specific
 * subclasses {@link org.springframework.web.bind.ServletRequestDataBinder}
 * and {@link org.springframework.web.portlet.bind.PortletRequestDataBinder}.
 *
 * <p>
 * 允许将属性值设置到目标对象上的绑定,包括对验证和绑定结果分析的支持绑定过程可以通过指定允许的字段,必填字段,自定义编辑器等进行自定义
 * 
 *  <p>请注意,无法设置允许字段数组有潜在的安全隐患在例如HTTP表单POST数据的情况下,恶意客户端可以尝试通过提供不存在的字段或属性的值来颠覆应用程序表单在某些情况下,这可能会导致在命令对象i或其嵌
 * 套对象上设置非法数据</i>因此,强烈建议您指定{@link #setAllowedFields allowedFields}属性< / b>在DataBinder上。
 * 
 * <p>绑定结果可以通过{@link BindingResult}接口检查,扩展{@link错误}界面：请参阅{@link #getBindingResult()}方法缺少字段和属性访问异常将转换为{@链接FieldError FieldErrors}
 * ,收集在Errors实例中,使用以下错误代码：。
 * 
 * <ul>
 *  <li>缺少字段错误："required"<li>类型不匹配错误："typeMismatch"<li>方法调用错误："methodInvocation"
 * </ul>
 * 
 *  <p>默认情况下,通过{@link BindingErrorProcessor}策略解决绑定错误,处理缺少的字段和属性访问异常：请参阅{@link #setBindingErrorProcessor}
 * 方法如果需要,您可以覆盖默认策略,例如生成不同的错误代码。
 * 
 * <p>以后可以添加自定义验证错误您通常会将此类错误代码解析为正确的用户可见错误消息;这可以通过{@link orgspringframeworkcontextMessageSource}解决每个错误来实
 * 现,它可以通过其{@link orgspringframeworkcontextMessageSource#getMessage(orgspringframeworkcontextMessageSourceResolvable,javautilLocale))方法来解析{@link ObjectError}
 *  / {@ link FieldError}方法列表消息代码可以通过{@link MessageCodesResolver}策略进行自定义：请参阅{@link #setMessageCodesResolver}
 * 方法{@link DefaultMessageCodesResolver}的javadoc状态关于默认分辨率规则的详细信息。
 * 
 * <p>此通用数据绑定器可用于任何类型的环境它通常由Spring Web MVC控制器通过Web特定子类{@link orgspringframeworkwebbindServletRequestDataBinder}
 * 和{@link orgspringframeworkwebportletbindPortletRequestDataBinder}使用。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Stephane Nicoll
 * @see #setAllowedFields
 * @see #setRequiredFields
 * @see #registerCustomEditor
 * @see #setMessageCodesResolver
 * @see #setBindingErrorProcessor
 * @see #bind
 * @see #getBindingResult
 * @see DefaultMessageCodesResolver
 * @see DefaultBindingErrorProcessor
 * @see org.springframework.context.MessageSource
 * @see org.springframework.web.bind.ServletRequestDataBinder
 */
public class DataBinder implements PropertyEditorRegistry, TypeConverter {

	/** Default object name used for binding: "target" */
	public static final String DEFAULT_OBJECT_NAME = "target";

	/** Default limit for array and collection growing: 256 */
	public static final int DEFAULT_AUTO_GROW_COLLECTION_LIMIT = 256;


	/**
	 * We'll create a lot of DataBinder instances: Let's use a static logger.
	 * <p>
	 *  我们将创建大量的DataBinder实例：让我们使用静态记录器
	 * 
	 */
	protected static final Log logger = LogFactory.getLog(DataBinder.class);

	private static Class<?> javaUtilOptionalClass = null;

	static {
		try {
			javaUtilOptionalClass =
					ClassUtils.forName("java.util.Optional", DataBinder.class.getClassLoader());
		}
		catch (ClassNotFoundException ex) {
			// Java 8 not available - Optional references simply not supported then.
		}
	}


	private final Object target;

	private final String objectName;

	private AbstractPropertyBindingResult bindingResult;

	private SimpleTypeConverter typeConverter;

	private boolean ignoreUnknownFields = true;

	private boolean ignoreInvalidFields = false;

	private boolean autoGrowNestedPaths = true;

	private int autoGrowCollectionLimit = DEFAULT_AUTO_GROW_COLLECTION_LIMIT;

	private String[] allowedFields;

	private String[] disallowedFields;

	private String[] requiredFields;

	private BindingErrorProcessor bindingErrorProcessor = new DefaultBindingErrorProcessor();

	private final List<Validator> validators = new ArrayList<Validator>();

	private ConversionService conversionService;


	/**
	 * Create a new DataBinder instance, with default object name.
	 * <p>
	 *  创建一个新的DataBinder实例,具有默认对象名称
	 * 
	 * 
	 * @param target the target object to bind onto (or {@code null}
	 * if the binder is just used to convert a plain parameter value)
	 * @see #DEFAULT_OBJECT_NAME
	 */
	public DataBinder(Object target) {
		this(target, DEFAULT_OBJECT_NAME);
	}

	/**
	 * Create a new DataBinder instance.
	 * <p>
	 *  创建一个新的DataBinder实例
	 * 
	 * 
	 * @param target the target object to bind onto (or {@code null}
	 * if the binder is just used to convert a plain parameter value)
	 * @param objectName the name of the target object
	 */
	public DataBinder(Object target, String objectName) {
		if (target != null && target.getClass() == javaUtilOptionalClass) {
			this.target = OptionalUnwrapper.unwrap(target);
		}
		else {
			this.target = target;
		}
		this.objectName = objectName;
	}


	/**
	 * Return the wrapped target object.
	 * <p>
	 *  返回包装的目标对象
	 * 
	 */
	public Object getTarget() {
		return this.target;
	}

	/**
	 * Return the name of the bound object.
	 * <p>
	 *  返回绑定对象的名称
	 * 
	 */
	public String getObjectName() {
		return this.objectName;
	}

	/**
	 * Set whether this binder should attempt to "auto-grow" a nested path that contains a null value.
	 * <p>If "true", a null path location will be populated with a default object value and traversed
	 * instead of resulting in an exception. This flag also enables auto-growth of collection elements
	 * when accessing an out-of-bounds index.
	 * <p>Default is "true" on a standard DataBinder. Note that since Spring 4.1 this feature is supported
	 * for bean property access (DataBinder's default mode) and field access.
	 * <p>
	 * 设置此绑定器是否应尝试"自动增长"包含空值的嵌套路径<p>如果为"true",将使用默认对象值填充空路径位置,而不是导致异常。
	 * 此标志还允许在访问超出索引时自动增长收集元素<p>标准DataBinder上的默认值为"true"请注意,由于Spring 41此功能支持bean属性访问(DataBinder的默认模式)和字段访问。
	 * 
	 * 
	 * @see #initBeanPropertyAccess()
	 * @see org.springframework.beans.BeanWrapper#setAutoGrowNestedPaths
	 */
	public void setAutoGrowNestedPaths(boolean autoGrowNestedPaths) {
		Assert.state(this.bindingResult == null,
				"DataBinder is already initialized - call setAutoGrowNestedPaths before other configuration methods");
		this.autoGrowNestedPaths = autoGrowNestedPaths;
	}

	/**
	 * Return whether "auto-growing" of nested paths has been activated.
	 * <p>
	 *  返回是否已激活嵌套路径的"自动增长"
	 * 
	 */
	public boolean isAutoGrowNestedPaths() {
		return this.autoGrowNestedPaths;
	}

	/**
	 * Specify the limit for array and collection auto-growing.
	 * <p>Default is 256, preventing OutOfMemoryErrors in case of large indexes.
	 * Raise this limit if your auto-growing needs are unusually high.
	 * <p>
	 *  指定数组和集合自动增长的限制<p>默认值为256,防止OutOfMemoryErrors在大索引的情况下提高此限制,如果您的自动增长需求异常高
	 * 
	 */
	public void setAutoGrowCollectionLimit(int autoGrowCollectionLimit) {
		this.autoGrowCollectionLimit = autoGrowCollectionLimit;
	}

	/**
	 * Return the current limit for array and collection auto-growing.
	 * <p>
	 * 返回阵列和集合自动增长的当前限制
	 * 
	 */
	public int getAutoGrowCollectionLimit() {
		return this.autoGrowCollectionLimit;
	}

	/**
	 * Initialize standard JavaBean property access for this DataBinder.
	 * <p>This is the default; an explicit call just leads to eager initialization.
	 * <p>
	 *  初始化此DataBinder的标准JavaBean属性访问<p>这是默认值;一个明确的调用只是导致了急切的初始化
	 * 
	 * 
	 * @see #initDirectFieldAccess()
	 * @see #createBeanPropertyBindingResult()
	 */
	public void initBeanPropertyAccess() {
		Assert.state(this.bindingResult == null,
				"DataBinder is already initialized - call initBeanPropertyAccess before other configuration methods");
		this.bindingResult = createBeanPropertyBindingResult();
	}

	/**
	 * Create the {@link AbstractPropertyBindingResult} instance using standard
	 * JavaBean property access.
	 * <p>
	 *  使用标准的JavaBean属性访问创建{@link AbstractPropertyBindingResult}实例
	 * 
	 * 
	 * @since 4.2.1
	 */
	protected AbstractPropertyBindingResult createBeanPropertyBindingResult() {
		BeanPropertyBindingResult result = new BeanPropertyBindingResult(getTarget(),
				getObjectName(), isAutoGrowNestedPaths(), getAutoGrowCollectionLimit());
		if (this.conversionService != null) {
			result.initConversion(this.conversionService);
		}
		return result;
	}

	/**
	 * Initialize direct field access for this DataBinder,
	 * as alternative to the default bean property access.
	 * <p>
	 *  初始化此DataBinder的直接字段访问,作为默认bean属性访问的替代方法
	 * 
	 * 
	 * @see #initBeanPropertyAccess()
	 * @see #createDirectFieldBindingResult()
	 */
	public void initDirectFieldAccess() {
		Assert.state(this.bindingResult == null,
				"DataBinder is already initialized - call initDirectFieldAccess before other configuration methods");
		this.bindingResult = createDirectFieldBindingResult();
	}

	/**
	 * Create the {@link AbstractPropertyBindingResult} instance using direct
	 * field access.
	 * <p>
	 *  使用直接字段访问创建{@link AbstractPropertyBindingResult}实例
	 * 
	 * 
	 * @since 4.2.1
	 */
	protected AbstractPropertyBindingResult createDirectFieldBindingResult() {
		DirectFieldBindingResult result = new DirectFieldBindingResult(getTarget(),
				getObjectName(), isAutoGrowNestedPaths());
		if (this.conversionService != null) {
			result.initConversion(this.conversionService);
		}
		return result;
	}

	/**
	 * Return the internal BindingResult held by this DataBinder,
	 * as an AbstractPropertyBindingResult.
	 * <p>
	 *  返回此DataBinder持有的内部BindingResult,作为AbstractPropertyBindingResult
	 * 
	 */
	protected AbstractPropertyBindingResult getInternalBindingResult() {
		if (this.bindingResult == null) {
			initBeanPropertyAccess();
		}
		return this.bindingResult;
	}

	/**
	 * Return the underlying PropertyAccessor of this binder's BindingResult.
	 * <p>
	 *  返回此绑定器的BindingResult的底层PropertyAccessor
	 * 
	 */
	protected ConfigurablePropertyAccessor getPropertyAccessor() {
		return getInternalBindingResult().getPropertyAccessor();
	}

	/**
	 * Return this binder's underlying SimpleTypeConverter.
	 * <p>
	 *  返回此binder的底层SimpleTypeConverter
	 * 
	 */
	protected SimpleTypeConverter getSimpleTypeConverter() {
		if (this.typeConverter == null) {
			this.typeConverter = new SimpleTypeConverter();
			if (this.conversionService != null) {
				this.typeConverter.setConversionService(this.conversionService);
			}
		}
		return this.typeConverter;
	}

	/**
	 * Return the underlying TypeConverter of this binder's BindingResult.
	 * <p>
	 *  返回此绑定器的BindingResult的底层TypeConverter
	 * 
	 */
	protected PropertyEditorRegistry getPropertyEditorRegistry() {
		if (getTarget() != null) {
			return getInternalBindingResult().getPropertyAccessor();
		}
		else {
			return getSimpleTypeConverter();
		}
	}

	/**
	 * Return the underlying TypeConverter of this binder's BindingResult.
	 * <p>
	 * 返回此绑定器的BindingResult的底层TypeConverter
	 * 
	 */
	protected TypeConverter getTypeConverter() {
		if (getTarget() != null) {
			return getInternalBindingResult().getPropertyAccessor();
		}
		else {
			return getSimpleTypeConverter();
		}
	}

	/**
	 * Return the BindingResult instance created by this DataBinder.
	 * This allows for convenient access to the binding results after
	 * a bind operation.
	 * <p>
	 *  返回此DataBinder创建的BindingResult实例这允许在绑定操作后方便地访问绑定结果
	 * 
	 * 
	 * @return the BindingResult instance, to be treated as BindingResult
	 * or as Errors instance (Errors is a super-interface of BindingResult)
	 * @see Errors
	 * @see #bind
	 */
	public BindingResult getBindingResult() {
		return getInternalBindingResult();
	}


	/**
	 * Set whether to ignore unknown fields, that is, whether to ignore bind
	 * parameters that do not have corresponding fields in the target object.
	 * <p>Default is "true". Turn this off to enforce that all bind parameters
	 * must have a matching field in the target object.
	 * <p>Note that this setting only applies to <i>binding</i> operations
	 * on this DataBinder, not to <i>retrieving</i> values via its
	 * {@link #getBindingResult() BindingResult}.
	 * <p>
	 *  设置是否忽略未知字段,即是否忽略目标对象中没有相应字段的绑定参数<p>默认值为"true"将此值关闭以强制所有绑定参数必须在目标中具有匹配字段对象<p>请注意,此设置仅适用于此DataBinder上的
	 * <i>绑定</i>操作,而不是<i>通过其{@link #getBindingResult()BindingResult}检索</i>值<。
	 * 
	 * 
	 * @see #bind
	 */
	public void setIgnoreUnknownFields(boolean ignoreUnknownFields) {
		this.ignoreUnknownFields = ignoreUnknownFields;
	}

	/**
	 * Return whether to ignore unknown fields when binding.
	 * <p>
	 *  返回是否在绑定时忽略未知字段
	 * 
	 */
	public boolean isIgnoreUnknownFields() {
		return this.ignoreUnknownFields;
	}

	/**
	 * Set whether to ignore invalid fields, that is, whether to ignore bind
	 * parameters that have corresponding fields in the target object which are
	 * not accessible (for example because of null values in the nested path).
	 * <p>Default is "false". Turn this on to ignore bind parameters for
	 * nested objects in non-existing parts of the target object graph.
	 * <p>Note that this setting only applies to <i>binding</i> operations
	 * on this DataBinder, not to <i>retrieving</i> values via its
	 * {@link #getBindingResult() BindingResult}.
	 * <p>
	 * 设置是否忽略无效字段,即是否忽略具有目标对象中不可访问的对应字段的绑定参数(例如由于嵌套路径中的空值)<p>默认值为"false"忽略目标对象图的非现有部分中嵌套对象的绑定参数<p>请注意,此设置仅适用
	 * 于此DataBinder上的<i>绑定</i>操作,而不适用于<i>检索</i>值通过其{@link #getBindingResult()BindingResult}。
	 * 
	 * 
	 * @see #bind
	 */
	public void setIgnoreInvalidFields(boolean ignoreInvalidFields) {
		this.ignoreInvalidFields = ignoreInvalidFields;
	}

	/**
	 * Return whether to ignore invalid fields when binding.
	 * <p>
	 *  返回是否在绑定时忽略无效字段
	 * 
	 */
	public boolean isIgnoreInvalidFields() {
		return this.ignoreInvalidFields;
	}

	/**
	 * Register fields that should be allowed for binding. Default is all
	 * fields. Restrict this for example to avoid unwanted modifications
	 * by malicious users when binding HTTP request parameters.
	 * <p>Supports "xxx*", "*xxx" and "*xxx*" patterns. More sophisticated matching
	 * can be implemented by overriding the {@code isAllowed} method.
	 * <p>Alternatively, specify a list of <i>disallowed</i> fields.
	 * <p>
	 * 注册应该允许绑定的字段默认为所有字段限制此范例,以避免恶意用户在绑定HTTP请求参数时进行不必要的修改<p>支持"xxx *","* xxx"和"* xxx *"模式更复杂可以通过覆盖{@code isAllowed}
	 * 方法来实现匹配<p>或者,指定<i>不允许的</i>字段的列表。
	 * 
	 * 
	 * @param allowedFields array of field names
	 * @see #setDisallowedFields
	 * @see #isAllowed(String)
	 * @see org.springframework.web.bind.ServletRequestDataBinder
	 */
	public void setAllowedFields(String... allowedFields) {
		this.allowedFields = PropertyAccessorUtils.canonicalPropertyNames(allowedFields);
	}

	/**
	 * Return the fields that should be allowed for binding.
	 * <p>
	 *  返回应允许绑定的字段
	 * 
	 * 
	 * @return array of field names
	 */
	public String[] getAllowedFields() {
		return this.allowedFields;
	}

	/**
	 * Register fields that should <i>not</i> be allowed for binding. Default is none.
	 * Mark fields as disallowed for example to avoid unwanted modifications
	 * by malicious users when binding HTTP request parameters.
	 * <p>Supports "xxx*", "*xxx" and "*xxx*" patterns. More sophisticated matching
	 * can be implemented by overriding the {@code isAllowed} method.
	 * <p>Alternatively, specify a list of <i>allowed</i> fields.
	 * <p>
	 * 允许<i>不允许绑定</i>的字段默认为无标记字段为不允许,以避免恶意用户在绑定HTTP请求参数时进行不必要的修改<p>支持"xxx *","* xxx"和"* xxx *"模式更复杂的匹配可以通过覆盖
	 * {@code isAllowed}方法来实现<p>或者,指定<i>允许的</i>字段的列表。
	 * 
	 * 
	 * @param disallowedFields array of field names
	 * @see #setAllowedFields
	 * @see #isAllowed(String)
	 * @see org.springframework.web.bind.ServletRequestDataBinder
	 */
	public void setDisallowedFields(String... disallowedFields) {
		this.disallowedFields = PropertyAccessorUtils.canonicalPropertyNames(disallowedFields);
	}

	/**
	 * Return the fields that should <i>not</i> be allowed for binding.
	 * <p>
	 *  返回允许<i>不</i>绑定的字段
	 * 
	 * 
	 * @return array of field names
	 */
	public String[] getDisallowedFields() {
		return this.disallowedFields;
	}

	/**
	 * Register fields that are required for each binding process.
	 * <p>If one of the specified fields is not contained in the list of
	 * incoming property values, a corresponding "missing field" error
	 * will be created, with error code "required" (by the default
	 * binding error processor).
	 * <p>
	 *  注册每个绑定过程所需的字段<p>如果其中一个指定字段未包含在传入属性值列表中,则将创建相应的"缺少字段"错误,并显示错误代码"required"(默认情况下)绑定错误处理器)
	 * 
	 * 
	 * @param requiredFields array of field names
	 * @see #setBindingErrorProcessor
	 * @see DefaultBindingErrorProcessor#MISSING_FIELD_ERROR_CODE
	 */
	public void setRequiredFields(String... requiredFields) {
		this.requiredFields = PropertyAccessorUtils.canonicalPropertyNames(requiredFields);
		if (logger.isDebugEnabled()) {
			logger.debug("DataBinder requires binding of required fields [" +
					StringUtils.arrayToCommaDelimitedString(requiredFields) + "]");
		}
	}

	/**
	 * Return the fields that are required for each binding process.
	 * <p>
	 * 返回每个绑定过程所需的字段
	 * 
	 * 
	 * @return array of field names
	 */
	public String[] getRequiredFields() {
		return this.requiredFields;
	}

	/**
	 * Set whether to extract the old field value when applying a
	 * property editor to a new value for a field.
	 * <p>Default is "true", exposing previous field values to custom editors.
	 * Turn this to "false" to avoid side effects caused by getters.
	 * <p>
	 *  设置是否在将属性编辑器应用于字段的新值时提取旧的字段值<p>默认值为"true",将以前的字段值显示为自定义编辑器将其设置为"false"以避免由getter引起的副作用
	 * 
	 */
	public void setExtractOldValueForEditor(boolean extractOldValueForEditor) {
		getPropertyAccessor().setExtractOldValueForEditor(extractOldValueForEditor);
	}

	/**
	 * Set the strategy to use for resolving errors into message codes.
	 * Applies the given strategy to the underlying errors holder.
	 * <p>Default is a DefaultMessageCodesResolver.
	 * <p>
	 *  设置用于将错误解决为消息代码的策略将给定的策略应用于底层错误持有者<p>默认值为DefaultMessageCodesResolver
	 * 
	 * 
	 * @see BeanPropertyBindingResult#setMessageCodesResolver
	 * @see DefaultMessageCodesResolver
	 */
	public void setMessageCodesResolver(MessageCodesResolver messageCodesResolver) {
		getInternalBindingResult().setMessageCodesResolver(messageCodesResolver);
	}

	/**
	 * Set the strategy to use for processing binding errors, that is,
	 * required field errors and {@code PropertyAccessException}s.
	 * <p>Default is a DefaultBindingErrorProcessor.
	 * <p>
	 *  设置用于处理绑定错误的策略,即必需的字段错误和{@code PropertyAccessException} s <p> Default是一个DefaultBindingErrorProcessor。
	 * 
	 * 
	 * @see DefaultBindingErrorProcessor
	 */
	public void setBindingErrorProcessor(BindingErrorProcessor bindingErrorProcessor) {
		Assert.notNull(bindingErrorProcessor, "BindingErrorProcessor must not be null");
		this.bindingErrorProcessor = bindingErrorProcessor;
	}

	/**
	 * Return the strategy for processing binding errors.
	 * <p>
	 *  返回处理绑定错误的策略
	 * 
	 */
	public BindingErrorProcessor getBindingErrorProcessor() {
		return this.bindingErrorProcessor;
	}

	/**
	 * Set the Validator to apply after each binding step.
	 * <p>
	 *  将验证器设置为在每个装订步骤之后应用
	 * 
	 * 
	 * @see #addValidators(Validator...)
	 * @see #replaceValidators(Validator...)
	 */
	public void setValidator(Validator validator) {
		assertValidators(validator);
		this.validators.clear();
		this.validators.add(validator);
	}

	private void assertValidators(Validator... validators) {
		Assert.notNull(validators, "Validators required");
		for (Validator validator : validators) {
			if (validator != null && (getTarget() != null && !validator.supports(getTarget().getClass()))) {
				throw new IllegalStateException("Invalid target for Validator [" + validator + "]: " + getTarget());
			}
		}
	}

	/**
	 * Add Validators to apply after each binding step.
	 * <p>
	 *  在每个绑定步骤后添加验证器以应用
	 * 
	 * 
	 * @see #setValidator(Validator)
	 * @see #replaceValidators(Validator...)
	 */
	public void addValidators(Validator... validators) {
		assertValidators(validators);
		this.validators.addAll(Arrays.asList(validators));
	}

	/**
	 * Replace the Validators to apply after each binding step.
	 * <p>
	 * 在每个绑定步骤后,更换验证器以应用
	 * 
	 * 
	 * @see #setValidator(Validator)
	 * @see #addValidators(Validator...)
	 */
	public void replaceValidators(Validator... validators) {
		assertValidators(validators);
		this.validators.clear();
		this.validators.addAll(Arrays.asList(validators));
	}

	/**
	 * Return the primary Validator to apply after each binding step, if any.
	 * <p>
	 *  返回主验证器以在每个绑定步骤之后应用(如果有)
	 * 
	 */
	public Validator getValidator() {
		return (this.validators.size() > 0 ? this.validators.get(0) : null);
	}

	/**
	 * Return the Validators to apply after data binding.
	 * <p>
	 *  返回验证器在数据绑定后应用
	 * 
	 */
	public List<Validator> getValidators() {
		return Collections.unmodifiableList(this.validators);
	}


	//---------------------------------------------------------------------
	// Implementation of PropertyEditorRegistry/TypeConverter interface
	//---------------------------------------------------------------------

	/**
	 * Specify a Spring 3.0 ConversionService to use for converting
	 * property values, as an alternative to JavaBeans PropertyEditors.
	 * <p>
	 *  指定用于转换属性值的Spring 30 ConversionService,作为JavaBeans PropertyEditor的替代方法
	 * 
	 */
	public void setConversionService(ConversionService conversionService) {
		Assert.state(this.conversionService == null, "DataBinder is already initialized with ConversionService");
		this.conversionService = conversionService;
		if (this.bindingResult != null && conversionService != null) {
			this.bindingResult.initConversion(conversionService);
		}
	}

	/**
	 * Return the associated ConversionService, if any.
	 * <p>
	 *  返回相关的ConversionService(如果有)
	 * 
	 */
	public ConversionService getConversionService() {
		return this.conversionService;
	}

	/**
	 * Add a custom formatter, applying it to all fields matching the
	 * {@link Formatter}-declared type.
	 * <p>Registers a corresponding {@link PropertyEditor} adapter underneath the covers.
	 * <p>
	 *  添加自定义格式化程序,将其应用于与{@link Formatter}  - 声明类型匹配的所有字段<p>在封面下注册相应的{@link PropertyEditor}适配器
	 * 
	 * 
	 * @param formatter the formatter to add, generically declared for a specific type
	 * @since 4.2
	 * @see #registerCustomEditor(Class, PropertyEditor)
	 */
	public void addCustomFormatter(Formatter<?> formatter) {
		FormatterPropertyEditorAdapter adapter = new FormatterPropertyEditorAdapter(formatter);
		getPropertyEditorRegistry().registerCustomEditor(adapter.getFieldType(), adapter);
	}

	/**
	 * Add a custom formatter for the field type specified in {@link Formatter} class,
	 * applying it to the specified fields only, if any, or otherwise to all fields.
	 * <p>Registers a corresponding {@link PropertyEditor} adapter underneath the covers.
	 * <p>
	 *  为{@link Formatter}类中指定的字段类型添加自定义格式化程序,仅将其应用于指定的字段(如果有的话),否则将其应用于所有字段<p>在封面下注册相应的{@link PropertyEditor}
	 * 适配器。
	 * 
	 * 
	 * @param formatter the formatter to add, generically declared for a specific type
	 * @param fields the fields to apply the formatter to, or none if to be applied to all
	 * @since 4.2
	 * @see #registerCustomEditor(Class, String, PropertyEditor)
	 */
	public void addCustomFormatter(Formatter<?> formatter, String... fields) {
		FormatterPropertyEditorAdapter adapter = new FormatterPropertyEditorAdapter(formatter);
		Class<?> fieldType = adapter.getFieldType();
		if (ObjectUtils.isEmpty(fields)) {
			getPropertyEditorRegistry().registerCustomEditor(fieldType, adapter);
		}
		else {
			for (String field : fields) {
				getPropertyEditorRegistry().registerCustomEditor(fieldType, field, adapter);
			}
		}
	}

	/**
	 * Add a custom formatter, applying it to the specified field types only, if any,
	 * or otherwise to all fields matching the {@link Formatter}-declared type.
	 * <p>Registers a corresponding {@link PropertyEditor} adapter underneath the covers.
	 * <p>
	 * 添加自定义格式化程序,仅将其应用于指定的字段类型(如果有),否则应用于与{@link Formatter}  - 声明类型匹配的所有字段<p>在封面下注册相应的{@link PropertyEditor}
	 * 适配器。
	 * 
	 * 
	 * @param formatter the formatter to add (does not need to generically declare a
	 * field type if field types are explicitly specified as parameters)
	 * @param fieldTypes the field types to apply the formatter to, or none if to be
	 * derived from the given {@link Formatter} implementation class
	 * @since 4.2
	 * @see #registerCustomEditor(Class, PropertyEditor)
	 */
	public void addCustomFormatter(Formatter<?> formatter, Class<?>... fieldTypes) {
		FormatterPropertyEditorAdapter adapter = new FormatterPropertyEditorAdapter(formatter);
		if (ObjectUtils.isEmpty(fieldTypes)) {
			getPropertyEditorRegistry().registerCustomEditor(adapter.getFieldType(), adapter);
		}
		else {
			for (Class<?> fieldType : fieldTypes) {
				getPropertyEditorRegistry().registerCustomEditor(fieldType, adapter);
			}
		}
	}

	@Override
	public void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor) {
		getPropertyEditorRegistry().registerCustomEditor(requiredType, propertyEditor);
	}

	@Override
	public void registerCustomEditor(Class<?> requiredType, String field, PropertyEditor propertyEditor) {
		getPropertyEditorRegistry().registerCustomEditor(requiredType, field, propertyEditor);
	}

	@Override
	public PropertyEditor findCustomEditor(Class<?> requiredType, String propertyPath) {
		return getPropertyEditorRegistry().findCustomEditor(requiredType, propertyPath);
	}

	@Override
	public <T> T convertIfNecessary(Object value, Class<T> requiredType) throws TypeMismatchException {
		return getTypeConverter().convertIfNecessary(value, requiredType);
	}

	@Override
	public <T> T convertIfNecessary(Object value, Class<T> requiredType, MethodParameter methodParam)
			throws TypeMismatchException {

		return getTypeConverter().convertIfNecessary(value, requiredType, methodParam);
	}

	@Override
	public <T> T convertIfNecessary(Object value, Class<T> requiredType, Field field)
			throws TypeMismatchException {

		return getTypeConverter().convertIfNecessary(value, requiredType, field);
	}


	/**
	 * Bind the given property values to this binder's target.
	 * <p>This call can create field errors, representing basic binding
	 * errors like a required field (code "required"), or type mismatch
	 * between value and bean property (code "typeMismatch").
	 * <p>Note that the given PropertyValues should be a throwaway instance:
	 * For efficiency, it will be modified to just contain allowed fields if it
	 * implements the MutablePropertyValues interface; else, an internal mutable
	 * copy will be created for this purpose. Pass in a copy of the PropertyValues
	 * if you want your original instance to stay unmodified in any case.
	 * <p>
	 * 将给定的属性值绑定到此binder的目标<p>此调用可以创建字段错误,表示基本绑定错误,如必填字段(代码"必需")或值与bean属性之间的类型不匹配(代码"typeMismatch")<p >请注意,给
	 * 定的PropertyValues应该是一个一次性的实例：为了效率,如果实现MutablePropertyValues接口,它将被修改为仅包含允许的字段;否则,将为此创建一个内部可变副本如果您希望原始实例
	 * 在任何情况下保持不变,则传递到PropertyValues的副本中。
	 * 
	 * 
	 * @param pvs property values to bind
	 * @see #doBind(org.springframework.beans.MutablePropertyValues)
	 */
	public void bind(PropertyValues pvs) {
		MutablePropertyValues mpvs = (pvs instanceof MutablePropertyValues) ?
				(MutablePropertyValues) pvs : new MutablePropertyValues(pvs);
		doBind(mpvs);
	}

	/**
	 * Actual implementation of the binding process, working with the
	 * passed-in MutablePropertyValues instance.
	 * <p>
	 *  绑定过程的实际实现,与传入的MutablePropertyValues实例一起使用
	 * 
	 * 
	 * @param mpvs the property values to bind,
	 * as MutablePropertyValues instance
	 * @see #checkAllowedFields
	 * @see #checkRequiredFields
	 * @see #applyPropertyValues
	 */
	protected void doBind(MutablePropertyValues mpvs) {
		checkAllowedFields(mpvs);
		checkRequiredFields(mpvs);
		applyPropertyValues(mpvs);
	}

	/**
	 * Check the given property values against the allowed fields,
	 * removing values for fields that are not allowed.
	 * <p>
	 * 根据允许的字段检查给定的属性值,删除不允许的字段的值
	 * 
	 * 
	 * @param mpvs the property values to be bound (can be modified)
	 * @see #getAllowedFields
	 * @see #isAllowed(String)
	 */
	protected void checkAllowedFields(MutablePropertyValues mpvs) {
		PropertyValue[] pvs = mpvs.getPropertyValues();
		for (PropertyValue pv : pvs) {
			String field = PropertyAccessorUtils.canonicalPropertyName(pv.getName());
			if (!isAllowed(field)) {
				mpvs.removePropertyValue(pv);
				getBindingResult().recordSuppressedField(field);
				if (logger.isDebugEnabled()) {
					logger.debug("Field [" + field + "] has been removed from PropertyValues " +
							"and will not be bound, because it has not been found in the list of allowed fields");
				}
			}
		}
	}

	/**
	 * Return if the given field is allowed for binding.
	 * Invoked for each passed-in property value.
	 * <p>The default implementation checks for "xxx*", "*xxx" and "*xxx*" matches,
	 * as well as direct equality, in the specified lists of allowed fields and
	 * disallowed fields. A field matching a disallowed pattern will not be accepted
	 * even if it also happens to match a pattern in the allowed list.
	 * <p>Can be overridden in subclasses.
	 * <p>
	 *  如果给定字段被允许绑定返回每个传入属性值调用<p>默认实现检查"xxx *","* xxx"和"* xxx *"匹配以及直接相等允许字段和不允许字段的指定列表不允许匹配不允许的模式的字段,即使它也恰好
	 * 匹配允许列表中的模式<p>可以在子类中覆盖。
	 * 
	 * 
	 * @param field the field to check
	 * @return if the field is allowed
	 * @see #setAllowedFields
	 * @see #setDisallowedFields
	 * @see org.springframework.util.PatternMatchUtils#simpleMatch(String, String)
	 */
	protected boolean isAllowed(String field) {
		String[] allowed = getAllowedFields();
		String[] disallowed = getDisallowedFields();
		return ((ObjectUtils.isEmpty(allowed) || PatternMatchUtils.simpleMatch(allowed, field)) &&
				(ObjectUtils.isEmpty(disallowed) || !PatternMatchUtils.simpleMatch(disallowed, field)));
	}

	/**
	 * Check the given property values against the required fields,
	 * generating missing field errors where appropriate.
	 * <p>
	 *  根据所需字段检查给定的属性值,在适当的情况下生成缺少的字段错误
	 * 
	 * 
	 * @param mpvs the property values to be bound (can be modified)
	 * @see #getRequiredFields
	 * @see #getBindingErrorProcessor
	 * @see BindingErrorProcessor#processMissingFieldError
	 */
	protected void checkRequiredFields(MutablePropertyValues mpvs) {
		String[] requiredFields = getRequiredFields();
		if (!ObjectUtils.isEmpty(requiredFields)) {
			Map<String, PropertyValue> propertyValues = new HashMap<String, PropertyValue>();
			PropertyValue[] pvs = mpvs.getPropertyValues();
			for (PropertyValue pv : pvs) {
				String canonicalName = PropertyAccessorUtils.canonicalPropertyName(pv.getName());
				propertyValues.put(canonicalName, pv);
			}
			for (String field : requiredFields) {
				PropertyValue pv = propertyValues.get(field);
				boolean empty = (pv == null || pv.getValue() == null);
				if (!empty) {
					if (pv.getValue() instanceof String) {
						empty = !StringUtils.hasText((String) pv.getValue());
					}
					else if (pv.getValue() instanceof String[]) {
						String[] values = (String[]) pv.getValue();
						empty = (values.length == 0 || !StringUtils.hasText(values[0]));
					}
				}
				if (empty) {
					// Use bind error processor to create FieldError.
					getBindingErrorProcessor().processMissingFieldError(field, getInternalBindingResult());
					// Remove property from property values to bind:
					// It has already caused a field error with a rejected value.
					if (pv != null) {
						mpvs.removePropertyValue(pv);
						propertyValues.remove(field);
					}
				}
			}
		}
	}

	/**
	 * Apply given property values to the target object.
	 * <p>Default implementation applies all of the supplied property
	 * values as bean property values. By default, unknown fields will
	 * be ignored.
	 * <p>
	 * 将给定的属性值应用于目标对象<p>默认实现将所有提供的属性值应用为bean属性值默认情况下,未知字段将被忽略
	 * 
	 * 
	 * @param mpvs the property values to be bound (can be modified)
	 * @see #getTarget
	 * @see #getPropertyAccessor
	 * @see #isIgnoreUnknownFields
	 * @see #getBindingErrorProcessor
	 * @see BindingErrorProcessor#processPropertyAccessException
	 */
	protected void applyPropertyValues(MutablePropertyValues mpvs) {
		try {
			// Bind request parameters onto target object.
			getPropertyAccessor().setPropertyValues(mpvs, isIgnoreUnknownFields(), isIgnoreInvalidFields());
		}
		catch (PropertyBatchUpdateException ex) {
			// Use bind error processor to create FieldErrors.
			for (PropertyAccessException pae : ex.getPropertyAccessExceptions()) {
				getBindingErrorProcessor().processPropertyAccessException(pae, getInternalBindingResult());
			}
		}
	}


	/**
	 * Invoke the specified Validators, if any.
	 * <p>
	 *  调用指定的验证器(如果有)
	 * 
	 * 
	 * @see #setValidator(Validator)
	 * @see #getBindingResult()
	 */
	public void validate() {
		for (Validator validator : this.validators) {
			validator.validate(getTarget(), getBindingResult());
		}
	}

	/**
	 * Invoke the specified Validators, if any, with the given validation hints.
	 * <p>Note: Validation hints may get ignored by the actual target Validator.
	 * <p>
	 *  使用给定的验证提示调用指定的验证器(如果有)<p>注意：验证提示可能被实际目标验证器忽略
	 * 
	 * 
	 * @param validationHints one or more hint objects to be passed to a {@link SmartValidator}
	 * @see #setValidator(Validator)
	 * @see SmartValidator#validate(Object, Errors, Object...)
	 */
	public void validate(Object... validationHints) {
		for (Validator validator : getValidators()) {
			if (!ObjectUtils.isEmpty(validationHints) && validator instanceof SmartValidator) {
				((SmartValidator) validator).validate(getTarget(), getBindingResult(), validationHints);
			}
			else if (validator != null) {
				validator.validate(getTarget(), getBindingResult());
			}
		}
	}

	/**
	 * Close this DataBinder, which may result in throwing
	 * a BindException if it encountered any errors.
	 * <p>
	 *  关闭此DataBinder,如果遇到任何错误,可能会导致抛出BindException
	 * 
	 * 
	 * @return the model Map, containing target object and Errors instance
	 * @throws BindException if there were any errors in the bind operation
	 * @see BindingResult#getModel()
	 */
	public Map<?, ?> close() throws BindException {
		if (getBindingResult().hasErrors()) {
			throw new BindException(getBindingResult());
		}
		return getBindingResult().getModel();
	}


	/**
	 * Inner class to avoid a hard dependency on Java 8.
	 * <p>
	 *  内部类避免了对Java 8的严重依赖
	 */
	@UsesJava8
	private static class OptionalUnwrapper {

		public static Object unwrap(Object optionalObject) {
			Optional<?> optional = (Optional<?>) optionalObject;
			if (!optional.isPresent()) {
				return null;
			}
			Object result = optional.get();
			Assert.isTrue(!(result instanceof Optional), "Multi-level Optional usage not supported");
			return result;
		}
	}

}
