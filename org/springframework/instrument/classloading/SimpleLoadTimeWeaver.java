/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.instrument.classloading;

import java.lang.instrument.ClassFileTransformer;

import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * {@code LoadTimeWeaver} that builds and exposes a
 * {@link SimpleInstrumentableClassLoader}.
 *
 * <p>Mainly intended for testing environments, where it is sufficient to
 * perform all class transformation on a newly created
 * {@code ClassLoader} instance.
 *
 * <p>
 *  {@code LoadTimeWeaver}构建并公开{@link SimpleInstrumentableClassLoader}
 * 
 * <p>主要用于测试环境,在新创建的{@code ClassLoader}实例上执行所有类转换就足够了
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see #getInstrumentableClassLoader()
 * @see SimpleInstrumentableClassLoader
 * @see ReflectiveLoadTimeWeaver
 */
public class SimpleLoadTimeWeaver implements LoadTimeWeaver {

	private final SimpleInstrumentableClassLoader classLoader;


	/**
	 * Create a new {@code SimpleLoadTimeWeaver} for the current context
	 * {@code ClassLoader}.
	 * <p>
	 *  为当前上下文{@code ClassLoader}创建一个新的{@code SimpleLoadTimeWeaver}
	 * 
	 * 
	 * @see SimpleInstrumentableClassLoader
	 */
	public SimpleLoadTimeWeaver() {
		this.classLoader = new SimpleInstrumentableClassLoader(ClassUtils.getDefaultClassLoader());
	}

	/**
	 * Create a new {@code SimpleLoadTimeWeaver} for the given
	 * {@code ClassLoader}.
	 * <p>
	 *  为给定的{@code ClassLoader}创建一个新的{@code SimpleLoadTimeWeaver}
	 * 
	 * 
	 * @param classLoader the {@code ClassLoader} to build a simple
	 * instrumentable {@code ClassLoader} on top of
	 */
	public SimpleLoadTimeWeaver(SimpleInstrumentableClassLoader classLoader) {
		Assert.notNull(classLoader, "ClassLoader must not be null");
		this.classLoader = classLoader;
	}


	@Override
	public void addTransformer(ClassFileTransformer transformer) {
		this.classLoader.addTransformer(transformer);
	}

	@Override
	public ClassLoader getInstrumentableClassLoader() {
		return this.classLoader;
	}

	/**
	 * This implementation builds a {@link SimpleThrowawayClassLoader}.
	 * <p>
	 *  此实现构建了一个{@link SimpleThrowawayClassLoader}
	 */
	@Override
	public ClassLoader getThrowawayClassLoader() {
		return new SimpleThrowawayClassLoader(getInstrumentableClassLoader());
	}

}
