import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

class Host {
	String ipAddr;
	int port;
}

public class MultiThreadServer implements Runnable {
	Socket csocket;

	static int option = -1;
	static ArrayList<Host> hostList;
	static String tableString = "";
	// static Socket sock;
	static String hostname = "";

	static HashMap<String, Socket> hm = new HashMap<String, Socket>();

	ReentrantLock lock = new ReentrantLock();

	MultiThreadServer(Socket csocket) {
		this.csocket = csocket;
	}

	public static void main(String args[]) throws Exception {

		ArrayList<String> ips = new ArrayList<String>();

		String label = "";

		hostname = InetAddress.getLocalHost().getHostName();
		// System.out.println(hostname);

		FileReader file = new FileReader("config_" + hostname);
		ArrayList<String> table = new ArrayList(10);
		String serverip = "";

		try (BufferedReader br = new BufferedReader(file)) {
			String line;
			while ((line = br.readLine()) != null) {
				// System.out.println(line);

				if (line.startsWith("ip")) {
					ips.add(line.substring(3));
				} else if (line.startsWith("edge"))
					table.add(line.substring(5));
				else if (line.startsWith("label")) {
					label = line.substring(6);
					System.out.println("label:::" + label);
				} else if (line.startsWith("serverip")) {
					serverip = line.substring(8);
				}

			}
		}

		// String tableString = "";
		int length = table.size();
		for (int i = 0; i < length; i++) {
			tableString = tableString + table.get(i);

			if (i != length - 1)
				tableString = tableString + ",";

		}

		System.out.println("table:    " + tableString);

		hostList = new ArrayList<Host>();

		for (int i = 0; i < ips.size(); i++) {

			String tmp1 = ips.get(i);
			String[] tmp2 = tmp1.split(",");

			String ipAddr = tmp2[0];
			String portNum = tmp2[1];

			int intportNum = Integer.valueOf(portNum);

			Host tmpHost = new Host();
			tmpHost.ipAddr = ipAddr;
			tmpHost.port = intportNum;
			hostList.add(tmpHost);

		}

		// for (int i = 0; i < hostList.size(); i++) {
		//
		// System.out.println(hostList.get(i).port);
		//
		// }

		if (label.equals("1")) {
			option = 1;
			new Thread(new MultiThreadServer(null)).start();

			// for (int i = 0; i < hostList.size(); i++) {
			//
			// int port = hostList.get(i).port;
			// String serverAddress = hostList.get(i).ipAddr;
			//
			// System.out.println("serverAddress: " + serverAddress);
			// System.out.println("port: " + port);
			//
			// Socket socket = new Socket(serverAddress, port);
			//
			// try {
			// PrintWriter out = new PrintWriter(socket.getOutputStream(),
			// true);
			// out.println(tableString);
			// } finally {
			// socket.close();
			// }
			//
			// }

		}

		String str_port = serverip.split(",")[1];
		int int_port = Integer.valueOf(str_port);
		ServerSocket ssock = new ServerSocket(int_port);
		System.out.println("Listening");

		while (true) {
			System.out.println("loop_wait");
			Socket sock = ssock.accept();
			System.out.println("Connected");

			option = 2;
			new Thread(new MultiThreadServer(sock)).start();

			// PrintStream pstream = new PrintStream(sock.getOutputStream());
			//
			// BufferedReader input = new BufferedReader(new
			// InputStreamReader(sock.getInputStream()));
			// String new_table = input.readLine();
			// System.out.println("new table \n" + new_table);
			//
			// String[] tmp = tableString.split(",");
			// String[] tmp2 = new_table.split(",");
			//
			//
			//
			// HashMap<String,String> hm = new HashMap<String,String>();
			//
			// boolean overallupdate =false;
			// for (int i = 0; i < tmp.length; i++) {
			// String o_edge = tmp[i];
			// String[] o_edge_1 = o_edge.split(" ");
			//
			// String left1 = o_edge_1[0];
			// String right1 = o_edge_1[1];
			// String cost1 = o_edge_1[2];
			// hm.put(right1, cost1);
			// }
			//
			// for (int i = 0; i < tmp.length; i++) {
			// String o_edge = tmp[i];
			// String[] o_edge_1 = o_edge.split(" ");
			//
			// String left1 = o_edge_1[0];
			// String right1 = o_edge_1[1];
			// String cost1 = o_edge_1[2];
			//
			// for (int j = 0; j < tmp2.length; j++) {
			// String n_edge = tmp2[j];
			// String[] n_edge_1 = n_edge.split(" ");
			//
			// String left2 = n_edge_1[0];
			// String right2 = n_edge_1[1];
			// String cost2 = n_edge_1[2];
			//
			// boolean update = false;
			// boolean match = false;
			//
			// if (right1.equals(left2)) {
			//
			// overallupdate = true;
			// update = true;
			//
			// System.out.println("cost1"+cost1);
			// System.out.println("cost2"+cost2);
			// int intcost1 = Integer.valueOf(cost1);
			// int intcost2 = Integer.valueOf(cost2);
			// int cost_sum = intcost1 + intcost2;
			// System.out.println("cost_sum: " + cost_sum);
			//
			// for (int k = 0; k < tmp.length; k++) {
			// String o2_edge = tmp[k];
			// String[] o2_edge_1 = o2_edge.split(" ");
			//
			// String left11 = o2_edge_1[0];
			// String right11 = o2_edge_1[1];
			// String cost11 = o_edge_1[2];
			//
			// int intcost11 = Integer.valueOf(cost11);
			//
			// if (right11.equals(right2) && cost_sum < intcost11) {
			// match = true;
			// update = true;
			//
			// System.out.println("match");
			// System.out.println(right2);
			// System.out.println(cost_sum);
			//
			// hm.put(right2, String.valueOf(cost_sum));
			//
			// }
			//
			// }
			//
			// if (match == false && update == true) {
			//
			// System.out.println("update");
			// System.out.println(right2);
			// System.out.println(cost_sum);
			//
			//
			// hm.put(right2, String.valueOf(cost_sum));
			// }
			//
			// }
			//
			// // System.out.println(edge_[1]);
			// }
			//
			// }
			//
			//
			//
			//
			// String update_tableString = "";
			//
			//
			// System.out.println("current routing table:");
			// Set set2 = hm.entrySet();
			// Iterator iterator2 = set2.iterator();
			// while (iterator2.hasNext()) {
			// Map.Entry mentry2 = (Map.Entry) iterator2.next();
			//
			// String key =(String) mentry2.getKey();
			// String value= (String) mentry2.getValue();
			//// String value = String.valueOf(v);
			// System.out.println("Key is: " + key + " & Value is: " + value);
			// update_tableString=update_tableString+ hostname+" "+key+" "+
			// value+",";
			// }
			// int tmplen= update_tableString.length();
			// update_tableString=update_tableString.substring(0,tmplen-1);
			//
			// System.out.println("update_tableString: "+ update_tableString);
			//
			//
			//
			// if(overallupdate == true)
			// {
			//
			//
			// tableString =update_tableString;
			// System.out.println("send update result");
			//
			// for (int i = 0; i < hostList.size(); i++) {
			//
			// int port = hostList.get(i).port;
			// String serverAddress = hostList.get(i).ipAddr;
			//
			// System.out.println("serverAddress: " + serverAddress);
			// System.out.println("port: " + port);
			//
			// Socket socket = new Socket(serverAddress, port);
			//
			// try {
			// PrintWriter out = new PrintWriter(socket.getOutputStream(),
			// true);
			// out.println(tableString);
			// } finally {
			// socket.close();
			// }
			//
			// }
			//
			// }
			//
			//
			//
			// pstream.close();
			// sock.close();

			// new Thread(new MultiThreadServer(sock)).start();
		}
	}

	public void run() {

		if (option == 1) {

			for (int i = 0; i < hostList.size(); i++) {

				int port = hostList.get(i).port;
				String serverAddress = hostList.get(i).ipAddr;

				System.out.println("serverAddress:   " + serverAddress);
				System.out.println("port:   " + port);

				Socket socket = null;
				try {

					socket = new Socket(serverAddress, port);

					PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
					out.println(tableString);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

		} else if (option == 2) {

			synchronized (tableString) {
				try {

					PrintStream pstream = new PrintStream(csocket.getOutputStream());

					BufferedReader input = new BufferedReader(new InputStreamReader(csocket.getInputStream()));
					String new_table = input.readLine();

					System.out.println("old table\n" + tableString);

					System.out.println("new table \n" + new_table);

					
					String routingTable ="";
					routingTable = tableString+",";
					String[] tmp = tableString.split(",");
					String[] tmp2 = new_table.split(",");

					HashMap<String, String> hm = new HashMap<String, String>();

					boolean overallupdate = false;
					for (int i = 0; i < tmp.length; i++) {
						String o_edge = tmp[i];
						String[] o_edge_1 = o_edge.split(" ");

						String left1 = o_edge_1[0];
						String right1 = o_edge_1[1];
						String cost1 = o_edge_1[2];
						hm.put(right1, cost1);
					}

					for (int i = 0; i < tmp.length; i++) {
						String o_edge = tmp[i];
						String[] o_edge_1 = o_edge.split(" ");

						String left1 = o_edge_1[0];
						String right1 = o_edge_1[1];
						String cost1 = o_edge_1[2];

						for (int j = 0; j < tmp2.length; j++) {
							String n_edge = tmp2[j];
							String[] n_edge_1 = n_edge.split(" ");

							String left2 = n_edge_1[0];
							String right2 = n_edge_1[1];
							String cost2 = n_edge_1[2];

							boolean update = false;
							boolean match = false;

							if (right1.equals(left2)) {

								System.out.println("cost1" + cost1);
								System.out.println("cost2" + cost2);
								int intcost1 = Integer.valueOf(cost1);
								int intcost2 = Integer.valueOf(cost2);
								int cost_sum = intcost1 + intcost2;
//								System.out.println("cost_sum: " + cost_sum);

								for (int k = 0; k < tmp.length; k++) {
									String o2_edge = tmp[k];
									String[] o2_edge_1 = o2_edge.split(" ");

									String left11 = o2_edge_1[0];
									String right11 = o2_edge_1[1];
									String cost11 = o2_edge_1[2];

//									System.out.println("check: " +left11+" "+ right11 + " " + cost11);

									int intcost11 = Integer.valueOf(cost11);

									if (right11.equals(right2)) {
										match = true;
										if (cost_sum < intcost11) {

											overallupdate = true;
											update = true;

//											System.out.println("match");
//											System.out.println(right2);
//											System.out.println(cost_sum);

											hm.put(right2, String.valueOf(cost_sum));
//											System.out.println("middle: "+ right1+"   "+ right2);
											String newRouting = hostname+" "+ right1+" "+ right2+" "+cost_sum+",";
											routingTable = routingTable+ newRouting;

										}
									}

								}

								if (match == false) {

									overallupdate = true;
									update = true;

//									System.out.println("update");
//									System.out.println(right2);
//									System.out.println(cost_sum);

									hm.put(right2, String.valueOf(cost_sum));
									
//									System.out.println("middle: "+ right1+"   "+ right2);
									String newRouting = hostname+" "+ right1+" "+ right2+" "+cost_sum+",";
									routingTable = routingTable+ newRouting;
								}

							}

							// System.out.println(edge_[1]);
						}

					}

					String update_tableString = "";

					System.out.println("current routing table:");
					Set set2 = hm.entrySet();
					Iterator iterator2 = set2.iterator();
					while (iterator2.hasNext()) {
						Map.Entry mentry2 = (Map.Entry) iterator2.next();

						String key = (String) mentry2.getKey();
						String value = (String) mentry2.getValue();
						// String value = String.valueOf(v);
						System.out.println("Key is: " + key + " & Value is: " + value);
						update_tableString = update_tableString + hostname + " " + key + " " + value + ",";
					}
					int tmplen = update_tableString.length();
					update_tableString = update_tableString.substring(0, tmplen - 1);

					System.out.println("update_tableString:  " + update_tableString);

					if (overallupdate == true) {

						tableString = update_tableString;
						System.out.println("send update result");

						for (int i = 0; i < hostList.size(); i++) {

							int port = hostList.get(i).port;
							String serverAddress = hostList.get(i).ipAddr;

							System.out.println("serverAddress:   " + serverAddress);
							System.out.println("port:   " + port);

							Socket socket = new Socket(serverAddress, port);

							try {
								PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
								out.println(tableString);
							} finally {
								socket.close();
							}

						}

					}
					
					System.out.println("----------------");
					System.out.println ("\n\nroutingTable:\n");
//					System.out.println (routingTable);
					String[] routing_t = routingTable.split(",");

					for(int i=0;i<routing_t.length;i++)
					{
						System.out.println(routing_t[i]);
					}
					
					System.out.println("----------------");
					
					pstream.close();
					csocket.close();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		// try {
		// PrintStream pstream = new PrintStream(csocket.getOutputStream());
		//
		// BufferedReader input = new BufferedReader(new
		// InputStreamReader(csocket.getInputStream()));
		// String answer = input.readLine();
		// System.out.println(answer);
		//
		// pstream.close();
		// csocket.close();
		// } catch (IOException e) {
		// System.out.println(e);
		// }
	}
}