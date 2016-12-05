(function() {
    _.templateSettings = {
            interpolate : /\{%\=(.+?)%\}/g,
            evaluate : /\{%(.+?)%\}/g
    };
	       
    var _sync = Backbone.sync;
    Backbone.sync = function(method, model, options) {
    	options.beforeSend = function(xhr) {
			xhr.setRequestHeader("token", ___token___);
		};
		_sync(method, model, options);
    };

    User = Backbone.Model.extend({
		initialize: function() {
			this.url = "/users/" + this.attributes.uid;
		},
		defaults: {
			uid: "",
			name: "",
			password: "",
			role: 1
		},
//		idAttribute: "uid",
		url:"/users/",
		defauls: {
			uid:"",name:"",password:"",role:1
		}
	});
	
	UserList = Backbone.Collection.extend({
		model: User,
		url: "/users",
		load: function() {
		}
	});
	
	var templateUserRow = _.template($('#templateUserRow').html());
	var templateNewUserRow = _.template($('#templateNewUserRow').html());
	
	var showElement = function(ele) {
		$(ele).css('display','');
	}
	
	var hideElement = function(ele) {
		$(ele).css('display','none');
	}
	
	var isHidden = function(ele) {
		return $(ele).css("display")=='none';
	}
	
	UserView = Backbone.View.extend({
		el:$("#userView"),
		_editmode:false,
		render: function() {
			var users = this.collection.models;
			for (var i= 0; i< users.length; i++) {
               	users[i].idAttribute = "uid";
            	this.addUserRow(users[i].attributes);
			}
		},
		events: {
			"click #addUserIcon" : "openNewUserRow",
			"click #submitNewUserButton" : "submitNewUser",
			"click #deleteUserImg" : "deleteUser",
			"click #editUserImg" : "enableUserEdit",
		},
		initialize: function() {
            this.userTab = $("#userTab");
            this.userTabBody = $("#userTabBody");
            this.startListenCollection();
		},
        startListenCollection: function() {
        	var that = this;
            this.listenTo(this.collection, "add", function(event) {
                var user = event.attributes;
                var lastTrNum = $("#userTabBody").children("tr").length - 1;
            	var lastUserId = $("#newUserId_" + lastTrNum).val();
            	if (lastUserId === user.uid) {
                	$("#user_tr_" + lastTrNum).remove();
            	}
                that.addUserRow(user);
            });
            this.listenTo(this.collection, "change", function(event) {
            	that.clear();
            	that.render();
            });
            this.listenTo(this.collection, "reset", function(event) {
            	that.clear();
            	that.render();
            });
        },
        
        stopListenCollection: function() {
        	this.stopListening(this.collection, "add");
        },
		openNewUserRow: function() {
        	if (this._editmode === true) {
        		alert("有其他用户正在编辑中，请先完成再操作");
        		return;
        	}
        	this._editmode = true;
            var rowNum = $("#userTabBody").children("tr").length;
            var tbodyEnd = rowNum
            var color = rowNum % 2==0 ? "#dff0d8" : "#fcf8e3";
			var user = { "uid":"", "name":"", "password":"", "role":"" };
            var newUserRow = templateNewUserRow({trId:rowNum, user:user, color:color});
            this.userTabBody.append(newUserRow);
            var that = this;
            $("#newUserEdit_" + rowNum).click(function() {            	
                var user = { "uid":$("#newUserId_" + rowNum).val(), 
                		     "name":$("#newUserName_" + rowNum).val(),
                		     "password":$("#newUserPassword_" + rowNum).val(),
                		     "role":$("#newUserRole_" + rowNum).val()
                };
            	if (that.validate(user) !== true) {
            		return;
            	}
                that.collection.create(user, {
                	success: function(model, response) {
//                        $("#user_tr_" + rowNum).remove();
//                        that.addUserRow(user);
                		that._editmode = false;
                	},
                	error: function(error) {
	           		 	alert("创建失败:" + user.uid);
	           		    console.log("创建失败:" + error);
                	},
                	wait: true
                });
            });            
            $("#newUserDelete_" + rowNum).click(function() {
                $("#user_tr_" + rowNum).remove();
                that._editmode = false;
            });
		},

		clear: function() {
			var users = this.collection.models;
        	var that = this;
            var rowNum = $("#userTabBody").children("tr").length;
            $("#userViewBody").css('visibility',false);
        	for (var i=0; i<rowNum; i++) {
        		 $("#user_tr_" + i).remove();
            }
        	$("#userViewBody").css('visibility',true);
		},
		refresh: function() {
			this.clear();
			this.collection.fetch({
				reset:true
			});
		},
        show: function() {
        	$("#userView").css('display','block');
        },
        
        hide: function() {
        	$("#userView").css('display','none');
        },
        validate:function(user) {
        	var patt = /^[a-zA-Z_0-9]+$/
        	if(patt.test(user.uid) !== true) {
        		alert("用户名只能使用字母数字和英文下划线");
        		return false;
        	}
        	patt = /^[\u4e00-\u9fa5a-zA-Z0-9]+$/;
        	if(patt.test(user.name) !== true) {
        		alert("用户姓名只能使用中英文字母和数字");
        		return false;
        	}
        	patt = /^\S+$/;
        	if(patt.test(user.password) !== true) {
        		alert("密码不能包含空格");
        		return false;
        	}
        	return true;
        },
        addUserRow : function(user) {
        	var that = this;
            var rowNum = $("#userTabBody").children("tr").length;
            var tbodyEnd = rowNum
            var color = rowNum % 2==0 ? "#dff0d8" : "#fcf8e3";
            var userRow = templateUserRow({trId:rowNum, user:user, color:color});
            this.userTabBody.append(userRow);
            $("#userEdit_" + rowNum).click(function() {
            	if (that._editmode === true) {
            		alert("有其他用户正在编辑中，请先完成再操作");
            		return;
            	}
            	that._editmode = true;
            	var user = { 
            		"uid":$("#userId_" + rowNum).text(), 
              		"name":$("#userName_" + rowNum).text(),
              		"password":$("#userPassword_div_" + rowNum).text(),
              		"role":$("#userRole_div_" + rowNum).text()=="普通" ? 1 : 0
            	};
            	$("#inputPassword_" + rowNum).val(user.password);
            	$("#selectRole_" + rowNum).val(user.role);
            	hideElement("#userPassword_div_" + rowNum);
            	showElement("#inputPassword_" + rowNum);
            	hideElement("#userRole_div_" + rowNum);
            	showElement("#selectRole_" + rowNum);
            	hideElement("#userEdit_" + rowNum);
            	showElement("#userEditDone_" + rowNum);
            });
            $("#userEditDone_" + rowNum).click(function() {
        		var userList = that.collection.models;
        		var user = userList[rowNum];
            	var userObj = {
                		"uid":$.trim($("#userId_" + rowNum).text()), 
                  		"name":$.trim($("#userName_" + rowNum).text()),
              		    "password":$.trim($("#inputPassword_" + rowNum).val()),
              		    "role":$.trim($("#selectRole_" + rowNum).val())
                };
            	if (that.validate(userObj) !== true) {
            		return;
            	}
	            var newUser = new User(userObj);
            	if (validate(user) !== true) {
            		return;
            	}
	            newUser.idAttribute = "uid";
	            newUser.save(null, {
	            	success: function(model, response) {
	            		user.set({password:newUser.get("password"),
	            				  role:newUser.get("role")
	            		});
	                	$("#inputPassword_div_" + rowNum).val(user.password);
	                	$("#selectRole_div_" + rowNum).val(user.role == 1 ? "普通" : "管理");
	                	hideElement("#inputPassword_" + rowNum);
	                	showElement("#userPassword_div_" + rowNum);
	                	hideElement("#selectRole_" + rowNum);
	                	showElement("#userRole_div_" + rowNum);
	                	hideElement("#userEditDone_" + rowNum);
	                	showElement("#userEdit_" + rowNum);
	                	that._editmode = false;
	            	},
	            	error: function(error) {
	           		 	alert("修改失败:" + user.uid);
	           		    console.log("修改失败:" + error);
	            	},
	            	wait:true
	            });
            });
            $("#userDelete_" + rowNum).click(function() {
            	if (isHidden("#userEdit_" + rowNum)) { //current is update mode, delete icon means "cancel"
                	hideElement("#inputPassword_" + rowNum);
                	showElement("#userPassword_div_" + rowNum);
                	hideElement("#selectRole_" + rowNum);
                	showElement("#userRole_div_" + rowNum);
                	hideElement("#userEditDone_" + rowNum);
                	showElement("#userEdit_" + rowNum);
                	that._editmode = false;
            	} else {
                	if (that._editmode === true) {
                		alert("有其他用户正在编辑中，请先完成再操作");
                		return;
                	}
            		if(!confirm("你确定要删除该用户吗？")) {
            			return
            		}
            		var userList = that.collection.models;
            		var user = userList[rowNum];
                   	user.destroy({
                   		success: function(model, response) {
                   			that.collection.remove(user);
                   			that.clear();
                   			that.render();
                   		},
                   		error: function(error){
	            		 	 alert("删除失败:" + user.uid);
	               		 	 console.log("删除失败:" + error);
                   		},
                   		wait:true
                   	});
            	}
            });            
        }
	});
	
	$(document).ready(function(){
		var userList = new UserList(User);
		___userView___ = new UserView({collection: userList});
//		___userView___.refresh();
//		___userView___.show();
	});
})();