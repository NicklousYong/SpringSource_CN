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

package org.springframework.web.servlet.view.freemarker;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import freemarker.core.ParseException;
import freemarker.ext.jsp.TaglibFactory;
import freemarker.ext.servlet.AllHttpScopesHashModel;
import freemarker.ext.servlet.FreemarkerServlet;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.ext.servlet.HttpRequestParametersHashModel;
import freemarker.ext.servlet.HttpSessionHashModel;
import freemarker.ext.servlet.ServletContextHashModel;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContextException;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.AbstractTemplateView;

/**
 * View using the FreeMarker template engine.
 *
 * <p>Exposes the following JavaBean properties:
 * <ul>
 * <li><b>url</b>: the location of the FreeMarker template to be wrapped,
 * relative to the FreeMarker template context (directory).
 * <li><b>encoding</b> (optional, default is determined by FreeMarker configuration):
 * the encoding of the FreeMarker template file
 * </ul>
 *
 * <p>Depends on a single {@link FreeMarkerConfig} object such as {@link FreeMarkerConfigurer}
 * being accessible in the current web application context, with any bean name.
 * Alternatively, you can set the FreeMarker {@link Configuration} object as bean property.
 * See {@link #setConfiguration} for more details on the impacts of this approach.
 *
 * <p>Note: Spring's FreeMarker support requires FreeMarker 2.3 or higher.
 *
 * <p>
 *  使用FreeMarker模板引擎查看
 * 
 *  <p>暴露以下JavaBean属性：
 * <ul>
 * <li> <b> url </b>：相对于FreeMarker模板上下文(目录),要包装的FreeMarker模板的位置<li> <b> encoding </b>(可选,默认值由FreeMarker确
 * 定配置)：FreeMarker模板文件的编码。
 * </ul>
 * 
 *  <p>取决于一个{@link FreeMarkerConfig}对象,例如{@link FreeMarkerConfigurer},可以在当前Web应用程序上下文中使用任何bean名称。
 * 或者,您可以将FreeMarker {@link Configuration}对象设置为bean属性查看{@link #setConfiguration}了解更多有关此方法的影响的详细信息。
 * 
 *  注意：Spring的FreeMarker支持需要FreeMarker 23或更高版本
 * 
 * 
 * @author Darren Davison
 * @author Juergen Hoeller
 * @since 03.03.2004
 * @see #setUrl
 * @see #setExposeSpringMacroHelpers
 * @see #setEncoding
 * @see #setConfiguration
 * @see FreeMarkerConfig
 * @see FreeMarkerConfigurer
 */
public class FreeMarkerView extends AbstractTemplateView {

	private String encoding;

	private Configuration configuration;

	private TaglibFactory taglibFactory;

	private ServletContextHashModel servletContextHashModel;


	/**
	 * Set the encoding of the FreeMarker template file. Default is determined
	 * by the FreeMarker Configuration: "ISO-8859-1" if not specified otherwise.
	 * <p>Specify the encoding in the FreeMarker Configuration rather than per
	 * template if all your templates share a common encoding.
	 * <p>
	 * 设置FreeMarker模板文件的编码默认值由FreeMarker配置确定："ISO-8859-1"(如果没有指定)<p>在FreeMarker配置中指定编码,而不是每个模板,如果所有模板共享一个公共编
	 * 码。
	 * 
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * Return the encoding for the FreeMarker template.
	 * <p>
	 *  返回FreeMarker模板的编码
	 * 
	 */
	protected String getEncoding() {
		return this.encoding;
	}

	/**
	 * Set the FreeMarker Configuration to be used by this view.
	 * <p>If this is not set, the default lookup will occur: a single {@link FreeMarkerConfig}
	 * is expected in the current web application context, with any bean name.
	 * <strong>Note:</strong> using this method will cause a new instance of {@link TaglibFactory}
	 * to created for every single {@link FreeMarkerView} instance. This can be quite expensive
	 * in terms of memory and initial CPU usage. In production it is recommended that you use
	 * a {@link FreeMarkerConfig} which exposes a single shared {@link TaglibFactory}.
	 * <p>
	 * 设置此视图使用的FreeMarker配置<p>如果未设置,默认查找将会发生：预期在当前Web应用程序上下文中单个{@link FreeMarkerConfig},任何bean名称<strong>注意： 
	 * </strong>使用此方法将为每个{@link FreeMarkerView}实例创建一个新的{@link TaglibFactory}实例。
	 * 在内存和初始CPU使用率方面可能相当昂贵在生产中,建议您使用一个{@link FreeMarkerConfig}公开了一个共享的{@link TaglibFactory}。
	 * 
	 */
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Return the FreeMarker configuration used by this view.
	 * <p>
	 *  返回此视图使用的FreeMarker配置
	 * 
	 */
	protected Configuration getConfiguration() {
		return this.configuration;
	}


	/**
	 * Invoked on startup. Looks for a single FreeMarkerConfig bean to
	 * find the relevant Configuration for this factory.
	 * <p>Checks that the template for the default Locale can be found:
	 * FreeMarker will check non-Locale-specific templates if a
	 * locale-specific one is not found.
	 * <p>
	 * 启动时调用查找单个FreeMarkerConfig bean以找到此工厂的相关配置<p>检查是否可以找到默认Locale的模板：如果未找到与特定于区域的特定模板,FreeMarker将检查非特定于区域设
	 * 置的模板。
	 * 
	 * 
	 * @see freemarker.cache.TemplateCache#getTemplate
	 */
	@Override
	protected void initServletContext(ServletContext servletContext) throws BeansException {
		if (getConfiguration() != null) {
			this.taglibFactory = new TaglibFactory(servletContext);
		}
		else {
			FreeMarkerConfig config = autodetectConfiguration();
			setConfiguration(config.getConfiguration());
			this.taglibFactory = config.getTaglibFactory();
		}

		GenericServlet servlet = new GenericServletAdapter();
		try {
			servlet.init(new DelegatingServletConfig());
		}
		catch (ServletException ex) {
			throw new BeanInitializationException("Initialization of GenericServlet adapter failed", ex);
		}
		this.servletContextHashModel = new ServletContextHashModel(servlet, getObjectWrapper());
	}

	/**
	 * Autodetect a {@link FreeMarkerConfig} object via the ApplicationContext.
	 * <p>
	 *  通过ApplicationContext自动检测{@link FreeMarkerConfig}对象
	 * 
	 * 
	 * @return the Configuration instance to use for FreeMarkerViews
	 * @throws BeansException if no Configuration instance could be found
	 * @see #getApplicationContext
	 * @see #setConfiguration
	 */
	protected FreeMarkerConfig autodetectConfiguration() throws BeansException {
		try {
			return BeanFactoryUtils.beanOfTypeIncludingAncestors(
					getApplicationContext(), FreeMarkerConfig.class, true, false);
		}
		catch (NoSuchBeanDefinitionException ex) {
			throw new ApplicationContextException(
					"Must define a single FreeMarkerConfig bean in this web application context " +
					"(may be inherited): FreeMarkerConfigurer is the usual implementation. " +
					"This bean may be given any name.", ex);
		}
	}

	/**
	 * Return the configured FreeMarker {@link ObjectWrapper}, or the
	 * {@link ObjectWrapper#DEFAULT_WRAPPER default wrapper} if none specified.
	 * <p>
	 *  如果没有指定,返回配置的FreeMarker {@link ObjectWrapper}或{@link ObjectWrapper#DEFAULT_WRAPPER默认包装器}
	 * 
	 * 
	 * @see freemarker.template.Configuration#getObjectWrapper()
	 */
	protected ObjectWrapper getObjectWrapper() {
		ObjectWrapper ow = getConfiguration().getObjectWrapper();
		return (ow != null ? ow :
				new DefaultObjectWrapperBuilder(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS).build());
	}

	/**
	 * Check that the FreeMarker template used for this view exists and is valid.
	 * <p>Can be overridden to customize the behavior, for example in case of
	 * multiple templates to be rendered into a single view.
	 * <p>
	 *  检查用于此视图的FreeMarker模板是否存在并且有效<p>可以覆盖自定义行为,例如,将多个模板呈现为单个视图
	 * 
	 */
	@Override
	public boolean checkResource(Locale locale) throws Exception {
		try {
			// Check that we can get the template, even if we might subsequently get it again.
			getTemplate(getUrl(), locale);
			return true;
		}
		catch (FileNotFoundException ex) {
			if (logger.isDebugEnabled()) {
				logger.debug("No FreeMarker view found for URL: " + getUrl());
			}
			return false;
		}
		catch (ParseException ex) {
			throw new ApplicationContextException(
					"Failed to parse FreeMarker template for URL [" +  getUrl() + "]", ex);
		}
		catch (IOException ex) {
			throw new ApplicationContextException(
					"Could not load FreeMarker template for URL [" + getUrl() + "]", ex);
		}
	}


	/**
	 * Process the model map by merging it with the FreeMarker template.
	 * Output is directed to the servlet response.
	 * <p>This method can be overridden if custom behavior is needed.
	 * <p>
	 * 通过将模型映射与FreeMarker模板合并来处理模型映射输出定向到servlet响应<p>如果需要自定义行为,此方法可以被覆盖
	 * 
	 */
	@Override
	protected void renderMergedTemplateModel(
			Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		exposeHelpers(model, request);
		doRender(model, request, response);
	}

	/**
	 * Expose helpers unique to each rendering operation. This is necessary so that
	 * different rendering operations can't overwrite each other's formats etc.
	 * <p>Called by {@code renderMergedTemplateModel}. The default implementation
	 * is empty. This method can be overridden to add custom helpers to the model.
	 * <p>
	 *  暴露每个渲染操作唯一的帮助器这是必要的,以便不同的渲染操作不能覆盖对方的格式等。
	 * 由{@code renderMergedTemplateModel}调用默认实现为空该方法可以被覆盖以将模板添加到模型中。
	 * 
	 * 
	 * @param model The model that will be passed to the template at merge time
	 * @param request current HTTP request
	 * @throws Exception if there's a fatal error while we're adding information to the context
	 * @see #renderMergedTemplateModel
	 */
	protected void exposeHelpers(Map<String, Object> model, HttpServletRequest request) throws Exception {
	}

	/**
	 * Render the FreeMarker view to the given response, using the given model
	 * map which contains the complete template model to use.
	 * <p>The default implementation renders the template specified by the "url"
	 * bean property, retrieved via {@code getTemplate}. It delegates to the
	 * {@code processTemplate} method to merge the template instance with
	 * the given template model.
	 * <p>Adds the standard Freemarker hash models to the model: request parameters,
	 * request, session and application (ServletContext), as well as the JSP tag
	 * library hash model.
	 * <p>Can be overridden to customize the behavior, for example to render
	 * multiple templates into a single view.
	 * <p>
	 * 将FreeMarker视图渲染到给定的响应中,使用包含完整模板模型的给定模型映射来使用<p>默认实现呈现由"url"bean属性指定的模板,通过{@code getTemplate}检索它代理到将模板实
	 * 例与给定的模板模型合并的{@code processTemplate}方法<p>将标准的Freemarker哈希模型添加到模型中：请求参数,请求,会话和应用程序(ServletContext)以及JSP
	 * 标记库哈希模型<p>可以覆盖自定义行为,例如将多个模板渲染到单个视图中。
	 * 
	 * 
	 * @param model the model to use for rendering
	 * @param request current HTTP request
	 * @param response current servlet response
	 * @throws IOException if the template file could not be retrieved
	 * @throws Exception if rendering failed
	 * @see #setUrl
	 * @see org.springframework.web.servlet.support.RequestContextUtils#getLocale
	 * @see #getTemplate(java.util.Locale)
	 * @see #processTemplate
	 * @see freemarker.ext.servlet.FreemarkerServlet
	 */
	protected void doRender(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		// Expose model to JSP tags (as request attributes).
		exposeModelAsRequestAttributes(model, request);
		// Expose all standard FreeMarker hash models.
		SimpleHash fmModel = buildTemplateModel(model, request, response);

		if (logger.isDebugEnabled()) {
			logger.debug("Rendering FreeMarker template [" + getUrl() + "] in FreeMarkerView '" + getBeanName() + "'");
		}
		// Grab the locale-specific version of the template.
		Locale locale = RequestContextUtils.getLocale(request);
		processTemplate(getTemplate(locale), fmModel, response);
	}

	/**
	 * Build a FreeMarker template model for the given model Map.
	 * <p>The default implementation builds a {@link AllHttpScopesHashModel}.
	 * <p>
	 *  为给定的模型构建FreeMarker模板模型Map <p>默认实现构建一个{@link AllHttpScopesHashModel}
	 * 
	 * 
	 * @param model the model to use for rendering
	 * @param request current HTTP request
	 * @param response current servlet response
	 * @return the FreeMarker template model, as a {@link SimpleHash} or subclass thereof
	 */
	protected SimpleHash buildTemplateModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
		AllHttpScopesHashModel fmModel = new AllHttpScopesHashModel(getObjectWrapper(), getServletContext(), request);
		fmModel.put(FreemarkerServlet.KEY_JSP_TAGLIBS, this.taglibFactory);
		fmModel.put(FreemarkerServlet.KEY_APPLICATION, this.servletContextHashModel);
		fmModel.put(FreemarkerServlet.KEY_SESSION, buildSessionModel(request, response));
		fmModel.put(FreemarkerServlet.KEY_REQUEST, new HttpRequestHashModel(request, response, getObjectWrapper()));
		fmModel.put(FreemarkerServlet.KEY_REQUEST_PARAMETERS, new HttpRequestParametersHashModel(request));
		fmModel.putAll(model);
		return fmModel;
	}

	/**
	 * Build a FreeMarker {@link HttpSessionHashModel} for the given request,
	 * detecting whether a session already exists and reacting accordingly.
	 * <p>
	 * 为给定的请求构建一个FreeMarker {@link HttpSessionHashModel},检测会话是否已经存在并相应地进行反应
	 * 
	 * 
	 * @param request current HTTP request
	 * @param response current servlet response
	 * @return the FreeMarker HttpSessionHashModel
	 */
	private HttpSessionHashModel buildSessionModel(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			return new HttpSessionHashModel(session, getObjectWrapper());
		}
		else {
			return new HttpSessionHashModel(null, request, response, getObjectWrapper());
		}
	}

	/**
	 * Retrieve the FreeMarker template for the given locale,
	 * to be rendering by this view.
	 * <p>By default, the template specified by the "url" bean property
	 * will be retrieved.
	 * <p>
	 *  检索给定语言环境的FreeMarker模板,以便通过此视图呈现<p>默认情况下,将检索由"url"bean属性指定的模板
	 * 
	 * 
	 * @param locale the current locale
	 * @return the FreeMarker template to render
	 * @throws IOException if the template file could not be retrieved
	 * @see #setUrl
	 * @see #getTemplate(String, java.util.Locale)
	 */
	protected Template getTemplate(Locale locale) throws IOException {
		return getTemplate(getUrl(), locale);
	}

	/**
	 * Retrieve the FreeMarker template specified by the given name,
	 * using the encoding specified by the "encoding" bean property.
	 * <p>Can be called by subclasses to retrieve a specific template,
	 * for example to render multiple templates into a single view.
	 * <p>
	 *  检索由给定名称指定的FreeMarker模板,使用"encoding"bean属性指定的编码<p>可以由子类调用以检索特定的模板,例如将多个模板呈现到单个视图
	 * 
	 * 
	 * @param name the file name of the desired template
	 * @param locale the current locale
	 * @return the FreeMarker template
	 * @throws IOException if the template file could not be retrieved
	 */
	protected Template getTemplate(String name, Locale locale) throws IOException {
		return (getEncoding() != null ?
				getConfiguration().getTemplate(name, locale, getEncoding()) :
				getConfiguration().getTemplate(name, locale));
	}

	/**
	 * Process the FreeMarker template to the servlet response.
	 * <p>Can be overridden to customize the behavior.
	 * <p>
	 *  将FreeMarker模板处理为servlet响应<p>可以覆盖自定义行为
	 * 
	 * 
	 * @param template the template to process
	 * @param model the model for the template
	 * @param response servlet response (use this to get the OutputStream or Writer)
	 * @throws IOException if the template file could not be retrieved
	 * @throws TemplateException if thrown by FreeMarker
	 * @see freemarker.template.Template#process(Object, java.io.Writer)
	 */
	protected void processTemplate(Template template, SimpleHash model, HttpServletResponse response)
			throws IOException, TemplateException {

		template.process(model, response.getWriter());
	}


	/**
	 * Simple adapter class that extends {@link GenericServlet}.
	 * Needed for JSP access in FreeMarker.
	 * <p>
	 *  扩展{@link GenericServlet}的简单适配器类,可在FreeMarker中进行JSP访问
	 * 
	 */
	@SuppressWarnings("serial")
	private static class GenericServletAdapter extends GenericServlet {

		@Override
		public void service(ServletRequest servletRequest, ServletResponse servletResponse) {
			// no-op
		}
	}


	/**
	 * Internal implementation of the {@link ServletConfig} interface,
	 * to be passed to the servlet adapter.
	 * <p>
	 * 内部实现{@link ServletConfig}接口,传递给servlet适配器
	 */
	private class DelegatingServletConfig implements ServletConfig {

		@Override
		public String getServletName() {
			return FreeMarkerView.this.getBeanName();
		}

		@Override
		public ServletContext getServletContext() {
			return FreeMarkerView.this.getServletContext();
		}

		@Override
		public String getInitParameter(String paramName) {
			return null;
		}

		@Override
		public Enumeration<String> getInitParameterNames() {
			return Collections.enumeration(Collections.<String>emptySet());
		}
	}

}
