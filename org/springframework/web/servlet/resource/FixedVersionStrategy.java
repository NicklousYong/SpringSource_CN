/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.servlet.resource;

import org.springframework.core.io.Resource;

/**
 * A {@code VersionStrategy} that relies on a fixed version applied as a request
 * path prefix, e.g. reduced SHA, version name, release date, etc.
 *
 * <p>This is useful for example when {@link ContentVersionStrategy} cannot be
 * used such as when using JavaScript module loaders which are in charge of
 * loading the JavaScript resources and need to know their relative paths.
 *
 * <p>
 * 
 * @author Brian Clozel
 * @author Rossen Stoyanchev
 * @since 4.1
 * @see VersionResourceResolver
 */
public class FixedVersionStrategy extends AbstractVersionStrategy {

	private final String version;


	/**
	 * Create a new FixedVersionStrategy with the given version string.
	 * <p>
	 *  依赖于固定版本的{@code VersionStrategy}应用为请求路径前缀,例如减少SHA,版本名称,发布日期等
	 * 
	 * <p>这是非常有用的,例如当{@link ContentVersionStrategy}不能使用时,例如使用负责加载JavaScript资源并需要知道其相对路径的JavaScript模块加载程序时
	 * 
	 * 
	 * @param version the fixed version string to use
	 */
	public FixedVersionStrategy(String version) {
		super(new PrefixVersionPathStrategy(version));
		this.version = version;
	}


	@Override
	public String getResourceVersion(Resource resource) {
		return this.version;
	}

}
