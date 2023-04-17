package com.goodPermission.mode

/**
 * @Time 2023/3/17 09:56:47
 * @Description
 **/
data class ContactMode(
    var name: String? = null,
    var phone: String? = null,
    var saveDate: String? = null,
    var contactId: String? = null,
) {
    //是否获取异常，再toContactPick方法因为没有联系人读取权限所有有些厂商改了Formwork强制验证权限而爆异常
    var isError: Boolean = false
}