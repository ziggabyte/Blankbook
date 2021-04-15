package model;
public class PostBean {

	private String text;
	private String tagName;
	private String tagId;
	private String postId;
	private boolean isInDatabase;
	
	public PostBean(String text, String tagName) {
		this.text = text;
		this.tagName = tagName;
		this.isInDatabase = false;
	}
	
	public PostBean(String text, String tagName, String postId) {
		this.text = text;
		this.tagName = tagName;
		this.postId = postId;
		this.isInDatabase = false;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tag) {
		this.tagName = tag;
	}

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	public boolean isInDatabase() {
		return isInDatabase;
	}

	public void setIsInDatabase(boolean isInDatabase) {
		this.isInDatabase = isInDatabase;
	}

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}
}
