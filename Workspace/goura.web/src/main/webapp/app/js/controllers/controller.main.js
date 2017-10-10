var MainController = MainApp.controller("MainController", 
		MainControllerFn
);


function MainControllerFn($scope, $rootScope, $http,  $window, $timeout,  $location,
		 $uibModal, FlowChartService, growl){
	$rootScope.height = $window.innerHeight;
	
	console.log('Inside MainController');	
	checkForHub(function(data){
		if(data.started){			
			$rootScope.hubStarted  = true;	
		}
		else{
			$rootScope.hubStarted  = false;	
		}
	});
	
	angular.element($window).bind('error', function(error){
		$rootScope.lastError = error;
    });
	
	angular.element($window).bind('sendRunnerCommand', function(error){
		$window.postMessage({ type: "GOURATESTRUNNERSEND", text: JSON.stringify($rootScope.runnerCommand) }, "*");
    });	
	
	$window.addEventListener("message", function(event) {
	    if (event.source != window)
	    return;

	    if (event.data.type && (event.data.type == "GOURATESTRUNNERECEIVE")) {
	    	$rootScope.runnerResponse = event.data.text;
	    	if($rootScope.runnerCallback){
	    		$rootScope.runnerCallback(event.data.text);
	    	}
	    }
	}, false);

	//$rootScope.runnerCommand = 'Testign Angularjs way';
	
	
	$rootScope.sendRunnerCommand = function(data, callback){
		$rootScope.runnerCommand = data;
		$rootScope.runnerCallback = callback;
		$($window).trigger("sendRunnerCommand");
	}
	
	angular.element($window).bind('resize', function(){
		$rootScope.width = $window.innerWidth;
        $rootScope.height = $window.innerHeight;
        
        $('#flowchartDiagramDiv').height($rootScope.height - $('#header').height() - $('#flowchartControlsDiv').height() - 20);
        
    });
	
	
	$scope.initflowChart = function(flowchartId, PaletteID){
		$('#flowchartDiagramDiv').height($rootScope.height - $('#header').height() - $('#flowchartControlsDiv').height() - 20);
		FlowChartService.InitDiagram(flowchartId);
		FlowChartService.InitPalette(PaletteID);
	}
	
	$scope.saveFlowChart = function(){		
		try{
			downloadFile('api/SaveTestCase', {testCaseModel : FlowChartService.SaveChart()}, function(error){
				if(error && error.trim() != ''){
					growl.error(error.trim(),{title: 'Error!', position: 'bottom-right'});
				}
			});
		}
		catch(e){
			console.log(e);
		}
	}
	
	$scope.newFlowChart = function(){
		FlowChartService.NewChart();
	}
	
	$scope.viewTestCases = function(){
		 var modalInstance = 	$uibModal.open({
		      animation: true,
		      ariaLabelledBy: 'modal-title-bottom',
		      ariaDescribedBy: 'modal-body-bottom',
		      templateUrl: 'app/templates/testcases-list.html',
//		      size: 'sm',
		      controller: function($scope, $uibModalInstance) {
		       
		    	  console.log('Inside Testcase List controller');
		    	  
		    	  $scope.ok = function(testcase){
		    		  $uibModalInstance.close(testcase);
		    	  };
		    	  
		    	  $scope.cancel = function(){
		    		  $uibModalInstance.dismiss('cancel');
		    	  };
		    	  
		    	  ;
		    	  getTestcases(function(list){
		    		  
		    		  $scope.list = list;
		    	  });
		    	  
		      }
		    });
		 
		 modalInstance.result.then(function (selectedItem) {
			 loadTestcase(selectedItem, function(testcaseJson){
				 FlowChartService.LoadChart(testcaseJson);
			 });
		    }, function () {
		      
		    });
	}
	
	$scope.loadFlowChart = function(){
		$('#uploadChart').click();
	}
	
	$scope.runFlowChart = function(){
		var testCase = FlowChartService.SaveChart();
		$http.post('api/RunTestCase', testCase).then(function success(response){
			
			if(response.data){
				FlowChartService.LoadChart(response.data);
			}			
			
		}, function error(){
			
		});
	}
	
	$scope.startStopHub = function startStopHub(){
		
		if(!$rootScope.hubStarted){
			$http.post('api/StartHub', {}).then(function success(response){
				$rootScope.hubStarted  = true;	
			}, function error(){
				
			});
		}
		else{
			$http.post('api/StopHub', {}).then(function success(response){
				$rootScope.hubStarted  = false;	
			}, function error(){
				
			});
		}
	}
	
	$scope.openConsole = function openConsole(){
		
		 var modalInstance = 	$uibModal.open({
		      animation: true,
		      ariaLabelledBy: 'modal-title-bottom',
		      ariaDescribedBy: 'modal-body-bottom',
		      templateUrl: 'app/templates/open-console.html',
		      controller: "ConsoleController"
		    });
		 
		 modalInstance.result.then(function ok(result){}, function cancle(){});
	}
	
	$('#uploadChart').change(function(){
		uploadFile('api/UploadRead', $('#uploadChart')[0], function(data){
			FlowChartService.LoadChart(data);
		});
	});
	
	function loadTestcase(testcase, callback){
		$http.post('api/GetTestcase', {testcase : testcase}).then(function success(response){
			if(callback){
				callback(response.data);
			}
		}, function error(){
			
		});
	}
	
	function getTestcases(callback){
		$http.post('api/GetTestcases', {}).then(function success(response){
			if(callback){
				callback(response.data);
			}
		}, function error(){
			
		});
	}
	
	function checkForHub(callback){
		$http.post('api/CheckForHub', {}).then(function success(response){
			
			if(callback){
				callback(response.data);
			}
			
		}, function error(){
			
		});
		
	}
	
}