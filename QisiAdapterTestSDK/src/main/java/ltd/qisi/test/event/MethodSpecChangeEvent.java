package ltd.qisi.test.event;

import ltd.qisi.test.model.MethodSpec;

public class MethodSpecChangeEvent {

    public MethodSpec methodSpec;

    public MethodSpecChangeEvent(MethodSpec methodSpec) {
        this.methodSpec = methodSpec;
    }
}
