$(function () {
    //实现全选与反选 var ids=[];
    $("#allAndNotAll").click(function () {
        if (this.checked) {
            $("input[name='items']:checkbox").each(function () {
                $(this).attr("checked", true);
            });
        } else {
            $("input[name='items']:checkbox").each(function () {
                $(this).attr("checked", false);
            });
        }
    });
    //获取被选中的id，并批量删除题目
    $('#getAllSelectedId').click(function () {
        var ids = [];
        $("input[name='items']:checked").each(function () {
            ids.push($(this).attr("id"));
        });
        if (ids === "") {
            alert("请选择删除的数据！");
        } else {
            var sure = confirm("确定要进行批量删除问题？");
            if (sure) {
                $.post(
                    "/community/admin/batchDelSingle",
                    {"ids": ids},
                    function (data) {
                        data = $.parseJSON(data);
                        if (data.code === 0) {
                            window.location.href = "/community/admin/singleList";
                        } else {
                            alert(data.msg);
                        }
                    }
                )
            }

        }
    });

    //鼠标点击td显示全部内容
    $("td").on("click", function () {
        if (this.offsetWidth < this.scrollWidth) {
            var that = this;
            var text = $(this).text();
            alert(text);
        }
    });

});

//添加
function add() {
    getPoint();
    $("#addSingle").modal({
        backdrop: "static"
    });
}

//在添加页面向分类下拉列表中添加分类信息，为防止重复发送，需进行判断
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

// 编辑信息的方法
function edit(id) {
    //回显分类信息
    var count = $("#categoryId1").children().length;
    if (count < 1) {
        $.ajax({
            url: '/community/admin/findCategories',
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
            "/community/admin/getSingle",
            {"id": id},
            function (data) {
                data = $.parseJSON(data);
                if (data.code === 0) {
                    //把得到的信息存到HTML页面中
                    $("#singleId1").val(data.single.id);
                    $("#stem1").val(data.single.stem);
                    //判断图片地址的长度，若为1则该题无图
                    var url = data.single.imageUrl;
                    if (url.length > 1) {
                        $("#img1").attr("src", url);
                        $("#imageUrl1").attr("value", url);
                    } else {
                        $("#img1").attr("src", "");
                        $("#imageUrl1").attr("value", "1");
                    }
                    $("#optionA1").val(data.single.optionA);
                    $("#optionB1").val(data.single.optionB);
                    $("#optionC1").val(data.single.optionC);
                    $("#optionD1").val(data.single.optionD);
                    $("#analysis1").val(data.single.analysis);
                    $("#score1").val(data.single.score);
                    //开启模态框
                    $("#editSingle").modal('show');
                } else {
                    alert(data.msg);
                }
            }
        )
    }
}

function resetForm() {
    var form = document.getElementById("editForm");
    form.reset();
}

//删除
function delOne(id) {
    var sure = confirm("确定要删除id为：" + id + "的题目吗？");
    if (sure) {
        $.post(
            "/community/admin/delSingle",
            {"id": id},
            function (data) {
                data = $.parseJSON(data);
                if (data.code === 0) {
                    window.location.href = "/community/admin/singleList";
                } else {
                    alert(data.msg);
                }
            }
        )
    }
}