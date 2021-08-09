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
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

import org.springframework.instrument.InstrumentationSavingAgent;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * {@link LoadTimeWeaver} relying on VM {@link Instrumentation}.
 *
 * <p>Start the JVM specifying the Java agent to be used, like as follows:
 *
 * <p><code class="code">-javaagent:path/to/org.springframework.instrument.jar</code>
 *
 * <p>where {@code org.springframework.instrument.jar} is a JAR file containing
 * the {@link InstrumentationSavingAgent} class, as shipped with Spring.
 *
 * <p>In Eclipse, for example, set the "Run configuration"'s JVM args to be of the form:
 *
 * <p><code class="code">-javaagent:${project_loc}/lib/org.springframework.instrument.jar</code>
 *
 * <p>
 *  {@link LoadTimeWeaver}依靠VM {@link Instrumentation}
 * 
 *  <p>启动指定要使用的Java代理的JVM,如下所示：
 * 
 *  <p> <code class ="code">  -  javaagent：path / to / orgspringframeworkinstrumentjar </code>
 * 
 * <p>其中{@code orgspringframeworkinstrumentjar}是一个包含{@link InstrumentationSavingAgent}类的JAR文件,与Spring一起发
 * 行。
 * 
 *  <p>在Eclipse中,例如,将"运行配置"的JVM参数设置为以下形式：
 * 
 *  <p> <code class ="code">  -  javaagent：$ {project_loc} / lib / orgspringframeworkinstrumentjar </code>
 * 。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see InstrumentationSavingAgent
 */
public class InstrumentationLoadTimeWeaver implements LoadTimeWeaver {

	private static final boolean AGENT_CLASS_PRESENT = ClassUtils.isPresent(
			"org.springframework.instrument.InstrumentationSavingAgent",
			InstrumentationLoadTimeWeaver.class.getClassLoader());


	private final ClassLoader classLoader;

	private final Instrumentation instrumentation;

	private final List<ClassFileTransformer> transformers = new ArrayList<ClassFileTransformer>(4);


	/**
	 * Create a new InstrumentationLoadTimeWeaver for the default ClassLoader.
	 * <p>
	 *  为默认的ClassLoader创建一个新的InstrumentationLoadTimeWeaver
	 * 
	 */
	public InstrumentationLoadTimeWeaver() {
		this(ClassUtils.getDefaultClassLoader());
	}

	/**
	 * Create a new InstrumentationLoadTimeWeaver for the given ClassLoader.
	 * <p>
	 *  为给定的ClassLoader创建一个新的InstrumentationLoadTimeWeaver
	 * 
	 * 
	 * @param classLoader the ClassLoader that registered transformers are supposed to apply to
	 */
	public InstrumentationLoadTimeWeaver(ClassLoader classLoader) {
		Assert.notNull(classLoader, "ClassLoader must not be null");
		this.classLoader = classLoader;
		this.instrumentation = getInstrumentation();
	}


	@Override
	public void addTransformer(ClassFileTransformer transformer) {
		Assert.notNull(transformer, "Transformer must not be null");
		FilteringClassFileTransformer actualTransformer =
				new FilteringClassFileTransformer(transformer, this.classLoader);
		synchronized (this.transformers) {
			if (this.instrumentation == null) {
				throw new IllegalStateException(
						"Must start with Java agent to use InstrumentationLoadTimeWeaver. See Spring documentation.");
			}
			this.instrumentation.addTransformer(actualTransformer);
			this.transformers.add(actualTransformer);
		}
	}

	/**
	 * We have the ability to weave the current class loader when starting the
	 * JVM in this way, so the instrumentable class loader will always be the
	 * current loader.
	 * <p>
	 *  我们有能力以这种方式启动JVM时编写当前的类加载器,所以可仪器类加载器将始终是当前加载器
	 * 
	 */
	@Override
	public ClassLoader getInstrumentableClassLoader() {
		return this.classLoader;
	}

	/**
	 * This implementation always returns a {@link SimpleThrowawayClassLoader}.
	 * <p>
	 *  这个实现总是返回一个{@link SimpleThrowawayClassLoader}
	 * 
	 */
	@Override
	public ClassLoader getThrowawayClassLoader() {
		return new SimpleThrowawayClassLoader(getInstrumentableClassLoader());
	}

	/**
	 * Remove all registered transformers, in inverse order of registration.
	 * <p>
	 *  删除所有注册的变压器,按注册的相反顺序
	 * 
	 */
	public void removeTransformers() {
		synchronized (this.transformers) {
			if (!this.transformers.isEmpty()) {
				for (int i = this.transformers.size() - 1; i >= 0; i--) {
					this.instrumentation.removeTransformer(this.transformers.get(i));
				}
				this.transformers.clear();
			}
		}
	}


	/**
	 * Check whether an Instrumentation instance is available for the current VM.
	 * <p>
	 * 检查仪器实例是否可用于当前虚拟机
	 * 
	 * 
	 * @see #getInstrumentation()
	 */
	public static boolean isInstrumentationAvailable() {
		return (getInstrumentation() != null);
	}

	/**
	 * Obtain the Instrumentation instance for the current VM, if available.
	 * <p>
	 *  获取当前虚拟机的Instrumentation实例(如果可用)
	 * 
	 * 
	 * @return the Instrumentation instance, or {@code null} if none found
	 * @see #isInstrumentationAvailable()
	 */
	private static Instrumentation getInstrumentation() {
		if (AGENT_CLASS_PRESENT) {
			return InstrumentationAccessor.getInstrumentation();
		}
		else {
			return null;
		}
	}


	/**
	 * Inner class to avoid InstrumentationSavingAgent dependency.
	 * <p>
	 *  内部类避免InstrumentationSavingAgent依赖
	 * 
	 */
	private static class InstrumentationAccessor {

		public static Instrumentation getInstrumentation() {
			return InstrumentationSavingAgent.getInstrumentation();
		}
	}


	/**
	 * Decorator that only applies the given target transformer to a specific ClassLoader.
	 * <p>
	 *  装饰器仅将给定的目标变压器应用于特定的ClassLoader
	 */
	private static class FilteringClassFileTransformer implements ClassFileTransformer {

		private final ClassFileTransformer targetTransformer;

		private final ClassLoader targetClassLoader;

		public FilteringClassFileTransformer(ClassFileTransformer targetTransformer, ClassLoader targetClassLoader) {
			this.targetTransformer = targetTransformer;
			this.targetClassLoader = targetClassLoader;
		}

		@Override
		public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
				ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

			if (!this.targetClassLoader.equals(loader)) {
				return null;
			}
			return this.targetTransformer.transform(
					loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
		}

		@Override
		public String toString() {
			return "FilteringClassFileTransformer for: " + this.targetTransformer.toString();
		}
	}

}
