//$(document).ready(function(){
//	var hierarchy_link = "/Organigramme/Hierarchy";
//	loadDatas("/Organigramme/Hierarchy"); 

//});
$(document).ready(function(){
	loadDatas("/Organigramme/Hierarchy");
	$("#people").on('click','.btn_objectif',function(e){
		e.preventDefault();
		var userId = $(".id_input").val();//getFonctionIdJson
		$.ajax({
			url : '/Organigramme/getFonctionIdJson?uid='+userId,
			method : 'get',
			contentType : 'json',
			success : function(res){
				if(res != null){
					window.location = "/Objectif/?fid="+res;
				}
			},
			error : function(e){
				console.log(e);
			}
		});
	});
});

var loadDatas = function(hierarchy_link){
	$.ajax({
		url : hierarchy_link,
        type : 'get',
        dataType : 'json',
        async : false,
        success : function(data){
        	var datas = [];
        	for(var i=0; i< data.length; i++){
        		datas[i] = {
        				id: data[i].id, parentId: data[i].parentId,Prenom:data[i].prenom, Nom:data[i].nom,
        				Fonction: data[i].fonction,Objectifs:data[i].objectif,
        				Telephone: data[i].telephone, Email: data[i].email, Adresse: data[i].adresse,image: data[i].image 
        		};
        	}

        	loadOrganigramme(datas);
        },
        error : function(e){
        	console.log(e);
        }
	});
};

loadOrganigramme = function(data){
	var peopleElement = document.getElementById("people");
	var orgChartPara = { 
        primaryFields: ["Prenom", "Fonction", "Telephone", "Email"],
        photoFields: ["image"],
        expandToLevel: 100,
        layout: getOrgChart.MIXED_HIERARCHY_RIGHT_LINKS,
        theme : "deborah",
        color: "blue",
        enableZoom: true,
        enableSearch: true,
        enableMove: true,
        enableDetailsView: false,
        enablePrint: true,
        enableZoomOnNodeDoubleClick: true,
        enableExportToImage: true,
        enableEdit: true,
        linkType: "B",
        insertNodeEvent: insertNodeEvent,
        createNodeEvent: createNodeEvent,
        updatedEvent: updatedEvent,
        updateNodeEvent : updateNodeEvent,
        removeNodeEvent: removeNodeEvent,
        renderNodeEvent : renderNodeEvent,
        dataSource: data,
	};
	var orgChart = new getOrgChart(peopleElement,orgChartPara);
};

function insertNodeEvent(sender,args){
	$.ajax({
		url : "/Organigramme/Hierarchy/insert?pid="+args.pid,
        type : 'get',
		contentType : 'application/json; charset=utf-8',
        success : function(added_id){
        	args.id=added_id;
        	 location.reload();
        },
        error : function(e){
        	console.log(e);
        }
	});
};
function createNodeEvent(sender, args){
	
};
function renderNodeEvent(sender,args){
	
};
function  updatedEvent(sender,args){
	//console.log(sender);
};

function updateNodeEvent(sender,args){
	var my_object = {};
	console.log(sender);
	my_object.nom = args.data.Nom;
	my_object.prenom=args.data.Prenom;
	my_object.id=args.node.id;
	my_object.fonction=args.data.Fonction;
	my_object.telephone=args.data.Telephone;
	my_object.email = args.data.Email;
	my_object.adresse=args.data.Adresse;
	my_object.image = args.data.image;
	my_object.parentId = args.pid;
	my_object.objectif = args.data.Objectifs;
	$.ajax({
		url : "/Organigramme/Hierarchy/update",
        type : 'POST',
		contentType : 'application/json; charset=utf-8',
        data : JSON.stringify(my_object),
        success : function(data){
        	console.log("update ok");
        },
        error : function(e){
        	console.log(e);
        }
	});
};
function removeNodeEvent(sender,args){
	$.ajax({
		url : '/Organigramme/ChangeOrgValue/?uid='+args.id,
		method : 'GET',
		success : function(data){
			console.log("dynamic node removed");
		},
	   error : function(e){
		   console.log(e);
	   }
	});
};
static_datas = function(){
	return [
        { id: 1, parentId: null,Nom: "Amber McKenzie", Fonction: "CEO", Telephone: "678-772-470", Email: "lemmons@jourrapide.com", Adresse: "Atlanta, GA 30303",Activite :"", Objectifs:"",image: "images/f-1.jpg" },
        { id: 2, parentId: 1,Nom: "Ava Field", Fonction: "Paper goods machine setter", Telephone: "937-912-4971", Email: "anderson@jourrapide.com", image: "images/f-2.jpg" },
        { id: 3, parentId: 1,Nom: "Evie Johnson", Fonction: "Employer relations representative", Telephone: "314-722-6164", Email: "thornton@armyspy.com", image: "images/f-3.jpg" },
        { id: 4, parentId: 2,Nom: "Paul Shetler", Fonction: "Teaching assistant", Telephone: "330-263-6439", Email: "shetler@rhyta.com", image: "images/f-4.jpg" },
        { id: 5, parentId: 2,Nom: "Rebecca Francis", Fonction: "Welding machine setter", Telephone: "408-460-0589", image: "images/f-5.jpg" },
        { id: 6, parentId: 2,Nom: "Rebecca Randall", Fonction: "Optometrist", Telephone: "801-920-9842", Email: "JasonWGoodman@armyspy.com", image: "images/f-6.jpg" },
        { id: 7, parentId: 2,Nom: "Riley Bray", Fonction: "Structural metal fabricator", Telephone: "479-359-2159", image: "images/f-12.jpg" },
        { id: 8, parentId: 3,Nom: "Spencer May", Fonction: "System operator", Telephone: "Conservation scientist", Email: "hodges@teleworm.us", image: "images/f-7.jpg" },
        { id: 9, parentId: 3,Nom: "Max Ford", Fonction: "Budget manager", Telephone: "989-474-8325", Email: "hunter@teleworm.us", image: "images/f-8.jpg" },
        { id: 10, parentId: 3,Nom: "Riley Bray", Fonction: "Structural metal fabricator", Telephone: "479-359-2159", image: "images/f-15.jpg" },
        { id: 11, parentId: 4,Nom: "Callum Whitehouse", Fonction: "Radar controller", Telephone: "847-474-8775", image: "images/f-10.jpg" },
        { id: 12, parentId: 4,Nom: "Max Ford", Fonction: "Budget manager", Telephone: "989-474-8325", Email: "hunter@teleworm.us", image: "images/f-11.jpg" },
        { id: 13, parentId: 4,Nom: "Riley Bray", Fonction: "Structural metal fabricator", Telephone: "479-359-2159", image: "images/f-12.jpg" },
        { id: 14, parentId: 5,Nom: "Callum Whitehouse", Fonction: "Radar controller", Telephone: "847-474-8775", image: "images/f-13.jpg" },
        { id: 15, parentId: 5,Nom: "Max Ford", Fonction: "Budget manager", Telephone: "989-474-8325", Email: "hunter@teleworm.us", image: "images/f-14.jpg" },
        { id: 16, parentId: 5,Nom: "Riley Bray", Fonction: "Structural metal fabricator", Telephone: "479-359-2159", image: "images/f-15.jpg" },
        { id: 17, parentId: 6,Nom: "Callum Whitehouse", Fonction: "Radar controller", Telephone: "847-474-8775", image: "images/f-16.jpg" },
        { id: 18, parentId: 6,Nom: "Max Ford", Fonction: "Budget manager", Telephone: "989-474-8325", Email: "hunter@teleworm.us", image: "images/f-17.jpg" },
        { id: 19, parentId: 7,Nom: "Spencer May", Fonction: "System operator", Telephone: "Conservation scientist", Email: "hodges@teleworm.us", image: "images/f-7.jpg" },
        { id: 20, parentId: 7,Nom: "Max Ford", Fonction: "Budget manager", Telephone: "989-474-8325", Email: "hunter@teleworm.us", image: "images/f-8.jpg" },
        { id: 21, parentId: 7,Nom: "Riley Bray", Fonction: "Structural metal fabricator", Telephone: "479-359-2159", image: "images/f-9.jpg" },
        { id: 22, parentId: 8,Nom: "Ava Field", Fonction: "Paper goods machine setter", Telephone: "937-912-4971", Email: "anderson@jourrapide.com", image: "images/f-2.jpg" },
        { id: 23, parentId: 8,Nom: "Evie Johnson", Fonction: "Employer relations representative", Telephone: "314-722-6164", Email: "thornton@armyspy.com", image: "images/f-3.jpg" }, 
        { id: 24, parentId: 9,Nom: "Callum Whitehouse", Fonction: "Radar controller", Telephone: "847-474-8775", image: "images/f-13.jpg" },
        { id: 25, parentId: 9,Nom: "Max Ford", Fonction: "Budget manager", Telephone: "989-474-8325", Email: "hunter@teleworm.us", image: "images/f-14.jpg" },
        { id: 26, parentId: 9,Nom: "Riley Bray", Fonction: "Structural metal fabricator", Telephone: "479-359-2159", image: "images/f-15.jpg" },
        { id: 27, parentId: 10,Nom: "Callum Whitehouse", Fonction: "Radar controller", Telephone: "847-474-8775", image: "images/f-13.jpg" },
        { id: 28, parentId: 10,Nom: "Max Ford", Fonction: "Budget manager", Telephone: "989-474-8325", Email: "hunter@teleworm.us", image: "images/f-14.jpg" },
        { id: 29, parentId: 10,Nom: "Riley Bray", Fonction: "Structural metal fabricator", Telephone: "479-359-2159", image: "images/f-15.jpg" }

    ];
}

