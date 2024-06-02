$(function () {
    //鼠标点击td显示全部内容
    $("td").on("click", function () {
        if (this.offsetWidth < this.scrollWidth) {
            var that = this;
            var text = $(this).text();
            alert(text);
        }
    });
})

//在添加/编辑页面向分类下拉列表中添加分类信息，为防止重复发送，需进行判断
function getPoint() {
    var childCount = $("#categoryId").children().length;
    if (childCount < 1) {
        $.ajax({
            url: '/community/admin/findSubjectiveCategories',
            type: 'POST',
            success: function (data) {
                for (var i = 0; i < data.length; i++) {
                    $("#categoryId").append("<option value='" + data[i].id + "'>" + data[i].name + "</option>");
                }
            }
        });
    }
}

//添加
function add() {
    getPoint();
    $("#addSubPaper").modal({
        backdrop: "static"
    });
}

//添加材料
function addArticle(id, articleNum) {
    //往 添加材料模态框 中添加输入框，有几篇材料即添加几个输入框
    for(var i = 1; i <= articleNum; i++) {
        $("#addArticleBody").append(
            "<div class=\"form-group input-group\">\n" +
            "   <span class=\"input-group-addon\" style=\"height: 40px ; tab-size: 16px \">材料"+ i +"</span>\n" +
            "   <textarea class=\"form-control\" rows=\"3\" name=\"article" + i + "\" id=\"article"+i+"\"></textarea>\n" +
            "</div>"
        );
    }
    $("#paperId").val(id);
    $("#addArticle").modal({
        backdrop: "static"
    });
}

//添加题目
function addEssay(id, essayNum) {
    //往 添加材料模态框 中添加题目和答案的输入框，有几篇材料即添加几个输入框
    for(var i = 1; i <= essayNum; i++) {
        $("#addEssayBody").append(
            "<div class=\"form-group input-group\">\n" +
            "    <span class=\"input-group-addon\" style=\"height: 40px ; tab-size: 16px \">问题"+ i +"</span>\n" +
            "    <textarea class=\"form-control\" rows=\"3\" name=\"essay"+i+"\" id=\"essay"+i+"\"></textarea>\n" +
            "</div>\n" +
            "\n" +
            "<div class=\"form-group input-group\">\n" +
            "    <span class=\"input-group-addon\" style=\"height: 40px ; tab-size: 16px \">答案"+ i +"</span>\n" +
            "    <textarea class=\"form-control\" rows=\"3\" name=\"answer"+i+"\" id=\"answer"+i+"\"></textarea>\n" +
            "</div>"
        );
    }
    $("#paperId1").val(id);
    $("#addEssay").modal({
        backdrop: "static"
    });
}

//清空节点
function clearDom() {
    $("#addArticleBody").empty();
}

//删除试卷
function delOne(id) {
    var sure = confirm("确定要删除id为：" + id + "的试卷吗？");
    if(sure) {
        $.post(
            "/community/admin/delSubPaper",
            {"id": id},
            function (data) {
                data = $.parseJSON(data);
                if (data.code === 0) {
                    window.location.href = "/community/admin/subPaperList";
                } else {
                    alert(data.msg);
                }
            }
        )
    }
}
