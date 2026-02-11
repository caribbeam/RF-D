package com.warehouse.rfid.printer

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.OutputStream
import java.util.*

/**
 * Barkod Yazıcı Yöneticisi
 * Zebra, TSC ve diğer ZPL/TSPL destekli yazıcılar için
 */
class PrinterManager(private val context: Context) {
    
    private val TAG = "PrinterManager"
    private val SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null
    
    var isConnected = false
        private set
    
    init {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    }
    
    /**
     * Eşleştirilmiş yazıcıları listele
     */
    fun getPairedPrinters(): List<BluetoothDevice> {
        return try {
            bluetoothAdapter?.bondedDevices?.filter { device ->
                device.name?.contains("Zebra", ignoreCase = true) == true ||
                device.name?.contains("TSC", ignoreCase = true) == true ||
                device.name?.contains("Printer", ignoreCase = true) == true
            } ?: emptyList()
        } catch (e: SecurityException) {
            Log.e(TAG, "Bluetooth izni gerekli: ${e.message}")
            emptyList()
        }
    }
    
    /**
     * Yazıcıya bağlan
     */
    suspend fun connect(device: BluetoothDevice): Boolean = withContext(Dispatchers.IO) {
        try {
            disconnect()
            
            bluetoothSocket = device.createRfcommSocketToServiceRecord(SPP_UUID)
            bluetoothSocket?.connect()
            outputStream = bluetoothSocket?.outputStream
            
            isConnected = true
            Log.d(TAG, "Yazıcıya bağlandı: ${device.name}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Bağlantı hatası: ${e.message}")
            disconnect()
            false
        }
    }
    
    /**
     * Bağlantıyı kes
     */
    fun disconnect() {
        try {
            outputStream?.close()
            bluetoothSocket?.close()
            isConnected = false
            Log.d(TAG, "Yazıcı bağlantısı kesildi")
        } catch (e: IOException) {
            Log.e(TAG, "Bağlantı kesme hatası: ${e.message}")
        }
    }
    
    /**
     * Barkod yazdır (ZPL formatı - Zebra yazıcılar için)
     */
    suspend fun printBarcodeZPL(
        barcode: String,
        productName: String,
        quantity: Int = 1
    ): Boolean = withContext(Dispatchers.IO) {
        if (!isConnected) {
            Log.e(TAG, "Yazıcı bağlı değil")
            return@withContext false
        }
        
        try {
            val zplCommand = buildZPLCommand(barcode, productName)
            
            repeat(quantity) {
                outputStream?.write(zplCommand.toByteArray())
                outputStream?.flush()
            }
            
            Log.d(TAG, "Barkod yazdırıldı: $barcode")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Yazdırma hatası: ${e.message}")
            false
        }
    }
    
    /**
     * Barkod yazdır (TSPL formatı - TSC yazıcılar için)
     */
    suspend fun printBarcodeTSPL(
        barcode: String,
        productName: String,
        quantity: Int = 1
    ): Boolean = withContext(Dispatchers.IO) {
        if (!isConnected) {
            Log.e(TAG, "Yazıcı bağlı değil")
            return@withContext false
        }
        
        try {
            val tsplCommand = buildTSPLCommand(barcode, productName)
            
            repeat(quantity) {
                outputStream?.write(tsplCommand.toByteArray())
                outputStream?.flush()
            }
            
            Log.d(TAG, "Barkod yazdırıldı: $barcode")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Yazdırma hatası: ${e.message}")
            false
        }
    }
    
    /**
     * Test yazdırma
     */
    suspend fun printTest(): Boolean = withContext(Dispatchers.IO) {
        if (!isConnected) return@withContext false
        
        try {
            val testCommand = """
                ^XA
                ^FO50,50^ADN,36,20^FDTEST PRINT^FS
                ^FO50,100^ADN,36,20^FDDepo RFID Sistemi^FS
                ^FO50,150^BY3^BCN,100,Y,N,N^FD123456789^FS
                ^XZ
            """.trimIndent()
            
            outputStream?.write(testCommand.toByteArray())
            outputStream?.flush()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Test yazdırma hatası: ${e.message}")
            false
        }
    }
    
    /**
     * ZPL komutu oluştur (Zebra)
     */
    private fun buildZPLCommand(barcode: String, productName: String): String {
        return """
            ^XA
            ^FO50,30^ADN,36,20^FD$productName^FS
            ^FO50,80^BY3^BCN,100,Y,N,N^FD$barcode^FS
            ^FO50,200^ADN,24,12^FD$barcode^FS
            ^XZ
        """.trimIndent()
    }
    
    /**
     * TSPL komutu oluştur (TSC)
     */
    private fun buildTSPLCommand(barcode: String, productName: String): String {
        return """
            SIZE 60 mm, 40 mm
            GAP 2 mm, 0 mm
            DIRECTION 1
            REFERENCE 0,0
            OFFSET 0 mm
            SET PEEL OFF
            SET CUTTER OFF
            SET PARTIAL_CUTTER OFF
            SET TEAR ON
            CLS
            TEXT 50,30,"3",0,1,1,"$productName"
            BARCODE 50,80,"128",100,1,0,2,2,"$barcode"
            TEXT 50,200,"2",0,1,1,"$barcode"
            PRINT 1,1
        """.trimIndent()
    }
    
    /**
     * RFID etiket yazdır (RFID destekli yazıcılar için)
     */
    suspend fun printRFIDLabel(
        rfidTag: String,
        barcode: String,
        productName: String
    ): Boolean = withContext(Dispatchers.IO) {
        if (!isConnected) return@withContext false
        
        try {
            // RFID yazma komutu (Zebra RFID yazıcılar için)
            val rfidCommand = """
                ^XA
                ^RFW,H,$rfidTag^FS
                ^FO50,30^ADN,36,20^FD$productName^FS
                ^FO50,80^BY3^BCN,100,Y,N,N^FD$barcode^FS
                ^FO50,200^ADN,24,12^FDRFID: $rfidTag^FS
                ^XZ
            """.trimIndent()
            
            outputStream?.write(rfidCommand.toByteArray())
            outputStream?.flush()
            true
        } catch (e: Exception) {
            Log.e(TAG, "RFID etiket yazdırma hatası: ${e.message}")
            false
        }
    }
}

/**
 * Yazıcı tipi
 */
enum class PrinterType {
    ZEBRA,  // ZPL
    TSC,    // TSPL
    AUTO    // Otomatik algılama
}
