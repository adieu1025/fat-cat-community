//添加
function add() {
    getPoint();
    $("#addObjectPaper").modal({
        backdrop: "static"
    });
}

//在添加/编辑页面向分类下拉列表中添加分类信息，为防止重复发送，需进行判断
function getPoint() {
    var childCount = $("#categoryId").children().length;
    if (childCount < 1) {
        $.ajax({
            url: '/community/admin/findCategories',
            type: 'POST',
            success: function (data) {
                for (var i = 0; i < data.length; i++) {
                    $("#categoryId").append("<option value='" + data[i].id + "'>" + data[i].name + "</option>");
                }
            }
        });
    }
}

//鼠标点击表格显示隐藏内容
function showContent(td) {
    if (td.offsetWidth < td.scrollWidth) {
        var text = $(td).text();
        alert(text);
    }
}


