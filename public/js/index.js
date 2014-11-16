function createChart() {

  var optionsInitial = {
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

  var dataInitial = {
    labels: ['', '', ''],
    datasets: [
      {
        fillColor: "rgba(83,205,236,1)",
        strokeColor: "rgba(83,205,236,1)",
        data: result.scores
      }
    ]
  }

  cht = document.getElementById('votingChart');
  ctx = cht.getContext('2d');
  ctx.canvas.width  = $('.container').width();
  ctx.canvas.height = $('.container').height();
  chart = new Chart(ctx).Bar(dataInitial, optionsInitial);
}

function updateChart() {
  var optionsFinal = {
    scaleShowGridLines : false,
    barShowStroke: false,
    barValueSpacing: 10,
    scaleOverride : true,
    scaleSteps: result.scores[0],
    scaleStepWidth : 1,
    scaleFontSize : 20,
    scaleFontColor : "#666",
    animationSteps : 100
  }

  var dataFinal = {
    labels: result.names,
    datasets: [
      {
        fillColor: "rgba(83,205,236,1)",
        strokeColor: "rgba(83,205,236,1)",
        data: result.scores
      }
    ]
  }

  document.getElementById("votingChart").innerHTML = '';
  cht = document.getElementById('votingChart');
  ctx = cht.getContext('2d');
  ctx.canvas.width  = $('.container').width();
  ctx.canvas.height = $('.container').height();
  chart = new Chart(ctx).Bar(dataFinal, optionsFinal);
  chart.update()
}

var cht, ctx, chart;

$(document).ready(function() {
  createChart();
});