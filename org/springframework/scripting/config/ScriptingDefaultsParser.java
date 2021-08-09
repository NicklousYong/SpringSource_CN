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

package org.springframework.scripting.config;

import org.w3c.dom.Element;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;

/**
/* <p>
/* 
/*  除非适用法律要求或以书面形式同意,根据许可证分发的软件以"按原样"分发,不附带任何明示或暗示的担保或条件,请参阅许可证中有关特定语言的权限和限制许可证
/* 
/* 
 * @author Mark Fisher
 * @since 2.5
 */
class ScriptingDefaultsParser implements BeanDefinitionParser {

	private static final String REFRESH_CHECK_DELAY_ATTRIBUTE = "refresh-check-delay";

	private static final String PROXY_TARGET_CLASS_ATTRIBUTE = "proxy-target-class";


	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		BeanDefinition bd =
				LangNamespaceUtils.registerScriptFactoryPostProcessorIfNecessary(parserContext.getRegistry());
		String refreshCheckDelay = element.getAttribute(REFRESH_CHECK_DELAY_ATTRIBUTE);
		if (StringUtils.hasText(refreshCheckDelay)) {
			bd.getPropertyValues().add("defaultRefreshCheckDelay", Long.valueOf(refreshCheckDelay));
		}
		String proxyTargetClass = element.getAttribute(PROXY_TARGET_CLASS_ATTRIBUTE);
		if (StringUtils.hasText(proxyTargetClass)) {
			bd.getPropertyValues().add("defaultProxyTargetClass", new TypedStringValue(proxyTargetClass, Boolean.class));
		}
		return null;
	}

}
