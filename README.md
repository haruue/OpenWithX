# OpenWithX

## Overview
Some applications (such as QQ) on Android has a `<data android:mimeType="*/*"/>` tag in their manifest so system will delegate all `ACTION_VIEW` to them. So that system will launch it by default when you open a file with "unknown" type (`*/*`). It is really annoying.

This application also has such a tag (but you can disable that), which makes system stop delegating the `ACTION_VIEW` on unknown to other application by default. And this application let you **share** the file which will be open, it is a useful feature for some applications (such as QQ) which will never use system share API. 

Please note that this application won't modified the data, if you want to convert the share data between `content://` uri and `file://` uri, please use [Bridge](https://play.google.com/store/apps/details?id=moe.shizuku.bridge). 


## License
GPLv3
