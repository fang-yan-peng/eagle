package eagle.jfaster.org.callback;

import org.springframework.stereotype.Service;

/**
 * Created by fangyanpeng1 on 2017/8/9.
 */
@Service("calculateDao")
public class CalculateDao {
    public void insert(){
        System.out.println("-----------insert--------");
    }
}
