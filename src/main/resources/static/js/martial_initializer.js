$(document).ready(function(){
    // martial datepicker initialization
      $('.datepicker').datepicker({
      	format : 'dd/mm/yyyy',
      	monthsFull: ['Janvier', 'Février', 'Mars', 'Avril', 'Mai', 'Juin', 'Juillet', 'Août', 'Septembre', 'Octobre', 'Novembre', 'Décembre'],
        weekdaysShort: ['Dim', 'Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam'],
        today: 'Aujourd\'hui',
        clear: 'effacer',
      	cancel : 'Annuler'
      });
  // martial select initialization
	    $('select').formSelect();
});