package com.syc.perms.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Menu implements Serializable {

	/**
	 * "title" : "二级菜单演示", "icon" : "&#xe61c;", "href" : "", "spread" : false,
	 * "children" : [
	 */
	private String title;
	private String icon;
	private String href;
	private String spread;
	private List<Menu> children;

}
