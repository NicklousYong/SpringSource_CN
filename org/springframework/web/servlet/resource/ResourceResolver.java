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

package org.springframework.web.servlet.resource;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;

/**
 * A strategy for resolving a request to a server-side resource.
 *
 * <p>Provides mechanisms for resolving an incoming request to an actual
 * {@link org.springframework.core.io.Resource} and for obtaining the
 * public URL path that clients should use when requesting the resource.
 *
 * <p>
 *  解决对服务器端资源的请求的策略
 * 
 * <p>提供用于将传入请求解析为实际的{@link orgspringframeworkcoreioResource}并获取客户端在请求资源时应使用的公共URL路径的机制
 * 
 * 
 * @author Jeremy Grelle
 * @author Rossen Stoyanchev
 * @author Sam Brannen
 * @since 4.1
 * @see org.springframework.web.servlet.resource.ResourceResolverChain
 */
public interface ResourceResolver {

	/**
	 * Resolve the supplied request and request path to a {@link Resource} that
	 * exists under one of the given resource locations.
	 * <p>
	 *  解决提供的请求和请求路径到存在于给定资源位置之一下的{@link资源}
	 * 
	 * 
	 * @param request the current request
	 * @param requestPath the portion of the request path to use
	 * @param locations the locations to search in when looking up resources
	 * @param chain the chain of remaining resolvers to delegate to
	 * @return the resolved resource or {@code null} if unresolved
	 */
	Resource resolveResource(HttpServletRequest request, String requestPath, List<? extends Resource> locations,
			ResourceResolverChain chain);

	/**
	 * Resolve the externally facing <em>public</em> URL path for clients to use
	 * to access the resource that is located at the given <em>internal</em>
	 * resource path.
	 * <p>This is useful when rendering URL links to clients.
	 * <p>
	 *  解决客户端用于访问位于给定的<em>内部资源路径</em>的资源的外部<em>公开</em> URL路径<p>在将URL链接呈现给客户端时,这很有用
	 * 
	 * @param resourcePath the internal resource path
	 * @param locations the locations to search in when looking up resources
	 * @param chain the chain of resolvers to delegate to
	 * @return the resolved public URL path or {@code null} if unresolved
	 */
	String resolveUrlPath(String resourcePath, List<? extends Resource> locations, ResourceResolverChain chain);

}
