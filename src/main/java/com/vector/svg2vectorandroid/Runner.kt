package com.vector.svg2vectorandroid


val usage = """
    Provide source directory as first argument for svg files to be converted 
    example: java -jar Svg2VectorAndroid-1.1.2.jar <SourceDirectoryPath>
""".trimIndent()

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        return println(usage)
    }

    if (args.contains("--help")) {
        return println(usage)
    }

    val sourceDirectory = args[0]
    if (sourceDirectory.isNotEmpty()) {
        val processor = SvgFilesProcessor(sourceDirectory)
        processor.process()
    }
}
