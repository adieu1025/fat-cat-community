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

function add() {
    getPoint();
    $("#addSubjective").modal({
        backdrop: "static"
    });
}

//在添加页面向分类下拉列表中添加【主观题】的分类信息，为防止重复发送，需进行判断
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

//jq监听input内容，内容有变化便执行此函数
$("#uploadImage").bind("input propertychange", function () {
    postImg();
});

//上传题目图片并回显
function postImg() {
    //判断复选框是否被选中
    var hasImg = $("#hasImg").prop('checked');
    console.log(hasImg);
    // 在勾选了题目存在图片时，才进行发送上传图片请求
    if (hasImg) {
        var formData = new FormData();
        formData.append("file", $("#uploadImage")[0].files[0]);
        $.ajax({
            url: "/community/admin/uploadQuesImg",
            type: "post",
            data: formData,
            processData: false, // 告诉jQuery不要去处理发送的数据(不要序列化数据，不然会报错)
            contentType: false, // 告诉jQuery不要去设置Content-Type请求头
            dataType: 'text', //返回值类型
            success: function (data) {
                var params = JSON.parse(data);
                //回显图片
                $("#img").attr("src", params.url);
                //保存图片url到隐藏域，方便传入后端进行问题的保存
                $("#imageUrl").attr("value", params.url)
            },
            error: function (data) {
                var params = JSON.parse(data);
                alert(params.msg);
            }
        });
    }
}

// 编辑判断题
function edit(id) {
    //回显分类信息
    var count = $("#categoryId1").children().length;
    if (count < 1) {
        $.ajax({
            url: '/community/admin/findSubjectiveCategories',
            type: 'POST',
            success: function (data) {
                for (var i = 0; i < data.length; i++) {
                    $("#categoryId1").append("<option value='" + data[i].id + "'>" + data[i].name + "</option>");
                }
            }
        });
    }

    if (!id) {
        //未传入问题id
        alert("error");
    } else {
        // 查询数据，回显在编辑框中
        $.post(
            "/community/admin/getSubjective",
            {"id": id},
            function (data) {
                data = $.parseJSON(data);
                if (data.code === 0) {
                    //把得到的信息存到HTML页面中
                    $("#subjectiveId1").val(data.subjective.id);
                    $("#stem1").val(data.subjective.stem);
                    //判断图片地址的长度，若为1则该题无图
                    var url = data.subjective.imageUrl;
                    if (url.length > 1) {
                        $("#img1").attr("src", url);
                        $("#imageUrl1").attr("value", url);
                    } else {
                        $("#img1").attr("src", "");
                        $("#imageUrl1").attr("value", "1");
                    }
                    $("#article1").val(data.subjective.article);
                    $("#answer1").val(data.subjective.answer);
                    $("#score1").val(data.subjective.score);
                    //开启模态框
                    $("#editSubjective").modal('show');
                } else {
                    alert(data.msg);
                }
            }
        )
    }
}

//重置表单
function resetForm() {
    var form = document.getElementById("editForm");
    form.reset();
}

//删除
function delOne(id) {
    var sure = confirm("确定要删除id为：" + id + "的题目吗？");
    if (sure) {
        $.post(
            "/community/admin/delSubjective",
            {"id": id},
            function (data) {
                data = $.parseJSON(data);
                if (data.code === 0) {
                    window.location.href = "/community/admin/subjectiveList";
                } else {
                    alert(data.msg);
                }
            }
        )
    }
}


