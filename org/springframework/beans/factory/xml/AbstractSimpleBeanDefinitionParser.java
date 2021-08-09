/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
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

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.Conventions;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Convenient base class for when there exists a one-to-one mapping
 * between attribute names on the element that is to be parsed and
 * the property names on the {@link Class} being configured.
 *
 * <p>Extend this parser class when you want to create a single
 * bean definition from a relatively simple custom XML element. The
 * resulting {@code BeanDefinition} will be automatically
 * registered with the relevant
 * {@link org.springframework.beans.factory.support.BeanDefinitionRegistry}.
 *
 * <p>An example will hopefully make the use of this particular parser
 * class immediately clear. Consider the following class definition:
 *
 * <pre class="code">public class SimpleCache implements Cache {
 *
 *     public void setName(String name) {...}
 *     public void setTimeout(int timeout) {...}
 *     public void setEvictionPolicy(EvictionPolicy policy) {...}
 *
 *     // remaining class definition elided for clarity...
 * }</pre>
 *
 * <p>Then let us assume the following XML tag has been defined to
 * permit the easy configuration of instances of the above class;
 *
 * <pre class="code">&lt;caching:cache name="..." timeout="..." eviction-policy="..."/&gt;</pre>
 *
 * <p>All that is required of the Java developer tasked with writing
 * the parser to parse the above XML tag into an actual
 * {@code SimpleCache} bean definition is the following:
 *
 * <pre class="code">public class SimpleCacheBeanDefinitionParser extends AbstractSimpleBeanDefinitionParser {
 *
 *     protected Class getBeanClass(Element element) {
 *         return SimpleCache.class;
 *     }
 * }</pre>
 *
 * <p>Please note that the {@code AbstractSimpleBeanDefinitionParser}
 * is limited to populating the created bean definition with property values.
 * if you want to parse constructor arguments and nested elements from the
 * supplied XML element, then you will have to implement the
 * {@link #postProcess(org.springframework.beans.factory.support.BeanDefinitionBuilder, org.w3c.dom.Element)}
 * method and do such parsing yourself, or (more likely) subclass the
 * {@link AbstractSingleBeanDefinitionParser} or {@link AbstractBeanDefinitionParser}
 * classes directly.
 *
 * <p>The process of actually registering the
 * {@code SimpleCacheBeanDefinitionParser} with the Spring XML parsing
 * infrastructure is described in the Spring Framework reference documentation
 * (in one of the appendices).
 *
 * <p>For an example of this parser in action (so to speak), do look at
 * the source code for the
 * {@link org.springframework.beans.factory.xml.UtilNamespaceHandler.PropertiesBeanDefinitionParser};
 * the observant (and even not so observant) reader will immediately notice that
 * there is next to no code in the implementation. The
 * {@code PropertiesBeanDefinitionParser} populates a
 * {@link org.springframework.beans.factory.config.PropertiesFactoryBean}
 * from an XML element that looks like this:
 *
 * <pre class="code">&lt;util:properties location="jdbc.properties"/&gt;</pre>
 *
 * <p>The observant reader will notice that the sole attribute on the
 * {@code <util:properties/>} element matches the
 * {@link org.springframework.beans.factory.config.PropertiesFactoryBean#setLocation(org.springframework.core.io.Resource)}
 * method name on the {@code PropertiesFactoryBean} (the general
 * usage thus illustrated holds true for any number of attributes).
 * All that the {@code PropertiesBeanDefinitionParser} needs
 * actually do is supply an implementation of the
 * {@link #getBeanClass(org.w3c.dom.Element)} method to return the
 * {@code PropertiesFactoryBean} type.
 *
 * <p>
 *  在要解析的元素上的属性名称与正在配置的{@link Class}上的属性名称之间存在一对一映射时,方便的基类
 * 
 * <p>当您想从相对简单的自定义XML元素创建单个bean定义时,扩展此解析器类生成的{@code BeanDefinition}将自动注册到相关的{@link orgspringframeworkbeansfactorysupportBeanDefinitionRegistry}
 * 。
 * 
 *  <p>一个例子有希望使这个特定的解析器类立即清除考虑下面的类定义：
 * 
 *  <pre class ="code"> public class SimpleCache实现Cache {
 * 
 *  public void setName(String name){} public void setTimeout(int timeout){} public void setEvictionPoli
 * cy(EvictionPolicy policy){}。
 * 
 *  //清除剩余类定义} </pre>
 * 
 * <p>然后让我们假设已经定义了以下XML标签,以便容易地配置上述类的实例;
 * 
 *  <pre class ="code">&lt; caching：cache name =""timeout =""eviction-policy =""/&gt; </pre>
 * 
 *  <p> Java开发人员需要编写解析器将上述XML标签解析成实际的{@code SimpleCache} bean定义所需的所有内容如下所示：
 * 
 *  <pre class ="code"> public class SimpleCacheBeanDefinitionParser extends AbstractSimpleBeanDefinitio
 * nParser {。
 * 
 *  protected Class getBeanClass(Element element){return SimpleCacheclass; }} </pre>
 * 
 * 请注意,如果要从提供的XML元素中解析构造函数参数和嵌套元素,则{@code AbstractSimpleBeanDefinitionParser}仅限于使用属性值填充创建的bean定义,那么您将必须实
 * 现{@link# postProcess(orgspringframeworkbeansfactorysupportBeanDefinitionBuilder,orgw3cdomElement)}方法,
 * 并自己做这样的解析,或者(更有可能)直接将{@link AbstractSingleBeanDefinitionParser}或{@link AbstractBeanDefinitionParser}类
 * 子类化。
 * 
 *  <p>在Spring框架参考文档(其中一个附录)中描述了使用Spring XML解析基础结构实际注册{@code SimpleCacheBeanDefinitionParser}的过程。
 * 
 * @author Rob Harrop
 * @author Rick Evans
 * @author Juergen Hoeller
 * @since 2.0
 * @see Conventions#attributeNameToPropertyName(String)
 */
public abstract class AbstractSimpleBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

	/**
	 * Parse the supplied {@link Element} and populate the supplied
	 * {@link BeanDefinitionBuilder} as required.
	 * <p>This implementation maps any attributes present on the
	 * supplied element to {@link org.springframework.beans.PropertyValue}
	 * instances, and
	 * {@link BeanDefinitionBuilder#addPropertyValue(String, Object) adds them}
	 * to the
	 * {@link org.springframework.beans.factory.config.BeanDefinition builder}.
	 * <p>The {@link #extractPropertyName(String)} method is used to
	 * reconcile the name of an attribute with the name of a JavaBean
	 * property.
	 * <p>
	 * 
	 * <p>有关此解析器的一个示例(可以这么说),请查看{@link orgspringframeworkbeansfactoryxmlUtilNamespaceHandlerPropertiesBeanDefinitionParser}
	 * 的源代码;观察者(甚至不那么谨慎)的读者将立即注意到,实现中没有代码。
	 * {@code PropertiesBeanDefinitionParser}从XML元素中填充一个{@link orgspringframeworkbeansfactoryconfigPropertiesFactoryBean}
	 * ,如下所示：。
	 * 
	 *  <pre class ="code">&lt; util：properties location ="jdbcproperties"/&gt; </pre>
	 * 
	 * <p>观察者将注意到,{@code <util：properties />}元素中的唯一属性与{@code PropertiesFactoryBean}中的{@link orgspringframeworkbeansfactoryconfigPropertiesFactoryBean#setLocation(orgspringframeworkcoreioResource)}
	 * 方法名称匹配(一般用法{@code PropertiesBeanDefinitionParser}实际需要的是提供{@link #getBeanClass(orgw3cdomElement)}方法的一个
	 * 实现来返回{@code PropertiesFactoryBean}类型。
	 * 
	 * 
	 * @param element the XML element being parsed
	 * @param builder used to define the {@code BeanDefinition}
	 * @see #extractPropertyName(String)
	 */
	@Override
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
		NamedNodeMap attributes = element.getAttributes();
		for (int x = 0; x < attributes.getLength(); x++) {
			Attr attribute = (Attr) attributes.item(x);
			if (isEligibleAttribute(attribute, parserContext)) {
				String propertyName = extractPropertyName(attribute.getLocalName());
				Assert.state(StringUtils.hasText(propertyName),
						"Illegal property name returned from 'extractPropertyName(String)': cannot be null or empty.");
				builder.addPropertyValue(propertyName, attribute.getValue());
			}
		}
		postProcess(builder, element);
	}

	/**
	 * Determine whether the given attribute is eligible for being
	 * turned into a corresponding bean property value.
	 * <p>The default implementation considers any attribute as eligible,
	 * except for the "id" attribute and namespace declaration attributes.
	 * <p>
	 * 解析提供的{@link元素}并根据需要填充提供的{@link BeanDefinitionBuilder} <p>此实现将提供的元素上存在的任何属性映射到{@link orgspringframeworkbeansPropertyValue}
	 * 实例,{@link BeanDefinitionBuilder#addPropertyValue(String, Object)将它们添加到{@link orgspringframeworkbeansfactoryconfigBeanDefinition builder}
	 *  <p> {@link #extractPropertyName(String)}方法用于调整属性的名称与JavaBean属性的名称。
	 * 
	 * 
	 * @param attribute the XML attribute to check
	 * @param parserContext the {@code ParserContext}
	 * @see #isEligibleAttribute(String)
	 */
	protected boolean isEligibleAttribute(Attr attribute, ParserContext parserContext) {
		String fullName = attribute.getName();
		return (!fullName.equals("xmlns") && !fullName.startsWith("xmlns:") &&
				isEligibleAttribute(parserContext.getDelegate().getLocalName(attribute)));
	}

	/**
	 * Determine whether the given attribute is eligible for being
	 * turned into a corresponding bean property value.
	 * <p>The default implementation considers any attribute as eligible,
	 * except for the "id" attribute.
	 * <p>
	 *  确定给定的属性是否有资格被转换为相应的bean属性值<p>默认实现将任何属性视为合格的,除了"id"属性和命名空间声明属性
	 * 
	 * 
	 * @param attributeName the attribute name taken straight from the
	 * XML element being parsed (never {@code null})
	 */
	protected boolean isEligibleAttribute(String attributeName) {
		return !ID_ATTRIBUTE.equals(attributeName);
	}

	/**
	 * Extract a JavaBean property name from the supplied attribute name.
	 * <p>The default implementation uses the
	 * {@link Conventions#attributeNameToPropertyName(String)}
	 * method to perform the extraction.
	 * <p>The name returned must obey the standard JavaBean property name
	 * conventions. For example for a class with a setter method
	 * '{@code setBingoHallFavourite(String)}', the name returned had
	 * better be '{@code bingoHallFavourite}' (with that exact casing).
	 * <p>
	 * 确定给定的属性是否有资格被转换为相应的bean属性值<p>默认实现将任何属性视为合格的,除了"id"属性
	 * 
	 * 
	 * @param attributeName the attribute name taken straight from the
	 * XML element being parsed (never {@code null})
	 * @return the extracted JavaBean property name (must never be {@code null})
	 */
	protected String extractPropertyName(String attributeName) {
		return Conventions.attributeNameToPropertyName(attributeName);
	}

	/**
	 * Hook method that derived classes can implement to inspect/change a
	 * bean definition after parsing is complete.
	 * <p>The default implementation does nothing.
	 * <p>
	 *  从提供的属性名称中提取JavaBean属性名称<p>默认实现使用{@link约定#attributeNameToPropertyName(String)}方法执行提取<p>返回的名称必须遵守标准的Ja
	 * vaBean属性名称约定例如一个具有setter方法"{@code setBingoHallFavourite(String)}"的类,返回的名称最好是"{@code bingoHallFavourite}
	 * "(具有这种确切的外壳)。
	 * 
	 * 
	 * @param beanDefinition the parsed (and probably totally defined) bean definition being built
	 * @param element the XML element that was the source of the bean definition's metadata
	 */
	protected void postProcess(BeanDefinitionBuilder beanDefinition, Element element) {
	}

}
