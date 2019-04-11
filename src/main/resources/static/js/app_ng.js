var cperfModule = angular.module("Cperf", []);
cperfModule.controller("CperfCtrl", function($scope, $http){
	$scope.loged = null;
	$scope.getLoged = function(){
		$http({
		url : '/getLoged/',
		method : 'get',
		}).then(function(response) {
			$scope.loged = response.data;
		});
	};
});
cperfModule.controller("AdminCtrl", function($scope, $http, $timeout) {
	$scope.page = 0;
	$scope.size = 7;
	$scope.users = null;
	$scope.totalPage = 0;
	$scope.pages = [];
	$scope.isSadmin = false;
	$scope.orderField = "firstname";
	$scope.orderFieldReverse ="-";
	
	$scope.getUsers = function() {
		$http({
			url : '/Admin/getUsersJson/',
			method : 'get',
			params : {
				page : $scope.page,
				size : $scope.size,
				searchKey : $scope.searchField
			}
		}).then(function(response) {
			$scope.users = response.data;
			if (response.data != null && response.data.content.length > 0) {
				$scope.pages = [];
				for (var i = 0; i < response.data.totalPages; i++)
					$scope.pages[i] = i;
			}
		});
	};

	$scope.changePage = function($event) {
		var element = angular.element($event.target)
		var selectedPageIndex = element[0].attributes['data-pageIndex'].value;
		if ($scope.page != selectedPageIndex)
			$scope.page = selectedPageIndex;
		$scope.getUsers();
	};

	$scope.chageUserStatus = function($event) {
		var element = angular.element($event.target)
		console.log(element);
		var userId = element[0].attributes['data-userId'].value;
		var status = element[0].attributes['data-status'].value;
		$http({
			url : '/Admin/User/Statut',
			method : 'get',
			params : {
				userId : userId,
				status : status
			}
		}).then(function(response) {
			if (response.data == true)
				$scope.getUsers();
		});
	};
	$scope.reoplaceWithNullOrBlank = function(current_value,repalce_value){
		if(current_value == null || current_value == "")
			return repalce_value;
		return current_value;
	};
});

// organigramme controller 

cperfModule.controller("OrganigrammeCtrl", function($scope, $http, $timeout) {
	$scope.page = 0;
	$scope.size = 10;
	$scope.users = null;
	$scope.totalPage = 0;
	$scope.pages = [];
	$scope.isSadmin = false;
	$scope.orderField = "firstname";
	$scope.orderFieldReverse ="-";
	
	$scope.getUsers = function() {
		$http({
			url : '/Organigramme/OthersJson/',
			method : 'get',
			params : {
				page : $scope.page,
				size : $scope.size,
				searchKey : $scope.searchField
			}
		}).then(function(response) {
			$scope.users = response.data;
			if (response.data != null && response.data.content.length > 0) {
				$scope.pages = [];
				for (var i = 0; i < response.data.totalPages; i++)
					$scope.pages[i] = i;
			}
		});
	};

	$scope.changePage = function($event) {
		var element = angular.element($event.target)
		var selectedPageIndex = element[0].attributes['data-pageIndex'].value;
		if ($scope.page != selectedPageIndex)
			$scope.page = selectedPageIndex;
		$scope.getUsers();
	};
	$scope.addInDiagramme = function($event){
		var element = angular.element($event.target);
		var userId = element[0].attributes['data-userId'].value;
		$http({
			url : '/Organigramme/ChangeOrgValue/',
			method : 'get',
			params : {uid : userId, stat: true}
		}).then(function(response) {
			$scope.getUsers();
		});
	};
	$scope.reoplaceWithNullOrBlank = function(current_value,repalce_value){
		if(current_value == null || current_value == "")
			return repalce_value;
		return current_value;
	};
});
