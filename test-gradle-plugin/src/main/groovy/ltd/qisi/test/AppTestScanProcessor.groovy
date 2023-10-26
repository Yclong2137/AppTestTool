package ltd.qisi.test


import ltd.qisi.test.bean.ScanResult
import ltd.qisi.test.utils.Logger
import org.objectweb.asm.*

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.regex.Pattern

class AppTestScanProcessor {


    private final AppTestConfig config

    AppTestScanProcessor(AppTestConfig config) {
        this.config = config
    }
    /**
     * 扫描class文件
     * @param classFile
     */
    void scanClass(File classFile) {
        if (classFile == null || !classFile.exists()) return
        if (classFile.isFile()) {
            scanClass(classFile.newInputStream(), classFile.getAbsolutePath())
        } else {
            classFile.listFiles().each {
                scanClass(it)
            }
        }
    }
    /**
     * 扫描jar文件
     * @param jarFile
     */
    void scanJar(File jarFile) {
        if (!jarFile) return
        def file = new JarFile(jarFile)
        def enumeration = file.entries()
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = enumeration.nextElement() as JarEntry
            String entryName = jarEntry.getName()
            if (shouldScanClass(entryName)) {
                InputStream is = file.getInputStream(jarEntry)
                scanClass(is, jarFile.absolutePath)
            }
        }
        if (file != null) {
            file.close()
        }

    }
    /**
     * 扫描字节码文件
     * @param is
     * @param filePath
     */
    void scanClass(InputStream is, String filePath) {
        Logger.w("scanClass() file $filePath")
        def cr = new ClassReader(is)
        def cv = new ScanClassVisitor(null, filePath)
        cr.accept(cv, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES)
        is.close()
    }

    /**
     * 是否需要扫描
     * @param regex
     * @param entryName
     * @return
     */
    boolean shouldScanClass(String entryName) {
        if (entryName == null || !entryName.endsWith(".class")) {
            return false
        }
        entryName = entryName.replace(".class", "")
        def includePatterns = config.extension.includePatterns
        if (includePatterns == null || includePatterns.size() <= 0) {
            return false
        }

        for (String regex : includePatterns) {
            if (Pattern.matches(regex, entryName)) {
                return true
            }
        }
        return false
    }

    class ScanClassVisitor extends ClassVisitor {
        //扫描文件
        private final String filePath
        private String className
        //扫描结果
        private ScanResult scanResult

        private boolean hasInterface = false

        ScanClassVisitor(ClassVisitor cv, String filePath) {
            super(Opcodes.ASM5, cv)
            this.filePath = filePath
        }

        boolean is(int access, int flag) {
            return (access & flag) == flag
        }

        @Override
        void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces)
            this.className = name
            //抽象类、接口等类无法调用其无参构造方法
            if (is(access, Opcodes.ACC_ABSTRACT) || is(access, Opcodes.ACC_INTERFACE)) {
                return
            }
            if (name == config.extension.injectClassName) {
                def injectScanResult = new ScanResult()
                injectScanResult.filePath = filePath
                injectScanResult.className = name
                config.injectScanResult = injectScanResult
            }
            hasInterface = interfaces.find { it == config.extension.scanInterface } != null
            if (hasInterface && scanResult == null) {
                scanResult = new ScanResult()
            }
        }

        @Override
        AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            def av = super.visitAnnotation(descriptor, visible)
            return av
        }

        @Override
        MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions)
            if (scanResult != null) {
                if ("<init>" == name || "<clinit>" == name) {
                    return mv
                }
                scanResult.sortedMethodNames.add(name)

            }
            return mv
        }


        @Override
        void visitEnd() {
            if (scanResult != null) {
                scanResult.filePath = filePath
                scanResult.className = className
                scanResult.hasInterface = hasInterface
                config.scanResults.add(scanResult)
            }
            super.visitEnd()
        }
    }


}