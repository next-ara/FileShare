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
        requestUrl = requestUrl + 'info';
    } else {
        requestUrl = requestUrl + '/info';
    }

    const xhr = new XMLHttpRequest();

    //设置请求方法
    xhr.open('GET', requestUrl, true);

    xhr.onload = function () {
        if (xhr.status === 200) {
            //设置body
            setBody(isMobile);
            requestSuccess(xhr.responseText)
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
    document.body.innerHTML = '<div class="app-download"><div class="app-download-left"><img src="image/ic_app.svg"><div class="app-info"><p class="app-name">Next快传</p><p class="app-des">简洁高效的局域网快传软件</p></div></div><button id="download-app" class="app-download-btn">下载APP</button></div><div class="file-share"><div class="file-share-info"><img src="image/ic_file_share.svg"><p id="nick-name">未知设备<br>向你发送0个文件</p></div><button id="download-all">全部下载</button></div><div class="floating-info"><span id="floating-tips" class="floating-tips">接收到0个文件</span><button id="floating-button" class="floating-button">全部下载</button></div><div id="file-list"></div>';
}

/**
 * 请求成功处理
 * @param data 数据
 */
function requestSuccess(data) {
    //获取数据
    var json = JSON.parse(data);

    const nickName = json.nickName;
    const shareFileInfoList = json.shareFileInfoList;

    //排序
    shareFileInfoList.sort((a, b) => b.index - a.index);

    //设置昵称
    setShareInfo(nickName, shareFileInfoList);
    //设置文件列表
    setFileList(shareFileInfoList);
    //设置悬浮信息
    setFloatingInfo(shareFileInfoList);
}

/**
 * 设置悬浮信息
 * @param list 文件列表
 */
function setFloatingInfo(list) {
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
        var stickyContainer = document.querySelector('.file-share');
        var floatingHeader = document.querySelector('.floating-info');
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

    downloadAppButton.onclick = function () {
        var downloadUrl = window.location.href;
        if (downloadUrl.endsWith('/')) {
            downloadUrl = downloadUrl + 'app';
        } else {
            downloadUrl = downloadUrl + '/app';
        }

        downloadFile(downloadUrl, "NextTransfer.apk")
    }
}

/**
 * 设置文件列表
 * @param list 文件列表
 */
function setFileList(list) {
    //获取文件列表容器
    const listContainer = document.getElementById('file-list');
    const fileNameWidth = (window.screen.width - 174) + 'px';

    for (let i = 0; i < list.length; i++) {
        var fileShareInfo = list[i];
        var fileName = fileShareInfo.fileName;
        var fileSize = fileShareInfo.fileSize;
        var index = fileShareInfo.index;

        const newItem = document.createElement('div');
        newItem.className = 'file-info-container';

        const fileInfo = document.createElement('div');
        fileInfo.className = 'file-info';

        const img = document.createElement('img');
        img.src = getFileImage(fileName);
        fileInfo.appendChild(img);

        const fileInfoText = document.createElement('div');
        fileInfoText.className = 'file-info-text';

        const fileNameText = document.createElement('p');
        fileNameText.className = 'file-info-text-name';
        fileNameText.textContent = fileName;
        fileNameText.style.width = fileNameWidth;
        fileInfoText.appendChild(fileNameText);

        const fileSizeText = document.createElement('p');
        fileSizeText.className = 'file-info-text-size';
        fileSizeText.textContent = fileSize;
        fileInfoText.appendChild(fileSizeText);

        fileInfo.appendChild(fileInfoText);

        newItem.appendChild(fileInfo);

        const downloadButton = document.createElement('button');
        downloadButton.className = 'download-button';
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
    var downloadUrl = window.location.href;
    if (downloadUrl.endsWith('/')) {
        downloadUrl = downloadUrl + 'download/' + index;
    } else {
        downloadUrl = downloadUrl + '/download/' + index;
    }

    return downloadUrl;
}