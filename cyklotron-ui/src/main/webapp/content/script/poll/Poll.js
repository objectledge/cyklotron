function Poll(pollId, pollInstance, voteUrl, fetchUrl) {
	this.pollId = pollId;
	this.pollInstance = pollInstance;
	this.voteUrl = voteUrl;
	this.fetchUrl = fetchUrl;
}


Poll.prototype.loadData = function(data, showResults)
{

			if(data.voted || showResults){
				
				var questions = data.results;
				var totalVotes = 0;
				for(var question_id in questions)
				{	
					var answers = questions[question_id];
					for(var answer_id in answers)
					{
						$("#votes_for_answer-" + answer_id).html(answers[answer_id]);
                                                totalVotes += answers[answer_id];
					}
				}
				$("#totalVotes").html(totalVotes);
				
				$("#voting").hide();
				$("#results").show();
			} else {

			    $("#results").hide();
	            $("#voting").show();
			}
}

Poll.prototype.fetch = function(showResults) {
	$.ajax({
		url : this.fetchUrl,
		dataType : "jsonp",
		data : {
			pid : this.pollId
		},
		success : function(data) { poll.loadData(data, showResults); }
	});
};

Poll.prototype.vote = function(questionName, answerId){

    var v_data = { pid : this.pollId, poll_instance : this.pollInstance, component_instance : this.pollInstance};
    v_data[questionName] = answerId;

	$.ajax({
		url : this.voteUrl, 
		dataType : "jsonp",
		data : v_data,
		success : function(data) { poll.loadData(data, false); }
	});
};

jQuery(document).ready(function(){ 

	$("#sendVote").click(function() { 
    	if($("form[name='form_"+ poll.pollInstance +"'] input[type='radio']:checked").size() > 0) { 
    		var answer_id = $("form[name='form_"+ poll.pollInstance +"'] input[type='radio']:checked").val();
    		var question_name = $("form[name='form_"+ poll.pollInstance +"'] input[type='radio']:checked").attr('name'); 
    		poll.vote(question_name, answer_id);
    	}
	});

	$("form[name='form_"+ poll.pollInstance +"'] input[type='radio']").click(function() {
 		$("#sendVote").removeAttr('disabled');
	});

	$("#showResults").click(function(){ poll.fetch(true); });

	$("#sendVote").attr('disabled','disabled');
	$("#results").hide();
	$("#voting").show();

});