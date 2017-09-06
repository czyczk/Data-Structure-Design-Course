package main

import manager.FileManager
import ui.LoginPage

class Application {
    companion object {
        fun start() {
            FileManager.loadAll()
            LoginPage.showMenu()
        }
    }
}