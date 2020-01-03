var currentInput;

function showKeyboxKeyboard(inputObjId){
	var height = $(inputObjId).offset().top;
	$(inputObjId).blur();
	currentInput = $(inputObjId);
	app.showKeyboxKeyboard(height);
}

function showCommonKeyboard(inputObjId) {
	var height = $(inputObjId).offset().top;
	$(inputObjId).blur();
	currentInput = $(inputObjId);
	app.showCommonKeyboard(height);
}

function keyboardDelete() { //删除
	var str = currentInput.val();
	var strNew = str.substring(0, str.length - 1);
	currentInput.val(strNew);
}

function keyboardInsert(str) { //插入
	var s = currentInput.val();
	var strNew = s + str;
	currentInput.val(strNew);
}

$(document).on('click', function(el) {
	if (el.target.tagName != 'INPUT') {
		$('input').blur()
		app.hideKeyboard();
	}
})
