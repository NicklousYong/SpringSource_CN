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

package org.springframework.web.servlet.view.groovy;

import java.util.Locale;

import org.springframework.web.servlet.view.AbstractTemplateViewResolver;

/**
 * Convenience subclass of @link AbstractTemplateViewResolver} that supports
 * {@link GroovyMarkupView} (i.e. Groovy XML/XHTML markup templates) and
 * custom subclasses of it.
 *
 * <p>The view class for all views created by this resolver can be specified
 * via the {@link #setViewClass(Class)} property.
 *
 * <p><b>Note:</b> When chaining ViewResolvers this resolver will check for the
 * existence of the specified template resources and only return a non-null
 * View object if a template is actually found.
 *
 * <p>
 *  支持{@link GroovyMarkupView}(即Groovy XML / XHTML标记模板)的@link AbstractTemplateViewResolver}的便利子类及其自定义子类。
 * 
 * <p>此解析器创建的所有视图的视图类可以通过{@link #setViewClass(Class)}属性指定
 * 
 *  <p> <b>注意：</b>当链接ViewResolvers时,此解析器将检查指定的模板资源的存在,并且只有在实际找到模板时才返回非空的View对象
 * 
 * 
 * @author Brian Clozel
 * @since 4.1
 * @see GroovyMarkupConfigurer
 */
public class GroovyMarkupViewResolver extends AbstractTemplateViewResolver {

	/**
	 * Sets the default {@link #setViewClass view class} to {@link #requiredViewClass}:
	 * by default {@link GroovyMarkupView}.
	 * <p>
	 *  将默认{@link #setViewClass视图类}设置为{@link #requiredViewClass}：默认情况下{@link GroovyMarkupView}
	 * 
	 */
	public GroovyMarkupViewResolver() {
		setViewClass(requiredViewClass());
	}

	/**
	 * A convenience constructor that allows for specifying {@link #setPrefix prefix}
	 * and {@link #setSuffix suffix} as constructor arguments.
	 * <p>
	 *  一个方便的构造函数,它允许将{@link #setPrefix prefix}和{@link #setSuffix suffix}指定为构造函数参数
	 * 
	 * 
	 * @param prefix the prefix that gets prepended to view names when building a URL
	 * @param suffix the suffix that gets appended to view names when building a URL
	 * @since 4.3
	 */
	public GroovyMarkupViewResolver(String prefix, String suffix) {
		this();
		setPrefix(prefix);
		setSuffix(suffix);
	}


	@Override
	protected Class<?> requiredViewClass() {
		return GroovyMarkupView.class;
	}

	/**
	 * This resolver supports i18n, so cache keys should contain the locale.
	 * <p>
	 *  此解析器支持i18n,因此缓存键应包含区域设置
	 */
	@Override
	protected Object getCacheKey(String viewName, Locale locale) {
		return viewName + "_" + locale;
	}

}
