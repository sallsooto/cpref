var cperfModule = angular.module("Cperf", [ '720kb.datepicker' ]);
cperfModule.value({
	datepicker : {
		months : [ 'j', 'f', 'm', 'a', 'ma', 'jn', 'jl', 'au', 'se', 'oc',
				'nv', 'dec' ]
	}
});
cperfModule.controller("CperfCtrl", function($scope, $http, $interval) {
	$scope.loged = null;
	$scope.getLoged = function() {
		$http({
			url : '/getLoged/',
			method : 'get',
		}).then(function(response) {
			$scope.loged = response.data;
		});
	};

	// notifications traitement
	$scope.seenMessages = [];
	$scope.noSeenMessages = [];
	$scope.seenNotifs = [];
	$scope.noSeenNotifs = [];
	$scope.getSeenNotifications = function() {
		$http({
			url : '/Notification/getUserSeenNotificationsJson',
			method : 'get',
			params : {}
		}).then(function(response) {
			$scope.seenNotifs = response.data;
			// console.log($scope.seenNotis);
		});
	};
	$scope.getNoSeenNotifications = function() {
		$http({
			url : '/Notification/getUserNoSeenNotificationsJson',
			method : 'get',
			params : {}
		}).then(function(response) {
			$scope.noSeenNotifs = response.data;
			// console.log($scope.noSeenNotifs);
		});
	};

	$scope.getSeenMessages = function() {
		$http({
			url : '/Notification/getUserSeenMessagesJson',
			method : 'get',
			params : {}
		}).then(function(response) {
			$scope.seenMessages = response.data;
			// console.log($scope.seenNotis);
		});
	};
	$scope.getNoSeenMessages = function() {
		$http({
			url : '/Notification/getUserNoSeenMessagesJson',
			method : 'get',
			params : {}
		}).then(function(response) {
			$scope.noSeenMessages = response.data;
			// console.log($scope.noSeenNotifs);
		});
	};
	$scope.getNotifications = function() {
		$scope.getSeenNotifications();
		$scope.getNoSeenNotifications();
		$scope.getSeenMessages();
		$scope.getNoSeenMessages();
	};

	$interval($scope.getNotifications, 30000);

	$scope.getNotifFaIconClass = function(notifType) {
		if (typeof notifType !== typeof undefined && notifType != null
				&& notifType.length > 0) {
			if (notifType.toLowerCase() == "info")
				return "fas fa-info bg-info ";
			else if (notifType.toLowerCase() == "alert")
				return "fas fa-exclamation-triangle bg-warning";
			else if (notifType.toLowerCase() == "success")
				return "fas fa-check-circle bg-success ";
			else if (notifType.toLowerCase() == "danger")
				return "fas fa-biohazard bg-danger ";
			else if (notifType.toLowerCase() == "message")
				return "fas fa-sms bg-secondary ";
			else
				return "fas fa-exclamation bg-primary ";
		} else {
			return "fas fa-exclamation bg-primary ";
		}
	};

	$scope.notifs = null;
	$scope.notePage = 0;
	$scope.noteSize = 3;
	$scope.totalNotePage = 0;
	$scope.notePages = [];
	$scope.orderNoteField = "storeAt";
	$scope.orderNoteFieldReverse = "-";
	$scope.noteType = "info";
	$scope.noteSeen = "true";
	$scope.getUserNotificationOrMessages = function() {
		$scope.booleanNoteSeen = $scope.noteSeen == "false" ? false : true
		$http({
			url : '/Notification/getUserSeenNotificationsOrMessagePageJson/',
			method : 'get',
			params : {
				page : $scope.notePage,
				size : $scope.noteSize,
				type : $scope.noteType,
				seen : $scope.booleanNoteSeen
			}
		}).then(function(response) {
			$scope.notifs = response.data;
			if (response.data != null && response.data.content.length > 0) {
				$scope.notePages = [];
				for (var i = 0; i < response.data.totalPages; i++)
					$scope.notePages[i] = i;
			}
		});
	};

	$scope.changePage = function($event) {
		var element = angular.element($event.target)
		var selectedPageIndex = element[0].attributes['data-pageIndex'].value;
		if ($scope.notePage != selectedPageIndex)
			$scope.notePage = selectedPageIndex;
		$scope.getUserNotificationOrMessages();
	};

	$scope.setDefaultNotifTypeOneMessage = function() {
		$scope.noteType = "message";
	};
	// end notifications traitements
});
cperfModule.controller("AdminCtrl", function($scope, $http, $timeout) {
	$scope.page = 0;
	$scope.size = 7;
	$scope.users = null;
	$scope.totalPage = 0;
	$scope.pages = [];
	$scope.isSadmin = false;
	$scope.orderField = "firstname";
	$scope.orderFieldReverse = "-";

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
	$scope.reoplaceWithNullOrBlank = function(current_value, repalce_value) {
		if (current_value == null || current_value == "")
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
	$scope.orderFieldReverse = "-";

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
	$scope.addInDiagramme = function($event) {
		var element = angular.element($event.target);
		var userId = element[0].attributes['data-userId'].value;
		$http({
			url : '/Organigramme/ChangeOrgValue/',
			method : 'get',
			params : {
				uid : userId,
				stat : true
			}
		}).then(function(response) {
			$scope.getUsers();
		});
	};
	$scope.reoplaceWithNullOrBlank = function(current_value, repalce_value) {
		if (current_value == null || current_value == "")
			return repalce_value;
		return current_value;
	};
});

// procedure controller
cperfModule.controller("ProcedureCtrl", function($scope, $http, $timeout) {
	$scope.page = 0;
	$scope.size = 7;
	$scope.procedures = null;
	$scope.totalPage = 0;
	$scope.pages = [];
	$scope.orderField = "storeAt";
	$scope.orderFieldReverse = "-";
	$scope.getProcedures = function() {

		$http({
			url : '/Procedure/getProceduresJson/',
			method : 'get',
			params : {
				page : $scope.page,
				size : $scope.size,
				name : $scope.searchField
			}
		}).then(function(response) {
			$scope.procedures = response.data;
			if (response.data != null && response.data.content.length > 0) {
				$scope.pages = [];
				for (var i = 0; i < response.data.totalPages; i++)
					$scope.pages[i] = i;
			}
		});
	};
	$scope.replaceWithNullOrBlank = function(current_value, repalce_value) {
		if (current_value == null || current_value == "")
			return repalce_value;
		return current_value;
	};

	$scope.changePage = function($event) {
		var element = angular.element($event.target)
		var selectedPageIndex = element[0].attributes['data-pageIndex'].value;
		if ($scope.page != selectedPageIndex)
			$scope.page = selectedPageIndex;
		$scope.getProcedures();
	};
});

// Tâches controller
cperfModule.controller("TaskCtrl", function($scope, $http, $timeout) {
	$scope.page = 0;
	$scope.size = 7;
	$scope.tasks = null;
	$scope.totalPage = 0;
	$scope.pages = [];
	$scope.orderField = "id";
	$scope.status = "all";
	$scope.orderFieldReverse = "-";
	$scope.processLunshed = "true";
	$scope.loged = null;
	$scope.getTasks = function() {
		var processLunshed = $scope.processLunshed == "true" ? true : false;
		$scope.status = !processLunshed ? "all" : $scope.status;
		$http({
			url : '/Task/userTasksJson/',
			method : 'get',
			params : {
				page : $scope.page,
				size : $scope.size,
				name : $scope.searchField,
				status : $scope.status,
				processLunshed : processLunshed
			}
		}).then(function(response) {
			$scope.tasks = response.data.tasks;
			$scope.pages = [];
			$scope.pages[0] = 0;
			for (var i = 0; i < response.data.totalPages; i++)
				$scope.pages[i] = i;
			$scope.loged = response.data.loged;
		});
	};
	$scope.replaceWithNullOrBlank = function(current_value, repalce_value) {
		if (current_value == null || current_value == "")
			return repalce_value;
		return current_value;
	};

	$scope.changePage = function($event) {
		var element = angular.element($event.target)
		var selectedPageIndex = element[0].attributes['data-pageIndex'].value;
		if ($scope.page != selectedPageIndex)
			$scope.page = selectedPageIndex;
		$scope.getTasks();
	};

	$scope.changeTaskStatus = function($event) {
		var element = angular.element($event.target);
		var taskId = element[0].attributes['data-taskId'].value;
		var newStatus = element[0].attributes['data-taskNewStatus'].value;
		console.log(taskId + " " + newStatus);
		$http({
			url : '/Task/' + taskId + '/Status/',
			method : 'get',
			params : {
				status : newStatus
			}
		}).then(function(response) {
			console.log(response.data);
			if (response.data != null && response.data)
				$scope.getTasks();
		});
	};
	
	$scope.changeStatusValid = function(taskId,valid){
		$http({
			url : '/Task/changeStatusValid',
			method : 'get',
			params : {
				tid : taskId,
				valid : valid
			}
		}).then(function(response) {
			if (response.data != null && response.data)
				$scope.getTasks();
		});
	};

});

// impact controller
cperfModule
		.controller(
				"ImpactCtrl",
				function($scope, $http, $timeout) {
					var currentDate = new Date();
					var lastDate = new Date(currentDate.getTime()
							- (30 * 24 * 60 * 60 * 1000));
					var currentDay = (currentDate.getDate() < 10) ? "0"
							+ (currentDate.getDate()) : currentDate.getDate();
					var currentMonth = (currentDate.getMonth() < 10) ? "0"
							+ (currentDate.getMonth() + 1) : currentDate
							.getMonth() + 1;
					var lastDay = (lastDate.getDate() < 10) ? "0"
							+ (lastDate.getDate()) : lastDate.getDate();
					var lastMonth = (lastDate.getMonth() < 10) ? "0"
							+ (lastDate.getMonth() + 1)
							: lastDate.getMonth() + 1;
					$scope.page = 0;
					$scope.size = 5;
					$scope.process = null;
					$scope.totalPage = 0;
					$scope.pages = [];
					$scope.startDate = lastMonth + "/" + lastMonth + "/"
							+ lastDate.getFullYear();
					$scope.endDate = currentDay + "/" + currentMonth + "/"
							+ currentDate.getFullYear();
					$scope.status = "finished_expired";
					$scope.selectedProcess = null;
					$scope.selectedJustification = null;
					$scope.justifications = [];
					$scope.withEdit = false;
					$scope.cause = "finished_expired";
					$scope.tid = null;
					$scope.jid = null;
					$scope.content = null;
					$scope.errorMsg = null;
					$scope.successMsg = null;
					$scope.selectedTask = null;
					$scope.impact = null;
					$scope.impacts = [];
					$scope.getImpacts = function() {
						$http({
							url : '/Impact/getAllJson/',
							method : 'get'
						}).then(
								function(response) {
									if (response.data != null
											&& response.data.length > 0) {
										$scope.impacts = response.data;
										$scope.impact = $scope.impacts[0].slug;
									}
								});
					};
					$scope.getProcess = function() {
						$http({
							url : '/Impact/getHistoryJson/',
							method : 'get',
							params : {
								page : $scope.page,
								size : $scope.size,
								startDatePattern : $scope.startDate,
								endDatePattern : $scope.endDate,
								status : $scope.status
							}
						})
								.then(
										function(response) {
											console.log(response.data);
											$scope.process = response.data.content;
											$scope.loadJustifications();
											$scope.pages = [];
											$scope.pages[0] = 0;
											for (var i = 0; i < response.data.totalPages; i++)
												$scope.pages[i] = i;
											if ($scope.process.length > 0
													&& $scope.selectedJustification == null) {
												$scope.selectedJustification = $scope.justifications[0];
											}
										});
					};

					$scope.changePage = function($event) {
						var element = angular.element($event.target)
						var selectedPageIndex = element[0].attributes['data-pageIndex'].value;
						if ($scope.page != selectedPageIndex)
							$scope.page = selectedPageIndex;
						$scope.getProcess();
					};

					$scope.getProcessById = function(processId) {
						$http({
							url : '/Impact/findProcessByIdJson/',
							method : 'get',
							params : {
								pid : processId
							}
						}).then(function(response) {
							$scope.selectedProcess = response.data;
						});
					};

					$scope.loadJustifications = function() {
						if ($scope.selectedTask != null) {
							$scope.justifications = $scope.selectedTask.justifications;
						} else {
							console.log($scope.selectedProcess);
							if ($scope.selectedProcess != null)
								$scope.justifications = $scope.selectedProcess.justifications;
						}

						if ($scope.selectedJustification == null
								&& $scope.justifications.length > 0)
							$scope.selectedJustification = $scope.justifications[0];
					};

					$scope.getJustificationById = function(id) {
						$http({
							url : '/Impact/findJustificationByIdJson/',
							method : 'get',
							params : {
								jid : id
							}
						}).then(function(response) {
							$scope.selectedJustification = response.data;
						});
					};

					$scope.showModalEditJustificationFormManager = function(
							processId, taskId, withEdit, fillForm) {
						$scope.findTaskById(taskId);
						if (processId != null || $scope.selectedTask != null) {
							$scope.loadJustifications();
							$scope.getProcessById(processId);
							if ($scope.selectedProcess != null) {
								$scope.withEdit = withEdit;
								if ($scope.selectedTask != null)
									$scope.cause = ($scope.selectedTask.status == "canceled") ? "t_canceled"
											: $scope.selectedTask.status;
								else
									$scope.cause = $scope.status;
								if (fillForm == true)
									$scope.fillOrClearEditJustificationForm(
											$scope.selectedJustification,
											fillForm);
								else
									$scope.fillOrClearEditJustificationForm(
											null, fillForm);
								var justificationModal = angular
										.element('#modal_justification');
								justificationModal.modal('show');
							}
						}
					};

					$scope.closeModal = function() {
						// location.reload(false);
						var justificationModal = angular
								.element('#modal_justification');
						window.location = window.location.href;
					};

					$scope.showModalEditJustificationWithNewForm = function(
							$event) {
						var element = angular.element($event.target);
						var processId = element[0].attributes['data-processId'].value;
						var taskId = element[0].attributes['data-taskId'].value;
						$scope.showModalEditJustificationFormManager(processId,
								taskId, true, false);
					};
					$scope.showModalEditJustificationWithOutNewForm = function(
							$event) {
						var element = angular.element($event.target);
						var processId = element[0].attributes['data-processId'].value;
						var taskId = element[0].attributes['data-taskId'].value;
						$scope.showModalEditJustificationFormManager(processId,
								taskId, false, false);
					};
					$scope.showModalEditJustificationWithEditForm = function(
							$event) {
						var element = angular.element($event.target);
						var processId = element[0].attributes['data-processId'].value;
						var taskId = element[0].attributes['data-taskId'].value;
						$scope.showModalEditJustificationFormManager(processId,
								taskId, true, true);
					};
					$scope.fillOrClearEditJustificationForm = function(
							justification, fillForm) {
						if (justification != null) {
							$scope.jid = justification.id;
							$scope.impact = justification.impact.slug;
							$scope.cause = justification.cause;
							if (fillForm == true)
								$scope.content = justification.content;
							else
								$scope.content = null;
						} else {
							$scope.jid = null;
							$scope.content = null;
						}
					};

					$scope.editJustification = function() {
						if ($scope.cause != null && $scope.cause != ""
								&& $scope.impact != "" && $scope.impact != ""
								&& $scope.content != null
								&& $scope.content != "") {
							var justification = {
								id : $scope.jid,
								processId : $scope.selectedProcess.id,
								impact : $scope.impact,
								cause : $scope.cause,
								content : $scope.content,
								taskId : $scope.tid
							};

							$http({
								url : '/Impact/editJustificationJson/',
								method : 'get',
								params : {
									data : justification
								}
							})
									.then(
											function(response) {
												if (response.data != null) {
													$scope.errorMsg = null;
													$scope.selectedJustification = response.data;
													if ($scope.jid != null) {
														$scope
																.fillOrClearEditJustificationForm($scope.selectedJustification);
														$scope.successMsg = "Justification modifiée!";
													} else {
														$scope
																.fillOrClearEditJustificationForm(null);
														$scope.successMsg = "Justification enregistrée!";
													}
													$scope
															.getProcessById($scope.selectedProcess.id);
													$scope.getProcess();
												} else {
													$scope.successMsg = null;
													$scope.errorMsg = "Echèc de l'opération veuillez recommencer ";
												}
											});
						}
					};

					$scope.hideEditJustificationForm = function() {
						$scope.withEdit = false;
					};
					$scope.resetEditJustificationForm = function() {
						$scope.fillOrClearEditJustificationForm(null);
					};

					$scope.findTaskById = function(id) {
						if (id != null && id > 0) {
							$http({
								url : '/Task/getTaskByIdJson/',
								method : 'get',
								params : {
									tid : id
								}
							}).then(function(response) {
								if (response.data != null) {
									$scope.selectedTask = response.data;
									$scope.tid = response.data.id;
								}
							});
						} else {
							$scope.selectedTask = null;
						}
					};
					$scope.changeSeletctedJustification = function($event) {
						$event.preventDefault();
						var element = angular.element($event.target);
						var justitifactionIdexId = element[0].attributes['data-justificationIndex'].value;
						if ($scope.justifications[justitifactionIdexId] != null)
							$scope.selectedJustification = $scope.justifications[justitifactionIdexId];
					};
				});

// Objectif indicator controller
cperfModule
		.controller(
				"ObjectifIndicatorCtrl",
				function($scope, $http, $filter) {
					$scope.selectedUser = null;
					$scope.tibId = null;
					$scope.page = 0;
					$scope.size = 5;
					$scope.totalPages = 0;
					$scope.pages = [];
					$scope.objectifs = [];
					$scope.selectedIndicator = null;
					$scope.selectedDataCollector = null;
					$scope.collectorResultNumber = null;
					$scope.collectorResultText = null;
					$scope.errorMsg = null;
					$scope.successMsg = null;
					$scope.userFirstOrLastname = null;
					$scope.users = [];
					$scope.showOthersUsers = true;
					$scope.selectedTypeObjectifId = 0;
					$scope.typeObjectifs = [];
					$scope.sumPurcente = 0;
					$scope.globalSumPercente = 0;
					$scope.staticView = false;

					$scope.getObjectifs = function() {
						$scope.showOthersUsers = false;
						$scope.pages = [];
						$http({
							url : '/Performance/getObjectifByUserJson/',
							method : 'get',
							params : {
								uid : $scope.selectedUser != null ? $scope.selectedUser.id : null,
								tobid : $scope.tibId,
								page : $scope.page,
								size : $scope.size,
								tobid : $scope.selectedTypeObjectifId
							}
						}).then(function(response) {
							if (response.data != null) {
								$scope.objectifs = response.data.content;
								$scope.totalPages = response.data.totalPages;
								for(var i=0; i<$scope.totalPages; i++)
									$scope.pages[i]=i;
								$scope.selectedUser = response.data.user;
								if($scope.selectedUser != null){
									$scope.userFirstOrLastname = $scope.selectedUser.firstname + ' '+$scope.selectedUser.lastname;
								}
							}
						});
					};
					
					$scope.getUsers = function(){
						$http({
							url : '/Performance/getUsersJson/',
							method : 'get'
						}).then(function(response) {
							if (response.data != null) 
								$scope.users = response.data;
						});
					};
					
					$scope.searchUserByLastOrFirstname = function(){
						$scope.showOthersUsers = true;
						console.log($scope.userFirstOrLastname);
						$http({
							url : '/Performance/getUserByLastOrFirstnameJson/',
							method : 'get',
							params : {lastOrFirstname : $scope.userFirstOrLastname}
						}).then(function(response) {
							if (response.data != null){
								$scope.users = response.data;
							}
						});
					};
					$scope.getTypeObjectifs = function(){
						$http({
							url : '/Performance/getTypesObjectifsJson/',
							method : 'get'
						}).then(function(response) {
							if (response.data != null) 
								$scope.typeObjectifs = response.data;
						});
					};
					
					$scope.changeTypeObjectifs = function(){
						$scope.getObjectifs();
					};
					
					$scope.changePage = function($event) {
						var element = angular.element($event.target)
						var selectedPageIndex = element[0].attributes['data-pageIndex'].value;
						if ($scope.page != selectedPageIndex)
							$scope.page = selectedPageIndex;
						$scope.getObjectifs();
					};

					$scope.selectIndicator = function(id) {
						if (typeof id != typeof undefined
								&& $scope.objectifs.length > 0) {
							$scope.selectedIndicator = null;
							for (var i = 0; i < $scope.objectifs.length; i++) {
								if ($scope.objectifs[i].indicators.length > 0) {
									var indicators = $scope.objectifs[i].indicators;
									for (var j = 0; j < indicators.length; j++) {
										if (indicators[j].id == id) {
											$scope.selectedIndicator = indicators[j];
											break;
										}
									}
								}
							}
						}
					};

					$scope.selectDataCollector = function(dataCollectorId) {
						if (typeof dataCollectorId !== typeof undefined
								&& $scope.selectedIndicator != null
								&& $scope.selectedIndicator.dataCollectors.length > 0) {
							$scope.selectedDataCollector = null;
							var datas = $scope.selectedIndicator.dataCollectors;
							for (var i = 0; i < datas.length; i++) {
								if (datas[i].id == dataCollectorId) {
									$scope.selectedDataCollector = datas[i];
									break;
								}
							}
							if ($scope.selectedDataCollector != null) {
								$scope.collectorResultNumber = $scope.selectedDataCollector.dataNumber;
								$scope.collectorResultText = $scope.selectedDataCollector.dataText;
							}
						}
					};

					$scope.showDataIndicatorCollectorForm = function($event) {
						var element = angular.element($event.target);
						var indicatorId = element[0].attributes['data-indicatorId'].value;
						var collectorId = element[0].attributes['data-indicatorCollectorId'].value;
						if (indicatorId.length > 0) {
							$scope.errorMsg = null;
							$scope.successMsg = null;
							$scope.selectIndicator(indicatorId);
							if ($scope.selectedIndicator != null) {
								if (collectorId.length > 0) {
									$scope.selectDataCollector(collectorId);
								}
								var dataCollecotrModal = angular
										.element("#dataCollecotrModal");
								dataCollecotrModal.modal('show');
							}
						}
					};

					$scope.editDatatIndicatorcollecorForm = function() {

						// console.log('start date
						// '+$filter('date')($scope.selectedIndicator.startDataEditionDate,'dd/MM/yyyy'));

						$scope.errorMsg = null;
						$scope.successMsg = null;
						if ($scope.selectedIndicator != null) {
							var maxDate = $scope.selectedDataCollector != null
									&& $scope.selectedDataCollector.maxDate != null ? $scope.selectedDataCollector.maxDate
									: $scope.selectedIndicator.maxDataEditionDate != null ? $scope.selectedIndicator.maxDataEditionDate: null;
							var startDate = $scope.selectedDataCollector != null 
										&& $scope.selectedDataCollector.startDate != null ? $scope.selectedDataCollector.startDate 
										: $scope.selectedIndicator.startDataEditionDate != null ? $scope.selectedIndicator.startDataEditionDate : null;
							
							var collecorJson = {
								id : $scope.selectedDataCollector != null ? $scope.selectedDataCollector.id: null,
								dataText : $scope.collectorResultText,
								dataNumber : $scope.collectorResultNumber,
								startDate : startDate,
								maxDate : maxDate,
							};

							$http(
									{
										url : '/Performance/getEditIndicatorDataCollectorJson/',
										method : 'get',
										params : {
											stringDataCollector : collecorJson,
											indicatorId : $scope.selectedIndicator.id
										}
									})
									.then(
											function(response) {
												if (response.data != null) {
													$scope.getObjectifs();
													$scope
															.selectIndicator($scope.selectedIndicator.id);
													$scope.selectedIndicator.dataCollectors
															.push(response.data);
													$scope
															.selectDataCollector(response.data.id);
													$scope.successMsg = collecorJson.id != null ? "Données modfiées !"
															: "Donneé enregistrées !";
												} else {
													$scope.errorMsg = "Opération echouée !";
												}
											});
						}
					};
					
					$scope.getPerformancePurcente = function(baseNumber,
							mesureNumber) {
						if (mesureNumber > 0 && baseNumber > 0)
							return ((mesureNumber * 100) / baseNumber)
									.toFixed(2);
						return 0;
					};
					
					$scope.shangeArchiveStatus = function($event){
						var element = angular.element($event.target);
						var status = element[0].attributes['data-status'].value;
						var collectorId = element[0].attributes['data-indicatorCollectorId'].value;
						if(typeof status != typeof undefined && typeof collectorId != typeof undefined){
							$http({
									url : '/Performance/changeDataCollectorArchieStatus/',
									method : 'get',
									params : { collectorId : collectorId,status : status}
								}).then(function(response) {
									if (response.data != null && response.data == true) {
										$scope.getObjectifs();
									}
								});
						}
					};
					
					$scope.getDataIndicatorAllDates = function(indicator){
						console.log(indicator);
						var dates = [];
						if(indicator != null && indicator.dataCollectors != null && indicator.dataCollectors.length>0){
							for(var i = 0; i < indicator.dataCollectors.length ; i++){
								var collector = indicator.dataCollectors[i];
								var startDateFined = false, maxDateFinded=false;
								for(var j=0; j<dates.length; j++){
									if((collector.startDate != null && dates[j] == collector.startDate)){
										startDateFined = true;
										break;
									}
								}
								
								for(var j=0; j<dates.length; j++){
									if((collector.maxDate != null && dates[j] == collector.maxDate)){
										maxDateFinded = true;
										break;
									}
									
								}
								
								if(!startDateFined && collector.startDate != null)
									dates.push(collector.startDate);
								if(!maxDateFinded && collector.maxDate != null)
									dates.push(collector.maxDate);
							}
						}
						return dates;
					};
					
					$scope.changeSelectedUser = function($event){
						$event.preventDefault();
						var element = angular.element($event.target);
						var userId = element[0].attributes['data-userId'].value;
						if(userId.length>0 && $scope.users.length>0){
							for(var i=0; i<$scope.users.length; i++){
								if($scope.users[i].id == userId  && $scope.users[i].id != $scope.selectedUser.id){
									$scope.selectedUser = $scope.users[i];
									$scope.getObjectifs();
									break;
								}
							}
						}
					};
					
					$scope.changeSize = function(){
						if($scope.size != null && $scope.size>0){
							$scope.page = 0;
							$scope.getObjectifs();
						}
					};
					
					$scope.getObjectifPerfomancePurcentage = function(objectif){
						var purcente = 0;
						$scope.sumPurcente = 0;
						if(objectif != null && objectif.indicators.length>0){
							for(var i =0; i<objectif.indicators.length; i++){
								var indicator = objectif.indicators[i];
								if(indicator.expectedNumberResult != null && indicator.dataCollectors.length>0){
									for(var j=0; j<indicator.dataCollectors.length; j++){
										var collector = indicator.dataCollectors[j];
										if(collector != null && collector.dataNumber != null){
											$scope.sumPurcente = $scope.sumPurcente + parseInt("100");
											$scope.globalSumPercente = $scope.globalSumPercente + $scope.sumPurcente;
											purcente = purcente +parseFloat($scope.getPerformancePurcente(indicator.expectedNumberResult,collector.dataNumber));
										}
									}
								}
							}
						}
						return purcente.toFixed(2);
					};
					
					$scope.getGlobalPerformance = function(){
						var objectifsPerfomance = 0;
						$scope.globalSumPercente = 0;
						if($scope.objectifs.length>0){
							for(var i=0; i<$scope.objectifs.length; i++){
								objectifsPerfomance = objectifsPerfomance + parseFloat($scope.getObjectifPerfomancePurcentage($scope.objectifs[i]));
							}
						}
						console.log(objectifsPerfomance);
						return objectifsPerfomance.toFixed(2) +" / " +$scope.globalSumPercente +" %";
					};
					$scope.changeStaticViewStatus= function(stat){
						$scope.staticView = stat;
					};
				});
cperfModule.controller("LogigrammeCtrl", function($scope, $http) {
	$scope.selectedTaskId = 5;
	$scope.errorMsg = null;
	$scope.successMsg = null;
	$scope.taskname = null;
	$scope.tasksection = null;
	$scope.nbYears = 0;
	$scope.nbMonths = 0;
	$scope.nbDays = 0;
	$scope.nbHours = 0;
	$scope.nbMinuites=0;
	$scope.taskDescription=null;
	$scope.taskFileDescription=null;
	$scope.taskType = null;
	$scope.taskStatus = null;
	$scope.taskValidator = null;
	$scope.taskGroup = null;
	$scope.taskLunchingByProcess = false;
	
	$scope.getSelectedTask = function(){
		if($scope.selectedTaskId !== null && $scope.selectedTaskId > 0){
		$http({
			url : '/Task/getTaskByIdJson/',
			method : 'get',
			params : {tid : $scope.selectedTaskId}
		}).then(function(response) {
			if (response.data != null){
				$scope.taskname = response.data.name;
				$scope.taskSection = response.data.section.id;
				$scope.nbYears = response.data.nbYears;
				$scope.nbMonths = response.data.nbMonths;
				$scope.nbDays = response.data.nbDays;
				$scope.nbHours = response.data.nbHours;
				$scope.nbMinuites=response.data.nbMinuites;
				$scope.taskDescription=response.data.description;
				$scope.taskFileDescription=response.data.DbFileDescription != null ? response.data.DbFileDescription.name : response.data.fileDescription;
				$scope.taskType = response.data.type;
				$scope.taskStatus = response.data.status;
				$scope.taskValidator = response.data.validator.id;
				$scope.taskGroup =response.data.group.id;
				$scope.taskLunchingByProcess = response.data.lunchingByProcess;
			}
		});
	 }
	};
});