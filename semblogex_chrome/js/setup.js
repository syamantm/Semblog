$(document).ready(function(){
	
	// Accoridon
	$("#accordion").accordion({
		autoHeight: false   // This is needed for the extensions to work properly in Chrome
	});
	
	// Date picker
	$("#datepicker").datepicker({
		showButtonPanel: true
	});
	
	// Tabs
	$("#tabs").tabs({		
		select:  function(event, ui) {  
			// Objects available in the function context:
	    		//ui.tab    - anchor element of the selected (clicked) tab
	    		//ui.panel   - element, that contains the selected/clicked tab contents
	    		//ui.index   - zero-based index of the selected (clicked) tab
	    		if(ui.index == 0){
				// index 0 - related posts 			
	     		}
	    		else if(ui.panel.id == "similarPosts"){
				// index 1 - similar posts
				retrieveSimilarPosts();
	     		}
			else if(ui.panel.id == "otherPosts"){
				// index 1 - similar posts
				retrieveSimilarAuthorPosts();
	     		}
 		}
	});		
});






	    
