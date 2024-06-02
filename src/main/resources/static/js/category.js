//添加分类
function add() {
    $("#addCategory").modal({
        backdrop: "static"
    });
}

//编辑分类
function edit(id) {
    //先查询所有科目信息，回显到编辑框的科目列表中
    $.ajax({
        url: '/community/admin/findAll',
        type: 'POST',
        success: function (data) {
            //如果下拉列表不存在时，才向<select>添加<option>，防止多次点击编辑时重复添加<option>
            if ($("#subjectId1").children().length < 1) {
                for (var i = 0; i < data.length; i++) {
                    $("#subjectId1").append("<option value='" + data[i].id + "'>" + data[i].name + "</option>");
                }
            }
        }
    });

    $.post(
        "/community/admin/getCategory",
        {"id": id},
        function (data) {
            data = $.parseJSON(data);
            if (data.code === 0) {
                $("#name1").val(data.name);
                //添加分类id到隐藏域中，方便表单提交时向后端提供分类id
                $("#CategoryId1").val(data.id)
                $("#editCategory").modal('show');
            } else {
                alert(data.msg);
            }
        }
    );
}

//根据id删除分类
function delCategory(id) {
    var sure = confirm("确定要删除id为：" + id + "的分类吗？");
    if (sure) {
        $.post(
            "/community/admin/delCategory",
            {"id": id},
            function (data) {
                data = $.parseJSON(data);
                if (data.code === 0) {
                    window.location.href = "/community/admin/categoryList";
                } else {
                    alert(data.msg);
                }
            }
        )
    }
}

//批量删除
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

    //获取被选中的id
    $('#getAllCategoryId').click(function () {
        var ids = [];
        $("input[name='items']:checked").each(function () {
            ids.push($(this).attr("id"));
        });
        if (ids === "") {
            alert("请选择删除的数据！")
        } else {
            console.log(ids);
            var sure = confirm("确定要进行批量删除分类？");
            if (sure) {
                //发送ajax请求进行批量删除
                $.post(
                    "/community/admin/batchDelCategory",
                    {"ids": ids},
                    function (data) {
                        data = $.parseJSON(data);
                        if (data.code === 0) {
                            window.location.href = "/community/admin/categoryList";
                        } else {
                            alert(data.msg);
                        }
                    }
                );
            }
        }
    });
});

