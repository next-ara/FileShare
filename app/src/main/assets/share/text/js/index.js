var content = '暂无分享的文本';
var buttonText = "复制"

window.onload = function () {
    var isMobile = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent);

    //发起请求
    startRequest(isMobile);
}

/**
 * 发起请求
 * @param isMobile 是否是移动端
 */
function startRequest(isMobile) {
    var requestUrl = window.location.href;

    if (requestUrl.endsWith('/')) {
        requestUrl = requestUrl + 'text';
    } else {
        requestUrl = requestUrl + '/text';
    }

    const xhr = new XMLHttpRequest();

    //设置请求方法
    xhr.open('GET', requestUrl, true);

    xhr.onload = function () {
        if (xhr.status === 200) {
            // 请求成功，解析返回的JSON数据
            const response = JSON.parse(xhr.responseText);
            // 获取需要显示的文本
            content = response.content.replace(/\n/g, '<br>');
        }

        //设置Body
        setBody(isMobile);
    };

    //发送请求
    xhr.send();
}

/**
 * 设置Body
 * @param isMobile 是否是移动端
 */
function setBody(isMobile) {
    if (isMobile) {
        document.body.innerHTML = '<div id="text" class="text_mobile">' + content + '</div><button id="button" class="button_mobile" onclick="copyClick()">' + buttonText + '</button>';
    } else {
        document.body.innerHTML = '<div id="text" class="text_computer">' + content + '</div><button id="button" class="button_computer" onclick="copyClick()">' + buttonText + '</button>';
    }
}