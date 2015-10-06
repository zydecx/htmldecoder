(function() {
	$(".menu-popup").on("mouseover", "li", function(e) {
		$(this).children(".sub-menu").addClass("active");
	});

	$(".menu-popup").on("mouseleave", "li", function(e) {
		$(".sub-menu.active", this).removeClass("active");
	});

	$(".menu-slide").on("click", "li .menu", function(e) {
		$(this).closest("li").children(".sub-menu").toggleClass("active");
		$(this).toggleClass("active");
	});

	$(".main-menu li").each(function() {
		var $_childUl = $(this).children(".sub-menu");
		if ($_childUl.length > 0 && $_childUl.children("li").length > 0) {
			$(this).children(".menu-wrapper").find(".menu.hide").removeClass("hide");
		}
	});

})();