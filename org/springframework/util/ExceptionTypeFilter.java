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

package org.springframework.util;

import java.util.Collection;

/**
 * An {@link InstanceFilter} implementation that handles exception types. A type
 * will match against a given candidate if it is assignable to that candidate.
 *
 * <p>
 *  除非适用法律要求或以书面形式同意,根据许可证分发的软件以"按原样"分发,不附带任何明示或暗示的担保或条件,请参阅许可证中有关特定语言的权限和限制许可证
 * 
 * 
 * @author Stephane Nicoll
 * @since 4.1
 */
public class ExceptionTypeFilter extends InstanceFilter<Class<? extends Throwable>> {

	public ExceptionTypeFilter(Collection<? extends Class<? extends Throwable>> includes,
			Collection<? extends Class<? extends Throwable>> excludes, boolean matchIfEmpty) {

		super(includes, excludes, matchIfEmpty);
	}

	@Override
	protected boolean match(Class<? extends Throwable> instance, Class<? extends Throwable> candidate) {
		return candidate.isAssignableFrom(instance);
	}

}
