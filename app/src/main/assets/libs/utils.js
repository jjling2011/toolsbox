function CreateUtils() {
    function isAlphabet(str) {
        if (!str) {
            return true
        }
        return /^[a-z.A-Z 0-9']+$/.test(str)
    }

    function removeAlphabet(str) {
        if (!str) {
            return str
        }
        return str.replace(/[a-z.A-Z 0-9']/gm, "")
    }

    function keepLeadingAlphabet(str) {
        if (!str) {
            return ""
        }
        const m = str.match(/^[a-z.A-Z 0-9']+/)
        return m ? m[0] : ""
    }

    function marks(kws, str) {
        const max = Number.MAX_SAFE_INTEGER
        if (!str || !kws || kws.length < 1) {
            return max
        }
        const lower = str.toLowerCase()
        const chars = [...lower]
        const len_s = chars.length
        const len_kw = kws.length

        let r = 0
        let idx_r = 0
        let idx_kw = 0
        let match = 0
        while (idx_r < len_s && idx_kw < len_kw) {
            if (chars[idx_r] === kws[idx_kw]) {
                match++
                r += idx_r
                idx_kw++
            }
            idx_r++
        }
        if (match >= len_kw) {
            return r + (len_s - match)
        }
        return max
    }

    return {
        // f(str)
        isAlphabet,

        // f(str)
        keepLeadingAlphabet,

        // f(kws, str)
        marks,

        // f(str)
        removeAlphabet,
    }
}

window.utils = CreateUtils()
