package com.yss.id.server.context;


import org.springframework.stereotype.Component;

/**
 * 上下文工厂类
 */
@Component
public class ContextFactory {

    protected static InheritableThreadLocal<ContextSupport> contexts = new InheritableThreadLocal<ContextSupport>();

    public static ContextSupport getContext() {
        return contexts.get();
    }

    public static void remove() {
        contexts.remove();
    }

    /**
     * 创建上下文信息
     *
     * @return
     */
    public void createContext(){
        contexts.set(new ContextSupport());
    }
}
