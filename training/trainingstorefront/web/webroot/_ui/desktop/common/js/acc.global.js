ACC.global = {
	// usage: ACC.global.addGoogleMapsApi("callback function"); // callback function name like "ACC.global.myfunction"
 	addGoogleMapsApi: function(callback){
		if(callback != undefined && $(".js-googleMapsApi").length == 0  ){
			$('head').append('<script class="js-googleMapsApi" type="text/javascript" src="//maps.googleapis.com/maps/api/js?key='+ACC.config.googleApiKey+'&sensor=false&callback='+callback+'"></script>');
		}else if(callback != undefined){
			eval(callback+"()");
		}
	}
}