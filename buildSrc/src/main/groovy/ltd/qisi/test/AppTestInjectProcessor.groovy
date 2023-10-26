package ltd.qisi.test

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type


class AppTestInjectProcessor {


    private final AppTestConfig config

    AppTestInjectProcessor(AppTestConfig config) {
        this.config = config
    }
    /**
     * 插入字节码*/
    void insertCodeTo() {
        if (config == null) return
        def injectScanResult = config.injectScanResult
        if (injectScanResult != null) {
            def file = new File(injectScanResult.filePath)
            String entryName = config.extension.injectClassName
            ASMHelper.insertCodeTo(file, entryName, new ASMHelper.MethodCodeAdapter() {

                /**
                 * 适配
                 * @param className 类名
                 * @param methodName 方法名
                 * @param api 版本
                 * @param mv 方法访问者
                 * @return
                 */
                @Override
                MethodVisitor adapt(String className, String methodName, int api, MethodVisitor mv) {
                    if (methodName == "<clinit>") {
                        mv = new MethodVisitor(api, mv) {
                            @Override
                            void visitInsn(int opcode) {
                                if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)) {
                                    config.scanResults.each { result ->
//                                        def moduleName = result.moduleName
//                                        if (moduleName == null) {
//                                            def length = result.className.length()
//                                            def lastIndex = result.className.lastIndexOf("/")
//                                            moduleName = result.className.substring(lastIndex + 1, length)
//                                        }
//                                        mv.visitLdcInsn(moduleName)
                                        mv.visitLdcInsn(Type.getType("L${result.className};"))
                                        //注册
                                        mv.visitMethodInsn(Opcodes.INVOKESTATIC
                                                , className
                                                , "register"
                                                , "(Ljava/lang/Class;)V"
                                                , false)
                                    }

                                }
                                super.visitInsn(opcode)
                            }

                        }
                    }
                    return mv
                }
            }

            )
        }
        config.scanResults.each { scanResult ->
            if (scanResult.hasInterface) {
                def file = new File(scanResult.filePath)
                ASMHelper.insertCodeTo(file, scanResult.className, new ASMHelper.MethodCodeAdapter() {

                    /**
                     * 适配
                     * @param className 类名
                     * @param methodName 方法名
                     * @param api 版本
                     * @param mv 方法访问者
                     * @return
                     */
                    @Override
                    MethodVisitor adapt(String className, String methodName, int api, MethodVisitor mv) {
                        if ("<init>" == methodName) {
                            mv = new MethodVisitor(api, mv) {
                                @Override
                                void visitInsn(int opcode) {
                                    if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)) {
                                        scanResult.sortedMethodNames.each { name ->
                                            //用无参构造方法创建一个组件实例
                                            mv.visitFieldInsn(Opcodes.GETSTATIC, scanResult.className, "sortedMethodNames", "Ljava/util/List;")
                                            mv.visitLdcInsn(name)
                                            //注册
                                            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE
                                                    , "java/util/List"
                                                    , "add"
                                                    , "(Ljava/lang/Object;)Z"
                                                    , true)
                                            mv.visitInsn(Opcodes.POP)
                                        }

                                    }
                                    super.visitInsn(opcode)
                                }
                            }
                        }
                        return mv
                    }
                })
            }
        }

    }


//    private void insertCodeToInterfaceImplClass(List<ScanResult> scanResults) {
//        if (scanResults == null || scanResults.isEmpty()) return
//        scanResults.each { scanResult ->
//            if (scanResult.hasInterface) {
//                ASMHelper.insertCodeToClass(scanResult.classFilePath, new ASMHelper.MethodCodeAdapter() {
//
//                    @Override
//                    boolean intercept(String methodName) {
//                        return methodName == "<init>"
//                    }
//
//                    @Override
//                    MethodVisitor adapt(int api, MethodVisitor mv) {
//                        return new MethodVisitor(api, mv) {
//                            @Override
//                            void visitInsn(int opcode) {
//                                if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)) {
//                                    scanResult.sortedMethodNames.each { name ->
//                                        //用无参构造方法创建一个组件实例
//                                        mv.visitFieldInsn(Opcodes.GETSTATIC, scanResult.className, "sortedMethodNames", "Ljava/util/List;")
//                                        mv.visitLdcInsn(name)
//                                        //注册
//                                        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE
//                                                , "java/util/List"
//                                                , "add"
//                                                , "(Ljava/lang/Object;)Z"
//                                                , true)
//                                        mv.visitInsn(Opcodes.POP)
//                                    }
//
//                                }
//                                super.visitInsn(opcode)
//                            }
//                        }
//                    }
//                })
//            }
//        }
//    }


}