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

package org.springframework.web.servlet.config.annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
import org.springframework.web.servlet.view.groovy.GroovyMarkupConfigurer;
import org.springframework.web.servlet.view.groovy.GroovyMarkupViewResolver;
import org.springframework.web.servlet.view.script.ScriptTemplateConfigurer;
import org.springframework.web.servlet.view.script.ScriptTemplateViewResolver;
import org.springframework.web.servlet.view.tiles3.TilesConfigurer;
import org.springframework.web.servlet.view.tiles3.TilesViewResolver;

/**
 * Assist with the configuration of a chain of
 * {@link org.springframework.web.servlet.ViewResolver ViewResolver} instances.
 * This class is expected to be used via {@link WebMvcConfigurer#configureViewResolvers}.
 *
 * <p>
 *  协助配置一系列{@link orgspringframeworkwebservletViewResolver ViewResolver}实例此类预计将通过{@link WebMvcConfigurer#configureViewResolvers}
 * 使用。
 * 
 * 
 * @author Sebastien Deleuze
 * @author Rossen Stoyanchev
 * @since 4.1
 */
public class ViewResolverRegistry {

	private ContentNegotiatingViewResolver contentNegotiatingResolver;

	private final List<ViewResolver> viewResolvers = new ArrayList<ViewResolver>(4);

	private Integer order;

	private ContentNegotiationManager contentNegotiationManager;

	private ApplicationContext applicationContext;


	protected void setContentNegotiationManager(ContentNegotiationManager contentNegotiationManager) {
		this.contentNegotiationManager = contentNegotiationManager;
	}

	protected void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * Whether any view resolvers have been registered.
	 * <p>
	 * 是否已经注册了任何视图解析器
	 * 
	 */
	public boolean hasRegistrations() {
		return (this.contentNegotiatingResolver != null || !this.viewResolvers.isEmpty());
	}


	/**
	 * Enable use of a {@link ContentNegotiatingViewResolver} to front all other
	 * configured view resolvers and select among all selected Views based on
	 * media types requested by the client (e.g. in the Accept header).
	 * <p>If invoked multiple times the provided default views will be added to
	 * any other default views that may have been configured already.
	 * <p>
	 *  启用使用{@link ContentNegotiatingViewResolver}前面所有其他配置的视图解析器,并根据客户端请求的媒体类型(例如在Accept标头中)选择所有选择的视图<p>如果多次
	 * 调用提供的默认视图将添加到可能已经配置的任何其他默认视图。
	 * 
	 * 
	 * @see ContentNegotiatingViewResolver#setDefaultViews
	 */
	public void enableContentNegotiation(View... defaultViews) {
		initContentNegotiatingViewResolver(defaultViews);
	}

	/**
	 * Enable use of a {@link ContentNegotiatingViewResolver} to front all other
	 * configured view resolvers and select among all selected Views based on
	 * media types requested by the client (e.g. in the Accept header).
	 * <p>If invoked multiple times the provided default views will be added to
	 * any other default views that may have been configured already.
	 *
	 * <p>
	 *  启用使用{@link ContentNegotiatingViewResolver}前面所有其他配置的视图解析器,并根据客户端请求的媒体类型(例如在Accept标头中)选择所有选择的视图<p>如果多次
	 * 调用提供的默认视图将添加到可能已经配置的任何其他默认视图。
	 * 
	 * 
	 * @see ContentNegotiatingViewResolver#setDefaultViews
	 */
	public void enableContentNegotiation(boolean useNotAcceptableStatus, View... defaultViews) {
		initContentNegotiatingViewResolver(defaultViews);
		this.contentNegotiatingResolver.setUseNotAcceptableStatusCode(useNotAcceptableStatus);
	}

	private void initContentNegotiatingViewResolver(View[] defaultViews) {
		// ContentNegotiatingResolver in the registry: elevate its precedence!
		this.order = (this.order != null ? this.order : Ordered.HIGHEST_PRECEDENCE);

		if (this.contentNegotiatingResolver != null) {
			if (!ObjectUtils.isEmpty(defaultViews)) {
				if (!CollectionUtils.isEmpty(this.contentNegotiatingResolver.getDefaultViews())) {
					List<View> views = new ArrayList<View>(this.contentNegotiatingResolver.getDefaultViews());
					views.addAll(Arrays.asList(defaultViews));
					this.contentNegotiatingResolver.setDefaultViews(views);
				}
			}
		}
		else {
			this.contentNegotiatingResolver = new ContentNegotiatingViewResolver();
			this.contentNegotiatingResolver.setDefaultViews(Arrays.asList(defaultViews));
			this.contentNegotiatingResolver.setViewResolvers(this.viewResolvers);
			this.contentNegotiatingResolver.setContentNegotiationManager(this.contentNegotiationManager);
		}
	}

	/**
	 * Register JSP view resolver using a default view name prefix of "/WEB-INF/"
	 * and a default suffix of ".jsp".
	 * <p>When this method is invoked more than once, each call will register a
	 * new ViewResolver instance. Note that since it's not easy to determine
	 * if a JSP exists without forwarding to it, using multiple JSP-based view
	 * resolvers only makes sense in combination with the "viewNames" property
	 * on the resolver indicating which view names are handled by which resolver.
	 * <p>
	 * 注册JSP视图解析器使用默认视图名称前缀"/ WEB-INF /"和默认后缀"jsp"<p>当这种方法被多次调用时,每个调用将注册一个新的ViewResolver实例注意,由于它是不容易确定JSP是否存
	 * 在而不转发它,使用多个基于JSP的视图解析器只能与解析器上的"viewNames"属性相结合,指出哪个视图名称由哪个解析器处理。
	 * 
	 */
	public UrlBasedViewResolverRegistration jsp() {
		return jsp("/WEB-INF/", ".jsp");
	}

	/**
	 * Register JSP view resolver with the specified prefix and suffix.
	 * <p>When this method is invoked more than once, each call will register a
	 * new ViewResolver instance. Note that since it's not easy to determine
	 * if a JSP exists without forwarding to it, using multiple JSP-based view
	 * resolvers only makes sense in combination with the "viewNames" property
	 * on the resolver indicating which view names are handled by which resolver.
	 * <p>
	 * 注册具有指定前缀和后缀的JSP视图解析器<p>当此方法被多次调用时,每个调用将注册一个新的ViewResolver实例注意,由于确定JSP是否存在而不转发它不容易,因此使用多个JSP基于视图解析器的视图
	 * 解析器与解析器上的"viewNames"属性结合使用,表明哪个视图名称由哪个解析器处理。
	 * 
	 */
	public UrlBasedViewResolverRegistration jsp(String prefix, String suffix) {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix(prefix);
		resolver.setSuffix(suffix);
		this.viewResolvers.add(resolver);
		return new UrlBasedViewResolverRegistration(resolver);
	}

	/**
	 * Register Tiles 3.x view resolver.
	 * <p><strong>Note</strong> that you must also configure Tiles by adding a
	 * {@link org.springframework.web.servlet.view.tiles3.TilesConfigurer} bean.
	 * <p>
	 *  注册Tiles 3x视图解析器<p> <strong>注意</strong>,您还必须通过添加{@link orgspringframeworkwebservletviewtiles3TilesConfigurer}
	 *  bean来配置Tiles。
	 * 
	 */
	public UrlBasedViewResolverRegistration tiles() {
		if (this.applicationContext != null && !hasBeanOfType(TilesConfigurer.class)) {
			throw new BeanInitializationException("In addition to a Tiles view resolver " +
					"there must also be a single TilesConfigurer bean in this web application context " +
					"(or its parent).");
		}
		TilesRegistration registration = new TilesRegistration();
		this.viewResolvers.add(registration.getViewResolver());
		return registration;
	}

	/**
	 * Register a FreeMarker view resolver with an empty default view name
	 * prefix and a default suffix of ".ftl".
	 * <p><strong>Note</strong> that you must also configure FreeMarker by adding a
	 * {@link org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer} bean.
	 * <p>
	 * 注册FreeMarker视图解析器,其中包含空默认视图名称前缀,默认后缀为"ftl"<p> <strong>注意</strong>,您还必须通过添加{@link orgspringframeworkwebservletviewfreemarkerFreeMarkerConfigurer}
	 *  bean来配置FreeMarker。
	 * 
	 */
	public UrlBasedViewResolverRegistration freeMarker() {
		if (this.applicationContext != null && !hasBeanOfType(FreeMarkerConfigurer.class)) {
			throw new BeanInitializationException("In addition to a FreeMarker view resolver " +
					"there must also be a single FreeMarkerConfig bean in this web application context " +
					"(or its parent): FreeMarkerConfigurer is the usual implementation. " +
					"This bean may be given any name.");
		}
		FreeMarkerRegistration registration = new FreeMarkerRegistration();
		this.viewResolvers.add(registration.getViewResolver());
		return registration;
	}

	/**
	 * Register Velocity view resolver with an empty default view name
	 * prefix and a default suffix of ".vm".
	 * <p><strong>Note</strong> that you must also configure Velocity by adding a
	 * {@link org.springframework.web.servlet.view.velocity.VelocityConfigurer} bean.
	 * <p>
	 *  注册Velocity视图解析器,具有空默认视图名称前缀,默认后缀为"vm"<p> <strong>注意</strong>,您还必须通过添加{@link orgspringframeworkwebservletviewvelocityVelocityConfigurer}
	 *  bean来配置Velocity。
	 * 
	 * 
	 * @deprecated as of Spring 4.3, in favor of FreeMarker
	 */
	@Deprecated
	public UrlBasedViewResolverRegistration velocity() {
		if (this.applicationContext != null && !hasBeanOfType(org.springframework.web.servlet.view.velocity.VelocityConfigurer.class)) {
			throw new BeanInitializationException("In addition to a Velocity view resolver " +
					"there must also be a single VelocityConfig bean in this web application context " +
					"(or its parent): VelocityConfigurer is the usual implementation. " +
					"This bean may be given any name.");
		}
		VelocityRegistration registration = new VelocityRegistration();
		this.viewResolvers.add(registration.getViewResolver());
		return registration;
	}

	/**
	 * Register a Groovy markup view resolver with an empty default view name
	 * prefix and a default suffix of ".tpl".
	 * <p>
	 *  注册一个Groovy标记视图解析器,其中包含一个空默认视图名称前缀,默认后缀为"tpl"
	 * 
	 */
	public UrlBasedViewResolverRegistration groovy() {
		if (this.applicationContext != null && !hasBeanOfType(GroovyMarkupConfigurer.class)) {
			throw new BeanInitializationException("In addition to a Groovy markup view resolver " +
					"there must also be a single GroovyMarkupConfig bean in this web application context " +
					"(or its parent): GroovyMarkupConfigurer is the usual implementation. " +
					"This bean may be given any name.");
		}
		GroovyMarkupRegistration registration = new GroovyMarkupRegistration();
		this.viewResolvers.add(registration.getViewResolver());
		return registration;
	}

	/**
	 * Register a script template view resolver with an empty default view name prefix and suffix.
	 * <p>
	 *  注册具有空默认视图名称前缀和后缀的脚本模板视图解析器
	 * 
	 * 
	 * @since 4.2
	 */
	public UrlBasedViewResolverRegistration scriptTemplate() {
		if (this.applicationContext != null && !hasBeanOfType(ScriptTemplateConfigurer.class)) {
			throw new BeanInitializationException("In addition to a script template view resolver " +
					"there must also be a single ScriptTemplateConfig bean in this web application context " +
					"(or its parent): ScriptTemplateConfigurer is the usual implementation. " +
					"This bean may be given any name.");
		}
		ScriptRegistration registration = new ScriptRegistration();
		this.viewResolvers.add(registration.getViewResolver());
		return registration;
	}

	/**
	 * Register a bean name view resolver that interprets view names as the names
	 * of {@link org.springframework.web.servlet.View} beans.
	 * <p>
	 * 注册一个bean名称视图解析器,它将视图名称解释为{@link orgspringframeworkwebservletView} bean的名称
	 * 
	 */
	public void beanName() {
		BeanNameViewResolver resolver = new BeanNameViewResolver();
		this.viewResolvers.add(resolver);
	}

	/**
	 * Register a {@link ViewResolver} bean instance. This may be useful to
	 * configure a custom (or 3rd party) resolver implementation. It may also be
	 * used as an alternative to other registration methods in this class when
	 * they don't expose some more advanced property that needs to be set.
	 * <p>
	 *  注册一个{@link ViewResolver} bean实例这可能对配置自定义(或第三方)解析器实现非常有用。当它们不暴露一些更高级的属性时,它也可以用作该类中其他注册方法的替代方法需要设置
	 * 
	 */
	public void viewResolver(ViewResolver viewResolver) {
		if (viewResolver instanceof ContentNegotiatingViewResolver) {
			throw new BeanInitializationException(
					"addViewResolver cannot be used to configure a ContentNegotiatingViewResolver. " +
					"Please use the method enableContentNegotiation instead.");
		}
		this.viewResolvers.add(viewResolver);
	}

	/**
	 * ViewResolver's registered through this registry are encapsulated in an
	 * instance of {@link org.springframework.web.servlet.view.ViewResolverComposite
	 * ViewResolverComposite} and follow the order of registration.
	 * This property determines the order of the ViewResolverComposite itself
	 * relative to any additional ViewResolver's (not registered here) present in
	 * the Spring configuration
	 * <p>By default this property is not set, which means the resolver is ordered
	 * at {@link Ordered#LOWEST_PRECEDENCE} unless content negotiation is enabled
	 * in which case the order (if not set explicitly) is changed to
	 * {@link Ordered#HIGHEST_PRECEDENCE}.
	 * <p>
	 * 通过此注册表注册的ViewResolver将封装在{@link orgspringframeworkwebservletviewViewResolverComposite ViewResolverComposite}
	 * 的实例中,并遵循注册顺序此属性确定ViewResolverComposite本身相对于Spring配置中存在的任何其他ViewResolver(未注册)的顺序<p>默认情况下,此属性未设置,这意味着解析
	 */
	public void order(int order) {
		this.order = order;
	}

	protected boolean hasBeanOfType(Class<?> beanType) {
		return !ObjectUtils.isEmpty(BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
				this.applicationContext, beanType, false, false));
	}


	protected int getOrder() {
		return (this.order != null ? this.order : Ordered.LOWEST_PRECEDENCE);
	}

	protected List<ViewResolver> getViewResolvers() {
		if (this.contentNegotiatingResolver != null) {
			return Collections.<ViewResolver>singletonList(this.contentNegotiatingResolver);
		}
		else {
			return this.viewResolvers;
		}
	}


	private static class TilesRegistration extends UrlBasedViewResolverRegistration {

		public TilesRegistration() {
			super(new TilesViewResolver());
		}
	}

	private static class VelocityRegistration extends UrlBasedViewResolverRegistration {

		@SuppressWarnings("deprecation")
		public VelocityRegistration() {
			super(new org.springframework.web.servlet.view.velocity.VelocityViewResolver());
			getViewResolver().setSuffix(".vm");
		}
	}

	private static class FreeMarkerRegistration extends UrlBasedViewResolverRegistration {

		public FreeMarkerRegistration() {
			super(new FreeMarkerViewResolver());
			getViewResolver().setSuffix(".ftl");
		}
	}

	private static class GroovyMarkupRegistration extends UrlBasedViewResolverRegistration {

		public GroovyMarkupRegistration() {
			super(new GroovyMarkupViewResolver());
			getViewResolver().setSuffix(".tpl");
		}
	}

	private static class ScriptRegistration extends UrlBasedViewResolverRegistration {

		public ScriptRegistration() {
			super(new ScriptTemplateViewResolver());
			getViewResolver();
		}
	}

}
