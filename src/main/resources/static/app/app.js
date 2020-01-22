
var arkApp = angular.module('atlasApp', []);

arkApp.controller("atlasCtrl",["$scope","$http","$interval","$sce",function($scope,$http,$interval,$sce)
{
	var vm = $scope;
	var nextUpdate="";
	var minExe;
	vm.lsBackups = [];
	vm.config = {};
	vm.clock = "";
	vm.todayDate = "";
	vm.timer="";
	vm.showLoader = false;
	vm.showConfig = false;
	
	vm.getBackups = function()
	{
		$http.get('/list').then(function(result)
		{
			vm.lsBackups = result.data;
		}, 
		function(error)
		{
			console.log("ERROR");
		
		});
	};
	
	vm.runBackup = function()
	{
		$http.get('/runBackup').then(function()
		{
			vm.getBackups();
		}, 
		function(error)
		{
			console.log("ERROR");
		
		});
	};
	
	vm.getConfig = function()
	{
		$http.get('/getConfig').then(function(result)
		{
			vm.config = result.data;
			var cron = vm.config.cronBackup.split(" ");
			minExe = cron[1].split("/")[1];
			delayedExe();
			
		}, 
		function(error)
		{
			console.log("ERROR");
		
		});
	};
	
	vm.showConfigBt = function(){
		if(vm.showConfig){
			vm.showConfig = false;
		}else{
			vm.showConfig = true;
		}
	};
	
	var delayedExe = function(){
		var nextMinute = moment().minutes()-(moment().minutes()%minExe)+(minExe * 1);
		nextUpdate = moment().minutes(nextMinute).seconds(5);
		vm.timer = nextUpdate.format("HH:mm:ss");
		vm.getBackups();
	};
	
	vm.getDate = function(d){
		var t = moment(d.timestamp).format("YYYY-MM-DD HH:mm");
		return t;
	};
	
	var idClock = $interval(function(){
		var currentDate = moment();
		vm.todayDate =currentDate.format("YYYY-MM-DD");
		vm.clock =currentDate.format("HH:mm:ss");
		
		var copyDate = moment(nextUpdate);
		
		if(copyDate.subtract(3,"seconds").isSameOrAfter(moment())){
			vm.showLoader = true;
			
		}else{
			vm.showLoader = false;
		}

	},1000);
	
	
	var idList =  $interval(function() {
		
		
		var current = moment();
		
		if(current.minutes()%minExe==0 && current.seconds()==5){
			delayedExe();
		}
		
	}, 1000);
	
	vm.getConfig();
	
	
	
}]);

