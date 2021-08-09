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

package org.springframework.instrument.classloading.jboss;

import java.lang.instrument.ClassFileTransformer;

import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.instrument.classloading.SimpleThrowawayClassLoader;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * {@link LoadTimeWeaver} implementation for JBoss's instrumentable ClassLoader.
 * Autodetects the specific JBoss version at runtime: currently supports
 * JBoss AS 6 and 7, as well as WildFly 8 and 9 (as of Spring 4.2).
 *
 * <p><b>NOTE:</b> On JBoss 6, to avoid the container loading the classes before the
 * application actually starts, one needs to add a <tt>WEB-INF/jboss-scanning.xml</tt>
 * file to the application archive - with the following content:
 * <pre class="code">&lt;scanning xmlns="urn:jboss:scanning:1.0"/&gt;</pre>
 *
 * <p>Thanks to Ales Justin and Marius Bogoevici for the initial prototype.
 *
 * <p>
 *  JBoss可测试ClassLoader的{@link LoadTimeWeaver}实现在运行时自动检测特定的JBoss版本：目前支持JBoss AS 6和7以及WildFly 8和9(截至Sprin
 * g 42)。
 * 
 * <p> <b>注意：</b>在JBoss 6上,为了避免在应用程序实际启动之前容器加载这些类,需要将一个<tt> WEB-INF / jboss-scanxml </tt>文件添加到应用程序存档 - 具
 * 有以下内容：<pre class ="code">&lt; scans xmlns ="urn：jboss：scanning：10"/&gt; </pre>。
 * 
 *  感谢Ales Justin和Marius Bogoevici的初步原型
 * 
 * @author Costin Leau
 * @author Juergen Hoeller
 * @since 3.0
 */
public class JBossLoadTimeWeaver implements LoadTimeWeaver {

	private final JBossClassLoaderAdapter adapter;


	/**
	 * Create a new instance of the {@link JBossLoadTimeWeaver} class using
	 * the default {@link ClassLoader class loader}.
	 * <p>
	 * 
	 * 
	 * @see org.springframework.util.ClassUtils#getDefaultClassLoader()
	 */
	public JBossLoadTimeWeaver() {
		this(ClassUtils.getDefaultClassLoader());
	}

	/**
	 * Create a new instance of the {@link JBossLoadTimeWeaver} class using
	 * the supplied {@link ClassLoader}.
	 * <p>
	 *  使用默认的{@link ClassLoader类加载器}创建{@link JBossLoadTimeWeaver}类的新实例
	 * 
	 * 
	 * @param classLoader the {@code ClassLoader} to delegate to for weaving
	 * (must not be {@code null})
	 */
	public JBossLoadTimeWeaver(ClassLoader classLoader) {
		Assert.notNull(classLoader, "ClassLoader must not be null");
		if (classLoader.getClass().getName().startsWith("org.jboss.modules")) {
			// JBoss AS 7 or WildFly
			this.adapter = new JBossModulesAdapter(classLoader);
		}
		else {
			// JBoss AS 6
			this.adapter = new JBossMCAdapter(classLoader);
		}
	}


	@Override
	public void addTransformer(ClassFileTransformer transformer) {
		this.adapter.addTransformer(transformer);
	}

	@Override
	public ClassLoader getInstrumentableClassLoader() {
		return this.adapter.getInstrumentableClassLoader();
	}

	@Override
	public ClassLoader getThrowawayClassLoader() {
		return new SimpleThrowawayClassLoader(getInstrumentableClassLoader());
	}

}
