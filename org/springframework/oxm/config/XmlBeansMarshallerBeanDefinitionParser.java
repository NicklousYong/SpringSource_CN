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

package org.springframework.oxm.config;

import org.w3c.dom.Element;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;

/**
 * Parser for the {@code <oxm:xmlbeans-marshaller/>} element.
 *
 * <p>
 *  除非适用法律要求或以书面形式同意,根据许可证分发的软件以"按原样"分发,不附带任何明示或暗示的担保或条件,请参阅许可证中有关特定语言的权限和限制许可证
 * 
 * 
 * @author Arjen Poutsma
 * @since 3.0
 * @deprecated as of Spring 4.2, following the XMLBeans retirement at Apache
 */
@Deprecated
class XmlBeansMarshallerBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

	@Override
	protected String getBeanClassName(Element element) {
		return "org.springframework.oxm.xmlbeans.XmlBeansMarshaller";
	}

	@Override
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder beanDefinitionBuilder) {
		String optionsName = element.getAttribute("options");
		if (StringUtils.hasText(optionsName)) {
			beanDefinitionBuilder.addPropertyReference("xmlOptions", optionsName);
		}
	}

}
