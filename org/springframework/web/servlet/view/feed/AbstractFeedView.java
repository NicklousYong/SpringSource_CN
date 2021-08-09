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

package org.springframework.web.servlet.view.feed;

import java.io.OutputStreamWriter;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.io.WireFeedOutput;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.AbstractView;

/**
 * Abstract base class for Atom and RSS Feed views, using the
 * <a href="https://github.com/rometools/rome">ROME</a> package.
 *
 * <p>><b>NOTE: As of Spring 4.1, this is based on the {@code com.rometools}
 * variant of ROME, version 1.5. Please upgrade your build dependency.</b>
 *
 * <p>Application-specific view classes will typically extend from either
 * {@link AbstractRssFeedView} or {@link AbstractAtomFeedView} instead of from this class.
 *
 * <p>Thanks to Jettro Coenradie and Sergio Bossa for the original feed view prototype!
 *
 * <p>
 *  使用<a href=\"https://githubcom/rometools/rome\"> ROME </a>软件包的Atom和RSS Feed视图的抽象基类
 * 
 * <p >> <b>注意：从Spring 41开始,这是基于ROME版本15的{@code comrometools}变体请升级您的构建依赖关系</b>
 * 
 *  <p>应用程序特定的视图类通常会从{@link AbstractRssFeedView}或{@link AbstractAtomFeedView}而不是此类扩展
 * 
 *  感谢Jettro Coenradie和Sergio Bossa的原始Feed视图原型！
 * 
 * 
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @since 3.0
 * @see AbstractRssFeedView
 * @see AbstractAtomFeedView
 */
public abstract class AbstractFeedView<T extends WireFeed> extends AbstractView {

	@Override
	protected final void renderMergedOutputModel(
			Map<String, Object> model, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		T wireFeed = newFeed();
		buildFeedMetadata(model, wireFeed, request);
		buildFeedEntries(model, wireFeed, request, response);

		setResponseContentType(request, response);
		if (!StringUtils.hasText(wireFeed.getEncoding())) {
			wireFeed.setEncoding("UTF-8");
		}

		WireFeedOutput feedOutput = new WireFeedOutput();
		ServletOutputStream out = response.getOutputStream();
		feedOutput.output(wireFeed, new OutputStreamWriter(out, wireFeed.getEncoding()));
		out.flush();
	}

	/**
	 * Create a new feed to hold the entries.
	 * <p>
	 *  创建一个新的feed来保存条目
	 * 
	 * 
	 * @return the newly created Feed instance
	 */
	protected abstract T newFeed();

	/**
	 * Populate the feed metadata (title, link, description, etc.).
	 * <p>Default is an empty implementation. Subclasses can override this method
	 * to add meta fields such as title, link description, etc.
	 * <p>
	 *  填充Feed元数据(标题,链接,描述等)<p>默认是一个空实现子类可以覆盖此方法来添加元字段,如标题,链接描述等
	 * 
	 * 
	 * @param model the model, in case meta information must be populated from it
	 * @param feed the feed being populated
	 * @param request in case we need locale etc. Shouldn't look at attributes.
	 */
	protected void buildFeedMetadata(Map<String, Object> model, T feed, HttpServletRequest request) {
	}

	/**
	 * Subclasses must implement this method to build feed entries, given the model.
	 * <p>Note that the passed-in HTTP response is just supposed to be used for
	 * setting cookies or other HTTP headers. The built feed itself will automatically
	 * get written to the response after this method returns.
	 * <p>
	 * 给出模型<p>,子类必须实现此方法来构建Feed条目。请注意,传入的HTTP响应仅适用于设置Cookie或其他HTTP标头内置Feed本身将自动在此之后写入响应方法返回
	 * 
	 * @param model the model Map
	 * @param feed the feed to add entries to
	 * @param request in case we need locale etc. Shouldn't look at attributes.
	 * @param response in case we need to set cookies. Shouldn't write to it.
	 * @throws Exception any exception that occurred during building
	 */
	protected abstract void buildFeedEntries(Map<String, Object> model, T feed,
			HttpServletRequest request, HttpServletResponse response) throws Exception;

}
