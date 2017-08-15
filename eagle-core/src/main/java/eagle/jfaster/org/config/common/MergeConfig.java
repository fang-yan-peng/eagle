package eagle.jfaster.org.config.common;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import eagle.jfaster.org.config.ConfigEnum;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.rpc.MethodInvokeCallBack;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Map;
import static eagle.jfaster.org.constant.EagleConstants.GROUP_SPLIT_PATTERN;

/**
 * 配置信息
 *
 * Created by fangyanpeng1 on 2017/7/27.
 */
@NoArgsConstructor
public class MergeConfig {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(MergeConfig.class);


    @Getter
    @Setter
    private String host;

    @Getter
    @Setter
    private int port;

    @Getter
    @Setter
    private String interfaceName;

    @Getter
    @Setter
    private String protocol;

    @Getter
    @Setter
    private String version;

    @Getter
    @Setter
    private transient MethodInvokeCallBack invokeCallBack;

    @Getter
    @Setter
    private Map<String,String> extFeilds = Maps.newHashMapWithExpectedSize(32);

    @Getter
    @Setter
    private volatile transient Map<String, Number> numbers = Maps.newHashMapWithExpectedSize(32);

    public void addExt(String name,String value){
        this.extFeilds.put(name,value);
    }

    public void addExts(Map<String,String> exts){
        this.extFeilds.putAll(exts);
    }

    public String getExt(String name,String defaultExt){
        String value =  extFeilds.get(name);
        if(Strings.isNullOrEmpty(value)){
            return defaultExt;
        }
        return value;
    }

    public Boolean getExtBoolean(String name,boolean defaultExt){
        String value =  extFeilds.get(name);
        if(Strings.isNullOrEmpty(value)){
            return defaultExt;
        }
        return Boolean.parseBoolean(value);
    }

    public int getExtInt(String name,int defaultExt){
        Number n = getNumbers().get(name);
        if (n != null) {
            return n.intValue();
        }
        String value = extFeilds.get(name);
        if (Strings.isNullOrEmpty(value)) {
            return defaultExt;
        }
        int i = Integer.parseInt(value);
        getNumbers().put(name, i);
        return i;
    }

    public long getExtLong(String name,long defaultExt){
        Number n = getNumbers().get(name);
        if (n != null) {
            return n.longValue();
        }
        String value = extFeilds.get(name);
        if (Strings.isNullOrEmpty(value)) {
            return defaultExt;
        }
        long l = Long.parseLong(value);
        getNumbers().put(name, l);
        return l;
    }

    public Double getExtDouble(String name,double defaultExt){
        Number n = getNumbers().get(name);
        if (n != null) {
            return n.doubleValue();
        }
        String value = extFeilds.get(name);
        if (value == null || value.length() == 0) {
            return defaultExt;
        }
        double l = Double.parseDouble(value);
        getNumbers().put(name, l);
        return l;
    }

    public void update(MergeConfig config){
        this.addExts(config.getExtFeilds());
        this.numbers.clear();
    }

    public boolean isSupport(MergeConfig other){
        //比较版本
        String version = getVersion();
        String refVersion = other.getVersion();
        if (!version.equals(refVersion)) {
            logger.info(String.format("Not support version:%s,current support version:%s",version,refVersion));
            return false;
        }
        //比较序列化
        String serialize = getExt(ConfigEnum.serialize.getName(), ConfigEnum.serialize.getValue());
        String refSerialize = other.getExt(ConfigEnum.serialize.getName(), ConfigEnum.serialize.getValue());
        if (!serialize.equals(refSerialize)) {
            logger.info(String.format("Not support serializeType:%s,current support serializeType:%s",serialize,refSerialize));
            return false;
        }
        //比较group
        String group = getExt(ConfigEnum.group.getName(), ConfigEnum.group.getValue());
        String refGroup = other.getExt(ConfigEnum.group.getName(), ConfigEnum.group.getValue());
        if(!compareGroup(refGroup,group)){
            logger.info(String.format("Not support group:%s,current support group:%s",group,refGroup));
            return false;
        }
        return true;
    }

    private boolean compareGroup(String refGroup,String serGroup){
        String[] refGroups = GROUP_SPLIT_PATTERN.split(refGroup);
        String[] serGroups = GROUP_SPLIT_PATTERN.split(serGroup);
        for(String ref : refGroups){
            String refr = ref.trim();
            for(String ser : serGroups){
                if(refr.equals(ser.trim())){
                    return true;
                }
            }
        }
        return false;
    }

    public static MergeConfig decode(final String data){
        return JSON.parseObject(data,MergeConfig.class);
    }

    public String encode(){
        return JSON.toJSONString(this, false);
    }

    public MergeConfig copy(){
        MergeConfig config = new MergeConfig();
        config.setHost(this.host);
        config.setPort(this.port);
        config.setVersion(this.version);
        config.setInterfaceName(this.interfaceName);
        config.addExts(this.extFeilds);
        return config;
    }

    private final String IDENTITY_FORMATE = "%s://%s:%d";

    public String identity(){
        return String.format(IDENTITY_FORMATE,protocol,host,port);
    }

    private final String HOST_FORMATE = "%s:%d";

    public String hostPort(){
        return String.format(HOST_FORMATE,host,port);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        MergeConfig config = (MergeConfig) o;

        if (port != config.port)
            return false;
        if (!host.equals(config.host))
            return false;
        if (!interfaceName.equals(config.interfaceName))
            return false;
        if (!version.equals(config.version))
            return false;
        if (!protocol.equals(config.protocol))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = host.hashCode();
        result = 31 * result + port;
        result = 31 * result + interfaceName.hashCode();
        result = 31 * result + version.hashCode();
        result = 31 * result + protocol.hashCode();
        result = 31 * result + (extFeilds != null ? extFeilds.hashCode() : 0);
        result = 31 * result + (numbers != null ? numbers.hashCode() : 0);
        result = 31 * result + (HOST_FORMATE != null ? HOST_FORMATE.hashCode() : 0);
        return result;
    }
}
