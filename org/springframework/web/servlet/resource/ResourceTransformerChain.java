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

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;

/**
 * A contract for invoking a chain of {@link ResourceTransformer}s where each resolver
 * is given a reference to the chain allowing it to delegate when necessary.
 *
 * <p>
 *  一个用于调用{@link ResourceTransformer}链的合同,其中每个解析器被赋予链的引用,允许它在必要时委派
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.1
 */
public interface ResourceTransformerChain {

	/**
	 * Return the {@code ResourceResolverChain} that was used to resolve the
	 * {@code Resource} being transformed. This may be needed for resolving
	 * related resources, e.g. links to other resources.
	 * <p>
	 * 返回用于解析正在转换的{@code资源}的{@code ResourceResolverChain}可能需要解决相关资源,例如链接到其他资源
	 * 
	 */
	ResourceResolverChain getResolverChain();

	/**
	 * Transform the given resource.
	 * <p>
	 *  转换给定的资源
	 * 
	 * @param request the current request
	 * @param resource the candidate resource to transform
	 * @return the transformed or the same resource, never {@code null}
	 * @throws IOException if transformation fails
	 */
	Resource transform(HttpServletRequest request, Resource resource) throws IOException;

}
