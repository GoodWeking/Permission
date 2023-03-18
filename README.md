# Permision 权限申请封装，页面跳转封装
目前第一版封装了：页面启动、页面启动回调、获取通讯录联系人、获取相册图片、拍照获取图片、单条权限申请、多条权限申请，且已适配到Android 13

### 导入
```java
    maven { url 'https://jitpack.io' }
```
```java
   implementation 'com.github.GoodWeking:Permission:v1.0'
```

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
           AlertDialog.Builder(context)
            .setMessage(resources.getString(R.string.register_permission_dialog_msg))
            .setPositiveButton(resources.getString(R.string.register_permission_dialog_confirm)) { _, _ ->
                openSettingsPermission()
            }
            .setNeutralButton(
                resources.getString(R.string.register_permission_dialog_cancel),
                null
            ).show()
          }
   ) {
             //权限申请结果   
             
}

```

```java
launchPermission(permissions = android.Manifest.permission.ACCESS_FINE_LOCATION,
                tipDialog = {
                    //自定义提示
                }) {
                Toast.makeText(this, "是否成功：$it", Toast.LENGTH_SHORT).show()
    }
```
跳转设置页
```java
fun Context.openSettingsPermission() {
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.parse("package:$packageName")
        startActivity(this)
    }
}
```

### 选择联系人
目前只封装了获取单个联系人以兼容到 api 33，在调用时请添加权限到清单文件 `Manifest.permission.READ_CONTACTS`
```java
launchContact {
    //it.name 联系人姓名
    //it.phone 联系人电话
    //it.saveDate 保存时间
    //it.contactId 联系人id
}
```

### Activity 跳转
```java
launch<ContentActivity>("value1" to "111", "value2" to 222)

launchForResult<ContentActivity>("customer" to "mainActivity") { rsultCode: Int, data: Intent? ->
              //回传值回调     
  }
```

### 获取图片
相册获取选择结果都是存放在sd的下载目录，需要再清单文件添加文件读写权限Android13以下`Manifest.permission.WRITE_EXTERNAL_STORAGE`以上`Manifest.permission.READ_MEDIA_IMAGES`

path=Pair<String,String>
first：存放文件夹
second：文件名

```java
    //相册获取 
launchPic("camera" to ("${System.currentTimeMillis()}.jpg")) {
                ivImage.setImageURI(it.uri)
}
```
              
相机获取需要再清单文件添加权限Android13以上`Manifest.permission.READ_MEDIA_IMAGES`,`Manifest.permission.CAMERA`以下`Manifest.permission.WRITE_EXTERNAL_STORAGE`,`Manifest.permission.CAMERA`
```java
launchCamera("camera" to ("${System.currentTimeMillis()}.jpg")) {
     //it.uri 
     //it.file 从uri转出的图片
}
```
