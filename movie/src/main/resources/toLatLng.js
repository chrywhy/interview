var geocoder;
var map;
window.onload = function() {
	$("mapLoading").style.display = 'none';
	$("map_canvas").style.display = 'block';
	initialize()
};
function initialize() {
	geocoder = new google.maps.Geocoder();
	var latlng = new google.maps.LatLng(26.57, 106.72);
	var myOptions = {
		zoom : 8,
		center : latlng,
		mapTypeId : google.maps.MapTypeId.ROADMAP
	};
	map = new google.maps.Map($("map_canvas"), myOptions);
	var input = document.getElementById('address');
	new google.maps.places.Autocomplete(input);
	var latLngControl = new LatLngControl(map);
	google.maps.event.addListener(map, 'mouseover', function(mEvent) {
		latLngControl.set('visible', true)
	});
	google.maps.event.addListener(map, 'mouseout', function(mEvent) {
		latLngControl.set('visible', false)
	});
	google.maps.event.addListener(map, 'mousemove', function(mEvent) {
		latLngControl.updatePosition(mEvent.latLng)
	});
	enterPress()
}
function codeAddress(address) {
	var address = address || $("address").value;
	if (/\s*^\-?\d+(\.\d+)?\s*\,\s*\-?\d+(\.\d+)?\s*$/.test(address)) {
		var latlng = parseLatLng(address);
		if (latlng == null) {
			$("address").value = ""
		} else {
		}
	} else {
		geocoder.geocode({
			'address' : address
		}, geo)
	}
}
function parseLatLng(value) {
	value.replace('/\s//g');
	var coords = value.split(',');
	var lat = parseFloat(coords[0]);
	var lng = parseFloat(coords[1]);
	if (isNaN(lat) || isNaN(lng)) {
		return null
	} else {
		return new google.maps.LatLng(lat, lng)
	}
}
function geo(results, status) {
	if (status == google.maps.GeocoderStatus.OK) {
		map.setCenter(results[0].geometry.location);
		map.setZoom(15);
		var marker = new google.maps.Marker({
			map : map,
			title : '当前经纬度：' + results[0].geometry.location + ' 可点击拖动',
			position : results[0].geometry.location,
			draggable : true,
		});
		if (results[0].geometry.viewport) {
			var boundsOverlay = new google.maps.Rectangle({
				'bounds' : results[0].geometry.viewport,
				'strokeColor' : '#ff0000',
				'strokeOpacity' : 1.0,
				'strokeWeight' : 2.0,
				'fillOpacity' : 0.0
			});
			boundsOverlay.setMap(map)
		}
		$('latLngRange').innerHTML = results[0].geometry.viewport;
		updateMarkerPosition(results[0].geometry.location);
		geocodePosition(results[0].geometry.location);
		google.maps.event.addListener(marker, 'dragstart', function() {
			updateMarkerAddress('拖动...')
		});
		google.maps.event.addListener(marker, 'drag', function() {
			updateMarkerStatus('正在拖动...');
			updateMarkerPosition(marker.getPosition())
		});
		google.maps.event.addListener(marker, 'dragend', function() {
			updateMarkerStatus('拖动结束');
			geocodePosition(marker.getPosition())
		})
	} else {
		alert("Geocode was not successful for the following reason: " + status)
	}
}
function enterPress() {
	$("address").onkeyup = function(e) {
		if (!e)
			var e = window.event;
		if (e.keyCode != 13)
			return;
		$("go").click()
	}
}
function geocodePosition(pos) {
	geocoder.geocode({
		latLng : pos
	}, function(responses) {
		if (responses && responses.length > 0) {
			updateMarkerAddress(responses[0].formatted_address)
		} else {
			updateMarkerAddress('地址不能正确解析')
		}
	})
}
function updateMarkerStatus(str) {
	$('markerStatus').innerHTML = str
}
function updateMarkerPosition(latLng) {
	$('info').innerHTML = [ latLng.lat(), latLng.lng() ].join(', ');
	$('lat').value = latLng.lat();
	$('lng').value = latLng.lng()
}
function updateMarkerAddress(str) {
	if (str == "Cannot determine address at this location.") {
		str = "未能解析出当前位置地名"
	}
	$('endAddress').innerHTML = str
}
function $(id) {
	return document.getElementById(id)
}
function LatLngControl(map) {
	this.ANCHOR_OFFSET_ = new google.maps.Point(8, 8);
	this.node_ = this.createHtmlNode_();
	map.controls[google.maps.ControlPosition.TOP].push(this.node_);
	this.setMap(map);
	this.set('visible', false)
}
LatLngControl.prototype = new google.maps.OverlayView();
LatLngControl.prototype.draw = function() {
};
LatLngControl.prototype.createHtmlNode_ = function() {
	var divNode = document.createElement('div');
	divNode.id = 'latlng-control';
	divNode.index = 9999;
	return divNode
};
LatLngControl.prototype.visible_changed = function() {
	this.node_.style.display = this.get('visible') ? '' : 'none'
};
LatLngControl.prototype.updatePosition = function(latLng) {
	var projection = this.getProjection();
	var point = projection.fromLatLngToContainerPixel(latLng);
	this.node_.style.left = point.x + this.ANCHOR_OFFSET_.x + 'px';
	this.node_.style.top = point.y + this.ANCHOR_OFFSET_.y + 'px';
	this.node_.innerHTML = [ latLng.toUrlValue(4), '<br/>', point.x, 'px, ',
			point.y, 'px' ].join('')
};