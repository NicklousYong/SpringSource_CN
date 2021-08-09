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

package org.springframework.web.servlet.handler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

/**
 * Implementation of the {@link org.springframework.web.servlet.HandlerMapping}
 * interface that map from URLs to beans with names that start with a slash ("/"),
 * similar to how Struts maps URLs to action names.
 *
 * <p>This is the default implementation used by the
 * {@link org.springframework.web.servlet.DispatcherServlet}, along with
 * {@link org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping}.
 * Alternatively, {@link SimpleUrlHandlerMapping} allows for customizing a
 * handler mapping declaratively.
 *
 * <p>The mapping is from URL to bean name. Thus an incoming URL "/foo" would map
 * to a handler named "/foo", or to "/foo /foo2" in case of multiple mappings to
 * a single handler. Note: In XML definitions, you'll need to use an alias
 * name="/foo" in the bean definition, as the XML id may not contain slashes.
 *
 * <p>Supports direct matches (given "/test" -> registered "/test") and "*"
 * matches (given "/test" -> registered "/t*"). Note that the default is
 * to map within the current servlet mapping if applicable; see the
 * {@link #setAlwaysUseFullPath "alwaysUseFullPath"} property for details.
 * For details on the pattern options, see the
 * {@link org.springframework.util.AntPathMatcher} javadoc.
 *
 * <p>
 *  实现从URL到bean的{@link orgspringframeworkwebservletHandlerMapping}接口,其名称以斜杠("/")开头,类似于Struts将URL映射到动作名称。
 * 
 * <p>这是{@link orgspringframeworkwebservletDispatcherServlet}使用的默认实现,以及{@link orgspringframeworkwebservletmvcannotationDefaultAnnotationHandlerMapping}
 * 。
 * 或者,{@link SimpleUrlHandlerMapping}可以声明地自定义处理程序映射。
 * 
 *  <p>映射是从URL到bean名称因此,一个传入URL"/ foo"将映射到名为"/ foo"的处理程序,或者在多个映射到单个处理程序的情况下映射到"/ foo / foo2"注意： XML定义中,您
 * 需要在bean定义中使用别名name ="/ foo",因为XML id可能不包含斜杠。
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see SimpleUrlHandlerMapping
 */
public class BeanNameUrlHandlerMapping extends AbstractDetectingUrlHandlerMapping {

	/**
	 * Checks name and aliases of the given bean for URLs, starting with "/".
	 * <p>
	 * 
	 * <p>支持直接匹配(给定"/ test" - >注册"/ test")和"*"匹配(给定"/ test" - >注册"/ t *")注意默认是在当前映射servlet映射(如果适用)有关详细信息,请参阅
	 * {@link #setAlwaysUseFullPath"alwaysUseFullPath"}属性。
	 * 有关模式选项的详细信息,请参阅{@link orgspringframeworkutilAntPathMatcher} javadoc。
	 */
	@Override
	protected String[] determineUrlsForHandler(String beanName) {
		List<String> urls = new ArrayList<String>();
		if (beanName.startsWith("/")) {
			urls.add(beanName);
		}
		String[] aliases = getApplicationContext().getAliases(beanName);
		for (String alias : aliases) {
			if (alias.startsWith("/")) {
				urls.add(alias);
			}
		}
		return StringUtils.toStringArray(urls);
	}

}
