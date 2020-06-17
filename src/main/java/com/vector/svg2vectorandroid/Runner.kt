package com.vector.svg2vectorandroid


val usage = """
    arguments:
    source directory: where to find the SVG files
    destination directory: where to place the generated Vector Drawables
    prefix: any prefix to add to the generated Vector Drawables
    
    example: 
        java -jar Svg2VectorAndroid-1.1.2.jar <SourceDirectoryPath> <DestinationDirectory> ic_
""".trimIndent()

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        return println(usage)
    }

    if (args.contains("--help")) {
        return println(usage)
    }

    val sourceDirectory = args[0]
    val destinationDirectory = args.getOrNull(1)
    val prefix = args.getOrNull(2)
    if (sourceDirectory.isNotEmpty()) {
        val processor = SvgFilesProcessor(sourceDirectory, destinationDirectory, prefix)
        processor.process()
    }
}
