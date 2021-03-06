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

package org.springframework.beans.factory.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;

/**
 * Base interface used by the {@link DefaultBeanDefinitionDocumentReader}
 * for handling custom namespaces in a Spring XML configuration file.
 *
 * <p>Implementations are expected to return implementations of the
 * {@link BeanDefinitionParser} interface for custom top-level tags and
 * implementations of the {@link BeanDefinitionDecorator} interface for
 * custom nested tags.
 *
 * <p>The parser will call {@link #parse} when it encounters a custom tag
 * directly under the {@code <beans>} tags and {@link #decorate} when
 * it encounters a custom tag directly under a {@code <bean>} tag.
 *
 * <p>Developers writing their own custom element extensions typically will
 * not implement this interface directly, but rather make use of the provided
 * {@link NamespaceHandlerSupport} class.
 *
 * <p>
 *  由{@link DefaultBeanDefinitionDocumentReader}用于处理Spring XML配置文件中的自定义命名空间的基本界面
 * 
 * <p>实现期望返回{@link BeanDefinitionParser}接口的实现,用于定制顶层标签和{@link BeanDefinitionDecorator}接口的自定义嵌套标签的实现
 * 
 *  <p>当直接在{@code <beans>}标签下遇到自定义标签时,解析器将直接在{@code #decorate}下遇到自定义标签,直接在{@code < bean>}标签
 * 
 *  编写自己的自定义元素扩展的开发人员通常不会直接实现此接口,而是使用提供的{@link NamespaceHandlerSupport}类
 * 
 * 
 * @author Rob Harrop
 * @author Erik Wiersma
 * @since 2.0
 * @see DefaultBeanDefinitionDocumentReader
 * @see NamespaceHandlerResolver
 */
public interface NamespaceHandler {

	/**
	 * Invoked by the {@link DefaultBeanDefinitionDocumentReader} after
	 * construction but before any custom elements are parsed.
	 * <p>
	 *  由{@link DefaultBeanDefinitionDocumentReader}在构建之后调用,但在解析任何自定义元素之前
	 * 
	 * 
	 * @see NamespaceHandlerSupport#registerBeanDefinitionParser(String, BeanDefinitionParser)
	 */
	void init();

	/**
	 * Parse the specified {@link Element} and register any resulting
	 * {@link BeanDefinition BeanDefinitions} with the
	 * {@link org.springframework.beans.factory.support.BeanDefinitionRegistry}
	 * that is embedded in the supplied {@link ParserContext}.
	 * <p>Implementations should return the primary {@code BeanDefinition}
	 * that results from the parse phase if they wish to be used nested
	 * inside (for example) a {@code <property>} tag.
	 * <p>Implementations may return {@code null} if they will
	 * <strong>not</strong> be used in a nested scenario.
	 * <p>
	 * 解析指定的{@link元素}并使用嵌入在提供的{@link ParserContext} <p>实现中的{@link orgspringframeworkbeansfactoryBeanDefinitionRegistry}
	 * 注册任何生成的{@link BeanDefinition BeanDefinitions},应该返回主要的{@code BeanDefinition}如果他们希望嵌套在内部(例如)一个{@code <property>}
	 * 标签,则解析阶段的结果<p>如果他们将<strong>不</strong>被使用,实现可能返回{@code null}在嵌套场景中。
	 * 
	 * 
	 * @param element the element that is to be parsed into one or more {@code BeanDefinitions}
	 * @param parserContext the object encapsulating the current state of the parsing process
	 * @return the primary {@code BeanDefinition} (can be {@code null} as explained above)
	 */
	BeanDefinition parse(Element element, ParserContext parserContext);

	/**
	 * Parse the specified {@link Node} and decorate the supplied
	 * {@link BeanDefinitionHolder}, returning the decorated definition.
	 * <p>The {@link Node} may be either an {@link org.w3c.dom.Attr} or an
	 * {@link Element}, depending on whether a custom attribute or element
	 * is being parsed.
	 * <p>Implementations may choose to return a completely new definition,
	 * which will replace the original definition in the resulting
	 * {@link org.springframework.beans.factory.BeanFactory}.
	 * <p>The supplied {@link ParserContext} can be used to register any
	 * additional beans needed to support the main definition.
	 * <p>
	 * 解析指定的{@link Node}并装饰提供的{@link BeanDefinitionHolder},返回装饰的定义<p> {@link Node}可以是{@link orgw3cdomAttr}或{@link Element}
	 * ,具体取决于是否正在解析自定义属性或元素<p>实现可能会选择返回一个全新的定义,这将取代最终的{@link orgspringframeworkbeansfactoryBeanFactory}中的原始定
	 * 义。
	 * 
	 * @param source the source element or attribute that is to be parsed
	 * @param definition the current bean definition
	 * @param parserContext the object encapsulating the current state of the parsing process
	 * @return the decorated definition (to be registered in the BeanFactory),
	 * or simply the original bean definition if no decoration is required.
	 * A {@code null} value is strictly speaking invalid, but will be leniently
	 * treated like the case where the original bean definition gets returned.
	 */
	BeanDefinitionHolder decorate(Node source, BeanDefinitionHolder definition, ParserContext parserContext);

}
