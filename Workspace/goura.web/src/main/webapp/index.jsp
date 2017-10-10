<!doctype html>
<html ng-app="MainApp" ng-controller="MainController">
<head>
	<link rel="icon" href="app/images/planner.png" type="image/png">
	<!-- Start Style Sheets -->
	<link rel="stylesheet" href="app/css/lib/bootstrap.min.css" >
	<link rel="stylesheet" href="app/css/lib/angular-growl.min.css">
	<!-- Application Styles -->
	<link rel="stylesheet" href="app/css/theme.css" >
	<link rel="stylesheet" href="app/css/style.css" >
	<!-- End Style Sheets -->
	
	<!-- Start JavaScipts -->
	<script type="text/javascript" src="app/js/lib/Class.js"></script>
	<script type="text/javascript" src="app/js/lib/jquery-3.2.1.min.js"></script>
	<script type="text/javascript" src="app/js/lib/bootstrap.min.js"></script>
	<script type="text/javascript" src="app/js/lib/moment.min.js"></script>
	<script type="text/javascript" src="app/js/lib/go.js"></script>	
	<script type="text/javascript" src="app/js/lib/jquery.fileDownload.js"></script>
	<script type="text/javascript" src="app/js/lib/commonFns.js"></script>	
	
	
	<script type="text/javascript" src="app/js/lib/angular.js"></script>
	<script type="text/javascript" src="app/js/lib/ui-bootstrap-tpls-2.5.0.min.js"></script>
    <script type="text/javascript" src="app/js/lib/angular-growl.min.js"></script>
    
	<script type="text/javascript" src="app/js/app.module.js"></script>
	<script type="text/javascript" src="app/js/app.config.js"></script>
	
	<script type="text/javascript" src="app/js/services/service.flowchart.js"></script>
	
	<script type="text/javascript" src="app/js/controllers/controller.main.js"></script>
		<script type="text/javascript" src="app/js/controllers/controller.console.js"></script>
	
	<!-- End JavaScipts -->
</head>

<body class="main-container">

		<!-- HEADER -->
		<header id="header" class="block" ng-include src="'app/templates/header.html'">
		</header>
		
		<div class="block" style="vertical-align: top;border: solid 1px black; height: 100px;">
			<div id="flowchartControlsDiv" ></div>
		</div>
			
		<div style="vertical-align: top;border: solid 1px black; height: 400px;">
		     <div id="flowchartDiagramDiv" ng-init="initflowChart('flowchartDiagramDiv', 'flowchartControlsDiv')"></div>
		</div>
		 <div growl></div>
</body>
</html>
