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

package org.springframework.instrument.classloading.jboss;

import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.springframework.util.ReflectionUtils;

/**
 * Reflective wrapper around a JBoss 7 class loader methods
 * (discovered and called through reflection) for load-time weaving.
 *
 * <p>
 *  除非适用法律要求或以书面形式同意,根据许可证分发的软件以"按原样"分发,不附带任何明示或暗示的担保或条件,请参阅许可证中有关特定语言的权限和限制许可证
 * 
 * 
 * @author Costin Leau
 * @author Juergen Hoeller
 * @since 3.1
 */
class JBossModulesAdapter implements JBossClassLoaderAdapter {

	private static final String DELEGATING_TRANSFORMER_CLASS_NAME =
			"org.jboss.as.server.deployment.module.DelegatingClassFileTransformer";


	private final ClassLoader classLoader;

	private final Method addTransformer;

	private final Object delegatingTransformer;


	public JBossModulesAdapter(ClassLoader loader) {
		this.classLoader = loader;
		try {
			Field transformer = ReflectionUtils.findField(loader.getClass(), "transformer");
			transformer.setAccessible(true);
			this.delegatingTransformer = transformer.get(loader);
			if (!this.delegatingTransformer.getClass().getName().equals(DELEGATING_TRANSFORMER_CLASS_NAME)) {
				throw new IllegalStateException("Transformer not of the expected type DelegatingClassFileTransformer: " +
						this.delegatingTransformer.getClass().getName());
			}
			this.addTransformer = ReflectionUtils.findMethod(this.delegatingTransformer.getClass(),
					"addTransformer", ClassFileTransformer.class);
			this.addTransformer.setAccessible(true);
		}
		catch (Exception ex) {
			throw new IllegalStateException("Could not initialize JBoss 7 LoadTimeWeaver", ex);
		}
	}

	@Override
	public void addTransformer(ClassFileTransformer transformer) {
		try {
			this.addTransformer.invoke(this.delegatingTransformer, transformer);
		}
		catch (Exception ex) {
			throw new IllegalStateException("Could not add transformer on JBoss 7 ClassLoader " + this.classLoader, ex);
		}
	}

	@Override
	public ClassLoader getInstrumentableClassLoader() {
		return this.classLoader;
	}

}
