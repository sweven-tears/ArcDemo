package pers.sweven.arcdemo

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.jvm.Throws

/**
 * Created by Sweven on 2020/8/21--17:04.
 */
object Test {
    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        //先模拟一个图形byte[]
        val b1 = image2Bytes("d:\\1.png")
        //存为文件
        buff2Image(b1, "d:\\test.png")
        println("Hello World!")
    }

    @Throws(Exception::class)
    fun buff2Image(b: ByteArray?, tagSrc: String?) {
        val fout = FileOutputStream(tagSrc)
        //将字节写入文件
        fout.write(b)
        fout.close()
    }

    @Throws(Exception::class)
    fun image2Bytes(imgSrc: String?): ByteArray {
        val fin = FileInputStream(File(imgSrc))
        //可能溢出,简单起见就不考虑太多,如果太大就要另外想办法，比如一次传入固定长度byte[]
        val bytes = ByteArray(fin.available())
        //将文件内容写入字节数组，提供测试的case
        fin.read(bytes)
        fin.close()
        return bytes
    }
}