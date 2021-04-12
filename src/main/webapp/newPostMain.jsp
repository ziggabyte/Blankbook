<form action="<%=request.getContextPath()%>/PostController" method="post">
<label for="text">Post:</label>
<textarea name="text"></textarea>
<label for="tag">Tag:</label>
<input type="text" name="tag">
<button type="submit">Post to feed</button>
</form>