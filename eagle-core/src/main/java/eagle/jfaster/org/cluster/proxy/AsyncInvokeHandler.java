package eagle.jfaster.org.cluster.proxy;

import eagle.jfaster.org.cluster.ReferCluster;
import eagle.jfaster.org.rpc.Request;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fangyanpeng1 on 2017/8/7.
 */
public class AsyncInvokeHandler<T> extends AbstractReferInvokeHandler<T> {

    public AsyncInvokeHandler(List<ReferCluster<T>> referClusters, Class<T> clz) {
        super(referClusters, clz);
    }

    @Override
    protected Object handle(Method method,Request request) {
        Object ret = this.defaultCluster.call(request);
        if(ret != null){
            return ret;
        }
        return PrimitiveDefault.getDefaultReturnValue(method.getReturnType());
    }

    private static class PrimitiveDefault {
        private static boolean defaultBoolean;
        private static char defaultChar;
        private static byte defaultByte;
        private static short defaultShort;
        private static int defaultInt;
        private static long defaultLong;
        private static float defaultFloat;
        private static double defaultDouble;

        private static Map<Class<?>, Object> primitiveValues = new HashMap<Class<?>, Object>();

        static {
            primitiveValues.put(boolean.class, defaultBoolean);
            primitiveValues.put(char.class, defaultChar);
            primitiveValues.put(byte.class, defaultByte);
            primitiveValues.put(short.class, defaultShort);
            primitiveValues.put(int.class, defaultInt);
            primitiveValues.put(long.class, defaultLong);
            primitiveValues.put(float.class, defaultFloat);
            primitiveValues.put(double.class, defaultDouble);
        }

        public static Object getDefaultReturnValue(Class<?> returnType) {
            return primitiveValues.get(returnType);
        }

    }

}
