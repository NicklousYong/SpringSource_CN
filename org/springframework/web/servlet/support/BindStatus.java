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

package org.springframework.web.servlet.support;

import java.beans.PropertyEditor;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.context.NoSuchMessageException;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.util.HtmlUtils;

/**
 * Simple adapter to expose the bind status of a field or object.
 * Set as a variable both by the JSP bind tag and Velocity/FreeMarker macros.
 *
 * <p>Obviously, object status representations (i.e. errors at the object level
 * rather than the field level) do not have an expression and a value but only
 * error codes and messages. For simplicity's sake and to be able to use the same
 * tags and macros, the same status class is used for both scenarios.
 *
 * <p>
 *  用于公开字段或对象的绑定状态的简单适配器通过JSP绑定标签和Velocity / FreeMarker宏设置为变量
 * 
 * <p>显然,对象状态表示(即对象级而不是字段级的错误)没有表达式和值,但只有错误代码和消息为了简单起见,并且能够使用相同的标签和宏,对于这两种情况都使用相同的状态类
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Darren Davison
 * @see RequestContext#getBindStatus
 * @see org.springframework.web.servlet.tags.BindTag
 * @see org.springframework.web.servlet.view.AbstractTemplateView#setExposeSpringMacroHelpers
 */
public class BindStatus {

	private final RequestContext requestContext;

	private final String path;

	private final boolean htmlEscape;

	private final String expression;

	private final Errors errors;

	private BindingResult bindingResult;

	private Object value;

	private Class<?> valueType;

	private Object actualValue;

	private PropertyEditor editor;

	private List<? extends ObjectError> objectErrors;

	private String[] errorCodes;

	private String[] errorMessages;


	/**
	 * Create a new BindStatus instance, representing a field or object status.
	 * <p>
	 *  创建一个新的BindStatus实例,表示一个字段或对象状态
	 * 
	 * 
	 * @param requestContext the current RequestContext
	 * @param path the bean and property path for which values and errors
	 * will be resolved (e.g. "customer.address.street")
	 * @param htmlEscape whether to HTML-escape error messages and string values
	 * @throws IllegalStateException if no corresponding Errors object found
	 */
	public BindStatus(RequestContext requestContext, String path, boolean htmlEscape)
			throws IllegalStateException {

		this.requestContext = requestContext;
		this.path = path;
		this.htmlEscape = htmlEscape;

		// determine name of the object and property
		String beanName;
		int dotPos = path.indexOf('.');
		if (dotPos == -1) {
			// property not set, only the object itself
			beanName = path;
			this.expression = null;
		}
		else {
			beanName = path.substring(0, dotPos);
			this.expression = path.substring(dotPos + 1);
		}

		this.errors = requestContext.getErrors(beanName, false);

		if (this.errors != null) {
			// Usual case: A BindingResult is available as request attribute.
			// Can determine error codes and messages for the given expression.
			// Can use a custom PropertyEditor, as registered by a form controller.
			if (this.expression != null) {
				if ("*".equals(this.expression)) {
					this.objectErrors = this.errors.getAllErrors();
				}
				else if (this.expression.endsWith("*")) {
					this.objectErrors = this.errors.getFieldErrors(this.expression);
				}
				else {
					this.objectErrors = this.errors.getFieldErrors(this.expression);
					this.value = this.errors.getFieldValue(this.expression);
					this.valueType = this.errors.getFieldType(this.expression);
					if (this.errors instanceof BindingResult) {
						this.bindingResult = (BindingResult) this.errors;
						this.actualValue = this.bindingResult.getRawFieldValue(this.expression);
						this.editor = this.bindingResult.findEditor(this.expression, null);
					}
					else {
						this.actualValue = this.value;
					}
				}
			}
			else {
				this.objectErrors = this.errors.getGlobalErrors();
			}
			initErrorCodes();
		}

		else {
			// No BindingResult available as request attribute:
			// Probably forwarded directly to a form view.
			// Let's do the best we can: extract a plain target if appropriate.
			Object target = requestContext.getModelObject(beanName);
			if (target == null) {
				throw new IllegalStateException("Neither BindingResult nor plain target object for bean name '" +
						beanName + "' available as request attribute");
			}
			if (this.expression != null && !"*".equals(this.expression) && !this.expression.endsWith("*")) {
				BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(target);
				this.value = bw.getPropertyValue(this.expression);
				this.valueType = bw.getPropertyType(this.expression);
				this.actualValue = this.value;
			}
			this.errorCodes = new String[0];
			this.errorMessages = new String[0];
		}

		if (htmlEscape && this.value instanceof String) {
			this.value = HtmlUtils.htmlEscape((String) this.value);
		}
	}

	/**
	 * Extract the error codes from the ObjectError list.
	 * <p>
	 *  从ObjectError列表中提取错误代码
	 * 
	 */
	private void initErrorCodes() {
		this.errorCodes = new String[this.objectErrors.size()];
		for (int i = 0; i < this.objectErrors.size(); i++) {
			ObjectError error = this.objectErrors.get(i);
			this.errorCodes[i] = error.getCode();
		}
	}

	/**
	 * Extract the error messages from the ObjectError list.
	 * <p>
	 *  从ObjectError列表中提取错误消息
	 * 
	 */
	private void initErrorMessages() throws NoSuchMessageException {
		if (this.errorMessages == null) {
			this.errorMessages = new String[this.objectErrors.size()];
			for (int i = 0; i < this.objectErrors.size(); i++) {
				ObjectError error = this.objectErrors.get(i);
				this.errorMessages[i] = this.requestContext.getMessage(error, this.htmlEscape);
			}
		}
	}


	/**
	 * Return the bean and property path for which values and errors
	 * will be resolved (e.g. "customer.address.street").
	 * <p>
	 *  返回要解析值和错误的bean和属性路径(例如"customeraddressstreet")
	 * 
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * Return a bind expression that can be used in HTML forms as input name
	 * for the respective field, or {@code null} if not field-specific.
	 * <p>Returns a bind path appropriate for resubmission, e.g. "address.street".
	 * Note that the complete bind path as required by the bind tag is
	 * "customer.address.street", if bound to a "customer" bean.
	 * <p>
	 * 返回可用于HTML表单的绑定表达式作为相应字段的输入名称,或{@code null}(如果不是字段)<p>返回适用于重新提交的绑定路径,例如"addressstreet"请注意,完整绑定绑定标签所要求的
	 * 路径是"customeraddressstreet",如果绑定到"客户"bean。
	 * 
	 */
	public String getExpression() {
		return this.expression;
	}

	/**
	 * Return the current value of the field, i.e. either the property value
	 * or a rejected update, or {@code null} if not field-specific.
	 * <p>This value will be an HTML-escaped String if the original value
	 * already was a String.
	 * <p>
	 *  返回字段的当前值,即属性值或拒绝的更新,或{@code null}(如果不是字段)<p>如果原始值已为String,则该值将为HTML转义的String
	 * 
	 */
	public Object getValue() {
		return this.value;
	}

	/**
	 * Get the '{@code Class}' type of the field. Favor this instead of
	 * '{@code getValue().getClass()}' since '{@code getValue()}' may
	 * return '{@code null}'.
	 * <p>
	 *  获取'{@code Class}'类型的字段,而不是'{@code getValue()getClass()}',因为'{@code getValue()}'可能返回'{@code null}'
	 * 
	 */
	public Class<?> getValueType() {
		return this.valueType;
	}

	/**
	 * Return the actual value of the field, i.e. the raw property value,
	 * or {@code null} if not available.
	 * <p>
	 * 返回字段的实际值,即原始属性值,如果不可用,则返回{@code null}
	 * 
	 */
	public Object getActualValue() {
		return this.actualValue;
	}

	/**
	 * Return a suitable display value for the field, i.e. the stringified
	 * value if not null, and an empty string in case of a null value.
	 * <p>This value will be an HTML-escaped String if the original value
	 * was non-null: the {@code toString} result of the original value
	 * will get HTML-escaped.
	 * <p>
	 *  为字段返回一个合适的显示值,即非空值的字符串值,如果空值为空,则为空字符串<p>如果原始值为非空,则此值将为HTML转义字符串：{ @code toString}原始值的结果将获得HTML转义
	 * 
	 */
	public String getDisplayValue() {
		if (this.value instanceof String) {
			return (String) this.value;
		}
		if (this.value != null) {
			return (this.htmlEscape ? HtmlUtils.htmlEscape(this.value.toString()) : this.value.toString());
		}
		return "";
	}

	/**
	 * Return if this status represents a field or object error.
	 * <p>
	 *  如果此状态表示字段或对象错误,则返回
	 * 
	 */
	public boolean isError() {
		return (this.errorCodes != null && this.errorCodes.length > 0);
	}

	/**
	 * Return the error codes for the field or object, if any.
	 * Returns an empty array instead of null if none.
	 * <p>
	 *  返回字段或对象的错误代码(如果有)返回空数组,而不返回null
	 * 
	 */
	public String[] getErrorCodes() {
		return this.errorCodes;
	}

	/**
	 * Return the first error codes for the field or object, if any.
	 * <p>
	 *  返回字段或对象的第一个错误代码(如果有)
	 * 
	 */
	public String getErrorCode() {
		return (this.errorCodes.length > 0 ? this.errorCodes[0] : "");
	}

	/**
	 * Return the resolved error messages for the field or object,
	 * if any. Returns an empty array instead of null if none.
	 * <p>
	 *  返回解决的字段或对象的错误消息(如果有的话)返回一个空数组,而不返回null
	 * 
	 */
	public String[] getErrorMessages() {
		initErrorMessages();
		return this.errorMessages;
	}

	/**
	 * Return the first error message for the field or object, if any.
	 * <p>
	 *  返回字段或对象的第一个错误消息(如果有)
	 * 
	 */
	public String getErrorMessage() {
		initErrorMessages();
		return (this.errorMessages.length > 0 ? this.errorMessages[0] : "");
	}

	/**
	 * Return an error message string, concatenating all messages
	 * separated by the given delimiter.
	 * <p>
	 * 返回错误消息字符串,连接由给定分隔符分隔的所有消息
	 * 
	 * 
	 * @param delimiter separator string, e.g. ", " or "<br>"
	 * @return the error message string
	 */
	public String getErrorMessagesAsString(String delimiter) {
		initErrorMessages();
		return StringUtils.arrayToDelimitedString(this.errorMessages, delimiter);
	}

	/**
	 * Return the Errors instance (typically a BindingResult) that this
	 * bind status is currently associated with.
	 * <p>
	 *  返回此绑定状态当前关联的错误实例(通常为BindingResult)
	 * 
	 * 
	 * @return the current Errors instance, or {@code null} if none
	 * @see org.springframework.validation.BindingResult
	 */
	public Errors getErrors() {
		return this.errors;
	}

	/**
	 * Return the PropertyEditor for the property that this bind status
	 * is currently bound to.
	 * <p>
	 *  返回此绑定状态当前绑定到的属性的PropertyEditor
	 * 
	 * 
	 * @return the current PropertyEditor, or {@code null} if none
	 */
	public PropertyEditor getEditor() {
		return this.editor;
	}

	/**
	 * Find a PropertyEditor for the given value class, associated with
	 * the property that this bound status is currently bound to.
	 * <p>
	 *  找到与此绑定状态当前绑定到的属性相关联的给定值类的PropertyEditor
	 * 
	 * @param valueClass the value class that an editor is needed for
	 * @return the associated PropertyEditor, or {@code null} if none
	 */
	public PropertyEditor findEditor(Class<?> valueClass) {
		return (this.bindingResult != null ? this.bindingResult.findEditor(this.expression, valueClass) : null);
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("BindStatus: ");
		sb.append("expression=[").append(this.expression).append("]; ");
		sb.append("value=[").append(this.value).append("]");
		if (isError()) {
			sb.append("; errorCodes=").append(Arrays.asList(this.errorCodes));
		}
		return sb.toString();
	}

}
