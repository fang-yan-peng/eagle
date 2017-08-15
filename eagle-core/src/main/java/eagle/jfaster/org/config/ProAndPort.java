package eagle.jfaster.org.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * 协议与端口信息
 *
 * Created by fangyanpeng1 on 2017/8/8.
 */
@RequiredArgsConstructor
public class ProAndPort {

    @Setter
    @Getter
    private final String protocolId;

    @Setter
    @Getter
    private final int port;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ProAndPort that = (ProAndPort) o;

        if (port != that.port)
            return false;
        return protocolId.equals(that.protocolId);

    }

    @Override
    public int hashCode() {
        int result = protocolId.hashCode();
        result = 31 * result + port;
        return result;
    }
}
