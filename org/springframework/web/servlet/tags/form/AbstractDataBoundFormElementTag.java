/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2013 the original author or authors.
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

package org.springframework.web.servlet.tags.form;

import java.beans.PropertyEditor;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.springframework.beans.PropertyAccessor;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.support.RequestDataValueProcessor;
import org.springframework.web.servlet.tags.EditorAwareTag;
import org.springframework.web.servlet.tags.NestedPathTag;

/**
 * Base tag for all data-binding aware JSP form tags.
 *
 * <p>Provides the common {@link #setPath path} and {@link #setId id} properties.
 * Provides sub-classes with utility methods for accessing the {@link BindStatus}
 * of their bound value and also for {@link #writeOptionalAttribute interacting}
 * with the {@link TagWriter}.
 *
 * <p>
 *  所有数据绑定感知JSP表单标签的基础标签
 * 
 * <p>提供通用的{@link #setPath路径}和{@link #setId id}属性提供子类与其绑定值访问{@link BindStatus}的实用程序方法以及{@link #writeOptionalAttribute交互}
 * 与{@link TagWriter}。
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 */
@SuppressWarnings("serial")
public abstract class AbstractDataBoundFormElementTag extends AbstractFormTag implements EditorAwareTag {

	/**
	 * Name of the exposed path variable within the scope of this tag: "nestedPath".
	 * Same value as {@link org.springframework.web.servlet.tags.NestedPathTag#NESTED_PATH_VARIABLE_NAME}.
	 * <p>
	 *  此标记范围内的公开路径变量的名称："nestedPath"与{@link orgspringframeworkwebservlettagsNestedPathTag#NESTED_PATH_VARIABLE_NAME}
	 * 相同的值。
	 * 
	 */
	protected static final String NESTED_PATH_VARIABLE_NAME = NestedPathTag.NESTED_PATH_VARIABLE_NAME;


	/**
	 * The property path from the {@link FormTag#setModelAttribute form object}.
	 * <p>
	 *  来自{@link FormTag#setModelAttribute form object}的属性路径
	 * 
	 */
	private String path;

	/**
	 * The value of the '{@code id}' attribute.
	 * <p>
	 *  "{@code id}"属性的值
	 * 
	 */
	private String id;

	/**
	 * The {@link BindStatus} of this tag.
	 * <p>
	 *  此标记的{@link BindStatus}
	 * 
	 */
	private BindStatus bindStatus;


	/**
	 * Set the property path from the {@link FormTag#setModelAttribute form object}.
	 * May be a runtime expression.
	 * <p>
	 *  从{@link FormTag#setModelAttribute form object}设置属性路径可以是运行时表达式
	 * 
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Get the {@link #evaluate resolved} property path for the
	 * {@link FormTag#setModelAttribute form object}.
	 * <p>
	 * 获取{@link FormTag#setModelAttribute表单对象}的{@link #evaluate resolved}属性路径
	 * 
	 */
	protected final String getPath() throws JspException {
		String resolvedPath = (String) evaluate("path", this.path);
		return (resolvedPath != null ? resolvedPath : "");
	}

	/**
	 * Set the value of the '{@code id}' attribute.
	 * <p>May be a runtime expression; defaults to the value of {@link #getName()}.
	 * Note that the default value may not be valid for certain tags.
	 * <p>
	 *  设置"{@code id}"属性的值<p>可以是运行时表达式;默认为{@link #getName()}的值。请注意,默认值可能对某些标记无效
	 * 
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Get the value of the '{@code id}' attribute.
	 * <p>
	 *  获取"{@code id}"属性的值
	 * 
	 */
	@Override
	public String getId() {
		return this.id;
	}


	/**
	 * Writes the default set of attributes to the supplied {@link TagWriter}.
	 * Further abstract sub-classes should override this method to add in
	 * any additional default attributes but <strong>must</strong> remember
	 * to call the {@code super} method.
	 * <p>Concrete sub-classes should call this method when/if they want
	 * to render default attributes.
	 * <p>
	 *  将默认的属性集写入提供的{@link TagWriter}。
	 * 其他抽象子类应该覆盖此方法以添加任何其他默认属性,但<strong>必须</strong>请记住调用{@code super}方法< p>当/他们想要渲染默认属性时,具体子类应该调用此方法。
	 * 
	 * 
	 * @param tagWriter the {@link TagWriter} to which any attributes are to be written
	 */
	protected void writeDefaultAttributes(TagWriter tagWriter) throws JspException {
		writeOptionalAttribute(tagWriter, "id", resolveId());
		writeOptionalAttribute(tagWriter, "name", getName());
	}

	/**
	 * Determine the '{@code id}' attribute value for this tag,
	 * autogenerating one if none specified.
	 * <p>
	 *  确定此标签的"{@code id}"属性值,如果没有指定,则自动生成一个
	 * 
	 * 
	 * @see #getId()
	 * @see #autogenerateId()
	 */
	protected String resolveId() throws JspException {
		Object id = evaluate("id", getId());
		if (id != null) {
			String idString = id.toString();
			return (StringUtils.hasText(idString) ? idString : null);
		}
		return autogenerateId();
	}

	/**
	 * Autogenerate the '{@code id}' attribute value for this tag.
	 * <p>The default implementation simply delegates to {@link #getName()},
	 * deleting invalid characters (such as "[" or "]").
	 * <p>
	 * 自动生成此标签的"{@code id}"属性值<p>默认实现简单地委托给{@link #getName()},删除无效字符(例如"["或"]")
	 * 
	 */
	protected String autogenerateId() throws JspException {
		return StringUtils.deleteAny(getName(), "[]");
	}

	/**
	 * Get the value for the HTML '{@code name}' attribute.
	 * <p>The default implementation simply delegates to
	 * {@link #getPropertyPath()} to use the property path as the name.
	 * For the most part this is desirable as it links with the server-side
	 * expectation for data binding. However, some subclasses may wish to change
	 * the value of the '{@code name}' attribute without changing the bind path.
	 * <p>
	 *  获取HTML"{@code name}"属性的值<p>默认实现简单地委托给{@link #getPropertyPath()}以使用属性路径作为名称在大多数情况下,这是可取的,因为它链接数据绑定的服务
	 * 器端期望然而,某些子类可能希望在不更改绑定路径的情况下更改"{@code name}"属性的值。
	 * 
	 * 
	 * @return the value for the HTML '{@code name}' attribute
	 */
	protected String getName() throws JspException {
		return getPropertyPath();
	}

	/**
	 * Get the {@link BindStatus} for this tag.
	 * <p>
	 *  获取此标签的{@link BindStatus}
	 * 
	 */
	protected BindStatus getBindStatus() throws JspException {
		if (this.bindStatus == null) {
			// HTML escaping in tags is performed by the ValueFormatter class.
			String nestedPath = getNestedPath();
			String pathToUse = (nestedPath != null ? nestedPath + getPath() : getPath());
			if (pathToUse.endsWith(PropertyAccessor.NESTED_PROPERTY_SEPARATOR)) {
				pathToUse = pathToUse.substring(0, pathToUse.length() - 1);
			}
			this.bindStatus = new BindStatus(getRequestContext(), pathToUse, false);
		}
		return this.bindStatus;
	}

	/**
	 * Get the value of the nested path that may have been exposed by the
	 * {@link NestedPathTag}.
	 * <p>
	 *  获取可能由{@link NestedPathTag}公开的嵌套路径的值
	 * 
	 */
	protected String getNestedPath() {
		return (String) this.pageContext.getAttribute(NESTED_PATH_VARIABLE_NAME, PageContext.REQUEST_SCOPE);
	}

	/**
	 * Build the property path for this tag, including the nested path
	 * but <i>not</i> prefixed with the name of the form attribute.
	 * <p>
	 * 构建此标记的属性路径,包括嵌套路径,但<i>不</i>以表单属性的名称为前缀
	 * 
	 * 
	 * @see #getNestedPath()
	 * @see #getPath()
	 */
	protected String getPropertyPath() throws JspException {
		String expression = getBindStatus().getExpression();
		return (expression != null ? expression : "");
	}

	/**
	 * Get the bound value.
	 * <p>
	 *  获取绑定值
	 * 
	 * 
	 * @see #getBindStatus()
	 */
	protected final Object getBoundValue() throws JspException {
		return getBindStatus().getValue();
	}

	/**
	 * Get the {@link PropertyEditor}, if any, in use for value bound to this tag.
	 * <p>
	 *  获取绑定到此标签的值的{@link PropertyEditor}(如果有的话)
	 * 
	 */
	protected PropertyEditor getPropertyEditor() throws JspException {
		return getBindStatus().getEditor();
	}

	/**
	 * Exposes the {@link PropertyEditor} for {@link EditorAwareTag}.
	 * <p>Use {@link #getPropertyEditor()} for internal rendering purposes.
	 * <p>
	 *  为{@link EditorAwareTag}使用{@link PropertyEditor} <p>使用{@link #getPropertyEditor()}进行内部渲染
	 * 
	 */
	@Override
	public final PropertyEditor getEditor() throws JspException {
		return getPropertyEditor();
	}

	/**
	 * Get a display String for the given value, converted by a PropertyEditor
	 * that the BindStatus may have registered for the value's Class.
	 * <p>
	 *  获取给定值的显示字符串,由PropertyEditor转换,BindStatus可能已注册该值的Class
	 * 
	 */
	protected String convertToDisplayString(Object value) throws JspException {
		PropertyEditor editor = (value != null ? getBindStatus().findEditor(value.getClass()) : null);
		return getDisplayString(value, editor);
	}

	/**
	 * Process the given form field through a {@link RequestDataValueProcessor}
	 * instance if one is configured or otherwise returns the same value.
	 * <p>
	 *  通过{@link RequestDataValueProcessor}实例处理给定的表单域,如果配置了该表单域,或以其他方式返回相同的值
	 * 
	 */
	protected final String processFieldValue(String name, String value, String type) {
		RequestDataValueProcessor processor = getRequestContext().getRequestDataValueProcessor();
		ServletRequest request = this.pageContext.getRequest();
		if (processor != null && (request instanceof HttpServletRequest)) {
			value = processor.processFormFieldValue((HttpServletRequest) request, name, value, type);
		}
		return value;
	}

	/**
	 * Disposes of the {@link BindStatus} instance.
	 * <p>
	 *  配置{@link BindStatus}实例
	 */
	@Override
	public void doFinally() {
		super.doFinally();
		this.bindStatus = null;
	}

}
