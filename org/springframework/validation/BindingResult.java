/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2012 the original author or authors.
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
import java.util.Map;

import org.springframework.beans.PropertyEditorRegistry;

/**
 * General interface that represents binding results. Extends the
 * {@link Errors interface} for error registration capabilities,
 * allowing for a {@link Validator} to be applied, and adds
 * binding-specific analysis and model building.
 *
 * <p>Serves as result holder for a {@link DataBinder}, obtained via
 * the {@link DataBinder#getBindingResult()} method. BindingResult
 * implementations can also be used directly, for example to invoke
 * a {@link Validator} on it (e.g. as part of a unit test).
 *
 * <p>
 * 表示绑定结果的通用界面扩展了{@link错误接口}以进行错误注册功能,允许应用{@link验证器},并添加特定于绑定的分析和模型构建
 * 
 *  <p>通过{@link DataBinder#getBindingResult()}方法获取的{@link DataBinder}的结果持有者也可以直接使用BindingResult实现,例如在其上调
 * 用{@link Validator}(例如作为单元测试的一部分)。
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0
 * @see DataBinder
 * @see Errors
 * @see Validator
 * @see BeanPropertyBindingResult
 * @see DirectFieldBindingResult
 * @see MapBindingResult
 */
public interface BindingResult extends Errors {

	/**
	 * Prefix for the name of the BindingResult instance in a model,
	 * followed by the object name.
	 * <p>
	 *  模型中BindingResult实例的名称的前缀,后跟对象名称
	 * 
	 */
	String MODEL_KEY_PREFIX = BindingResult.class.getName() + ".";


	/**
	 * Return the wrapped target object, which may be a bean, an object with
	 * public fields, a Map - depending on the concrete binding strategy.
	 * <p>
	 *  返回包装的目标对象,可能是一个bean,一个带有公共字段的对象,一个Map,这取决于具体的绑定策略
	 * 
	 */
	Object getTarget();

	/**
	 * Return a model Map for the obtained state, exposing a BindingResult
	 * instance as '{@link #MODEL_KEY_PREFIX MODEL_KEY_PREFIX} + objectName'
	 * and the object itself as 'objectName'.
	 * <p>Note that the Map is constructed every time you're calling this method.
	 * Adding things to the map and then re-calling this method will not work.
	 * <p>The attributes in the model Map returned by this method are usually
	 * included in the {@link org.springframework.web.servlet.ModelAndView}
	 * for a form view that uses Spring's {@code bind} tag in a JSP,
	 * which needs access to the BindingResult instance. Spring's pre-built
	 * form controllers will do this for you when rendering a form view.
	 * When building the ModelAndView instance yourself, you need to include
	 * the attributes from the model Map returned by this method.
	 * <p>
	 * 返回所获取状态的模型映射,将BindingResult实例显示为"{@link #MODEL_KEY_PREFIX MODEL_KEY_PREFIX} + objectName",将对象本身显示为"ob
	 * jectName"<p>请注意,每次调用此方法时都会构建Map向地图添加东西,然后重新调用此方法将无法正常工作。
	 * 此方法返回的模型Map中的属性通常包含在使用Spring的{@code bind}的窗体视图的{@link orgspringframeworkwebservletModelAndView}中需要访问B
	 * indingResult实例的JSP中的标记Spring的预构建表单控制器将在呈现表单视图时为您执行此操作当您自己构建ModelAndView实例时,需要包含来自模型返回的Map的属性。
	 * 
	 * 
	 * @see #getObjectName()
	 * @see #MODEL_KEY_PREFIX
	 * @see org.springframework.web.servlet.ModelAndView
	 * @see org.springframework.web.servlet.tags.BindTag
	 */
	Map<String, Object> getModel();

	/**
	 * Extract the raw field value for the given field.
	 * Typically used for comparison purposes.
	 * <p>
	 * 提取给定字段的原始字段值通常用于比较目的
	 * 
	 * 
	 * @param field the field to check
	 * @return the current value of the field in its raw form,
	 * or {@code null} if not known
	 */
	Object getRawFieldValue(String field);

	/**
	 * Find a custom property editor for the given type and property.
	 * <p>
	 *  找到给定类型和属性的自定义属性编辑器
	 * 
	 * 
	 * @param field the path of the property (name or nested path), or
	 * {@code null} if looking for an editor for all properties of the given type
	 * @param valueType the type of the property (can be {@code null} if a property
	 * is given but should be specified in any case for consistency checking)
	 * @return the registered editor, or {@code null} if none
	 */
	PropertyEditor findEditor(String field, Class<?> valueType);

	/**
	 * Return the underlying PropertyEditorRegistry.
	 * <p>
	 *  返回底层的PropertyEditorRegistry
	 * 
	 * 
	 * @return the PropertyEditorRegistry, or {@code null} if none
	 * available for this BindingResult
	 */
	PropertyEditorRegistry getPropertyEditorRegistry();

	/**
	 * Add a custom {@link ObjectError} or {@link FieldError} to the errors list.
	 * <p>Intended to be used by cooperating strategies such as {@link BindingErrorProcessor}.
	 * <p>
	 *  将自定义{@link ObjectError}或{@link FieldError}添加到错误列表<p>预期由合作策略使用,例如{@link BindingErrorProcessor}
	 * 
	 * 
	 * @see ObjectError
	 * @see FieldError
	 * @see BindingErrorProcessor
	 */
	void addError(ObjectError error);

	/**
	 * Resolve the given error code into message codes.
	 * <p>Calls the configured {@link MessageCodesResolver} with appropriate parameters.
	 * <p>
	 *  将给定的错误代码解析成消息代码<p>使用适当的参数调用配置的{@link MessageCodesResolver}
	 * 
	 * 
	 * @param errorCode the error code to resolve into message codes
	 * @return the resolved message codes
	 */
	String[] resolveMessageCodes(String errorCode);

	/**
	 * Resolve the given error code into message codes for the given field.
	 * <p>Calls the configured {@link MessageCodesResolver} with appropriate parameters.
	 * <p>
	 *  将给定的错误代码解析为给定字段的消息代码<p>使用适当的参数调用配置的{@link MessageCodesResolver}
	 * 
	 * 
	 * @param errorCode the error code to resolve into message codes
	 * @param field the field to resolve message codes for
	 * @return the resolved message codes
	 */
	String[] resolveMessageCodes(String errorCode, String field);

	/**
	 * Mark the specified disallowed field as suppressed.
	 * <p>The data binder invokes this for each field value that was
	 * detected to target a disallowed field.
	 * <p>
	 * 将指定的不允许字段标记为已抑制<p>数据绑定器为检测到的每个字段值调用此值,以定位不允许的字段
	 * 
	 * 
	 * @see DataBinder#setAllowedFields
	 */
	void recordSuppressedField(String field);

	/**
	 * Return the list of fields that were suppressed during the bind process.
	 * <p>Can be used to determine whether any field values were targeting
	 * disallowed fields.
	 * <p>
	 *  返回在绑定过程中被压制的字段列表<p>可用于确定任何字段值是否定位不允许的字段
	 * 
	 * @see DataBinder#setAllowedFields
	 */
	String[] getSuppressedFields();

}
