package ltd.qisi.test

import ltd.qisi.test.utils.Logger
import org.apache.commons.io.IOUtils
import org.gradle.internal.io.IoUtils
import org.objectweb.asm.*
import org.objectweb.asm.util.ASMifier
import org.objectweb.asm.util.Printer
import org.objectweb.asm.util.Textifier
import org.objectweb.asm.util.TraceClassVisitor

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class ASMHelper {

    /**
     * 插入字节码到Class中
     * @param file
     * @param codeAdapter
     */
    static void insertCodeTo(File file, String className, MethodCodeAdapter codeAdapter) {
        if (file.absolutePath.endsWith(".jar")) {
            insertCodeToJar(file, className, codeAdapter)
        } else {
            insertCodeToClass(file, codeAdapter)
        }
    }

    /**
     * 插入字节码到Class中
     * @param file
     * @param codeAdapter
     */
    private static void insertCodeToClass(File classFile, MethodCodeAdapter codeAdapter) {
        if (classFile) {
            def file = classFile
            def tmpClass = new File(file.getParent(), file.name + ".tmp")
            InputStream fis = new FileInputStream(file)
            FileOutputStream fos = new FileOutputStream(tmpClass)
            try {
                def bytes = doGenerateCode(fis, codeAdapter)
                fos.write(bytes)
            } catch (Exception e) {
                e.printStackTrace()
            } finally {
                fis.close()
                fos.close()
            }

            if (file.exists()) {
                file.delete()
            }
            tmpClass.renameTo(file)
        }

    }
    /**
     * 插入字节码到jar文件
     * @param jarFile
     */
    private static void insertCodeToJar(File jarFile, String className, MethodCodeAdapter codeAdapter) {
        if (jarFile) {
            def tmpJar = new File(jarFile.getParent(), jarFile.name + ".tmp")
            if (tmpJar.exists()) {
                tmpJar.delete()
            }
            def file = new JarFile(jarFile)
            def enumeration = file.entries()
            def jarOutputStream = new JarOutputStream(new FileOutputStream(tmpJar))
            while (enumeration.hasMoreElements()) {
                def jarEntry = enumeration.nextElement()
                String entryName = jarEntry.getName()
                def zipEntry = new ZipEntry(entryName)
                def is = file.getInputStream(jarEntry)
                jarOutputStream.putNextEntry(zipEntry)
                if (className == entryName.replace(".class", "")) {
                    def bytes = doGenerateCode(is, codeAdapter)
                    jarOutputStream.write(bytes)
                } else {
                    jarOutputStream.write(IOUtils.toByteArray(is))
                }
                is.close()
                jarOutputStream.closeEntry()

            }
            jarOutputStream.close()
            file.close()
            if (jarFile.exists()) {
                jarFile.delete()
            }
            tmpJar.renameTo(jarFile)
        }
    }
    /**
     * 生成字节码
     * @param is
     * @param codeAdapter
     * @return
     */
    static byte[] doGenerateCode(InputStream is, MethodCodeAdapter codeAdapter) {
        ClassReader cr = new ClassReader(is)
        ClassWriter cw = new ClassWriter(cr, 0)
        ClassVisitor cv = new ClassVisitor(Opcodes.ASM5, cw) {

            private String className

            @Override
            void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                super.visit(version, access, name, signature, superName, interfaces)
                this.className = name
            }

            @Override
            MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                def mv = super.visitMethod(access, name, descriptor, signature, exceptions)
                mv = codeAdapter.adapt(className, name, Opcodes.ASM5, mv)
                return mv
            }
        }
        cr.accept(cv, ClassReader.EXPAND_FRAMES)
        return cw.toByteArray()
    }


    interface MethodCodeAdapter {
        /**
         * 适配
         * @param className 类名
         * @param methodName 方法名
         * @param api 版本
         * @param mv 方法访问者
         * @return
         */
        MethodVisitor adapt(String className, String methodName, int api, MethodVisitor mv)

    }
    /**
     * 打印字节码文件
     * @param classFile
     */
    private static void printClassCode(byte[] classFile) {
        Printer printer = new ASMifier()
        PrintWriter printWriter = new PrintWriter(System.out, true);
        TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, printer, printWriter);
        int parseOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        new ClassReader(classFile).accept(traceClassVisitor, parseOptions)
    }

}