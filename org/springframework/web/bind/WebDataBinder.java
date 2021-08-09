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

package org.springframework.web.bind;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.core.CollectionFactory;
import org.springframework.validation.DataBinder;
import org.springframework.web.multipart.MultipartFile;

/**
 * Special {@link DataBinder} for data binding from web request parameters
 * to JavaBean objects. Designed for web environments, but not dependent on
 * the Servlet API; serves as base class for more specific DataBinder variants,
 * such as {@link org.springframework.web.bind.ServletRequestDataBinder}.
 *
 * <p>Includes support for field markers which address a common problem with
 * HTML checkboxes and select options: detecting that a field was part of
 * the form, but did not generate a request parameter because it was empty.
 * A field marker allows to detect that state and reset the corresponding
 * bean property accordingly. Default values, for parameters that are otherwise
 * not present, can specify a value for the field other then empty.
 *
 * <p>
 * 用于从Web请求参数到JavaBean对象的数据绑定的特殊{@link DataBinder}专为Web环境而设计,但不依赖于Servlet API;作为更具体的DataBinder变体的基类,例如{@link orgspringframeworkwebbindServletRequestDataBinder}
 * 。
 * 
 *  <p>包括对字段标记的支持,它解决了HTML复选框和选择选项的常见问题：检测到一个字段是表单的一部分,但没有生成请求参数,因为它是空的字段标记允许检测该状态,相应地重置对应的bean属性默认值,对于否
 * 则不存在的参数,可以为其他字段指定一个值为空。
 * 
 * 
 * @author Juergen Hoeller
 * @author Scott Andrews
 * @author Brian Clozel
 * @since 1.2
 * @see #registerCustomEditor
 * @see #setAllowedFields
 * @see #setRequiredFields
 * @see #setFieldMarkerPrefix
 * @see #setFieldDefaultPrefix
 * @see ServletRequestDataBinder
 */
public class WebDataBinder extends DataBinder {

	/**
	 * Default prefix that field marker parameters start with, followed by the field
	 * name: e.g. "_subscribeToNewsletter" for a field "subscribeToNewsletter".
	 * <p>Such a marker parameter indicates that the field was visible, that is,
	 * existed in the form that caused the submission. If no corresponding field
	 * value parameter was found, the field will be reset. The value of the field
	 * marker parameter does not matter in this case; an arbitrary value can be used.
	 * This is particularly useful for HTML checkboxes and select options.
	 * <p>
	 * 字段标记参数开头的默认前缀,后跟字段名称：例如"_subscribeToNewsletter"用于字段"subscribeToNewsletter"<p>这样的标记参数指示该字段是可见的,即以导致提交的
	 * 形式存在如果没有找到对应的字段值参数,则该字段将被重置。
	 * 在这种情况下,字段标记参数的值无关紧要;可以使用任意值这对于HTML复选框和选择选项特别有用。
	 * 
	 * 
	 * @see #setFieldMarkerPrefix
	 */
	public static final String DEFAULT_FIELD_MARKER_PREFIX = "_";

	/**
	 * Default prefix that field default parameters start with, followed by the field
	 * name: e.g. "!subscribeToNewsletter" for a field "subscribeToNewsletter".
	 * <p>Default parameters differ from field markers in that they provide a default
	 * value instead of an empty value.
	 * <p>
	 *  字段默认参数开头的默认前缀,后跟字段名称：例如"！subscribeToNewsletter"用于字段"subscribeToNewsletter"<p>默认参数不同于字段标记,因为它们提供了默认值而
	 * 不是空值。
	 * 
	 * 
	 * @see #setFieldDefaultPrefix
	 */
	public static final String DEFAULT_FIELD_DEFAULT_PREFIX = "!";

	private String fieldMarkerPrefix = DEFAULT_FIELD_MARKER_PREFIX;

	private String fieldDefaultPrefix = DEFAULT_FIELD_DEFAULT_PREFIX;

	private boolean bindEmptyMultipartFiles = true;


	/**
	 * Create a new WebDataBinder instance, with default object name.
	 * <p>
	 * 创建一个新的WebDataBinder实例,具有默认对象名称
	 * 
	 * 
	 * @param target the target object to bind onto (or {@code null}
	 * if the binder is just used to convert a plain parameter value)
	 * @see #DEFAULT_OBJECT_NAME
	 */
	public WebDataBinder(Object target) {
		super(target);
	}

	/**
	 * Create a new WebDataBinder instance.
	 * <p>
	 *  创建一个新的WebDataBinder实例
	 * 
	 * 
	 * @param target the target object to bind onto (or {@code null}
	 * if the binder is just used to convert a plain parameter value)
	 * @param objectName the name of the target object
	 */
	public WebDataBinder(Object target, String objectName) {
		super(target, objectName);
	}


	/**
	 * Specify a prefix that can be used for parameters that mark potentially
	 * empty fields, having "prefix + field" as name. Such a marker parameter is
	 * checked by existence: You can send any value for it, for example "visible".
	 * This is particularly useful for HTML checkboxes and select options.
	 * <p>Default is "_", for "_FIELD" parameters (e.g. "_subscribeToNewsletter").
	 * Set this to null if you want to turn off the empty field check completely.
	 * <p>HTML checkboxes only send a value when they're checked, so it is not
	 * possible to detect that a formerly checked box has just been unchecked,
	 * at least not with standard HTML means.
	 * <p>One way to address this is to look for a checkbox parameter value if
	 * you know that the checkbox has been visible in the form, resetting the
	 * checkbox if no value found. In Spring web MVC, this typically happens
	 * in a custom {@code onBind} implementation.
	 * <p>This auto-reset mechanism addresses this deficiency, provided
	 * that a marker parameter is sent for each checkbox field, like
	 * "_subscribeToNewsletter" for a "subscribeToNewsletter" field.
	 * As the marker parameter is sent in any case, the data binder can
	 * detect an empty field and automatically reset its value.
	 * <p>
	 * 指定可用于标记潜在空字段的参数的前缀,具有"prefix + field"作为名称这样的标记参数由存在检查：您可以发送任何值,例如"可见"这对于HTML复选框和选择选项<p>默认为"_",对于"_FIE
	 * LD"参数(例如"_subscribeToNewsletter")如果要完全关闭空字段检查,请将其设置为null <p> HTML复选框只发送一个值他们被检查,所以不可能检测到以前的复选框刚被取消选中,
	 * 至少不符合标准HTML的意思<p>解决此问题的一种方法是查找复选框参数值,如果您知道复选框已在表单中显示,如果找不到值,请重置复选框在Spring web MVC中,这通常发生在自定义{@code onBind}
	 * 实现中<p>这个自动重置机制解决了这个缺陷,前提是每个复选框字段都会发送一个标记参数,如"subscribeToNewsletter"的"_subscribeToNewsletter"字段由于在任何情况
	 * 下发送标记参数,数据绑定器可以检测到一个空字段并自动重置其值。
	 * 
	 * 
	 * @see #DEFAULT_FIELD_MARKER_PREFIX
	 */
	public void setFieldMarkerPrefix(String fieldMarkerPrefix) {
		this.fieldMarkerPrefix = fieldMarkerPrefix;
	}

	/**
	 * Return the prefix for parameters that mark potentially empty fields.
	 * <p>
	 * 返回标记潜在空字段的参数的前缀
	 * 
	 */
	public String getFieldMarkerPrefix() {
		return this.fieldMarkerPrefix;
	}

	/**
	 * Specify a prefix that can be used for parameters that indicate default
	 * value fields, having "prefix + field" as name. The value of the default
	 * field is used when the field is not provided.
	 * <p>Default is "!", for "!FIELD" parameters (e.g. "!subscribeToNewsletter").
	 * Set this to null if you want to turn off the field defaults completely.
	 * <p>HTML checkboxes only send a value when they're checked, so it is not
	 * possible to detect that a formerly checked box has just been unchecked,
	 * at least not with standard HTML means.  A default field is especially
	 * useful when a checkbox represents a non-boolean value.
	 * <p>The presence of a default parameter preempts the behavior of a field
	 * marker for the given field.
	 * <p>
	 *  指定可用于指示默认值字段的参数的前缀,具有"prefix + field"作为名称当不提供字段时,将使用默认字段的值<p>默认值为"！",对于"！FIELD "参数(例如"！subscribeToNe
	 * wsletter")如果要将字段默认关闭,请将此值设置为null <p> HTML复选框仅在检查时才发送值,因此无法检测到以前的复选框刚刚被取消选中,至少不使用标准HTML意味着当复选框表示非布尔值时,
	 * 默认字段特别有用<p>默认参数的存在会抢占给定字段的字段标记的行为。
	 * 
	 * 
	 * @see #DEFAULT_FIELD_DEFAULT_PREFIX
	 */
	public void setFieldDefaultPrefix(String fieldDefaultPrefix) {
		this.fieldDefaultPrefix = fieldDefaultPrefix;
	}

	/**
	 * Return the prefix for parameters that mark default fields.
	 * <p>
	 * 返回标记默认字段的参数的前缀
	 * 
	 */
	public String getFieldDefaultPrefix() {
		return this.fieldDefaultPrefix;
	}

	/**
	 * Set whether to bind empty MultipartFile parameters. Default is "true".
	 * <p>Turn this off if you want to keep an already bound MultipartFile
	 * when the user resubmits the form without choosing a different file.
	 * Else, the already bound MultipartFile will be replaced by an empty
	 * MultipartFile holder.
	 * <p>
	 *  设置是否绑定空MultipartFile参数默认为"true"<p>如果要在用户重新提交表单而不选择其他文件时保留已经绑定的MultipartFile,请将其关闭。
	 * 否则,已经绑定的MultipartFile将被空多部分文件夹。
	 * 
	 * 
	 * @see org.springframework.web.multipart.MultipartFile
	 */
	public void setBindEmptyMultipartFiles(boolean bindEmptyMultipartFiles) {
		this.bindEmptyMultipartFiles = bindEmptyMultipartFiles;
	}

	/**
	 * Return whether to bind empty MultipartFile parameters.
	 * <p>
	 *  返回是否绑定空MultipartFile参数
	 * 
	 */
	public boolean isBindEmptyMultipartFiles() {
		return this.bindEmptyMultipartFiles;
	}


	/**
	 * This implementation performs a field default and marker check
	 * before delegating to the superclass binding process.
	 * <p>
	 *  此实现在委派到超类绑定过程之前执行字段默认和标记检查
	 * 
	 * 
	 * @see #checkFieldDefaults
	 * @see #checkFieldMarkers
	 */
	@Override
	protected void doBind(MutablePropertyValues mpvs) {
		checkFieldDefaults(mpvs);
		checkFieldMarkers(mpvs);
		super.doBind(mpvs);
	}

	/**
	 * Check the given property values for field defaults,
	 * i.e. for fields that start with the field default prefix.
	 * <p>The existence of a field defaults indicates that the specified
	 * value should be used if the field is otherwise not present.
	 * <p>
	 *  检查字段默认值的给定属性值,即以字段默认前缀开头的字段<p>字段默认值的存在表示如果该字段不存在,则应使用指定的值
	 * 
	 * 
	 * @param mpvs the property values to be bound (can be modified)
	 * @see #getFieldDefaultPrefix
	 */
	protected void checkFieldDefaults(MutablePropertyValues mpvs) {
		if (getFieldDefaultPrefix() != null) {
			String fieldDefaultPrefix = getFieldDefaultPrefix();
			PropertyValue[] pvArray = mpvs.getPropertyValues();
			for (PropertyValue pv : pvArray) {
				if (pv.getName().startsWith(fieldDefaultPrefix)) {
					String field = pv.getName().substring(fieldDefaultPrefix.length());
					if (getPropertyAccessor().isWritableProperty(field) && !mpvs.contains(field)) {
						mpvs.add(field, pv.getValue());
					}
					mpvs.removePropertyValue(pv);
				}
			}
		}
	}

	/**
	 * Check the given property values for field markers,
	 * i.e. for fields that start with the field marker prefix.
	 * <p>The existence of a field marker indicates that the specified
	 * field existed in the form. If the property values do not contain
	 * a corresponding field value, the field will be considered as empty
	 * and will be reset appropriately.
	 * <p>
	 * 检查字段标记的给定属性值,即以字段标记前缀开头的字段<p>字段标记的存在表示指定字段存在于表单中如果属性值不包含相应的字段值,则字段将被视为空,并将被适当地重置
	 * 
	 * 
	 * @param mpvs the property values to be bound (can be modified)
	 * @see #getFieldMarkerPrefix
	 * @see #getEmptyValue(String, Class)
	 */
	protected void checkFieldMarkers(MutablePropertyValues mpvs) {
		if (getFieldMarkerPrefix() != null) {
			String fieldMarkerPrefix = getFieldMarkerPrefix();
			PropertyValue[] pvArray = mpvs.getPropertyValues();
			for (PropertyValue pv : pvArray) {
				if (pv.getName().startsWith(fieldMarkerPrefix)) {
					String field = pv.getName().substring(fieldMarkerPrefix.length());
					if (getPropertyAccessor().isWritableProperty(field) && !mpvs.contains(field)) {
						Class<?> fieldType = getPropertyAccessor().getPropertyType(field);
						mpvs.add(field, getEmptyValue(field, fieldType));
					}
					mpvs.removePropertyValue(pv);
				}
			}
		}
	}

	/**
	 * Determine an empty value for the specified field.
	 * <p>Default implementation returns:
	 * <ul>
	 *     <li>{@code Boolean.FALSE} for boolean fields
	 *     <li>an empty array for array types
	 *     <li>Collection implementations for Collection types
	 *     <li>Map implementations for Map types
	 *     <li>else, {@code null} is used as default
	 * </ul>
	 * <p>
	 *  确定指定字段的空值<p>默认实现返回：
	 * <ul>
	 *  布尔字段的<li> {@ code BooleanFALSE} <li>数组类型的空数组<li>集合类型的集合实现<li>映射类型的映射实现<li> else,{@code null}用作默认值
	 * </ul>
	 * 
	 * @param field the name of the field
	 * @param fieldType the type of the field
	 * @return the empty value (for most fields: null)
	 */
	protected Object getEmptyValue(String field, Class<?> fieldType) {
		if (fieldType != null) {
			try {
				if (boolean.class == fieldType || Boolean.class == fieldType) {
					// Special handling of boolean property.
					return Boolean.FALSE;
				}
				else if (fieldType.isArray()) {
					// Special handling of array property.
					return Array.newInstance(fieldType.getComponentType(), 0);
				}
				else if (Collection.class.isAssignableFrom(fieldType)) {
					return CollectionFactory.createCollection(fieldType, 0);
				}
				else if (Map.class.isAssignableFrom(fieldType)) {
					return CollectionFactory.createMap(fieldType, 0);
				}
			} catch (IllegalArgumentException exc) {
				return null;
			}
		}
		// Default value: try null.
		return null;
	}

	/**
	 * Bind all multipart files contained in the given request, if any
	 * (in case of a multipart request).
	 * <p>Multipart files will only be added to the property values if they
	 * are not empty or if we're configured to bind empty multipart files too.
	 * <p>
	 * 
	 * @param multipartFiles Map of field name String to MultipartFile object
	 * @param mpvs the property values to be bound (can be modified)
	 * @see org.springframework.web.multipart.MultipartFile
	 * @see #setBindEmptyMultipartFiles
	 */
	protected void bindMultipart(Map<String, List<MultipartFile>> multipartFiles, MutablePropertyValues mpvs) {
		for (Map.Entry<String, List<MultipartFile>> entry : multipartFiles.entrySet()) {
			String key = entry.getKey();
			List<MultipartFile> values = entry.getValue();
			if (values.size() == 1) {
				MultipartFile value = values.get(0);
				if (isBindEmptyMultipartFiles() || !value.isEmpty()) {
					mpvs.add(key, value);
				}
			}
			else {
				mpvs.add(key, values);
			}
		}
	}

}
