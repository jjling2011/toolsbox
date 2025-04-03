function CreatePager(divId, pageButtonNum, onClickCallBack) {
    const container = $(`#${divId}`)
    let showN = pageButtonNum
    let lbPageNum
    const onClick = onClickCallBack

    function goto(cur, last) {
        const end = Math.max(1, last)
        const c = Math.min(Math.max(1, cur), end)
        onClick(c, end)
        draw(c, end)
    }

    function createBtn(tag, idx, last) {
        const btn = $("<button>")
        btn.text(tag)
        btn.on("click", () => goto(idx, last))
        return btn
    }

    function draw(curPage, lastPage) {
        const c = container

        c.empty()
        if (lastPage < 2) {
            return
        }

        const start = Math.max(curPage - Math.floor(showN / 2), 1)
        const end = Math.min(start + showN - 1, lastPage)

        if (start !== 1) {
            const firstBtn = createBtn("<", 1, lastPage)
            c.append(firstBtn)
            c.append($("<span>...</span>"))
        }

        for (let index = start; index <= end; index++) {
            const idx = index
            const btn = createBtn(`${idx}`, idx, lastPage)
            if (idx === curPage) {
                btn.addClass("active")
            }
            container.append(btn)
        }
        if (end !== lastPage) {
            c.append($("<span>...</span>"))
            const lastBtn = createBtn(">", lastPage, lastPage)
            c.append(lastBtn)
        }

        const s = $("<div>")
        lbPageNum = $('<input type="text">')

        const btnJump = $("<button>Go</button>")

        btnJump.on("click", () => {
            const pn = parseInt(lbPageNum.val()) || 1
            goto(pn, lastPage)
            setTimeout(() => {
                lbPageNum && lbPageNum.focus()
            }, 200)
        })

        lbPageNum.keydown((evt) => {
            if (evt.key === "Enter") {
                btnJump.trigger("click")
                setTimeout(() => {
                    lbPageNum && lbPageNum.focus()
                }, 200)
            }
        })

        s.append(lbPageNum)
        s.append(btnJump)
        c.append(s)
    }

    return {
        goto,
    }
}
