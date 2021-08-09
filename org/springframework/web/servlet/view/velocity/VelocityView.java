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

package org.springframework.web.servlet.view.velocity;

import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.NumberTool;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContextException;
import org.springframework.core.NestedIOException;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.AbstractTemplateView;
import org.springframework.web.util.NestedServletException;

/**
 * View using the Velocity template engine.
 *
 * <p>Exposes the following JavaBean properties:
 * <ul>
 * <li><b>url</b>: the location of the Velocity template to be wrapped,
 * relative to the Velocity resource loader path (see VelocityConfigurer).
 * <li><b>encoding</b> (optional, default is determined by Velocity configuration):
 * the encoding of the Velocity template file
 * <li><b>velocityFormatterAttribute</b> (optional, default=null): the name of
 * the VelocityFormatter helper object to expose in the Velocity context of this
 * view, or {@code null} if not needed. VelocityFormatter is part of standard Velocity.
 * <li><b>dateToolAttribute</b> (optional, default=null): the name of the
 * DateTool helper object to expose in the Velocity context of this view,
 * or {@code null} if not needed. DateTool is part of Velocity Tools.
 * <li><b>numberToolAttribute</b> (optional, default=null): the name of the
 * NumberTool helper object to expose in the Velocity context of this view,
 * or {@code null} if not needed. NumberTool is part of Velocity Tools.
 * <li><b>cacheTemplate</b> (optional, default=false): whether or not the Velocity
 * template should be cached. It should normally be true in production, but setting
 * this to false enables us to modify Velocity templates without restarting the
 * application (similar to JSPs). Note that this is a minor optimization only,
 * as Velocity itself caches templates in a modification-aware fashion.
 * </ul>
 *
 * <p>Depends on a VelocityConfig object such as VelocityConfigurer being
 * accessible in the current web application context, with any bean name.
 * Alternatively, you can set the VelocityEngine object as bean property.
 *
 * <p>Note: Spring 3.0's VelocityView requires Velocity 1.4 or higher, and optionally
 * Velocity Tools 1.1 or higher (depending on the use of DateTool and/or NumberTool).
 *
 * <p>
 *  使用Velocity模板引擎查看
 * 
 *  <p>暴露以下JavaBean属性：
 * <ul>
 * 相对于Velocity资源加载程序路径(请参阅VelocityConfigurer)<li> <b> url </b>：要包装的Velocity模板的位置<li> <b> encoding </b>(可
 * 选,默认为确定通过Velocity配置)：Velocity模板文件的编码<li> <b> velocityFormatterAttribute </b>(可选,默认= null)：要在此视图的Veloc
 * ity上下文中显示的VelocityFormatter助手对象的名称,或{ @code null}如果不需要VelocityFormatter是标准Velocity <li> <b> dateToolA
 * ttribute </b>(可选,default = null)的一部分：在此视图的Velocity上下文中公开的DateTool帮助对象的名称,或{@code null}如果不需要,DateTool是
 * Velocity Tools的一部分<li> <b> numberToolAttribute </b>(可选,default = null)：在此视图的Velocity上下文中显示的NumberTool
 * 帮助对象的名称,如果不需要,则为{@code null} NumberTool是Velocity的一部分工具<li> <b> cacheTemplate </b>(可选,默认= false)：Veloc
 * ity模板是否应该缓存通常在生产中应该是正确的,但是将其设置为false可以修改Velocity模板而不重新启动应用程序(类似于JSP)请注意,这只是一个次要的优化,因为Velocity本身以修改感知方
 * 式缓存模板。
 * </ul>
 * 
 * <p>取决于VelocityConfig对象,如VelocityConfigurer,可在当前Web应用程序上下文中使用任何bean名称。
 * 或者,您可以将VelocityEngine对象设置为bean属性。
 * 
 *  注意：Spring 30的VelocityView需要Velocity 14或更高版本,并且可选择Velocity Tools 11或更高版本(取决于使用DateTool和/或NumberTool)。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Dave Syer
 * @see VelocityConfig
 * @see VelocityConfigurer
 * @see #setUrl
 * @see #setExposeSpringMacroHelpers
 * @see #setEncoding
 * @see #setVelocityEngine
 * @see VelocityConfig
 * @see VelocityConfigurer
 * @deprecated as of Spring 4.3, in favor of FreeMarker
 */
@Deprecated
public class VelocityView extends AbstractTemplateView {

	private Map<String, Class<?>> toolAttributes;

	private String dateToolAttribute;

	private String numberToolAttribute;

	private String encoding;

	private boolean cacheTemplate = false;

	private VelocityEngine velocityEngine;

	private Template template;


	/**
	 * Set tool attributes to expose to the view, as attribute name / class name pairs.
	 * An instance of the given class will be added to the Velocity context for each
	 * rendering operation, under the given attribute name.
	 * <p>For example, an instance of MathTool, which is part of the generic package
	 * of Velocity Tools, can be bound under the attribute name "math", specifying the
	 * fully qualified class name "org.apache.velocity.tools.generic.MathTool" as value.
	 * <p>Note that VelocityView can only create simple generic tools or values, that is,
	 * classes with a public default constructor and no further initialization needs.
	 * This class does not do any further checks, to not introduce a required dependency
	 * on a specific tools package.
	 * <p>For tools that are part of the view package of Velocity Tools, a special
	 * Velocity context and a special init callback are needed. Use VelocityToolboxView
	 * in such a case, or override {@code createVelocityContext} and
	 * {@code initTool} accordingly.
	 * <p>For a simple VelocityFormatter instance or special locale-aware instances
	 * of DateTool/NumberTool, which are part of the generic package of Velocity Tools,
	 * specify the "velocityFormatterAttribute", "dateToolAttribute" or
	 * "numberToolAttribute" properties, respectively.
	 * <p>
	 * 设置工具属性以暴露给视图,作为属性名称/类名称对在给定的属性名称<p>下,给定类的实例将被添加到每个渲染操作的Velocity上下文中。
	 * 例如,MathTool的一个实例,它是Velocity Tools的通用软件包的一部分,可以绑定在属性名称"math"下,将完整的类名称指定为"orgapachevelocitytoolsgeneric
	 * MathTool"作为值<p>请注意,VelocityView只能创建简单的通用工具或值,即,具有公共默认构造函数的类和没有进一步的初始化需要此类不进行任何进一步的检查,以便不对特定工具包引入所需的依赖
	 * 关系对于作为Velocity Tools视图包的一部分的工具,需要特殊的Velocity上下文和特殊的init回调在这种情况下使用VelocityToolboxView,或相应地覆盖{@code createVelocityContext}
	 * 和{@code initTool} <p >对于属于Velocity Tools通用包的一部分的简单VelocityFormatter实例或特殊的区域感知实例DateTool / NumberTool,
	 * 分别指定"velocityFormatterAttribute","dateToolAttribute"或"numberToolAttribute"属性。
	 * 设置工具属性以暴露给视图,作为属性名称/类名称对在给定的属性名称<p>下,给定类的实例将被添加到每个渲染操作的Velocity上下文中。
	 * 
	 * 
	 * @param toolAttributes attribute names as keys, and tool class names as values
	 * @see org.apache.velocity.tools.generic.MathTool
	 * @see VelocityToolboxView
	 * @see #createVelocityContext
	 * @see #initTool
	 * @see #setDateToolAttribute
	 * @see #setNumberToolAttribute
	 */
	public void setToolAttributes(Map<String, Class<?>> toolAttributes) {
		this.toolAttributes = toolAttributes;
	}

	/**
	 * Set the name of the DateTool helper object to expose in the Velocity context
	 * of this view, or {@code null} if not needed. The exposed DateTool will be aware of
	 * the current locale, as determined by Spring's LocaleResolver.
	 * <p>DateTool is part of the generic package of Velocity Tools 1.0.
	 * Spring uses a special locale-aware subclass of DateTool.
	 * <p>
	 * 设置要在此视图的Velocity上下文中显示的DateTool帮助对象的名称,如果不需要,则返回{@code null}。
	 * 暴露的DateTool将会注意到当前的区域设置,由Spring的LocaleResolver <p> DateTool确定为Velocity Tools 10 Spring的通用包使用DateTool的
	 * 特殊的区域感知子类。
	 * 设置要在此视图的Velocity上下文中显示的DateTool帮助对象的名称,如果不需要,则返回{@code null}。
	 * 
	 * 
	 * @see org.apache.velocity.tools.generic.DateTool
	 * @see org.springframework.web.servlet.support.RequestContextUtils#getLocale
	 * @see org.springframework.web.servlet.LocaleResolver
	 */
	public void setDateToolAttribute(String dateToolAttribute) {
		this.dateToolAttribute = dateToolAttribute;
	}

	/**
	 * Set the name of the NumberTool helper object to expose in the Velocity context
	 * of this view, or {@code null} if not needed. The exposed NumberTool will be aware of
	 * the current locale, as determined by Spring's LocaleResolver.
	 * <p>NumberTool is part of the generic package of Velocity Tools 1.1.
	 * Spring uses a special locale-aware subclass of NumberTool.
	 * <p>
	 *  设置要在此视图的Velocity上下文中显示的NumberTool帮助对象的名称,如果不需要,则设置为{@code null}。
	 * 暴露的NumberTool将注意到当前的区域设置,由Spring的LocaleResolver <p> NumberTool确定为Velocity Tools 11 Spring的通用包使用了一个特殊的
	 * 区域设置感知的NumberTool子类。
	 *  设置要在此视图的Velocity上下文中显示的NumberTool帮助对象的名称,如果不需要,则设置为{@code null}。
	 * 
	 * 
	 * @see org.apache.velocity.tools.generic.NumberTool
	 * @see org.springframework.web.servlet.support.RequestContextUtils#getLocale
	 * @see org.springframework.web.servlet.LocaleResolver
	 */
	public void setNumberToolAttribute(String numberToolAttribute) {
		this.numberToolAttribute = numberToolAttribute;
	}

	/**
	 * Set the encoding of the Velocity template file. Default is determined
	 * by the VelocityEngine: "ISO-8859-1" if not specified otherwise.
	 * <p>Specify the encoding in the VelocityEngine rather than per template
	 * if all your templates share a common encoding.
	 * <p>
	 * 设置Velocity模板文件的编码默认值由VelocityEngine确定："ISO-8859-1"(如果未指定)<p>如果所有模板共享一个公共编码,则在VelocityEngine中指定编码,而不是每
	 * 个模板。
	 * 
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * Return the encoding for the Velocity template.
	 * <p>
	 *  返回Velocity模板的编码
	 * 
	 */
	protected String getEncoding() {
		return this.encoding;
	}

	/**
	 * Set whether the Velocity template should be cached. Default is "false".
	 * It should normally be true in production, but setting this to false enables us to
	 * modify Velocity templates without restarting the application (similar to JSPs).
	 * <p>Note that this is a minor optimization only, as Velocity itself caches
	 * templates in a modification-aware fashion.
	 * <p>
	 *  设置Velocity模板是否应该缓存默认为"false"通常在生产中应该是正确的,但将其设置为false可以修改Velocity模板,而不重新启动应用程序(类似于JSP)<p>请注意,这是一个未成年人
	 * 优化,Velocity本身以修改感知的方式缓存模板。
	 * 
	 */
	public void setCacheTemplate(boolean cacheTemplate) {
		this.cacheTemplate = cacheTemplate;
	}

	/**
	 * Return whether the Velocity template should be cached.
	 * <p>
	 *  返回Velocity模板是否应被缓存
	 * 
	 */
	protected boolean isCacheTemplate() {
		return this.cacheTemplate;
	}

	/**
	 * Set the VelocityEngine to be used by this view.
	 * <p>If this is not set, the default lookup will occur: A single VelocityConfig
	 * is expected in the current web application context, with any bean name.
	 * <p>
	 * 设置要由此视图使用的VelocityEngine <p>如果未设置,则会发生默认查找：预期在当前Web应用程序上下文中单个VelocityConfig,任何bean名称
	 * 
	 * 
	 * @see VelocityConfig
	 */
	public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}

	/**
	 * Return the VelocityEngine used by this view.
	 * <p>
	 *  返回此视图使用的VelocityEngine
	 * 
	 */
	protected VelocityEngine getVelocityEngine() {
		return this.velocityEngine;
	}


	/**
 	 * Invoked on startup. Looks for a single VelocityConfig bean to
 	 * find the relevant VelocityEngine for this factory.
 	 * <p>
 	 *  在启动时调用查找单个VelocityConfig bean以找到此工厂的相关VelocityEngine
 	 * 
 	 */
	@Override
	protected void initApplicationContext() throws BeansException {
		super.initApplicationContext();

		if (getVelocityEngine() == null) {
			// No explicit VelocityEngine: try to autodetect one.
			setVelocityEngine(autodetectVelocityEngine());
		}
	}

	/**
	 * Autodetect a VelocityEngine via the ApplicationContext.
	 * Called if no explicit VelocityEngine has been specified.
	 * <p>
	 *  通过ApplicationContext自动检测VelocityEngine如果未指定显式VelocityEngine,则调用
	 * 
	 * 
	 * @return the VelocityEngine to use for VelocityViews
	 * @throws BeansException if no VelocityEngine could be found
	 * @see #getApplicationContext
	 * @see #setVelocityEngine
	 */
	protected VelocityEngine autodetectVelocityEngine() throws BeansException {
		try {
			VelocityConfig velocityConfig = BeanFactoryUtils.beanOfTypeIncludingAncestors(
					getApplicationContext(), VelocityConfig.class, true, false);
			return velocityConfig.getVelocityEngine();
		}
		catch (NoSuchBeanDefinitionException ex) {
			throw new ApplicationContextException(
					"Must define a single VelocityConfig bean in this web application context " +
					"(may be inherited): VelocityConfigurer is the usual implementation. " +
					"This bean may be given any name.", ex);
		}
	}

	/**
	 * Check that the Velocity template used for this view exists and is valid.
	 * <p>Can be overridden to customize the behavior, for example in case of
	 * multiple templates to be rendered into a single view.
	 * <p>
	 *  检查用于此视图的Velocity模板是否存在并且有效<p>可以覆盖以自定义行为,例如,将多个模板呈现为单个视图
	 * 
	 */
	@Override
	public boolean checkResource(Locale locale) throws Exception {
		try {
			// Check that we can get the template, even if we might subsequently get it again.
			this.template = getTemplate(getUrl());
			return true;
		}
		catch (ResourceNotFoundException ex) {
			if (logger.isDebugEnabled()) {
				logger.debug("No Velocity view found for URL: " + getUrl());
			}
			return false;
		}
		catch (Exception ex) {
			throw new NestedIOException(
					"Could not load Velocity template for URL [" + getUrl() + "]", ex);
		}
	}


	/**
	 * Process the model map by merging it with the Velocity template.
	 * Output is directed to the servlet response.
	 * <p>This method can be overridden if custom behavior is needed.
	 * <p>
	 * 通过将模型映射与Velocity模板合并来处理模型映射Output指向servlet响应<p>如果需要自定义行为,此方法可以被覆盖
	 * 
	 */
	@Override
	protected void renderMergedTemplateModel(
			Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		exposeHelpers(model, request);

		Context velocityContext = createVelocityContext(model, request, response);
		exposeHelpers(velocityContext, request, response);
		exposeToolAttributes(velocityContext, request);

		doRender(velocityContext, response);
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
	 * @param model the model that will be passed to the template for merging
	 * @param request current HTTP request
	 * @throws Exception if there's a fatal error while we're adding model attributes
	 * @see #renderMergedTemplateModel
	 */
	protected void exposeHelpers(Map<String, Object> model, HttpServletRequest request) throws Exception {
	}

	/**
	 * Create a Velocity Context instance for the given model,
	 * to be passed to the template for merging.
	 * <p>The default implementation delegates to {@link #createVelocityContext(Map)}.
	 * Can be overridden for a special context class, for example ChainedContext which
	 * is part of the view package of Velocity Tools. ChainedContext is needed for
	 * initialization of ViewTool instances.
	 * <p>Have a look at {@link VelocityToolboxView}, which pre-implements
	 * ChainedContext support. This is not part of the standard VelocityView class
	 * in order to avoid a required dependency on the view package of Velocity Tools.
	 * <p>
	 * 为给定的模型创建一个Velocity Context实例,将其传递给模板进行合并<p>默认实现委托给{@link #createVelocityContext(Map)}可以覆盖一个特殊的上下文类,例如
	 * ChainedContext,它是一部分Velocity Tools ChainedContext的查看包需要初始化ViewTool实例<p>查看{@link VelocityToolboxView},
	 * 它预先实现ChainedContext支持这不是标准VelocityView类的一部分,以避免所需的依赖关系在Velocity Tools的视图包中。
	 * 
	 * 
	 * @param model the model Map, containing the model attributes to be exposed to the view
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @return the Velocity Context
	 * @throws Exception if there's a fatal error while creating the context
	 * @see #createVelocityContext(Map)
	 * @see #initTool
	 * @see org.apache.velocity.tools.view.context.ChainedContext
	 * @see VelocityToolboxView
	 */
	protected Context createVelocityContext(
			Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		return createVelocityContext(model);
	}

	/**
	 * Create a Velocity Context instance for the given model,
	 * to be passed to the template for merging.
	 * <p>Default implementation creates an instance of Velocity's
	 * VelocityContext implementation class.
	 * <p>
	 *  为给定的模型创建一个Velocity Context实例,传递给模板进行合并<p>默认实现创建一个Velocity的VelocityContext实现类的实例
	 * 
	 * 
	 * @param model the model Map, containing the model attributes
	 * to be exposed to the view
	 * @return the Velocity Context
	 * @throws Exception if there's a fatal error while creating the context
	 * @see org.apache.velocity.VelocityContext
	 */
	protected Context createVelocityContext(Map<String, Object> model) throws Exception {
		return new VelocityContext(model);
	}

	/**
	 * Expose helpers unique to each rendering operation. This is necessary so that
	 * different rendering operations can't overwrite each other's formats etc.
	 * <p>Called by {@code renderMergedTemplateModel}. Default implementation
	 * delegates to {@code exposeHelpers(velocityContext, request)}. This method
	 * can be overridden to add special tools to the context, needing the servlet response
	 * to initialize (see Velocity Tools, for example LinkTool and ViewTool/ChainedContext).
	 * <p>
	 * 显示每个渲染操作唯一的帮助器这是必要的,以便不同的渲染操作不能覆盖对方的格式等。
	 * 由{@code renderMergedTemplateModel}调用的默认实现委托{@code exposeHelpers(velocityContext,request)}这个方法可以被覆盖以为上
	 * 下文添加特殊工具,需要servlet响应来初始化(参见Velocity Tools,例如LinkTool和ViewTool / ChainedContext)。
	 * 显示每个渲染操作唯一的帮助器这是必要的,以便不同的渲染操作不能覆盖对方的格式等。
	 * 
	 * 
	 * @param velocityContext Velocity context that will be passed to the template
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @throws Exception if there's a fatal error while we're adding model attributes
	 * @see #exposeHelpers(org.apache.velocity.context.Context, HttpServletRequest)
	 */
	protected void exposeHelpers(
			Context velocityContext, HttpServletRequest request, HttpServletResponse response) throws Exception {

		exposeHelpers(velocityContext, request);
	}

	/**
	 * Expose helpers unique to each rendering operation. This is necessary so that
	 * different rendering operations can't overwrite each other's formats etc.
	 * <p>Default implementation is empty. This method can be overridden to add
	 * custom helpers to the Velocity context.
	 * <p>
	 *  暴露每个渲染操作唯一的帮助器这是必要的,以便不同的渲染操作不能覆盖彼此的格式等。<p>默认实现为空该方法可以被覆盖以将自定义帮助器添加到Velocity上下文
	 * 
	 * 
	 * @param velocityContext Velocity context that will be passed to the template
	 * @param request current HTTP request
	 * @throws Exception if there's a fatal error while we're adding model attributes
	 * @see #exposeHelpers(Map, HttpServletRequest)
	 */
	protected void exposeHelpers(Context velocityContext, HttpServletRequest request) throws Exception {
	}

	/**
	 * Expose the tool attributes, according to corresponding bean property settings.
	 * <p>Do not override this method unless for further tools driven by bean properties.
	 * Override one of the {@code exposeHelpers} methods to add custom helpers.
	 * <p>
	 * 根据相应的bean属性设置公开工具属性<p>不要重写此方法,除非由bean属性驱动的进一步工具覆盖其中一个{@code exposeHelpers}方法以添加自定义助手
	 * 
	 * 
	 * @param velocityContext Velocity context that will be passed to the template
	 * @param request current HTTP request
	 * @throws Exception if there's a fatal error while we're adding model attributes
	 * @see #setDateToolAttribute
	 * @see #setNumberToolAttribute
	 * @see #exposeHelpers(Map, HttpServletRequest)
	 * @see #exposeHelpers(org.apache.velocity.context.Context, HttpServletRequest, HttpServletResponse)
	 */
	protected void exposeToolAttributes(Context velocityContext, HttpServletRequest request) throws Exception {
		// Expose generic attributes.
		if (this.toolAttributes != null) {
			for (Map.Entry<String, Class<?>> entry : this.toolAttributes.entrySet()) {
				String attributeName = entry.getKey();
				Class<?> toolClass = entry.getValue();
				try {
					Object tool = toolClass.newInstance();
					initTool(tool, velocityContext);
					velocityContext.put(attributeName, tool);
				}
				catch (Exception ex) {
					throw new NestedServletException("Could not instantiate Velocity tool '" + attributeName + "'", ex);
				}
			}
		}

		// Expose locale-aware DateTool/NumberTool attributes.
		if (this.dateToolAttribute != null || this.numberToolAttribute != null) {
			if (this.dateToolAttribute != null) {
				velocityContext.put(this.dateToolAttribute, new LocaleAwareDateTool(request));
			}
			if (this.numberToolAttribute != null) {
				velocityContext.put(this.numberToolAttribute, new LocaleAwareNumberTool(request));
			}
		}
	}

	/**
	 * Initialize the given tool instance. The default implementation is empty.
	 * <p>Can be overridden to check for special callback interfaces, for example
	 * the ViewContext interface which is part of the view package of Velocity Tools.
	 * In the particular case of ViewContext, you'll usually also need a special
	 * Velocity context, like ChainedContext which is part of Velocity Tools too.
	 * <p>Have a look at {@link VelocityToolboxView}, which pre-implements such a
	 * ViewTool check. This is not part of the standard VelocityView class in order
	 * to avoid a required dependency on the view package of Velocity Tools.
	 * <p>
	 * 初始化给定的工具实例默认实现为空<p>可以覆盖以检查特殊的回调接口,例如ViewContext接口,它是Velocity Tools视图包的一部分在特定的ViewContext的情况下,您通常也会需要一
	 * 个特殊的Velocity上下文,像ChainedContext这是Velocity Tools的一部分<p>看看{@link VelocityToolboxView},它预先实现了这样一个ViewToo
	 * l检查这不是标准VelocityView类的一部分,以避免需要依赖于Velocity Tools的视图包。
	 * 
	 * 
	 * @param tool the tool instance to initialize
	 * @param velocityContext the Velocity context
	 * @throws Exception if initializion of the tool failed
	 * @see #createVelocityContext
	 * @see org.apache.velocity.tools.view.context.ViewContext
	 * @see org.apache.velocity.tools.view.context.ChainedContext
	 * @see VelocityToolboxView
	 */
	protected void initTool(Object tool, Context velocityContext) throws Exception {
	}


	/**
	 * Render the Velocity view to the given response, using the given Velocity
	 * context which contains the complete template model to use.
	 * <p>The default implementation renders the template specified by the "url"
	 * bean property, retrieved via {@code getTemplate}. It delegates to the
	 * {@code mergeTemplate} method to merge the template instance with the
	 * given Velocity context.
	 * <p>Can be overridden to customize the behavior, for example to render
	 * multiple templates into a single view.
	 * <p>
	 * 将Velocity视图渲染到给定的响应,使用给定的Velocity上下文,其中包含完整的模板模型以使用<p>默认实现呈现由"url"bean属性指定的模板,通过{@code getTemplate}检索
	 * 它委托给将模板实例与给定的Velocity上下文<p>合并的{@code mergeTemplate}方法可以被覆盖以自定义行为,例如将多个模板呈现为单个视图。
	 * 
	 * 
	 * @param context the Velocity context to use for rendering
	 * @param response servlet response (use this to get the OutputStream or Writer)
	 * @throws Exception if thrown by Velocity
	 * @see #setUrl
	 * @see #getTemplate()
	 * @see #mergeTemplate
	 */
	protected void doRender(Context context, HttpServletResponse response) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Rendering Velocity template [" + getUrl() + "] in VelocityView '" + getBeanName() + "'");
		}
		mergeTemplate(getTemplate(), context, response);
	}

	/**
	 * Retrieve the Velocity template to be rendered by this view.
	 * <p>By default, the template specified by the "url" bean property will be
	 * retrieved: either returning a cached template instance or loading a fresh
	 * instance (according to the "cacheTemplate" bean property)
	 * <p>
	 *  检索要由此视图渲染的Velocity模板<p>默认情况下,将检索由"url"bean属性指定的模板：返回缓存的模板实例或加载新实例(根据"cacheTemplate"bean属性)
	 * 
	 * 
	 * @return the Velocity template to render
	 * @throws Exception if thrown by Velocity
	 * @see #setUrl
	 * @see #setCacheTemplate
	 * @see #getTemplate(String)
	 */
	protected Template getTemplate() throws Exception {
		// We already hold a reference to the template, but we might want to load it
		// if not caching. Velocity itself caches templates, so our ability to
		// cache templates in this class is a minor optimization only.
		if (isCacheTemplate() && this.template != null) {
			return this.template;
		}
		else {
			return getTemplate(getUrl());
		}
	}

	/**
	 * Retrieve the Velocity template specified by the given name,
	 * using the encoding specified by the "encoding" bean property.
	 * <p>Can be called by subclasses to retrieve a specific template,
	 * for example to render multiple templates into a single view.
	 * <p>
	 * 检索由给定名称指定的Velocity模板,使用由"encoding"bean属性指定的编码属性<p>可以通过子类调用以检索特定模板,例如将多个模板呈现到单个视图
	 * 
	 * 
	 * @param name the file name of the desired template
	 * @return the Velocity template
	 * @throws Exception if thrown by Velocity
	 * @see org.apache.velocity.app.VelocityEngine#getTemplate
	 */
	protected Template getTemplate(String name) throws Exception {
		return (getEncoding() != null ?
				getVelocityEngine().getTemplate(name, getEncoding()) :
				getVelocityEngine().getTemplate(name));
	}

	/**
	 * Merge the template with the context.
	 * Can be overridden to customize the behavior.
	 * <p>
	 *  使用上下文合并模板可以覆盖以自定义行为
	 * 
	 * 
	 * @param template the template to merge
	 * @param context the Velocity context to use for rendering
	 * @param response servlet response (use this to get the OutputStream or Writer)
	 * @throws Exception if thrown by Velocity
	 * @see org.apache.velocity.Template#merge
	 */
	protected void mergeTemplate(
			Template template, Context context, HttpServletResponse response) throws Exception {

		try {
			template.merge(context, response.getWriter());
		}
		catch (MethodInvocationException ex) {
			Throwable cause = ex.getWrappedThrowable();
			throw new NestedServletException(
					"Method invocation failed during rendering of Velocity view with name '" +
					getBeanName() + "': " + ex.getMessage() + "; reference [" + ex.getReferenceName() +
					"], method '" + ex.getMethodName() + "'",
					cause==null ? ex : cause);
		}
	}


	/**
	 * Subclass of DateTool from Velocity Tools, using a Spring-resolved
	 * Locale and TimeZone instead of the default Locale.
	 * <p>
	 *  来自Velocity Tools的DateTool的子类,使用Spring解析的区域设置和TimeZone,而不是默认的区域设置
	 * 
	 * 
	 * @see org.springframework.web.servlet.support.RequestContextUtils#getLocale
	 * @see org.springframework.web.servlet.support.RequestContextUtils#getTimeZone
	 */
	private static class LocaleAwareDateTool extends DateTool {

		private final HttpServletRequest request;

		public LocaleAwareDateTool(HttpServletRequest request) {
			this.request = request;
		}

		@Override
		public Locale getLocale() {
			return RequestContextUtils.getLocale(this.request);
		}

		@Override
		public TimeZone getTimeZone() {
			TimeZone timeZone = RequestContextUtils.getTimeZone(this.request);
			return (timeZone != null ? timeZone : super.getTimeZone());
		}
	}


	/**
	 * Subclass of NumberTool from Velocity Tools, using a Spring-resolved
	 * Locale instead of the default Locale.
	 * <p>
	 *  来自Velocity Tools的NumberTool子类,使用Spring解析的区域设置而不是默认的区域设置
	 * 
	 * @see org.springframework.web.servlet.support.RequestContextUtils#getLocale
	 */
	private static class LocaleAwareNumberTool extends NumberTool {

		private final HttpServletRequest request;

		public LocaleAwareNumberTool(HttpServletRequest request) {
			this.request = request;
		}

		@Override
		public Locale getLocale() {
			return RequestContextUtils.getLocale(this.request);
		}
	}

}
