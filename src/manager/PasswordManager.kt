package manager

class PasswordManager {
    companion object {
        private lateinit var password: String

        fun update(newPassword: String) {
            password = newPassword
        }
    }
}