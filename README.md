# Permision
目前第一版封装了：页面启动、页面启动回调、获取通讯录联系人、获取相册图片、拍照获取图片、单条权限申请、多条权限申请，且已适配到Android 13


### 权限申请
权限申请支持单条权限和多条权限申请，支持强制拒绝后弹窗提示用户并引导区设置也开启权限
```java
//请求单条权限
launchPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) {
      Toast.makeText(this, "是否成功：$it", Toast.LENGTH_SHORT).show()
}
```
```java
//多条权限申请
launchPermission(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,  android.Manifest.permission.READ_SMS)) {
                Toast.makeText(this, "是否成功：$it", Toast.LENGTH_SHORT).show()
}
```
默认强制拒绝弹窗提示
```java
 AlertDialog.Builder(context)
            .setMessage(resources.getString(R.string.register_permission_dialog_msg))
            .setPositiveButton(resources.getString(R.string.register_permission_dialog_confirm)) { _, _ ->
                openSettingsPermission()
            }
            .setNeutralButton(
                resources.getString(R.string.register_permission_dialog_cancel),
                null
            ).show()
```
<img width="353" alt="image" src="https://user-images.githubusercontent.com/106650697/225846135-73860917-d662-44a1-8936-c3f7d44eb7bf.png">
支持自定义

```java
 launchPermission(
      permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.READ_SMS),
      tipDialog = {
                    //自定义提示引导
          }
   ) {
             //权限申请结果   
             
}
```

### 选择联系人
目前只封装了获取单个联系人以兼容到 api 33，在调用时请添加权限到清单文件 `Manifest.permission.READ_CONTACTS`
