(function() {
	var MODULE = angular.module("enRoute$addressbook", ['ngResource', 'ngRoute'] );
	
	MODULE.config( function($routeProvider) {
		$routeProvider.when('/testdata', { templateUrl: '/osgi.enroute.examples.jdbc.addressbook/main/htm/testdata.htm', controller: testdataController });
		$routeProvider.when('/addressbook', { templateUrl: '/osgi.enroute.examples.jdbc.addressbook/main/htm/people.htm', controller: listController });
		$routeProvider.when('/addressbook/:name', { templateUrl: '/osgi.enroute.examples.jdbc.addressbook/main/htm/person.htm', controller: detailsController});
	});

	MODULE.factory('Person',['$resource',
	    function($resource){
			return $resource('/rest/person/:name');
		}                  
	]);

	function listController($scope, $routeParams, $anchorScroll, $location, Person) {
		$scope.list = Person.query();
		$scope.select = function(id) {
			$location.url('/addressbook/'+id);
		}
	}
	
	function detailsController($scope,$routeParams,$location,Person) {
		if ( $routeParams.name >= 0 ) {
			$scope.person = Person.get($routeParams);
		} else {
			$scope.person = new Person();
			$scope.person.addresses = [];
		}
		$scope.submit = function() {
			$scope.person.$save(function(data) {
				$location.url('/addressbook/'+$scope.person.personId);
			},function(data) {
				$scope.alerts.push('Failed ' + data );
			});
		}
		$scope.next =  {};
	}
	
	function testdataController($scope, $location, $http, Person) {
		$scope.testdata = function() {
			$http.post("/rest/testdata", {} ).then(
				function() {
					$location.url('/person');
				}
			);
		}
	}
	
})();