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

package org.springframework.web.servlet.tags;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.RequestDataValueProcessor;
import org.springframework.web.util.JavaScriptUtils;
import org.springframework.web.util.TagUtils;
import org.springframework.web.util.UriUtils;

/**
 * JSP tag for creating URLs. Modeled after the JSTL c:url tag with backwards
 * compatibility in mind.
 *
 * <p>Enhancements to the JSTL functionality include:
 * <ul>
 * <li>URL encoded template URI variables</li>
 * <li>HTML/XML escaping of URLs</li>
 * <li>JavaScript escaping of URLs</li>
 * </ul>
 *
 * <p>Template URI variables are indicated in the {@link #setValue(String) 'value'}
 * attribute and marked by braces '{variableName}'. The braces and attribute name are
 * replaced by the URL encoded value of a parameter defined with the spring:param tag
 * in the body of the url tag. If no parameter is available the literal value is
 * passed through. Params matched to template variables will not be added to the query
 * string.
 *
 * <p>Use of the spring:param tag for URI template variables is strongly recommended
 * over direct EL substitution as the values are URL encoded.  Failure to properly
 * encode URL can leave an application vulnerable to XSS and other injection attacks.
 *
 * <p>URLs can be HTML/XML escaped by setting the {@link #setHtmlEscape(boolean)
 * 'htmlEscape'} attribute to 'true'.  Detects an HTML escaping setting, either on
 * this tag instance, the page level, or the {@code web.xml} level. The default
 * is 'false'.  When setting the URL value into a variable, escaping is not recommended.
 *
 * <p>Example usage:
 * <pre class="code">&lt;spring:url value="/url/path/{variableName}"&gt;
 *   &lt;spring:param name="variableName" value="more than JSTL c:url" /&gt;
 * &lt;/spring:url&gt;</pre>
 * Results in:
 * {@code /currentApplicationContext/url/path/more%20than%20JSTL%20c%3Aurl}
 *
 * <p>
 *  用于创建URL的JSP标记在JSTL c：url标记之后建模,具有向后兼容性
 * 
 *  <p> JSTL功能的增强功能包括：
 * <ul>
 * <li> URL编码模板URI变量</li> <li> HTML / XML转义网址</li> <li> JavaScript转义网址</li>
 * </ul>
 * 
 *  <p>模板URI变量在{@link #setValue(String)'value'}属性中指示,并用大括号'{variableName}'标记。
 * 大括号和属性名称将被定义的参数的URL编码值替换spring：在url标签正文中的param标签如果没有参数可用,文字值通过匹配的模板变量传递给模板变量不会被添加到查询字符串。
 * 
 *  <p>对于URI模板变量,使用spring：param标签强烈建议使用直接EL替换,因为值为URL编码无法正确编码URL可能会使应用程序容易受到XSS和其他注入攻击
 * 
 * 通过将{@link #setHtmlEscape(boolean)'htmlEscape'}属性设置为"true",可以将HTML / XML转义为HTML / XML。
 * 检测HTML转义设置,无论是在此标记实例,页面级别还是{@代码webxml}级别默认值为"false"将URL值设置为变量时,不推荐转义。
 * 
 *  <p>使用示例：<pre class ="code">&lt; spring：url value ="/ url / path / {variableName}"&gt; &lt; spring：pa
 * ram name ="variableName"value ="more than JSTL c：url"/&gt; &lt; / spring：url&gt; </pre>结果：{@code / currentApplicationContext / url / path / more％20th％％20JSTL％20c％3Aurl}
 * 。
 * 
 * 
 * @author Scott Andrews
 * @since 3.0
 * @see ParamTag
 */
@SuppressWarnings("serial")
public class UrlTag extends HtmlEscapingAwareTag implements ParamAware {

	private static final String URL_TEMPLATE_DELIMITER_PREFIX = "{";

	private static final String URL_TEMPLATE_DELIMITER_SUFFIX = "}";

	private static final String URL_TYPE_ABSOLUTE = "://";


	private List<Param> params;

	private Set<String> templateParams;

	private UrlType type;

	private String value;

	private String context;

	private String var;

	private int scope = PageContext.PAGE_SCOPE;

	private boolean javaScriptEscape = false;


	/**
	 * Sets the value of the URL
	 * <p>
	 *  设置URL的值
	 * 
	 */
	public void setValue(String value) {
		if (value.contains(URL_TYPE_ABSOLUTE)) {
			this.type = UrlType.ABSOLUTE;
			this.value = value;
		}
		else if (value.startsWith("/")) {
			this.type = UrlType.CONTEXT_RELATIVE;
			this.value = value;
		}
		else {
			this.type = UrlType.RELATIVE;
			this.value = value;
		}
	}

	/**
	 * Set the context path for the URL. Defaults to the current context
	 * <p>
	 *  将URL Defaults的上下文路径设置为当前上下文
	 * 
	 */
	public void setContext(String context) {
		if (context.startsWith("/")) {
			this.context = context;
		}
		else {
			this.context = "/" + context;
		}
	}

	/**
	 * Set the variable name to expose the URL under. Defaults to rendering the
	 * URL to the current JspWriter
	 * <p>
	 *  设置变量名称以将URL显示在"默认值"下,将URL呈现给当前的JspWriter
	 * 
	 */
	public void setVar(String var) {
		this.var = var;
	}

	/**
	 * Set the scope to export the URL variable to. This attribute has no
	 * meaning unless var is also defined.
	 * <p>
	 * 将范围设置为将URL变量导出到此属性没有意义,除非也定义了var
	 * 
	 */
	public void setScope(String scope) {
		this.scope = TagUtils.getScope(scope);
	}

	/**
	 * Set JavaScript escaping for this tag, as boolean value.
	 * Default is "false".
	 * <p>
	 *  设置JavaScript转义为此标记,布尔值默认为"false"
	 * 
	 */
	public void setJavaScriptEscape(boolean javaScriptEscape) throws JspException {
		this.javaScriptEscape = javaScriptEscape;
	}

	@Override
	public void addParam(Param param) {
		this.params.add(param);
	}


	@Override
	public int doStartTagInternal() throws JspException {
		this.params = new LinkedList<Param>();
		this.templateParams = new HashSet<String>();
		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag() throws JspException {
		String url = createUrl();

		RequestDataValueProcessor processor = getRequestContext().getRequestDataValueProcessor();
		ServletRequest request = this.pageContext.getRequest();
		if ((processor != null) && (request instanceof HttpServletRequest)) {
			url = processor.processUrl((HttpServletRequest) request, url);
		}

		if (this.var == null) {
			// print the url to the writer
			try {
				pageContext.getOut().print(url);
			}
			catch (IOException ex) {
				throw new JspException(ex);
			}
		}
		else {
			// store the url as a variable
			pageContext.setAttribute(var, url, scope);
		}
		return EVAL_PAGE;
	}


	/**
	 * Build the URL for the tag from the tag attributes and parameters.
	 * <p>
	 *  从标签属性和参数构建标签的URL
	 * 
	 * 
	 * @return the URL value as a String
	 * @throws JspException
	 */
	private String createUrl() throws JspException {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
		StringBuilder url = new StringBuilder();
		if (this.type == UrlType.CONTEXT_RELATIVE) {
			// add application context to url
			if (this.context == null) {
				url.append(request.getContextPath());
			}
			else {
				if (this.context.endsWith("/")) {
					url.append(this.context.substring(0, this.context.length() - 1));
				}
				else {
					url.append(this.context);
				}
			}
		}
		if (this.type != UrlType.RELATIVE && this.type != UrlType.ABSOLUTE && !this.value.startsWith("/")) {
			url.append("/");
		}
		url.append(replaceUriTemplateParams(this.value, this.params, this.templateParams));
		url.append(createQueryString(this.params, this.templateParams, (url.indexOf("?") == -1)));

		String urlStr = url.toString();
		if (this.type != UrlType.ABSOLUTE) {
			// Add the session identifier if needed
			// (Do not embed the session identifier in a remote link!)
			urlStr = response.encodeURL(urlStr);
		}

		// HTML and/or JavaScript escape, if demanded.
		urlStr = htmlEscape(urlStr);
		urlStr = this.javaScriptEscape ? JavaScriptUtils.javaScriptEscape(urlStr) : urlStr;

		return urlStr;
	}

	/**
	 * Build the query string from available parameters that have not already
	 * been applied as template params.
	 * <p>The names and values of parameters are URL encoded.
	 * <p>
	 *  从尚未应用为模板参数的可用参数构建查询字符串<p>参数的名称和值由URL编码
	 * 
	 * 
	 * @param params the parameters to build the query string from
	 * @param usedParams set of parameter names that have been applied as
	 * template params
	 * @param includeQueryStringDelimiter true if the query string should start
	 * with a '?' instead of '&'
	 * @return the query string
	 */
	protected String createQueryString(List<Param> params, Set<String> usedParams, boolean includeQueryStringDelimiter)
			throws JspException {

		String encoding = pageContext.getResponse().getCharacterEncoding();
		StringBuilder qs = new StringBuilder();
		for (Param param : params) {
			if (!usedParams.contains(param.getName()) && StringUtils.hasLength(param.getName())) {
				if (includeQueryStringDelimiter && qs.length() == 0) {
					qs.append("?");
				}
				else {
					qs.append("&");
				}
				try {
					qs.append(UriUtils.encodeQueryParam(param.getName(), encoding));
					if (param.getValue() != null) {
						qs.append("=");
						qs.append(UriUtils.encodeQueryParam(param.getValue(), encoding));
					}
				}
				catch (UnsupportedEncodingException ex) {
					throw new JspException(ex);
				}
			}
		}
		return qs.toString();
	}

	/**
	 * Replace template markers in the URL matching available parameters. The
	 * name of matched parameters are added to the used parameters set.
	 * <p>Parameter values are URL encoded.
	 * <p>
	 *  在匹配可用参数的URL中替换模板标记匹配参数的名称将添加到已使用的参数集<p>参数值为URL编码
	 * 
	 * 
	 * @param uri the URL with template parameters to replace
	 * @param params parameters used to replace template markers
	 * @param usedParams set of template parameter names that have been replaced
	 * @return the URL with template parameters replaced
	 */
	protected String replaceUriTemplateParams(String uri, List<Param> params, Set<String> usedParams)
			throws JspException {

		String encoding = pageContext.getResponse().getCharacterEncoding();
		for (Param param : params) {
			String template = URL_TEMPLATE_DELIMITER_PREFIX + param.getName() + URL_TEMPLATE_DELIMITER_SUFFIX;
			if (uri.contains(template)) {
				usedParams.add(param.getName());
				try {
					uri = uri.replace(template, UriUtils.encodePath(param.getValue(), encoding));
				}
				catch (UnsupportedEncodingException ex) {
					throw new JspException(ex);
				}
			}
			else {
				template = URL_TEMPLATE_DELIMITER_PREFIX + "/" + param.getName() + URL_TEMPLATE_DELIMITER_SUFFIX;
				if (uri.contains(template)) {
					usedParams.add(param.getName());
					try {
						uri = uri.replace(template, UriUtils.encodePathSegment(param.getValue(), encoding));
					}
					catch (UnsupportedEncodingException ex) {
						throw new JspException(ex);
					}
				}
			}
		}
		return uri;
	}


	/**
	 * Internal enum that classifies URLs by type.
	 * <p>
	 *  内部枚举,按类型对URL​​进行分类
	 */
	private enum UrlType {

		CONTEXT_RELATIVE, RELATIVE, ABSOLUTE
	}

}
