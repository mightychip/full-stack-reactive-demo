const proxy = require("http-proxy-middleware")
module.exports = app => {
    app.use(proxy("/ws", {target: "http://webflux-back-end:8080", ws: true}))
    // app.use(proxy("/ws", {target: "http://localhost:8080", ws: true}))
}