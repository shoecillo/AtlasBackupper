
var arkApp = angular.module('atlasApp', []);

arkApp.controller("atlasCtrl",["$scope","$http","$interval","$sce",function($scope,$http,$interval,$sce)
{
	var vm = $scope;
	var nextUpdate="";
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
			console.log(cron);
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
		nextUpdate = moment().add(1,"m");
		vm.timer = nextUpdate.format("HH:mm:ss");
		vm.getBackups();
	};
	
	vm.getDate = function(d){
		var t = moment(d.timestamp).format("YYYY-MM-DD HH:mm");
		return t;
	};
	
	var idClock = $interval(function(){
		vm.todayDate =moment().format("YYYY-MM-DD");
		vm.clock =moment().format("HH:mm:ss");
		
		var copyDate = moment(nextUpdate);
		if(copyDate.subtract(3,"seconds").isSameOrAfter(moment())){
			vm.showLoader = true;
		}else{
			vm.showLoader = false;
		}

	},1000);
	
	
	var idList =  $interval(function() {
		delayedExe();
	}, 60*1000);
	
	
	delayedExe();
	vm.getConfig();
	
}]);

