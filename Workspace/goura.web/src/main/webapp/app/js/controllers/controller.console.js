var ConsoleController = MainApp.controller("ConsoleController", 
		ConsoleControllerFn
);

function ConsoleControllerFn($scope, $rootScope, $http,  $window, $timeout,  $location,
		$uibModalInstance, FlowChartService, growl){
	
	  console.log('Inside open Console controller');
	  $scope.consoleOutput = "";
	  readlogs();
	  
	  $scope.ok = function(testcase){
		  $uibModalInstance.close(testcase);
	  };
	  
	  $scope.cancel = function(){
		  $uibModalInstance.dismiss('cancel');
	  };	  
	  
	  $scope.startNode = function startNode(){
		  sendMessage({command : "start"});
	  };
	  
	  $scope.stopNode = function startNode(){
		  sendMessage({command : "stop"});
	  };
	  
	  
	  $scope.send = function send(){
		  var command = $scope.command;
		  if(command.indexOf("{") > -1){
			  command = JSON.parse(command);
		  }
		  sendMessage(command);
	  }
	  
	  function sendMessage(message){		  
		  $rootScope.sendRunnerCommand(message, recevieMessage);
	  }
	  
	  function recevieMessage(message){		
		  var con =  $scope.consoleOutput;
		  con = con + moment ().format("HH:mm");
		  con = con + ">>";
		  con = con + JSON.stringify(message.data);
		  con = con + "\n";
		  $scope.consoleOutput = con;
		  $scope.$apply();
	  }
	  
	  function readlogs(){
		  $http.post('api/GetLogs',{}).then(function success(response){
			  if(response.data.length > 0){
				  var con =  $scope.consoleOutput;
				  response.data.forEach(function(i){
					  con = con + moment ().format("HH:mm");
					  con = con + ">>";
					  con = con + JSON.stringify(i);
					  con = con + "\n";
				  });
				  $scope.consoleOutput = con;
			  }
		  }, function error(){});
	  }
}