var Hartbeat = (function() {

	function Hartbeat(interval) {
		this.beatInterval = interval || 30 * 1000;
		this.beating = null;
	}

	Hartbeat.prototype.start = function() {
		var self = this;
		this.beating = window.setInterval(function() {
			self.beat();
		}, self.beatInterval);
	};

	Hartbeat.prototype.stop = function() {
		clearInterval(this.beating);
	};

	Hartbeat.prototype.beat = function() {

		$.ajax({
			"url" : "../rest/ping",
			"cache" : false,
			"dataType" : 'json'
		}).done(function(data) {
		});
	};

	return Hartbeat;

})();