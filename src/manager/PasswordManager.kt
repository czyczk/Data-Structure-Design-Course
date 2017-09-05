package manager

import util.UiUtil

class PasswordManager {
    companion object {
        var password = UiUtil.getString("defaultPassword")

        fun update(newPassword: String) {
            password = newPassword
        }
    }
}