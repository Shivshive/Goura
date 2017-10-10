MainApp.service('FlowChartService', function($http, $uibModal) {
	var service = {};
	
	service.InitDiagram = InitDiagram;
	service.InitPalette = InitPalette;
	service.SaveChart = SaveChart;
	service.NewChart = NewChart;
	service.LoadChart =  LoadChart;
	
	
	var $ = go.GraphObject.make;
	
	function InitDiagram(myDiagramDiv){
		
		jQuery('#' + myDiagramDiv).height('100%');
		
		myDiagram =
		      $(go.Diagram, myDiagramDiv,  // must name or refer to the DIV HTML element
		        {
		          initialContentAlignment: go.Spot.Center,
		          allowDrop: true,  // must be true to accept drops from the Palette
		          "LinkDrawn": showLinkLabel,  // this DiagramEvent listener is defined below
		          "LinkRelinked": showLinkLabel,
		          "animationManager.duration": 800, // slightly longer than default (600ms) animation
		          "undoManager.isEnabled": true  // enable undo & redo
		        });
		
		
		// define the Node templates for regular nodes

	    var lightText = 'whitesmoke';

	    myDiagram.nodeTemplateMap.add("",  // the default category
	      $(go.Node, "Spot", nodeStyle(true),
	        // the main object is a Panel that surrounds a TextBlock with a rectangular Shape
	        $(go.Panel, "Auto",
	        
	          $(go.Shape, "Rectangle",
	            { fill: "#00A9C9", stroke: null },
	            new go.Binding("figure", "figure")),
	            $(go.Panel, "Vertical",
	            		$(go.TextBlock,
	            	            {
	            	              font: "bold 11pt Helvetica, Arial, sans-serif",
	            	              stroke: lightText,
	            	              margin: 8,
	            	              maxSize: new go.Size(160, NaN),
	            	              wrap: go.TextBlock.WrapFit,
	            	              editable: true
	            	            },
	            	            new go.Binding("text").makeTwoWay()),
	            	            
	            	            $("PanelExpanderButton", "PREVIEW",
		            	          { 
	            	            	row: 1, 
	            	            	column: 1, 
	            	            	alignment: go.Spot.Bottom, 
	            	            	visible: false
		            	          },
		            	          new go.Binding("visible", "preview", function(preview){return preview?true:false;})),
		            	        $(go.Panel, "Vertical",  
		            	        		{ 
		            	        		name: "PREVIEW",
		            	        		visible: false
		            	        		},
		            	        		$("Button",
		            	        				{
			            	        				click:function(e,obj){	
			            	        	                var fromData = obj.part.data;
			            	        					
			            	        					viewScreenShot(fromData.preview);
			            	        				}
		            	        				},
		            	        				$(go.Picture,
		            	        						{
		            	        							name: "Picture",
		            	        							desiredSize: new go.Size(39, 50),
		            	        							margin: new go.Margin(6, 8, 6, 10),
		            	        						},
		            	        						new go.Binding("source", "preview", findScreenShot)
		            	        				)
		            	        		)
	            	    		 
	            	            )
	            	        
	            	           
	            	      
	            )
	        ),
	        // four named ports, one on each side:
	        makePort("T", go.Spot.Top, false, true),
	        makePort("L", go.Spot.Left, true, true),
	        makePort("R", go.Spot.Right, true, true),
	        makePort("B", go.Spot.Bottom, true, false)
	      ));

	    myDiagram.nodeTemplateMap.add("Start",
	      $(go.Node, "Spot", nodeStyle(),
	        $(go.Panel, "Auto",
	          $(go.Shape, "Circle",
	            { minSize: new go.Size(40, 40), fill: "#79C900", stroke: null }),
	          $(go.TextBlock, "Start",
	            { font: "bold 11pt Helvetica, Arial, sans-serif", stroke: lightText },
	            new go.Binding("text"))
	        ),
	        // three named ports, one on each side except the top, all output only:
	        makePort("L", go.Spot.Left, true, false),
	        makePort("R", go.Spot.Right, true, false),
	        makePort("B", go.Spot.Bottom, true, false)
	      ));

	    myDiagram.nodeTemplateMap.add("End",
	      $(go.Node, "Spot", nodeStyle(),
	        $(go.Panel, "Auto",
	          $(go.Shape, "Circle",
	            { minSize: new go.Size(40, 40), fill: "#DC3C00", stroke: null }),
	          $(go.TextBlock, "End",
	            { font: "bold 11pt Helvetica, Arial, sans-serif", stroke: lightText },
	            new go.Binding("text"))
	        ),
	        // three named ports, one on each side except the bottom, all input only:
	        makePort("T", go.Spot.Top, false, true),
	        makePort("L", go.Spot.Left, false, true),
	        makePort("R", go.Spot.Right, false, true)
	      ));

	    myDiagram.nodeTemplateMap.add("Comment",
	      $(go.Node, "Auto", nodeStyle(true),
	        $(go.Shape, "File",
	          { fill: "#EFFAB4", stroke: null }),
	        $(go.TextBlock,
	          {
	            margin: 5,
	            maxSize: new go.Size(200, NaN),
	            wrap: go.TextBlock.WrapFit,
	            textAlign: "center",
	            editable: true,
	            font: "bold 12pt Helvetica, Arial, sans-serif",
	            stroke: '#454545'
	          },
	          new go.Binding("text").makeTwoWay())
	        // no ports, because no links are allowed to connect with a comment
	      ));

		
//	    // unlike the normal selection Adornment, this one includes a Button
//	    myDiagram.nodeTemplate.selectionAdornmentTemplate =
//	      $(go.Adornment, "Spot",
//	        $(go.Panel, "Auto",
//	          $(go.Shape, { fill: null, stroke: "blue", strokeWidth: 2 }),
//	          $(go.Placeholder)  // a Placeholder sizes itself to the selected Node
//	        ),
//	        // the button to delete node, at the top-right corner
//	        $("Button",
//	          {
//	            alignment: go.Spot.TopRight,
//	            click: function(e,obj){
//	            	debugger;
//	            	
//	            	var adornment = obj.part;
//	                var diagram = e.diagram;
//	                // get the node data for which the user clicked the button
//	                var fromNode = adornment.adornedPart;
//	                var fromData = fromNode.data;
//	            	
//	                fromData.showProperties=!fromData.showProperties;
//	            }  // this function is defined below
//	          },
//	          $(go.Shape, "Diamond", 
//	        		  { width: 6, height: 6 },
//	        		  new go.Binding("figure", "showProperties", function(is){ return is ? "Triangle" : "Diamond"}))
//	        ) // end button
//	      ); // end Adornment  Triangle Diamond
	    
	    
	 // replace the default Link template in the linkTemplateMap
	    myDiagram.linkTemplate =
	      $(go.Link,  // the whole link panel
	        {
	          routing: go.Link.AvoidsNodes,
	          curve: go.Link.JumpOver,
	          corner: 5, toShortLength: 4,
	          relinkableFrom: true,
	          relinkableTo: true,
	          reshapable: true,
	          resegmentable: true,
	          // mouse-overs subtly highlight links:
	          mouseEnter: function(e, link) { link.findObject("HIGHLIGHT").stroke = "rgba(30,144,255,0.2)"; },
	          mouseLeave: function(e, link) { link.findObject("HIGHLIGHT").stroke = "transparent"; }
	        },
	        new go.Binding("points").makeTwoWay(),
	        $(go.Shape,  // the highlight shape, normally transparent
	          { isPanelMain: true, strokeWidth: 8, stroke: "transparent", name: "HIGHLIGHT" }),
	        $(go.Shape,  // the link path shape
	          { isPanelMain: true, stroke: "gray", strokeWidth: 2 }),
	        $(go.Shape,  // the arrowhead
	          { toArrow: "standard", stroke: null, fill: "gray"}),
	        $(go.Panel, "Auto",  // the link label, normally not visible
	          { visible: false, name: "LABEL", segmentIndex: 2, segmentFraction: 0.5},
	          new go.Binding("visible", "visible").makeTwoWay(),
	          $(go.Shape, "RoundedRectangle",  // the label shape
	            { fill: "#F8F8F8", stroke: null }),
	          $(go.TextBlock, "Yes",  // the label
	            {
	              textAlign: "center",
	              font: "10pt helvetica, arial, sans-serif",
	              stroke: "#333333",
	              editable: true
	            },
	            new go.Binding("text").makeTwoWay())
	        )
	      );
		
	    // temporary links used by LinkingTool and RelinkingTool are also orthogonal:
	    myDiagram.toolManager.linkingTool.temporaryLink.routing = go.Link.Orthogonal;
	    myDiagram.toolManager.relinkingTool.temporaryLink.routing = go.Link.Orthogonal;
	    
	}
	
	function findScreenShot(key){
		
		return 'api/FindScreenShot?path='+key;
		
	}
	
	
	function viewScreenShot(preview){
		 var modalInstance = 	$uibModal.open({
		      animation: true,
		      ariaLabelledBy: 'modal-title-bottom',
		      ariaDescribedBy: 'modal-body-bottom',
		      templateUrl: 'app/templates/screenshot-viewer.html',
		      windowClass : 'screen-shot',
		      controller: function($scope, $uibModalInstance) {
		    	  $scope.path = preview;
		    	  $scope.cancel = function(){
		    		  $uibModalInstance.dismiss('cancel');
		    	  };
		      }
		    });
		 
		 modalInstance.result.then(function (selectedItem) {
			
		    }, function () {
		      
		    });
	}
	
	function InitPalette(PaletteID){
		 // initialize the Palette that is on the left side of the page
		jQuery('#' + PaletteID).height('100%');
	    myPalette =
	      $(go.Palette, PaletteID,  // must name or refer to the DIV HTML element
	        {
	          "animationManager.duration": 800, // slightly longer than default (600ms) animation
	          nodeTemplateMap: myDiagram.nodeTemplateMap,  // share the templates used by myDiagram
	          model: new go.GraphLinksModel([  // specify the contents of the Palette
	            { category: "Start", text: "Start" },
	            { category:"Step", text: "Get", figure: "RoundedRectangle" , showProperties:true },
	            { category:"Step", text: "Step"  },
	            { category:"Step", text: "???", figure: "Diamond" },
	            { category: "End", text: "End" },
	            { category: "Comment", text: "Comment" }
	          ])
	        });
	}
	
	// helper definitions for node templates

    function nodeStyle(resize) {
    	
    	
    var obj = {
    		locationSpot: go.Spot.Center,
    		mouseEnter: function (e, obj) { showPorts(obj.part, true); },
            mouseLeave: function (e, obj) { showPorts(obj.part, false); },
    		};    
    
    if(resize){
    	obj.resizable= true;
    	obj.layoutConditions= go.Part.LayoutStandard & ~go.Part.LayoutNodeSized;
    	obj.fromSpot= go.Spot.AllSides;
    	obj.toSpot= go.Spot.AllSides;
    }
    	
      return [
        // The Node.location comes from the "loc" property of the node data,
        // converted by the Point.parse static method.
        // If the Node.location is changed, it updates the "loc" property of the node data,
        // converting back using the Point.stringify static method.
        new go.Binding("location", "loc", go.Point.parse).makeTwoWay(go.Point.stringify),
        obj
      ];
    }

    // Define a function for creating a "port" that is normally transparent.
    // The "name" is used as the GraphObject.portId, the "spot" is used to control how links connect
    // and where the port is positioned on the node, and the boolean "output" and "input" arguments
    // control whether the user can draw links from or to the port.
    function makePort(name, spot, output, input) {
      // the port is basically just a small circle that has a white stroke when it is made visible
      return $(go.Shape, "Circle",
               {
                  fill: "transparent",
                  stroke: null,  // this is changed to "white" in the showPorts function
                  desiredSize: new go.Size(8, 8),
                  alignment: spot, alignmentFocus: spot,  // align the port on the main Shape
                  portId: name,  // declare this object to be a "port"
                  fromSpot: spot, toSpot: spot,  // declare where links may connect at this port
                  fromLinkable: output, toLinkable: input,  // declare whether the user may draw links to/from here
                  cursor: "pointer"  // show a different cursor to indicate potential link point
               });
    }

	
	
	 // Make link labels visible if coming out of a "conditional" node.
    // This listener is called by the "LinkDrawn" and "LinkRelinked" DiagramEvents.
    function showLinkLabel(e) {
      var label = e.subject.findObject("LABEL");
      if (label !== null) label.visible = (e.subject.fromNode.data.figure === "Diamond");
    }
	
    // Make all ports on a node visible when the mouse is over the node
    function showPorts(node, show) {
      var diagram = node.diagram;
      if (!diagram || diagram.isReadOnly || !diagram.allowLink) return;
      node.ports.each(function(port) {
          port.stroke = (show ? "white" : null);
        });
    }
    
 // The following code overrides GoJS focus to stop the browser from scrolling
    // the page when either the Diagram or Palette are clicked or dragged onto.

    function customFocus() {
      var x = window.scrollX || window.pageXOffset;
      var y = window.scrollY || window.pageYOffset;
      go.Diagram.prototype.doFocus.call(this);
      window.scrollTo(x, y);
    }
    
    
    function SaveChart(){
    	myDiagram.isModified = false;
    	return myDiagram.model.toJson();
    }
    
    function NewChart (){
    	
    	var newModel = {"class":"go.GraphLinksModel","linkFromPortIdProperty":"fromPort","linkToPortIdProperty":"toPort","nodeDataArray":[{"category":"Start","text":"Start","key":-1,"loc":"-12 83"},{"category":"Step","text":"Get=>http://www.google.com","figure":"RoundedRectangle","preview":"google","showProperties":true,"key":-2,"loc":"291 200"},{"category":"End","text":"End","key":-5,"loc":"603 365"},{"category":"Comment","text":"Name=>TestCase","key":-6,"loc":"599 -12"}],"linkDataArray":[{"from":-1,"to":-2,"fromPort":"R","toPort":"L","points":[13.209302325581397,83,23.209302325581397,83,106.47393928821373,83,106.47393928821373,200,189.73857625084605,200,199.73857625084605,200]},{"from":-2,"to":-5,"fromPort":"R","toPort":"L","points":[382.261423749154,200,392.261423749154,200,482.2062932699259,200,482.2062932699259,365,572.1511627906978,365,582.1511627906978,365]}]};

    	LoadChart(newModel);
    }

    function LoadChart(model){
    	myDiagram.model = go.Model.fromJson(model);
    }
    
    
	return service;	
});