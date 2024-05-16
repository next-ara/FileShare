var requestUrl = window.location.href;
var win_width = getWinWidth();
var json_data = {};

window.onload = function () {
    //发起请求
    startRequest();
}

/**
 * 窗口大小检测
 */
function winSizeCheck() {
    var width = getWinWidth();
    if (win_width !== width) {
        win_width = width;
        //设置Body
        setBody(isMobile());
    }
}

/**
 * 发起请求
 */
function startRequest() {
    var url = requestUrl;

    if (url.endsWith('/')) {
        url = url + 'info';
    } else {
        url = url + '/info';
    }

    const xhr = new XMLHttpRequest();

    //设置请求方法
    xhr.open('GET', url, true);

    xhr.onload = function () {
        if (xhr.status === 200) {
            json_data = JSON.parse(xhr.responseText);
            //设置body
            setBody(isMobile());
            //设置检测窗口大小定时器
            setInterval("winSizeCheck()", 500);
        }
    };

    //发送请求
    xhr.send();
}

/**
 * 设置body
 * @param isMobile 是否是移动端
 */
function setBody(isMobile) {
    if (isMobile) {
        document.body.innerHTML = '<div class="app_download"><div class="app_download_left"><img src="image/ic_app.svg"><div class="app_info"><p class="app_name">Next快传</p><p class="app_des">简洁高效的局域网快传软件</p></div></div><button id="download-app" class="app_download_btn">下载APP</button></div><div class="file_share_mobile"><div class="file_share_info_mobile"><img src="image/ic_file_share.svg"><p id="nick-name">未知设备<br>向你发送0个文件</p></div><button id="download-all">全部下载</button></div><div class="floating_info_mobile"><span id="floating-tips" class="floating_tips_mobile">接收到0个文件</span><button id="floating-button" class="floating_button_mobile">全部下载</button></div><div id="file-list" class="file_list_mobile"></div>';
    } else {
        document.body.innerHTML = '<div class="file_share_computer"><div class="file_share_info_computer"><img src="image/ic_file_share.svg"><p id="nick-name">未知设备<br>向你发送0个文件</p><button id="download-all">全部下载</button></div></div><div class="floating_info_computer"><div class="float_container_computer"><span id="floating-tips" class="floating_tips_computer">接收到0个文件</span><button id="floating-button" class="floating_button_computer">全部下载</button></div></div><div id="file-list" class="file_list_computer"></div>';
    }

    //请求成功处理
    requestSuccess(isMobile)
}

/**
 * 请求成功处理
 * @param isMobile 是否是移动端
 */
function requestSuccess(isMobile) {
    const nickName = json_data.nickName;
    const shareFileInfoList = json_data.shareFileInfoList;

    //排序
    shareFileInfoList.sort((a, b) => b.index - a.index);

    //设置昵称
    setShareInfo(nickName, shareFileInfoList);
    if (isMobile) {
        //设置文件列表
        setMobileFileList(shareFileInfoList);
        //设置悬浮信息
        setMobileFloatingInfo(shareFileInfoList);
    } else {
        //设置文件列表
        setComputerFileList(shareFileInfoList);
        //设置悬浮信息
        setComputerFloatingInfo(shareFileInfoList);
    }
}

/**
 * 设置悬浮信息
 * @param list 文件列表
 */
function setMobileFloatingInfo(list) {
    const floatingText = document.getElementById('floating-tips');
    const floatingButton = document.getElementById('floating-button');
    floatingText.textContent = '接收到' + list.length + '个文件';

    floatingButton.onclick = function () {
        for (let i = 0; i < list.length; i++) {
            var fileShareInfo = list[i];
            var fileName = fileShareInfo.fileName;
            var index = fileShareInfo.index;

            //下载文件
            downloadFile(getDownloadUrl(index), fileName)
        }
    }

    window.addEventListener('scroll', function () {
        var stickyContainer = document.querySelector('.file_share_mobile');
        var floatingHeader = document.querySelector('.floating_info_mobile');
        var stickyRect = stickyContainer.getBoundingClientRect();

        // 如果stickyContainer的顶部在视口内，则隐藏悬浮区域
        if (stickyRect.bottom >= 0 && stickyRect.bottom <= window.innerHeight) {
            floatingHeader.style.display = 'none';
        } else {
            // 否则显示悬浮区域
            floatingHeader.style.display = 'block';
        }
    });
}

/**
 * 设置悬浮信息
 * @param list 文件列表
 */
function setComputerFloatingInfo(list) {
    const floatingText = document.getElementById('floating-tips');
    const floatingButton = document.getElementById('floating-button');
    floatingText.textContent = '接收到' + list.length + '个文件';

    floatingButton.onclick = function () {
        for (let i = 0; i < list.length; i++) {
            var fileShareInfo = list[i];
            var fileName = fileShareInfo.fileName;
            var index = fileShareInfo.index;

            //下载文件
            downloadFile(getDownloadUrl(index), fileName)
        }
    }

    window.addEventListener('scroll', function () {
        var stickyContainer = document.querySelector('.file_share_computer');
        var floatingHeader = document.querySelector('.floating_info_computer');
        var stickyRect = stickyContainer.getBoundingClientRect();

        // 如果stickyContainer的顶部在视口内，则隐藏悬浮区域
        if (stickyRect.bottom >= 0 && stickyRect.bottom <= window.innerHeight) {
            floatingHeader.style.display = 'none';
        } else {
            // 否则显示悬浮区域
            floatingHeader.style.display = 'flex';
        }
    });
}

/**
 * 设置分享信息
 * @param name 昵称
 * @param list 文件列表
 */
function setShareInfo(name, list) {
    const shareInfo = document.getElementById('nick-name');
    const downloadButton = document.getElementById('download-all');
    const downloadAppButton = document.getElementById('download-app');
    shareInfo.innerHTML = name + '<br>向你发送' + list.length + '个文件';

    downloadButton.onclick = function () {
        for (let i = 0; i < list.length; i++) {
            var fileShareInfo = list[i];
            var fileName = fileShareInfo.fileName;
            var index = fileShareInfo.index;

            //下载文件
            downloadFile(getDownloadUrl(index), fileName)
        }
    }

    if (downloadAppButton !== null) {
        downloadAppButton.onclick = function () {
            var downloadUrl = requestUrl;
            if (downloadUrl.endsWith('/')) {
                downloadUrl = downloadUrl + 'app';
            } else {
                downloadUrl = downloadUrl + '/app';
            }

            downloadFile(downloadUrl, "NextTransfer.apk")
        }
    }
}

/**
 * 设置手机版文件列表
 * @param list 文件列表
 */
function setMobileFileList(list) {
    //获取文件列表容器
    const listContainer = document.getElementById('file-list');
    const fileNameWidth = (getWinWidth() - 174) + 'px';

    for (let i = 0; i < list.length; i++) {
        var fileShareInfo = list[i];
        var fileName = fileShareInfo.fileName;
        var fileSize = fileShareInfo.fileSize;
        var index = fileShareInfo.index;

        const newItem = document.createElement('div');
        newItem.className = 'file_info_container_mobile';

        const fileInfo = document.createElement('div');
        fileInfo.className = 'file_info_mobile';

        const img = document.createElement('img');
        img.src = getFileImage(fileName);
        fileInfo.appendChild(img);

        const fileInfoText = document.createElement('div');
        fileInfoText.className = 'file_info_text_mobile';

        const fileNameText = document.createElement('p');
        fileNameText.className = 'file_info_text_name_mobile';
        fileNameText.textContent = fileName;
        fileNameText.style.width = fileNameWidth;
        fileInfoText.appendChild(fileNameText);

        const fileSizeText = document.createElement('p');
        fileSizeText.className = 'file_info_text_size_mobile';
        fileSizeText.textContent = fileSize;
        fileInfoText.appendChild(fileSizeText);

        fileInfo.appendChild(fileInfoText);

        newItem.appendChild(fileInfo);

        const downloadButton = document.createElement('button');
        downloadButton.className = 'download_button_mobile';
        downloadButton.textContent = '下载';
        downloadButton.dataset.download = index;
        downloadButton.dataset.fileName = fileName;
        downloadButton.onclick = function () {
            downloadFile(getDownloadUrl(this.dataset.download, this.dataset.fileName))
        };

        newItem.appendChild(downloadButton);

        listContainer.appendChild(newItem);
    }
}

/**
 * 设置电脑版文件列表
 * @param list 文件列表
 */
function setComputerFileList(list) {
    //获取文件列表容器
    const listContainer = document.getElementById('file-list');
    const fileNameWidth = '500px';

    for (let i = 0; i < list.length; i++) {
        var fileShareInfo = list[i];
        var fileName = fileShareInfo.fileName;
        var fileSize = fileShareInfo.fileSize;
        var index = fileShareInfo.index;

        const newItem = document.createElement('div');
        newItem.className = 'file_info_container_computer';

        const fileInfo = document.createElement('div');
        fileInfo.className = 'file_info_computer';

        const img = document.createElement('img');
        img.src = getFileImage(fileName);
        fileInfo.appendChild(img);

        const fileInfoText = document.createElement('div');
        fileInfoText.className = 'file_info_text_computer';

        const fileNameText = document.createElement('p');
        fileNameText.className = 'file_info_text_name_computer';
        fileNameText.textContent = fileName;
        fileNameText.style.width = fileNameWidth;
        fileInfoText.appendChild(fileNameText);

        const fileSizeText = document.createElement('p');
        fileSizeText.className = 'file_info_text_size_computer';
        fileSizeText.textContent = fileSize;
        fileInfoText.appendChild(fileSizeText);

        fileInfo.appendChild(fileInfoText);

        newItem.appendChild(fileInfo);

        const downloadButton = document.createElement('button');
        downloadButton.className = 'download_button_computer';
        downloadButton.textContent = '下载';
        downloadButton.dataset.download = index;
        downloadButton.dataset.fileName = fileName;
        downloadButton.onclick = function () {
            downloadFile(getDownloadUrl(this.dataset.download, this.dataset.fileName))
        };

        newItem.appendChild(downloadButton);

        listContainer.appendChild(newItem);
    }
}

/**
 * 获取文件图标
 * @param fileName 文件名
 * @returns {string} 文件图标
 */
function getFileImage(fileName) {
    if (fileName.toLowerCase().endsWith('.apk')) {
        return 'image/ic_mobile_file_apk.svg';
    }

    if (fileName.toLowerCase().endsWith('.jpg') || fileName.toLowerCase().endsWith('.png') || fileName.toLowerCase().endsWith('.jpeg') || fileName.toLowerCase().endsWith('.gif') || fileName.toLowerCase().endsWith('.bmp') || fileName.toLowerCase().endsWith('.webp')) {
        return 'image/ic_mobile_file_image.svg';
    }

    if (fileName.toLowerCase().endsWith('.mp4') || fileName.toLowerCase().endsWith('.avi') || fileName.toLowerCase().endsWith('.mkv') || fileName.toLowerCase().endsWith('.mov') || fileName.toLowerCase().endsWith('.wmv') || fileName.toLowerCase().endsWith('.flv') || fileName.toLowerCase().endsWith('.rmvb') || fileName.toLowerCase().endsWith('.mpg') || fileName.toLowerCase().endsWith('.mpeg')) {
        return 'image/ic_mobile_file_video.svg';
    }

    if (fileName.toLowerCase().endsWith('.mp3') || fileName.toLowerCase().endsWith('.wav') || fileName.toLowerCase().endsWith('.wma') || fileName.toLowerCase().endsWith('.aac') || fileName.toLowerCase().endsWith('.flac') || fileName.toLowerCase().endsWith('.m4a') || fileName.toLowerCase().endsWith('.ogg') || fileName.toLowerCase().endsWith('.wma') || fileName.toLowerCase().endsWith('.amr')) {
        return 'image/ic_mobile_file_audio.svg';
    }

    if (fileName.toLowerCase().endsWith('.txt') || fileName.toLowerCase().endsWith('.doc') || fileName.toLowerCase().endsWith('.docx') || fileName.toLowerCase().endsWith('.xls') || fileName.toLowerCase().endsWith('.xlsx') || fileName.toLowerCase().endsWith('.ppt') || fileName.toLowerCase().endsWith('.pptx')) {
        return 'image/ic_mobile_file_document.svg';
    }

    if (fileName.toLowerCase().endsWith('.zip') || fileName.toLowerCase().endsWith('.rar') || fileName.toLowerCase().endsWith('.7z') || fileName.toLowerCase().endsWith('.tar') || fileName.toLowerCase().endsWith('.gz') || fileName.toLowerCase().endsWith('.bz2') || fileName.toLowerCase().endsWith('.xz') || fileName.toLowerCase().endsWith('.z') || fileName.toLowerCase().endsWith('.cab') || fileName.toLowerCase().endsWith('.iso') || fileName.toLowerCase().endsWith('.dmg')) {
        return 'image/ic_mobile_file_zip.svg';
    }

    return 'image/ic_mobile_file_other.svg';
}

/**
 * 下载文件
 * @param url 下载地址
 * @param fileName 文件名
 */
function downloadFile(url, fileName) {
    // 创建一个隐藏的a标签
    var a = document.createElement('a');
    a.href = url;
    a.download = fileName; // 设置下载后的文件名
    // 阻止a标签的默认跳转行为
    a.style.display = 'none';
    document.body.appendChild(a);
    // 触发点击事件
    a.click();
    // 然后移除a标签
    document.body.removeChild(a);
}

/**
 * 获取下载地址
 * @param index 文件索引
 * @returns {string} 下载地址
 */
function getDownloadUrl(index) {
    var downloadUrl = requestUrl;
    if (downloadUrl.endsWith('/')) {
        downloadUrl = downloadUrl + 'download/' + index;
    } else {
        downloadUrl = downloadUrl + '/download/' + index;
    }

    return downloadUrl;
}

/**
 * 判断是否是移动端
 * @returns {boolean} 是否是移动端
 */
function isMobile() {
    return win_width < 700;
}

/**
 * 获取窗口宽度
 * @returns {number} 窗口宽度
 */
function getWinWidth() {
    return window.innerWidth;
}