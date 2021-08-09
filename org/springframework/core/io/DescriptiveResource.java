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

package org.springframework.core.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Simple {@link Resource} implementation that holds a resource description
 * but does not point to an actually readable resource.
 *
 * <p>To be used as placeholder if a {@code Resource} argument is
 * expected by an API but not necessarily used for actual reading.
 *
 * <p>
 *  简单的{@link Resource}实现,它保存资源描述,但不指向实际可读资源
 * 
 * <p>如果API预期{@code资源}参数,但不一定用于实际读取,则用作占位符
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.2.6
 */
public class DescriptiveResource extends AbstractResource {

	private final String description;


	/**
	 * Create a new DescriptiveResource.
	 * <p>
	 *  创建一个新的描述资源
	 * 
	 * 
	 * @param description the resource description
	 */
	public DescriptiveResource(String description) {
		this.description = (description != null ? description : "");
	}


	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public boolean isReadable() {
		return false;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		throw new FileNotFoundException(
				getDescription() + " cannot be opened because it does not point to a readable resource");
	}

	@Override
	public String getDescription() {
		return this.description;
	}


	/**
	 * This implementation compares the underlying description String.
	 * <p>
	 *  这个实现比较了底层的描述字符串
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		return (obj == this ||
			(obj instanceof DescriptiveResource && ((DescriptiveResource) obj).description.equals(this.description)));
	}

	/**
	 * This implementation returns the hash code of the underlying description String.
	 * <p>
	 *  此实现返回底层描述字符串的哈希码
	 */
	@Override
	public int hashCode() {
		return this.description.hashCode();
	}

}
