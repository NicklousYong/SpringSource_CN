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

package org.springframework.core;

/**
 * Class that exposes the Spring version. Fetches the
 * "Implementation-Version" manifest attribute from the jar file.
 *
 * <p>Note that some ClassLoaders do not expose the package metadata,
 * hence this class might not be able to determine the Spring version
 * in all environments. Consider using a reflection-based check instead:
 * For example, checking for the presence of a specific Spring 2.0
 * method that you intend to call.
 *
 * <p>
 * 
 * @author Juergen Hoeller
 * @since 1.1
 */
public class SpringVersion {

	/**
	 * Return the full version string of the present Spring codebase,
	 * or {@code null} if it cannot be determined.
	 * <p>
	 *  暴露Spring版本的类从jar文件中获取"实现版本"清单属性
	 * 
	 * 请注意,某些ClassLoaders不会公开包元数据,因此此类可能无法在所有环境中确定Spring版本。请考虑使用基于反射的检查：例如,检查是否存在特定的Spring 20你打算打电话的方法
	 * 
	 * 
	 * @see Package#getImplementationVersion()
	 */
	public static String getVersion() {
		Package pkg = SpringVersion.class.getPackage();
		return (pkg != null ? pkg.getImplementationVersion() : null);
	}

}
