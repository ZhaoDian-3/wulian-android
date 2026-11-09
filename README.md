# 五练 · Android

安卓原生 Java App（不是 WebView / 网页快捷方式），配套账号同步服务。

## 1.1.0 更新

- 训练页每个动作新增「编辑动作」；点击动作下方注释也可进入编辑。
- 可修改名称、分类、注释（支持多行与清空）。默认同时更新计划，也可仅修改本次训练。
- 保留已录入的组数、重量、次数、完成状态和其他历史记录。
- 计划编辑页显示注释和清晰字段标签。
- 使用原签名覆盖安装即可升级，请勿卸载旧版。

## 使用

安装签名 APK（Android 8.0+）。可以先离线记录，也可从「我的」使用 ChatGPT 账号登录：打开浏览器核对手机验证码并授权，返回 App 点「已授权，完成登录」。每位用户使用自己的账号。

- 胸、背、肩、腿、手臂五日计划；原始含糊数字保留备注。
- 日期、组数、重量、次数、打卡、训练感受、历史记录。
- 原生休息计时；回到前台时按实际时钟更新。
- 登录后在 App 打开期间自动同步，支持手动同步；离线修改排队。
- 用户数据按账号隔离，授权 token 和本机数据使用 Android Keystore AES-GCM 加密。
- 同一条记录并发修改时保留本机副本及云端版本，避免静默覆盖。
- 分享计划生成随机分享码；别人登录后导入独立副本；原作者可撤销分享码。
- 每个训练日提供谭成义 B站视频跳转，优先 B站 App，缺失时使用浏览器。
- 本机备份可通过 Android 文档选择器导出 JSON。备份不含授权凭证。

## 视频资料

仅保存外链，不下载或转载视频。视频讲解不保证与用户提供的动作逐项相同。

| 部位 | 视频 |
| --- | --- |
| 胸 | https://www.bilibili.com/video/BV1fa6CBDEEu/ |
| 背 | https://www.bilibili.com/video/BV1HR7o6CE8q/ |
| 肩 | https://www.bilibili.com/video/BV1csj36CEf9/ |
| 腿 | https://www.bilibili.com/video/BV1RjTT6FEKo/ |
| 手臂 | https://www.bilibili.com/video/BV1GzEg6wEVb/ |

胸、背、腿使用 B站搜索结果中的作者/标题；肩及手臂 BV 号通过公开私教系列索引交叉核对。B站对服务器直连返回 412，因此未在设备上验证视频播放，App 可直接通过 B站打开。

## 本地编译 APK

需要 JDK 17、Android SDK platform 35 / build-tools 35.0.0、zip。

```bash
export ANDROID_SDK_ROOT=/your/android-sdk
export WULIAN_KEYSTORE=/private/path/wulian-release.jks
# WULIAN_SIGN_PASSWORD 从受保护的环境输入，切勿提交到仓库。
bash android/build.sh
```

不设置签名变量时只生成未签名中间包。输出 `android/build/wulian-1.1.0.apk`。签名密钥必须与后续更新保持一致；项目仓库不存放私钥、密码或任何用户授权凭证。

## 云端

Vinext / Cloudflare D1 托管服务。`lib/service.mjs` 实现原生设备授权、条目版本同步及计划分享。身份源为托管平台的 Sign in with ChatGPT。设备授权只授予随机的一次发起手机凭证，浏览器显式确认且校验同源与 CSRF。API 不接受客户端自报用户 ID。

数据库迁移在 `drizzle/`；表结构在 `db/schema.ts`。公开的是 App 的登录和 API 入口，训练数据必须持有各自账号的有效 token 才可读写。

```bash
node --experimental-sqlite tests/service.test.mjs
node node_modules/typescript/bin/tsc --noEmit
npm run build
```

已覆盖：设备授权、CSRF、重试、不同用户数据隔离、并发版本冲突、仅分享计划、分享码作者撤销权限、数据验证、退出后令牌失效。

## 已知限制

- 尚未上架应用商店，按 APK 侧载安装；未完成真实安卓设备上的端到端验收。
- 已发布服务的浏览器健康检查通过，未授权记录请求被拒绝；当前终端的直连请求被 Cloudflare 1010 拒绝，因此未能从此环境完成原生客户端的线上同步验收。手机上的网络结果需要实机确认。
- 没有独立用户名/密码体系，每位使用云同步的人需要 ChatGPT 账号并能访问登录及云服务。
- 云服务可用性依赖网络和托管平台。服务维护者可维护数据库，不是端到端加密。
- 离线更改仅在 App 打开并联网时同步，不运行后台常驻服务。
- 计划分享只分享计划快照；撤销不会抹除接收者已导入副本。
