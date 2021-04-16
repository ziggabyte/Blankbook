package model;

public class SearchBean {
	private String search; //Innehåller bara söktermen, men kändes ändå tydligare att ha det som en bean
	
	public SearchBean(String search) {
		this.setSearch(search);
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	
}
