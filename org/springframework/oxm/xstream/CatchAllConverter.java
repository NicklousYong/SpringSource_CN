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

package org.springframework.oxm.xstream;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * XStream {@link Converter} that supports all classes, but throws exceptions for
 * (un)marshalling.
 *
 * <p>The main purpose of this class is to
 * {@linkplain com.thoughtworks.xstream.XStream#registerConverter(com.thoughtworks.xstream.converters.Converter, int) register}
 * this converter as a catch-all last converter with a
 * {@linkplain com.thoughtworks.xstream.XStream#PRIORITY_NORMAL normal}
 * or higher priority, in addition to converters that explicitly handle the domain
 * classes that should be supported. As a result, default XStream converters with
 * lower priorities and possible security vulnerabilities do not get invoked.
 *
 * <p>For instance:
 * <pre class="code">
 * XStreamMarshaller unmarshaller = new XStreamMarshaller();
 * unmarshaller.getXStream().registerConverter(new MyDomainClassConverter(), XStream.PRIORITY_VERY_HIGH);
 * unmarshaller.getXStream().registerConverter(new CatchAllConverter(), XStream.PRIORITY_NORMAL);
 * MyDomainClass myObject = unmarshaller.unmarshal(source);
 * </pre
 *
 * <p>
 *  支持所有类的XStream {@link转换器},但抛出(un)编组的异常
 * 
 * <p>此类的主要目的是将{@linkplain comthoughtworksxstreamXStream#registerConverter(comthoughtworksxstreamconvertersConverter,int)register}
 * 转换为具有{@linkplain comthoughtworksxstreamXStream#PRIORITY_NORMAL normal}或更高优先级的全部最后一个转换器,除了显式处理应支持的域类的转
 * 换器因此,不会调用具有较低优先级和可能的安全漏洞的默认XStream转换器。
 * 
 *  例如：
 * 
 * @author Arjen Poutsma
 * @since 3.2.5
 */
public class CatchAllConverter implements Converter {

	@Override
	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class type) {
		return true;
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		throw new UnsupportedOperationException("Marshalling not supported");
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		throw new UnsupportedOperationException("Unmarshalling not supported");
	}

}
