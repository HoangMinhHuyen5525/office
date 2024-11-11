package com.huyenhm.common;

import java.util.List;

public class PaginationRequest {
	private Integer  page;
	private Integer  size;
	private List<String> sort;

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public List<String> getSort() {
		return sort;
	}

	public void setSort(List<String> sort) {
		this.sort = sort;
	}

	public PaginationRequest(Integer page, Integer size, List<String> sort) {
		this.page = page;
		this.size = size;
		this.sort = sort;
	}
}
