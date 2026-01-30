package indi.midreamsheep.vegetable.backend.features.file.domain.model;

import java.io.InputStream;

/**
 * 文件下载数据：包含文件元数据与对象流。
 *
 * @param file 文件对象元数据
 * @param inputStream 文件输入流（调用方负责关闭）
 */
public record FileDownloadData(
        FileObjectData file,
        InputStream inputStream
) {
}

