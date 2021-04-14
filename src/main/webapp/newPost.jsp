<div class="newPostContainer">
<form action="<%=request.getContextPath()%>/PostController" method="post">
<div class="mb-3">
<div class="mb-3">
  <label for="exampleFormControlTextarea1" class="form-label">Post:</label>
  <textarea class="form-control" id="exampleFormControlTextarea1" rows="3" name="text"></textarea>
</div>
  <label for="exampleFormControlInput1" class="form-label">Tag:</label>
  <input type="text" class="form-control" id="exampleFormControlInput1" name="tag">
</div>
<button type="submit">Post to feed</button>
</form>
</div>
