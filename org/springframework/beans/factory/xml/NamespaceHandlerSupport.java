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

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;

/**
 * Support class for implementing custom {@link NamespaceHandler NamespaceHandlers}.
 * Parsing and decorating of individual {@link Node Nodes} is done via {@link BeanDefinitionParser}
 * and {@link BeanDefinitionDecorator} strategy interfaces, respectively.
 *
 * <p>Provides the {@link #registerBeanDefinitionParser} and {@link #registerBeanDefinitionDecorator}
 * methods for registering a {@link BeanDefinitionParser} or {@link BeanDefinitionDecorator}
 * to handle a specific element.
 *
 * <p>
 * 支持类实现自定义{@link NamespaceHandler NamespaceHandlers}单独的{@link节点节点}的解析和装饰分别通过{@link BeanDefinitionParser}
 * 和{@link BeanDefinitionDecorator}策略接口完成。
 * 
 *  <p>提供{@link #registerBeanDefinitionParser}和{@link #registerBeanDefinitionDecorator}注册{@link BeanDefinitionParser}
 * 或{@link BeanDefinitionDecorator}以处理特定元素的方法。
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 * @see #registerBeanDefinitionParser(String, BeanDefinitionParser)
 * @see #registerBeanDefinitionDecorator(String, BeanDefinitionDecorator)
 */
public abstract class NamespaceHandlerSupport implements NamespaceHandler {

	/**
	 * Stores the {@link BeanDefinitionParser} implementations keyed by the
	 * local name of the {@link Element Elements} they handle.
	 * <p>
	 *  存储由他们处理的{@link元素元素}的本地名称键入的{@link BeanDefinitionParser}实现
	 * 
	 */
	private final Map<String, BeanDefinitionParser> parsers =
			new HashMap<String, BeanDefinitionParser>();

	/**
	 * Stores the {@link BeanDefinitionDecorator} implementations keyed by the
	 * local name of the {@link Element Elements} they handle.
	 * <p>
	 *  存储由他们处理的{@link Element Elements}的本地名称键入的{@link BeanDefinitionDecorator}实现
	 * 
	 */
	private final Map<String, BeanDefinitionDecorator> decorators =
			new HashMap<String, BeanDefinitionDecorator>();

	/**
	 * Stores the {@link BeanDefinitionDecorator} implementations keyed by the local
	 * name of the {@link Attr Attrs} they handle.
	 * <p>
	 * 存储他们处理的{@link Attr Attrs}的本地名称的{@link BeanDefinitionDecorator}实现
	 * 
	 */
	private final Map<String, BeanDefinitionDecorator> attributeDecorators =
			new HashMap<String, BeanDefinitionDecorator>();


	/**
	 * Parses the supplied {@link Element} by delegating to the {@link BeanDefinitionParser} that is
	 * registered for that {@link Element}.
	 * <p>
	 *  通过将{@link Element}注册的{@link BeanDefinitionParser}委托给提供的{@link Element}
	 * 
	 */
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		return findParserForElement(element, parserContext).parse(element, parserContext);
	}

	/**
	 * Locates the {@link BeanDefinitionParser} from the register implementations using
	 * the local name of the supplied {@link Element}.
	 * <p>
	 *  使用提供的{@link元素}的本地名称从注册实现中找到{@link BeanDefinitionParser}
	 * 
	 */
	private BeanDefinitionParser findParserForElement(Element element, ParserContext parserContext) {
		String localName = parserContext.getDelegate().getLocalName(element);
		BeanDefinitionParser parser = this.parsers.get(localName);
		if (parser == null) {
			parserContext.getReaderContext().fatal(
					"Cannot locate BeanDefinitionParser for element [" + localName + "]", element);
		}
		return parser;
	}

	/**
	 * Decorates the supplied {@link Node} by delegating to the {@link BeanDefinitionDecorator} that
	 * is registered to handle that {@link Node}.
	 * <p>
	 *  通过委托已经注册处理该{{linklink}}的{@link BeanDefinitionDecorator}来装载提供的{@link Node}
	 * 
	 */
	@Override
	public BeanDefinitionHolder decorate(
			Node node, BeanDefinitionHolder definition, ParserContext parserContext) {

		return findDecoratorForNode(node, parserContext).decorate(node, definition, parserContext);
	}

	/**
	 * Locates the {@link BeanDefinitionParser} from the register implementations using
	 * the local name of the supplied {@link Node}. Supports both {@link Element Elements}
	 * and {@link Attr Attrs}.
	 * <p>
	 *  使用所提供的{@link Node}的本地名称从注册实现中找到{@link BeanDefinitionParser}支持{@link元素元素}和{@link Attr Attrs}
	 * 
	 */
	private BeanDefinitionDecorator findDecoratorForNode(Node node, ParserContext parserContext) {
		BeanDefinitionDecorator decorator = null;
		String localName = parserContext.getDelegate().getLocalName(node);
		if (node instanceof Element) {
			decorator = this.decorators.get(localName);
		}
		else if (node instanceof Attr) {
			decorator = this.attributeDecorators.get(localName);
		}
		else {
			parserContext.getReaderContext().fatal(
					"Cannot decorate based on Nodes of type [" + node.getClass().getName() + "]", node);
		}
		if (decorator == null) {
			parserContext.getReaderContext().fatal("Cannot locate BeanDefinitionDecorator for " +
					(node instanceof Element ? "element" : "attribute") + " [" + localName + "]", node);
		}
		return decorator;
	}


	/**
	 * Subclasses can call this to register the supplied {@link BeanDefinitionParser} to
	 * handle the specified element. The element name is the local (non-namespace qualified)
	 * name.
	 * <p>
	 * 子类可以调用它来注册提供的{@link BeanDefinitionParser}来处理指定的元素元素名称是本地(非命名空间限定)名称
	 * 
	 */
	protected final void registerBeanDefinitionParser(String elementName, BeanDefinitionParser parser) {
		this.parsers.put(elementName, parser);
	}

	/**
	 * Subclasses can call this to register the supplied {@link BeanDefinitionDecorator} to
	 * handle the specified element. The element name is the local (non-namespace qualified)
	 * name.
	 * <p>
	 *  子类可以调用它来注册提供的{@link BeanDefinitionDecorator}来处理指定的元素元素名称是本地(非命名空间限定)名称
	 * 
	 */
	protected final void registerBeanDefinitionDecorator(String elementName, BeanDefinitionDecorator dec) {
		this.decorators.put(elementName, dec);
	}

	/**
	 * Subclasses can call this to register the supplied {@link BeanDefinitionDecorator} to
	 * handle the specified attribute. The attribute name is the local (non-namespace qualified)
	 * name.
	 * <p>
	 *  子类可以调用它来注册提供的{@link BeanDefinitionDecorator}来处理指定的属性属性名称是本地(非命名空间限定)名称
	 */
	protected final void registerBeanDefinitionDecoratorForAttribute(String attrName, BeanDefinitionDecorator dec) {
		this.attributeDecorators.put(attrName, dec);
	}

}
