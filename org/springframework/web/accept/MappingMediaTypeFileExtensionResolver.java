/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2016 the original author or authors.
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

package org.springframework.web.accept;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * An implementation of {@code MediaTypeFileExtensionResolver} that maintains
 * lookups between file extensions and MediaTypes in both directions.
 *
 * <p>Initially created with a map of file extensions and media types.
 * Subsequently subclasses can use {@link #addMapping} to add more mappings.
 *
 * <p>
 *  {@code MediaTypeFileExtensionResolver}的实现,它在两个方向上维护文件扩展名和MediaTypes之间的查找
 * 
 * <p>最初使用文件扩展名和媒体类型的映射创建后续子类可以使用{@link #addMapping}添加更多映射
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.2
 */
public class MappingMediaTypeFileExtensionResolver implements MediaTypeFileExtensionResolver {

	private final ConcurrentMap<String, MediaType> mediaTypes =
			new ConcurrentHashMap<String, MediaType>(64);

	private final MultiValueMap<MediaType, String> fileExtensions =
			new LinkedMultiValueMap<MediaType, String>();

	private final List<String> allFileExtensions = new LinkedList<String>();


	/**
	 * Create an instance with the given map of file extensions and media types.
	 * <p>
	 *  使用给定的文件扩展名和媒体类型的映射创建一个实例
	 * 
	 */
	public MappingMediaTypeFileExtensionResolver(Map<String, MediaType> mediaTypes) {
		if (mediaTypes != null) {
			for (Entry<String, MediaType> entries : mediaTypes.entrySet()) {
				String extension = entries.getKey().toLowerCase(Locale.ENGLISH);
				MediaType mediaType = entries.getValue();
				this.mediaTypes.put(extension, mediaType);
				this.fileExtensions.add(mediaType, extension);
				this.allFileExtensions.add(extension);
			}
		}
	}


	public Map<String, MediaType> getMediaTypes() {
		return this.mediaTypes;
	}

	protected List<MediaType> getAllMediaTypes() {
		return new ArrayList<MediaType>(this.mediaTypes.values());
	}

	/**
	 * Map an extension to a MediaType. Ignore if extension already mapped.
	 * <p>
	 *  将扩展名映射到MediaType时,如果扩展名已映射,则忽略此属性
	 * 
	 */
	protected void addMapping(String extension, MediaType mediaType) {
		MediaType previous = this.mediaTypes.putIfAbsent(extension, mediaType);
		if (previous == null) {
			this.fileExtensions.add(mediaType, extension);
			this.allFileExtensions.add(extension);
		}
	}


	@Override
	public List<String> resolveFileExtensions(MediaType mediaType) {
		List<String> fileExtensions = this.fileExtensions.get(mediaType);
		return (fileExtensions != null) ? fileExtensions : Collections.<String>emptyList();
	}

	@Override
	public List<String> getAllFileExtensions() {
		return Collections.unmodifiableList(this.allFileExtensions);
	}

	/**
	 * Use this method for a reverse lookup from extension to MediaType.
	 * <p>
	 *  使用此方法进行从扩展到MediaType的反向查找
	 * 
	 * @return a MediaType for the key, or {@code null} if none found
	 */
	protected MediaType lookupMediaType(String extension) {
		return this.mediaTypes.get(extension.toLowerCase(Locale.ENGLISH));
	}

}
