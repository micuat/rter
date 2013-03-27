angular.module('rter', [
	'ui.bootstrap',          //Tabs
	'alerts',                //Main alert box
	'taxonomy',              //Taxonomy for tag-cloud
	'termview',              //term-view directives and TermViewRemote
	'http-auth-interceptor', //401 catcher
	'auth'                   //Login system
])

.controller('RterCtrl', function($scope, LoginDialog) {
	$scope.loginDialogOpen = false;
	$scope.$on('event:auth-loginRequired', function() {
		if(!$scope.loginDialogOpen) {
			$scope.loginDialogOpen = true;
			LoginDialog.open().then(function() {
				$scope.loginDialogOpen = false;
			});
		}
	});
})

.controller('TabsCtrl', function($scope, TermViewRemote) {
	$scope.termViews = TermViewRemote.termViews;
	TermViewRemote.addTermView({Term: ""});
})

.controller('TagCloudCtrl', function($scope, TermViewRemote, TaxonomyResource) {
	$scope.terms = TaxonomyResource.query(function() {
		$scope.countMax = 0;

		angular.forEach($scope.terms, function(val) {
			if($scope.countMax < val.Count) $scope.countMax = val.Count;
		});
	}); //TODO: Make me dynamic

	$scope.addTermView = function(term) {
		TermViewRemote.addTermView(term);
	};

	$scope.termFontSize = function(term) {
		return map(term.Count, 1, $scope.countMax, 12, 60);
	};
})

.directive('tagCloud', function(ItemCache) {
	return {
		restrict: 'E',
		scope: {},
		templateUrl: '/template/tag-cloud.html',
		controller: 'TagCloudCtrl',
		link: function(scope, element, attrs) {

		}
	};
});