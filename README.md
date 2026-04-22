# [E0254]吃饭啦

## 项目简介

这是一个简单的 Android 二维码展示应用。

当前行为如下：
- 打开 App 后立即显示二维码
- 每次进入前台都会按当前时间重新生成二维码
- 二维码文本格式为 `工号|yyyy/M/d HH:mm:ss`
- 应用名称会按工号自动生成，例如 `[E0254]吃饭啦`

## 功能说明

应用首页为一个居中的白色卡片区域，默认包含：
- 一个二维码
- 一行工号文本，例如 `E0254`
- 一行明文内容，例如 `E0254|2026/4/22 09:48:58`

其中二维码的实际内容与明文内容一致，都是由“工号 + 当前打开时间”拼接而成。

## 可调整参数

| 参数名 | 默认值 | 在哪里改 | 本地覆盖方式 | GitHub Actions 覆盖方式 | 影响范围 | 备注 |
| --- | --- | --- | --- | --- | --- | --- |
| `qrLabel` | `E0254` | `gradle.properties` | `-PqrLabel=D6297` | 手动触发时填写 `qr_label` | 二维码前缀、页面工号、应用名称、artifact 名、release 名 | 推荐用于给不同工号单独打包 |
| `showQrDetails` | `true` | 不写死在文件中，Gradle 默认值在 `app/build.gradle` | `-PshowQrDetails=false` | 当前 **不支持** | 是否显示底部明文 `工号|时间` | 适合做“隐藏底部说明”的神秘版 |

### 改默认工号

编辑 `gradle.properties`：

```properties
qrLabel=E0254
```

例如改成：

```properties
qrLabel=D6297
```

### 隐藏底部明文

本地构建时追加参数：

```powershell
gradlew.bat assembleDebug -PshowQrDetails=false
```

说明：
- 这不会影响二维码真实内容
- 只是把页面底部的 `工号|时间` 文本隐藏掉
- 当前 GitHub Actions 没有暴露这个输入项，所以只能本地通过 Gradle 参数控制

## 本地打包

### 环境准备

请先准备好：
- JDK 11
- Android SDK Platform 29
- Android Build Tools 29.0.3
- 可用的 `ANDROID_HOME` 或 `ANDROID_SDK_ROOT`

### Windows 默认打包

```powershell
gradlew.bat assembleDebug
```

### Windows 指定工号打包

```powershell
gradlew.bat assembleDebug -PqrLabel=D6297
```

### Windows 指定工号并隐藏底部明文打包

```powershell
gradlew.bat assembleDebug -PqrLabel=D6297 -PshowQrDetails=false
```

### 其他平台示例

```bash
./gradlew assembleDebug
./gradlew assembleDebug -PqrLabel=D6297
./gradlew assembleDebug -PqrLabel=D6297 -PshowQrDetails=false
```

### 输出 APK 路径

构建完成后，APK 默认在：

```text
app/build/outputs/apk/debug/app-debug.apk
```

## GitHub Actions 打包

项目已经配置了 GitHub Actions 工作流：
- push 到 `master` / `main` 时会自动构建
- 支持手动触发
- 手动触发时可输入 `qr_label`，生成指定工号的包

### 手动触发可调项

当前 GitHub Actions 只支持手动输入以下参数：
- `qr_label`：临时二维码编号，留空时使用 `gradle.properties` 中的默认值

当前 GitHub Actions **不支持** 手动输入 `showQrDetails`。

### artifact 命名规则

Actions 构建产物会带上：
- 工号
- 版本号
- 日期时间

例如：

```text
[D6297]吃饭啦-v1.0-2026-04-22_14-20-00-Debug安装包
```

### Release 规则

GitHub Release 采用“按工号滚动覆盖”的方式：
- `D1616` 会发布到固定 tag：`build-d1616`
- `E0254` 会发布到固定 tag：`build-e0254`
- 同工号会覆盖旧 Release 附件和说明
- 不同工号互不影响

也就是说：
- 同一个工号不会越堆越多
- 不同工号可以各自保留一个“最新调试安装包”

## 环境要求

当前项目基于以下版本组合：
- JDK 11：用于 Gradle 构建
- Android SDK Platform 29
- Android Build Tools 29.0.3
- Gradle Wrapper 6.5
- Android Gradle Plugin 4.1.3

补充说明：
- 首次构建时，Gradle 可能会联网下载依赖和分发包
- 项目在 Windows 本地已验证可构建，但并不限定只能在 Windows 使用
- GitHub Actions 中会先使用 Java 17 处理 Android SDK 工具，再切回 Java 11 进行 Gradle 构建

## 项目结构

下面是当前项目的核心结构：

```text
.github/
  workflows/
    build-android.yml        # GitHub 自动打包与发布工作流
app/
  build.gradle               # App 模块构建配置，包含 qrLabel / showQrDetails / 版本号
  proguard-rules.pro         # Release 混淆规则（当前基本为空）
  src/main/
    AndroidManifest.xml      # 应用入口、图标、应用名配置
    java/com/example/qrrefresh/
      MainActivity.java      # 二维码生成、时间格式、页面刷新逻辑
    res/
      layout/activity_main.xml   # 首页布局
      mipmap-*/                  # 启动图标资源
      drawable/                  # 启动图标前景与背景资源
      values/                    # 文本、颜色、主题资源
gradle/
  wrapper/
    gradle-wrapper.jar
    gradle-wrapper.properties    # Gradle Wrapper 配置（当前为 6.5）
gradle.properties                # 默认工号等全局 Gradle 参数
build.gradle                     # 根项目构建脚本
settings.gradle                  # 项目模块声明
gradlew                          # macOS / Linux 构建入口
gradlew.bat                      # Windows 构建入口
```

## 常见修改入口

### 改默认工号
- `gradle.properties`

### 改版本号
- `app/build.gradle`
- 当前字段：`versionCode`、`versionName`

### 改页面布局
- `app/src/main/res/layout/activity_main.xml`

### 改二维码逻辑 / 时间格式
- `app/src/main/java/com/example/qrrefresh/MainActivity.java`
- 当前时间格式：`yyyy/M/d HH:mm:ss`

### 改应用图标
- `app/src/main/res/mipmap-*`
- `app/src/main/res/drawable/ic_launcher_foreground_image.png`

### 改 GitHub 打包规则
- `.github/workflows/build-android.yml`

## 当前实现备注

为了避免 README 和实际代码不一致，下面这些点特别说明：
- `qrLabel` 既支持本地 Gradle 参数覆盖，也支持 GitHub Actions 手动输入覆盖
- `showQrDetails` 目前只支持本地 Gradle 参数覆盖
- README 只描述当前已经实现的能力，没有额外扩展未上线的参数输入方式
