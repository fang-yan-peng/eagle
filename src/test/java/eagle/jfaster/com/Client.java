/*
package eagle.jfaster.com;

import com.facebook.nifty.client.FramedClientConnector;
import com.facebook.swift.service.ThriftClientManager;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

*/
/**
 * Created by fangyanpeng1 on 2017/7/7.
 *//*

public class Client {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ThriftClientManager clientManager = new ThriftClientManager();
        FramedClientConnector connector = new FramedClientConnector(new InetSocketAddress("localhost", 8899));
        Scribe scribe = clientManager.createClient(connector, Scribe.class).get();
        LogEntry entry = scribe.process(1);
        System.out.println(entry.getDate());
        System.out.println(entry.getId());
        FramedClientConnector connector1 = new FramedClientConnector(new InetSocketAddress("localhost", 8899));
        Scribe1 scribe1 = clientManager.createClient(connector1, Scribe1.class).get();
        LogEntry entry1 = scribe1.process1(2);
        System.out.println(entry1.getDate());
        System.out.println(entry1.getId());

    }
}
*/
