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

/**
 * Defines the contract for adding one or more
 * {@link ClassFileTransformer ClassFileTransformers} to a {@link ClassLoader}.
 *
 * <p>Implementations may operate on the current context {@code ClassLoader}
 * or expose their own instrumentable {@code ClassLoader}.
 *
 * <p>
 *  定义将一个或多个{@link ClassFileTransformer ClassFileTransformers}添加到{@link ClassLoader}的合同
 * 
 * <p>实现可以在当前上下文{@code ClassLoader}中运行,或者显示他们自己的可检测的{@code ClassLoader}
 * 
 * 
 * @author Rod Johnson
 * @author Costin Leau
 * @since 2.0
 * @see java.lang.instrument.ClassFileTransformer
 */
public interface LoadTimeWeaver {

	/**
	 * Add a {@code ClassFileTransformer} to be applied by this
	 * {@code LoadTimeWeaver}.
	 * <p>
	 *  添加{@code ClassFileTransformer}以由{{@code LoadTimeWeaver}}应用
	 * 
	 * 
	 * @param transformer the {@code ClassFileTransformer} to add
	 */
	void addTransformer(ClassFileTransformer transformer);

	/**
	 * Return a {@code ClassLoader} that supports instrumentation
	 * through AspectJ-style load-time weaving based on user-defined
	 * {@link ClassFileTransformer ClassFileTransformers}.
	 * <p>May be the current {@code ClassLoader}, or a {@code ClassLoader}
	 * created by this {@link LoadTimeWeaver} instance.
	 * <p>
	 *  通过基于用户定义的{@link ClassFileTransformer ClassFileTransformers}的AspectJ风格的载入时间编织返回支持测试的{@code ClassLoader}
	 *  <pcode>可能是当前的{@code ClassLoader}或{@code ClassLoader}创建的由{@link LoadTimeWeaver}实例。
	 * 
	 * 
	 * @return the {@code ClassLoader} which will expose
	 * instrumented classes according to the registered transformers
	 */
	ClassLoader getInstrumentableClassLoader();

	/**
	 * Return a throwaway {@code ClassLoader}, enabling classes to be
	 * loaded and inspected without affecting the parent {@code ClassLoader}.
	 * <p>Should <i>not</i> return the same instance of the {@link ClassLoader}
	 * returned from an invocation of {@link #getInstrumentableClassLoader()}.
	 * <p>
	 *  返回一个一次性的{@code ClassLoader},使类能够被加载和检查,而不会影响父级{@code ClassLoader} <p>如果<i>不</i>返回从{@link ClassLoader}
	 * 返回的相同实例调用{@link #getInstrumentableClassLoader()}。
	 * 
	 * @return a temporary throwaway {@code ClassLoader}; should return
	 * a new instance for each call, with no existing state
	 */
	ClassLoader getThrowawayClassLoader();

}
