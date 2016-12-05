(function() {
	Location = Backbone.Model.extend({
		initialize: function() {
		},
		defaults: {
			lat: 37.747837,
			lng: -122.4442064,
			movies:[]
		}
	});
	
	Locations = Backbone.Collection.extend({
		model: Location,
		url: "/latlngs",
		loadMovieTitles: function(movieView) {
			var that = this;
			$.ajax({
			    url:'/movieTitles',
			    success:function(data,textStatus,jqXHR){
			    	that.movieTitles = data;
			    	movieView.updateAutocomplete();
			    }
			})
		},
		loadLocations: function(movie) {
			if (movie == undefined) {
				this.url = "/latlngs";
			} else {
				this.url = "/latlngs/movies/" + movie.title;
			}
			this.fetch({
				reset:true,
				success: function(collection, rsp, options) {
				}
			});
		}
	});

	MapView = Backbone.View.extend({
		el:$("#movieView"),
		render: function() {
			this.showMap();
		},
		events: {
			"click #findLocation" : "findLocations",
			"click #showAll" : "findAllLocations"
		},
		initialize: function() {
            this.startListenCollection();
		},
        startListenCollection: function() {
        	var that = this;
            this.listenTo(this.collection, "reset", function(event) {
            	that.clear();
            	that.render();
            });
        },
        
        stopListenCollection: function() {
        	this.stopListening(this.collection, "reset");
        },
		
		updateAutocomplete: function() {
            $( "#movie" ).autocomplete({
                source: this.collection.movieTitles
            });
		},
		clear: function() {
			
		},
		
		refresh: function() {
			var that = this;
			this.clear();
			this.showMap();
			this.collection.loadMovieTitles(this);			
		},
        
		findAllLocations: function() {
			var that = this;
			this.collection.loadLocations();
		},
		
		findLocations: function() {
			var that = this;
			this.clear();
			this.collection.loadLocations({
				title: $("#movie").val()
			});
		},
		
        showMap: function() {
        	var myCenter = new google.maps.LatLng(37.747837, -122.4442064);
            var mapProp = {
                    center: myCenter,
                    zoom: 13,
                    mapTypeId: google.maps.MapTypeId.ROADMAP
                };
                var map=new google.maps.Map(document.getElementById("googleMap"),mapProp);
                for(var i=0; i<this.collection.models.length; i++) {
                	var latlng = this.collection.models[i];
                	if (latlng.get("lat") == 37.747837 && latlng.get("lng")==-122.4442064) {
                		continue;
                	}
                	var position = new google.maps.LatLng(latlng.get("lat"), latlng.get("lng"))
                    var marker = new google.maps.Marker({
//                        icon: bluePin,
                        map: map,
                        movies: latlng.get("movies"),
                        position:position
                     });
                     google.maps.event.addListener(marker, 'click', function() {
                    	 if (!this.infowindow) {
                        	 var content = "";
                        	 for (var i=0; i<this.movies.length;i++) {
                        		 var movie = this.movies[i];
                        		 content += movie.title + "<br>";
                        	 }
                        	 this.infowindow = new google.maps.InfoWindow({
                                 content: content
                             });
                    	 }
                    	 this.infowindow.open(map, this);
                     });
                }
        }
	});
	
	$(document).ready(function(){
		var locList = new Locations(Location);
		var mapView = new MapView({collection: locList});
		mapView.refresh();
	});
})();