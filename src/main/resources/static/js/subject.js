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

    $('#getAllSelectedId').click(function () {
        var ids = [];
        $("input[name='items']:checked").each(function () {
            ids.push($(this).attr("id"));
        });
        if (ids === "") {
            alert("请选择删除的数据！")
        } else {
            console.log(ids);
            var sure = confirm("确定要进行批量删除科目？");
            if (sure) {
                //发送ajax请求进行批量删除
                $.post(
                    "/community/admin/batchDelSub",
                    {"ids": ids},
                    function (data) {
                        data = $.parseJSON(data);
                        if (data.code === 0) {
                            window.location.href = "/community/admin/subjectList";
                        } else {
                            alert(data.msg);
                        }
                    }
                );
            }
        }
    });
});

//添加科目
function add() {
    $("#addSubject").modal({
        backdrop: "static"
    });
}

//编辑科目
function edit(id) {
    $.post(
        "/community/admin/getSubject",
        {"id": id},
        function (data) {
            data = $.parseJSON(data);
            if (data.code === 0) {
                console.log("科目：" + data.name);
                $("#name1").val(data.name);
                //添加科目id到隐藏域中，方便表单提交时向后端提供科目id
                $("#subjectId1").val(data.id)
                $("#editSubject").modal('show');
            } else {
                alert(data.msg);
            }
        }
    );
}

//删除科目
function delSubject(id) {
    var sure = confirm("你确定要删除id为：" + id + "的科目吗？");
    if (sure) {
        $.post(
            "/community/admin/delSubject",
            {"id": id},
            function (data) {
                data = $.parseJSON(data);
                if (data.code === 0) {
                    window.location.href = "/community/admin/subjectList";
                } else {
                    alert(data.msg);
                }
            }
        );
    } else {
        return false;
    }
}




