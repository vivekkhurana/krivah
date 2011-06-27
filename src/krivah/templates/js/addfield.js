$(document).ready(function(){
	alert($('#fieldcount').val());	
$('#addmore').click(function(){
	alert("test");
	var count = parseInt($('#fieldcount').val()) + 1;
	var str = "<div class=\"itemcontainer\"><div class=\"formitem\">Label:</div><div class=\"formitem\"><input type=\"text\" name=\"itemlabel"+count+"\" id=\"itemlabel"+count+"\"></div><div class=\"clearall\"></div> <div class=\"formitem\">Machine name:</div><div class=\"formitem\"><input type=\"text\" name=\"machinename"+count+"\" id=\"machinename"+count+"\"></div><div class=\"clearall\"></div><div class=\"formitem\">Type:</div><div class=\"formitem\"><select class=\"selectitem\" name=\"formtype"+count+"\" id=\"formtype"+count+"\"><option value=\"text\">Text box</option><option  value=\"textarea\">Text box, multi line</option><option value=\"select\">Select drop down</option> <option value=\"selectmaster\">Select box from master data</option></select></div><div class=\"clearall\"></div></div>";
	$('#formcontainer').append(str);
	$('#fieldcount').val(count);

	

});

$('.selectitem').live('change',function(){
	if($(this).val() == 'select'){
		var str = "<div class=\"optioncontainer\"><div class=\"formitem\">Option name</div><div class=\"formitem\">Option Value</div><div class=\"clearall\"></div><div class=\"formitem\"><input type=\"text\" name=\"selectoption1\" id=\"selectoption1\"/></div><div class=\"formitem\"><input type=\"text\" name=\"selectvalue1\" id=\"selectvalue1\"/></div><div class=\"clearall\"></div></div><input type=\"hidden\" name=\"selectcount_"+$(this).attr('id')+"\" id=\"selectcount_"+$(this).attr('id')+"\" value=\"1\"/><input type=\"button\" class=\"addoption\" value=\"Add option\" parent=\""+$(this).attr('id')+"\"/>";
	$(this).parent().append(str);
	}
});

$('.addoption').live('click',function(){
	
	var v = $(this).attr('parent');

	var x = $('#'+v).parent().find('.optioncontainer');
	var count = parseInt($('#selectcount_'+v).val()) + 1;
	var str = "<div class=\"formitem\"><input type=\"text\" name=\"selectoption"+count+"\" id=\"selectoption"+count+"\"/></div><div class=\"formitem\"><input type=\"text\" name=\"selectvalue"+count+"\" id=\"selectvalue"+count+"\"/></div><div class=\"clearall\"></div>";
	$('#selectcount_'+v).val(count);
	$(x).append(str);
});

});
