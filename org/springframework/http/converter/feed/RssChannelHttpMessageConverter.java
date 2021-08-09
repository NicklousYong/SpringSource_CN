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

package org.springframework.http.converter.feed;

import com.rometools.rome.feed.rss.Channel;

import org.springframework.http.MediaType;

/**
 * Implementation of {@link org.springframework.http.converter.HttpMessageConverter}
 * that can read and write RSS feeds. Specifically, this converter can handle {@link Channel}
 * objects from the <a href="https://github.com/rometools/rome">ROME</a> project.
 *
 * <p>><b>NOTE: As of Spring 4.1, this is based on the {@code com.rometools}
 * variant of ROME, version 1.5. Please upgrade your build dependency.</b>
 *
 * <p>By default, this converter reads and writes the media type ({@code application/rss+xml}).
 * This can be overridden through the {@link #setSupportedMediaTypes supportedMediaTypes} property.
 *
 * <p>
 * 实现可以读写RSS Feed的{@link orgspringframeworkhttpconverterHttpMessageConverter}具体来说,此转换器可以处理<a href=\"https://githubcom/rometools/rome\">
 *  ROME </a>项目中的{@link Channel}对象。
 * 
 *  注意：从Spring 41开始,这是基于ROME版本15的{@code comrometools}变体请升级您的构建依赖关系</b>
 * 
 * @author Arjen Poutsma
 * @since 3.0.2
 * @see Channel
 */
public class RssChannelHttpMessageConverter extends AbstractWireFeedHttpMessageConverter<Channel> {

	public RssChannelHttpMessageConverter() {
		super(new MediaType("application", "rss+xml"));
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return Channel.class.isAssignableFrom(clazz);
	}

}