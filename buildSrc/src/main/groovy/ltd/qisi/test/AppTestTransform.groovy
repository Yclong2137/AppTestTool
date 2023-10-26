package ltd.qisi.test

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import ltd.qisi.test.utils.Logger
import org.gradle.api.Project

import java.util.regex.Pattern

class AppTestTransform extends Transform {

    Project project

    AppTestExtension extension

    AppTestTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return "AppTestTransform"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, java.io.IOException {
        Logger.e("start test transform... ")
        long time = System.currentTimeMillis()
        AppTestConfig appTestConfig = new AppTestConfig()
        appTestConfig.extension = extension
        def scanProcessor = new AppTestScanProcessor(appTestConfig)
        def insertProcessor = new AppTestInjectProcessor(appTestConfig)
        // 遍历所有的输入
        transformInvocation.inputs.each { input ->
            // 把 文件夹 类型的输入，拷贝到目标目录
            input.directoryInputs.each { directoryInput ->
                def destDir = transformInvocation.outputProvider
                        .getContentLocation(directoryInput.name,
                                directoryInput.contentTypes,
                                directoryInput.scopes,
                                Format.DIRECTORY)
                FileUtils.copyDirectory(directoryInput.file, destDir)
                String root = directoryInput.file.absolutePath
                if (!root.endsWith(File.separator)) root += File.separator
                directoryInput.file.eachFileRecurse { file ->
                    if (file.isFile()) {
                        def path = file.absolutePath.replace(root, '')
                        def entryName = path.replaceAll("\\\\", "/")
                        if (scanProcessor.shouldScanClass(entryName)) {
                            scanProcessor.scanClass(new File(destDir.absolutePath + File.separator + path))
                        }
                    }
                }

            }

            // 把 JAR 类型的输入，拷贝到目标目录
            input.jarInputs.each { jarInput ->
                def dest = transformInvocation.outputProvider
                        .getContentLocation(jarInput.name,
                                jarInput.contentTypes,
                                jarInput.scopes, Format.JAR)
                FileUtils.copyFile(jarInput.file, dest)
                scanProcessor.scanJar(dest)
            }


        }
        Logger.json("AppTestConfig", appTestConfig)
        insertProcessor.insertCodeTo()
        Logger.e("test transform cost time: ${System.currentTimeMillis() - time} ms")

    }
    /**
     * 获取目标文件路径
     * @param src
     * @return
     */
    private String getDestFilePath(File src) {

    }

}