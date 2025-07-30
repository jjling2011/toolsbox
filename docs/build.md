### Build

```bash
gradlew build -Dhttp.proxyHost=127.0.0.1 -Dhttp.proxyPort=8080 -U --no-build-cache --no-daemon --parallel

# assets location:
# ./app/build/outputs/apk/debug/
# release 没有签名，无法安装
```