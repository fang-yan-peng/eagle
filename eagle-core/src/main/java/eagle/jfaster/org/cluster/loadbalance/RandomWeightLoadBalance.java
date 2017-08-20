package eagle.jfaster.org.cluster.loadbalance;

import eagle.jfaster.org.config.ConfigEnum;
import eagle.jfaster.org.rpc.Refer;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.spi.SpiInfo;

import java.util.concurrent.ThreadLocalRandom;

import static eagle.jfaster.org.constant.EagleConstants.WARMUP;


/**
 * 基于权重的负载算法
 *
 * Created by fangyanpeng on 2017/8/20.
 */
@SpiInfo(name = "weight")
public class RandomWeightLoadBalance<T> extends AbstractLoadBalance<T> {

    @Override
    public Refer<T> doSelect(Request request) {
        int length = refers.size(); // 总个数
        int totalWeight = 0; // 总权重
        boolean sameWeight = true; // 权重是否都一样
        for (int i = 0; i < length; i++) {
            int weight = getWeight(refers.get(i));
            totalWeight += weight; // 累计总权重
            if (sameWeight && i > 0 && weight != getWeight(refers.get(i - 1))) {
                sameWeight = false; // 计算所有权重是否一样
            }
        }
        if (totalWeight > 0 && ! sameWeight) {
            // 如果权重不相同且权重大于0则按总权重数随机
            int offset = ThreadLocalRandom.current().nextInt(totalWeight);
            // 并确定随机值落在哪个片断上
            for (int i = 0; i < length; i++) {
                Refer<T> refer = refers.get(i);
                offset -= getWeight(refer);
                if (offset < 0 && refer.isAlive()) {
                    return refer;
                }
            }
        }
        // 如果权重相同或权重为0则均等随机
        int idx = ThreadLocalRandom.current().nextInt(length);
        for (int i = 0; i < refers.size(); i++) {
            Refer<T> ref = refers.get((i + idx) % refers.size());
            if (ref.isAlive()) {
                return ref;
            }
        }
        return null;
    }

    protected int getWeight(Refer<T> refer) {
        int weight = refer.getConfig().getExtInt(ConfigEnum.weight.getName(),ConfigEnum.weight.getIntValue());
        if (weight > 0) {
            long timestamp = refer.getConfig().getExtLong(ConfigEnum.refreshTimestamp.getName(), ConfigEnum.refreshTimestamp.getIntValue());
            if (timestamp > 0L) {
                int uptime = (int) (System.currentTimeMillis() - timestamp);
                if (uptime > 0 && uptime < WARMUP) {
                    weight = calculateWarmupWeight(uptime, weight);
                }
            }
        }
        return weight;
    }

    static int calculateWarmupWeight(int uptime, int weight) {
        int ww = (int) ( (float) uptime / ( (float) WARMUP / (float) weight ) );
        return ww < 1 ? 1 : (ww > weight ? weight : ww);
    }
}
