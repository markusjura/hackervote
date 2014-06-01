function createChart() {

  var options = {
    scaleShowGridLines : false,
    barShowStroke: false,
    barValueSpacing: 10,
    scaleOverride : true,
    scaleSteps: result.scores[0],
    scaleStepWidth : 1,
    scaleFontSize : 20,
    scaleFontColor : "#666",
    animationSteps : 600
  }

  var data = {
    labels: result.names,
    datasets: [
      {
        fillColor: "rgba(83,205,236,1)",
        strokeColor: "rgba(83,205,236,1)",
        data: result.scores
      }
    ]
  }

  var cht = document.getElementById('votingChart');
  var ctx = cht.getContext('2d');
  ctx.canvas.width  = $('.container').width();
  ctx.canvas.height = $('.container').height();
  new Chart(ctx).Bar(data, options);
}

$(document).ready(function() {
  createChart();
});