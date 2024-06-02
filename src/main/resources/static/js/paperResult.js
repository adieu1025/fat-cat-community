//根据用户答案是否正确添加绿色/红色的css样式
function setKeyColor() {
    var userKeys = document.getElementsByClassName("userKey");
    var rightKeys = document.getElementsByClassName("rightKey");

    for(let i = 0; i < userKeys.length; i++) {
        if(userKeys[i].textContent === rightKeys[i].textContent) {
            userKeys[i].setAttribute('style', 'color: #03f803;');
        } else {
            userKeys[i].setAttribute('style', 'color: #ff0000;');
        }
    }
}