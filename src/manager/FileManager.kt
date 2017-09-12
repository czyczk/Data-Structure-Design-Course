package manager

import com.google.gson.GsonBuilder
import model.scenicArea.Route
import model.scenicArea.Spot
import model.scenicArea.Vector
import util.Util
import java.io.*
import java.nio.file.Files
import java.nio.file.Paths

class FileManager {
    companion object {
        private val directory = "data"
        private val spotFilename = directory + "/spot.json"
        private val routeFilename = directory + "/route.json"
        private val passwordFilename = directory + "/password.txt"
        private val gson = GsonBuilder().setPrettyPrinting().create()

        /**
         * 读取所有数据，包含景点、线路和密码。
         */
        fun loadAll() {
            loadAllSpots()
            loadAllRoutes()
            loadPassword()
        }

        // 读取所有景点
        private fun loadAllSpots() {
            // 文件不存在则退出
            val file = File(spotFilename)
            if (!file.exists())
                return
            // 读取
            val reader = file.bufferedReader()
            SpotManager.spotMap =
                    gson.fromJson(reader, Util.genericType<MutableMap<String, Spot>>())
            reader.close()
        }

        // 读取所有线路
        private fun loadAllRoutes() {
            // 文件不存在则退出
            val file = File(routeFilename)
            if (!file.exists())
                return
            // 读取
            val reader = file.bufferedReader()
            val tempRouteMap: MutableMap<String, List<Vector>>
            // (fuck generic type erasure)
            tempRouteMap = gson.fromJson(reader, Util.genericType<MutableMap<String, List<Vector>>>())
            tempRouteMap.forEach { t, u ->
                u.forEach {
                    val route = Route(t, it.destination, it.distance)
                    RouteManager.add(route, true)
                }
            }
            reader.close()
        }

        // 读取密码
        private fun loadPassword() {
            // 文件不存在则退出
            if (!File(passwordFilename).exists())
                return
            // 读取
            val reader = BufferedReader(FileReader(passwordFilename))
            val password = reader.readText()
            reader.close()
            PasswordManager.update(password)
        }

        /**
         * 保存所有景点
         */
        fun saveAllSpots() {
            saveTextToFile(gson.toJson(SpotManager.spotMap), spotFilename)
        }

        /**
         * 保存所有线路
         */
        fun saveAllRoutes() {
            // 整理所有线路为不含重复线路的 map<景点名, 非链表列表>
            val tempRouteMap = mutableMapOf<String, List<Vector>>()
            RouteManager.routeMap.forEach { t, u ->
                val tempList = mutableListOf<Vector>()
                u.forEach {
                    if (!tempRouteMap.containsKey(it.destination))
                        tempList.add(it)
                }
                tempRouteMap.put(t, tempList)
            }

            saveTextToFile(gson.toJson(tempRouteMap), routeFilename)
        }

        /**
         * 保存密码
         */
        fun savePassword() {
            saveTextToFile(PasswordManager.password, passwordFilename)
        }

        // 保存字符串至文件的流程
        private fun saveTextToFile(str: String, path: String) {
            createDirectoryIfNotExists()

            // 文件不存在则创建文件
            val file = File(path)
            if (!file.exists())
                file.createNewFile()

            // 获取 PrintWriter 写入字符串至文件
            val writer = file.printWriter()
            writer.print(str)
            writer.close()
        }

        // 若所需的数据文件夹不存在则创建一个
        private fun createDirectoryIfNotExists() {
            val path = Paths.get(directory)
            if (!Files.exists(path))
                Files.createDirectories(path)
        }
    }
}