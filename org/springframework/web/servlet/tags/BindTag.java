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

package org.springframework.web.servlet.tags;

import java.beans.PropertyEditor;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

import org.springframework.validation.Errors;
import org.springframework.web.servlet.support.BindStatus;

/**
 * Bind tag, supporting evaluation of binding errors for a certain
 * bean or bean property. Exposes a "status" variable of type
 * {@link org.springframework.web.servlet.support.BindStatus},
 * to both Java expressions and JSP EL expressions.
 *
 * <p>Can be used to bind to any bean or bean property in the model.
 * The specified path determines whether the tag exposes the status of the
 * bean itself (showing object-level errors), a specific bean property
 * (showing field errors), or a matching set of bean properties
 * (showing all corresponding field errors).
 *
 * <p>The {@link org.springframework.validation.Errors} object that has
 * been bound using this tag is exposed to collaborating tags, as well
 * as the bean property that this errors object applies to. Nested tags
 * such as the {@link TransformTag} can access those exposed properties.
 *
 * <p>
 * 绑定标签,支持对某个bean或bean属性的绑定错误的评估将类型为{@link orgspringframeworkwebservletsupportBindStatus}的"status"变量暴露给J
 * ava表达式和JSP EL表达式。
 * 
 *  <p>可用于绑定到模型中的任何bean或bean属性指定的路径确定标签是否暴露了bean本身的状态(显示对象级错误),特定的bean属性(显示字段错误),或一组匹配的bean属性(显示所有相应的字段错
 * 误)。
 * 
 * <p>使用此标记绑定的{@link orgspringframeworkvalidationErrors}对象暴露给协作标记,以及此错误对象适用于嵌套标记(如{@link TransformTag})的
 * bean属性可以访问这些暴露的属性。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see #setPath
 */
@SuppressWarnings("serial")
public class BindTag extends HtmlEscapingAwareTag implements EditorAwareTag {

	/**
	 * Name of the exposed variable within the scope of this tag: "status".
	 * <p>
	 *  此变量范围内的暴露变量的名称："status"
	 * 
	 */
	public static final String STATUS_VARIABLE_NAME = "status";


	private String path;

	private boolean ignoreNestedPath = false;

	private BindStatus status;

	private Object previousPageStatus;

	private Object previousRequestStatus;


	/**
	 * Set the path that this tag should apply. Can be a bean (e.g. "person")
	 * to get global errors, or a bean property (e.g. "person.name") to get
	 * field errors (also supporting nested fields and "person.na*" mappings).
	 * "person.*" will return all errors for the specified bean, both global
	 * and field errors.
	 * <p>
	 *  设置此标签应用的路径可以是一个bean(例如"person")来获取全局错误,或者一个bean属性(例如"personname")来获取字段错误(也支持嵌套字段和"personna *"映射)" pe
	 * rson *"将返回指定的bean的所有错误,包括全局和字段错误。
	 * 
	 * 
	 * @see org.springframework.validation.Errors#getGlobalErrors
	 * @see org.springframework.validation.Errors#getFieldErrors
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Return the path that this tag applies to.
	 * <p>
	 *  返回此标签应用于的路径
	 * 
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * Set whether to ignore a nested path, if any.
	 * Default is to not ignore.
	 * <p>
	 *  设置是否忽略嵌套路径,如果任何默认值不被忽略
	 * 
	 */
	public void setIgnoreNestedPath(boolean ignoreNestedPath) {
	  this.ignoreNestedPath = ignoreNestedPath;
	}

	/**
	 * Return whether to ignore a nested path, if any.
	 * <p>
	 * 返回是否忽略嵌套路径,如果有的话
	 * 
	 */
	public boolean isIgnoreNestedPath() {
	  return this.ignoreNestedPath;
	}


	@Override
	protected final int doStartTagInternal() throws Exception {
		String resolvedPath = getPath();
		if (!isIgnoreNestedPath()) {
			String nestedPath = (String) pageContext.getAttribute(
					NestedPathTag.NESTED_PATH_VARIABLE_NAME, PageContext.REQUEST_SCOPE);
			// only prepend if not already an absolute path
			if (nestedPath != null && !resolvedPath.startsWith(nestedPath) &&
					!resolvedPath.equals(nestedPath.substring(0, nestedPath.length() - 1))) {
				resolvedPath = nestedPath + resolvedPath;
			}
		}

		try {
			this.status = new BindStatus(getRequestContext(), resolvedPath, isHtmlEscape());
		}
		catch (IllegalStateException ex) {
			throw new JspTagException(ex.getMessage());
		}

		// Save previous status values, for re-exposure at the end of this tag.
		this.previousPageStatus = pageContext.getAttribute(STATUS_VARIABLE_NAME, PageContext.PAGE_SCOPE);
		this.previousRequestStatus = pageContext.getAttribute(STATUS_VARIABLE_NAME, PageContext.REQUEST_SCOPE);

		// Expose this tag's status object as PageContext attribute,
		// making it available for JSP EL.
		pageContext.removeAttribute(STATUS_VARIABLE_NAME, PageContext.PAGE_SCOPE);
		pageContext.setAttribute(STATUS_VARIABLE_NAME, this.status, PageContext.REQUEST_SCOPE);

		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag() {
		// Reset previous status values.
		if (this.previousPageStatus != null) {
			pageContext.setAttribute(STATUS_VARIABLE_NAME, this.previousPageStatus, PageContext.PAGE_SCOPE);
		}
		if (this.previousRequestStatus != null) {
			pageContext.setAttribute(STATUS_VARIABLE_NAME, this.previousRequestStatus, PageContext.REQUEST_SCOPE);
		}
		else {
			pageContext.removeAttribute(STATUS_VARIABLE_NAME, PageContext.REQUEST_SCOPE);
		}
		return EVAL_PAGE;
	}


	/**
	 * Retrieve the property that this tag is currently bound to,
	 * or {@code null} if bound to an object rather than a specific property.
	 * Intended for cooperating nesting tags.
	 * <p>
	 *  检索此标记当前被绑定的属性,或者{@code null}如果绑定到一个对象而不是一个特定的属性意图用于协作嵌套标记
	 * 
	 * 
	 * @return the property that this tag is currently bound to,
	 * or {@code null} if none
	 */
	public final String getProperty() {
		return this.status.getExpression();
	}

	/**
	 * Retrieve the Errors instance that this tag is currently bound to.
	 * Intended for cooperating nesting tags.
	 * <p>
	 *  检索Errors实例,该标签当前被绑定到旨在协作的嵌套标签
	 * 
	 * @return the current Errors instance, or {@code null} if none
	 */
	public final Errors getErrors() {
		return this.status.getErrors();
	}

	@Override
	public final PropertyEditor getEditor() {
		return this.status.getEditor();
	}


	@Override
	public void doFinally() {
		super.doFinally();
		this.status = null;
		this.previousPageStatus = null;
		this.previousRequestStatus = null;
	}

}
