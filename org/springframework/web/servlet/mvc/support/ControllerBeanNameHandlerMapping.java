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

package org.springframework.web.servlet.mvc.support;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

/**
 * Implementation of {@link org.springframework.web.servlet.HandlerMapping} that
 * follows a simple convention for generating URL path mappings from the <i>bean names</i>
 * of registered {@link org.springframework.web.servlet.mvc.Controller} beans
 * as well as {@code @Controller} annotated beans.
 *
 * <p>This is similar to {@link org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping}
 * but doesn't expect bean names to follow the URL convention: It turns plain bean names
 * into URLs by prepending a slash and optionally applying a specified prefix and/or suffix.
 * However, it only does so for well-known {@link #isControllerType controller types},
 * as listed above (analogous to {@link ControllerClassNameHandlerMapping}).
 *
 * <p>
 * 遵循从注册的{@link orgspringframeworkwebservletmvcController} bean的<i> bean名称</i>以及{@code @Controller}注释的be
 * an生成URL路径映射的简单约定的{@link orgspringframeworkwebservletHandlerMapping}的实现。
 * 
 *  <p>这与{@link orgspringframeworkwebservlethandlerBeanNameUrlHandlerMapping}类似,但不希望bean名称遵循URL惯例：它将粗略的b
 * ean名称转换为URL,前缀为斜杠,并且可选地应用指定的前缀和/或后缀。
 * 但是,它只是对于上述(类似于{@link ControllerClassNameHandlerMapping})的众所周知的{@link #isControllerType控制器类型},。
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.5.3
 * @see ControllerClassNameHandlerMapping
 * @see org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping
 * @deprecated as of 4.3, in favor of annotation-driven handler methods
 */
@Deprecated
public class ControllerBeanNameHandlerMapping extends AbstractControllerUrlHandlerMapping {

	private String urlPrefix = "";

	private String urlSuffix = "";


	/**
	 * Set an optional prefix to prepend to generated URL mappings.
	 * <p>By default this is an empty String. If you want a prefix like
	 * "/myapp/", you can set it for all beans mapped by this mapping.
	 * <p>
	 * 设置一个可选的前缀到前缀生成的URL映射<p>默认情况下,这是一个空字符串如果你想要一个前缀像"/ myapp /",你可以设置它通过映射映射的所有bean
	 * 
	 */
	public void setUrlPrefix(String urlPrefix) {
		this.urlPrefix = (urlPrefix != null ? urlPrefix : "");
	}

	/**
	 * Set an optional suffix to append to generated URL mappings.
	 * <p>By default this is an empty String. If you want a suffix like
	 * ".do", you can set it for all beans mapped by this mapping.
	 * <p>
	 *  设置一个可选的后缀附加到生成的URL映射<p>默认情况下,这是一个空字符串如果你想要一个像"do"这样的后缀,你可以为这个映射映射的所有bean设置它
	 * 
	 */
	public void setUrlSuffix(String urlSuffix) {
		this.urlSuffix = (urlSuffix != null ? urlSuffix : "");
	}


	@Override
	protected String[] buildUrlsForHandler(String beanName, Class<?> beanClass) {
		List<String> urls = new ArrayList<String>();
		urls.add(generatePathMapping(beanName));
		String[] aliases = getApplicationContext().getAliases(beanName);
		for (String alias : aliases) {
			urls.add(generatePathMapping(alias));
		}
		return StringUtils.toStringArray(urls);
	}

	/**
	 * Prepends a '/' if required and appends the URL suffix to the name.
	 * <p>
	 *  如果需要,请预处理"/",并将URL后缀追加到名称中
	 */
	protected String generatePathMapping(String beanName) {
		String name = (beanName.startsWith("/") ? beanName : "/" + beanName);
		StringBuilder path = new StringBuilder();
		if (!name.startsWith(this.urlPrefix)) {
			path.append(this.urlPrefix);
		}
		path.append(name);
		if (!name.endsWith(this.urlSuffix)) {
			path.append(this.urlSuffix);
		}
		return path.toString();
	}

}
