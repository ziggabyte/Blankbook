package model;
public class PostBean {

	private String text;
	private String tagName;
	private String tagId;
	private String postId;
	
	public PostBean(String text, String tagName) { //används för att skapa PostBeans för nya poster
		this.text = text;
		this.tagName = tagName;
	}
	
	public PostBean(String text, String tagName, String postId) { //används för att skapa PostBeans vid sökning
		this.text = text;
		this.tagName = tagName;
		this.postId = postId;
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

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}
}
