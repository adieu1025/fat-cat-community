var userTime = 0;//用户当前答题时间
var quesNum = document.getElementById("quesNum").value;

/**题库页面**/


/**答题页面（单选）**/
//动态显示答题时间
function showTime() {
    userTime += 1;
    var hour = Math.floor(userTime / 3600);
    var minute = Math.floor((userTime - hour * 3600) / 60);
    var second = Math.floor(userTime - hour * 3600 - minute * 60);
    document.getElementById("userTime").innerHTML = format(hour) + ":" + format(minute) + ":" + format(second);

}

// 格式化日期数字
function format(timeNumber) {
    if (timeNumber === 0) {
        return "00";
    } else if (timeNumber < 10) {
        return "0" + timeNumber;
    } else {
        return timeNumber;
    }
}

//每秒调用一次显示时间的函数
window.setInterval("showTime()", 1000);


//记录开始答题的时间，打开页面时传值
var startTime = 0;
window.onload = function () {
    startTime = new Date().getTime();
}

//确认是否提交
function sureSubmit() {
    //获取用户的答案
    var radio = document.getElementsByClassName("option");
    var userKeys = [];
    var quesKeys = {};
    for (let i = 0; i < radio.length; i++) {
        if (radio[i].checked) {
            //用户答案数组，用来判断是否答题完整
            userKeys.push(radio[i].value);
            //问题id：用户答案，用来传输给后端进行业务逻辑判断
            quesKeys[radio[i].name] = radio[i].value;
        } else {
            //对于未选择的题目，记录题目id，用户答案则为空串
            if(!quesKeys.hasOwnProperty(radio[i].name)) {
                //如果已经存在该key了，说明在checked中或者本else中已经进行过赋值了，就不必重复赋值，以免用户选择的答案被覆盖
                //因此，应该在不存在该key时才进行赋值
                quesKeys[radio[i].name] = "";
            }
        }
    }
    console.log(quesKeys);
    if (userKeys.length < quesNum) {
        var sure = confirm("还有未选择的题目，是否提交？");
        if (sure) {
            submitPaper(userKeys, quesKeys);
            return;
        } else {
            return;
        }
    }
    sure = confirm("将提交该试卷，是否继续?");
    if (sure) {
        submitPaper(userKeys, quesKeys);
    }
}

//提交答案到后端
function submitPaper(userKeys, quesKeys) {
    //获取表单数据
    let formData = {};
    let value = $('#singleForm').serializeArray();
    $.each(value, function (index, item) {
        formData[item.name] = item.value;
    });

    //组装要发送的数据
    // 问题id-用户答案的json
    let postData = {};
    postData["quesKeys"] = quesKeys;
    //试卷id、用户id
    postData["paperId"] = parseInt(formData["paperId"]);
    postData["userId"] = parseInt(formData["userId"]);
    //开始答题的时间(时间戳)
    postData["startTime"] = startTime;
    //提交时间(时间戳)
    postData["submitTime"] = new Date().getTime();
    //答题时间
    postData["userTime"] = Math.round(userTime / 60); //四舍五入
    //题目总数
    postData["totalNum"] = parseInt(quesNum);
    //console.log(postData);

    //发送ajax请求
    $.ajax({
        type: 'POST',
        contentType: "application/json;charset=utf-8",
        url: CONTEXT_PATH + "/exercises/paper/userScore",
        data: JSON.stringify(postData),
        dataType: 'json',
        success: function (data) {
            if(data.code === 0) {
                let userPaperId = data.object;
                window.location.href = CONTEXT_PATH + '/exercises/paper/userPaper/' + userPaperId;
            } else {
                alert(data.msg);
            }
        },
        error: function (data) {
            alert("error");
        }
    });

}



