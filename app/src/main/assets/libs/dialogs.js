function CreateDialogs() {
    let confirmResolve
    let confirmReject
    let promptResolve
    let promptReject
    let selectResolve
    let selectReject

    injectHtml()

    $("#dialog-alert-ok").on("click", () => $("#dialog-alert").hide())
    $("#dialog-confirm-ok").on(
        "click",
        () => confirmResolve && confirmResolve(),
    )
    $("#dialog-confirm-cancel").on(
        "click",
        () => confirmReject && confirmReject(),
    )
    $("#dialog-prompt-ok").on("click", () => promptResolve && promptResolve())
    $("#dialog-prompt-cancel").on("click", () => promptReject && promptReject())
    $("#dialog-select-ok").on("click", () => selectResolve && selectResolve())
    $("#dialog-select-cancel").on("click", () => selectReject && selectReject())

    function injectHtml() {
        const html = `
    <div id="dialog-loading">
        <div id="dialog-loading-window">
            <pre class="dialog-content" id="dialog-loading-content"></pre>
            <div class="dialog-content" id="dialog-loading-img-container">
            </div>
        </div>
    </div>
    <div id="dialog-prompt">
        <div id="dialog-prompt-window">
            <pre class="dialog-content" id="dialog-prompt-title"></pre>
            <div class="dialog-content">
                <input type="text" id="dialog-prompt-content" />
            </div>
            <div class="dialog-content">
                <button id="dialog-prompt-ok">确定</button>
                <button id="dialog-prompt-cancel">取消</button>
            </div>
        </div>
    </div>
    <div id="dialog-confirm">
        <div id="dialog-confirm-window">
            <pre class="dialog-content" id="dialog-confirm-content"></pre>
            <div class="dialog-content">
                <button id="dialog-confirm-ok">确定</button>
                <button id="dialog-confirm-cancel">取消</button>
            </div>
        </div>
    </div>
    <div id="dialog-alert">
        <div id="dialog-alert-window">
            <pre class="dialog-content" id="dialog-alert-content"></pre>
            <div class="dialog-content">
                <button id="dialog-alert-ok">确定</button>
            </div>
        </div>
    </div>
    <div id="dialog-select">
        <div id="dialog-select-window">
            <pre class="dialog-content" id="dialog-select-title"></pre>
            <select class="dialog-content" id="dialog-select-content"></select>
            <div class="dialog-content">
                <button id="dialog-select-ok">确定</button>
                <button id="dialog-select-cancel">取消</button>
            </div>
        </div>
    </div>
`
        $("body").append(html)
    }

    async function select(title, list) {
        $("#dialog-select-title").text(title)
        const sel = $("#dialog-select-content")
        sel.empty()
        for (let v of list) {
            const option = $("<option>")
            option.val(v)
            option.text(v)
            sel.append(option)
        }

        const p = new Promise((resolve, reject) => {
            selectResolve = resolve
            selectReject = reject
        })
        const dialog = $("#dialog-select")
        dialog.show()
        try {
            await p
            const r = $("#dialog-select-content").val()
            return r
        } catch {
        } finally {
            dialog.hide()
        }
        return null
    }

    async function prompt(title, content) {
        const dialog = $("#dialog-prompt")
        const p = new Promise((resolve, reject) => {
            promptResolve = resolve
            promptReject = reject
        })
        $("#dialog-prompt-title").text(title)
        $("#dialog-prompt-content").val(content)
        dialog.show()
        try {
            await p
            const s = $("#dialog-prompt-content").val()
            return s
        } catch {
        } finally {
            dialog.hide()
        }
        return null
    }

    async function confirm(content) {
        const dialog = $("#dialog-confirm")
        const p = new Promise((resolve, reject) => {
            confirmResolve = resolve
            confirmReject = reject
        })
        $("#dialog-confirm-content").text(content)
        dialog.show()
        try {
            await p
            return true
        } catch {
        } finally {
            dialog.hide()
        }
        return false
    }

    function loading(title, donePromise, imgSrc) {
        $("#dialog-loading-content").text(title)
        if (imgSrc) {
            const c = $("#dialog-loading-img-container")
            c.empty()
            const img = $("<img>")
            img.attr("src", imgSrc)
            c.append(img)
        }
        const loading = $("#dialog-loading")
        loading.show()
        donePromise.finally(() => loading.hide())
    }

    function alert(content) {
        $("#dialog-alert-content").text(content)
        $("#dialog-alert").show()
    }

    return {
        loading,
        alert,
        confirm,
        select,
        prompt,
    }
}
