package com.viatom.littlePu.er2.bean

data class FileBean(
    var fileName: String = "",
    var fileData: ByteArray,
    var fileSize: Int
)
