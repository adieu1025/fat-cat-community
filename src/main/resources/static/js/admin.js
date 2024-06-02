// $(function () {
//     $("button#userStatusBtn").click(setUserStatus);
//     $("button#delUserBtn").click(delUser);
// });

//激活/禁用 用户账号
function setUserStatus(btn, id) {
    $.post(
        CONTEXT_PATH + "/admin/setUserStatus",
        {"id":id},
        function (data) {
            data = $.parseJSON(data);
            if(data.code === 0) {
                //按钮内容变化
                $(btn).text(data.status === 1?'禁用':'启用');
                //表格内容变化,寻找父节点的兄弟节点
                var tdElement = document.getElementById(id);
                // 根据当前文本内容进行状态切换,trim()去除两端空格
                var currentStatus = tdElement.textContent.trim();
                var newStatus = (currentStatus === '正常') ? '禁用' : '正常';
                // 更新<td>元素的文本内容
                tdElement.textContent = newStatus;

            }else {
                alert(data.msg);
            }
        }
    )
}

// 删除用户账号
function delUser(btn, id) {
    var sure = confirm("你确定要删除id为：" + id + "的用户吗？");
    if(sure) {
        $.post(
            CONTEXT_PATH + "/admin/delUser",
            {"id":id},
            function (data) {
                data = $.parseJSON(data);
                if(data.code === 0) {
                    window.location.href = CONTEXT_PATH + "/admin/userList";
                } else {
                    alert(data.msg);
                }
            }
        );
    } else {
        return false;
    }

}
