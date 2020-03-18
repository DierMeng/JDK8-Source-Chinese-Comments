package learn.juc.thread.local;

/**
 * ThreadLocal 和 InheritableThreadLocal 透传全局上下文示例
 *
 * 源码来自于《码处高效》，侵删
 *
 * @ClassName: RequestProcessTrace
 * @author: Glorze
 * @since: 2020/3/17 21:47
 */
public class RequestProcessTrace {
    private static final InheritableThreadLocal<FullLinkContext> FULL_LINK_THREADLOCAL = new InheritableThreadLocal<FullLinkContext>();

    public static FullLinkContext getContext() {
        FullLinkContext fullLinkContext = FULL_LINK_THREADLOCAL.get();
        if (null == fullLinkContext){
            FULL_LINK_THREADLOCAL.set(new FullLinkContext());
            fullLinkContext = FULL_LINK_THREADLOCAL.get();
        }
        return fullLinkContext;
    }

    public static class FullLinkContext {
        private String traceId;

        public String getTraceId() {
            if (null != traceId && !"".equals(traceId)) {
                // 设置 traceId 到对应的业务框架中
            }
            return traceId;
        }

        public void setTraceId(String traceId) {
            this.traceId = traceId;
        }
    }
}
