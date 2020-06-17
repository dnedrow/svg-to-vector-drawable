package com.vector.svg2vectorandroid

import com.android.ide.common.vectordrawable.Svg2Vector
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.CopyOption
import java.nio.file.FileAlreadyExistsException
import java.nio.file.FileVisitOption
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.BasicFileAttributes
import java.util.*


class SvgFilesProcessor @JvmOverloads constructor(
        sourceSvgDirectory: String,
        destinationVectorDirectory: String?,
        private val prefix: String = "ic_",
        private val extension: String = "xml",
        private val extensionSuffix: String = ""
) {
    private val sourceSvgPath: Path = Paths.get(sourceSvgDirectory)
    private val destinationVectorPath: Path =
            Paths.get(destinationVectorDirectory ?: "$sourceSvgDirectory/ProcessedSVG")

    fun process() {
        try {
            val options = EnumSet.of(FileVisitOption.FOLLOW_LINKS)
            //check first if source is a directory
            if (Files.isDirectory(sourceSvgPath)) {
                Files.walkFileTree(
                        sourceSvgPath,
                        options,
                        Int.MAX_VALUE,
                        object : FileVisitor<Path> {
                            override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
                                return FileVisitResult.CONTINUE
                            }

                            override fun preVisitDirectory(
                                    dir: Path,
                                    attrs: BasicFileAttributes
                            ): FileVisitResult {
                                // Skip folder which is processing svgs to xml
                                if (dir == destinationVectorPath) {
                                    return FileVisitResult.SKIP_SUBTREE
                                }
                                val opt = arrayOf<CopyOption>(StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING)
                                val newDirectory = destinationVectorPath.resolve(sourceSvgPath.relativize(dir))
                                try {
                                    Files.copy(dir, newDirectory, *opt)
                                } catch (ex: FileAlreadyExistsException) {
                                    println("FileAlreadyExistsException $ex")
                                } catch (x: IOException) {
                                    return FileVisitResult.SKIP_SUBTREE
                                }
                                return FileVisitResult.CONTINUE
                            }

                            override fun visitFile(
                                    file: Path,
                                    attrs: BasicFileAttributes
                            ): FileVisitResult {
                                convertToVector(file, destinationVectorPath.resolve(sourceSvgPath.relativize(file)))
                                return FileVisitResult.CONTINUE
                            }

                            @Throws(IOException::class)
                            override fun visitFileFailed(
                                    file: Path,
                                    exc: IOException
                            ): FileVisitResult {
                                return FileVisitResult.CONTINUE
                            }
                        }
                )
            } else {
                println("source not a directory")
            }
        } catch (e: IOException) {
            println("IOException " + e.message)
        }
    }

    private fun convertToVector(source: Path, target: Path) {
        // convert only if it is .svg
        if (source.fileName.toString().endsWith(".svg")) {
            val targetFile = getFileWithXMlExtention(target, prefix, extension, extensionSuffix)
            val fous = FileOutputStream(targetFile)
            Svg2Vector.parseSvgToXml(source.toFile(), fous)
        } else {
            println("Skipping file as its not svg " + source.fileName.toString())
        }
    }

    private fun getFileWithXMlExtention(
            target: Path,
            prefix: String?,
            extention: String,
            extentionSuffix: String?
    ): File {
        val svgFilePath = target.toFile().absolutePath
        val svgBaseFile = StringBuilder()
        val index = svgFilePath.lastIndexOf("/")
        if (index != -1) {
            val subStr = svgFilePath.substring(0, index + 1)
            svgBaseFile.append(subStr)
            svgBaseFile.append(prefix ?: "")
            val fileName = svgFilePath.substring(index + 1, svgFilePath.lastIndexOf("."))
            svgBaseFile.append(fileName.toLowerCase())
        }
        svgBaseFile.append(extentionSuffix ?: "")
        svgBaseFile.append(".")
        svgBaseFile.append(extention)
        return File(svgBaseFile.toString())
    }
}