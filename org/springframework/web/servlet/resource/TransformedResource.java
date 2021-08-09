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

package org.springframework.web.servlet.resource;

import java.io.IOException;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

/**
 * An extension of {@link org.springframework.core.io.ByteArrayResource}
 * that a {@link ResourceTransformer} can use to represent an original
 * resource preserving all other information except the content.
 *
 * <p>
 *  除非适用法律要求或以书面形式同意,根据许可证分发的软件以"按原样"分发,不附带任何明示或暗示的担保或条件,请参阅许可证中有关特定语言的权限和限制许可证
 * 
 * 
 * @author Jeremy Grelle
 * @author Rossen Stoyanchev
 * @since 4.1
 */
public class TransformedResource extends ByteArrayResource {

	private final String filename;

	private final long lastModified;


	public TransformedResource(Resource original, byte[] transformedContent) {
		super(transformedContent);
		this.filename = original.getFilename();
		try {
			this.lastModified = original.lastModified();
		}
		catch (IOException ex) {
			// should never happen
			throw new IllegalArgumentException(ex);
		}
	}


	@Override
	public String getFilename() {
		return this.filename;
	}

	@Override
	public long lastModified() throws IOException {
		return this.lastModified;
	}

}
