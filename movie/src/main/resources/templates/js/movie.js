(function() {
    _.templateSettings = {
            interpolate : /\{%\=(.+?)%\}/g,
            evaluate : /\{%(.+?)%\}/g
    };

	Location = Backbone.Model.extend({
		initialize: function() {
		},
		defaults: {
			lat: 37.747837,
			lng: -122.4442064,
		},
		url:"/movies/latlngs/"
	});
	
	Locations = Backbone.Collection.extend({
		model: Location,
		url: "http://localhost:8080/latlngs",
		load: function() {
		}
	});

	MapView = Backbone.View.extend({
		el:$("#movieView"),
		render: function() {
			that.showMap();
		},
		events: {
			"click #findLocation" : "findLocation"
		},
		initialize: function() {
            this.startListenCollection();
		},
        startListenCollection: function() {
        	var that = this;
            this.listenTo(this.collection, "add", function(event) {
            });
            this.listenTo(this.collection, "change", function(event) {
            });
            this.listenTo(this.collection, "reset", function(event) {
            	that.clear();
            	that.render();
            });
        },
        
        stopListenCollection: function() {
        	this.stopListening(this.collection, "add");
        },

        clear: function() {
        	//TODO: clear GoogleMap
		},
		refresh: function() {
			var that = this;
			this.clear();
			this.collection.fetch({
				reset:true,
				success: function(collection, rsp, options) {
				}
			});
		},
        show: function() {
        },
        
        hide: function() {
        },
        
        showMap: function() {
        	var myCenter = new google.maps.LatLng(37.747837, -122.4442064);
            var mapProp = {
                    center: myCenter,
                    zoom: 13,
                    mapTypeId: google.maps.MapTypeId.ROADMAP
                };
                var map=new google.maps.Map(document.getElementById("googleMap"),mapProp);
                var marker=new google.maps.Marker({
                   position:myCenter
                });
                marker.setMap(map);
                var infowindow = new google.maps.InfoWindow({
                    content:"Hello World!"
                });
                
                google.maps.event.addListener(marker, 'click', function() {
                    infowindow.open(map,marker);
                });
        }
	});
	
	$(document).ready(function(){
		var locList = new Locations(Location);
		var mapView = new MapView({collection: locList});
		mapView.refresh();
		mapView.show();
	});
})();