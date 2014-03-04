package test.odmrp.messaging;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import net.odmrp.router.Router;

import org.junit.BeforeClass;
import org.junit.Test;

public class PacketTest {
	
	@BeforeClass
	public static void setup() {
		try {
			Constructor<Router> constructor = (Constructor<Router>)Router.class.getConstructor(InetAddress.class);
			Router router = constructor.newInstance(InetAddress.getByName("localhost"));
			System.out.println(router);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void test() {
//		fail("Not yet implemented");
		assertTrue(true);
	}

}
