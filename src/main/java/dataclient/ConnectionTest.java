package dataclient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ConnectionTest {
	
	private static final int TIMEOUT = 10;
	private Set<Integer> ports;
	private final String ADDRESS;
	private Set<Integer> open;
	private Set<Integer> closed;
	
	public ConnectionTest(String address){
		ADDRESS = address;
		open = new HashSet<Integer>();
		closed = new HashSet<Integer>();
	}
	
	public ConnectionTest(String address, Integer ... ports){
		this(address, Arrays.asList(ports));
	}
	
	public ConnectionTest(String address, Collection<Integer> ports){
		this(address);
		setPorts(ports);
	}
	
	public ConnectionTest(String address, int lower, int upper){
		this(address);
		List<Integer> ports = new ArrayList<Integer>();
		for(int port = lower; port <= upper; port++){
			ports.add(port);
		}
		setPorts(ports);
	}
	
	public void setPorts(Collection<Integer> ports){
		this.ports = new TreeSet<Integer>(ports);
	}
	
	public Set<Integer> getPorts(){
		return ports;
	}
	
	public Set<Integer> getOpenPorts(){
		return open;
	}
	
	public Set<Integer> getClosedPorts(){
		return closed;
	}
	
	public boolean isPortOpen(Integer port){
		if(ports.contains(port)){
			return open.contains(port);
		}
		else throw new IllegalArgumentException("Port not included in the set:" + ports);
	}
	
	public void refresh(){
		open.clear();
		closed.clear();
		for(Integer port : ports){
			try {
				Socket sok = new Socket();
				sok.connect(new InetSocketAddress(ADDRESS, port),TIMEOUT);
				sok.close();
				open.add(port);
			} catch (IOException e) {
				closed.add(port);
			}
		}
	}
	
	public static void main(String[] args){
		ConnectionTest test = new ConnectionTest("localhost", 0, 1000);
		test.refresh();
		System.out.println("Open ports:" + test.getOpenPorts());
	}
}
