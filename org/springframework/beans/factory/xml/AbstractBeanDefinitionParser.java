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

package org.springframework.beans.factory.xml;

import org.w3c.dom.Element;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.util.StringUtils;

/**
 * Abstract {@link BeanDefinitionParser} implementation providing
 * a number of convenience methods and a
 * {@link AbstractBeanDefinitionParser#parseInternal template method}
 * that subclasses must override to provide the actual parsing logic.
 *
 * <p>Use this {@link BeanDefinitionParser} implementation when you want
 * to parse some arbitrarily complex XML into one or more
 * {@link BeanDefinition BeanDefinitions}. If you just want to parse some
 * XML into a single {@code BeanDefinition}, you may wish to consider
 * the simpler convenience extensions of this class, namely
 * {@link AbstractSingleBeanDefinitionParser} and
 * {@link AbstractSimpleBeanDefinitionParser}.
 *
 * <p>
 * 抽象{@link BeanDefinitionParser}实现提供了一些方便的方法和{@link AbstractBeanDefinitionParser#parseInternal模板方法},子类必
 * 须覆盖以提供实际的解析逻辑。
 * 
 *  <p>如果您想将一些任意复杂的XML解析成一个或多个{@link BeanDefinition BeanDefinitions},请使用此{@link BeanDefinitionParser}实现。
 * 如果您只想将一些XML解析为单个{@code BeanDefinition},则可能希望考虑这个类的更简单的便利扩展,即{@link AbstractSingleBeanDefinitionParser}
 * 和{@link AbstractSimpleBeanDefinitionParser}。
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Rick Evans
 * @author Dave Syer
 * @since 2.0
 */
public abstract class AbstractBeanDefinitionParser implements BeanDefinitionParser {

	/** Constant for the "id" attribute */
	public static final String ID_ATTRIBUTE = "id";

	/** Constant for the "name" attribute */
	public static final String NAME_ATTRIBUTE = "name";


	@Override
	public final BeanDefinition parse(Element element, ParserContext parserContext) {
		AbstractBeanDefinition definition = parseInternal(element, parserContext);
		if (definition != null && !parserContext.isNested()) {
			try {
				String id = resolveId(element, definition, parserContext);
				if (!StringUtils.hasText(id)) {
					parserContext.getReaderContext().error(
							"Id is required for element '" + parserContext.getDelegate().getLocalName(element)
									+ "' when used as a top-level tag", element);
				}
				String[] aliases = null;
				if (shouldParseNameAsAliases()) {
					String name = element.getAttribute(NAME_ATTRIBUTE);
					if (StringUtils.hasLength(name)) {
						aliases = StringUtils.trimArrayElements(StringUtils.commaDelimitedListToStringArray(name));
					}
				}
				BeanDefinitionHolder holder = new BeanDefinitionHolder(definition, id, aliases);
				registerBeanDefinition(holder, parserContext.getRegistry());
				if (shouldFireEvents()) {
					BeanComponentDefinition componentDefinition = new BeanComponentDefinition(holder);
					postProcessComponentDefinition(componentDefinition);
					parserContext.registerComponent(componentDefinition);
				}
			}
			catch (BeanDefinitionStoreException ex) {
				parserContext.getReaderContext().error(ex.getMessage(), element);
				return null;
			}
		}
		return definition;
	}

	/**
	 * Resolve the ID for the supplied {@link BeanDefinition}.
	 * <p>When using {@link #shouldGenerateId generation}, a name is generated automatically.
	 * Otherwise, the ID is extracted from the "id" attribute, potentially with a
	 * {@link #shouldGenerateIdAsFallback() fallback} to a generated id.
	 * <p>
	 * 解决所提供的{@link BeanDefinition}的ID {p}当使用{@link #shouldGenerateId generation}时,会自动生成名称否则,该ID将从"id"属性中提取,
	 * 可能会使用{@link #shouldGenerateIdAsFallback ()fallback}到生成的id。
	 * 
	 * 
	 * @param element the element that the bean definition has been built from
	 * @param definition the bean definition to be registered
	 * @param parserContext the object encapsulating the current state of the parsing process;
	 * provides access to a {@link org.springframework.beans.factory.support.BeanDefinitionRegistry}
	 * @return the resolved id
	 * @throws BeanDefinitionStoreException if no unique name could be generated
	 * for the given bean definition
	 */
	protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext)
			throws BeanDefinitionStoreException {

		if (shouldGenerateId()) {
			return parserContext.getReaderContext().generateBeanName(definition);
		}
		else {
			String id = element.getAttribute(ID_ATTRIBUTE);
			if (!StringUtils.hasText(id) && shouldGenerateIdAsFallback()) {
				id = parserContext.getReaderContext().generateBeanName(definition);
			}
			return id;
		}
	}

	/**
	 * Register the supplied {@link BeanDefinitionHolder bean} with the supplied
	 * {@link BeanDefinitionRegistry registry}.
	 * <p>Subclasses can override this method to control whether or not the supplied
	 * {@link BeanDefinitionHolder bean} is actually even registered, or to
	 * register even more beans.
	 * <p>The default implementation registers the supplied {@link BeanDefinitionHolder bean}
	 * with the supplied {@link BeanDefinitionRegistry registry} only if the {@code isNested}
	 * parameter is {@code false}, because one typically does not want inner beans
	 * to be registered as top level beans.
	 * <p>
	 * 使用提供的{@link BeanDefinitionRegistry注册表}注册提供的{@link BeanDefinitionHolder bean} <p>子类可以覆盖此方法来控制是否提供的{@link BeanDefinitionHolder bean}
	 * 实际上已注册,或者注册更多的bean <p>默认实现只有{@code isNested}参数为{@code false}时才将所提供的{@link BeanDefinitionHolder bean}注
	 * 册到提供的{@link BeanDefinitionRegistry注册表},因为一般通常不希望内部bean注册为顶级bean。
	 * 
	 * 
	 * @param definition the bean definition to be registered
	 * @param registry the registry that the bean is to be registered with
	 * @see BeanDefinitionReaderUtils#registerBeanDefinition(BeanDefinitionHolder, BeanDefinitionRegistry)
	 */
	protected void registerBeanDefinition(BeanDefinitionHolder definition, BeanDefinitionRegistry registry) {
		BeanDefinitionReaderUtils.registerBeanDefinition(definition, registry);
	}


	/**
	 * Central template method to actually parse the supplied {@link Element}
	 * into one or more {@link BeanDefinition BeanDefinitions}.
	 * <p>
	 *  中央模板方法来实际将提供的{@link元素}解析成一个或多个{@link BeanDefinition BeanDefinitions}
	 * 
	 * 
	 * @param element	the element that is to be parsed into one or more {@link BeanDefinition BeanDefinitions}
	 * @param parserContext the object encapsulating the current state of the parsing process;
	 * provides access to a {@link org.springframework.beans.factory.support.BeanDefinitionRegistry}
	 * @return the primary {@link BeanDefinition} resulting from the parsing of the supplied {@link Element}
	 * @see #parse(org.w3c.dom.Element, ParserContext)
	 * @see #postProcessComponentDefinition(org.springframework.beans.factory.parsing.BeanComponentDefinition)
	 */
	protected abstract AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext);

	/**
	 * Should an ID be generated instead of read from the passed in {@link Element}?
	 * <p>Disabled by default; subclasses can override this to enable ID generation.
	 * Note that this flag is about <i>always</i> generating an ID; the parser
	 * won't even check for an "id" attribute in this case.
	 * <p>
	 * 应该生成ID,而不是从{@link Element}中传递的ID读取? <p>默认情况下禁用;子类可以覆盖此值以启用ID生成注意,此标志关于<i>始终</i>生成ID;在这种情况下,解析器甚至不会检查"
	 * id"属性。
	 * 
	 * 
	 * @return whether the parser should always generate an id
	 */
	protected boolean shouldGenerateId() {
		return false;
	}

	/**
	 * Should an ID be generated instead if the passed in {@link Element} does not
	 * specify an "id" attribute explicitly?
	 * <p>Disabled by default; subclasses can override this to enable ID generation
	 * as fallback: The parser will first check for an "id" attribute in this case,
	 * only falling back to a generated ID if no value was specified.
	 * <p>
	 *  如果在{@link Element}中传递的明文没有指定"id"属性,是否应生成ID? <p>默认情况下禁用;子类可以覆盖此功能以使ID生成为备用：解析器将在此情况下首先检查"id"属性,如果未指定值
	 * ,则仅返回到生成的ID。
	 * 
	 * 
	 * @return whether the parser should generate an id if no id was specified
	 */
	protected boolean shouldGenerateIdAsFallback() {
		return false;
	}

	/**
	 * Determine whether the element's "name" attribute should get parsed as
	 * bean definition aliases, i.e. alternative bean definition names.
	 * <p>The default implementation returns {@code true}.
	 * <p>
	 * 确定元素的"name"属性是否应被解析为bean定义别名,即替代bean定义名称<p>默认实现返回{@code true}
	 * 
	 * 
	 * @return whether the parser should evaluate the "name" attribute as aliases
	 * @since 4.1.5
	 */
	protected boolean shouldParseNameAsAliases() {
		return true;
	}

	/**
	 * Determine whether this parser is supposed to fire a
	 * {@link org.springframework.beans.factory.parsing.BeanComponentDefinition}
	 * event after parsing the bean definition.
	 * <p>This implementation returns {@code true} by default; that is,
	 * an event will be fired when a bean definition has been completely parsed.
	 * Override this to return {@code false} in order to suppress the event.
	 * <p>
	 *  确定此解析器是否应在解析bean定义后触发{@link orgspringframeworkbeansfactoryparsingBeanComponentDefinition}事件<p>此实现默认返
	 * 回{@code true};也就是说,当一个bean定义已经被完全解析时,一个事件将被触发。
	 * 为了返回{@code false}来覆盖这个事件来阻止事件。
	 * 
	 * 
	 * @return {@code true} in order to fire a component registration event
	 * after parsing the bean definition; {@code false} to suppress the event
	 * @see #postProcessComponentDefinition
	 * @see org.springframework.beans.factory.parsing.ReaderContext#fireComponentRegistered
	 */
	protected boolean shouldFireEvents() {
		return true;
	}

	/**
	 * Hook method called after the primary parsing of a
	 * {@link BeanComponentDefinition} but before the
	 * {@link BeanComponentDefinition} has been registered with a
	 * {@link org.springframework.beans.factory.support.BeanDefinitionRegistry}.
	 * <p>Derived classes can override this method to supply any custom logic that
	 * is to be executed after all the parsing is finished.
	 * <p>The default implementation is a no-op.
	 * <p>
	 * 
	 * @param componentDefinition the {@link BeanComponentDefinition} that is to be processed
	 */
	protected void postProcessComponentDefinition(BeanComponentDefinition componentDefinition) {
	}

}
