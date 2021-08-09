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

package org.springframework.web.servlet.tags.form;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;

import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Base class for databinding-aware JSP tags that render HTML element. Provides
 * a set of properties corresponding to the set of HTML attributes that are common
 * across elements.
 *
 * <p>Additionally, this base class allows for rendering non-standard attributes
 * as part of the tag's output.  These attributes are accessible to subclasses if
 * needed via the {@link AbstractHtmlElementTag#getDynamicAttributes() dynamicAttributes}
 * map.
 *
 * <p>
 *  支持数据绑定感知的JSP标签的基类,用于呈现HTML元素提供一组属性,这些属性对应于元素中常见的一组HTML属性
 * 
 * 此外,此基类允许将非标准属性渲染为标记输出的一部分如果需要,可通过{@link AbstractHtmlElementTag#getDynamicAttributes()dynamicAttributes}
 * 映射来访问子类的这些属性。
 * 
 * 
 * @author Rob Harrop
 * @author Jeremy Grelle
 * @author Rossen Stoyanchev
 * @since 2.0
 */
@SuppressWarnings("serial")
public abstract class AbstractHtmlElementTag extends AbstractDataBoundFormElementTag implements DynamicAttributes {

	public static final String CLASS_ATTRIBUTE = "class";

	public static final String STYLE_ATTRIBUTE = "style";

	public static final String LANG_ATTRIBUTE = "lang";

	public static final String TITLE_ATTRIBUTE = "title";

	public static final String DIR_ATTRIBUTE = "dir";

	public static final String TABINDEX_ATTRIBUTE = "tabindex";

	public static final String ONCLICK_ATTRIBUTE = "onclick";

	public static final String ONDBLCLICK_ATTRIBUTE = "ondblclick";

	public static final String ONMOUSEDOWN_ATTRIBUTE = "onmousedown";

	public static final String ONMOUSEUP_ATTRIBUTE = "onmouseup";

	public static final String ONMOUSEOVER_ATTRIBUTE = "onmouseover";

	public static final String ONMOUSEMOVE_ATTRIBUTE = "onmousemove";

	public static final String ONMOUSEOUT_ATTRIBUTE = "onmouseout";

	public static final String ONKEYPRESS_ATTRIBUTE = "onkeypress";

	public static final String ONKEYUP_ATTRIBUTE = "onkeyup";

	public static final String ONKEYDOWN_ATTRIBUTE = "onkeydown";


	private String cssClass;

	private String cssErrorClass;

	private String cssStyle;

	private String lang;

	private String title;

	private String dir;

	private String tabindex;

	private String onclick;

	private String ondblclick;

	private String onmousedown;

	private String onmouseup;

	private String onmouseover;

	private String onmousemove;

	private String onmouseout;

	private String onkeypress;

	private String onkeyup;

	private String onkeydown;

	private Map<String, Object> dynamicAttributes;


	/**
	 * Set the value of the '{@code class}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  设置"{@code class}"属性的值可以是运行时表达式
	 * 
	 */
	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	/**
	 * Get the value of the '{@code class}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  获取"{@code class}"属性的值可以是运行时表达式
	 * 
	 */
	protected String getCssClass() {
		return this.cssClass;
	}

	/**
	 * The CSS class to use when the field bound to a particular tag has errors.
	 * May be a runtime expression.
	 * <p>
	 *  当绑定到特定标签的字段有错误时使用的CSS类可能是运行时表达式
	 * 
	 */
	public void setCssErrorClass(String cssErrorClass) {
		this.cssErrorClass = cssErrorClass;
	}

	/**
	 * The CSS class to use when the field bound to a particular tag has errors.
	 * May be a runtime expression.
	 * <p>
	 *  当绑定到特定标签的字段有错误时使用的CSS类可能是运行时表达式
	 * 
	 */
	protected String getCssErrorClass() {
		return this.cssErrorClass;
	}

	/**
	 * Set the value of the '{@code style}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  设置"{@code style}"属性的值可以是运行时表达式
	 * 
	 */
	public void setCssStyle(String cssStyle) {
		this.cssStyle = cssStyle;
	}

	/**
	 * Get the value of the '{@code style}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  获取"{@code style}"属性的值可以是运行时表达式
	 * 
	 */
	protected String getCssStyle() {
		return this.cssStyle;
	}

	/**
	 * Set the value of the '{@code lang}' attribute.
	 * May be a runtime expression.
	 * <p>
	 * 设置"{@code lang}"属性的值可以是运行时表达式
	 * 
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}

	/**
	 * Get the value of the '{@code lang}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  获取"{@code lang}"属性的值可以是运行时表达式
	 * 
	 */
	protected String getLang() {
		return this.lang;
	}

	/**
	 * Set the value of the '{@code title}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  设置"{@code title}"属性的值可以是运行时表达式
	 * 
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Get the value of the '{@code title}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  获取"{@code title}"属性的值可以是运行时表达式
	 * 
	 */
	protected String getTitle() {
		return this.title;
	}

	/**
	 * Set the value of the '{@code dir}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  设置"{@code dir}"属性的值可以是运行时表达式
	 * 
	 */
	public void setDir(String dir) {
		this.dir = dir;
	}

	/**
	 * Get the value of the '{@code dir}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  获取"{@code dir}"属性的值可以是运行时表达式
	 * 
	 */
	protected String getDir() {
		return this.dir;
	}

	/**
	 * Set the value of the '{@code tabindex}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  设置"{@code tabindex}"属性的值可以是运行时表达式
	 * 
	 */
	public void setTabindex(String tabindex) {
		this.tabindex = tabindex;
	}

	/**
	 * Get the value of the '{@code tabindex}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  获取"{@code tabindex}"属性的值可以是运行时表达式
	 * 
	 */
	protected String getTabindex() {
		return this.tabindex;
	}

	/**
	 * Set the value of the '{@code onclick}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  设置"{@code onclick}"属性的值可以是运行时表达式
	 * 
	 */
	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}

	/**
	 * Get the value of the '{@code onclick}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  获取"{@code onclick}"属性的值可以是运行时表达式
	 * 
	 */
	protected String getOnclick() {
		return this.onclick;
	}

	/**
	 * Set the value of the '{@code ondblclick}' attribute.
	 * May be a runtime expression.
	 * <p>
	 * 设置"{@code ondblclick}"属性的值可以是运行时表达式
	 * 
	 */
	public void setOndblclick(String ondblclick) {
		this.ondblclick = ondblclick;
	}

	/**
	 * Get the value of the '{@code ondblclick}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  获取"{@code ondblclick}"属性的值可以是运行时表达式
	 * 
	 */
	protected String getOndblclick() {
		return this.ondblclick;
	}

	/**
	 * Set the value of the '{@code onmousedown}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  设置"{@code onmousedown}"属性的值可以是运行时表达式
	 * 
	 */
	public void setOnmousedown(String onmousedown) {
		this.onmousedown = onmousedown;
	}

	/**
	 * Get the value of the '{@code onmousedown}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  获取"{@code onmousedown}"属性的值可以是运行时表达式
	 * 
	 */
	protected String getOnmousedown() {
		return this.onmousedown;
	}

	/**
	 * Set the value of the '{@code onmouseup}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  设置"{@code onmouseup}"属性的值可以是运行时表达式
	 * 
	 */
	public void setOnmouseup(String onmouseup) {
		this.onmouseup = onmouseup;
	}

	/**
	 * Get the value of the '{@code onmouseup}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  获取"{@code onmouseup}"属性的值可以是运行时表达式
	 * 
	 */
	protected String getOnmouseup() {
		return this.onmouseup;
	}

	/**
	 * Set the value of the '{@code onmouseover}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  设置"{@code onmouseover}"属性的值可以是运行时表达式
	 * 
	 */
	public void setOnmouseover(String onmouseover) {
		this.onmouseover = onmouseover;
	}

	/**
	 * Get the value of the '{@code onmouseover}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  获取"{@code onmouseover}"属性的值可以是运行时表达式
	 * 
	 */
	protected String getOnmouseover() {
		return this.onmouseover;
	}

	/**
	 * Set the value of the '{@code onmousemove}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  设置"{@code onmousemove}"属性的值可以是运行时表达式
	 * 
	 */
	public void setOnmousemove(String onmousemove) {
		this.onmousemove = onmousemove;
	}

	/**
	 * Get the value of the '{@code onmousemove}' attribute.
	 * May be a runtime expression.
	 * <p>
	 * 获取"{@code onmousemove}"属性的值可以是运行时表达式
	 * 
	 */
	protected String getOnmousemove() {
		return this.onmousemove;
	}

	/**
	 * Set the value of the '{@code onmouseout}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  设置"{@code onmouseout}"属性的值可以是运行时表达式
	 * 
	 */
	public void setOnmouseout(String onmouseout) {
		this.onmouseout = onmouseout;
	}
	/**
	 * Get the value of the '{@code onmouseout}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  获取"{@code onmouseout}"属性的值可以是运行时表达式
	 * 
	 */
	protected String getOnmouseout() {
		return this.onmouseout;
	}

	/**
	 * Set the value of the '{@code onkeypress}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  设置"{@code onkeypress}"属性的值可以是运行时表达式
	 * 
	 */
	public void setOnkeypress(String onkeypress) {
		this.onkeypress = onkeypress;
	}

	/**
	 * Get the value of the '{@code onkeypress}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  获取"{@code onkeypress}"属性的值可以是运行时表达式
	 * 
	 */
	protected String getOnkeypress() {
		return this.onkeypress;
	}

	/**
	 * Set the value of the '{@code onkeyup}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  设置"{@code onkeyup}"属性的值可以是运行时表达式
	 * 
	 */
	public void setOnkeyup(String onkeyup) {
		this.onkeyup = onkeyup;
	}

	/**
	 * Get the value of the '{@code onkeyup}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  获取"{@code onkeyup}"属性的值可以是运行时表达式
	 * 
	 */
	protected String getOnkeyup() {
		return this.onkeyup;
	}

	/**
	 * Set the value of the '{@code onkeydown}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  设置"{@code onkeydown}"属性的值可以是运行时表达式
	 * 
	 */
	public void setOnkeydown(String onkeydown) {
		this.onkeydown = onkeydown;
	}

	/**
	 * Get the value of the '{@code onkeydown}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  获取"{@code onkeydown}"属性的值可以是运行时表达式
	 * 
	 */
	protected String getOnkeydown() {
		return this.onkeydown;
	}

	/**
	 * Get the map of dynamic attributes.
	 * <p>
	 *  获取动态属性的映射
	 * 
	 */
	protected Map<String, Object> getDynamicAttributes() {
		return this.dynamicAttributes;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 *  {} @inheritDoc
	 * 
	 */
	@Override
	public void setDynamicAttribute(String uri, String localName, Object value ) throws JspException {
		if (this.dynamicAttributes == null) {
			this.dynamicAttributes = new HashMap<String, Object>();
		}
		if (!isValidDynamicAttribute(localName, value)) {
			throw new IllegalArgumentException(
					"Attribute " + localName + "=\"" + value + "\" is not allowed");
		}
		dynamicAttributes.put(localName, value);
	}

	/**
	 * Whether the given name-value pair is a valid dynamic attribute.
	 * <p>
	 * 给定的名称 - 值对是否是有效的动态属性
	 * 
	 */
	protected boolean isValidDynamicAttribute(String localName, Object value) {
		return true;
	}

	/**
	 * Writes the default attributes configured via this base class to the supplied {@link TagWriter}.
	 * Subclasses should call this when they want the base attribute set to be written to the output.
	 * <p>
	 *  将通过此基类配置的默认属性写入所提供的{@link TagWriter}子类应在将基本属性集写入输出时调用
	 * 
	 */
	@Override
	protected void writeDefaultAttributes(TagWriter tagWriter) throws JspException {
		super.writeDefaultAttributes(tagWriter);
		writeOptionalAttributes(tagWriter);
	}

	/**
	 * Writes the optional attributes configured via this base class to the supplied {@link TagWriter}.
	 * The set of optional attributes that will be rendered includes any non-standard dynamic attributes.
	 * Called by {@link #writeDefaultAttributes(TagWriter)}.
	 * <p>
	 *  将通过此基类配置的可选属性写入提供的{@link TagWriter}将被渲染的可选属性集包括{@link #writeDefaultAttributes(TagWriter)}调用的任何非标准动态属
	 * 性。
	 * 
	 */
	protected void writeOptionalAttributes(TagWriter tagWriter) throws JspException {
		tagWriter.writeOptionalAttributeValue(CLASS_ATTRIBUTE, resolveCssClass());
		tagWriter.writeOptionalAttributeValue(STYLE_ATTRIBUTE,
				ObjectUtils.getDisplayString(evaluate("cssStyle", getCssStyle())));
		writeOptionalAttribute(tagWriter, LANG_ATTRIBUTE, getLang());
		writeOptionalAttribute(tagWriter, TITLE_ATTRIBUTE, getTitle());
		writeOptionalAttribute(tagWriter, DIR_ATTRIBUTE, getDir());
		writeOptionalAttribute(tagWriter, TABINDEX_ATTRIBUTE, getTabindex());
		writeOptionalAttribute(tagWriter, ONCLICK_ATTRIBUTE, getOnclick());
		writeOptionalAttribute(tagWriter, ONDBLCLICK_ATTRIBUTE, getOndblclick());
		writeOptionalAttribute(tagWriter, ONMOUSEDOWN_ATTRIBUTE, getOnmousedown());
		writeOptionalAttribute(tagWriter, ONMOUSEUP_ATTRIBUTE, getOnmouseup());
		writeOptionalAttribute(tagWriter, ONMOUSEOVER_ATTRIBUTE, getOnmouseover());
		writeOptionalAttribute(tagWriter, ONMOUSEMOVE_ATTRIBUTE, getOnmousemove());
		writeOptionalAttribute(tagWriter, ONMOUSEOUT_ATTRIBUTE, getOnmouseout());
		writeOptionalAttribute(tagWriter, ONKEYPRESS_ATTRIBUTE, getOnkeypress());
		writeOptionalAttribute(tagWriter, ONKEYUP_ATTRIBUTE, getOnkeyup());
		writeOptionalAttribute(tagWriter, ONKEYDOWN_ATTRIBUTE, getOnkeydown());

		if (!CollectionUtils.isEmpty(this.dynamicAttributes)) {
			for (String attr : this.dynamicAttributes.keySet()) {
				tagWriter.writeOptionalAttributeValue(attr, getDisplayString(this.dynamicAttributes.get(attr)));
			}
		}
	}

	/**
	 * Gets the appropriate CSS class to use based on the state of the current
	 * {@link org.springframework.web.servlet.support.BindStatus} object.
	 * <p>
	 */
	protected String resolveCssClass() throws JspException {
		if (getBindStatus().isError() && StringUtils.hasText(getCssErrorClass())) {
			return ObjectUtils.getDisplayString(evaluate("cssErrorClass", getCssErrorClass()));
		}
		else {
			return ObjectUtils.getDisplayString(evaluate("cssClass", getCssClass()));
		}
	}

}
