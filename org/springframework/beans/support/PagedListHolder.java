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

package org.springframework.beans.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.util.Assert;

/**
 * PagedListHolder is a simple state holder for handling lists of objects,
 * separating them into pages. Page numbering starts with 0.
 *
 * <p>This is mainly targetted at usage in web UIs. Typically, an instance will be
 * instantiated with a list of beans, put into the session, and exported as model.
 * The properties can all be set/get programmatically, but the most common way will
 * be data binding, i.e. populating the bean from request parameters. The getters
 * will mainly be used by the view.
 *
 * <p>Supports sorting the underlying list via a {@link SortDefinition} implementation,
 * available as property "sort". By default, a {@link MutableSortDefinition} instance
 * will be used, toggling the ascending value on setting the same property again.
 *
 * <p>The data binding names have to be called "pageSize" and "sort.ascending",
 * as expected by BeanWrapper. Note that the names and the nesting syntax match
 * the respective JSTL EL expressions, like "myModelAttr.pageSize" and
 * "myModelAttr.sort.ascending".
 *
 * <p>
 *  PagedListHolder是一个简单的状态持有人,用于处理对象列表,将它们分隔成页面编号以0开头
 * 
 * 这主要是在web UI中使用的。通常情况下,一个实例将被实例化为一个bean列表,放在会话中并作为模型导出。
 * 这些属性都可以通过编程方式设置/获取,但最常见的方式是是数据绑定,即从请求参数填充bean getters将主要由视图使用。
 * 
 *  <p>支持通过{@link SortDefinition}实现对底层列表进行排序,作为属性"sort"可用默认情况下,将使用{@link MutableSortDefinition}实例,在重新设置相
 * 同属性时切换升序值。
 * 
 * <p>数据绑定名称必须被称为"pageSize"和"sortascending",如BeanWrapper所预期的。
 * 请注意,名称和嵌套语法与各自的JSTL EL表达式匹配,如"myModelAttrpageSize"和"myModelAttrsortascending"。
 * 
 * 
 * @author Juergen Hoeller
 * @since 19.05.2003
 * @see #getPageList()
 * @see org.springframework.beans.support.MutableSortDefinition
 */
@SuppressWarnings("serial")
public class PagedListHolder<E> implements Serializable {

	public static final int DEFAULT_PAGE_SIZE = 10;

	public static final int DEFAULT_MAX_LINKED_PAGES = 10;


	private List<E> source;

	private Date refreshDate;

	private SortDefinition sort;

	private SortDefinition sortUsed;

	private int pageSize = DEFAULT_PAGE_SIZE;

	private int page = 0;

	private boolean newPageSet;

	private int maxLinkedPages = DEFAULT_MAX_LINKED_PAGES;


	/**
	 * Create a new holder instance.
	 * You'll need to set a source list to be able to use the holder.
	 * <p>
	 *  创建一个新的持有者实例您需要设置源列表才能使用持有者
	 * 
	 * 
	 * @see #setSource
	 */
	public PagedListHolder() {
		this(new ArrayList<E>(0));
	}

	/**
	 * Create a new holder instance with the given source list, starting with
	 * a default sort definition (with "toggleAscendingOnProperty" activated).
	 * <p>
	 *  使用给定的源列表创建一个新的持有者实例,从默认排序定义开始(激活"toggleAscendingOnProperty")
	 * 
	 * 
	 * @param source the source List
	 * @see MutableSortDefinition#setToggleAscendingOnProperty
	 */
	public PagedListHolder(List<E> source) {
		this(source, new MutableSortDefinition(true));
	}

	/**
	 * Create a new holder instance with the given source list.
	 * <p>
	 *  使用给定的源列表创建一个新的持有者实例
	 * 
	 * 
	 * @param source the source List
	 * @param sort the SortDefinition to start with
	 */
	public PagedListHolder(List<E> source, SortDefinition sort) {
		setSource(source);
		setSort(sort);
	}


	/**
	 * Set the source list for this holder.
	 * <p>
	 *  设置此持有人的来源列表
	 * 
	 */
	public void setSource(List<E> source) {
		Assert.notNull(source, "Source List must not be null");
		this.source = source;
		this.refreshDate = new Date();
		this.sortUsed = null;
	}

	/**
	 * Return the source list for this holder.
	 * <p>
	 *  返回此持有人的来源列表
	 * 
	 */
	public List<E> getSource() {
		return this.source;
	}

	/**
	 * Return the last time the list has been fetched from the source provider.
	 * <p>
	 *  返回列表从源提供程序中获取的最后一次
	 * 
	 */
	public Date getRefreshDate() {
		return this.refreshDate;
	}

	/**
	 * Set the sort definition for this holder.
	 * Typically an instance of MutableSortDefinition.
	 * <p>
	 *  设置此持有人的排序定义通常是MutableSortDefinition的一个实例
	 * 
	 * 
	 * @see org.springframework.beans.support.MutableSortDefinition
	 */
	public void setSort(SortDefinition sort) {
		this.sort = sort;
	}

	/**
	 * Return the sort definition for this holder.
	 * <p>
	 * 返回此持有人的排序定义
	 * 
	 */
	public SortDefinition getSort() {
		return this.sort;
	}

	/**
	 * Set the current page size.
	 * Resets the current page number if changed.
	 * <p>Default value is 10.
	 * <p>
	 *  设置当前页面大小如果更改,则重置当前页面编号<p>默认值为10
	 * 
	 */
	public void setPageSize(int pageSize) {
		if (pageSize != this.pageSize) {
			this.pageSize = pageSize;
			if (!this.newPageSet) {
				this.page = 0;
			}
		}
	}

	/**
	 * Return the current page size.
	 * <p>
	 *  返回当前页面大小
	 * 
	 */
	public int getPageSize() {
		return this.pageSize;
	}

	/**
	 * Set the current page number.
	 * Page numbering starts with 0.
	 * <p>
	 *  设置当前页码页码编号从0开始
	 * 
	 */
	public void setPage(int page) {
		this.page = page;
		this.newPageSet = true;
	}

	/**
	 * Return the current page number.
	 * Page numbering starts with 0.
	 * <p>
	 *  返回当前页码页码编号从0开始
	 * 
	 */
	public int getPage() {
		this.newPageSet = false;
		if (this.page >= getPageCount()) {
			this.page = getPageCount() - 1;
		}
		return this.page;
	}

	/**
	 * Set the maximum number of page links to a few pages around the current one.
	 * <p>
	 *  将最大页面链接数设置为当前页面的几页
	 * 
	 */
	public void setMaxLinkedPages(int maxLinkedPages) {
		this.maxLinkedPages = maxLinkedPages;
	}

	/**
	 * Return the maximum number of page links to a few pages around the current one.
	 * <p>
	 *  将最大页面链接数返回到当前页面的几页
	 * 
	 */
	public int getMaxLinkedPages() {
		return this.maxLinkedPages;
	}


	/**
	 * Return the number of pages for the current source list.
	 * <p>
	 *  返回当前源列表的页数
	 * 
	 */
	public int getPageCount() {
		float nrOfPages = (float) getNrOfElements() / getPageSize();
		return (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages);
	}

	/**
	 * Return if the current page is the first one.
	 * <p>
	 *  如果当前页面是第一个页面,则返回
	 * 
	 */
	public boolean isFirstPage() {
		return getPage() == 0;
	}

	/**
	 * Return if the current page is the last one.
	 * <p>
	 *  如果当前页是最后一页返回
	 * 
	 */
	public boolean isLastPage() {
		return getPage() == getPageCount() -1;
	}

	/**
	 * Switch to previous page.
	 * Will stay on first page if already on first page.
	 * <p>
	 *  切换到上一页如果已经在第一页上,将保留在第一页
	 * 
	 */
	public void previousPage() {
		if (!isFirstPage()) {
			this.page--;
		}
	}

	/**
	 * Switch to next page.
	 * Will stay on last page if already on last page.
	 * <p>
	 *  切换到下一页如果已经在最后一页,将保留在最后一页
	 * 
	 */
	public void nextPage() {
		if (!isLastPage()) {
			this.page++;
		}
	}

	/**
	 * Return the total number of elements in the source list.
	 * <p>
	 * 返回源列表中的元素总数
	 * 
	 */
	public int getNrOfElements() {
		return getSource().size();
	}

	/**
	 * Return the element index of the first element on the current page.
	 * Element numbering starts with 0.
	 * <p>
	 *  返回当前页面上第一个元素的元素索引元素编号以0开头
	 * 
	 */
	public int getFirstElementOnPage() {
		return (getPageSize() * getPage());
	}

	/**
	 * Return the element index of the last element on the current page.
	 * Element numbering starts with 0.
	 * <p>
	 *  返回当前页面上最后一个元素的元素索引元素编号以0开头
	 * 
	 */
	public int getLastElementOnPage() {
		int endIndex = getPageSize() * (getPage() + 1);
		int size = getNrOfElements();
		return (endIndex > size ? size : endIndex) - 1;
	}

	/**
	 * Return a sub-list representing the current page.
	 * <p>
	 *  返回表示当前页面的子列表
	 * 
	 */
	public List<E> getPageList() {
		return getSource().subList(getFirstElementOnPage(), getLastElementOnPage() + 1);
	}

	/**
	 * Return the first page to which create a link around the current page.
	 * <p>
	 *  返回在当前页面周围创建链接的第一页
	 * 
	 */
	public int getFirstLinkedPage() {
		return Math.max(0, getPage() - (getMaxLinkedPages() / 2));
	}

	/**
	 * Return the last page to which create a link around the current page.
	 * <p>
	 *  返回在当前页面周围创建链接的最后一页
	 * 
	 */
	public int getLastLinkedPage() {
		return Math.min(getFirstLinkedPage() + getMaxLinkedPages() - 1, getPageCount() - 1);
	}


	/**
	 * Resort the list if necessary, i.e. if the current {@code sort} instance
	 * isn't equal to the backed-up {@code sortUsed} instance.
	 * <p>Calls {@code doSort} to trigger actual sorting.
	 * <p>
	 *  如果需要,可以调整列表,即当前的{@code sort}实例不等于备份的{@code sortUsed}实例调用{@code doSort}来触发实际排序
	 * 
	 * 
	 * @see #doSort
	 */
	public void resort() {
		SortDefinition sort = getSort();
		if (sort != null && !sort.equals(this.sortUsed)) {
			this.sortUsed = copySortDefinition(sort);
			doSort(getSource(), sort);
			setPage(0);
		}
	}

	/**
	 * Create a deep copy of the given sort definition,
	 * for use as state holder to compare a modified sort definition against.
	 * <p>Default implementation creates a MutableSortDefinition instance.
	 * Can be overridden in subclasses, in particular in case of custom
	 * extensions to the SortDefinition interface. Is allowed to return
	 * null, which means that no sort state will be held, triggering
	 * actual sorting for each {@code resort} call.
	 * <p>
	 * 创建给定排序定义的深层副本,作为状态持有者将修改的排序定义与<p>进行比较默认实现创建MutableSortDefinition实例可以在子类中覆盖,特别是在SortDefinition接口的自定义扩展
	 * 的情况下允许返回null,这意味着不会保留排序状态,触发每个{@code度假村}调用的实际排序。
	 * 
	 * 
	 * @param sort the current SortDefinition object
	 * @return a deep copy of the SortDefinition object
	 * @see MutableSortDefinition#MutableSortDefinition(SortDefinition)
	 */
	protected SortDefinition copySortDefinition(SortDefinition sort) {
		return new MutableSortDefinition(sort);
	}

	/**
	 * Actually perform sorting of the given source list, according to
	 * the given sort definition.
	 * <p>The default implementation uses Spring's PropertyComparator.
	 * Can be overridden in subclasses.
	 * <p>
	 * 
	 * @see PropertyComparator#sort(java.util.List, SortDefinition)
	 */
	protected void doSort(List<E> source, SortDefinition sort) {
		PropertyComparator.sort(source, sort);
	}

}
