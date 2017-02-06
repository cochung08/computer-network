
//
//# TCP Flags
//use constant FIN => 0x01;
//use constant SYN => 0x02;
//use constant RST => 0x04;
//use constant PSH => 0x08;
//use constant ACK => 0x10;
//use constant URG => 0x20;
//use constant ECE => 0x40;
//use constant CWR => 0x80;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;

class Packet {

	int length;
	String ipSrc;
	String ipDest;

	int srcPort;
	int destPort;
	// short srcPort;
	// short destPort;
	int seq;
	int ack;
	short window;
	byte flag;
	int sec;
	int microsec;
	byte protocal;
	int length_ip;
	int length_ip_header;
	int length_tcp_header;
	int length_payload;
	int mss;

	@Override
	public String toString() {
		return String.valueOf(seq);
	}
}

public class part_b {

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static byte[] long2bytearray(long l) {
		byte b[] = new byte[8];

		ByteBuffer buf = ByteBuffer.wrap(b);
		buf.putLong(l);
		return b;
	}

	public static void main(String[] args) throws IOException {

		
		
		 File pcap = new File(args[0]);
//		 File pcap = new File("HTTP_SampleB.pcap");
		// File pcap = new File("http_first_sample.pcap");
		// File pcap = new File("hw1.pcap");
//		File pcap = new File("HTTP_Sample_Big_Packet.pcap");

		FileInputStream fileInputStream = new FileInputStream(pcap);

		byte[] file_header = new byte[24];
		byte[] data_header = new byte[16];

		ArrayList<Packet> list_packet = new ArrayList<Packet>();

		int r = fileInputStream.read(file_header);

		boolean syn_packet = false;

		while (r > 0) {
			Packet _packet = new Packet();

			r = fileInputStream.read(data_header);

			byte[] byte_array_4 = new byte[4];
			byte[] byte_array_2 = new byte[2];

			int offset = 0;
			for (int i = 0; i < 4; i++) {
				byte_array_4[i] = data_header[i + offset];
			}

			int sec = java.nio.ByteBuffer.wrap(byte_array_4).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
			_packet.sec = sec;
			// System.out.println("dateAsText: "+new Date(sec * 1000L)); //
			// *1000 is to convert seconds to milliseconds

			offset += 4;

			byte[] byte_array_4_second = new byte[4];
			for (int i = 0; i < 4; i++) {
				byte_array_4_second[i] = data_header[i + offset];
			}

			int microsec = java.nio.ByteBuffer.wrap(byte_array_4_second).order(java.nio.ByteOrder.LITTLE_ENDIAN)
					.getInt();
			// System.out.println("microsec: "+microsec);

			_packet.microsec = microsec;

			int offset_len = 8;

			for (int i = 0; i < 4; i++) {
				byte_array_4[i] = data_header[i + offset_len];
			}

			int caplen = java.nio.ByteBuffer.wrap(byte_array_4).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();

			_packet.length = caplen;
			// System.out.println("caplen: " + caplen);

			byte packet_data[] = new byte[caplen];
			r = fileInputStream.read(packet_data);

			byte protocol = packet_data[14 + 9];
			_packet.protocal = protocol;

			// System.out.println("protocol:" + protocol);

			if (_packet.protocal == 0x06) {

				int offset_ip = 14;
				int offset_tcp = offset_ip + 20;
				int offset_tcp_sn = offset_tcp + 4;
				int offset_tcp_ack = offset_tcp + 4 + 4;
				int offset_tcp_ws = offset_tcp + 14;
				int offset_tcp_flag = offset_tcp + 4 + 4 + 4 + 1;
				int offset_tcp_data_offset = offset_tcp + 4 + 4 + 4;
				int offset_ip_len = offset_ip + 2;
				int offset_tcp_option = offset_tcp + 20;

				for (int i = 0; i < 2; i++) {
					byte_array_2[i] = packet_data[i + offset_ip_len];
				}

				int ip_total_len = Utils.byte_2_ToShort(byte_array_2);

				byte tmp = packet_data[offset_tcp_data_offset];
				int tmp12 = (tmp & 0xf0) >> 4;
				int tcp_header_len = tmp12 * 4;

				byte tmpa = packet_data[14];
				int tmpb = (tmpa & 0x0f);
				int ip_header_len = tmpb * 4;

				int payload_length = ip_total_len - ip_header_len - tcp_header_len;

				_packet.length_ip = ip_total_len;
				_packet.length_ip_header = ip_header_len;
				_packet.length_tcp_header = tcp_header_len;
				_packet.length_payload = payload_length;

				// System.out.println("length:"+ tcp_header_len);

//				if (syn_packet == false) {
//					syn_packet = true;
//
//					int tcp_option = packet_data[offset_tcp_option];
//					// System.out.println("tcp_option: " + tcp_option);
//
//					if (tcp_option == 2) {
//
//						for (int i = 0; i < 2; i++) {
//							byte_array_2[i] = packet_data[offset_tcp_option + 2 + i];
//						}
//
//						// byte_array_2[0] = packet_data[ offset_tcp_option + 1
//						// +1];
//						// byte_array_2[1] = packet_data[ offset_tcp_option +
//						// 1];
//
//						short mss = Utils.byte_2_ToShort(byte_array_2);
//						_packet.mss = mss;
//						// System.out.println("mss: " + mss);
//
//					}
//
//				}

				// System.out.println("ip_len: "+ ip_total_len);
				// System.out.println("ip_header_len: "+ ip_header_len);
				// System.out.println("tcp_data_offset: "+ tcp_header_len);
				// System.out.println("payload: "+ payload_length );

				for (int i = 0; i < 4; i++) {
					byte_array_4[i] = packet_data[i + offset_tcp_sn];
				}

				int seqNum = java.nio.ByteBuffer.wrap(byte_array_4).getInt();
				_packet.seq = seqNum;

				// System.out.println("Sequnce_num: " + seqNum);

				for (int i = 0; i < 4; i++) {
					byte_array_4[i] = packet_data[i + offset_tcp_ack];
				}

				int ack = java.nio.ByteBuffer.wrap(byte_array_4).getInt();
				// System.out.println("ack: " + ack);

				_packet.ack = ack;

				byte flag = packet_data[offset_tcp_flag];
				_packet.flag = flag;

				// System.out.println("flag: " + flag);
				 if (flag == 0x02) {
//				 System.out.println("syn");
				 
				 int tcp_option = packet_data[offset_tcp_option];
					// System.out.println("tcp_option: " + tcp_option);

					if (tcp_option == 2) {

						for (int i = 0; i < 2; i++) {
							byte_array_2[i] = packet_data[offset_tcp_option + 2 + i];
						}

						// byte_array_2[0] = packet_data[ offset_tcp_option + 1
						// +1];
						// byte_array_2[1] = packet_data[ offset_tcp_option +
						// 1];

						short mss = Utils.byte_2_ToShort(byte_array_2);
						_packet.mss = mss;
//						 System.out.println("mss: " + mss);

					}
				 
				 }

				for (int i = 0; i < 2; i++) {
					byte_array_2[i] = packet_data[i + offset_tcp_ws];
				}
				short window = Utils.byte_2_ToShort(byte_array_2);

				// System.out.println("window: " + window);
				_packet.window = window;

				int offset_ip_src = 26;
				int offset_ip_dst = 30;

				for (int i = 0; i < 4; i++) {
					byte_array_4[i] = packet_data[i + offset_ip_src];
				}

				StringBuilder builder = new StringBuilder();
				for (int i = 0; i < 4; i++) {
					builder.append((int) (byte_array_4[i] & 0xff));
					builder.append(".");
				}

				builder.deleteCharAt(builder.length() - 1);
				String sourceIP = builder.toString();
				_packet.ipSrc = sourceIP;
				// System.out.println("sourceIP: " + sourceIP);

				for (int i = 0; i < 4; i++) {
					byte_array_4[i] = packet_data[i + offset_ip_dst];
				}

				builder = new StringBuilder();
				for (int i = 0; i < 4; i++) {
					builder.append((int) (byte_array_4[i] & 0xff));
					builder.append(".");
				}
				builder.deleteCharAt(builder.length() - 1);
				String destinationIP = builder.toString();
				_packet.ipDest = destinationIP;

				// System.out.println("destinationIP: " + destinationIP);

				int offset_tcp_src_port = offset_tcp;
				int offset_tcp_dest_port = offset_tcp + 2;

				for (int i = 0; i < 2; i++) {
					byte_array_2[i] = packet_data[i + offset_tcp_src_port];
				}

				// short srcPort = Utils.byte_2_ToShort(byte_array_2);

				// short srcPort =
				// java.nio.ByteBuffer.wrap(byte_array_2).getShort();
				int srcPort = ((byte_array_2[0] & 0xFF) << 8) | ((byte_array_2[1] & 0xFF) << 0);

				_packet.srcPort = srcPort;
				// System.out.println("srcPort: "+ srcPort);

				for (int i = 0; i < 2; i++) {
					byte_array_2[i] = packet_data[i + offset_tcp_dest_port];
				}

				int destPort = ((byte_array_2[0] & 0xFF) << 8) | ((byte_array_2[1] & 0xFF) << 0);
				// int destPort = Utils.byte_2_ToShort(byte_array_2);
				// System.out.println("destPort: "+ destPort);
				_packet.destPort = destPort;

				// int offset_tcp_payload = 54;
				// if(caplen>54)
				// {
				// int leng= caplen-54;
				// byte payload[] =new byte[leng];
				//
				// for(int k=0;k<leng;k++)
				// {
				// payload[k]= packet_data[k+54];
				//
				// }
				// String str = new String(payload);
				// System.out.println(str);
				//
				//
				// }

				// System.out.println("________");

				list_packet.add(_packet);
				// System.out.println(_packet.toString());
			}
		}

		System.out.println("number of packets:   " + list_packet.size());

		// for (int j = 0; j < list_packet.size(); j++) {
		// Packet new_packet = list_packet.get(j);
		// System.out.println("flag" + new_packet.flag);
		//
		// }

		ArrayList<ArrayList<Packet>> list_list_packet = new ArrayList<ArrayList<Packet>>();
		boolean[] skip_array = new boolean[list_packet.size()];
		for (int i = 0; i < skip_array.length; i++)
			skip_array[i] = true;

		for (int i = 0; i < list_packet.size(); i++) {
			if (skip_array[i] == true) {
				skip_array[i] = false;

				Packet first_packet = list_packet.get(i);
				ArrayList<Packet> tmp = new ArrayList<Packet>();
				tmp.add(first_packet);

				for (int j = i + 1; j < list_packet.size(); j++) {
					if (skip_array[j] == true) {

						Packet second_packet = list_packet.get(j);

						String first_src_ip = first_packet.ipSrc;
						String first_dest_ip = first_packet.ipDest;
						int first_src_port = first_packet.srcPort;
						int first_dest_port = first_packet.destPort;

						String second_src_ip = second_packet.ipSrc;
						String second_dest_ip = second_packet.ipDest;
						int second_src_port = second_packet.srcPort;
						int second_dest_port = second_packet.destPort;

						boolean c1 = first_src_ip.equals(second_src_ip) && first_dest_ip.equals(second_dest_ip)
								&& first_src_port == second_src_port && first_dest_port == second_dest_port;
						boolean c2 = first_src_ip.equals(second_dest_ip) && first_dest_ip.equals(second_src_ip)
								&& first_src_port == second_dest_port && first_dest_port == second_src_port;

						if (c1 || c2) {
							tmp.add(second_packet);
							skip_array[j] = false;
							// list_packet.remove(j);
						}

					}

				}

				list_list_packet.add(tmp);
			}

		}

		for (int i = 0; i < list_list_packet.size(); i++) {
			ArrayList<Packet> tmp2 = list_list_packet.get(i);
			Packet first_packet = tmp2.get(0);
			System.out.println("flow from  " + first_packet.ipSrc + "  port: " + first_packet.srcPort + " to "
					+ first_packet.ipDest + "  port: " + first_packet.destPort);

			int mss = first_packet.mss;
			System.out.println("mss:  " + mss);

			int initial_cw = 0;

			if (mss > 2190)
				initial_cw = 2 * mss;
			if (mss > 1095 && mss <= 2190)
				initial_cw = 3 * mss;
			if (mss <= 1095)
				initial_cw = 4 * mss;

			System.out.println("initial_congestion_window is:   " + initial_cw);

			// System.out.println(tmp2.size());
			Packet a1 = tmp2.get(0);
			Packet a2 = tmp2.get(tmp2.size() - 1);

			double total_rtt = getRTT(a1, a2);

			// System.out.println("flow "+i + "__total_rtt__:" + total_rtt);

			int total_length = 0;
			int total_good_length = 0;
			for (int k = 0; k < tmp2.size(); k++) {
				int len = tmp2.get(k).length;
				total_length = total_length + len;
				total_good_length = total_good_length + len - 54;

			}

//			System.out.println("flow " + i + ":  total amount of bytes of thoughput:" + total_length);
			// System.out.println("flow "+i + " total amount of
			// bytes_of_goodput:" + total_good_length);

			double throuhput = 1000 * (total_length / total_rtt);
			System.out.println("flow " + i + ":  throuhput:  : " + throuhput + " byte per second");

			double goodput = 1000 * (total_good_length / total_rtt);
			System.out.println("flow " + i + ":  goodput:  : " + goodput + " byte per second");

			/////////////////////////// calculate rtt and rto

			boolean first_rtt = false;

			// double SRTT; <- R
			// RTTVAR <- R/2
			// RTO <- SRTT + max (G, K*RTTVAR)
			// where K = 4.

			double srtt = 0;
			double rttvar = 0;
			double rto = 0;
			int rtt_number = 0;
			double sum_individual_rtt = 0;

			String local_ip = a1.ipDest;
			for (int j = 1; j < tmp2.size(); j++) {
				Packet ack_packet = tmp2.get(j);
				String ack_dest = ack_packet.ipDest;
				if (ack_dest.equals(local_ip)) {
					Packet prev_packet = tmp2.get(j - 1);
					boolean c1 = prev_packet.ipSrc.equals(ack_packet.ipSrc);
					boolean c2 = prev_packet.ipDest.equals(ack_packet.ipDest);
					// if ((c1 && c2) == false) {
					for (int k = j - 1; k >= 0; k--) {
						Packet sec_packet = tmp2.get(k);

						int ack = ack_packet.ack;
						int seq = sec_packet.seq;
						int payload = sec_packet.length_payload;
						// System.out.println(ack+"_"+seq+"_" + payload );

						if (sec_packet.ipSrc.equals(ack_dest) && ack == seq + payload) {
							rtt_number++;
							
							double rtt = getRTT(sec_packet, ack_packet);

							// System.out.println("rtt number: " + rtt_number+"
							// from packet "+(k + 1) +" to "+"packet "+ (j +
							// 1));

							// System.out.println("rtt:_" + rtt + "
							// millsecond");
							sum_individual_rtt = sum_individual_rtt + rtt;

							if (rtt_number < 21)

							{

								initial_cw = initial_cw + mss;
								// System.out.println("congestion window size :
								// " + initial_cw);
							}

							if (first_rtt == false) {
								first_rtt = true;
								srtt = rtt;
								rttvar = rtt / 2;
								rto = srtt + Double.max(1000, 4 * rttvar);

							}

							else {

								double alpha = 0.125;
								double beta = 0.25;

								// double tmp = Math.abs(srtt - rtt);
								// rttvar = (1 - beta) * rttvar + beta * tmp;
								// srtt = (1 - alpha) * srtt + alpha * rtt;
								// rto = srtt + Double.max(1000, 4 * rttvar);

							}

							// System.out.println("srtt: " + srtt);
							// System.out.println("rttvar: " + rttvar);
							// System.out.println("rto: " + rto);

							break;

						}
					}
					// }

				}

			}

			double avg_rtt = sum_individual_rtt / rtt_number;
			System.out.println("flow "+i+":  avg_rtt:   " + avg_rtt +" millisecond");
			System.out.println("\n\n");

		}

	}

	private static double getRTT(Packet a1, Packet a2) {
		long sec_1 = a1.sec;
		long mills_sec_1 = sec_1 * 1000;
		double microsec_1 = a1.microsec;
		double mills_micro_1 = microsec_1 / 1000;
		double total_1 = (double) (mills_sec_1) + mills_micro_1;
		// System.out.println("mills_sec_1: " + (double) mills_sec_1);
		// System.out.println("mills_micro_1: " + (double) mills_micro_1);
		// System.out.println("total_1: " + total_1);

		long sec_2 = a2.sec;
		long mills_sec_2 = sec_2 * 1000;
		double microsec_2 = a2.microsec;
		double mills_micro_2 = microsec_2 / 1000;
		double total_2 = (double) (mills_sec_2) + mills_micro_2;
		// System.out.println("mills_sec_2: " + (double) mills_sec_2);
		// System.out.println("mills_micro_2: " + (double) mills_micro_2);
		// System.out.println("total_2: " + total_2);

		double total_rtt = total_2 - total_1;
		return total_rtt;
	}

}
