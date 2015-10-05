(function() {
	console.log("fdsf");
	$(".menu-popup").on("mouseover", "li", function(e) {
		$(this).children(".sub-menu").addClass("active");
	});

	$(".menu-popup").on("mouseleave", "li", function(e) {
		$(".sub-menu.active", this).removeClass("active");
	});


})();