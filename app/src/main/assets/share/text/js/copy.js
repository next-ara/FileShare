/**
 * 复制按钮点击事件
 */
function copyClick() {
    if (copyToClipboard(getContent())) {
        alert('已复制到剪贴板')
    } else {
        alert('复制失败')
    }
}

/**
 * 复制文本到剪贴板
 * @param content 文本内容
 * @returns {boolean} true/false
 */
function copyToClipboard(content) {
    if (navigator.clipboard && window.isSecureContext) {
        navigator.clipboard.writeText(content).then(() => {
            return true
        }).catch(err => {
            return false
        })
    } else {
        let input = document.createElement('input')
        input.style.position = 'fixed'
        input.style.top = '-10000px'
        input.style.zIndex = '-999'
        document.body.appendChild(input)
        input.value = content
        input.focus()
        input.select()
        try {
            let result = document.execCommand('copy')
            document.body.removeChild(input)
            if (!result || result === 'unsuccessful') {
                return false;
            } else {
                return true;
            }
        } catch (e) {
            document.body.removeChild(input)
            return false;
        }
    }

    return true
}

/**
 * 获取文本内容
 * @returns {string} 文本内容
 */
function getContent() {
    return document.getElementById('text').innerText
}