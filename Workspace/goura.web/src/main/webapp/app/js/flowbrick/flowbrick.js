MainApp.directive("flowBrick", function() {
    
	var directive = {};	
	directive.restrict = 'E';
	
	directive.template = "<canvas id='canvas' resize></canvas>";
	directive.scope = {
              			flowchart : "=flowchart"
            	  };
	
	directive.canvas = {};
	directive.items = {
			TotalItems : 0,
			TotalRect : 0,
			TotalCircle : 0,
			Collections : {},
			CollectionKeys : {}
	};
	
	
	directive.compile = function(element, attributes) {
        // do one-time configuration of element.

		directive.canvas = new fabric.Canvas('canvas');
		directive.AddRect(100, 50);		
		directive.AddRect(200, 200);
		
		
        var linkFunction = function($scope, element, atttributes) {
            // bind element to data in $scope
        	directive.canvas.renderAll();
        }

        return linkFunction;
    }
	
	
	// create a rectangle object
	directive.AddRect = function(x, y){
		
		var rect = new fabric.Rect({
		  left: x,
		  top: y,
		  fill: 'red',
		  width: 50,
		  angle: 45,
		  height: 50
		});
		
		if(!directive.items.TotalRect){
			directive.items.TotalRect = 1;
		}
		rect.id = "Rectangle" + (directive.items.TotalRect + 1);
		
		
		directive.items.Collections[rect.id] = rect;
	}
	
	
	return directive;
});