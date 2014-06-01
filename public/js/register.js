$(document).ready(function() {
  $('#add-member').click(function() {
    var count = $('#members li').length;
    console.log("count", count);
    if(count < 4) {
      var newMember = "<li>" +
                      "<div class='input-container'>" +
                      "<input name='members[" + count + "].name' type='text' placeholder='Member name'/>" +
                      "<input name='members[" + count + "].email' class='margin-left' type='text' placeholder='Member email' />" +
                      "</div>" +
                      "</li>";

      $('#members').append(newMember);
    }
  })
});