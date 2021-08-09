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

package org.springframework.web.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * A generic composite servlet {@link Filter} that just delegates its behavior
 * to a chain (list) of user-supplied filters, achieving the functionality of a
 * {@link FilterChain}, but conveniently using only {@link Filter} instances.
 *
 * <p>This is useful for filters that require dependency injection, and can
 * therefore be set up in a Spring application context. Typically, this
 * composite would be used in conjunction with {@link DelegatingFilterProxy},
 * so that it can be declared in Spring but applied to a servlet context.
 *
 * <p>
 * 一个通用的复合servlet {@link Filter},它将其行为委托给用户提供的过滤器的链(列表),实现{@link FilterChain}的功能,但只能方便地使用{@link Filter}实
 * 例。
 * 
 *  <p>这对于需要依赖注入的过滤器是有用的,因此可以在Spring应用程序上下文中设置。
 * 通常,此复合将与{@link DelegatingFilterProxy}结合使用,因此可以在Spring中声明它,但应用到一个servlet上下文。
 * 
 * 
 * @author Dave Syer
 * @since 3.1
 */
public class CompositeFilter implements Filter {

	private List<? extends Filter> filters = new ArrayList<Filter>();


	public void setFilters(List<? extends Filter> filters) {
		this.filters = new ArrayList<Filter>(filters);
	}


	/**
	 * Initialize all the filters, calling each one's init method in turn in the order supplied.
	 * <p>
	 *  初始化所有过滤器,按照提供的顺序依次调用每个过滤器的init方法
	 * 
	 * 
	 * @see Filter#init(FilterConfig)
	 */
	@Override
	public void init(FilterConfig config) throws ServletException {
		for (Filter filter : this.filters) {
			filter.init(config);
		}
	}

	/**
	 * Forms a temporary chain from the list of delegate filters supplied ({@link #setFilters})
	 * and executes them in order. Each filter delegates to the next one in the list, achieving
	 * the normal behavior of a {@link FilterChain}, despite the fact that this is a {@link Filter}.
	 * <p>
	 * 从提供的委托过滤器列表({@link #setFilters})中创建一个临时链,并按顺序执行它们。
	 * 每个过滤器委托列表中的下一个,实现{@link FilterChain}的正常行为,尽管事实上这是一个{@link过滤器}。
	 * 
	 * 
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		new VirtualFilterChain(chain, this.filters).doFilter(request, response);
	}

	/**
	 * Clean up all the filters supplied, calling each one's destroy method in turn, but in reverse order.
	 * <p>
	 * 
	 * @see Filter#init(FilterConfig)
	 */
	@Override
	public void destroy() {
		for (int i = this.filters.size(); i-- > 0;) {
			Filter filter = this.filters.get(i);
			filter.destroy();
		}
	}


	private static class VirtualFilterChain implements FilterChain {

		private final FilterChain originalChain;

		private final List<? extends Filter> additionalFilters;

		private int currentPosition = 0;

		public VirtualFilterChain(FilterChain chain, List<? extends Filter> additionalFilters) {
			this.originalChain = chain;
			this.additionalFilters = additionalFilters;
		}

		@Override
		public void doFilter(final ServletRequest request, final ServletResponse response)
				throws IOException, ServletException {

			if (this.currentPosition == this.additionalFilters.size()) {
				this.originalChain.doFilter(request, response);
			}
			else {
				this.currentPosition++;
				Filter nextFilter = this.additionalFilters.get(this.currentPosition - 1);
				nextFilter.doFilter(request, response, this);
			}
		}
	}

}
